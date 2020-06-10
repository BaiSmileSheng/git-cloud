package com.cloud.settle.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import com.cloud.settle.enums.CurrencyEnum;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.mapper.SmsScrapOrderMapper;
import com.cloud.settle.service.ISmsScrapOrderService;
import com.cloud.system.domain.entity.*;
import com.cloud.system.enums.SettleRatioEnum;
import com.cloud.system.feign.*;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
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
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private RemoteSettleRatioService remoteSettleRatioService;
    @Autowired
    private RemoteCdSapSalePriceInfoService remoteCdSapSalePriceInfoService;
    @Autowired
    private RemoteCdMouthRateService remoteCdMouthRateService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;


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
    @Transactional
    public R addSave(SmsScrapOrder smsScrapOrder) {
        log.info(StrUtil.format("报废申请新增保存开始：参数为{}", smsScrapOrder.toString()));
        //生产订单号
        String productOrderCode = smsScrapOrder.getProductOrderCode();
        //校验
        R rCheck = checkScrapOrderCondition(smsScrapOrder,productOrderCode);
        if (!rCheck.isSuccess()) {
            return rCheck;
        }

        String seq = remoteSequeceService.selectSeq("scrap_seq", 4);
        StringBuffer scrapNo = new StringBuffer();
        //WH+年月日+4位顺序号
        scrapNo.append("BF").append(DateUtils.dateTime()).append(seq);
        smsScrapOrder.setScrapNo(scrapNo.toString());
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        smsScrapOrder.setMachiningPrice(omsProductionOrder.getProcessCost());
        //根据线体号查询供应商编码
        CdFactoryLineInfo factoryLineInfo = remotefactoryLineInfoService.selectInfoByCodeLineCode(omsProductionOrder.getProductLineCode());
        if (factoryLineInfo != null) {
            smsScrapOrder.setSupplierCode(factoryLineInfo.getSupplierCode());
            smsScrapOrder.setSupplierName(factoryLineInfo.getSupplierDesc());
        }
        smsScrapOrder.setFactoryCode(omsProductionOrder.getFactoryCode());
        CdFactoryInfo cdFactoryInfo = remoteFactoryInfoService.selectOneByFactory(omsProductionOrder.getFactoryCode());
        if (cdFactoryInfo == null) {
            log.error(StrUtil.format("(报废)报废申请新增保存开始：公司信息为空参数为{}", omsProductionOrder.getFactoryCode()));
            return R.error("公司信息为空！");
        }
        smsScrapOrder.setCompanyCode(cdFactoryInfo.getCompanyCode());
        if (StrUtil.isBlank(smsScrapOrder.getScrapStatus())) {
            smsScrapOrder.setScrapStatus(ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode());
        }
        smsScrapOrder.setDelFlag("0");
//        smsScrapOrder.setCreateBy(sysUser.getLoginName());
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
        int applyNum = smsScrapOrder.getScrapAmount();//申请量
        //生产单号获取排产订单信息
        OmsProductionOrder omsProductionOrder = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if (omsProductionOrder == null) {
            return R.error("订单信息不存在！");
        }
        //5、校验申请量是否大于订单量
        BigDecimal productNum = omsProductionOrder.getProductNum();
        if (new BigDecimal(applyNum).compareTo(productNum) > 0) {
            return R.error("申请量不得大于订单量");
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
    @Transactional
    public R updatePriceEveryMonth(String month) {
        //报废索赔系数
        CdSettleRatio cdSettleRatioBF = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_BF.getCode());
        if (cdSettleRatioBF == null) {
            log.error("(月度结算定时任务)报废索赔系数未维护！");
            throw new BusinessException("报废索赔系数未维护！");
        }
        //查询指定月汇率
        R rRate = remoteCdMouthRateService.findRateByYearMouth(month);
        if (!rRate.isSuccess()) {
            throw new BusinessException(StrUtil.format("{}月份未维护费率", month));
        }
        BigDecimal rate = new BigDecimal(rRate.get("data").toString());//汇率
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
                smsScrapOrder.setMaterialPrice(new BigDecimal(cdSapSalePrice.getSalePrice()));
                smsScrapOrder.setScrapPrice(smsScrapOrder.getMaterialPrice().multiply(new BigDecimal(smsScrapOrder.getScrapAmount())));
                //索赔金额=（Sap成品物料销售价格*报废数量*报废索赔系数）+（报废数量*生产订单加工费单价）
                BigDecimal scrapPrice ;//索赔金额
                BigDecimal scrapAmount = new BigDecimal(smsScrapOrder.getScrapAmount());//报废数量
                BigDecimal materialPrice = smsScrapOrder.getMaterialPrice();//成品物料销售价格

                BigDecimal ratio = cdSettleRatioBF.getRatio();//报废索赔系数
                BigDecimal machiningPrice = smsScrapOrder.getMachiningPrice();//加工费单价
                scrapPrice = (materialPrice.multiply(scrapAmount.multiply(ratio))).add(scrapAmount.multiply(machiningPrice));
                if (CurrencyEnum.CURRENCY_USD.getCode().equals(smsScrapOrder.getCurrency())) {
                    //如果是美元，还要*汇率
                    scrapPrice = scrapPrice.multiply(rate);
                }
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
//    @GlobalTransactional
    public R updateSAPPriceEveryMonth(String month) {
        //查询上个月、待结算的物耗申请中的物料号，公司编号
        List<Map<String,String>> materialCodeComCodeList = smsScrapOrderMapper.selectMaterialAndCompanyCodeGroupBy(month, CollUtil.newArrayList(ScrapOrderStatusEnum.BF_ORDER_STATUS_DJS.getCode()));
        JCoDestination destination;
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName("ZSD_INT_DDPS_01")
                .content(CollUtil.join(materialCodeComCodeList, "#")).build();
        Date date = DateUtil.date();
        StringBuffer error = new StringBuffer();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP600);
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
                inputTable.setValue("MATNR", stringStringMap.get("materialCode"));
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
                            .beginDate(outTableOutput.getDate("DATBI"))
                            .endDate(outTableOutput.getDate("DATAB"))
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
            return R.error(error.toString());
        }

    }
}
