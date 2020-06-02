package com.cloud.activiti.service.impl;

import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsDelaysDeliveryService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.feign.RemoteDelaysDeliveryService;
import com.cloud.system.domain.entity.SysUser;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

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


    /**
     * 延期索赔工作流根据业务key获取延期索赔信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 质量索赔信息
     */
    @Override
    public R getBizInfoByTableId(String businessKey) {
        logger.info("延期索赔工作流根据业务key获取质量索赔信息 businessKey:{}",businessKey);
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            logger.info("延期索赔工作流根据业务key获取质量索赔信息 主键id:{}",business.getTableId());
            //根据主键id 获取对应的质量索赔信息
            SmsDelaysDelivery smsDelaysDelivery  = remoteDelaysDeliveryService.get(Long.valueOf(business.getTableId()));
            if(null == smsDelaysDelivery){
                logger.error("延期索赔工作流根据业务key获取质量索赔信息 根据主键id获取索赔信息失败Req主键id:{}",business.getTableId());
                return R.error("延期索赔工作流根据业务key获取质量索赔信息  根据主键id获取索赔信息失败");
            }
            logger.info("延期索赔工作流根据业务key获取质量索赔信息 索赔单号:{}",smsDelaysDelivery.getDelaysNo());
            return R.data(smsDelaysDelivery);
        }
        return R.error("延期索赔工作流根据业务key获取质量索赔信息失败");
    }

    /**
     * 延期索赔开启流程
     * @param smsDelaysDelivery 延期索赔嘻嘻
     * @param sysUser 用户信息
     * @return
     */
    @GlobalTransactional
    @Override
    public R addSave(SmsDelaysDelivery smsDelaysDelivery,SysUser sysUser) {
        logger.info("延期索赔开启流程 延期索赔id:{},延期索赔索赔单号:{}",smsDelaysDelivery.getId(),
                smsDelaysDelivery.getDelaysNo());
        //构造延期索赔流程信息
        BizBusiness business = initBusiness(smsDelaysDelivery,sysUser);
        //新增延期索赔流程
        bizBusinessService.insertBizBusiness(business);
        Map<String, Object> variables = Maps.newHashMap();
        //启动延期索赔流程
        bizBusinessService.startProcess(business, variables);
        return R.ok();
    }

    /**
     * biz构造业务信息
     * @param smsDelaysDelivery
     * @return
     */
    private BizBusiness initBusiness(SmsDelaysDelivery smsDelaysDelivery,SysUser sysUser) {
        BizBusiness business = new BizBusiness();
        business.setTableId(smsDelaysDelivery.getId().toString());
        business.setProcDefId(smsDelaysDelivery.getProcDefId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SDEPALYS_TEST);
        business.setProcName(smsDelaysDelivery.getProcName());
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
        SmsDelaysDelivery smsDelaysDelivery = remoteDelaysDeliveryService.get(Long.valueOf(bizBusiness.getTableId()));
        if(null == smsDelaysDelivery){
            logger.error ("延期索赔审批流程 查询延期索赔信息失败Req主键id:{}",bizBusiness.getTableId());
            return R.error("延期索赔审批流程 查询延期索赔信息失败");
        }
        //状态是否是待订单部部长审核
        Boolean flagStatus4 = DeplayStatusEnum.DELAYS_STATUS_4.getCode().equals(smsDelaysDelivery.getDelaysStatus());
        //状态是否是待小微主审核
        Boolean flagStatus5 = DeplayStatusEnum.DELAYS_STATUS_5.getCode().equals(smsDelaysDelivery.getDelaysStatus());
        if (null == smsDelaysDelivery || !flagStatus4 || !flagStatus5) {
            logger.error ("延期索赔审批流程 查询延期索赔信息失败Req主键id:{} 状态:{}",
                    bizBusiness.getTableId(),smsDelaysDelivery.getDelaysStatus());
            return R.error("延期索赔审批流程 延期索赔单不可审核");
        }
        smsDelaysDelivery.setRemark("我已经审批了，审批结果：" + bizAudit.getResult());
        //根据角色和延期索赔状态审批
        //订单部部长审批: 将供应商申诉4--->待小微主审核5
        if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_DDBBZ)){
            if(flagStatus4){
                smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_5.getCode());
            }
        }
        //小微主审批: 将待小微主审核5--->待结算11
        if(sysUser.getRoleKeys().contains(RoleConstants.ROLE_KEY_XWZ)){
            if(flagStatus5){
                smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_11.getCode());
            }
        }
        //更新延期索赔状态
        logger.info ("延期索赔审批流程 更新延期索赔主键id:{} 状态:{}",smsDelaysDelivery.getId(),smsDelaysDelivery.getDelaysStatus());
        R updateResult = remoteDelaysDeliveryService.editSave(smsDelaysDelivery);
        if("0".equals(updateResult.get("code").toString())){
            //审批 推进工作流
            return actTaskService.audit(bizAudit, sysUser.getUserId());
        }
        return R.error();
    }
}
