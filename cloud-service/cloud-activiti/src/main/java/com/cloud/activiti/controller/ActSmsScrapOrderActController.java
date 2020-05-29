package com.cloud.activiti.controller;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActSmsScrapOrderService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsScrapOrderService;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 报废申请单 审核流程
 *
 * @author cs
 * @date 2020-05-20
 */
@RestController
@RequestMapping("actScrapOrder")
@Api(tags = "报废申请单审核流程 ")
public class ActSmsScrapOrderActController extends BaseController {
    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteSmsScrapOrderService remoteSmsScrapOrderService;

    @Autowired
    private IActSmsScrapOrderService actSmsScrapOrderService;

    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;


    /**
     * 根据业务key获取数据
     *
     * @param businessKey
     * @return smsSupplementaryOrder
     * @author cs
     */
    @GetMapping("biz/{businessKey}")
    @ApiOperation(value = "根据业务key获取数据", response = SmsScrapOrder.class)
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        //查询流程业务表
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            //根据流程业务表 tableId 查询业务表信息
            SmsScrapOrder smsScrapOrder = remoteSmsScrapOrderService.get(Long.valueOf(business.getTableId()));
            return R.data(smsScrapOrder);
        }
        return R.error("no record");
    }

    /**
     * 报废审核开启流程  提交
     *
     * @param smsSupplementaryOrder
     * @return R 成功/失败
     */
    @PostMapping("open")
    @OperLog(title = "报废审核开启流程 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "报废审核开启流程 ", response = R.class)
    public R addSave(@RequestBody SmsScrapOrder smsSupplementaryOrder) {
        //判断状态是否是未提交，如果不是则抛出错误
        Long id = smsSupplementaryOrder.getId();
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsScrapOrder smsScrapOrder = remoteSmsScrapOrderService.get(id);
        if (smsScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsScrapOrder.getScrapStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        //校验物料号是否同步了sap价格
        R r=remoteCdMaterialPriceInfoService.checkSynchroSAP(smsScrapOrder.getProductMaterialCode());
        if(!r.isSuccess()){
            throw new BusinessException(r.get("msg").toString());
        }
        //将返回值Map转为CdMaterialPriceInfo
        CdMaterialPriceInfo cdMaterialPriceInfo =BeanUtil.mapToBean((Map<?, ?>) r.get("data"), CdMaterialPriceInfo.class,true);
        //校验修改申请数量是否是最小包装量的整数倍
        int applyNum=smsSupplementaryOrder.getScrapAmount();//申请量
        //最小包装量
        int minUnit= Integer.parseInt(cdMaterialPriceInfo.getPriceUnit()==null?"0":cdMaterialPriceInfo.getPriceUnit());
        if(minUnit==0){
            throw new BusinessException("最小包装量不正确！");
        }
        if(applyNum%minUnit!=0){
            throw new BusinessException("申请量必须是最小包装量的整数倍！");
        }
        //开启审核流程
        return actSmsScrapOrderService.startAct(smsSupplementaryOrder,getCurrentUserId());
    }



    /**
     * 报废流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @PostMapping("audit")
    @ApiOperation(value = "报废流程审批 ", response = R.class)
    public R audit(@RequestBody BizAudit bizAudit) {
        //流程审核业务表
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            return R.error();
        }
        //查询物耗表信息
        SmsScrapOrder smsSupplementaryOrder = remoteSmsScrapOrderService.get(Long.valueOf(bizBusiness.getTableId()));
        if (smsSupplementaryOrder == null) {
            return R.error("未找到此业务数据！");
        }
        //审核
        return actSmsScrapOrderService.audit(bizAudit,smsSupplementaryOrder,getCurrentUserId());
    }
}
