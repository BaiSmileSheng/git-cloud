package com.cloud.activiti.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.consts.ActivitiProTitleConstants;
import com.cloud.activiti.consts.ActivitiTableNameConstants;
import com.cloud.activiti.consts.ActivitiProDefKeyConstants;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.mail.MailService;
import com.cloud.activiti.service.IActSmsClaimOtherService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.settle.enums.ClaimOtherStatusEnum;
import com.cloud.settle.feign.RemoteClaimOtherService;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteOssService;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
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
 * 其他索赔审批
 * @Author Lihongxia
 * @Date 2020-06-02
 */
@Service
public class ActSmsClaimOtherServiceImpl implements IActSmsClaimOtherService {

    private static Logger logger = LoggerFactory.getLogger(ActSmsClaimOtherServiceImpl.class);

    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteClaimOtherService remoteClaimOtherService;

    @Autowired
    private IActTaskService actTaskService;

    @Autowired
    private RemoteOssService remoteOssService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MailService mailService;


    /**
     * 索赔单所对应的申诉文件订单号后缀
     */
    private static final String ORDER_NO_OTHER_APPEAL_END = "_02";


    /**
     * 根据业务key获取其他索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 其他索赔信息
     */
    @Override
    public R getBizInfoByTableId(String businessKey) {
        logger.info("其他索赔工作流根据业务key获取其他索赔信息 businessKey:{}",businessKey);
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        String procInstId = business.getProcInstId();
        if (null != business) {
            logger.info("其他索赔工作流根据业务key获取其他索赔信息 主键id:{}",business.getTableId());
            //根据主键id 获取对应的其他索赔信息(包含文件信息)
            R selectResult = remoteClaimOtherService.selectById(Long.valueOf(business.getTableId()));
            selectResult.put("procInstId",procInstId);
            return selectResult;
        }
        return R.error("其他索赔工作流根据业务key获取其他索赔信息失败");
    }

    /**
     * 供应商发起申诉时 其他索赔信息开启流程
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param sysUser 当前用户信息
     * @return 成功或失败
     */
    @GlobalTransactional
    @Override
    public R addSave(Long id,String complaintDescription, String ossIds, SysUser sysUser) {
        SmsClaimOther smsClaimOther = new SmsClaimOther();
        smsClaimOther.setId(id);
        smsClaimOther.setComplaintDescription(complaintDescription);
        smsClaimOther.setUpdateBy(sysUser.getLoginName());
        logger.info("其他索赔开启流程 其他索赔id:{},其他索赔索赔单号:{}",smsClaimOther.getId(),
                smsClaimOther.getClaimCode());
        //1.供应商申诉
        R appealResult = supplierAppeal(smsClaimOther,ossIds);
        if(!appealResult.isSuccess()){
            logger.error("其他索赔开启流程失败 其他索赔索赔单号:{},res:{}", smsClaimOther.getClaimCode(),
                    JSONObject.toJSON(appealResult));
            return appealResult;
        }
        String claimCode = appealResult.getStr("data");
        smsClaimOther.setClaimCode(claimCode);
        //2.构造其他索赔流程信息
        BizBusiness business = initBusiness(smsClaimOther,sysUser);
        //新增其他索赔流程
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //启动其他索赔流程
        bizBusinessService.startProcess(business, variables);
        return R.ok();
    }

    /**
     * 供应商申诉
     * @param smsClaimOther 其他索赔信息
     * @param ossIds 文件
     * @return 成功或失败
     */
    private R supplierAppeal(SmsClaimOther smsClaimOther, String ossIds) {
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        R smsClaimOtherResR = remoteClaimOtherService.get(smsClaimOther.getId());
        if(!smsClaimOtherResR.isSuccess()){
            logger.error("供应商申诉的其他索赔单不存在 id:{}",smsClaimOther.getId());
            throw new BusinessException("索赔单不存在");
        }
        SmsClaimOther smsClaimOtherRes = smsClaimOtherResR.getData(SmsClaimOther.class);
        Boolean flagResult = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1.getCode().equals(smsClaimOtherRes.getClaimOtherStatus())
                ||ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_7.equals(smsClaimOtherRes.getClaimOtherStatus());
        if(!flagResult){
            logger.error("供应商申诉的其他索赔单 状态异常 id:{},claimOtherStatus:{}",
                    smsClaimOther.getId(),smsClaimOtherRes.getClaimOtherStatus());
            throw new BusinessException("此索赔单不可申诉");
        }
        //2.修改索赔单信息
        smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_3.getCode());
        smsClaimOther.setComplaintDate(new Date());
        R updateResult = remoteClaimOtherService.editSave(smsClaimOther);
        if(!updateResult.isSuccess()){
            throw new BusinessException("修改索赔单信息失败");
        }
        String orderNo = smsClaimOtherRes.getClaimCode() + ORDER_NO_OTHER_APPEAL_END;
        //3.根据订单号新增文件
        //修改文件信息
        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        if(!uplodeFileResult.isSuccess()){
            logger.info("其他索赔单申诉修改文件 索赔单号:{}",smsClaimOtherRes.getClaimCode());
            throw new  BusinessException("其他索赔单申诉修改文件异常");
        }
        //发送邮件
        String factoryCode = smsClaimOtherRes.getFactoryCode();
        String roleKey = RoleConstants.ROLE_KEY_XWZ;
        String claimCode = smsClaimOtherRes.getClaimCode();
        sendEmail(claimCode, factoryCode, roleKey);
        return R.data(smsClaimOtherRes.getClaimCode());
    }

    /**
     * 发送邮件
     * @param claimCode
     * @param factoryCode
     * @param roleKey
     */
    private void sendEmail(String claimCode, String factoryCode, String roleKey) {
        R sysUserR = remoteUserService.selectUserByMaterialCodeAndRoleKey(factoryCode,roleKey);
        if(!sysUserR.isSuccess()){
            logger.error("获取对应的负责人邮箱失败");
            throw new BusinessException("获取对应的负责人邮箱失败" + sysUserR.get("msg").toString());
        }
        List<SysUserVo> sysUserVoList = sysUserR.getCollectData(new TypeReference<List<SysUserVo>>() {});
        //校验邮箱
        for(SysUserVo sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            if(StringUtils.isBlank(email)){
                throw new  BusinessException("用户"+sysUserVo.getUserName()+"邮箱不存在");
            }
        }
        //发送邮件
        for(SysUserVo sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            String subject = "供应商申诉";
            String content = "其他索赔单 单号:" + claimCode + "供应商发起申诉";
            mailService.sendTextMail(email,subject,content);
        }
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
     * @param smsClaimOther 其他索赔信息
     * @return
     */
    private BizBusiness initBusiness(SmsClaimOther smsClaimOther,SysUser sysUser) {
        //构造质量索赔流程信息
        R keyMap = getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_CHAIM_OTHER_TEST);
        if (!keyMap.isSuccess()) {
            logger.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        BizBusiness business = new BizBusiness();
        business.setOrderNo(smsClaimOther.getClaimCode());
        business.setTableId(smsClaimOther.getId().toString());
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_OTHER);
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SCHAIM_TEST);
        business.setProcDefId(processDefinitionAct.getId());
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
     * 其他索赔审批流程
     * @param bizAudit
     * @return 成功/失败
     */
    @GlobalTransactional
    @Override
    public R audit(BizAudit bizAudit,SysUser sysUser) {
        //1.查询可处理业务逻辑(获取其他索赔id)
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            logger.error ("其他索赔审批流程 查询流程业务失败Req主键id:{}",bizAudit.getBusinessKey().toString());
            return R.error("其他索赔审批流程 查询流程业务失败");
        }
        //2.根据id获取其他索赔信息 判断是否待审批
        logger.info ("其他索赔审批流程 获取其他索赔信息主键id:{}",bizBusiness.getTableId());
        R smsClaimOtherR = remoteClaimOtherService.get(Long.valueOf(bizBusiness.getTableId()));
        if(!smsClaimOtherR.isSuccess()){
            logger.error ("其他索赔审批流程 查询其他索赔信息失败Req主键id:{}",bizBusiness.getTableId());
            return R.error("其他索赔审批流程 查询其他索赔信息失败");
        }
        SmsClaimOther smsClaimOther = smsClaimOtherR.getData(SmsClaimOther.class);
        //状态是否是待小微主审核
        Boolean flagStatus3 = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_3.getCode().equals(smsClaimOther.getClaimOtherStatus());
        if (!flagStatus3) {
            logger.error ("其他索赔审批流程 查询其他索赔信息失败Req主键id:{} 状态:{}",bizBusiness.getTableId(),smsClaimOther.getClaimOtherStatus());
            return R.error("其他索赔审批流程 其他索赔单不可审核");
        }

        //3.根据结果修改其他索赔信息
        //审核结果 2表示通过,3表示驳回
        Boolean flagBizResult = "2".equals(bizAudit.getResult().toString());
        String claimCode = smsClaimOther.getClaimCode();
        String supplierCode = smsClaimOther.getSupplierCode();
        //小微主审批: 将待小微主审核5--->待结算11   驳回将待小微主审核5--->待供应商确认7
        if(flagBizResult){
            smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_11.getCode());
            //发送邮件
            String contentDetail = "申诉通过";
            supplierSendEmail(claimCode,supplierCode,contentDetail);
        }else{
            smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_7.getCode());
            //发送邮件
            String contentDetail = "申诉驳回";
            supplierSendEmail(claimCode,supplierCode,contentDetail);
        }
        //更新其他索赔状态
        logger.info ("其他索赔审批流程 更新其他索赔主键id:{} 状态:{}",smsClaimOther.getId(),smsClaimOther.getClaimOtherStatus());
        R updateResult = remoteClaimOtherService.editSave(smsClaimOther);
        if(!updateResult.isSuccess()){
            logger.error("其他索赔审批流程 更新其他索赔失败 主键id:{}res:{}",smsClaimOther.getId(),JSONObject.toJSON(updateResult));
            throw new BusinessException("其他索赔审批流程 更新其他索赔失败 ");
        }
        //4.审批 推进工作流
        R resultAck = actTaskService.audit(bizAudit, sysUser.getUserId());
        if(!resultAck.isSuccess()){
            logger.error("其他索赔审批流程 审批 推进工作流 req:{}res:{}",JSONObject.toJSON(bizAudit),JSONObject.toJSON(resultAck));
            throw new BusinessException("其他索赔审批流程 审批 推进工作流失败 ");
        }
        return R.ok();
    }
    /**
     * 向供应商发送邮件
     * @param claimCode
     * @param supplierCode
     * @param contentDetail
     */
    private void supplierSendEmail(String claimCode,String supplierCode,String contentDetail) {
        R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
        if(!sysUserR.isSuccess()){
            logger.error("获取对应的负责人邮箱失败");
            throw new BusinessException(sysUserR.get("msg").toString());
        }
        SysUserVo sysUserVo = sysUserR.getData(SysUserVo.class);
        String email = sysUserVo.getEmail();
        if(StringUtils.isBlank(email)){
            throw new  BusinessException("用户"+sysUserVo.getUserName()+"邮箱不存在");
        }
        String subject = "供应商申诉";
        String content = "其他索赔单 单号:" + claimCode + contentDetail;
        mailService.sendTextMail(email,subject,content);
    }
}
