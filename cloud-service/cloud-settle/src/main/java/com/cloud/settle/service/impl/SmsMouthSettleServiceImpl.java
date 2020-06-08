package com.cloud.settle.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.utils.DateUtils;
import com.cloud.settle.domain.entity.*;
import com.cloud.settle.enums.*;
import com.cloud.settle.mapper.*;
import com.cloud.settle.service.*;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.CdSettleRatio;
import com.cloud.system.enums.SettleRatioEnum;
import com.cloud.system.feign.RemoteCdMaterialPriceInfoService;
import com.cloud.system.feign.RemoteCdMouthRateService;
import com.cloud.system.feign.RemoteSequeceService;
import com.cloud.system.feign.RemoteSettleRatioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 月度结算信息 Service业务层处理
 *
 * @author cs
 * @date 2020-06-04
 */
@Service
@Slf4j
public class SmsMouthSettleServiceImpl extends BaseServiceImpl<SmsMouthSettle> implements ISmsMouthSettleService {
    @Autowired
    private SmsMouthSettleMapper smsMouthSettleMapper;
    @Autowired
    private ISmsSettleInfoService smsSettleInfoService;
    @Autowired
    private ISmsSupplementaryOrderService smsSupplementaryOrderService;
    @Autowired
    private ISmsScrapOrderService smsScrapOrderService;
    @Autowired
    private RemoteCdMouthRateService remoteCdMouthRateService;
    @Autowired
    private RemoteSettleRatioService remoteSettleRatioService;
    @Autowired
    private SmsQualityOrderMapper smsQualityOrderMapper;
    @Autowired
    private SmsDelaysDeliveryMapper smsDelaysDeliveryMapper;
    @Autowired
    private SmsClaimOtherMapper smsClaimOtherMapper;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private SmsSupplementaryOrderMapper smsSupplementaryOrderMapper;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private ISmsClaimCashDetailService smsClaimCashDetailService;

    /**
     * 月度结算定时任务   计算上个月
     *
     * @return
     */
    @Override
    public R countMonthSettle() {
        log.info("----------------------------月度结算定时任务开始------------------------------");
        Date date = DateUtil.date();//当前时间
        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");//计算月份：上个月
        //查询上个月汇率
        R rRate = remoteCdMouthRateService.findRateByYearMouth(lastMonth);
        if (!rRate.isSuccess()) {
            return R.error(StrUtil.format("{}月份未维护费率", lastMonth));
        }
        BigDecimal rate = new BigDecimal(rRate.get("data").toString()) ;//汇率

        //将加工费及索赔、历史按照供应商和付款公司分组
        //报废及历史数据分组map
        Map<String, List<SmsScrapOrder>> mapScrap = new ConcurrentHashMap<>();
        Map<String, List<SmsScrapOrder>> mapScrapLS = new ConcurrentHashMap<>();
        //物耗及历史数据分组map
        Map<String, List<SmsSupplementaryOrder>> mapSupplement = new ConcurrentHashMap<>();
        Map<String, List<SmsSupplementaryOrder>> mapSupplementLS = new ConcurrentHashMap<>();
        //质量及历史数据分组map
        Map<String, List<SmsQualityOrder>> mapQuality = new ConcurrentHashMap<>();
        Map<String, List<SmsQualityOrder>> mapQualityLS = new ConcurrentHashMap<>();
        //延期及历史数据分组map
        Map<String, List<SmsDelaysDelivery>> mapDelays = new ConcurrentHashMap<>();
        Map<String, List<SmsDelaysDelivery>> mapDelaysLS = new ConcurrentHashMap<>();
        //其他及历史数据分组map
        Map<String, List<SmsClaimOther>> mapOther = new ConcurrentHashMap<>();
        Map<String, List<SmsClaimOther>> mapOtherLS = new ConcurrentHashMap<>();
        //结算分组map
        Map<String, List<SmsSettleInfo>> mapSettle = new ConcurrentHashMap<>();
        String key;//分组map的key supplierCode+componyCode

        /**-------------------物耗计算索赔  分组开始----------------------------------**/
        //物耗索赔系数
        CdSettleRatio cdSettleRatioWH = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_WH.getCode());
        if (cdSettleRatioWH == null) {
            log.error("物耗索赔系数未维护！");
            return R.error("物耗索赔系数未维护！");
        }
        //查询上个月、待结算的物耗申请中的物料号  用途是查询SAP成本价 更新到物耗表
        List<String> materialCodeList = smsSupplementaryOrderMapper.selectMaterialByMonthAndStatus(lastMonth, CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode()));
        Map<String, CdMaterialPriceInfo> mapMaterialPrice = new ConcurrentHashMap<>();
        if (materialCodeList != null) {
            log.info(StrUtil.format("(月度结算定时任务)物耗申请需要更新成本价格的物料号:{}",materialCodeList.toString()));
            String now = DateUtil.now();
            String materialCodeStr = StrUtil.join(",",materialCodeList);
            //根据前面查出的物料号查询SAP成本价 map key:物料号  value:CdMaterialPriceInfo
            mapMaterialPrice = remoteCdMaterialPriceInfoService.selectPriceByInMaterialCodeAndDate(materialCodeStr, now, now);
        }
        //取得计算月份、待结算的物耗申请数据
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode()));
        //循环物耗，更新成本价格，计算索赔金额
        if (smsSupplementaryOrderList != null) {
            for (SmsSupplementaryOrder smsSupplementaryOrder : smsSupplementaryOrderList) {
                CdMaterialPriceInfo cdMaterialPriceInfo = mapMaterialPrice.get(smsSupplementaryOrder.getRawMaterialCode());
                if (cdMaterialPriceInfo == null) {
                    //如果没有找到SAP价格，则更新备注
                    log.info(StrUtil.format("(月度结算定时任务)SAP价格未同步的物料号:{}",smsSupplementaryOrder.getRawMaterialCode()));
                    smsSupplementaryOrder.setRemark("SAP价格未同步！");
                    smsSupplementaryOrderService.updateByPrimaryKeySelective(smsSupplementaryOrder);
                    continue;
                }
                smsSupplementaryOrder.setStuffPrice(cdMaterialPriceInfo.getNetWorth());//单价  取得materialPrice表的净价值
                smsSupplementaryOrder.setStuffUnit(cdMaterialPriceInfo.getUnit());
                smsSupplementaryOrder.setCurrency(cdMaterialPriceInfo.getCurrency());//币种
                //索赔金额=物耗数量* 原材料单价*物耗申请系数
                BigDecimal spPrice;//索赔金额
                BigDecimal stuffAmount = new BigDecimal(smsSupplementaryOrder.getStuffAmount());//物耗数量
                BigDecimal stuffPrice = smsSupplementaryOrder.getStuffPrice();//原材料单价
                BigDecimal ratio = cdSettleRatioWH.getRatio();//物耗索赔系数
                spPrice = stuffAmount.multiply(stuffPrice.multiply(ratio));
                if ("USD".equals(smsSupplementaryOrder.getCurrency())) {
                    //如果是美元，还要*汇率
                    spPrice = spPrice.multiply(rate);
                    smsSupplementaryOrder.setRate(rate);
                }
                smsSupplementaryOrder.setSettleFee(spPrice);

                //下面开始分组：根据供应商和付款公司分组
                //key:supplierCode+componyCode
                key = smsSupplementaryOrder.getSupplierCode() + smsSupplementaryOrder.getComponyCode();
                List<SmsSupplementaryOrder> supplementList = mapSupplement.getOrDefault(key, new ArrayList<>());
                supplementList.add(smsSupplementaryOrder);
                mapSupplement.put(key, supplementList);
            }
        }

        //下面对物耗申请历史未兑现完成的数据进行分组
        //取得计算月份、未兑现、部分兑现的物耗数据
        List<String> statusSup = CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_WDX.getCode(), SupplementaryOrderStatusEnum.WH_ORDER_STATUS_BFDX.getCode());
        List<SmsSupplementaryOrder> smsSupplementaryOrderListLS = smsSupplementaryOrderService.selectByMonthAndStatus(null, statusSup);
        //循环物耗，计算索赔金额
        if (smsSupplementaryOrderListLS != null) {
            for (SmsSupplementaryOrder smsSupplementaryOrder : smsSupplementaryOrderListLS) {
                //根据供应商和付款公司分组
                //key:supplierCode+componyCode
                key = smsSupplementaryOrder.getSupplierCode() + smsSupplementaryOrder.getComponyCode();
                List<SmsSupplementaryOrder> supplementList = mapSupplementLS.getOrDefault(key, new ArrayList<>());
                supplementList.add(smsSupplementaryOrder);
                mapSupplementLS.put(key, supplementList);
            }
        }
        /**-------------------物耗计算索赔  分组结束----------------------------------**/

        /**-------------------报废计算索赔  分组开始----------------------------------**/
        //报废索赔系数
        CdSettleRatio cdSettleRatioBF = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_BF.getCode());
        if (cdSettleRatioBF == null) {
            log.error("(月度结算定时任务)报废索赔系数未维护！");
            return R.error("报废索赔系数未维护！");
        }
        //取得计算月份、待结算的报废数据
        List<SmsScrapOrder> smsScrapOrderList = smsScrapOrderService.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode()));
        //循环报废，计算索赔金额
        if (smsScrapOrderList != null) {
            for (SmsScrapOrder smsScrapOrder : smsScrapOrderList) {
                //索赔金额=（Sap成品物料销售价格*报废数量*报废索赔系数）+（报废数量*生产订单加工费单价）
                BigDecimal scrapPrice = BigDecimal.ZERO;//索赔金额
                BigDecimal scrapAmount = new BigDecimal(smsScrapOrder.getScrapAmount());//报废数量
                BigDecimal materialPrice = smsScrapOrder.getMaterialPrice();//成品物料销售价格
                BigDecimal ratio = cdSettleRatioBF.getRatio();//报废索赔系数
                BigDecimal machiningPrice = smsScrapOrder.getMachiningPrice();//加工费单价
                scrapPrice = (materialPrice.multiply(scrapAmount.multiply(ratio))).add(scrapAmount.multiply(machiningPrice));
                if ("USD".equals(smsScrapOrder.getCurrency())) {
                    //如果是美元，还要*汇率
                    scrapPrice = scrapPrice.multiply(rate);
                }
                smsScrapOrder.setSettleFee(scrapPrice);
                //根据供应商和付款公司分组
                //key:supplierCode+componyCode
                key = smsScrapOrder.getSupplierCode() + smsScrapOrder.getComponyCode();
                List<SmsScrapOrder> scrapOrderList = mapScrap.getOrDefault(key, new ArrayList<>());
                scrapOrderList.add(smsScrapOrder);
                mapScrap.put(key, scrapOrderList);
            }
        }
        //历史数据分组
        //取得计算月份、未兑现、部分兑现的报废数据
        List<String> statusScrap = CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_WDX.getCode(), ScrapOrderStatusEnum.BF_ORDER_STATUS_BFDX.getCode());
        List<SmsScrapOrder> smsScrapOrderListLS = smsScrapOrderService.selectByMonthAndStatus(null, statusScrap);
        //循环报废，计算索赔金额
        if (smsScrapOrderListLS != null) {
            for (SmsScrapOrder smsScrapOrder : smsScrapOrderListLS) {
                //根据供应商和付款公司分组
                //key:supplierCode+componyCode
                key = smsScrapOrder.getSupplierCode() + smsScrapOrder.getComponyCode();
                List<SmsScrapOrder> scrapOrderList = mapScrapLS.getOrDefault(key, new ArrayList<>());
                scrapOrderList.add(smsScrapOrder);
                mapScrapLS.put(key, scrapOrderList);
            }
        }
        /**-------------------报废计算索赔  分组结束----------------------------------**/

        /**-------------------质量分组开始----------------------------------**/
        //上个月、待结算质量索赔数据
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderMapper.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(QualityStatusEnum.QUALITY_STATUS_11.getCode()));
        //循环根据供应商和付款公司分组
        if (smsQualityOrderList != null) {
            for (SmsQualityOrder smsQuality : smsQualityOrderList) {
                //key:supplierCode+componyCode
                key = smsQuality.getSupplierCode() + smsQuality.getComponyCode();
                List<SmsQualityOrder> qualityList = mapQuality.getOrDefault(key, new ArrayList<>());
                qualityList.add(smsQuality);
                mapQuality.put(key, qualityList);
            }
        }
        //历史未兑现、部分兑现数据分组
        List<String> statusQuality = CollUtil.newArrayList(QualityStatusEnum.QUALITY_STATUS_15.getCode(), QualityStatusEnum.QUALITY_STATUS_14.getCode());
        List<SmsQualityOrder> smsQualityOrderListLS = smsQualityOrderMapper.selectByMonthAndStatus(null, statusQuality);
        //循环根据供应商和付款公司分组
        if (smsQualityOrderListLS != null) {
            for (SmsQualityOrder smsQuality : smsQualityOrderListLS) {
                //key:supplierCode+componyCode
                key = smsQuality.getSupplierCode() + smsQuality.getComponyCode();
                List<SmsQualityOrder> qualityList = mapQualityLS.getOrDefault(key, new ArrayList<>());
                qualityList.add(smsQuality);
                mapQualityLS.put(key, qualityList);
            }
        }
        /**-------------------质量分组结束----------------------------------**/

        /**-------------------延期分组开始----------------------------------**/
        //上个月、待结算延期索赔数据
        List<SmsDelaysDelivery> smsDelaysDeliveryList = smsDelaysDeliveryMapper.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(DeplayStatusEnum.DELAYS_STATUS_11.getCode()));
        //循环根据供应商和付款公司分组
        if (smsDelaysDeliveryList != null) {
            for (SmsDelaysDelivery smsDelivery : smsDelaysDeliveryList) {
                //key:supplierCode+componyCode
                key = smsDelivery.getSupplierCode() + smsDelivery.getComponyCode();
                List<SmsDelaysDelivery> delaysList = mapDelays.getOrDefault(key, new ArrayList<>());
                delaysList.add(smsDelivery);
                mapDelays.put(key, delaysList);
            }
        }
        //历史未兑现、部分兑现数据分组
        List<String> statusDelays = CollUtil.newArrayList(DeplayStatusEnum.DELAYS_STATUS_15.getCode(), DeplayStatusEnum.DELAYS_STATUS_14.getCode());
        List<SmsDelaysDelivery> smsDelaysDeliveryListLS = smsDelaysDeliveryMapper.selectByMonthAndStatus(null, statusDelays);
        //循环根据供应商和付款公司分组
        if (smsDelaysDeliveryListLS != null) {
            for (SmsDelaysDelivery smsDelivery : smsDelaysDeliveryListLS) {
                //key:supplierCode+componyCode
                key = smsDelivery.getSupplierCode() + smsDelivery.getComponyCode();
                List<SmsDelaysDelivery> delaysList = mapDelaysLS.getOrDefault(key, new ArrayList<>());
                delaysList.add(smsDelivery);
                mapDelaysLS.put(key, delaysList);
            }
        }
        /**-------------------延期分组结束----------------------------------**/

        /**-------------------其他分组开始----------------------------------**/
        //上个月、待结算其他索赔数据
        List<SmsClaimOther> smsClaimOtherList = smsClaimOtherMapper.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_11.getCode()));
        //循环根据供应商和付款公司分组
        if (smsClaimOtherList != null) {
            for (SmsClaimOther smsClaimOther : smsClaimOtherList) {
                //key:supplierCode+componyCode
                key = smsClaimOther.getSupplierCode() + smsClaimOther.getComponyCode();
                List<SmsClaimOther> otherList = mapOther.getOrDefault(key, new ArrayList<>());
                otherList.add(smsClaimOther);
                mapOther.put(key, otherList);
            }
        }
        //历史未兑现、部分兑现数据分组
        List<String> statusClaim = CollUtil.newArrayList(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_14.getCode(), ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_15.getCode());
        List<SmsClaimOther> smsClaimOtherListLS = smsClaimOtherMapper.selectByMonthAndStatus(null, statusClaim);
        //循环根据供应商和付款公司分组
        if (smsClaimOtherListLS != null) {
            for (SmsClaimOther smsClaimOther : smsClaimOtherListLS) {
                //key:supplierCode+componyCode
                key = smsClaimOther.getSupplierCode() + smsClaimOther.getComponyCode();
                List<SmsClaimOther> otherList = mapOtherLS.getOrDefault(key, new ArrayList<>());
                otherList.add(smsClaimOther);
                mapOtherLS.put(key, otherList);
            }
        }
        /**-------------------其他分组结束----------------------------------**/


        /**-------------------加工费分组开始----------------------------------**/
        //查找上月、状态是待结算的加工费结算单
        List<SmsSettleInfo> settleInfos = smsSettleInfoService.selectForMonthSettle(lastMonth, SettleInfoOrderStatusEnum.ORDER_STATUS_11.getCode());
        //循环根据供应商和付款公司分组
        if (settleInfos != null) {
            for (SmsSettleInfo smsSettleInfo : settleInfos) {
                //key:supplierCode+componyCode
                key = smsSettleInfo.getSupplierCode() + smsSettleInfo.getComponyCode();
                List<SmsSettleInfo> settleList = mapSettle.getOrDefault(key, new ArrayList<>());
                settleList.add(smsSettleInfo);
                mapSettle.put(key, settleList);
            }
        }
        /**-------------------加工费分组结束----------------------------------**/
        if (MapUtil.isEmpty(mapSettle)) {
            return R.error("没有加工费数据！");
        }

        /**----------------------------------------循环分组后的加工费进行计算了-----------------------------------**/
        mapSettle.forEach((keyCode, settleList) -> {
            log.info(StrUtil.format("(月度结算定时任务){}开始计算了------------------",keyCode));
            //索赔兑换明细list 每次有兑现操作都需要插入一条  循环内批量插入用
            List<SmsClaimCashDetail> claimCashDetailList = new ArrayList<>();
            //结算单号
            String seq = remoteSequeceService.selectSeq("month_settle_seq", 4);
            StringBuffer monthSettleNo = new StringBuffer();
            //YDJS+年月日+4位顺序号
            monthSettleNo.append("YDJS").append(DateUtils.dateTime()).append(seq);
            log.info(StrUtil.format("(月度结算定时任务)结算单号：{}------------------",monthSettleNo.toString()));
            BigDecimal settlePrice = BigDecimal.ZERO;//加工费(分组总额-计算)
            BigDecimal settlePriceTotal = BigDecimal.ZERO;//加工费(分组总额-存储)
            BigDecimal claimPrice = BigDecimal.ZERO;//应扣款(分组总额)
            BigDecimal unCashPrice = BigDecimal.ZERO;//未兑现金额(分组总额)

            //加工费循环相加得到settlePrice
            for (SmsSettleInfo settle : settleList) {
                settle.setSettleNo(monthSettleNo.toString());//结算单号
                settle.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_12.getCode());//修改状态为已结算
                settlePrice = settlePrice.add(settle.getSettlePrice());
            }
            settlePriceTotal = settlePrice;
            log.info(StrUtil.format("(月度结算定时任务)加工费：{}------------------",settlePrice));
            //物耗索赔
            if (MapUtil.isNotEmpty(mapSupplement)&&CollUtil.isNotEmpty(mapSupplement.get(keyCode))) {
                for (SmsSupplementaryOrder supplement : mapSupplement.get(keyCode)) {
                    supplement.setSettleNo(monthSettleNo.toString());//结算单号
                    //更新兑现金额、未兑现金额、状态
                    //加工费累减索赔费用，直到0
                    if (settlePrice.compareTo(supplement.getSettleFee()) >= 0) {
                        supplement.setCashAmount(supplement.getSettleFee());
                        supplement.setUncashAmount(BigDecimal.ZERO);
                        supplement.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_YDX.getCode());
                        settlePrice = settlePrice.subtract(supplement.getSettleFee());
                    } else {
                        if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                            supplement.setCashAmount(BigDecimal.ZERO);
                            supplement.setUncashAmount(supplement.getSettleFee());
                            supplement.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_WDX.getCode());
                        } else {
                            supplement.setCashAmount(settlePrice);
                            supplement.setUncashAmount(supplement.getSettleFee().subtract(settlePrice));
                            supplement.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_BFDX.getCode());
                            settlePrice = BigDecimal.ZERO;
                        }
                    }
                    //应扣款总额累加索赔金额
                    claimPrice = claimPrice.add(supplement.getSettleFee());

                    //插入索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(supplement.getStuffNo()).claimType(SettleRatioEnum.SPLX_WH.getCode())
                            .cashAmount(supplement.getCashAmount()).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            //报废索赔
            if (MapUtil.isNotEmpty(mapScrap)&&CollUtil.isNotEmpty(mapScrap.get(keyCode))) {
                for (SmsScrapOrder smsScrapOrder : mapScrap.get(keyCode)) {
                    smsScrapOrder.setSettleNo(monthSettleNo.toString());//结算单号
                    if (settlePrice.compareTo(smsScrapOrder.getSettleFee()) >= 0) {
                        smsScrapOrder.setCashAmount(smsScrapOrder.getSettleFee());
                        smsScrapOrder.setUncashAmount(BigDecimal.ZERO);
                        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YDX.getCode());
                        settlePrice = settlePrice.subtract(smsScrapOrder.getSettleFee());
                    } else {
                        if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                            smsScrapOrder.setCashAmount(BigDecimal.ZERO);
                            smsScrapOrder.setUncashAmount(smsScrapOrder.getSettleFee());
                            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_WDX.getCode());
                        } else {
                            smsScrapOrder.setCashAmount(settlePrice);
                            smsScrapOrder.setUncashAmount(smsScrapOrder.getSettleFee().subtract(settlePrice));
                            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_BFDX.getCode());
                            settlePrice = BigDecimal.ZERO;
                        }
                    }
                    claimPrice = claimPrice.add(smsScrapOrder.getSettleFee());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsScrapOrder.getScrapNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(smsScrapOrder.getCashAmount()).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            //质量索赔
            if (MapUtil.isNotEmpty(mapQuality)&&CollUtil.isNotEmpty(mapQuality.get(keyCode))) {
                for (SmsQualityOrder smsQualityOrder : mapQuality.get(keyCode)) {
                    smsQualityOrder.setSettleNo(monthSettleNo.toString());//结算单号
                    if (settlePrice.compareTo(smsQualityOrder.getSettleFee()) >= 0) {
                        smsQualityOrder.setCashAmount(smsQualityOrder.getSettleFee());
                        smsQualityOrder.setUncashAmount(BigDecimal.ZERO);
                        smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_13.getCode());
                        settlePrice = settlePrice.subtract(smsQualityOrder.getSettleFee());
                    } else {
                        if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                            smsQualityOrder.setCashAmount(BigDecimal.ZERO);
                            smsQualityOrder.setUncashAmount(smsQualityOrder.getSettleFee());
                            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_15.getCode());
                        } else {
                            smsQualityOrder.setCashAmount(settlePrice);
                            smsQualityOrder.setUncashAmount(smsQualityOrder.getSettleFee().subtract(settlePrice));
                            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_14.getCode());
                            settlePrice = BigDecimal.ZERO;
                        }
                    }
                    claimPrice = claimPrice.add(smsQualityOrder.getSettleFee());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsQualityOrder.getQualityNo()).claimType(SettleRatioEnum.SPLX_ZL.getCode())
                            .cashAmount(smsQualityOrder.getCashAmount()).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            //延期索赔
            if (MapUtil.isNotEmpty(mapDelays)&&CollUtil.isNotEmpty(mapDelays.get(keyCode))) {
                for (SmsDelaysDelivery smsDelaysDelivery : mapDelays.get(keyCode)) {
                    smsDelaysDelivery.setSettleNo(monthSettleNo.toString());//结算单号
                    if (settlePrice.compareTo(smsDelaysDelivery.getSettleFee()) >= 0) {
                        smsDelaysDelivery.setCashAmount(smsDelaysDelivery.getSettleFee());
                        smsDelaysDelivery.setUncashAmount(BigDecimal.ZERO);
                        smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_13.getCode());
                        settlePrice = settlePrice.subtract(smsDelaysDelivery.getSettleFee());
                    } else {
                        if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                            smsDelaysDelivery.setCashAmount(BigDecimal.ZERO);
                            smsDelaysDelivery.setUncashAmount(smsDelaysDelivery.getSettleFee());
                            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_15.getCode());
                        } else {
                            smsDelaysDelivery.setCashAmount(settlePrice);
                            smsDelaysDelivery.setUncashAmount(smsDelaysDelivery.getSettleFee().subtract(settlePrice));
                            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_14.getCode());
                            settlePrice = BigDecimal.ZERO;
                        }
                    }
                    claimPrice = claimPrice.add(smsDelaysDelivery.getSettleFee());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsDelaysDelivery.getDelaysNo()).claimType(SettleRatioEnum.SPLX_YQ.getCode())
                            .cashAmount(smsDelaysDelivery.getCashAmount()).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            //其他索赔
            if (MapUtil.isNotEmpty(mapOther)&&CollUtil.isNotEmpty(mapOther.get(keyCode))) {
                for (SmsClaimOther smsClaimOther : mapOther.get(keyCode)) {
                    smsClaimOther.setSettleNo(monthSettleNo.toString());//结算单号
                    if (settlePrice.compareTo(smsClaimOther.getSettleFee()) >= 0) {
                        smsClaimOther.setCashAmount(smsClaimOther.getSettleFee());
                        smsClaimOther.setUncashAmount(BigDecimal.ZERO);
                        smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_13.getCode());
                        settlePrice = settlePrice.subtract(smsClaimOther.getSettleFee());
                    } else {
                        if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                            smsClaimOther.setCashAmount(BigDecimal.ZERO);
                            smsClaimOther.setUncashAmount(smsClaimOther.getSettleFee());
                            smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_15.getCode());
                        } else {
                            smsClaimOther.setCashAmount(settlePrice);
                            smsClaimOther.setUncashAmount(smsClaimOther.getSettleFee().subtract(settlePrice));
                            smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_14.getCode());
                            settlePrice = BigDecimal.ZERO;
                        }
                    }
                    claimPrice = claimPrice.add(smsClaimOther.getSettleFee());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsClaimOther.getClaimCode()).claimType(SettleRatioEnum.SPLX_YQ.getCode())
                            .cashAmount(smsClaimOther.getCashAmount()).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            /**-------------------------历史开始-------------------------**/
            //物耗索赔历史
            if (MapUtil.isNotEmpty(mapSupplementLS)&&CollUtil.isNotEmpty(mapSupplementLS.get(keyCode))) {
                for (SmsSupplementaryOrder supplement : mapSupplementLS.get(keyCode)) {
                    //如果加工费还剩余，继续扣除历史未兑现和部分兑现的数据
                    if (settlePrice.compareTo(BigDecimal.ZERO) > 0) {
                        break;
                    }
                    supplement.setSettleNo(monthSettleNo.toString());//结算单号
                    BigDecimal cashAmount;//此次兑现金额
                    if (settlePrice.compareTo(supplement.getUncashAmount()) >= 0) {
                        cashAmount = supplement.getUncashAmount();
                        supplement.setCashAmount(supplement.getCashAmount().add(supplement.getUncashAmount()));
                        supplement.setUncashAmount(BigDecimal.ZERO);
                        supplement.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_YDX.getCode());
                        settlePrice = settlePrice.subtract(supplement.getUncashAmount());
                    } else {
                        cashAmount = settlePrice;
                        supplement.setCashAmount(supplement.getCashAmount().add(settlePrice));
                        supplement.setUncashAmount(supplement.getUncashAmount().subtract(settlePrice));
                        supplement.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_BFDX.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                    unCashPrice = unCashPrice.add(supplement.getUncashAmount());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(supplement.getStuffNo()).claimType(SettleRatioEnum.SPLX_WH.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(DateUtil.format(supplement.getSapDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            //报废索赔历史
            if (MapUtil.isNotEmpty(mapScrapLS)&&CollUtil.isNotEmpty(mapScrapLS.get(keyCode))) {
                for (SmsScrapOrder smsScrapOrder : mapScrapLS.get(keyCode)) {
                    if (settlePrice.compareTo(BigDecimal.ZERO) > 0) {
                        break;
                    }
                    smsScrapOrder.setSettleNo(monthSettleNo.toString());//结算单号
                    BigDecimal cashAmount;
                    if (settlePrice.compareTo(smsScrapOrder.getUncashAmount()) >= 0) {
                        cashAmount = smsScrapOrder.getUncashAmount();
                        smsScrapOrder.setCashAmount(smsScrapOrder.getCashAmount().add(smsScrapOrder.getUncashAmount()));
                        smsScrapOrder.setUncashAmount(BigDecimal.ZERO);
                        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YDX.getCode());
                        settlePrice = settlePrice.subtract(smsScrapOrder.getUncashAmount());
                    } else {
                        cashAmount = settlePrice;
                        smsScrapOrder.setCashAmount(smsScrapOrder.getCashAmount().add(settlePrice));
                        smsScrapOrder.setUncashAmount(smsScrapOrder.getUncashAmount().subtract(settlePrice));
                        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_BFDX.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                    unCashPrice = unCashPrice.add(smsScrapOrder.getUncashAmount());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsScrapOrder.getScrapNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(DateUtil.format(smsScrapOrder.getSapTransDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }

            //质量索赔历史
            if (MapUtil.isNotEmpty(mapQualityLS)&&CollUtil.isNotEmpty(mapQualityLS.get(keyCode))) {
                for (SmsQualityOrder smsQualityOrder : mapQualityLS.get(keyCode)) {
                    if (settlePrice.compareTo(BigDecimal.ZERO) > 0) {
                        break;
                    }
                    smsQualityOrder.setSettleNo(monthSettleNo.toString());//结算单号
                    BigDecimal cashAmount;
                    if (settlePrice.compareTo(smsQualityOrder.getUncashAmount()) >= 0) {
                        cashAmount = smsQualityOrder.getUncashAmount();
                        smsQualityOrder.setCashAmount(smsQualityOrder.getCashAmount().add(smsQualityOrder.getUncashAmount()));
                        smsQualityOrder.setUncashAmount(BigDecimal.ZERO);
                        smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_13.getCode());
                        settlePrice = settlePrice.subtract(smsQualityOrder.getUncashAmount());
                    } else {
                        cashAmount = settlePrice;
                        smsQualityOrder.setCashAmount(smsQualityOrder.getCashAmount().add(settlePrice));
                        smsQualityOrder.setUncashAmount(smsQualityOrder.getUncashAmount().subtract(settlePrice));
                        smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_14.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                    unCashPrice = unCashPrice.add(smsQualityOrder.getUncashAmount());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsQualityOrder.getQualityNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(DateUtil.format(smsQualityOrder.getSupplierConfirmDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }


            //延期索赔历史
            if (MapUtil.isNotEmpty(mapDelaysLS)&&CollUtil.isNotEmpty(mapDelaysLS.get(keyCode))) {
                for (SmsDelaysDelivery smsDelaysDelivery : mapDelaysLS.get(keyCode)) {
                    if (settlePrice.compareTo(BigDecimal.ZERO) > 0) {
                        break;
                    }
                    smsDelaysDelivery.setSettleNo(monthSettleNo.toString());//结算单号
                    BigDecimal cashAmount;
                    if (settlePrice.compareTo(smsDelaysDelivery.getUncashAmount()) >= 0) {
                        cashAmount = smsDelaysDelivery.getUncashAmount();
                        smsDelaysDelivery.setCashAmount(smsDelaysDelivery.getCashAmount().add(smsDelaysDelivery.getUncashAmount()));
                        smsDelaysDelivery.setUncashAmount(BigDecimal.ZERO);
                        smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_13.getCode());
                        settlePrice = settlePrice.subtract(smsDelaysDelivery.getUncashAmount());
                    } else {
                        cashAmount = settlePrice;
                        smsDelaysDelivery.setCashAmount(smsDelaysDelivery.getCashAmount().add(settlePrice));
                        smsDelaysDelivery.setUncashAmount(smsDelaysDelivery.getUncashAmount().subtract(settlePrice));
                        smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_14.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                    unCashPrice = unCashPrice.add(smsDelaysDelivery.getUncashAmount());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsDelaysDelivery.getDelaysNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(DateUtil.format(smsDelaysDelivery.getSupplierConfirmDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }


            //其他索赔历史
            if (MapUtil.isNotEmpty(mapOtherLS)&&CollUtil.isNotEmpty(mapOtherLS.get(keyCode))) {
                for (SmsClaimOther smsClaimOther : mapOtherLS.get(keyCode)) {
                    if (settlePrice.compareTo(BigDecimal.ZERO) > 0) {
                        break;
                    }
                    smsClaimOther.setSettleNo(monthSettleNo.toString());//结算单号
                    BigDecimal cashAmount;
                    if (settlePrice.compareTo(smsClaimOther.getUncashAmount()) >= 0) {
                        cashAmount = smsClaimOther.getUncashAmount();
                        smsClaimOther.setCashAmount(smsClaimOther.getCashAmount().add(smsClaimOther.getUncashAmount()));
                        smsClaimOther.setUncashAmount(BigDecimal.ZERO);
                        smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_13.getCode());
                        settlePrice = settlePrice.subtract(smsClaimOther.getUncashAmount());
                    } else {
                        cashAmount = settlePrice;
                        smsClaimOther.setCashAmount(smsClaimOther.getCashAmount().add(settlePrice));
                        smsClaimOther.setUncashAmount(smsClaimOther.getUncashAmount().subtract(settlePrice));
                        smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_14.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                    unCashPrice = unCashPrice.add(smsClaimOther.getUncashAmount());

                    //索赔明细
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsClaimOther.getClaimCode()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo.toString())
                            .shouldCashMounth(DateUtil.format(smsClaimOther.getSupplierConfirmDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(date);
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }


            //如果加工费大于等于（应扣款+历史未兑现）：本月兑现金额=应扣款+历史未兑现 不含税金额=加工费-应扣款-历史未兑现
            //如果加工费小于（应扣款+历史未兑现）：本月兑现金额=加工费 不含税金额=0
            BigDecimal noCashAmount = unCashPrice;//历史未兑现金额
            BigDecimal cashAmount;//本月兑现金额
            BigDecimal excludingFee=settlePriceTotal.subtract(claimPrice).subtract(noCashAmount);//不含税金额
            if (excludingFee.compareTo(BigDecimal.ZERO) > 0) {
                cashAmount = claimPrice.add(noCashAmount);
            } else {
                cashAmount = settlePrice;
                excludingFee = BigDecimal.ZERO;
            }

            //每次循环增加一条月度结算
            SmsMouthSettle smsMouthSettle = new SmsMouthSettle().builder().dataMoth(lastMonth)
                    .supplierCode(settleList.get(0).getSupplierCode()).supplierName(settleList.get(0).getSupplierName())
                    .componyCode(settleList.get(0).getComponyCode()).machiningAmount(settlePriceTotal)
                    .claimAmount(claimPrice).noCashAmount(noCashAmount)
                    .cashAmount(cashAmount).excludingFee(excludingFee)
                    .includeTaxeFee(excludingFee.multiply(BigDecimal.valueOf(1.13))).build();
            smsMouthSettle.setDelFlag("0");
            smsMouthSettle.setCreateBy("定时任务");
            smsMouthSettle.setCreateTime(date);
            smsMouthSettleMapper.insert(smsMouthSettle);

            //更新加工费结算
            smsSettleInfoService.updateBatchByPrimaryKeySelective(settleList);

            //更新索赔五个表及历史
            if (MapUtil.isNotEmpty(mapSupplement) && CollUtil.isNotEmpty(mapSupplement.get(keyCode))) {
                smsSupplementaryOrderService.updateBatchByPrimaryKeySelective(mapSupplement.get(keyCode));
            }
            if (MapUtil.isNotEmpty(mapSupplementLS) && CollUtil.isNotEmpty(mapSupplementLS.get(keyCode))) {
                smsSupplementaryOrderService.updateBatchByPrimaryKeySelective(mapSupplementLS.get(keyCode));
            }

            if (MapUtil.isNotEmpty(mapScrap) && CollUtil.isNotEmpty(mapScrap.get(keyCode))) {
                smsScrapOrderService.updateBatchByPrimaryKeySelective(mapScrap.get(keyCode));
            }
            if (MapUtil.isNotEmpty(mapScrapLS) && CollUtil.isNotEmpty(mapScrapLS.get(keyCode))) {
                smsScrapOrderService.updateBatchByPrimaryKeySelective(mapScrapLS.get(keyCode));
            }

            if (MapUtil.isNotEmpty(mapQuality) && CollUtil.isNotEmpty(mapQuality.get(keyCode))) {
                smsQualityOrderMapper.updateBatchByPrimaryKeySelective(mapQuality.get(keyCode));
            }
            if (MapUtil.isNotEmpty(mapQualityLS) && CollUtil.isNotEmpty(mapQualityLS.get(keyCode))) {
                smsQualityOrderMapper.updateBatchByPrimaryKeySelective(mapQualityLS.get(keyCode));
            }

            if (MapUtil.isNotEmpty(mapOther) && CollUtil.isNotEmpty(mapOther.get(keyCode))) {
                smsClaimOtherMapper.updateBatchByPrimaryKeySelective(mapOther.get(keyCode));
            }
            if (MapUtil.isNotEmpty(mapOtherLS) && CollUtil.isNotEmpty(mapOtherLS.get(keyCode))) {
                smsClaimOtherMapper.updateBatchByPrimaryKeySelective(mapOtherLS.get(keyCode));
            }

            if (MapUtil.isNotEmpty(mapDelays) && CollUtil.isNotEmpty(mapDelays.get(keyCode))) {
                smsDelaysDeliveryMapper.updateBatchByPrimaryKeySelective(mapDelays.get(keyCode));
            }
            if (MapUtil.isNotEmpty(mapDelaysLS) && CollUtil.isNotEmpty(mapDelaysLS.get(keyCode))) {
                smsDelaysDeliveryMapper.updateBatchByPrimaryKeySelective(mapDelaysLS.get(keyCode));
            }

            //增加索赔兑换明细
            if (claimCashDetailList != null && claimCashDetailList.size() > 0) {
                smsClaimCashDetailService.insertList(claimCashDetailList);
            }
        });
        return R.ok();
    }
}
