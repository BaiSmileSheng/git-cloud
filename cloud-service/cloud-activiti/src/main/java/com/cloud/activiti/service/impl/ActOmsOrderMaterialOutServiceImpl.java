package com.cloud.activiti.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.consts.ActivitiProDefKeyConstants;
import com.cloud.activiti.consts.ActivitiProTitleConstants;
import com.cloud.activiti.domain.ActReProcdef;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.service.IActOmsOrderMaterialOutService;
import com.cloud.activiti.service.IActReProcdefService;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.enums.RealOrderAduitStatusEnum;
import com.cloud.order.feign.RemoteOmsRealOrderService;
import com.cloud.system.domain.entity.SysUser;
import com.google.common.collect.Maps;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class ActOmsOrderMaterialOutServiceImpl implements IActOmsOrderMaterialOutService {

    private static Logger logger = LoggerFactory.getLogger(ActOmsOrderMaterialOutServiceImpl.class);

    @Autowired
    private IBizBusinessService bizBusinessService;
    @Autowired
    private IActReProcdefService actReProcdefService;
    @Autowired
    private IActTaskService actTaskService;
    @Autowired
    private RemoteOmsRealOrderService remoteOmsRealOrderService;

    /**
     * 根据业务key获取真单审核信息
     * @param businessKey biz_business的主键
     * @return 查询结果包含 真单审核信息
     */
    @Override
    public R getBizInfoByTableId(String businessKey) {
        logger.info("真单工作流根据业务key获取真单信息 businessKey:{}",businessKey);
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            logger.info("真单工作流根据业务key获取真单信息 主键id:{}",business.getTableId());
            //根据主键id 获取对应的真单
            R selectResult = remoteOmsRealOrderService.get(Long.valueOf(business.getTableId()));
            return selectResult;
        }
        return R.error("真单工作流根据业务key获取真单信息失败");
    }

    /**
     * 物料下市审核 真单审核开启流程
     * @return 成功或失败
     */
    @Override
    public R addSave(OmsOrderMaterialOutVo omsOrderMaterialOutVo) {
        List<OmsOrderMaterialOutVo> list = omsOrderMaterialOutVo.getOmsOrderMaterialOutVoList();
        //查流程定义  procDefId procName
        ActReProcdef actReProcdef = new ActReProcdef();
        actReProcdef.setKey(ActivitiProDefKeyConstants.ACTIVITI_PRO_DEF_KEY_MATERIAL_OUT_TEST);
        List<ActReProcdef> actReProcdefList = actReProcdefService.selectList(actReProcdef);
        if(CollectionUtils.isEmpty(actReProcdefList)){
            throw new BusinessException("获取流程定义为空,请先定义流程");
        }
        Collections.sort(actReProcdefList, Comparator.comparing(ActReProcdef::getVersion).reversed());
        ActReProcdef actReProcdefRes = actReProcdefList.get(0);
        if(!CollectionUtils.isEmpty(list)){
            list.forEach(omsOrderMaterialOutVoReq ->{
                //1.构造其他索赔流程信息
                BizBusiness business = initBusiness(omsOrderMaterialOutVoReq,actReProcdefRes);
                //新增真单审核流程
                bizBusinessService.insertBizBusiness(business);
                Map<String, Object> variables = Maps.newHashMap();
                //启动真单审核流程
                bizBusinessService.startProcess(business, variables);
            });
        }
        return R.ok();
    }

    /**
     * biz构造业务信息
     * @param omsOrderMaterialOutVo 审核信息
     * @return
     */
    private BizBusiness initBusiness(OmsOrderMaterialOutVo omsOrderMaterialOutVo,ActReProcdef actReProcdefRes) {
        BizBusiness business = new BizBusiness();
        business.setOrderNo(omsOrderMaterialOutVo.getOrderCode());
        business.setTableId(omsOrderMaterialOutVo.getId().toString());
        business.setProcDefId(actReProcdefRes.getId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SMATERIAL_OUT_TEST);
        business.setProcName(actReProcdefRes.getName());
        business.setUserId(omsOrderMaterialOutVo.getLoginId());
        business.setApplyer(omsOrderMaterialOutVo.getCreateBy());
        //设置流程状态
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 下市流程审批
     * @param bizAudit
     * @param sysUser 当前用户信息
     * @return
     */
    @GlobalTransactional
    @Override
    public R audit(BizAudit bizAudit, SysUser sysUser) {
        //1.查询可处理业务逻辑(获取其他索赔id)
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            logger.error ("真单  审批流程 查询流程业务失败Req主键id:{}",bizAudit.getBusinessKey().toString());
            return R.error("真单 审批流程 查询流程业务失败");
        }
        //2.根据id获取真单 判断是否待审批
        logger.info ("真单 审批流程 获取其他索赔信息主键id:{}",bizBusiness.getTableId());
        R omsRealOrderR = remoteOmsRealOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if(!omsRealOrderR.isSuccess()){
            logger.error ("真单 审批流程 查询真单信息失败Req主键id:{}",bizBusiness.getTableId());
            return R.error("真单审批流程 查询真单信息失败");
        }
        OmsRealOrder omsRealOrder = omsRealOrderR.getData(OmsRealOrder.class);
        //状态是否是审核中
        Boolean flagStatus3 = RealOrderAduitStatusEnum.AUDIT_STATUS_SHZ.getCode().equals(omsRealOrder.getAuditStatus());
        if (!flagStatus3) {
            logger.error ("真单审批流程 查询真单信息失败Req主键id:{} 状态:{}",bizBusiness.getTableId(),omsRealOrder.getAuditStatus());
            return R.error("真单审批流程 真单不可审核");
        }

        //3.根据结果修改真单信息
        //审核结果 2表示通过,3表示驳回
        Boolean flagBizResult = "2".equals(bizAudit.getResult().toString());
        //订单经理进行审核
        if(flagBizResult){
            omsRealOrder.setAuditStatus(RealOrderAduitStatusEnum.AUDIT_STATUS_SHWC.getCode());
        }else{
            omsRealOrder.setAuditStatus(RealOrderAduitStatusEnum.AUDIT_STATUS_SHBO.getCode());
        }
        //更新真单审核状态
        logger.info ("真单审批流程 更新更新真单审核主键id:{} 状态:{}",omsRealOrder.getId(),omsRealOrder.getAuditStatus());
        R updateResult = remoteOmsRealOrderService.editSave(omsRealOrder);
        if(!updateResult.isSuccess()){
            logger.error("真单审批流程 更新更新真单审核主键id:{}res:{}",omsRealOrder.getId(),JSONObject.toJSON(updateResult));
            throw new BusinessException("真单审批流程 更新更新真单审核失败 ");
        }
        //4.审批 推进工作流
        R resultAck = actTaskService.audit(bizAudit, sysUser.getUserId());
        if(!resultAck.isSuccess()){
            logger.error("真单审批流程 审批 推进工作流 req:{}res:{}",JSONObject.toJSON(bizAudit),JSONObject.toJSON(updateResult));
            throw new BusinessException("真单审批流程审批 推进工作流失败 ");
        }
        return R.error();
    }
}
