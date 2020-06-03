package com.cloud.settle.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.mapper.SmsSupplementaryOrderMapper;
import com.cloud.settle.service.ISmsSupplementaryOrderService;
import com.cloud.system.domain.entity.CdBom;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import com.cloud.system.feign.RemoteSequeceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 物耗申请单 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Slf4j
@Service
public class SmsSupplementaryOrderServiceImpl extends BaseServiceImpl<SmsSupplementaryOrder> implements ISmsSupplementaryOrderService {
    @Autowired
    private SmsSupplementaryOrderMapper smsSupplementaryOrderMapper;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteBomService remoteBomService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private RemoteFactoryLineInfoService remotefactoryLineInfoService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;

    @Override
    public R editSave(SmsSupplementaryOrder smsSupplementaryOrder) {
        Long id = smsSupplementaryOrder.getId();
        log.info(StrUtil.format("物耗申请修改保存开始：参数为{}", smsSupplementaryOrder.toString()));
        //校验状态是否是未提交
        R rCheckStatus = checkCondition(id);
        SmsSupplementaryOrder smsSupplementaryOrderCheck = (SmsSupplementaryOrder) rCheckStatus.get("data");
        //校验
        R rCheck = checkSmsSupplementaryOrderCondition(smsSupplementaryOrder, smsSupplementaryOrderCheck.getProductOrderCode());
        if (!rCheck.isSuccess()) {
            return rCheck;
        }
        int rows = updateByPrimaryKeySelective(smsSupplementaryOrder);
        if (rows > 0) {
            return R.ok();
        } else {
            return R.error("物耗申请更新失败！");
        }
    }

    @Override
    public R remove(String ids) {
        log.info(StrUtil.format("物耗申请删除开始：id为{}", ids));
        if (StringUtils.isBlank(ids)) {
            throw new BusinessException("传入参数不能为空！");
        }
        for (String id : ids.split(",")) {
            //校验状态是否是未提交
            checkCondition(Long.valueOf(id));
        }
        int rows = deleteByIds(ids);
        return rows > 0 ? R.ok() : R.error("删除错误！");
    }

    @Override
    public R addSave(SmsSupplementaryOrder smsSupplementaryOrder) {
        String productOrderCode = smsSupplementaryOrder.getProductOrderCode();
        //校验
        R rCheck = checkSmsSupplementaryOrderCondition(smsSupplementaryOrder, productOrderCode);
        if (!rCheck.isSuccess()) {
            return rCheck;
        }
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        String productMaterialCode = omsProductionOrder.getProductMaterialCode();
        String rawMaterialCode = smsSupplementaryOrder.getRawMaterialCode();


        //根据物料号  有效期查询SAP价格
        String date = DateUtils.getTime();
        List<CdMaterialPriceInfo> materialPrices = remoteCdMaterialPriceInfoService.findByMaterialCode(rawMaterialCode,date,date);
        if (materialPrices == null || materialPrices.size() == 0) {
            return R.error("物料成本价格未维护！");
        }
        CdMaterialPriceInfo cdMaterialPriceInfo = materialPrices.get(0);

        //开始插入
        String seq = remoteSequeceService.selectSeq("supplementary_seq", 4);
        StringBuffer stuffNo = new StringBuffer();
        //WH+年月日+4位顺序号
        stuffNo.append("WH").append(DateUtils.dateTime()).append(seq);
        smsSupplementaryOrder.setStuffNo(stuffNo.toString());
        //根据线体号查询供应商编码
        CdFactoryLineInfo factoryLineInfo = remotefactoryLineInfoService.selectInfoByCodeLineCode(omsProductionOrder.getProductLineCode());
        if (factoryLineInfo != null) {
            smsSupplementaryOrder.setSupplierCode(factoryLineInfo.getSupplierCode());
            smsSupplementaryOrder.setSupplierName(factoryLineInfo.getSupplierDesc());
        }
        smsSupplementaryOrder.setFactoryCode(omsProductionOrder.getFactoryCode());
        smsSupplementaryOrder.setProductOrderCode(omsProductionOrder.getProductOrderCode());//生产订单号
        if (StrUtil.isBlank(smsSupplementaryOrder.getStuffStatus())) {
            smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode());//状态：待提交
        }
        smsSupplementaryOrder.setStuffPrice(cdMaterialPriceInfo.getNetWorth());//单价  取得materialPrice表的净价值
        smsSupplementaryOrder.setStuffUnit(cdMaterialPriceInfo.getUnit());
        smsSupplementaryOrder.setCurrency(cdMaterialPriceInfo.getCurrency());//币种
        CdBom cdBom = remoteBomService.listByProductAndMaterial(productMaterialCode, rawMaterialCode);
        smsSupplementaryOrder.setSapStoreage(cdBom.getStoragePoint());
        smsSupplementaryOrder.setPurchaseGroupCode(cdBom.getPurchaseGroup());
        smsSupplementaryOrder.setDelFlag("0");
        smsSupplementaryOrder.setCreateTime(DateUtils.getNowDate());
        int rows = insertSelective(smsSupplementaryOrder);
        if (rows > 0) {
            return R.data(smsSupplementaryOrder.getId());
        } else {
            return R.error("物耗申请插入失败！");
        }
    }

    /**
     * 1、校验物料号是否同步了sap价格
     * 2、校验修改申请数量是否是最小包装量的整数倍
     * 3、校验申请数量是否是单耗的整数倍
     * 4、校验申请量与单号是否整数倍
     * 5、校验申请量是否大于订单量*单耗
     *
     * @param smsSupplementaryOrder
     * @param productOrderCode
     * @return
     */
    R checkSmsSupplementaryOrderCondition(SmsSupplementaryOrder smsSupplementaryOrder, String productOrderCode) {
        if (smsSupplementaryOrder.getStuffAmount() == null) {
            return R.error("申请数量为空！");
        }
        if (smsSupplementaryOrder.getRawMaterialCode() == null) {
            return R.error("物料号为空！");
        }
        if (productOrderCode == null) {
            return R.error("生产订单号为空！");
        }

        //1、校验物料号是否同步了sap价格
        R r = remoteCdMaterialPriceInfoService.checkSynchroSAP(smsSupplementaryOrder.getRawMaterialCode());
        if (!r.isSuccess()) {
            return r;
        }
        //将返回值Map转为CdMaterialPriceInfo
        CdMaterialPriceInfo cdMaterialPriceInfo = BeanUtil.mapToBean((Map<?, ?>) r.get("data"), CdMaterialPriceInfo.class, true);
        //2、校验修改申请数量是否是最小包装量的整数倍
        int applyNum = smsSupplementaryOrder.getStuffAmount();//申请量
        //最小包装量
        int minUnit = Integer.parseInt(cdMaterialPriceInfo.getPriceUnit() == null ? "0" : cdMaterialPriceInfo.getPriceUnit());
        if (minUnit == 0) {
            return R.error("最小包装量不正确！");
        }
        if (applyNum % minUnit != 0) {
            return R.error("申请量必须是最小包装量的整数倍！");
        }
        //3、校验申请数量是否是单耗的整数倍
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if (omsProductionOrder == null) {
            return R.error("排产订单信息不存在！");
        }
        //根据成品物料号和原材料物料号取bom单耗
        String productMaterialCode = omsProductionOrder.getProductMaterialCode();
        String rawMaterialCode = smsSupplementaryOrder.getRawMaterialCode();
        //4、校验申请量与单号是否整数倍
        R rBomNum = remoteBomService.checkBomNum(productMaterialCode, rawMaterialCode, applyNum);
        if (!rBomNum.isSuccess()) {
            return rBomNum;
        }
        //5、校验申请量是否大于订单量*单耗
        BigDecimal productNum = omsProductionOrder.getProductNum();
        if (new BigDecimal(applyNum).compareTo(productNum.multiply(new BigDecimal(rBomNum.get("data").toString()))) >= 0) {
            return R.error("申请量不得大于订单量");
        }
        return R.ok();
    }

    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     *
     * @param id
     * @return 返回SmsSupplementaryOrder信息
     */
    public R checkCondition(Long id) {
        if (id == null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsSupplementaryOrder smsSupplementaryOrderCheck = selectByPrimaryKey(id);
        if (smsSupplementaryOrderCheck == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode().equals(smsSupplementaryOrderCheck.getStuffStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsSupplementaryOrderCheck);
    }
}
