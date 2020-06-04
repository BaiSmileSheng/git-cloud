package com.cloud.settle.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.mapper.SmsScrapOrderMapper;
import com.cloud.settle.service.ISmsScrapOrderService;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 报废申请 Service业务层处理
 *
 * @author cs
 * @date 2020-05-29
 */
@Slf4j
@Service
public class SmsScrapOrderServiceImpl extends BaseServiceImpl<SmsScrapOrder> implements ISmsScrapOrderService {
    @Autowired
    private SmsScrapOrderMapper smsScrapOrderMapper;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private RemoteFactoryLineInfoService remotefactoryLineInfoService;

    /**
     * 编辑报废申请单功能  --有状态校验
     * @param smsScrapOrder
     * @return
     */
    @Override
    public R editSave(SmsScrapOrder smsScrapOrder) {
        Long id = smsScrapOrder.getId();
        log.info(StrUtil.format("报废申请修改保存开始：参数为{}", smsScrapOrder.toString()));
        //校验状态是否是未提交
        R rCheckStatus = checkCondition(id);
        SmsScrapOrder smsScrapOrderCheck = (SmsScrapOrder) rCheckStatus.get("data");
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,smsScrapOrderCheck.getProductOrderCode());
        if (!rCheck.isSuccess()) {
            return rCheck;
        }
        int rows = updateByPrimaryKeySelective(smsScrapOrder);
        return rows > 0 ? R.ok() : R.error("更新错误！");
    }

    /**
     * 新增保存报废申请
     * @param smsScrapOrder
     * @return
     */
    @Override
    public R addSave(SmsScrapOrder smsScrapOrder, SysUser sysUser) {
        //生产订单号
        String productOrderCode = smsScrapOrder.getProductOrderCode();
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,productOrderCode);
        if (!rCheck.isSuccess()) {
            return rCheck;
        }

        //TODO:新增提交   获取数据  插入数据
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        //根据线体号查询供应商编码
        CdFactoryLineInfo factoryLineInfo = remotefactoryLineInfoService.selectInfoByCodeLineCode(omsProductionOrder.getProductLineCode());
        if (factoryLineInfo != null) {
            smsScrapOrder.setSupplierCode(factoryLineInfo.getSupplierCode());
            smsScrapOrder.setSupplierName(factoryLineInfo.getSupplierDesc());
        }
        smsScrapOrder.setFactoryCode(omsProductionOrder.getFactoryCode());
//        smsScrapOrder.setComponyCode();
        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode());
//        smsScrapOrder.setMaterialPrice();
        smsScrapOrder.setDelFlag("0");
        smsScrapOrder.setCreateBy(sysUser.getLoginName());
        smsScrapOrder.setCreateTime(DateUtils.getNowDate());
        int rows=insertSelective(smsScrapOrder);
        if (rows > 0) {
            return R.data(smsScrapOrder.getId());
        }else{
            return R.error("报废申请插入失败！");
        }
    }

    /**
     * 1、校验物料号是否同步了sap价格
     * @param smsScrapOrder
     * @param productOrderCode
     * @return
     */
    R checkScrapOrderCondition(SmsScrapOrder smsScrapOrder,String productOrderCode) {
        if (smsScrapOrder.getScrapAmount() == null) {
            return R.error("报废数量为空！");
        }
        if (productOrderCode == null) {
            return R.error("生产订单号为空！");
        }
        //校验物料号是否同步了sap价格
        R r=remoteCdMaterialPriceInfoService.checkSynchroSAP(smsScrapOrder.getProductMaterialCode());
        if(!r.isSuccess()){
            return r;
        }
        return R.ok();
    }

    /**
     * 删除报废申请
     * @param ids
     * @return
     */
    @Override
    public R remove(String ids) {
        log.info(StrUtil.format("报废申请删除开始：id为{}", ids));
        if(StringUtils.isBlank(ids)){
            throw new BusinessException("传入参数不能为空！");
        }
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            checkCondition(Long.valueOf(id));
        }
        int rows = deleteByIds(ids);
        return rows > 0 ? R.ok() : R.error("删除错误！");
    }

    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return 返回SmsScrapOrder信息
     */
    public R checkCondition(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsScrapOrder smsScrapOrder = selectByPrimaryKey(id);
        if (smsScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsScrapOrder.getScrapStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsScrapOrder);
    }
}
