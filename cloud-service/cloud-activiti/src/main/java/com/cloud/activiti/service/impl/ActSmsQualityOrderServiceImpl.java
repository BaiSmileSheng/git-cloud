package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.consts.ActivitiProDefKeyConstants;
import com.cloud.activiti.consts.ActivitiProTitleConstants;
import com.cloud.activiti.consts.ActivitiTableNameConstants;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.service.IActSmsQualityOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.feign.RemoteQualityOrderService;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteOssService;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 质量索赔审批
 * @Author Lihongxia
 * @Date 2020-06-02
 */
@Service
public class ActSmsQualityOrderServiceImpl implements IActSmsQualityOrderService {

    private static Logger logger = LoggerFactory.getLogger(ActSmsQualityOrderServiceImpl.class);

    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteQualityOrderService remoteQualityOrderService;

    @Autowired
    private IActTaskService actTaskService;

    @Autowired
    private RemoteOssService remoteOssService;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 索赔单所对应的申诉文件订单号后缀
     */
    private static final String ORDER_NO_QUALITY_APPEAL_END = "_02";


    /**
     * 根据业务key获取质量索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    @Override
    public R getBizInfoByTableId(String businessKey) {
        logger.info("质量索赔工作流根据业务key获取质量索赔信息 businessKey:{}",businessKey);
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            logger.info("质量索赔工作流根据业务key获取质量索赔信息 主键id:{}",business.getTableId());
            //根据主键id 获取对应的质量索赔信息
            R result  = remoteQualityOrderService.selectById(Long.valueOf(business.getTableId()));
            return result;
        }
        return R.error("质量索赔工作流根据业务key获取质量索赔信息失败");
    }

    /**
     * 质量索赔信息开启流程
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param ossIds
     * @return 成功或失败
     */
    @GlobalTransactional
    @Override
    public R addSave(Long id,String complaintDescription,String ossIds, SysUser sysUser) {

        SmsQualityOrder smsQualityOrder = new SmsQualityOrder();
        smsQualityOrder.setId(id);
        smsQualityOrder.setComplaintDescription(complaintDescription);
        smsQualityOrder.setUpdateBy(sysUser.getLoginName());
        logger.info("质量索赔信息开启流程 质量索赔id:{},质量索赔索赔单号:{}",smsQualityOrder.getId(),
                smsQualityOrder.getQualityNo());
        //1.供应商发起申诉
        R suppResult = supplierAppeal(smsQualityOrder,ossIds);
        if(!suppResult.isSuccess()){
            return suppResult;
        }
        String orderNo = suppResult.getStr("data");
        smsQualityOrder.setQualityNo(orderNo);
        BizBusiness business = initBusiness(smsQualityOrder,sysUser);
        //新增质量索赔流程
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //启动质量索赔流程
        bizBusinessService.startProcess(business, variables);
        return R.ok();
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param smsQualityOrder 质量索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    private R supplierAppeal(SmsQualityOrder smsQualityOrder,String ossIds) {
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        R smsQualityOrderResR = remoteQualityOrderService.get(smsQualityOrder.getId());
        if(!smsQualityOrderResR.isSuccess()){
            logger.error("质量索赔单申诉时索赔单不存在 索赔单id:{}",smsQualityOrder.getId());
            throw new BusinessException("索赔单不存在");
        }
        SmsQualityOrder smsQualityOrderRes = smsQualityOrderResR.getData(SmsQualityOrder.class);
        Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_1.getCode().equals(smsQualityOrderRes.getQualityStatus())
                ||QualityStatusEnum.QUALITY_STATUS_7.getCode().equals(smsQualityOrderRes.getQualityStatus());
        if(!flagResult){
            logger.error("质量索赔单申诉时索赔单状态异常 res:{}",JSONObject.toJSONString(smsQualityOrderRes));
            throw new BusinessException("此索赔单不可申诉");
        }
        //2.修改索赔单信息
        smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_4.getCode());
        smsQualityOrder.setComplaintDate(new Date());
        R result = remoteQualityOrderService.editSave(smsQualityOrder);
        if(!result.isSuccess()){
            logger.error("质量索赔单申诉时修改索赔单异常 索赔单号:{},res:{}",smsQualityOrder.getQualityNo(),
                    JSONObject.toJSONString(result));
            throw new BusinessException("质量索赔单供应商申诉时修改状态失败");
        }
        //3.根据订单号新增文件
        String orderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_APPEAL_END;
        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        if(!uplodeFileResult.isSuccess()){
            logger.info("质量索赔单申诉修改文件 索赔单号:{}",smsQualityOrderRes.getQualityNo());
            throw new  BusinessException("质量索赔单申诉修改文件异常");
        }
        return R.data(smsQualityOrderRes.getQualityNo());
    }

    /**
     * Description:  根据Key查询最新版本流程
     * Param: [key]
     * return: com.cloud.common.core.domain.R
     */
    private R getByKey(String key) {
        // 使用repositoryService查询单个流程实例
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
        if (BeanUtil.isEmpty(processDefinition)) {
            logger.error("根据Key值查询流程实例失败!");
            return R.error("根据Key值查询流程实例失败！");
        }
        ProcessDefinitionAct processDefinitionAct =
                ProcessDefinitionAct.builder()
                        .id(processDefinition.getId())
                        .name(processDefinition.getName())
                        .category(processDefinition.getCategory())
                        .deploymentId(processDefinition.getDeploymentId())
                        .description(processDefinition.getDescription())
                        .diagramResourceName(processDefinition.getDiagramResourceName())
                        .resourceName(processDefinition.getResourceName())
                        .tenantId(processDefinition.getTenantId())
                        .version(processDefinition.getVersion()).build();
        return R.data(processDefinitionAct);
    }
    /**
     * biz构造业务信息
     * @param smsQualityOrder 质量索赔信息
     * @return
     */
    private BizBusiness initBusiness(SmsQualityOrder smsQualityOrder,SysUser sysUser) {
        //构造质量索赔流程信息
        R keyMap = getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_QUALITY_TEST);
        if (!keyMap.isSuccess()) {
            logger.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        BizBusiness business = new BizBusiness();
        business.setOrderNo(smsQualityOrder.getQualityNo());
        business.setTableId(smsQualityOrder.getId().toString());
        business.setProcDefId(processDefinitionAct.getId());
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_QUALITY);
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SQUALITY_TEST);
        business.setProcName(processDefinitionAct.getName());
        business.setUserId(sysUser.getUserId());
        business.setApplyer(sysUser.getUserName());
        //设置流程状态
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 质量索赔审批流程
     * @param bizAudit
     * @return 成功/失败
     */
    @GlobalTransactional
    @Override
    public R audit(BizAudit bizAudit,SysUser sysUser) {
        //查询可处理业务逻辑
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            logger.error ("质量索赔审批流程 查询流程业务失败Req主键id:{}",bizAudit.getBusinessKey().toString());
            throw new BusinessException("质量索赔审批流程 查询流程业务失败");
        }
        //获取质量索赔信息
        logger.info ("质量索赔审批流程 获取质量索赔信息主键id:{}",bizBusiness.getTableId());
        R smsQualityOrderR = remoteQualityOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if(!smsQualityOrderR.isSuccess()){
            logger.error("质量索赔审批流程 查询质量索赔信息失败Req主键id:{}",bizBusiness.getTableId());
            throw new BusinessException("质量索赔审批流程 查询质量索赔信息失败");
        }
        SmsQualityOrder smsQualityOrder = smsQualityOrderR.getData(SmsQualityOrder.class);
        //状态是否是待质量部长审核
        Boolean flagStatus4 = QualityStatusEnum.QUALITY_STATUS_4.getCode().equals(smsQualityOrder.getQualityStatus());
        if (null == smsQualityOrder || !flagStatus4) {
            logger.error ("质量索赔审批流程 查询质量索赔信息失败Req主键id:{} 状态:{}",bizBusiness.getTableId(),smsQualityOrder.getQualityStatus());
            throw new BusinessException("质量索赔审批流程 查询质量索赔信息失败");
        }

        //审核结果 2表示通过,3表示驳回
        Boolean flagBizResult = "2".equals(bizAudit.getResult().toString());
        //根据角色和质量索赔状态审批
        if(flagBizResult){
            //质量部部长审批: 将供应商申诉4--->待小微主审核5 //小微主审批: 将待小微主审核5--->待结算11
            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
        }else{
            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_7.getCode());
        }

        //更新质量索赔状态
        logger.info ("质量索赔审批流程 更新质量索赔主键id:{} 状态:{}",smsQualityOrder.getId(),smsQualityOrder.getQualityStatus());
        R updateResult = remoteQualityOrderService.editSave(smsQualityOrder);
        if(!updateResult.isSuccess()){
            logger.error("质量索赔审批流程 更新质量索赔失败 主键id:{}res:{}",smsQualityOrder.getId(),JSONObject.toJSON(updateResult));
            throw new BusinessException("质量索赔审批流程 更新质量索赔失败 ");
        }
        //审批 推进工作流
        R resultAck =  actTaskService.audit(bizAudit, sysUser.getUserId());
        if(!resultAck.isSuccess()){
            logger.error("质量索赔审批流程 审批 推进工作流 req:{}res:{}",JSONObject.toJSON(bizAudit),JSONObject.toJSON(updateResult));
            throw new BusinessException("质量索赔审批流程 审批 推进工作流失败 ");
        }
        return R.error();
    }
}
