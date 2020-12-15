package com.cloud.settle.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.feign.RemoteActTaskService;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.enums.ProductionOrderStatusEnum;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.CurrencyEnum;
import com.cloud.settle.enums.IsEntityEnum;
import com.cloud.settle.enums.IsPayEnum;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.mapper.SmsScrapOrderMapper;
import com.cloud.settle.service.ISmsScrapOrderService;
import com.cloud.settle.util.OrderNoGenerateUtil;
import com.cloud.system.domain.entity.*;
import com.cloud.system.enums.OutSourceTypeEnum;
import com.cloud.system.enums.PriceTypeEnum;
import com.cloud.system.enums.SettleRatioEnum;
import com.cloud.system.feign.*;
import com.sap.conn.jco.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.assertj.core.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private RemoteFactoryLineInfoService remotefactoryLineInfoService;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteSettleRatioService remoteSettleRatioService;
    @Autowired
    private RemoteCdSapSalePriceInfoService remoteCdSapSalePriceInfoService;
    @Autowired
    private RemoteCdMouthRateService remoteCdMouthRateService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    @Autowired
    private RemoteCdProductWarehouseService remoteCdProductWarehouseService;
    @Autowired
    private RemoteCdScrapMonthNoService remoteCdScrapMonthNoService;
    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;
    @Autowired
    private RemoteActTaskService remoteActTaskService;
    @Autowired
    private RemoteCdSettleProductMaterialService remoteCdSettleProductMaterialService;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;




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
        R rCheckStatus = checkConditionCommit(id);
        SmsScrapOrder smsScrapOrderCheck = (SmsScrapOrder) rCheckStatus.get("data");
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,smsScrapOrderCheck.getProductOrderCode());
        if (!rCheck.isSuccess()) {
            throw new BusinessException(rCheck.getStr("msg"));
        }
        String productOrderCode = smsScrapOrderCheck.getProductOrderCode();
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if(!omsProductionOrderResult.isSuccess()){
            log.error("根据生产单号获取排产订单信息失败 productOrderCode:{},res:{}",productOrderCode, JSONObject.toJSON(omsProductionOrderResult));
            throw new BusinessException(omsProductionOrderResult.get("msg").toString());
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
        R rFactory = remoteFactoryInfoService.selectOneByFactory(omsProductionOrder.getProductFactoryCode());
        if(!rFactory.isSuccess()){
            log.error(StrUtil.format("(报废)报废申请新增保存开始：公司信息为空参数为{}", omsProductionOrder.getProductFactoryCode()));
            return R.error("公司信息为空！");
        }
        CdFactoryInfo cdFactoryInfo = rFactory.getData(CdFactoryInfo.class);
        smsScrapOrder.setCompanyCode(cdFactoryInfo.getCompanyCode());
        //加工费总额
        BigDecimal machiningPrice = BigDecimal.ZERO;
        if (OutSourceTypeEnum.OUT_SOURCE_TYPE_BWW.getCode().equals(smsScrapOrder.getScrapType())) {
            //半成品，按照成品加工费计算
            machiningPrice = getMachiningPrice(smsScrapOrder.getProductMaterialCode(),
                    smsScrapOrder.getScrapType(),cdFactoryInfo.getPurchaseOrg(),smsScrapOrderCheck.getSupplierCode(),smsScrapOrder.getScrapAmount());
        }else{
            Preconditions.checkArgument(omsProductionOrder.getProcessCost() != null,StrUtil.format("生产订单：{}无加工费单价！",omsProductionOrder.getProductOrderCode()));
            machiningPrice = omsProductionOrder.getProcessCost().multiply(BigDecimal.valueOf(smsScrapOrder.getScrapAmount()));
        }
        smsScrapOrder.setMachiningPrice(machiningPrice);
        int rows = updateByPrimaryKeySelective(smsScrapOrder);
        return rows > 0 ? R.ok() : R.error("更新错误！");
    }

    /**
     * 新增保存报废申请
     * @param smsScrapOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R addSave(SmsScrapOrder smsScrapOrder) {
        log.info(StrUtil.format("报废申请新增保存开始：参数为{}", smsScrapOrder.toString()));
        //生产订单号
        String productOrderCode = smsScrapOrder.getProductOrderCode();
        //生产单号获取排产订单信息
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if(!omsProductionOrderResult.isSuccess()){
            log.error("根据生产单号获取排产订单信息失败 productOrderCode:{},res:{}",productOrderCode, JSONObject.toJSON(omsProductionOrderResult));
            throw new BusinessException(omsProductionOrderResult.get("msg").toString());
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
        //20201117 王福丽提出传SAP后才可以申请
        List<String> canStatus = CollUtil.newArrayList(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode(),
                ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode());
        if (!canStatus.contains(omsProductionOrder.getStatus())) {
            return R.error(StrUtil.format("只允许{},{}状态申请报废单！", ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getMsg(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getMsg()));
        }
        //20200917 王福丽提出去除状态限制  基本开始日期<=今天就可以申请
        String productStartDateStr = omsProductionOrder.getProductStartDate();
        if (StrUtil.isEmpty(productStartDateStr)) {
            throw new BusinessException("订单基本开始日期为空,不允许申请报废！");
        }
        Date productStartDate = DateUtils.parseDate(productStartDateStr);
        if (DateUtil.compare(DateUtil.date(), productStartDate) < 0) {
            throw new BusinessException("未到达订单基本开始日期,不允许申请报废！");
        }
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,productOrderCode);
        if (!rCheck.isSuccess()) {
            throw new BusinessException(rCheck.getStr("msg"));
        }
        //订单号
        List<String> randomList = OrderNoGenerateUtil.getOrderNos(1, "BF");
        String scrapNo = randomList.get(0);
        smsScrapOrder.setScrapNo(scrapNo);
        //根据线体号查询供应商编码
        R rFactoryLineInfo=remotefactoryLineInfoService.selectInfoByCodeLineCode(omsProductionOrder.getProductLineCode(),
                omsProductionOrder.getProductFactoryCode());
        if (!rFactoryLineInfo.isSuccess()) {
            return rFactoryLineInfo;
        }
        CdFactoryLineInfo factoryLineInfo = rFactoryLineInfo.getData(CdFactoryLineInfo.class);
        if (factoryLineInfo == null||StrUtil.isEmpty(factoryLineInfo.getSupplierCode())) {
            return R.error(StrUtil.format("工厂：{}，线体{}，缺少供应商信息",omsProductionOrder.getProductFactoryCode(),
                    omsProductionOrder.getProductLineCode()));
        }
        smsScrapOrder.setSupplierCode(factoryLineInfo.getSupplierCode());
        smsScrapOrder.setSupplierName(factoryLineInfo.getSupplierDesc());
        R rFactory = remoteFactoryInfoService.selectOneByFactory(omsProductionOrder.getProductFactoryCode());
        if(!rFactory.isSuccess()){
            log.error(StrUtil.format("(报废)报废申请新增保存开始：公司信息为空参数为{}", omsProductionOrder.getProductFactoryCode()));
            return R.error("公司信息为空！");
        }
        CdFactoryInfo cdFactoryInfo = rFactory.getData(CdFactoryInfo.class);
        smsScrapOrder.setCompanyCode(cdFactoryInfo.getCompanyCode());
        //加工费总额
        BigDecimal machiningPrice = BigDecimal.ZERO;
        if (OutSourceTypeEnum.OUT_SOURCE_TYPE_BWW.getCode().equals(smsScrapOrder.getScrapType())) {
            //半成品，按照成品加工费计算
            machiningPrice = getMachiningPrice(smsScrapOrder.getProductMaterialCode(),
                    smsScrapOrder.getScrapType(),cdFactoryInfo.getPurchaseOrg(),smsScrapOrder.getSupplierCode(),smsScrapOrder.getScrapAmount());
        }else{
            Preconditions.checkArgument(omsProductionOrder.getProcessCost() != null,StrUtil.format("生产订单：{}无加工费单价！",omsProductionOrder.getProductOrderCode()));
            machiningPrice = omsProductionOrder.getProcessCost().multiply(BigDecimal.valueOf(smsScrapOrder.getScrapAmount()));
        }
        smsScrapOrder.setMachiningPrice(machiningPrice);
        smsScrapOrder.setFactoryCode(omsProductionOrder.getProductFactoryCode());
        if (StrUtil.isBlank(smsScrapOrder.getScrapStatus())) {
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode());
        }
        smsScrapOrder.setDelFlag("0");
        smsScrapOrder.setCreateTime(DateUtils.getNowDate());
        int rows=insertSelective(smsScrapOrder);
        if (rows > 0) {
            return R.data(smsScrapOrder.getId());
        }else{
            return R.error("报废申请插入失败！");
        }
    }

    /**
     * 计算加工费
     * @param productMaterialCode
     * @param outSourceType
     * @param purchaseOrg
     * @param supplierCode
     * @param amount
     * @return
     */
    public BigDecimal getMachiningPrice(String productMaterialCode,String outSourceType,String purchaseOrg,String supplierCode,int amount){
        BigDecimal machiningPrice = BigDecimal.ZERO;
        R productMaterialMap =
                remoteCdSettleProductMaterialService.selectOne(productMaterialCode, outSourceType);
        Preconditions.checkArgument(productMaterialMap.isSuccess(),"加工费号查询失败！");
        CdSettleProductMaterial cdSettleProductMaterial = productMaterialMap.getData(CdSettleProductMaterial.class);
        String rawMaterialCode = cdSettleProductMaterial.getRawMaterialCode();
        //根据加工费号,供应商,采购组织 查加工费
        R maResult = remoteCdMaterialPriceInfoService.selectOneByCondition(rawMaterialCode, purchaseOrg,
                supplierCode, PriceTypeEnum.PRICE_TYPE_1.getCode());
        Preconditions.checkArgument(maResult.isSuccess(),"加工费查询失败！");
        CdMaterialPriceInfo cdMaterialPriceInfo = maResult.getData(CdMaterialPriceInfo.class);
        Preconditions.checkArgument(cdMaterialPriceInfo.getNetWorth() != null, "加工费查询失败！");
        machiningPrice = cdMaterialPriceInfo.getNetWorth().multiply(BigDecimal.valueOf(amount));
        return machiningPrice;
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
        int applyNum = smsScrapOrder.getScrapAmount();//申请量
        //生产单号获取排产订单信息
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if (!omsProductionOrderResult.isSuccess()) {
            return R.error("订单信息不存在！");
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
        //5、校验申请量是否大于订单量
        BigDecimal productNum = omsProductionOrder.getProductNum();
        if (new BigDecimal(applyNum).compareTo(productNum) > 0) {
            return R.error("本次申请量不得大于订单量");
        }
        //查找已申请的报废量
        Example example = new Example(SmsScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productOrderCode", productOrderCode);
        List<String> cannotStatus = CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYBH.getCode());
        criteria.andNotIn("scrapStatus", cannotStatus);
        if (smsScrapOrder.getId() != null) {
            criteria.andNotEqualTo("id", smsScrapOrder.getId());
        }
        List<SmsScrapOrder> smsScrapOrders = selectByExample(example);
        if (CollUtil.isNotEmpty(smsScrapOrders)) {
            int amount=smsScrapOrders.stream().mapToInt(t -> t.getScrapAmount()).sum();
            applyNum += amount;
        }
        if (new BigDecimal(applyNum).compareTo(productNum) > 0) {
            return R.error("申请量总额不得大于订单量");
        }

        //校验半成品物料是否合法
        String outsourceType = omsProductionOrder.getOutsourceType();
        Preconditions.checkArgument(StrUtil.isNotEmpty(outsourceType),"生产订单无加工承揽方式，不能申请！");
        if (OutSourceTypeEnum.OUT_SOURCE_TYPE_BWW.getCode().equals(outsourceType)) {
            //半成品：需要校验成品号
            String productMaterialCode = smsScrapOrder.getProductMaterialCode();
            Preconditions.checkArgument(StrUtil.isNotEmpty(productMaterialCode),"请填写正确的成品专用号！");
            R rMate = remoteMaterialExtendInfoService.selectOneByMaterialCode(productMaterialCode);
            Preconditions.checkArgument(rMate.isSuccess(),"无效成品专用号！");
        }
        //校验无实物时是否买单
        Preconditions.checkArgument(StrUtil.isNotEmpty(smsScrapOrder.getIsPay()),"请选择是否买单！");
        Preconditions.checkArgument(StrUtil.isNotEmpty(smsScrapOrder.getIsEntity()),"请选择有无实物！");
        if (IsEntityEnum.IS_ENTITY_NO.getCode().equals(smsScrapOrder.getIsEntity())) {
            Preconditions.checkArgument(IsPayEnum.IS_PAY_YES.getCode().equals(smsScrapOrder.getIsPay()),
                    "无实物时必须买单！");
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
        //如果已生成审批流信息需删除
        List<String> shStatus = CollUtil.newArrayList(
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLSH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSHBMD.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYBHBMD.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSHBMD.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBHBMD.getCode()
        );
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            R rCheckStatus = checkConditionRemove(Long.valueOf(id));
            SmsScrapOrder smsScrapOrder = (SmsScrapOrder) rCheckStatus.get("data");
            if (CollUtil.contains(shStatus, smsScrapOrder.getScrapStatus())) {
                //删除审批信息
                Map<String,Object> map = new HashMap<>();
                List<String> orderCodeList = CollUtil.newArrayList(smsScrapOrder.getScrapNo());
                map.put("userName","报废删除同时删除审批流");
                map.put("orderCodeList",orderCodeList);
                R deleteActMap = remoteActTaskService.deleteByOrderCode(map);
                if (!deleteActMap.isSuccess()){
                    throw new BusinessException("删除审批流程失败，原因："+deleteActMap.get("msg"));
                }
            }
        }
        int rows = deleteByIds(ids);
        return rows > 0 ? R.ok() : R.error("删除错误！");
    }

    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return 返回SmsScrapOrder信息
     */
    public R checkConditionCommit(Long id){
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

    /**
     * 删除校验
     * @param id
     * @return
     */
    public R checkConditionRemove(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsScrapOrder smsScrapOrder = selectByPrimaryKey(id);
        if (smsScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        List<String> shStatus = CollUtil.newArrayList(
                ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLSH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_ZLJLBH.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYSHBMD.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_PCYBHBMD.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKSHBMD.getCode(),
                ScrapOrderStatusEnum.BF_ORDER_STATUS_YWKBHBMD.getCode()
        );
        if (!shStatus.contains(smsScrapOrder.getScrapStatus())) {
            throw new BusinessException("已传SAP的数据不能操作！");
        }
        return R.data(smsScrapOrder);
    }

    /**
     * 根据月份和状态查询
     * @param month
     * @param scrapStatus
     * @return
     */
    @Override
    public List<SmsScrapOrder> selectByMonthAndStatus(String month, List<String> scrapStatus) {
        return smsScrapOrderMapper.selectByMonthAndStatus(month,scrapStatus);
    }

    /**
     * 定时任务更新指定月份销售价格到报废表
     * @param month
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R updatePriceEveryMonth(String month) {
        //报废索赔系数
        CdSettleRatio cdSettleRatioBF = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_BF.getCode());
        CdSettleRatio cdSettleRatioWSWBF = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_WSWBF.getCode());
        Preconditions.checkArgument(cdSettleRatioBF!=null,"报废索赔系数未维护！");
        Preconditions.checkArgument(cdSettleRatioWSWBF!=null,"无实物报废索赔系数未维护！");
        BigDecimal ratio = cdSettleRatioBF.getRatio();//报废索赔系数
        BigDecimal wswRatio = cdSettleRatioWSWBF.getRatio();//无实物报废索赔系数
        //从SAP销售价格表取值（销售组织、物料号、有效期）
        //查询上个月、待结算的物耗申请中的物料号  用途是查询SAP成本价 更新到物耗表
        List<String> materialCodeList = smsScrapOrderMapper.selectMaterialByMonthAndStatus(month, CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode()));
        Map<String, CdSapSalePrice> sapPrice = new ConcurrentHashMap<>();
        if (materialCodeList != null) {
            log.info(StrUtil.format("(月度结算定时任务)报废申请需要更新销售价格的物料号:{}", materialCodeList.toString()));
            String now = DateUtil.now();
            String materialCodeStr = StrUtil.join(",", materialCodeList);
            //根据前面查出的物料号查询SAP成本价 map key:物料号  value:CdMaterialPriceInfo
            sapPrice = remoteCdSapSalePriceInfoService.selectPriceByInMaterialCodeAndDate(materialCodeStr, now, now);
        }
        //取得计算月份、待结算的报废数据
        List<SmsScrapOrder> smsScrapOrderList = selectByMonthAndStatus(month, CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode()));
        //循环报废，计算索赔金额
        if (smsScrapOrderList != null) {
            for (SmsScrapOrder smsScrapOrder : smsScrapOrderList) {
                CdSapSalePrice cdSapSalePrice = sapPrice.get(smsScrapOrder.getProductMaterialCode()+smsScrapOrder.getCompanyCode());
                if (cdSapSalePrice == null) {
                    //如果没有找到SAP销售价格，则更新备注
                    log.info(StrUtil.format("(定时任务)SAP销售价格未同步的物料号:{}", smsScrapOrder.getProductMaterialCode()));
                    smsScrapOrder.setRemark("SAP销售价格未同步！");
                    updateByPrimaryKeySelective(smsScrapOrder);
                    continue;
                }
                smsScrapOrder.setCurrency(cdSapSalePrice.getConditionsMonetary());
                smsScrapOrder.setMaterialPrice(new BigDecimal(cdSapSalePrice.getSalePrice()).divide(new BigDecimal(cdSapSalePrice.getUnitPricing()),6, BigDecimal.ROUND_HALF_UP));
                smsScrapOrder.setMeasureUnit(cdSapSalePrice.getMeasureUnit());
                smsScrapOrder.setScrapPrice(smsScrapOrder.getMaterialPrice().multiply(new BigDecimal(smsScrapOrder.getScrapAmount())));
                //索赔金额=（Sap成品物料销售价格*报废数量*报废索赔系数）+（报废数量*生产订单加工费单价）
                BigDecimal scrapPrice ;//索赔金额
                BigDecimal scrapAmount = new BigDecimal(smsScrapOrder.getScrapAmount());//报废数量
                BigDecimal materialPrice = smsScrapOrder.getMaterialPrice();//成品物料销售价格

                BigDecimal machiningPrice = smsScrapOrder.getMachiningPrice();//加工费总额
                scrapPrice = materialPrice.multiply(scrapAmount.multiply(ratio));
                //如果无实物，还需乘无实物买单系数
                if(IsEntityEnum.IS_ENTITY_NO.getCode().equals(smsScrapOrder.getIsEntity())){
                    scrapPrice = scrapPrice.multiply(wswRatio);
                }
                //如果是外币，还要 除以数额*汇率
                if (StrUtil.isEmpty(smsScrapOrder.getCurrency())) {
                    throw new BusinessException(StrUtil.format("{}报废单未维护币种", smsScrapOrder.getScrapNo()));
                }
                if (!StrUtil.equals(CurrencyEnum.CURRENCY_CNY.getCode(), smsScrapOrder.getCurrency())) {
                    //查询指定月汇率
                    R rRate = remoteCdMouthRateService.findRateByYearMouth(month,smsScrapOrder.getCurrency());
                    if (!rRate.isSuccess()) {
                        throw new BusinessException(StrUtil.format("{}月份未维护{}币种费率", month,smsScrapOrder.getCurrency()));
                    }
                    CdMouthRate cdMouthRate = rRate.getData(CdMouthRate.class);
                    BigDecimal rate = cdMouthRate.getRate();//汇率
                    BigDecimal rateAmount = cdMouthRate.getAmount();//数额
                    scrapPrice = scrapPrice.divide(rateAmount,6, BigDecimal.ROUND_HALF_UP).multiply(rate);
                }
                //因为加工费单价都是人民币，所以在计算汇率后再加
                scrapPrice = scrapPrice.add(machiningPrice);
                smsScrapOrder.setSettleFee(scrapPrice);
            }
            updateBatchByPrimaryKeySelective(smsScrapOrderList);
        }
        return R.ok();
    }

    /**
     * 定时任务更新指定月份SAP销售价格
     * @param month
     * @return
     */
    @Override
    @GlobalTransactional
    public R updateSAPPriceEveryMonth(String month) {
        //查询上个月、待结算的物耗申请中的物料号，公司编号
        List<Map<String,String>> materialCodeComCodeList = smsScrapOrderMapper.selectMaterialAndCompanyCodeGroupBy(month, CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode()));
        JCoDestination destination;
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName(SapConstants.ZSD_INT_DDPS_01)
                .content(CollUtil.join(materialCodeComCodeList, "#")).build();
        Date date = DateUtil.date();
        StringBuffer error = new StringBuffer();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction("ZSD_INT_DDPS_01");
            if (fm == null) {
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoTable inputTable = fm.getTableParameterList().getTable("T_INPUT");
            materialCodeComCodeList.forEach(stringStringMap -> {
                inputTable.appendRow();
                inputTable.setValue("VKORG", stringStringMap.get("companyCode"));
                inputTable.setValue("MATNR", stringStringMap.get("materialCode").toUpperCase());
            });
            log.info(StrUtil.format("【SAP销售价格接口】传输参数：{}"),CollUtil.join(materialCodeComCodeList,"#"));
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoParameterList exportParameter=fm.getExportParameterList();
            String eType=exportParameter.getString("E_TYPE");
            String eMessage=exportParameter.getString("E_MESSAGE");
            if (SapConstants.SAP_RESULT_TYPE_FAIL.equals(eType)) {
                log.error(StrUtil.format("SAP返回错误信息：{}",eMessage));
                sysInterfaceLog.setResults(StrUtil.format("SAP返回错误信息：{}",eMessage));
                return R.error(eMessage);
            }
            JCoTable outTableOutput = fm.getTableParameterList().getTable("T_OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    CdSapSalePrice cdSapSalePrice = new CdSapSalePrice().builder()
                            .conditionsType(outTableOutput.getString("KSCHL"))
                            .marketingOrganization(outTableOutput.getString("VKORG"))
                            .materialCode(outTableOutput.getString("MATNR"))
                            .beginDate(outTableOutput.getDate("DATAB"))
                            .endDate(outTableOutput.getDate("DATBI"))
                            .pricingRecordNo(outTableOutput.getString("KNUMH"))
                            .salePrice(outTableOutput.getString("KBETR"))
                            .conditionsMonetary(outTableOutput.getString("KONWA"))
                            .unitPricing(outTableOutput.getString("KPEIN"))
                            .measureUnit(outTableOutput.getString("KMEIN"))
                            .sapDelFlag(outTableOutput.getString("LOEVM_KO")).build();
                    List<CdSapSalePrice> cdSapSalePriceList = remoteCdSapSalePriceInfoService.findByMaterialCodeAndOraganization(outTableOutput.getString("MATNR"),outTableOutput.getString("VKORG"),null,null);
                    R r ;
                    if (cdSapSalePriceList == null||cdSapSalePriceList.size()==0) {
                        cdSapSalePrice.setCreateBy("定时任务");
                        cdSapSalePrice.setCreateTime(date);
                        r=remoteCdSapSalePriceInfoService.addSave(cdSapSalePrice);
                    }else{
                        r=remoteCdSapSalePriceInfoService.updateByMarketingOrganizationAndMaterialCode(cdSapSalePrice);
                    }
                    if (!r.isSuccess()) {
                        error.append(StrUtil.format("销售组织：{},专用号：{}，数据更新错误！", outTableOutput.getString("VKORG"), outTableOutput.getString("MATNR")));
                        log.error(StrUtil.format("销售组织：{},专用号：{}，数据更新错误！",outTableOutput.getString("VKORG"),outTableOutput.getString("MATNR")));
                    }
                }
            }
        } catch (Exception e) {
            log.error(StrUtil.format("SAP返回错误信息：{}",e.getMessage()));
            throw new BusinessException(StrUtil.format("SAP返回错误信息：{}",e.getMessage()));
        }finally {
            sysInterfaceLog.setCreateBy("定时任务");
            sysInterfaceLog.setCreateTime(date);
            sysInterfaceLog.setRemark("定时任务获取SAP销售价格");
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
        if(StrUtil.isEmpty(error)){
            return R.ok();
        }else {
            throw new BusinessException(error.toString());
        }

    }

    /**
     * 传SAP261
     * @param smsScrapOrder
     * @return
     */
    @Override
    public R autidSuccessToSAP261(SmsScrapOrder smsScrapOrder) {
        Date date = DateUtil.date();
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName(SapConstants.ZESP_IM_001).build();
        String productMaterialCode = smsScrapOrder.getProductMaterialCode();//传SAP专用号
        //成品报废库位默认0088，如果0088没有库存就选择0188
        String lgort = "0088";
        CdProductWarehouse cdProductWarehouse = new CdProductWarehouse().builder()
                .productMaterialCode(productMaterialCode)
                .productFactoryCode(smsScrapOrder.getFactoryCode())
                .storehouse(lgort).build();
        R rWare = remoteCdProductWarehouseService.queryOneByExample(cdProductWarehouse);
        if (rWare.isSuccess()) {
            cdProductWarehouse = rWare.getData(CdProductWarehouse.class);
            if (cdProductWarehouse.getWarehouseNum() == null || cdProductWarehouse.getWarehouseNum().compareTo(BigDecimal.ZERO) <= 0) {
                lgort = "0188";
            }
        }else{
            lgort = "0188";
        }
        String yearMouth = DateFormatUtils.format(date, "yyyyMM");
        R rNo = remoteCdScrapMonthNoService.findOne(yearMouth,smsScrapOrder.getFactoryCode());
        if (!rNo.isSuccess()) {
            throw new BusinessException("请维护本月订单号！");
        }
        CdScrapMonthNo cdScrapMonthNo = rNo.getData(CdScrapMonthNo.class);

        //发送SAP
        JCoDestination destination =null;
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZESP_IM_001);
            if (fm == null) {
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            JCoParameterList input = fm.getImportParameterList();
            input.setValue("FLAG_GZ","1");
            //获取输入参数
            JCoTable inputTable = fm.getTableParameterList().getTable("T_INPUT");
            //附加表的最后一个新行,行指针,它指向新添加的行。
            inputTable.appendRow();
            inputTable.setValue("BWARTWA","261");//移动类型（库存管理）  261/Y61
            inputTable.setValue("BKTXT", StrUtil.concat(true,smsScrapOrder.getSupplierCode(),smsScrapOrder.getScrapNo()));//凭证抬头文本  V码+报废单号
            inputTable.setValue("WERKS", smsScrapOrder.getFactoryCode());//工厂
            inputTable.setValue("LGORT", lgort);//库存地点
            inputTable.setValue("MATNR", productMaterialCode.toUpperCase());//物料号
            inputTable.setValue("ERFME", smsScrapOrder.getMeasureUnit());//基本计量单位
            inputTable.setValue("ERFMG", smsScrapOrder.getScrapAmount());//数量
            inputTable.setValue("AUFNR", cdScrapMonthNo.getOrderNo());//每月维护一次订单号
            String content = StrUtil.format("BWARTWA:{},BKTXT:{},WERKS:{},LGORT:{},MATNR:{}" +
                    ",ERFME:{},ERFMG:{},AUFNR:{}","261",
                    StrUtil.concat(true,smsScrapOrder.getSupplierCode(),smsScrapOrder.getScrapNo()),
                    smsScrapOrder.getFactoryCode(),lgort,productMaterialCode,smsScrapOrder.getMeasureUnit(),
                    smsScrapOrder.getScrapAmount(),cdScrapMonthNo.getOrderNo());
            sysInterfaceLog.setContent(content);
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("T_MESSAGE");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    if(SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(outTableOutput.getString("FLAG"))){
                        //获取成功
                        smsScrapOrder.setPostingNo(outTableOutput.getString("MBLNR"));
                        smsScrapOrder.setSapTransDate(date);
                        smsScrapOrder.setSapRemark(outTableOutput.getString("MESSAGE"));
                        smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode());
                        updateByPrimaryKeySelective(smsScrapOrder);
                    }else {
                        //获取失败
                        sysInterfaceLog.setResults(StrUtil.format("SAP返回错误信息：{}",outTableOutput.getString("MESSAGE")));
                        return R.error(StrUtil.format("发送SAP失败！原因：{}",outTableOutput.getString("MESSAGE")));
                    }
                }
            }
        } catch (JCoException e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            return R.error(e.getMessage());
        }finally {
            sysInterfaceLog.setDelFlag("0");
            sysInterfaceLog.setCreateBy("定时任务");
            sysInterfaceLog.setCreateTime(date);
            sysInterfaceLog.setRemark("定时任务报废审核通过传SAP261");
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
        return R.ok();
    }
}
