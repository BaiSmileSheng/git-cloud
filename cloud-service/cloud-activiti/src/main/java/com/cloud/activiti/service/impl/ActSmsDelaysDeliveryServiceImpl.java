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
import com.cloud.activiti.mail.MailService;
import com.cloud.activiti.service.IActSmsDelaysDeliveryService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.feign.RemoteDelaysDeliveryService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author Lihongxia
 * @Date 2020-06-02
 */
@Service
public class ActSmsDelaysDeliveryServiceImpl implements IActSmsDelaysDeliveryService {

    private static Logger logger = LoggerFactory.getLogger(ActSmsDelaysDeliveryServiceImpl.class);


    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteDelaysDeliveryService remoteDelaysDeliveryService;

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
     * 延期索赔工作流根据业务key获取延期索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    @Override
    public R getBizInfoByTableId(String businessKey) {
        logger.info("延期索赔工作流根据业务key获取质量索赔信息 businessKey:{}",businessKey);
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        String procInstId = business.getProcInstId();
        if (null != business) {
            logger.info("延期索赔工作流根据业务key获取质量索赔信息 主键id:{}",business.getTableId());
            //根据主键id 获取对应的质量索赔信息
            R result  = remoteDelaysDeliveryService.selectById(Long.valueOf(business.getTableId()));
            result.put("procInstId",procInstId);
            return result;
        }
        return R.error("延期索赔工作流根据业务key获取质量索赔信息失败");
    }

    /**
     * 供应商申诉延期索赔开启流程
     * @param id 主键id
     * @param complaintDescription 申诉描述
     * @param ossIds
     * @param sysUser 用户信息
     * @return
     */
    @GlobalTransactional
    @Override
    public R addSave(Long id,String complaintDescription, String ossIds,SysUser sysUser) {

        SmsDelaysDelivery smsDelaysDelivery = new SmsDelaysDelivery();
        smsDelaysDelivery.setId(id);
        smsDelaysDelivery.setComplaintDescription(complaintDescription);
        smsDelaysDelivery.setUpdateBy(sysUser.getLoginName());
        logger.info("供应商申诉延期索赔开启流程 延期索赔id:{},延期索赔索赔单号:{}",smsDelaysDelivery.getId(),
                smsDelaysDelivery.getDelaysNo());
        //1.供应商申诉
        R resultRes = supplierAppeal(smsDelaysDelivery,ossIds);
        SmsDelaysDelivery smsDelaysDeliverySelect = (SmsDelaysDelivery)resultRes.get("smsDelaysDeliveryRes");
        List<SysUserVo> sysUserVoList = (List<SysUserVo>)resultRes.get("sysUserVoList");
        String orderNo = smsDelaysDeliverySelect.getDelaysNo();
        smsDelaysDelivery.setDelaysNo(orderNo);
        //2.构造延期索赔流程信息
        BizBusiness business = initBusiness(smsDelaysDelivery,sysUser);
        //新增延期索赔流程
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //启动延期索赔流程
        Set<String> userIdSet = sysUserVoList.stream().map(sysUserVo -> sysUserVo.getUserId().toString()).collect(Collectors.toSet());
        bizBusinessService.startProcess(business, variables,userIdSet);
        return R.ok();
    }

    /**
     * 延期索赔单供应商申诉(包含文件信息)
     * @param smsDelaysDeliveryReq 延期索赔信息
     * @return 延期索赔单供应商申诉结果成功或失败
     */
    private R supplierAppeal(SmsDelaysDelivery smsDelaysDeliveryReq, String ossIds) {
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        logger.info("延期索赔单供应商申诉(包含文件信息) id:{}",smsDelaysDeliveryReq.getId());
        R selectSmsDelaysDeliveryR = remoteDelaysDeliveryService.get(smsDelaysDeliveryReq.getId());
        if(!selectSmsDelaysDeliveryR.isSuccess()){
            logger.info("延期索赔单申诉异常 id:{}",smsDelaysDeliveryReq.getId());
            throw new BusinessException("此延期索赔单不存在");
        }
        SmsDelaysDelivery selectSmsDelaysDelivery = selectSmsDelaysDeliveryR.getData(SmsDelaysDelivery.class);
        Boolean flagSelectStatus = DeplayStatusEnum.DELAYS_STATUS_1.getCode().equals(selectSmsDelaysDelivery.getDelaysStatus())
                ||DeplayStatusEnum.DELAYS_STATUS_7.getCode().equals(selectSmsDelaysDelivery.getDelaysStatus());
        if(!flagSelectStatus){
            logger.info("延期索赔单申诉状态异常 索赔单号:{}",selectSmsDelaysDelivery.getDelaysNo());
            throw new  BusinessException("此延期索赔单不可再申诉");
        }
        //修改延期索赔单
        smsDelaysDeliveryReq.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_4.getCode());
        smsDelaysDeliveryReq.setComplaintDate(new Date());
        R updateR = remoteDelaysDeliveryService.editSave(smsDelaysDeliveryReq);
        if(!updateR.isSuccess()){
            logger.info("延期索赔单申诉修改延期索赔单状态 索赔单号:{}",selectSmsDelaysDelivery.getDelaysNo());
            throw new  BusinessException("延期索赔单申诉修改延期索赔单状态异常");
        }
        //修改文件信息
        String orderNo = selectSmsDelaysDelivery.getDelaysNo();
        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        if(!uplodeFileResult.isSuccess()){
            logger.info("延期索赔单申诉修改文件 索赔单号:{}",selectSmsDelaysDelivery.getDelaysNo());
            throw new  BusinessException("延期索赔单申诉修改文件异常");
        }
        //4.发送邮件
        String factoryCode = selectSmsDelaysDelivery.getFactoryCode();
        String roleKey = RoleConstants.ROLE_KEY_ZLBBZ;
        String delaysNo = selectSmsDelaysDelivery.getDelaysNo();
        List<SysUserVo> sysUserVoList = sendEmail(delaysNo, factoryCode, roleKey);
        R resultRes = new R();
        resultRes.put("smsDelaysDeliveryRes",selectSmsDelaysDelivery);
        resultRes.put("sysUserVoList",sysUserVoList);
        return resultRes;
    }

    /**
     * 发送邮件
     * @param delaysNo
     * @param factoryCode
     * @param roleKey
     */
    private List<SysUserVo> sendEmail(String delaysNo, String factoryCode, String roleKey) {
        R sysUserR = remoteUserService.selectUserByMaterialCodeAndRoleKey(factoryCode,roleKey);
        if(!sysUserR.isSuccess()){
            logger.error("获取对应的负责人邮箱失败");
            throw new BusinessException("获取对应的负责人邮箱失败" + sysUserR.get("msg").toString());
        }
        List<SysUserVo> sysUserVoList = sysUserR.getCollectData(new TypeReference<List<SysUserVo>>() {});
        //校验邮件
        for(SysUserVo sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            if(StringUtils.isBlank(email)){
                throw new  BusinessException("用户"+sysUserVo.getUserName()+"邮箱不存在");
            }
        }
        //发送邮件
        for(SysUserVo sysUserVo : sysUserVoList){
            String email = sysUserVo.getEmail();
            String subject = "延期索赔单供应商申诉";
            String content = "您有一条待办消息要处理!" + "延期索赔单 单号:" + delaysNo + "供应商发起申诉。" + EmailConstants.ORW_URL;
            mailService.sendTextMail(email,subject,content);
        }
        return sysUserVoList;
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
     * @param smsDelaysDelivery
     * @return
     */
    private BizBusiness initBusiness(SmsDelaysDelivery smsDelaysDelivery,SysUser sysUser) {
        //构造质量索赔流程信息
        R keyMap = getByKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_DELAYS_TEST);
        if (!keyMap.isSuccess()) {
            logger.error("根据Key获取最新版流程实例失败："+keyMap.get("msg"));
            throw new BusinessException("根据Key获取最新版流程实例失败!");
        }
        ProcessDefinitionAct processDefinitionAct = keyMap.getData(ProcessDefinitionAct.class);
        BizBusiness business = new BizBusiness();
        business.setOrderNo(smsDelaysDelivery.getDelaysNo());
        business.setTableId(smsDelaysDelivery.getId().toString());
        business.setProcDefId(processDefinitionAct.getId());
        business.setTableName(ActivitiTableNameConstants.ACTIVITI_TABLE_NAME_DELAYS);
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SDEPALYS_TEST);
        business.setProcName(processDefinitionAct.getName());
        business.setUserId(sysUser.getUserId());
        business.setApplyer(sysUser.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 延期索赔流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @GlobalTransactional
    @Override
    public R audit(BizAudit bizAudit,SysUser sysUser) {
        //延期索赔可处理业务逻辑
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            logger.error ("延期索赔审批流程 查询流程业务失败Req主键id:{}",bizAudit.getBusinessKey().toString());
            return R.error("延期索赔审批流程 查询流程业务失败");
        }
        logger.info ("延期索赔审批流程 获取延期索赔信息主键id:{}",bizBusiness.getTableId());
        R smsDelaysDeliveryR = remoteDelaysDeliveryService.get(Long.valueOf(bizBusiness.getTableId()));
        if(!smsDelaysDeliveryR.isSuccess()){
            logger.error ("延期索赔审批流程 查询延期索赔信息失败Req主键id:{}",bizBusiness.getTableId());
            return R.error("延期索赔审批流程 查询延期索赔信息失败");
        }
        SmsDelaysDelivery smsDelaysDelivery = smsDelaysDeliveryR.getCollectData(new TypeReference<SmsDelaysDelivery>() {});
        //状态是否是待订单部部长审核
        Boolean flagStatus4 = DeplayStatusEnum.DELAYS_STATUS_4.getCode().equals(smsDelaysDelivery.getDelaysStatus());
        //状态是否是待小微主审核
        Boolean flagStatus5 = DeplayStatusEnum.DELAYS_STATUS_5.getCode().equals(smsDelaysDelivery.getDelaysStatus());

        //审核结果 2表示通过,3表示驳回
        Boolean flagBizResult = "2".equals(bizAudit.getResult().toString());
        String delaysNo = smsDelaysDelivery.getDelaysNo();
        String supplierCode = smsDelaysDelivery.getSupplierCode();
        //根据角色和延期索赔状态审批
        //订单部部长审批: 将供应商申诉4--->待小微主审核5  小微主审批: 将待小微主审核5--->已结算12
        R  resultAck;
        if(flagBizResult){
            if( flagStatus4){
                smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_5.getCode());
                //向小微主发送邮件
                String factoryCode = smsDelaysDelivery.getFactoryCode();
                String roleKey = RoleConstants.ROLE_KEY_XWZ;
                List<SysUserVo> sysUserVoList = sendEmail(delaysNo, factoryCode, roleKey);
                //审批 推进工作流
                Set<String> userIdSet = sysUserVoList.stream().map(sysUserVo -> sysUserVo.getUserId().toString()).collect(Collectors.toSet());
                resultAck =actTaskService.auditCandidateUser(bizAudit, sysUser.getUserId(),userIdSet);
            }else if(flagStatus5){
                smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_12.getCode());
                smsDelaysDelivery.setSettleFee(BigDecimal.ZERO);
                //向供应商发邮件
                String contentDetail = "申诉通过";
                supplierSendEmail(delaysNo,supplierCode,contentDetail);
                //审批 推进工作流
                resultAck =actTaskService.auditCandidateUser(bizAudit, sysUser.getUserId(),null);
            }else{
                logger.error ("延期索赔审批流程 此延期索赔单不可审批Req主键id:{} 状态:{}",bizBusiness.getTableId(),
                        smsDelaysDelivery.getDelaysStatus());
                throw new BusinessException("此延期索赔单不可审批");
            }
        }else{
            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_7.getCode());
            //向供应商发邮件
            String contentDetail = "申诉驳回";
            supplierSendEmail(delaysNo,supplierCode,contentDetail);
            //审批 推进工作流
            resultAck =actTaskService.auditCandidateUser(bizAudit, sysUser.getUserId(),null);
        }
        if(!resultAck.isSuccess()){
            logger.error("延期索赔审批流程 审批 推进工作流 req:{}res:{}",JSONObject.toJSON(bizAudit),JSONObject.toJSON(resultAck));
            throw new BusinessException("延期索赔审批流程 审批 推进工作流失败 ");
        }
        //更新延期索赔状态
        logger.info ("延期索赔审批流程 更新延期索赔主键id:{} 状态:{}",smsDelaysDelivery.getId(),smsDelaysDelivery.getDelaysStatus());
        R updateResult = remoteDelaysDeliveryService.editSave(smsDelaysDelivery);
        if(!updateResult.isSuccess()){
            logger.error("延期索赔审批流程 更新延期索赔失败 主键id:{}res:{}",smsDelaysDelivery.getId(),JSONObject.toJSON(updateResult));
            throw new BusinessException("延期索赔审批流程 更新索赔索赔失败 ");
        }
        return R.ok();
    }

    /**
     * 向供应商发送邮件
     * @param delaysNo
     * @param supplierCode
     * @param contentDetail
     */
    private void supplierSendEmail(String delaysNo,String supplierCode,String contentDetail) {
        R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
        if(!sysUserR.isSuccess()){
            logger.error("获取对应的负责人邮箱失败");
            throw new BusinessException(sysUserR.get("msg").toString());
        }
        SysUserVo  sysUserVo = sysUserR.getData(SysUserVo.class);
        String email = sysUserVo.getEmail();
        if(StringUtils.isBlank(email)){
            throw new  BusinessException("用户"+sysUserVo.getUserName()+"邮箱不存在");
        }
        String subject = "延期索赔单申诉审批结果";
        String content = "您有一条通知：" + "延期索赔单 单号：" + delaysNo + contentDetail + "。"+ EmailConstants.ORW_URL;
        mailService.sendTextMail(email,subject,content);
    }
}
