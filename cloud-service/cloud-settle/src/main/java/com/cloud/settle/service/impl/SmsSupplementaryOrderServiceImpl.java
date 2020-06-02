package com.cloud.settle.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.mapper.SmsSupplementaryOrderMapper;
import com.cloud.settle.service.ISmsSupplementaryOrderService;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    public R editSave(SmsSupplementaryOrder smsSupplementaryOrder) {
        Long id = smsSupplementaryOrder.getId();
        log.info(StrUtil.format("物耗申请修改保存开始：参数为{}", smsSupplementaryOrder.toString()));
        //校验状态是否是未提交
        R rCheck=checkCondition(id);
        SmsSupplementaryOrder smsSupplementaryOrderCheck = (SmsSupplementaryOrder) rCheck.get("data");
        //校验物料号是否同步了sap价格
        R r=remoteCdMaterialPriceInfoService.checkSynchroSAP(smsSupplementaryOrder.getRawMaterialCode());
        if(!r.isSuccess()){
            return r;
        }
        //将返回值Map转为CdMaterialPriceInfo
        CdMaterialPriceInfo cdMaterialPriceInfo = BeanUtil.mapToBean((Map<?, ?>) r.get("data"), CdMaterialPriceInfo.class,true);
        //校验申请数量是否是最小包装量的整数倍
        int applyNum=smsSupplementaryOrder.getStuffAmount();//申请量
        R rMin = remoteCdMaterialPriceInfoService.checkIsMinUnit(cdMaterialPriceInfo.getMaterialCode(),applyNum);
        if (!rMin.isSuccess()) {
            return rMin;
        }
        //校验申请数量是否是单耗的整数倍
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder=remoteProductionOrderService.selectByProdctOrderCode(smsSupplementaryOrderCheck.getProductOrderCode());
        if (omsProductionOrder == null) {
            throw new BusinessException("排产订单信息不存在！");
        }
        //根据成品物料号和原材料物料号取bom单耗
        String productMaterialCode = omsProductionOrder.getProductMaterialCode();
        String rawMaterialCode = smsSupplementaryOrder.getRawMaterialCode();
        //校验申请量与单号是否整数倍
        R rBomNum = remoteBomService.checkBomNum(productMaterialCode,rawMaterialCode,applyNum);
        if (!rBomNum.isSuccess()) {
            return rBomNum;
        }
        BigDecimal bomNum = new BigDecimal(rBomNum.get("data").toString());//单耗
        //校验申请量是否大于订单量
        BigDecimal productNum=omsProductionOrder.getProductNum();//订单量
        BigDecimal totalNum = productNum.multiply(bomNum);//订单量*单耗
        if (new BigDecimal(applyNum).compareTo(totalNum) >= 0) {
            return R.error("申请量不得大于订单量");
        }
        int rows = updateByPrimaryKeySelective(smsSupplementaryOrder);
        return rows > 0 ? R.ok() : R.error("更新错误！");
    }

    @Override
    public R remove(String ids) {
        log.info(StrUtil.format("物耗申请删除开始：id为{}", ids));
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
     * @return 返回SmsSupplementaryOrder信息
     */
    public R checkCondition(Long id){
        if (id==null) {
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
