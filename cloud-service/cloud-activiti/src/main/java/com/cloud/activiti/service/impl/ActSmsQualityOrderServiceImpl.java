package com.cloud.activiti.service.impl;

import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsQualityOrderService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.feign.RemoteQualityOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
            SmsQualityOrder smsQualityOrder  = remoteQualityOrderService.get(Long.valueOf(business.getTableId()));
            if(null == smsQualityOrder){
                logger.error("质量索赔工作流根据业务key获取质量索赔信息 根据主键id获取索赔信息失败Req主键id:{}",business.getTableId());
                return R.error("质量索赔工作流根据业务key获取质量索赔信息  根据主键id获取索赔信息失败");
            }
            logger.info("质量索赔工作流根据业务key获取质量索赔信息 索赔单号:{}",smsQualityOrder.getQualityNo());
            return R.data(smsQualityOrder);
        }
        return R.error("质量索赔工作流根据业务key获取质量索赔信息失败");
    }

    /**
     * 质量索赔信息开启流程
     * @param smsQualityOrder 质量索赔信息
     * @return 成功或失败
     */
    @Transactional
    @Override
    public R addSave(SmsQualityOrder smsQualityOrder,SysUser sysUser) {
        logger.info("质量索赔信息开启流程 质量索赔id:{},质量索赔索赔单号:{}",smsQualityOrder.getId(),
                smsQualityOrder.getQualityNo());
        //构造质量索赔流程信息
        BizBusiness business = initBusiness(smsQualityOrder,sysUser);
        //新增质量索赔流程
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //启动质量索赔流程
        bizBusinessService.startProcess(business, variables);
        return R.ok();
    }

    /**
     * biz构造业务信息
     * @param smsQualityOrder 质量索赔信息
     * @return
     */
    private BizBusiness initBusiness(SmsQualityOrder smsQualityOrder,SysUser sysUser) {
        BizBusiness business = new BizBusiness();
        business.setTableId(smsQualityOrder.getId().toString());
        business.setProcDefId(smsQualityOrder.getProcDefId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SQUALITY_TEST);
        business.setProcName(smsQualityOrder.getProcName());
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
            return R.error("质量索赔审批流程 查询流程业务失败");
        }
        //获取质量索赔信息
        logger.info ("质量索赔审批流程 获取质量索赔信息主键id:{}",bizBusiness.getTableId());
        SmsQualityOrder smsQualityOrder = remoteQualityOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if(null == smsQualityOrder){
            logger.error ("质量索赔审批流程 查询质量索赔信息失败Req主键id:{}",bizBusiness.getTableId());
            return R.error("质量索赔审批流程 查询质量索赔信息失败");
        }
        //状态是否是待质量部部长审核
        Boolean flagStatus4 = QualityStatusEnum.QUALITY_STATUS_4.getCode().equals(smsQualityOrder.getQualityStatus());
        //状态是否是待小微主审核
        Boolean flagStatus5 = QualityStatusEnum.QUALITY_STATUS_5.getCode().equals(smsQualityOrder.getQualityStatus());
        if (null == smsQualityOrder || !flagStatus4 || !flagStatus5) {
            logger.error ("质量索赔审批流程 查询质量索赔信息失败Req主键id:{} 状态:{}",bizBusiness.getTableId(),smsQualityOrder.getQualityStatus());
            return R.error("质量索赔审批流程 质量索赔单不可审核");
        }
        smsQualityOrder.setRemark("我已经审批了，审批结果：" + bizAudit.getResult());
        //根据角色和质量索赔状态审批
        //质量部部长审批: 将供应商申诉4--->待小微主审核5
        if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_ZLBBZ)){
            if(flagStatus4){
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_5.getCode());
            }
        }
        //小微主审批: 将待小微主审核5--->待结算11
        if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)){
            if(flagStatus5){
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
            }
        }
        //更新质量索赔状态
        logger.info ("质量索赔审批流程 更新质量索赔主键id:{} 状态:{}",smsQualityOrder.getId(),smsQualityOrder.getQualityStatus());
        R updateResult = remoteQualityOrderService.editSave(smsQualityOrder);
        if("0".equals(updateResult.get("code").toString())){
            //审批 推进工作流
            return actTaskService.audit(bizAudit, sysUser.getUserId());
        }
        return R.error();
    }
}
