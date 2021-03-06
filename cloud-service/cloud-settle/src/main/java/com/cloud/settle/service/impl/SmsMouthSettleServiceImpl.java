package com.cloud.settle.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.settle.domain.entity.*;
import com.cloud.settle.domain.webServicePO.BaseClaimDetail;
import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.settle.enums.*;
import com.cloud.settle.feign.RemoteSmsRawScrapOrderService;
import com.cloud.settle.mapper.SmsClaimOtherMapper;
import com.cloud.settle.mapper.SmsDelaysDeliveryMapper;
import com.cloud.settle.mapper.SmsMouthSettleMapper;
import com.cloud.settle.mapper.SmsQualityOrderMapper;
import com.cloud.settle.service.*;
import com.cloud.system.enums.SettleRatioEnum;
import com.cloud.system.feign.RemoteInterfaceLogService;
import com.cloud.system.feign.RemoteSequeceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    private SmsQualityOrderMapper smsQualityOrderMapper;
    @Autowired
    private SmsDelaysDeliveryMapper smsDelaysDeliveryMapper;
    @Autowired
    private SmsClaimOtherMapper smsClaimOtherMapper;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private ISmsClaimCashDetailService smsClaimCashDetailService;
    @Autowired
    private IBaseMutilItemService baseMutilItemService;
    @Autowired
    private ISmsInvoiceInfoService smsInvoiceInfoService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    @Autowired
    private ISmsRawMaterialScrapOrderService smsRawMaterialScrapOrderService;
    @Autowired
    private ISmsQualityScrapOrderService smsQualityScrapOrderService;



    /**
     * 月度结算定时任务   计算上个月
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R countMonthSettle() {
        log.info("----------------------------月度结算定时任务开始------------------------------");
        Date date = DateUtil.date();//当前时间
        String lastMonth = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");//计算月份：上个月

        //将加工费及索赔、历史按照供应商和付款公司分组
        /**-------------------物耗索赔  分组开始----------------------------------**/
        //物耗及历史数据分组map
        Map<String, List<SmsSupplementaryOrder>> mapSupplement = supplementGroup(lastMonth);
        Map<String, List<SmsSupplementaryOrder>> mapSupplementLS = supplementLSGroup();
        /**-------------------物耗索赔  分组结束----------------------------------**/

        /**-------------------报废索赔  分组开始----------------------------------**/
        //报废及历史数据分组map
        Map<String, List<SmsScrapOrder>> mapScrap = scrapGroup(lastMonth);
        Map<String, List<SmsScrapOrder>> mapScrapLS = scrapLSGroup();
        /**-------------------报废索赔  分组结束----------------------------------**/

        /**-------------------质量开始----------------------------------**/
        //质量及历史数据分组map
        Map<String, List<SmsQualityOrder>> mapQuality = qualityGroup(lastMonth);
        Map<String, List<SmsQualityOrder>> mapQualityLS = qualityLSGroup();
        /**-------------------质量结束----------------------------------**/

        /**-------------------延期分组开始----------------------------------**/
        //延期及历史数据分组map
        Map<String, List<SmsDelaysDelivery>> mapDelays = delayGroup(lastMonth);
        Map<String, List<SmsDelaysDelivery>> mapDelaysLS = delayLSGroup();
        /**-------------------延期分组结束----------------------------------**/

        /**-------------------其他分组开始----------------------------------**/
        //其他及历史数据分组map
        Map<String, List<SmsClaimOther>> mapOther = otherGroup(lastMonth);
        Map<String, List<SmsClaimOther>> mapOtherLS = otherLSGroup();
        /**-------------------其他分组结束----------------------------------**/

        /**-------------------原材料报废分组开始----------------------------------**/
        //原材料报废及历史数据分组Map
        Map<String,List<SmsRawMaterialScrapOrder>> mapRawScrap = rawScrapGroup(lastMonth);
        Map<String,List<SmsRawMaterialScrapOrder>> mapRawScrapLS = rawScrapLSGroup();
        /**-------------------原材料报废分组结束----------------------------------**/

        /**-------------------质量部报废分组开始----------------------------------**/
        //质量部报废分组开始分组Map
        Map<String,List<SmsQualityScrapOrder>> mapQualityScrap = qualityScrapGroup(lastMonth);
        Map<String,List<SmsQualityScrapOrder>> mapQualityScrapLS = qualityScrapLSGroup();
        /**-------------------质量部报废分组结束----------------------------------**/

        /**-------------------加工费分组开始----------------------------------**/
        //结算分组map
        Map<String, List<SmsSettleInfo>> mapSettle = settleInfoGroup(lastMonth);
        /**-------------------加工费分组结束----------------------------------**/


        /**----------------------------------------循环分组后的加工费进行计算了-----------------------------------**/
        mapSettle.forEach((keyCode, settleList) -> {
            log.info(StrUtil.format("(月度结算定时任务){}开始计算了------------------", keyCode));
            //索赔兑换明细list 每次有兑现操作都需要插入一条  循环内批量插入用
            List<SmsClaimCashDetail> claimCashDetailList = new ArrayList<>();
            //结算单号
            R seqResult = remoteSequeceService.selectSeq("month_settle_seq", 4);
            if(!seqResult.isSuccess()){
                throw new BusinessException("查序列号不存在");
            }
            String seq = seqResult.getStr("data");
            StringBuffer monthSettleNo = new StringBuffer();
            //YDJS+年月日+4位顺序号
            monthSettleNo.append("YDJS").append(DateUtils.dateTime()).append(seq);
            log.info(StrUtil.format("(月度结算定时任务)结算单号：{}------------------", monthSettleNo.toString()));
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
            log.info(StrUtil.format("(月度结算定时任务)加工费：{}------------------", settlePrice));

            Map<String, Object> map;
            /**-----------------------------本月计算---------------------------**/
            //物耗索赔
            map = supplementCompute(mapSupplement, keyCode, monthSettleNo.toString(), settlePrice, claimPrice, lastMonth, claimCashDetailList);

            //报废索赔
            map = scrapCompute(mapScrap, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("claimPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //质量索赔
            map = qualityCompute(mapQuality, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("claimPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //延期索赔
            map = delayCompute(mapDelays, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("claimPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //其他索赔
            map = otherCompute(mapOther, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("claimPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));
            //原材料报废
            map = rawScrapCompute(mapRawScrap,keyCode,monthSettleNo.toString(),(BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("claimPrice"),lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //质量部报废
            map = qualityScrapCompute(mapQualityScrap,keyCode,monthSettleNo.toString(),(BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("claimPrice"),lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            claimPrice = (BigDecimal) map.get("claimPrice");
            /**-------------------------历史计算-------------------------**/
            //物耗索赔历史
            map = supplementLSCompute(mapSupplementLS, keyCode, monthSettleNo.toString(),
                    (BigDecimal) map.get("settlePrice"), unCashPrice, lastMonth, claimCashDetailList);

            //报废索赔历史
            map = scrapLSCompute(mapScrapLS, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("unCashPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //质量索赔历史
            map = qualityLSCompute(mapQualityLS, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("unCashPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //延期索赔历史
            map = delayLSCompute(mapDelaysLS, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("unCashPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //其他索赔历史
            map = otherLSCompute(mapOtherLS, keyCode, monthSettleNo.toString(), (BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("unCashPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //原材料报废历史
            map = rawScrapLSCompute(mapRawScrapLS,keyCode,monthSettleNo.toString(),(BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("unCashPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            //质量部报废历史
            map = qualityScrapLSCompute(mapQualityScrapLS,keyCode,monthSettleNo.toString(),(BigDecimal) map.get("settlePrice"),
                    (BigDecimal) map.get("unCashPrice"), lastMonth, (List<SmsClaimCashDetail>) map.get("claimCashDetailList"));

            settlePrice = (BigDecimal) map.get("settlePrice");
            unCashPrice = (BigDecimal) map.get("unCashPrice");


            //如果加工费大于等于（应扣款+历史未兑现）：本月兑现金额=应扣款+历史未兑现 不含税金额=加工费-应扣款-历史未兑现
            //如果加工费小于（应扣款+历史未兑现）：本月兑现金额=加工费 不含税金额=0
            BigDecimal noCashAmount = unCashPrice;//历史未兑现金额
            BigDecimal cashAmount = settlePriceTotal.subtract(settlePrice);//本月兑现金额
            BigDecimal excludingFee = settlePrice;//不含税金额

            //每次循环增加一条月度结算
            SmsMouthSettle smsMouthSettle = new SmsMouthSettle().builder().settleNo(monthSettleNo.toString()).dataMoth(lastMonth)
                    .supplierCode(settleList.get(0).getSupplierCode()).supplierName(settleList.get(0).getSupplierName())
                    .companyCode(settleList.get(0).getCompanyCode()).machiningAmount(settlePriceTotal)
                    .claimAmount(claimPrice).noCashAmount(noCashAmount)
                    .cashAmount(cashAmount).excludingFee(excludingFee)
                    .includeTaxeFee(excludingFee.multiply(BigDecimal.valueOf(1.13)))
                    .settleStatus(MonthSettleStatusEnum.YD_SETTLE_STATUS_NKDQR.getCode()).build();
            smsMouthSettle.setDelFlag("0");
            smsMouthSettle.setCreateBy("定时任务");
            smsMouthSettle.setCreateTime(date);
            smsMouthSettleMapper.insert(smsMouthSettle);

            //更新加工费结算
            smsSettleInfoService.updateBatchByPrimaryKeySelective(settleList);

            //增加索赔兑换明细
            if (map.get("claimCashDetailList") != null) {
                claimCashDetailList = (List<SmsClaimCashDetail>) map.get("claimCashDetailList");
                if (claimCashDetailList.size() > 0) {
                    smsClaimCashDetailService.insertList(claimCashDetailList);
                }
            }
        });
        //更新状态还是11待结算的索赔数据为15未兑现
        smsMouthSettleMapper.updateMouthSettleToUpdateStatus15(lastMonth);
        return R.ok();
    }



    /**
     * 加工费分组
     *
     * @param lastMonth
     * @return
     */
    Map<String, List<SmsSettleInfo>> settleInfoGroup(String lastMonth) {
        String key;
        Map<String, List<SmsSettleInfo>> mapSettle = new ConcurrentHashMap<>();
        //查找上月、状态是待结算的加工费结算单
        List<SmsSettleInfo> settleInfos = smsSettleInfoService.selectForMonthSettle(lastMonth, SettleInfoOrderStatusEnum.ORDER_STATUS_11.getCode());
        //循环根据供应商和付款公司分组
        if (settleInfos != null) {
            for (SmsSettleInfo smsSettleInfo : settleInfos) {
                //key:supplierCode+companyCode
                key = smsSettleInfo.getSupplierCode() + smsSettleInfo.getCompanyCode();
                List<SmsSettleInfo> settleList = mapSettle.getOrDefault(key, new ArrayList<>());
                settleList.add(smsSettleInfo);
                mapSettle.put(key, settleList);
            }
        }
        if (MapUtil.isEmpty(mapSettle)) {
            throw new BusinessException("没有加工费数据！");
        }
        return mapSettle;
    }

    /**
     * 物耗分组
     * @param lastMonth
     * @return
     */
    Map<String, List<SmsSupplementaryOrder>> supplementGroup(String lastMonth) {
        String key;
        Map<String, List<SmsSupplementaryOrder>> mapSupplement = new ConcurrentHashMap<>();
        //取得计算月份、待结算的物耗申请数据
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = smsSupplementaryOrderService.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode()));
        if (smsSupplementaryOrderList != null) {
            for (SmsSupplementaryOrder smsSupplementaryOrder : smsSupplementaryOrderList) {
                //下面开始分组：根据供应商和付款公司分组
                //key:supplierCode+companyCode
                key = smsSupplementaryOrder.getSupplierCode() + smsSupplementaryOrder.getCompanyCode();
                List<SmsSupplementaryOrder> supplementList = mapSupplement.getOrDefault(key, new ArrayList<>());
                supplementList.add(smsSupplementaryOrder);
                mapSupplement.put(key, supplementList);
            }
        }
        return mapSupplement;
    }

    /**
     * 物耗历史分组
     *
     * @return
     */
    Map<String, List<SmsSupplementaryOrder>> supplementLSGroup() {
        String key;
        Map<String, List<SmsSupplementaryOrder>> mapSupplementLS = new ConcurrentHashMap<>();
        //下面对物耗申请历史未兑现完成的数据进行分组
        //取得计算月份、未兑现、部分兑现的物耗数据
        List<String> statusSup = CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_WDX.getCode(), SupplementaryOrderStatusEnum.WH_ORDER_STATUS_BFDX.getCode());
        List<SmsSupplementaryOrder> smsSupplementaryOrderListLS = smsSupplementaryOrderService.selectByMonthAndStatus(null, statusSup);
        //循环物耗，计算索赔金额
        if (smsSupplementaryOrderListLS != null) {
            for (SmsSupplementaryOrder smsSupplementaryOrder : smsSupplementaryOrderListLS) {
                //根据供应商和付款公司分组
                //key:supplierCode+companyCode
                key = smsSupplementaryOrder.getSupplierCode() + smsSupplementaryOrder.getCompanyCode();
                List<SmsSupplementaryOrder> supplementList = mapSupplementLS.getOrDefault(key, new ArrayList<>());
                supplementList.add(smsSupplementaryOrder);
                mapSupplementLS.put(key, supplementList);
            }
        }
        return mapSupplementLS;
    }

    /**
     * 物耗计算
     *
     * @param mapSupplement
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> supplementCompute(Map<String, List<SmsSupplementaryOrder>> mapSupplement, String keyCode,
                                          String monthSettleNo, BigDecimal settlePrice, BigDecimal claimPrice,
                                          String lastMonth, List<SmsClaimCashDetail> claimCashDetailList) {
        //物耗索赔
        if (MapUtil.isNotEmpty(mapSupplement) && CollUtil.isNotEmpty(mapSupplement.get(keyCode))) {
            for (SmsSupplementaryOrder supplement : mapSupplement.get(keyCode)) {
                supplement.setSettleNo(monthSettleNo);//结算单号
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
                        .cashAmount(supplement.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                        .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                smsClaimCashDetail.setCreateTime(DateUtil.date());
                claimCashDetailList.add(smsClaimCashDetail);
            }
            smsSupplementaryOrderService.updateBatchByPrimaryKeySelective(mapSupplement.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 物耗历史计算
     *
     * @param mapSupplementLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> supplementLSCompute(Map<String, List<SmsSupplementaryOrder>> mapSupplementLS, String keyCode,
                                            String monthSettleNo, BigDecimal settlePrice, BigDecimal unCashPrice,
                                            String lastMonth, List<SmsClaimCashDetail> claimCashDetailList) {
        //物耗索赔历史
        if (MapUtil.isNotEmpty(mapSupplementLS) && CollUtil.isNotEmpty(mapSupplementLS.get(keyCode))) {
            for (SmsSupplementaryOrder supplement : mapSupplementLS.get(keyCode)) {
                //如果加工费还剩余，继续扣除历史未兑现和部分兑现的数据
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
//                supplement.setSettleNo(monthSettleNo);//结算单号
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
                        .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                        .shouldCashMounth(DateUtil.format(supplement.getSapDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                smsClaimCashDetail.setCreateTime(DateUtil.date());
                claimCashDetailList.add(smsClaimCashDetail);
            }
            smsSupplementaryOrderService.updateBatchByPrimaryKeySelective(mapSupplementLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 报废分组
     *
     * @param lastMonth
     * @return
     */
    Map<String, List<SmsScrapOrder>> scrapGroup(String lastMonth) {
        String key;
        Map<String, List<SmsScrapOrder>> mapScrap = new ConcurrentHashMap<>();
        //取得计算月份、待结算的报废数据
        List<SmsScrapOrder> smsScrapOrderList = smsScrapOrderService.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode()));
        //循环报废，计算索赔金额
        if (smsScrapOrderList != null) {
            for (SmsScrapOrder smsScrapOrder : smsScrapOrderList) {
                //根据供应商和付款公司分组
                //key:supplierCode+companyCode
                key = smsScrapOrder.getSupplierCode() + smsScrapOrder.getCompanyCode();
                List<SmsScrapOrder> scrapOrderList = mapScrap.getOrDefault(key, new ArrayList<>());
                scrapOrderList.add(smsScrapOrder);
                mapScrap.put(key, scrapOrderList);
            }
        }
        return mapScrap;
    }

    /**
     * 报废历史分组
     *
     * @return
     */
    Map<String, List<SmsScrapOrder>> scrapLSGroup() {
        String key;
        Map<String, List<SmsScrapOrder>> mapScrapLS = new ConcurrentHashMap<>();
        //历史数据分组
        //取得计算月份、未兑现、部分兑现的报废数据
        List<String> statusScrap = CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_WDX.getCode(), ScrapOrderStatusEnum.BF_ORDER_STATUS_BFDX.getCode());
        List<SmsScrapOrder> smsScrapOrderListLS = smsScrapOrderService.selectByMonthAndStatus(null, statusScrap);
        //循环报废，计算索赔金额
        if (smsScrapOrderListLS != null) {
            for (SmsScrapOrder smsScrapOrder : smsScrapOrderListLS) {
                //根据供应商和付款公司分组
                //key:supplierCode+companyCode
                key = smsScrapOrder.getSupplierCode() + smsScrapOrder.getCompanyCode();
                List<SmsScrapOrder> scrapOrderList = mapScrapLS.getOrDefault(key, new ArrayList<>());
                scrapOrderList.add(smsScrapOrder);
                mapScrapLS.put(key, scrapOrderList);
            }
        }
        return mapScrapLS;
    }

    /**
     * 报废计算
     *
     * @param mapScrap
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> scrapCompute(Map<String, List<SmsScrapOrder>> mapScrap, String keyCode, String monthSettleNo,
                                     BigDecimal settlePrice, BigDecimal claimPrice, String lastMonth,
                                     List<SmsClaimCashDetail> claimCashDetailList) {
        //报废索赔
        if (MapUtil.isNotEmpty(mapScrap) && CollUtil.isNotEmpty(mapScrap.get(keyCode))) {
            for (SmsScrapOrder smsScrapOrder : mapScrap.get(keyCode)) {
                smsScrapOrder.setSettleNo(monthSettleNo);//结算单号
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
                if (smsScrapOrder.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsScrapOrder.getScrapNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(smsScrapOrder.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsScrapOrderService.updateBatchByPrimaryKeySelective(mapScrap.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 报废历史计算
     *
     * @param mapScrapLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> scrapLSCompute(Map<String, List<SmsScrapOrder>> mapScrapLS, String keyCode, String monthSettleNo,
                                       BigDecimal settlePrice, BigDecimal unCashPrice, String lastMonth,
                                       List<SmsClaimCashDetail> claimCashDetailList) {
        //报废索赔历史
        if (MapUtil.isNotEmpty(mapScrapLS) && CollUtil.isNotEmpty(mapScrapLS.get(keyCode))) {
            for (SmsScrapOrder smsScrapOrder : mapScrapLS.get(keyCode)) {
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
//                smsScrapOrder.setSettleNo(monthSettleNo);//结算单号
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
                if (cashAmount.compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsScrapOrder.getScrapNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(DateUtil.format(smsScrapOrder.getSapTransDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsScrapOrderService.updateBatchByPrimaryKeySelective(mapScrapLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 质量索赔分组
     *
     * @param lastMonth
     * @return
     */
    Map<String, List<SmsQualityOrder>> qualityGroup(String lastMonth) {
        String key;
        Map<String, List<SmsQualityOrder>> mapQuality = new ConcurrentHashMap<>();
        //上个月、待结算质量索赔数据
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderMapper.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(QualityStatusEnum.QUALITY_STATUS_11.getCode()));
        //循环根据供应商和付款公司分组
        if (smsQualityOrderList != null) {
            for (SmsQualityOrder smsQuality : smsQualityOrderList) {
                //key:supplierCode+companyCode
                key = smsQuality.getSupplierCode() + smsQuality.getCompanyCode();
                List<SmsQualityOrder> qualityList = mapQuality.getOrDefault(key, new ArrayList<>());
                qualityList.add(smsQuality);
                mapQuality.put(key, qualityList);
            }
        }
        return mapQuality;
    }

    /**
     * 质量索赔历史分组
     *
     * @return
     */
    Map<String, List<SmsQualityOrder>> qualityLSGroup() {
        String key;
        Map<String, List<SmsQualityOrder>> mapQualityLS = new ConcurrentHashMap<>();
        //历史未兑现、部分兑现数据分组
        List<String> statusQuality = CollUtil.newArrayList(QualityStatusEnum.QUALITY_STATUS_15.getCode(), QualityStatusEnum.QUALITY_STATUS_14.getCode());
        List<SmsQualityOrder> smsQualityOrderListLS = smsQualityOrderMapper.selectByMonthAndStatus(null, statusQuality);
        //循环根据供应商和付款公司分组
        if (smsQualityOrderListLS != null) {
            for (SmsQualityOrder smsQuality : smsQualityOrderListLS) {
                //key:supplierCode+companyCode
                key = smsQuality.getSupplierCode() + smsQuality.getCompanyCode();
                List<SmsQualityOrder> qualityList = mapQualityLS.getOrDefault(key, new ArrayList<>());
                qualityList.add(smsQuality);
                mapQualityLS.put(key, qualityList);
            }
        }
        return mapQualityLS;
    }

    /**
     * 质量索赔计算
     *
     * @param mapQuality
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> qualityCompute(Map<String, List<SmsQualityOrder>> mapQuality, String keyCode,
                                       String monthSettleNo, BigDecimal settlePrice,
                                       BigDecimal claimPrice, String lastMonth,
                                       List<SmsClaimCashDetail> claimCashDetailList) {
        //质量索赔
        if (MapUtil.isNotEmpty(mapQuality) && CollUtil.isNotEmpty(mapQuality.get(keyCode))) {
            for (SmsQualityOrder smsQualityOrder : mapQuality.get(keyCode)) {
                smsQualityOrder.setSettleNo(monthSettleNo);//结算单号
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
                if (smsQualityOrder.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsQualityOrder.getQualityNo()).claimType(SettleRatioEnum.SPLX_ZL.getCode())
                            .cashAmount(smsQualityOrder.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsQualityOrderMapper.updateBatchByPrimaryKeySelective(mapQuality.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 质量索赔历史计算
     *
     * @param mapQualityLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> qualityLSCompute(Map<String, List<SmsQualityOrder>> mapQualityLS, String keyCode,
                                         String monthSettleNo, BigDecimal settlePrice,
                                         BigDecimal unCashPrice, String lastMonth,
                                         List<SmsClaimCashDetail> claimCashDetailList) {
        //质量索赔历史
        if (MapUtil.isNotEmpty(mapQualityLS) && CollUtil.isNotEmpty(mapQualityLS.get(keyCode))) {
            for (SmsQualityOrder smsQualityOrder : mapQualityLS.get(keyCode)) {
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
//                smsQualityOrder.setSettleNo(monthSettleNo);//结算单号
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
                if (cashAmount.compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsQualityOrder.getQualityNo()).claimType(SettleRatioEnum.SPLX_ZL.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(DateUtil.format(smsQualityOrder.getSupplierConfirmDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }

            }
            smsQualityOrderMapper.updateBatchByPrimaryKeySelective(mapQualityLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 延期索赔分组
     *
     * @param lastMonth
     * @return
     */
    Map<String, List<SmsDelaysDelivery>> delayGroup(String lastMonth) {
        String key;
        Map<String, List<SmsDelaysDelivery>> mapDelays = new ConcurrentHashMap<>();
        //上个月、待结算延期索赔数据
        List<SmsDelaysDelivery> smsDelaysDeliveryList = smsDelaysDeliveryMapper.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(DeplayStatusEnum.DELAYS_STATUS_11.getCode()));
        //循环根据供应商和付款公司分组
        if (smsDelaysDeliveryList != null) {
            for (SmsDelaysDelivery smsDelivery : smsDelaysDeliveryList) {
                //key:supplierCode+companyCode
                key = smsDelivery.getSupplierCode() + smsDelivery.getCompanyCode();
                List<SmsDelaysDelivery> delaysList = mapDelays.getOrDefault(key, new ArrayList<>());
                delaysList.add(smsDelivery);
                mapDelays.put(key, delaysList);
            }
        }
        return mapDelays;
    }

    /**
     * 延期索赔历史分组
     *
     * @return
     */
    Map<String, List<SmsDelaysDelivery>> delayLSGroup() {
        String key;
        Map<String, List<SmsDelaysDelivery>> mapDelaysLS = new ConcurrentHashMap<>();
        //历史未兑现、部分兑现数据分组
        List<String> statusDelays = CollUtil.newArrayList(DeplayStatusEnum.DELAYS_STATUS_15.getCode(), DeplayStatusEnum.DELAYS_STATUS_14.getCode());
        List<SmsDelaysDelivery> smsDelaysDeliveryListLS = smsDelaysDeliveryMapper.selectByMonthAndStatus(null, statusDelays);
        //循环根据供应商和付款公司分组
        if (smsDelaysDeliveryListLS != null) {
            for (SmsDelaysDelivery smsDelivery : smsDelaysDeliveryListLS) {
                //key:supplierCode+companyCode
                key = smsDelivery.getSupplierCode() + smsDelivery.getCompanyCode();
                List<SmsDelaysDelivery> delaysList = mapDelaysLS.getOrDefault(key, new ArrayList<>());
                delaysList.add(smsDelivery);
                mapDelaysLS.put(key, delaysList);
            }
        }
        return mapDelaysLS;
    }

    /**
     * 延期索赔计算
     *
     * @param mapDelays
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> delayCompute(Map<String, List<SmsDelaysDelivery>> mapDelays, String keyCode,
                                     String monthSettleNo, BigDecimal settlePrice,
                                     BigDecimal claimPrice, String lastMonth,
                                     List<SmsClaimCashDetail> claimCashDetailList) {
        //延期索赔
        if (MapUtil.isNotEmpty(mapDelays) && CollUtil.isNotEmpty(mapDelays.get(keyCode))) {
            for (SmsDelaysDelivery smsDelaysDelivery : mapDelays.get(keyCode)) {
                smsDelaysDelivery.setSettleNo(monthSettleNo);//结算单号
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
                if (smsDelaysDelivery.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsDelaysDelivery.getDelaysNo()).claimType(SettleRatioEnum.SPLX_YQ.getCode())
                            .cashAmount(smsDelaysDelivery.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsDelaysDeliveryMapper.updateBatchByPrimaryKeySelective(mapDelays.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }


    /**
     * 延期索赔历史计算
     *
     * @param mapDelaysLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> delayLSCompute(Map<String, List<SmsDelaysDelivery>> mapDelaysLS, String keyCode,
                                       String monthSettleNo, BigDecimal settlePrice,
                                       BigDecimal unCashPrice, String lastMonth,
                                       List<SmsClaimCashDetail> claimCashDetailList) {
        //延期索赔历史
        if (MapUtil.isNotEmpty(mapDelaysLS) && CollUtil.isNotEmpty(mapDelaysLS.get(keyCode))) {
            for (SmsDelaysDelivery smsDelaysDelivery : mapDelaysLS.get(keyCode)) {
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
//                smsDelaysDelivery.setSettleNo(monthSettleNo);//结算单号
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
                if (cashAmount.compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsDelaysDelivery.getDelaysNo()).claimType(SettleRatioEnum.SPLX_YQ.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(DateUtil.format(smsDelaysDelivery.getSupplierConfirmDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsDelaysDeliveryMapper.updateBatchByPrimaryKeySelective(mapDelaysLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 其他索赔分组
     *
     * @param lastMonth
     * @return
     */
    Map<String, List<SmsClaimOther>> otherGroup(String lastMonth) {
        String key;
        Map<String, List<SmsClaimOther>> mapOther = new ConcurrentHashMap<>();
        //上个月、待结算其他索赔数据
        List<SmsClaimOther> smsClaimOtherList = smsClaimOtherMapper.selectByMonthAndStatus(lastMonth, CollUtil.newArrayList(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_11.getCode()));
        //循环根据供应商和付款公司分组
        if (smsClaimOtherList != null) {
            for (SmsClaimOther smsClaimOther : smsClaimOtherList) {
                //key:supplierCode+companyCode
                key = smsClaimOther.getSupplierCode() + smsClaimOther.getCompanyCode();
                List<SmsClaimOther> otherList = mapOther.getOrDefault(key, new ArrayList<>());
                otherList.add(smsClaimOther);
                mapOther.put(key, otherList);
            }
        }
        return mapOther;
    }

    /**
     * 其他索赔历史分组
     *
     * @return
     */
    Map<String, List<SmsClaimOther>> otherLSGroup() {
        String key;
        Map<String, List<SmsClaimOther>> mapOtherLS = new ConcurrentHashMap<>();
        //历史未兑现、部分兑现数据分组
        List<String> statusClaim = CollUtil.newArrayList(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_14.getCode(), ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_15.getCode());
        List<SmsClaimOther> smsClaimOtherListLS = smsClaimOtherMapper.selectByMonthAndStatus(null, statusClaim);
        //循环根据供应商和付款公司分组
        if (smsClaimOtherListLS != null) {
            for (SmsClaimOther smsClaimOther : smsClaimOtherListLS) {
                //key:supplierCode+companyCode
                key = smsClaimOther.getSupplierCode() + smsClaimOther.getCompanyCode();
                List<SmsClaimOther> otherList = mapOtherLS.getOrDefault(key, new ArrayList<>());
                otherList.add(smsClaimOther);
                mapOtherLS.put(key, otherList);
            }
        }
        return mapOtherLS;
    }

    /**
     * 其他索赔计算
     *
     * @param mapOther
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> otherCompute(Map<String, List<SmsClaimOther>> mapOther, String keyCode,
                                     String monthSettleNo, BigDecimal settlePrice,
                                     BigDecimal claimPrice, String lastMonth,
                                     List<SmsClaimCashDetail> claimCashDetailList) {
        //其他索赔
        if (MapUtil.isNotEmpty(mapOther) && CollUtil.isNotEmpty(mapOther.get(keyCode))) {
            for (SmsClaimOther smsClaimOther : mapOther.get(keyCode)) {
                smsClaimOther.setSettleNo(monthSettleNo);//结算单号
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
                if (smsClaimOther.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsClaimOther.getClaimCode()).claimType(SettleRatioEnum.SPLX_QT.getCode())
                            .cashAmount(smsClaimOther.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsClaimOtherMapper.updateBatchByPrimaryKeySelective(mapOther.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 其他历史计算
     *
     * @param mapOtherLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> otherLSCompute(Map<String, List<SmsClaimOther>> mapOtherLS, String keyCode,
                                       String monthSettleNo, BigDecimal settlePrice,
                                       BigDecimal unCashPrice, String lastMonth,
                                       List<SmsClaimCashDetail> claimCashDetailList) {
        //其他索赔历史
        if (MapUtil.isNotEmpty(mapOtherLS) && CollUtil.isNotEmpty(mapOtherLS.get(keyCode))) {
            for (SmsClaimOther smsClaimOther : mapOtherLS.get(keyCode)) {
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
//                smsClaimOther.setSettleNo(monthSettleNo);//结算单号
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
                if (cashAmount.compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = new SmsClaimCashDetail().builder()
                            .claimNo(smsClaimOther.getClaimCode()).claimType(SettleRatioEnum.SPLX_QT.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(DateUtil.format(smsClaimOther.getSupplierConfirmDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsClaimOtherMapper.updateBatchByPrimaryKeySelective(mapOtherLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 内控确认和小微主确认
     * @param id
     * @param settleStatus
     * @return
     */
    @Override
    public R confirm(Long id, String settleStatus) {
        if (id == null || StrUtil.isBlank(settleStatus)) {
            return R.error("缺少参数！");
        }
        SmsMouthSettle smsMouthSettle=selectByPrimaryKey(id);
        if(smsMouthSettle==null){
            return R.error("数据不存在！");
        }
        if (!settleStatus.equals(smsMouthSettle.getSettleStatus())) {
            return R.error("数据状态不允许此操作！");
        }
        if(MonthSettleStatusEnum.YD_SETTLE_STATUS_NKDQR.getCode().equals(settleStatus)){
            //内控确认
//            BigDecimal includeTaxeFee = smsMouthSettle.getIncludeTaxeFee();
//            BigDecimal invoiceFee = smsMouthSettle.getInvoiceFee();
//            if ((includeTaxeFee.subtract(invoiceFee).abs()).compareTo(BigDecimal.ONE)>0) {
//                return R.error("含税金额与发票金额不等，不允许提交！");
//            }
            smsMouthSettle.setSettleStatus(MonthSettleStatusEnum.YD_SETTLE_STATUS_XWZDQR.getCode());
            updateByPrimaryKeySelective(smsMouthSettle);
        }else if(MonthSettleStatusEnum.YD_SETTLE_STATUS_XWZDQR.getCode().equals(settleStatus)){
            //小微主确认
            smsMouthSettle.setSettleStatus(MonthSettleStatusEnum.YD_SETTLE_STATUS_DFPLR.getCode());
            updateByPrimaryKeySelective(smsMouthSettle);
            //传KMS
//            createMultiItemClaim(smsMouthSettle);

        }else{
            return R.error("状态错误！");
        }
        return R.ok();

    }

    /**
     * 打印结算
     * @param settleNo
     * @return
     */
    @Override
    public R settlePrint(String settleNo) {
        //加工承揽加工费
        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("settleNo", settleNo);
        SmsMouthSettle smsMouthSettle = findByExampleOne(example);
        if (smsMouthSettle==null) {
            return R.error("月度结算数据为空！");
        }
        BigDecimal supplementMoney;//物耗金额
        BigDecimal scrapMoney;//报废金额
        BigDecimal qualityMoney;//质量索赔金额
        BigDecimal otherMoney;//其他索赔金额
        BigDecimal delayMoney;//延期索赔金额

        //兑换明细
        Example exampleDetail = new Example(SmsClaimCashDetail.class);
        Example.Criteria criteriaDetail = exampleDetail.createCriteria();
        criteriaDetail.andEqualTo("settleNo",settleNo);
        List<SmsClaimCashDetail> details = smsClaimCashDetailService.selectByExample(exampleDetail);
        if (CollUtil.isEmpty(details)) {
            supplementMoney = BigDecimal.ZERO;
            scrapMoney = BigDecimal.ZERO;
            qualityMoney = BigDecimal.ZERO;
            otherMoney = BigDecimal.ZERO;
            delayMoney = BigDecimal.ZERO;
        }else{
            supplementMoney=details.stream()
                    .filter(d -> StrUtil.equals(SettleRatioEnum.SPLX_WH.getCode(),d.getClaimType()))
                    .map(SmsClaimCashDetail::getCashAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            scrapMoney=details.stream()
                    .filter(d -> StrUtil.equals(SettleRatioEnum.SPLX_BF.getCode(),d.getClaimType()))
                    .map(SmsClaimCashDetail::getCashAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            qualityMoney=details.stream()
                    .filter(d -> StrUtil.equals(SettleRatioEnum.SPLX_ZL.getCode(),d.getClaimType()))
                    .map(SmsClaimCashDetail::getCashAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            delayMoney=details.stream()
                    .filter(d -> StrUtil.equals(SettleRatioEnum.SPLX_YQ.getCode(),d.getClaimType()))
                    .map(SmsClaimCashDetail::getCashAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            otherMoney=details.stream()
                    .filter(d -> StrUtil.equals(SettleRatioEnum.SPLX_QT.getCode(),d.getClaimType()))
                    .map(SmsClaimCashDetail::getCashAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        //结算加工费
        BigDecimal machiningAmount = smsMouthSettle.getMachiningAmount();
        //汇总金额（不含税）
        BigDecimal excludingFee = smsMouthSettle.getExcludingFee();
        //税率	 0.13
        //汇总金额（含税）
        BigDecimal includeTaxeFee = smsMouthSettle.getIncludeTaxeFee();
        Map<String, BigDecimal> map = MapUtil.newConcurrentHashMap();
        map.put("machiningAmount",machiningAmount);
        map.put("supplementMoney",supplementMoney);
        map.put("scrapMoney",scrapMoney);
        map.put("qualityMoney",qualityMoney);
        map.put("delayMoney",delayMoney);
        map.put("otherMoney",otherMoney);
        map.put("excludingFee",excludingFee);
        map.put("rate",new BigDecimal(0.13));
        map.put("includeTaxeFee",includeTaxeFee);
        return R.data(map);
    }

    /**
     * 打印索赔单
     * @param settleNo
     * @return
     */
    @Override
    public R spPrint(String settleNo) {
        //物耗单
        Example exampleWH = new Example(SmsSupplementaryOrder.class);
        Example.Criteria criteriaWH = exampleWH.createCriteria();
        criteriaWH.andEqualTo("settleNo", settleNo);
        List<SmsSupplementaryOrder> listWH = smsSupplementaryOrderService.selectByExample(exampleWH);

        //报废单
        Example exampleBF = new Example(SmsScrapOrder.class);
        Example.Criteria criteriaBF = exampleBF.createCriteria();
        criteriaBF.andEqualTo("settleNo", settleNo);
        List<SmsScrapOrder> listBF = smsScrapOrderService.selectByExample(exampleWH);

        //质量索赔单
        Example exampleZL = new Example(SmsQualityOrder.class);
        Example.Criteria criteriaZL = exampleZL.createCriteria();
        criteriaZL.andEqualTo("settleNo", settleNo);
        List<SmsQualityOrder> listZL = smsQualityOrderMapper.selectByExample(exampleWH);

        //延期索赔单
        Example exampleYQ = new Example(SmsDelaysDelivery.class);
        Example.Criteria criteriaYQ = exampleYQ.createCriteria();
        criteriaYQ.andEqualTo("settleNo", settleNo);
        List<SmsDelaysDelivery> listYQ = smsDelaysDeliveryMapper.selectByExample(exampleWH);

        //其他索赔单
        Example exampleQT = new Example(SmsClaimOther.class);
        Example.Criteria criteriaQT = exampleQT.createCriteria();
        criteriaQT.andEqualTo("settleNo", settleNo);
        List<SmsClaimOther> listQT = smsClaimOtherMapper.selectByExample(exampleWH);
        Map<String, Object> map = MapUtil.newConcurrentHashMap();
        map.put("listWH",listWH);
        map.put("listBF",listBF);
        map.put("listZL",listZL);
        map.put("listYQ",listYQ);
        map.put("listQT",listQT);
        return R.data(map);
    }


    /**
     * 创建报账单并修改月度结算状态回填kems单号
     * @param smsMouthSettle
     * @return
     * @throws Exception
     */
    @Override
    public void createMultiItemClaim(SmsMouthSettle smsMouthSettle){
        //1.创建报账单
        BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest = getBaseMultiItemClaimSaveRequest(smsMouthSettle);
        BaseClaimResponse baseClaimResponse;
        try{
            baseClaimResponse = baseMutilItemService.createMultiItemClaim(baseMultiItemClaimSaveRequest);
            if(null == baseClaimResponse){
                log.error("调用创建报账单接口异常 req:{},res:{}", JSONObject.toJSONString(baseMultiItemClaimSaveRequest),
                        JSONObject.toJSON(baseClaimResponse));
                throw new BusinessException("调用创建报账单接口异常");
            }
            if(BaseMultiItemClaimStatusEnum.FAIL.getCode().equals(baseClaimResponse.getSuccessFlag())){
                log.error("调用创建报账单接口失败req:{},res:{}", JSONObject.toJSONString(baseMultiItemClaimSaveRequest),
                        JSONObject.toJSON(baseClaimResponse));
                String failReason = baseClaimResponse.getFailReason();
                throw new BusinessException("调用创建报账单接口失败" + failReason);
            }
        }catch (Exception e){
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.error(
                    "调用创建报账单接口失败 : {}", w.toString());
            throw new BusinessException(e.getMessage());
        }
        //2.创建报账单成功后修改月度结算状态回填kems单号
        SmsMouthSettle smsMouthSettleReq = new SmsMouthSettle();
        smsMouthSettleReq.setId(smsMouthSettle.getId());
        smsMouthSettleReq.setSettleStatus(MonthSettleStatusEnum.YD_SETTLE_STATUS_DFK.getCode());
        smsMouthSettleReq.setKmsNo(baseClaimResponse.getGemsDocNo());
        smsMouthSettleMapper.updateByPrimaryKeySelective(smsMouthSettleReq);
    }

    /**
     * 组装报账单参数
     * @param smsMouthSettle
     * @return
     * @throws Exception
     */
    private BaseMultiItemClaimSaveRequest getBaseMultiItemClaimSaveRequest(SmsMouthSettle smsMouthSettle){
        String settleNo = smsMouthSettle.getSettleNo();
        BaseMultiItemClaimSaveRequest baseMultiItemClaimSaveRequest = new BaseMultiItemClaimSaveRequest();
        baseMultiItemClaimSaveRequest.setCompanyCode(smsMouthSettle.getCompanyCode());
        baseMultiItemClaimSaveRequest.setVendorCode(smsMouthSettle.getSupplierCode());
        baseMultiItemClaimSaveRequest.setOriginDocNo(smsMouthSettle.getSettleNo());
        //根据结算单号查发票信息
        Example example = new Example(SmsInvoiceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mouthSettleId",settleNo);
        List<SmsInvoiceInfo> smsInvoiceInfoList = smsInvoiceInfoService.selectByExample(example);
        if(CollectionUtils.isEmpty(smsInvoiceInfoList)){
            log.error("生成报账单时根据根据结算单号查发票信息不存在 mouthSettleId:{}",settleNo);
            throw new BusinessException("根据结算单号查发票信息不存在");
        }
        //转换对象
        List<BaseClaimDetail> claimDetailList = new ArrayList<>();
        for(SmsInvoiceInfo smsInvoiceInfo : smsInvoiceInfoList){
            BaseClaimDetail baseClaimDetail = new BaseClaimDetail();
            baseClaimDetail.setApplyAmount(smsInvoiceInfo.getInvoiceAmount());
            baseClaimDetail.setInvoiceNo(smsInvoiceInfo.getInvoiceNo());
            XMLGregorianCalendar invoiceDate = DateUtils.convertToXMLGregorianCalendar(smsInvoiceInfo.getInvoiceDate());
            baseClaimDetail.setInvoiceDate(invoiceDate);
            baseClaimDetail.setTaxRate(smsInvoiceInfo.getInvoiceRate());
            baseClaimDetail.setTaxAmount(smsInvoiceInfo.getTaxAmount());
            claimDetailList.add(baseClaimDetail);
        }
        baseMultiItemClaimSaveRequest.setClaimDetailList(claimDetailList);
        return baseMultiItemClaimSaveRequest;
    }

    /**
     * 定时任务更新索赔单已兑现的为已结算
     */
    @Override
    public R timeUpdateSettle() {
        //1.查 update_settle_flag 为待更新的
        Example exampleMouthSettle = new Example(SmsMouthSettle.class);
        Example.Criteria criteriaMouthSettle = exampleMouthSettle.createCriteria();
        criteriaMouthSettle.andEqualTo("updateSettleFlag", SettleUpdateFlagEnum.UPDATE_FLAG_0.getCode());
        criteriaMouthSettle.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleMapper.selectByExample(exampleMouthSettle);
        if(CollectionUtils.isEmpty(smsMouthSettleList)){
            return R.ok("无需更新数据");
        }
        List<String> settleNoList = smsMouthSettleList.stream().map(SmsMouthSettle :: getSettleNo).collect(Collectors.toList());
        //2.查索赔对象明细
        Example exampleClaimCashDetail = new Example(SmsClaimCashDetail.class);
        Example.Criteria criteriaClaimCashDetail = exampleClaimCashDetail.createCriteria();
        criteriaClaimCashDetail.andIn("settleNo",settleNoList);
        List<SmsClaimCashDetail> smsClaimCashDetailList = smsClaimCashDetailService.selectByExample(exampleClaimCashDetail);
        if(CollectionUtils.isEmpty(smsClaimCashDetailList)){
            return R.ok("索赔兑现明细");
        }
        Map<String, Set<String>> map = new HashMap<>();//key是索赔类型,value是索赔单号集合
        smsClaimCashDetailList.forEach(smsClaimCashDetail ->{
            String claimType = smsClaimCashDetail.getClaimType();
            if(map.containsKey(claimType)){
                Set<String> set = map.get(claimType);
                set.add(smsClaimCashDetail.getSettleNo());
                map.put(claimType,set);
            }else {
                Set<String> set = new HashSet<>();
                set.add(smsClaimCashDetail.getSettleNo());
                map.put(claimType,set);
            }
        });
        //3.更新对应的索赔单状态
        updateDbSettle(map);
        //4.更新月度结算更新标记
        Example exampleMouthSettleUpdate = new Example(SmsMouthSettle.class);
        Example.Criteria criteriaMouthSettleUpdate = exampleMouthSettleUpdate.createCriteria();
        criteriaMouthSettleUpdate.andEqualTo("updateSettleFlag", SettleUpdateFlagEnum.UPDATE_FLAG_0.getCode());
        SmsMouthSettle smsMouthSettleReq = new SmsMouthSettle();
        smsMouthSettleReq.setUpdateSettleFlag(SettleUpdateFlagEnum.UPDATE_FLAG_1.getCode());
        smsMouthSettleMapper.updateByConditionSelective(smsMouthSettleReq,exampleMouthSettleUpdate);
        return R.ok();
    }

    /**
     * 更新索赔单状态为已结算
     * @param map
     */
    private void updateDbSettle(Map<String, Set<String>> map) {
        map.keySet().forEach(claimType ->{
            Set<String> settleList = map.get(claimType);
            if(!CollectionUtils.isEmpty(settleList)){
                if(SettleRatioEnum.SPLX_BF.getCode().equals(claimType)){
                    Example exampleScrapOrder = new Example(SmsScrapOrder.class);
                    Example.Criteria criteriaScrapOrder = exampleScrapOrder.createCriteria();
                    criteriaScrapOrder.andEqualTo("scrapStatus", ScrapOrderStatusEnum.BF_ORDER_STATUS_YDX.getCode());
                    criteriaScrapOrder.andIn("settleNo",settleList);
                    SmsScrapOrder smsScrapOrderReq = new SmsScrapOrder();
                    smsScrapOrderReq.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_JSWC.getCode());
                    smsScrapOrderService.updateByExampleSelective(smsScrapOrderReq,exampleScrapOrder);
                }
                if(SettleRatioEnum.SPLX_WH.getCode().equals(claimType)){
                    Example exampleSupplementaryOrder = new Example(SmsSupplementaryOrder.class);
                    Example.Criteria criteriaSupplementaryOrder = exampleSupplementaryOrder.createCriteria();
                    criteriaSupplementaryOrder.andEqualTo("stuffStatus", SupplementaryOrderStatusEnum.WH_ORDER_STATUS_YDX.getCode());
                    criteriaSupplementaryOrder.andIn("settleNo",settleList);
                    SmsSupplementaryOrder smsSupplementaryOrderReq = new SmsSupplementaryOrder();
                    smsSupplementaryOrderReq.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_JSWC.getCode());
                    smsSupplementaryOrderService.updateByExampleSelective(smsSupplementaryOrderReq,exampleSupplementaryOrder);
                }
                if(SettleRatioEnum.SPLX_ZL.getCode().equals(claimType)){
                    Example exampleQualityOrder = new Example(SmsQualityOrder.class);
                    Example.Criteria criteriaQualityOrder = exampleQualityOrder.createCriteria();
                    criteriaQualityOrder.andEqualTo("qualityStatus", QualityStatusEnum.QUALITY_STATUS_13.getCode());
                    criteriaQualityOrder.andIn("settleNo",settleList);
                    SmsQualityOrder smsQualityOrderReq = new SmsQualityOrder();
                    smsQualityOrderReq.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_12.getCode());
                    smsQualityOrderMapper.updateByExampleSelective(smsQualityOrderReq,exampleQualityOrder);
                }
                if(SettleRatioEnum.SPLX_QT.getCode().equals(claimType)){
                    Example exampleClaimOther = new Example(SmsClaimOther.class);
                    Example.Criteria criteriaClaimOther = exampleClaimOther.createCriteria();
                    criteriaClaimOther.andEqualTo("claimOtherStatus", ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_13.getCode());
                    criteriaClaimOther.andIn("settleNo",settleList);
                    SmsClaimOther smsClaimOtherReq = new SmsClaimOther();
                    smsClaimOtherReq.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_12.getCode());
                    smsClaimOtherMapper.updateByExampleSelective(smsClaimOtherReq,exampleClaimOther);
                }
                if(SettleRatioEnum.SPLX_YQ.getCode().equals(claimType)){
                    Example exampleDelaysDelivery = new Example(SmsDelaysDelivery.class);
                    Example.Criteria criteriaDelaysDelivery = exampleDelaysDelivery.createCriteria();
                    criteriaDelaysDelivery.andEqualTo("delaysStatus", DeplayStatusEnum.DELAYS_STATUS_13.getCode());
                    criteriaDelaysDelivery.andIn("settleNo",settleList);
                    SmsDelaysDelivery smsDelaysDeliveryReq = new SmsDelaysDelivery();
                    smsDelaysDeliveryReq.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_12.getCode());
                    smsDelaysDeliveryMapper.updateByExampleSelective(smsDelaysDeliveryReq,exampleDelaysDelivery);
                }
            }

        });
    }
    /**
     * 原材料报废待结算分组
     * @param lastMonth
     */
    public Map<String,List<SmsRawMaterialScrapOrder>> rawScrapGroup(String lastMonth){
        String key;
        Map<String, List<SmsRawMaterialScrapOrder>> mapRawScrap = new ConcurrentHashMap<>();
        //取得计算月份、待结算的原材料报废记录
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrders =
                smsRawMaterialScrapOrderService.selectByMonthAndStatus(lastMonth,  CollUtil.newArrayList(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DJS.getCode()));
        if (smsRawMaterialScrapOrders != null) {
            for (SmsRawMaterialScrapOrder smsRawMaterialScrapOrder : smsRawMaterialScrapOrders) {
                //下面开始分组：根据供应商和付款公司分组
                key = smsRawMaterialScrapOrder.getSupplierCode() + smsRawMaterialScrapOrder.getComponyCode();
                List<SmsRawMaterialScrapOrder> supplementList = mapRawScrap.getOrDefault(key, new ArrayList<>());
                supplementList.add(smsRawMaterialScrapOrder);
                mapRawScrap.put(key, supplementList);
            }
        }
        return mapRawScrap;
    }

    /**
     * 原材料报废待结算分组
     * @param
     */
    public Map<String,List<SmsRawMaterialScrapOrder>> rawScrapLSGroup(){
        String key;
        Map<String, List<SmsRawMaterialScrapOrder>> mapRawScrap = new ConcurrentHashMap<>();
        List<String> statusList = CollUtil.newArrayList(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_WDX.getCode(),RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_BFDX.getCode());
        //取得计算月份、待结算的原材料报废记录
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrders =
                smsRawMaterialScrapOrderService.selectByMonthAndStatus(null, statusList);
        if (smsRawMaterialScrapOrders != null) {
            for (SmsRawMaterialScrapOrder smsRawMaterialScrapOrder : smsRawMaterialScrapOrders) {
                //下面开始分组：根据供应商和付款公司分组
                key = smsRawMaterialScrapOrder.getSupplierCode() + smsRawMaterialScrapOrder.getComponyCode();
                List<SmsRawMaterialScrapOrder> supplementList = mapRawScrap.getOrDefault(key, new ArrayList<>());
                supplementList.add(smsRawMaterialScrapOrder);
                mapRawScrap.put(key, supplementList);
            }
        }
        return mapRawScrap;
    }
    /**
     * 原材料报废计算
     *
     * @param mapRawScrap
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> rawScrapCompute(Map<String, List<SmsRawMaterialScrapOrder>> mapRawScrap, String keyCode, String monthSettleNo,
                                     BigDecimal settlePrice, BigDecimal claimPrice, String lastMonth,
                                     List<SmsClaimCashDetail> claimCashDetailList) {
        //报废索赔
        if (MapUtil.isNotEmpty(mapRawScrap) && CollUtil.isNotEmpty(mapRawScrap.get(keyCode))) {
            for (SmsRawMaterialScrapOrder smsRawMaterialScrapOrder : mapRawScrap.get(keyCode)) {
                smsRawMaterialScrapOrder.setSettleNo(monthSettleNo);//结算单号
                if (settlePrice.compareTo(smsRawMaterialScrapOrder.getScrapPrice()) >= 0) {
                    smsRawMaterialScrapOrder.setCashAmount(smsRawMaterialScrapOrder.getScrapPrice());
                    smsRawMaterialScrapOrder.setUncashAmount(BigDecimal.ZERO);
                    smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_YDX.getCode());
                    settlePrice = settlePrice.subtract(smsRawMaterialScrapOrder.getScrapPrice());
                } else {
                    if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                        smsRawMaterialScrapOrder.setCashAmount(BigDecimal.ZERO);
                        smsRawMaterialScrapOrder.setUncashAmount(smsRawMaterialScrapOrder.getScrapPrice());
                        smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_WDX.getCode());
                    } else {
                        smsRawMaterialScrapOrder.setCashAmount(settlePrice);
                        smsRawMaterialScrapOrder.setUncashAmount(smsRawMaterialScrapOrder.getScrapPrice().subtract(settlePrice));
                        smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_BFDX.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                }
                claimPrice = claimPrice.add(smsRawMaterialScrapOrder.getScrapPrice());

                //索赔明细
                if (smsRawMaterialScrapOrder.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = SmsClaimCashDetail.builder()
                            .claimNo(smsRawMaterialScrapOrder.getRawScrapNo()).claimType(SettleRatioEnum.SPLX_YCLBF.getCode())
                            .cashAmount(smsRawMaterialScrapOrder.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsRawMaterialScrapOrderService.updateBatchByPrimaryKeySelective(mapRawScrap.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 原材料报废历史计算
     *
     * @param mapRawScrapLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> rawScrapLSCompute(Map<String, List<SmsRawMaterialScrapOrder>> mapRawScrapLS, String keyCode, String monthSettleNo,
                                       BigDecimal settlePrice, BigDecimal unCashPrice, String lastMonth,
                                       List<SmsClaimCashDetail> claimCashDetailList) {
        //报废索赔历史
        if (MapUtil.isNotEmpty(mapRawScrapLS) && CollUtil.isNotEmpty(mapRawScrapLS.get(keyCode))) {
            for (SmsRawMaterialScrapOrder smsRawMaterialScrapOrder : mapRawScrapLS.get(keyCode)) {
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
                BigDecimal cashAmount;
                if (settlePrice.compareTo(smsRawMaterialScrapOrder.getUncashAmount()) >= 0) {
                    cashAmount = smsRawMaterialScrapOrder.getUncashAmount();
                    smsRawMaterialScrapOrder.setCashAmount(smsRawMaterialScrapOrder.getCashAmount().add(smsRawMaterialScrapOrder.getUncashAmount()));
                    smsRawMaterialScrapOrder.setUncashAmount(BigDecimal.ZERO);
                    smsRawMaterialScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_YDX.getCode());
                    settlePrice = settlePrice.subtract(smsRawMaterialScrapOrder.getUncashAmount());
                } else {
                    cashAmount = settlePrice;
                    smsRawMaterialScrapOrder.setCashAmount(smsRawMaterialScrapOrder.getCashAmount().add(settlePrice));
                    smsRawMaterialScrapOrder.setUncashAmount(smsRawMaterialScrapOrder.getUncashAmount().subtract(settlePrice));
                    smsRawMaterialScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_BFDX.getCode());
                    settlePrice = BigDecimal.ZERO;
                }
                unCashPrice = unCashPrice.add(smsRawMaterialScrapOrder.getUncashAmount());

                //索赔明细
                if (cashAmount.compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = SmsClaimCashDetail.builder()
                            .claimNo(smsRawMaterialScrapOrder.getRawScrapNo()).claimType(SettleRatioEnum.SPLX_BF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(DateUtil.format(smsRawMaterialScrapOrder.getSapTransDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsRawMaterialScrapOrderService.updateBatchByPrimaryKeySelective(mapRawScrapLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 质量部报废待结算分组
     * @param lastMonth
     */
    public Map<String,List<SmsQualityScrapOrder>> qualityScrapGroup(String lastMonth){
        String key;
        Map<String, List<SmsQualityScrapOrder>> mapQualityScrap = new ConcurrentHashMap<>();
        //取得计算月份、待结算的质量部报废记录
        List<SmsQualityScrapOrder> smsQualityScrapOrders =
                smsQualityScrapOrderService.selectByMonthAndStatus(lastMonth,  CollUtil.newArrayList(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DJS.getCode()));
        if (smsQualityScrapOrders != null) {
            for (SmsQualityScrapOrder smsQualityScrapOrder : smsQualityScrapOrders) {
                //下面开始分组：根据供应商和付款公司分组
                key = smsQualityScrapOrder.getSupplierCode() + smsQualityScrapOrder.getCompanyCode();
                List<SmsQualityScrapOrder> smsQualityScrapOrderList = mapQualityScrap.getOrDefault(key, new ArrayList<>());
                smsQualityScrapOrderList.add(smsQualityScrapOrder);
                mapQualityScrap.put(key, smsQualityScrapOrderList);
            }
        }
        return mapQualityScrap;
    }

    /**
     * 质量部报废历史待结算分组
     * @param
     */
    public Map<String,List<SmsQualityScrapOrder>> qualityScrapLSGroup(){
        String key;
        Map<String, List<SmsQualityScrapOrder>> mapQualityScrap = new ConcurrentHashMap<>();
        List<String> statusList = CollUtil.newArrayList(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_BFDX.getCode(),QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_WDX.getCode());
        //取得计算月份、待结算的原材料报废记录
        List<SmsQualityScrapOrder> smsQualityScrapOrders =
                smsQualityScrapOrderService.selectByMonthAndStatus(null, statusList);
        if (smsQualityScrapOrders != null) {
            for (SmsQualityScrapOrder smsQualityScrapOrder : smsQualityScrapOrders) {
                //下面开始分组：根据供应商和付款公司分组
                key = smsQualityScrapOrder.getSupplierCode() + smsQualityScrapOrder.getCompanyCode();
                List<SmsQualityScrapOrder> smsQualityScrapOrderList = mapQualityScrap.getOrDefault(key, new ArrayList<>());
                smsQualityScrapOrderList.add(smsQualityScrapOrder);
                mapQualityScrap.put(key, smsQualityScrapOrderList);
            }
        }
        return mapQualityScrap;
    }

    /**
     * 质量部报废计算
     *
     * @param mapQualityScrap
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param claimPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> qualityScrapCompute(Map<String, List<SmsQualityScrapOrder>> mapQualityScrap, String keyCode, String monthSettleNo,
                                        BigDecimal settlePrice, BigDecimal claimPrice, String lastMonth,
                                        List<SmsClaimCashDetail> claimCashDetailList) {
        //报废索赔
        if (MapUtil.isNotEmpty(mapQualityScrap) && CollUtil.isNotEmpty(mapQualityScrap.get(keyCode))) {
            for (SmsQualityScrapOrder smsQualityScrapOrder : mapQualityScrap.get(keyCode)) {
                smsQualityScrapOrder.setSettleNo(monthSettleNo);//结算单号
                if (settlePrice.compareTo(smsQualityScrapOrder.getSettleFee()) >= 0) {
                    smsQualityScrapOrder.setCashAmount(smsQualityScrapOrder.getSettleFee());
                    smsQualityScrapOrder.setUncashAmount(BigDecimal.ZERO);
                    smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_YJS.getCode());
                    settlePrice = settlePrice.subtract(smsQualityScrapOrder.getSettleFee());
                } else {
                    if (settlePrice.compareTo(BigDecimal.ZERO) == 0) {
                        smsQualityScrapOrder.setCashAmount(BigDecimal.ZERO);
                        smsQualityScrapOrder.setUncashAmount(smsQualityScrapOrder.getSettleFee());
                        smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_WDX.getCode());
                    } else {
                        smsQualityScrapOrder.setCashAmount(settlePrice);
                        smsQualityScrapOrder.setUncashAmount(smsQualityScrapOrder.getSettleFee().subtract(settlePrice));
                        smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_BFDX.getCode());
                        settlePrice = BigDecimal.ZERO;
                    }
                }
                claimPrice = claimPrice.add(smsQualityScrapOrder.getSettleFee());

                //索赔明细
                if (smsQualityScrapOrder.getCashAmount().compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = SmsClaimCashDetail.builder()
                            .claimNo(smsQualityScrapOrder.getScrapNo()).claimType(SettleRatioEnum.SPLX_ZLBBF.getCode())
                            .cashAmount(smsQualityScrapOrder.getCashAmount()).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(lastMonth).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsQualityScrapOrderService.updateBatchByPrimaryKeySelective(mapQualityScrap.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("claimPrice", claimPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }

    /**
     * 质量部报废历史计算
     *
     * @param mapQualityScrapLS
     * @param keyCode
     * @param monthSettleNo
     * @param settlePrice
     * @param unCashPrice
     * @param lastMonth
     * @param claimCashDetailList
     * @return
     */
    Map<String, Object> qualityScrapLSCompute(Map<String, List<SmsQualityScrapOrder>> mapQualityScrapLS, String keyCode, String monthSettleNo,
                                          BigDecimal settlePrice, BigDecimal unCashPrice, String lastMonth,
                                          List<SmsClaimCashDetail> claimCashDetailList) {
        //报废索赔历史
        if (MapUtil.isNotEmpty(mapQualityScrapLS) && CollUtil.isNotEmpty(mapQualityScrapLS.get(keyCode))) {
            for (SmsQualityScrapOrder smsQualityScrapOrder : mapQualityScrapLS.get(keyCode)) {
                if (settlePrice.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
                BigDecimal cashAmount;
                if (settlePrice.compareTo(smsQualityScrapOrder.getUncashAmount()) >= 0) {
                    cashAmount = smsQualityScrapOrder.getUncashAmount();
                    smsQualityScrapOrder.setCashAmount(smsQualityScrapOrder.getCashAmount().add(smsQualityScrapOrder.getUncashAmount()));
                    smsQualityScrapOrder.setUncashAmount(BigDecimal.ZERO);
                    smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_YJS.getCode());
                    settlePrice = settlePrice.subtract(smsQualityScrapOrder.getUncashAmount());
                } else {
                    cashAmount = settlePrice;
                    smsQualityScrapOrder.setCashAmount(smsQualityScrapOrder.getCashAmount().add(settlePrice));
                    smsQualityScrapOrder.setUncashAmount(smsQualityScrapOrder.getUncashAmount().subtract(settlePrice));
                    smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_BFDX.getCode());
                    settlePrice = BigDecimal.ZERO;
                }
                unCashPrice = unCashPrice.add(smsQualityScrapOrder.getUncashAmount());

                //索赔明细
                if (cashAmount.compareTo(BigDecimal.ZERO)>0) {
                    SmsClaimCashDetail smsClaimCashDetail = SmsClaimCashDetail.builder()
                            .claimNo(smsQualityScrapOrder.getScrapNo()).claimType(SettleRatioEnum.SPLX_ZLBBF.getCode())
                            .cashAmount(cashAmount).settleNo(monthSettleNo).delFlag("0")
                            .shouldCashMounth(DateUtil.format(smsQualityScrapOrder.getSapDate(), "yyyyMM")).actualCashMounth(lastMonth).build();
                    smsClaimCashDetail.setCreateTime(DateUtil.date());
                    claimCashDetailList.add(smsClaimCashDetail);
                }
            }
            smsQualityScrapOrderService.updateBatchByPrimaryKeySelective(mapQualityScrapLS.get(keyCode));
        }
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("settlePrice", settlePrice);
        map.put("unCashPrice", unCashPrice);
        map.put("claimCashDetailList", claimCashDetailList);
        return map;
    }
}
