package com.cloud.settle.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.CurrencyEnum;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.mapper.SmsSupplementaryOrderMapper;
import com.cloud.settle.service.ISmsSupplementaryOrderService;
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
    private RemoteBomService remoteBomService;
    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;
    @Autowired
    private RemoteFactoryLineInfoService remotefactoryLineInfoService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteMaterialService remoteMaterialService;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteCdMouthRateService remoteCdMouthRateService;
    @Autowired
    private RemoteSettleRatioService remoteSettleRatioService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    /**
     * 编辑保存物耗申请单功能  --有逻辑校验
     * @param smsSupplementaryOrder
     * @return
     */
    @Override
    @Transactional
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
    @Transactional
    public R editSaveList(List<SmsSupplementaryOrder> smsSupplementaryOrders) {
        smsSupplementaryOrders.forEach(smsSupplementaryOrder ->{
            R r = editSave(smsSupplementaryOrder);
            if(!r.isSuccess()){
                throw new BusinessException(r.getStr("msg"));
            }
        });
        return R.ok();
    }

    /**
     * 删除物耗申请单
     * @param ids
     * @return
     */
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

    /**
     * 新增保存物耗申请单
     * @param smsSupplementaryOrder
     * @return id
     */
    @Override
    @Transactional
    public R addSave(SmsSupplementaryOrder smsSupplementaryOrder) {
        log.info(StrUtil.format("物耗申请新增保存开始：参数为{}", smsSupplementaryOrder.toString()));
        String productOrderCode = smsSupplementaryOrder.getProductOrderCode();
        //校验
        R rCheck = checkSmsSupplementaryOrderCondition(smsSupplementaryOrder, productOrderCode);
        if (!rCheck.isSuccess()) {
            return rCheck;
        }
        //生产单号获取排产订单信息
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if(!omsProductionOrderResult.isSuccess()){
            log.error("根据生产单号获取排产订单信息异常 productOrderCode:{},res:{}",productOrderCode, JSONObject.toJSON(omsProductionOrderResult));
            throw new BusinessException(omsProductionOrderResult.get("msg").toString());
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
        String productMaterialCode = omsProductionOrder.getProductMaterialCode();
        String rawMaterialCode = smsSupplementaryOrder.getRawMaterialCode();

        //开始插入
        R seqResult = remoteSequeceService.selectSeq("supplementary_seq", 4);
        if(!seqResult.isSuccess()){
            throw new BusinessException("获取序列号失败");
        }
        String seq = seqResult.getStr("data");
        StringBuffer stuffNo = new StringBuffer();
        //WH+年月日+4位顺序号
        stuffNo.append("WH").append(DateUtils.dateTime()).append(seq);
        smsSupplementaryOrder.setStuffNo(stuffNo.toString());
        //根据线体号查询供应商编码
        R rFactory = remotefactoryLineInfoService.selectInfoByCodeLineCode(omsProductionOrder.getProductLineCode(),
                                                omsProductionOrder.getProductFactoryCode());
        if (!rFactory.isSuccess()) {
            return rFactory;
        }
        CdFactoryLineInfo factoryLineInfo = rFactory.getData(CdFactoryLineInfo.class);
        if (factoryLineInfo != null) {
            smsSupplementaryOrder.setSupplierCode(factoryLineInfo.getSupplierCode());
            smsSupplementaryOrder.setSupplierName(factoryLineInfo.getSupplierDesc());
        }
        smsSupplementaryOrder.setFactoryCode(omsProductionOrder.getProductFactoryCode());
        R rFactoryInfo= remoteFactoryInfoService.selectOneByFactory(omsProductionOrder.getProductFactoryCode());
        if(!rFactoryInfo.isSuccess()){
            log.error(StrUtil.format("(物耗)物耗申请新增保存开始：公司信息为空参数为{}", omsProductionOrder.getProductFactoryCode()));
            return R.error("公司信息为空！");
        }
        CdFactoryInfo cdFactoryInfo = rFactoryInfo.getData(CdFactoryInfo.class);

        smsSupplementaryOrder.setCompanyCode(cdFactoryInfo.getCompanyCode());
        smsSupplementaryOrder.setProductOrderCode(omsProductionOrder.getProductOrderCode());//生产订单号
        if (StrUtil.isBlank(smsSupplementaryOrder.getStuffStatus())) {
            smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DTJ.getCode());//状态：待提交
        }
        //改为月度结算时候取值
//        smsSupplementaryOrder.setStuffPrice(cdMaterialPriceInfo.getNetWorth());//单价  取得materialPrice表的净价值
//        smsSupplementaryOrder.setStuffUnit(cdMaterialPriceInfo.getUnit());
//        smsSupplementaryOrder.setCurrency(cdMaterialPriceInfo.getCurrency());//币种
        R rBom = remoteBomService.listByProductAndMaterial(productMaterialCode, rawMaterialCode);
        if (!rBom.isSuccess()) {
            return rBom;
        }
        CdBomInfo cdBom = rBom.getData(CdBomInfo.class);
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
     * 多条增加
     * @param smsSupplementaryOrders
     * @return
     */
    @Override
    @Transactional
    public R addSaveList(List<SmsSupplementaryOrder> smsSupplementaryOrders) {
        smsSupplementaryOrders.forEach(smsSupplementaryOrder ->{
            R r = addSave(smsSupplementaryOrder);
            if(!r.isSuccess()){
                throw new BusinessException(r.getStr("msg"));
            }
        });
        return R.ok();
    }

    /**
     * 根据月份和状态查询
     * @param month
     * @param stuffStatus
     * @return
     */
    @Override
    public List<SmsSupplementaryOrder> selectByMonthAndStatus(String month, List<String> stuffStatus) {
        return smsSupplementaryOrderMapper.selectByMonthAndStatus(month,stuffStatus);
    }

    /**
     * 定时任务更新指定月份原材料价格到物耗表
     * @return
     */
    @Override
    @Transactional
    public R updatePriceEveryMonth(String month) {
        //查询指定月、待结算的物耗申请中的物料号  用途是查询SAP成本价 更新到物耗表
        List<String> materialCodeList = smsSupplementaryOrderMapper.selectMaterialByMonthAndStatus(month, CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode()));
        Map<String, CdMaterialPriceInfo> mapMaterialPrice = new ConcurrentHashMap<>();
        if (materialCodeList != null) {
            log.info(StrUtil.format("(定时任务)物耗申请需要更新成本价格的物料号:{}", materialCodeList.toString()));
            String now = DateUtil.now();
            String materialCodeStr = StrUtil.join(",", materialCodeList);
            //根据前面查出的物料号查询SAP成本价 map key:物料号  value:CdMaterialPriceInfo
            mapMaterialPrice = remoteCdMaterialPriceInfoService.selectPriceByInMaterialCodeAndDate(materialCodeStr, now, now);
        }
        //查询指定月汇率
        R rRate = remoteCdMouthRateService.findRateByYearMouth(month);
        if (!rRate.isSuccess()) {
            throw new BusinessException(StrUtil.format("{}月份未维护费率", month));
        }
        BigDecimal rate = new BigDecimal(rRate.get("data").toString());//汇率
        //物耗索赔系数
        CdSettleRatio cdSettleRatioWH = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_WH.getCode());
        if (cdSettleRatioWH == null) {
            log.error("物耗索赔系数未维护！");
            throw new BusinessException("物耗索赔系数未维护！");
        }
        //取得计算月份、待结算的物耗申请数据
        List<SmsSupplementaryOrder> smsSupplementaryOrderList = selectByMonthAndStatus(month, CollUtil.newArrayList(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode()));
        //循环物耗，更新成本价格，计算索赔金额
        if (smsSupplementaryOrderList != null) {
            for (SmsSupplementaryOrder smsSupplementaryOrder : smsSupplementaryOrderList) {
                CdMaterialPriceInfo cdMaterialPriceInfo = mapMaterialPrice.get(smsSupplementaryOrder.getRawMaterialCode()+smsSupplementaryOrder.getPurchaseGroupCode());
                if (cdMaterialPriceInfo == null) {
                    //如果没有找到SAP价格，则更新备注
                    log.info(StrUtil.format("(月度结算定时任务)SAP价格未同步的物料号:{}", smsSupplementaryOrder.getRawMaterialCode()));
                    smsSupplementaryOrder.setRemark("SAP价格未同步！");
                    updateByPrimaryKeySelective(smsSupplementaryOrder);
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
                if (CurrencyEnum.CURRENCY_USD.getCode().equals(smsSupplementaryOrder.getCurrency())) {
                    //如果是美元，还要*汇率
                    spPrice = spPrice.multiply(rate);
                    smsSupplementaryOrder.setRate(rate);
                }
                smsSupplementaryOrder.setSettleFee(spPrice);
            }
            //更新
            updateBatchByPrimaryKeySelective(smsSupplementaryOrderList);
        }
        return R.ok();
    }

    /**
     * 小微主审批通过传SAPY61
     * @param smsSupplementaryOrder
     * @return
     */
    @Override
    public R autidSuccessToSAPY61(SmsSupplementaryOrder smsSupplementaryOrder) {
        Date date = DateUtil.date();
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName(SapConstants.ZESP_IM_001).build();
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
            inputTable.setValue("BWARTWA","Y61");//移动类型（库存管理）  261/Y61
            inputTable.setValue("BKTXT", StrUtil.concat(true,smsSupplementaryOrder.getSupplierCode(),smsSupplementaryOrder.getStuffNo()));//凭证抬头文本  V码+物耗单号
            inputTable.setValue("WERKS", smsSupplementaryOrder.getFactoryCode());//工厂
            inputTable.setValue("LGORT", "0088");//库存地点 成品报废库位默认0088，如果0088没有库存就选择0188
            inputTable.setValue("MATNR", smsSupplementaryOrder.getRawMaterialCode());//物料号
            inputTable.setValue("ERFME", smsSupplementaryOrder.getStuffUnit());//基本计量单位
            inputTable.setValue("ERFMG", smsSupplementaryOrder.getStuffAmount());//数量
            inputTable.setValue("AUFNR", smsSupplementaryOrder.getProductOrderCode());//生产订单号
            String content = StrUtil.format("BWARTWA:{},BKTXT:{},WERKS:{},LGORT:{},MATNR:{}" +
                            ",ERFME:{},ERFMG:{},AUFNR:{}","261",
                    StrUtil.concat(true,smsSupplementaryOrder.getSupplierCode(),smsSupplementaryOrder.getStuffNo()),
                    smsSupplementaryOrder.getFactoryCode(),"0088",smsSupplementaryOrder.getRawMaterialCode(),
                    smsSupplementaryOrder.getStuffUnit(),smsSupplementaryOrder.getStuffAmount(),smsSupplementaryOrder.getProductOrderCode());
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
                        smsSupplementaryOrder.setPostingNo(outTableOutput.getString("MBLNR"));
                        smsSupplementaryOrder.setSapStoreage("0088");
                        smsSupplementaryOrder.setSapResult(outTableOutput.getString("FLAG"));
                        smsSupplementaryOrder.setSapDate(date);
                        smsSupplementaryOrder.setSapRemark(outTableOutput.getString("MESSAGE"));
                        smsSupplementaryOrder.setStuffStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode());
                        return R.data(smsSupplementaryOrder);
                    }else {
                        //获取失败
                        sysInterfaceLog.setResults(StrUtil.format("SAP返回错误信息：{}",outTableOutput.getString("MESSAGE")));
                        throw new BusinessException(StrUtil.format("发送SAP失败！原因：{}",outTableOutput.getString("MESSAGE")));
                    }
                }
            }
        } catch (JCoException e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }finally {
            sysInterfaceLog.setDelFlag("0");
            sysInterfaceLog.setCreateBy("定时任务");
            sysInterfaceLog.setCreateTime(date);
            sysInterfaceLog.setRemark("定时任务物耗审核通过传SAP261");
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
        return R.ok();
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
//        R r = remoteCdMaterialPriceInfoService.checkSynchroSAP(smsSupplementaryOrder.getRawMaterialCode());
//        if (!r.isSuccess()) {
//            return r;
//        }
        //将返回值Map转为CdMaterialPriceInfo
//        CdMaterialPriceInfo cdMaterialPriceInfo = BeanUtil.mapToBean((Map<?, ?>) r.get("data"), CdMaterialPriceInfo.class, true);
        //2、校验修改申请数量是否是最小包装量的整数倍 CdMaterialInfo cdMaterialInfo
        R cdMaterialInfoResult = remoteMaterialService.getByMaterialCode(smsSupplementaryOrder.getRawMaterialCode());
        if (!cdMaterialInfoResult.isSuccess()) {
            log.error(StrUtil.format("(物耗)未维护物料信息{}", smsSupplementaryOrder.getRawMaterialCode()));
            return R.error("未维护物料信息！");
        }
        CdMaterialInfo cdMaterialInfo = cdMaterialInfoResult.getData(CdMaterialInfo.class);
        int applyNum = smsSupplementaryOrder.getStuffAmount();//申请量
        //最小包装量
        Double minUnit = Double.valueOf(cdMaterialInfo.getRoundingQuantit() == null ? "0" : cdMaterialInfo.getRoundingQuantit().toString());
        if (minUnit == 0) {
            return R.error(StrUtil.format("{}最小包装量不正确！",smsSupplementaryOrder.getRawMaterialCode()));
        }
        if (applyNum % minUnit != 0) {
            log.error(StrUtil.format("(物耗)申请量必须是最小包装量的整数倍参数为{},{}", applyNum,minUnit));
            return R.error(StrUtil.format("{}申请量必须是最小包装量的整数倍！",smsSupplementaryOrder.getRawMaterialCode()));
        }
        //3、校验申请数量是否是单耗的整数倍
        //生产单号获取排产订单信息
        R omsProductionOrderResult = remoteProductionOrderService.selectByProdctOrderCode(productOrderCode);
        if (!omsProductionOrderResult.isSuccess()) {
            log.error(StrUtil.format("(物耗)排产订单信息不存在!排产订单号参数为{}", productOrderCode));
            return R.error(StrUtil.format("{}排产订单信息不存在！",productOrderCode));
        }
        OmsProductionOrder omsProductionOrder = omsProductionOrderResult.getData(OmsProductionOrder.class);
////        //根据成品物料号和原材料物料号取bom单耗
        String productMaterialCode = omsProductionOrder.getProductMaterialCode();
        String rawMaterialCode = smsSupplementaryOrder.getRawMaterialCode();
//        R rBomNum = remoteBomService.checkBomNum(productMaterialCode, rawMaterialCode, applyNum);
//        if (!rBomNum.isSuccess()) {
//            return rBomNum;
//        }
        R rBom = remoteBomService.listByProductAndMaterial(productMaterialCode, rawMaterialCode);
        if (!rBom.isSuccess()) {
            return R.error("BOM信息为空！");
        }
        CdBomInfo cdBom = rBom.getData(CdBomInfo.class);
        if (cdBom.getBomNum() == null) {
            return R.error("BOM单耗信息为空！");
        }
        //5、校验申请量是否大于订单量*单耗,如果大于单耗，则判断超出部分是否大于最小包装量，大于则返回错误
        BigDecimal productNum = omsProductionOrder.getProductNum();
        BigDecimal applyNumBig = new BigDecimal(applyNum);
        if (applyNumBig.compareTo(productNum.multiply(cdBom.getBomNum())) >= 0) {
            //差值
            BigDecimal sub = applyNumBig.subtract(productNum.multiply(cdBom.getBomNum()));
            if (sub.compareTo(new BigDecimal(minUnit)) > 0) {
                R.error(StrUtil.format("{}申请量大于订单量*单耗时，超出部分不得大于最小包装量！",productOrderCode));
            }
            return R.error(StrUtil.format("{}申请量不得大于订单量*单耗",productOrderCode));
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

    /**
     * 根据状态查物料号
     * @param status
     * @return 物料号集合
     */
    @Override
    public List<String> materialCodeListByStatus(String status) {
        return smsSupplementaryOrderMapper.materialCodeListByStatus(status);
    }
}