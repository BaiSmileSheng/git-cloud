package com.cloud.settle.service.impl;

    import cn.hutool.core.bean.BeanUtil;
    import cn.hutool.core.collection.CollectionUtil;
    import cn.hutool.core.date.DateUtil;
    import cn.hutool.core.util.StrUtil;
    import com.cloud.common.constant.SapConstants;
    import com.cloud.common.core.domain.R;
    import com.cloud.common.exception.BusinessException;
    import com.cloud.common.utils.DateUtils;
    import com.cloud.common.utils.StringUtils;
    import com.cloud.settle.converter.RawScrapOrderStatusConverter;
    import com.cloud.settle.domain.entity.SmsScrapOrder;
    import com.cloud.settle.enums.RawScrapOrderIsCheckEnum;
    import com.cloud.settle.enums.RawScrapOrderIsMaterialObjectEnum;
    import com.cloud.settle.enums.RawScrapOrderStatusEnum;
    import com.cloud.settle.enums.ScrapOrderStatusEnum;
    import com.cloud.system.domain.entity.*;
    import com.cloud.system.enums.SettleRatioEnum;
    import com.cloud.system.feign.*;
    import com.fasterxml.jackson.core.type.TypeReference;
    import com.sap.conn.jco.*;
    import lombok.extern.slf4j.Slf4j;
    import org.apache.commons.lang3.time.DateFormatUtils;
    import org.apache.poi.ss.formula.functions.T;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsRawMaterialScrapOrderMapper;
import com.cloud.settle.domain.entity.SmsRawMaterialScrapOrder;
import com.cloud.settle.service.ISmsRawMaterialScrapOrderService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
    import tk.mybatis.mapper.entity.Example;

    import java.math.BigDecimal;
    import java.util.Date;
    import java.util.List;
    import java.util.Map;
    import java.util.stream.Collectors;

/**
 * 原材料报废申请Service业务层处理
 *
 * @author ltq
 * @date 2020-12-07
 */
@Service
@Slf4j
public class SmsRawMaterialScrapOrderServiceImpl extends BaseServiceImpl<SmsRawMaterialScrapOrder> implements ISmsRawMaterialScrapOrderService {
    @Autowired
    private SmsRawMaterialScrapOrderMapper smsRawMaterialScrapOrderMapper;
    @Autowired
    private RemoteMaterialService remoteMaterialService;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private RemoteCdScrapMonthNoService remoteCdScrapMonthNoService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteSettleRatioService remoteSettleRatioService;

    private static final String SAVE = "0";
    private static final String SUBMIT = "1";

    /**
     * 原材料报废新增
     * @author ltq
     * @date 2020-12-07
     */
    @Override
    public R insetRawScrap(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder, SysUser sysUser) {
        log.info("=============原材料报废申请-新增方法开始===============");
        //数据字段非空校验
        R checkMap = checkNotNull(smsRawMaterialScrapOrder);
        if (!checkMap.isSuccess()) {
            log.error("执行数据字段非空校验方法返回错误："+checkMap.get("msg"));
            throw new BusinessException(checkMap.getStr("msg"));
        }
        //数据字段合规校验
        R checkDataMap = checkData(smsRawMaterialScrapOrder);
        if (!checkDataMap.isSuccess()) {
            log.error("执行数据字段正确性校验方法返回错误："+checkDataMap.get("msg"));
            throw new BusinessException(checkDataMap.getStr("msg"));
        }
        //组织补充记录字段数据
        R seqResult = remoteSequeceService.selectSeq("scrap_seq", 4);
        if(!seqResult.isSuccess()){
            throw new BusinessException("获取序列号失败");
        }
        String seq = seqResult.getStr("data");
        StringBuffer scrapNo = new StringBuffer();
        //WH+年月日+4位顺序号
        scrapNo.append("YCLBF").append(DateUtils.dateTime()).append(seq);
        smsRawMaterialScrapOrder.setRawScrapNo(scrapNo.toString());
        //校验+获取工厂信息
        R factoryMap = remoteFactoryInfoService.selectOneByFactory(smsRawMaterialScrapOrder.getFactoryCode());
        if (!factoryMap.isSuccess()) {
            log.error("查询"+smsRawMaterialScrapOrder.getFactoryCode()+"工厂信息失败，原因："+factoryMap.get("msg"));
            return R.error("查询"+smsRawMaterialScrapOrder.getFactoryCode()+"工厂信息失败，原因："+factoryMap.get("msg"));
        }
        CdFactoryInfo factoryInfo = factoryMap.getData(CdFactoryInfo.class);
        //获取匹配月度报废订单号
        String yearMonth = DateUtils.dateFormat(new Date(),"yyyyMM");
        R scrapMonthNoMap = remoteCdScrapMonthNoService.findOne(yearMonth,smsRawMaterialScrapOrder.getFactoryCode());
        if (!scrapMonthNoMap.isSuccess()) {
            log.error("获取匹配月度报废订单号失败，原因："+scrapMonthNoMap.get("msg"));
            return R.error("获取匹配月度报废订单号失败!");
        }
        CdScrapMonthNo cdScrapMonthNo = scrapMonthNoMap.getData(CdScrapMonthNo.class);
        if (!BeanUtil.isNotEmpty(cdScrapMonthNo) || !StrUtil.isNotBlank(cdScrapMonthNo.getOrderNo())) {
            log.error("月度报废订单号为空！");
            return R.error("月度报废订单号为空！");
        }
        smsRawMaterialScrapOrder.setScrapOrderCode(cdScrapMonthNo.getOrderNo());
        smsRawMaterialScrapOrder.setCreateBy(sysUser.getLoginName());
        smsRawMaterialScrapOrder.setCreateTime(new Date());
        smsRawMaterialScrapOrder.setComponyCode(factoryInfo.getCompanyCode());
        smsRawMaterialScrapOrder.setSupplierCode(sysUser.getSupplierCode());
        smsRawMaterialScrapOrder.setSupplierName(sysUser.getSupplierName());
        R insertCheckMap = insertRawScrapCheck(smsRawMaterialScrapOrder);
        if (!insertCheckMap.isSuccess()) {
            log.error("新增原材料报废失败，原因："+insertCheckMap.get("msg"));
            throw new BusinessException("新增原材料报废失败，原因："+insertCheckMap.get("msg"));
        }
        return R.ok();
    }
    public R insertRawScrapCheck(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder){
        if (SAVE.equals(smsRawMaterialScrapOrder.getSaveOrSubmit())) { //保存
            smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DTJ.getCode());
            smsRawMaterialScrapOrderMapper.insert(smsRawMaterialScrapOrder);
        } else { //提交
            //判断是否买单
            if (RawScrapOrderIsCheckEnum.YCLBF_ORDER_IS_CHECK_TRUE.getCode().equals(smsRawMaterialScrapOrder.getIsCheck())) {
                //买单,待结算
                smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DJS.getCode());
                //传SAP系统
                R sapMap = autidSuccessToSAP261(smsRawMaterialScrapOrder);
                smsRawMaterialScrapOrderMapper.insert(smsRawMaterialScrapOrder);
                if (!sapMap.isSuccess()) {
                    log.error("原材料报废订单传SAP系统失败，原因："+sapMap.get("msg"));
                    throw new BusinessException("原材料报废订单传SAP系统失败，原因："+sapMap.get("msg"));
                }
            } else {
                //进入审批流
                smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_YWKSH.getCode());
                smsRawMaterialScrapOrderMapper.insert(smsRawMaterialScrapOrder);
            }
        }
        return R.ok();
    }
    public R updateRawScrapCheck(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder){
        if (SAVE.equals(smsRawMaterialScrapOrder.getSaveOrSubmit())) { //保存
            smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DTJ.getCode());
            smsRawMaterialScrapOrderMapper.updateByPrimaryKey(smsRawMaterialScrapOrder);
        } else { //提交
            //判断是否买单
            if (RawScrapOrderIsCheckEnum.YCLBF_ORDER_IS_CHECK_TRUE.getCode().equals(smsRawMaterialScrapOrder.getIsCheck())) {
                //买单,待结算
                smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DJS.getCode());
                //传SAP系统
                R sapMap = autidSuccessToSAP261(smsRawMaterialScrapOrder);
                smsRawMaterialScrapOrderMapper.updateByPrimaryKey(smsRawMaterialScrapOrder);
                if (!sapMap.isSuccess()) {
                    log.error("原材料报废订单传SAP系统失败，原因："+sapMap.get("msg"));
                    throw new BusinessException("原材料报废订单传SAP系统失败，原因："+sapMap.get("msg"));
                }
            } else {
                //进入审批流
                smsRawMaterialScrapOrder.setScrapStatus(RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_YWKSH.getCode());
                smsRawMaterialScrapOrderMapper.updateByPrimaryKey(smsRawMaterialScrapOrder);
            }
        }
        return R.ok();
    }
    /**
     * 传SAP261
     * @param smsRawMaterialScrapOrder
     * @return
     */
    @Override
    public R autidSuccessToSAP261(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder) {

        Date date = DateUtil.date();
        SysInterfaceLog sysInterfaceLog = SysInterfaceLog.builder()
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
            inputTable.setValue("BWARTWA","261");//移动类型（库存管理）  261/Y61
            inputTable.setValue("BKTXT", StrUtil.concat(true,smsRawMaterialScrapOrder.getSupplierCode(),smsRawMaterialScrapOrder.getRawScrapNo()));//凭证抬头文本  V码+报废单号
            inputTable.setValue("WERKS", smsRawMaterialScrapOrder.getFactoryCode());//工厂
            inputTable.setValue("LGORT", smsRawMaterialScrapOrder.getStation());//库存地点
            inputTable.setValue("MATNR", smsRawMaterialScrapOrder.getRawMaterialCode().toUpperCase());//物料号
            inputTable.setValue("ERFME", smsRawMaterialScrapOrder.getMeasureUnit());//基本计量单位
            inputTable.setValue("ERFMG", smsRawMaterialScrapOrder.getScrapNum());//数量
            inputTable.setValue("AUFNR", smsRawMaterialScrapOrder.getRawScrapNo());//每月维护一次订单号
            String content = StrUtil.format("BWARTWA:{},BKTXT:{},WERKS:{},LGORT:{},MATNR:{}" +
                            ",ERFME:{},ERFMG:{},AUFNR:{}","261",
                    StrUtil.concat(true,smsRawMaterialScrapOrder.getSupplierCode(),smsRawMaterialScrapOrder.getRawScrapNo()),
                    smsRawMaterialScrapOrder.getFactoryCode(),smsRawMaterialScrapOrder.getStation(),smsRawMaterialScrapOrder.getRawMaterialCode(),
                    smsRawMaterialScrapOrder.getMeasureUnit(),smsRawMaterialScrapOrder.getScrapNum(),smsRawMaterialScrapOrder.getScrapOrderCode());
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
                        smsRawMaterialScrapOrder.setPostingNo(outTableOutput.getString("MBLNR"));
                        smsRawMaterialScrapOrder.setSapTransDate(date);
                        smsRawMaterialScrapOrder.setSapRemark(outTableOutput.getString("MESSAGE"));
                        updateByPrimaryKeySelective(smsRawMaterialScrapOrder);
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
            sysInterfaceLog.setRemark("定时任务原材料报废传SAP261");
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
        return R.ok();
    }
    /**
     * 更新修改原材料报废订单
     * @param smsRawMaterialScrapOrder
     * @return
     */
    @Override
    public R editRawScrap(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder, SysUser sysUser) {
        log.info("==============原材料报废订单修改方法================");
        if (!RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DTJ.getCode().equals(smsRawMaterialScrapOrder.getScrapStatus())) {
            return R.error("只能修改[待提交]状态的原材料报废订单！");
        }
        //数据字段非空校验
        R checkNotNullMap = checkNotNull(smsRawMaterialScrapOrder);
        if (!checkNotNullMap.isSuccess()) {
            log.error("执行数据字段非空校验方法返回错误："+checkNotNullMap.get("msg"));
            throw new BusinessException(checkNotNullMap.getStr("msg"));
        }
        //数据字段合规校验
        R checkDataMap = checkData(smsRawMaterialScrapOrder);
        if (!checkDataMap.isSuccess()) {
            log.error("执行数据字段正确性校验方法返回错误："+checkDataMap.get("msg"));
            throw new BusinessException(checkDataMap.getStr("msg"));
        }
        //查询原来数据
        SmsRawMaterialScrapOrder rawMaterialScrapOrder =
                smsRawMaterialScrapOrderMapper.selectByPrimaryKey(smsRawMaterialScrapOrder);
        if (!rawMaterialScrapOrder.getFactoryCode().equals(smsRawMaterialScrapOrder.getFactoryCode())) {
            //校验+获取工厂信息
            R factoryInfoMap = remoteFactoryInfoService.selectOneByFactory(smsRawMaterialScrapOrder.getFactoryCode());
            if (!factoryInfoMap.isSuccess()) {
                log.error("查询"+smsRawMaterialScrapOrder.getFactoryCode()+"工厂信息失败，原因："+factoryInfoMap.get("msg"));
                return R.error("查询"+smsRawMaterialScrapOrder.getFactoryCode()+"工厂信息失败，原因："+factoryInfoMap.get("msg"));
            }
            CdFactoryInfo factoryInfo = factoryInfoMap.getData(CdFactoryInfo.class);
            //获取匹配月度报废订单号
            String yearMonth = DateUtils.dateFormat(new Date(),"yyyyMM");
            R scrapMonthNoMap = remoteCdScrapMonthNoService.findOne(yearMonth,smsRawMaterialScrapOrder.getFactoryCode());
            if (!scrapMonthNoMap.isSuccess()) {
                log.error("获取匹配月度报废订单号失败，原因："+scrapMonthNoMap.get("msg"));
                return R.error("获取匹配月度报废订单号失败!");
            }
            CdScrapMonthNo cdScrapMonthNo = scrapMonthNoMap.getData(CdScrapMonthNo.class);
            if (!BeanUtil.isNotEmpty(cdScrapMonthNo) || !StrUtil.isNotBlank(cdScrapMonthNo.getOrderNo())) {
                log.error("月度报废订单号为空！");
                return R.error("月度报废订单号为空！");
            }
            smsRawMaterialScrapOrder.setScrapOrderCode(cdScrapMonthNo.getOrderNo());
            smsRawMaterialScrapOrder.setComponyCode(factoryInfo.getCompanyCode());
        }
        smsRawMaterialScrapOrder.setUpdateBy(sysUser.getLoginName());
        smsRawMaterialScrapOrder.setUpdateTime(new Date());
        R updateCheckMap = updateRawScrapCheck(smsRawMaterialScrapOrder);
        if (!updateCheckMap.isSuccess()) {
            log.error("更新原材料报废失败，原因："+updateCheckMap.get("msg"));
            throw new BusinessException("更新原材料报废失败，原因："+updateCheckMap.get("msg"));
        }
        return R.ok();
    }
    /**
     * 删除原材料报废申请
     * @param ids
     * @return
     */
    @Override
    public R remove(String ids) {
        log.info(StrUtil.format("原材料报废删除开始：id为{}", ids));
        if(StrUtil.isBlank(ids)){
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
     * 定时任务更新价格
     * @param
     * @return
     */
    @Override
    public R updateRawScrapJob() {
        log.info("=========原材料报废价格计算开始============");
        //1、查询待结算的原材料报废数据
        Example example = new Example(SmsRawMaterialScrapOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("scrapStatus",RawScrapOrderStatusEnum.YCLBF_ORDER_STATUS_DJS.getCode());
        List<SmsRawMaterialScrapOrder> smsRawMaterialScrapOrders = smsRawMaterialScrapOrderMapper.selectByExample(example);
        if (!CollectionUtil.isNotEmpty(smsRawMaterialScrapOrders)) {
            log.info("没有需要更新的原材料报废记录！");
            return R.ok("没有需要更新的原材料报废记录！");
        }
        //2、查询原材料价格数据
        List<CdMaterialPriceInfo> materialPriceInfos = smsRawMaterialScrapOrders.stream().map(smsRawMaterialScrapOrder -> {
            CdMaterialPriceInfo cdMaterialPriceInfo = CdMaterialPriceInfo.builder()
                    .materialCode(smsRawMaterialScrapOrder.getRawMaterialCode())
                    .memberCode(smsRawMaterialScrapOrder.getSupplierCode())
                    .build();
            return cdMaterialPriceInfo;
        }).collect(Collectors.toList());
        R materialPriceMap = remoteCdMaterialPriceInfoService.selectBymaterialSupplierList(materialPriceInfos);
        if (!materialPriceMap.isSuccess()) {
            log.error("根据原材料物料和供应商查询原材料价格失败，原因："+materialPriceMap.get("msg"));
            throw new BusinessException("根据原材料物料和供应商查询原材料价格失败，原因："+materialPriceMap.get("msg"));
        }
        List<CdMaterialPriceInfo> materialPriceInfoList =
                materialPriceMap.getCollectData(new TypeReference<List<CdMaterialPriceInfo>>() {});
        Map<String,List<CdMaterialPriceInfo>> priceInfoMap = materialPriceInfoList
                .stream().collect(Collectors.groupingBy(cdMaterialPriceInfo ->
                        StrUtil.concat(true,cdMaterialPriceInfo.getMaterialCode()
                                ,cdMaterialPriceInfo.getMemberCode())));
        //原材料报废系数
        CdSettleRatio cdSettleRatioYCLBF = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_YCLBF.getCode());
        //原材料无实物系数
        CdSettleRatio cdSettleRatioYCLWSW = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_YCLWSW.getCode());
        //3、匹配原材料单价、币种、单位、结算费用
        smsRawMaterialScrapOrders.forEach(smsRawMaterialScrapOrder -> {
            String key = StrUtil.concat(true,smsRawMaterialScrapOrder.getRawMaterialCode(),smsRawMaterialScrapOrder.getSupplierCode());
            CdMaterialPriceInfo cdMaterialPriceInfo = priceInfoMap.get(key).get(0);
            //原材料单价
            BigDecimal rawMaterialPrice = cdMaterialPriceInfo.getNetWorth();
            //币种
            String currency = cdMaterialPriceInfo.getCurrency();
            //价格单位
            String priceUnit = cdMaterialPriceInfo.getPriceUnit();
            rawMaterialPrice = rawMaterialPrice.divide(new BigDecimal(priceUnit),6,BigDecimal.ROUND_HALF_UP);
            smsRawMaterialScrapOrder.setRawMaterialPrice(rawMaterialPrice);
            smsRawMaterialScrapOrder.setCurrency(currency);
            smsRawMaterialScrapOrder.setScrapPrice(BigDecimal.ZERO);
            if (RawScrapOrderIsCheckEnum.YCLBF_ORDER_IS_CHECK_TRUE.getCode().equals(smsRawMaterialScrapOrder.getIsCheck())) {
                BigDecimal scrapPrice = smsRawMaterialScrapOrder.getRawMaterialPrice().multiply(smsRawMaterialScrapOrder.getScrapNum());
                if (BeanUtil.isNotEmpty(cdSettleRatioYCLBF)) {
                    scrapPrice = scrapPrice.multiply(cdSettleRatioYCLBF.getRatio());
                }
                if (RawScrapOrderIsMaterialObjectEnum.YCLBF_ORDER_IS_MATERIAL_OBJECT_FALSE.getCode()
                        .equals(smsRawMaterialScrapOrder.getIsMaterialObject())
                        && BeanUtil.isNotEmpty(cdSettleRatioYCLWSW)) {
                    scrapPrice = scrapPrice.multiply(cdSettleRatioYCLWSW.getRatio());
                }
                smsRawMaterialScrapOrder.setScrapPrice(scrapPrice);
            }
        });
        //更新原材料报废记录
        int updateCount = smsRawMaterialScrapOrderMapper.updateBatchByPrimaryKeySelective(smsRawMaterialScrapOrders);
        if (updateCount <= 0) {
            log.error("更新原材料报废记录报废金额数据失败！");
            return R.error("更新原材料报废记录报废金额数据失败！");
        }
        log.info("=========原材料报废价格计算结束============");
        return R.ok();
    }

    @Override
    public List<SmsRawMaterialScrapOrder> selectByMonthAndStatus(String lastMonth, List<String> rawScrapStatus) {
        return smsRawMaterialScrapOrderMapper.selectByMonthAndStatus(lastMonth,rawScrapStatus);
    }
    /**
     * 提交
     * @param smsRawMaterialScrapOrder,sysUser
     * @return
     */
    @Override
    public R commit(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder, SysUser sysUser) {
        log.info("=========提交原材料报废申请==========");
        if (!BeanUtil.isNotEmpty(smsRawMaterialScrapOrder)){
            log.error("提交原材料报废申请，传入参数为空！");
            return R.error("提交原材料报废申请，传入参数为空！");
        }
        smsRawMaterialScrapOrder.setUpdateTime(new Date());
        smsRawMaterialScrapOrder.setUpdateBy(sysUser.getLoginName());
        R commitMap = updateRawScrapCheck(smsRawMaterialScrapOrder);
        if (!commitMap.isSuccess()) {
            log.error("提交失败，原因："+commitMap.get("msg"));
            return R.error("提交失败！");
        }
        return R.ok();
    }

    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return 返回SmsRawMaterialScrapOrder信息
     */
    public R checkCondition(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsRawMaterialScrapOrder smsRawMaterialScrapOrder = selectByPrimaryKey(id);
        if (smsRawMaterialScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!ScrapOrderStatusEnum.BF_ORDER_STATUS_DTJ.getCode().equals(smsRawMaterialScrapOrder.getScrapStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsRawMaterialScrapOrder);
    }
    public R checkNotNull(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder){
        log.info("=============原材料报废申请-新增-校验数据非空开始==============");
        if (!BeanUtil.isNotEmpty(smsRawMaterialScrapOrder)) {
            return R.error("新增原材料报废申请，传入参数为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getRawMaterialCode())) {
            return R.error("新增原材料报废申请，传入参数[原材料物料号]为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getFactoryCode())) {
            return R.error("新增原材料报废申请，传入参数[生产工厂]为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getStation())) {
            return R.error("新增原材料报废申请，传入参数[工位]为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getScrapNum().toString())) {
            return R.error("新增原材料报废申请，传入参数[报废数量]为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getAssessmentType())) {
            return R.error("新增原材料报废申请，传入参数[评估类型]为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getIsCheck())) {
            return R.error("新增原材料报废申请，传入参数[是否买单]为空！");
        }
        if (!StrUtil.isNotBlank(smsRawMaterialScrapOrder.getIsMaterialObject())) {
            return R.error("新增原材料报废申请，传入参数[有无实物]为空！");
        }
        return R.ok();
    }

    public R checkData(SmsRawMaterialScrapOrder smsRawMaterialScrapOrder){
        //根据原材料、工厂获取物料信息
        R materialMap = remoteMaterialService.getByMaterialCode(smsRawMaterialScrapOrder.getRawMaterialCode()
                ,smsRawMaterialScrapOrder.getFactoryCode());
        if (!materialMap.isSuccess()) {
            log.error("根据原材料、工厂查询物料主数据信息失败，原因："+materialMap.get("msg"));
            return R.error("根据原材料、工厂查询物料主数据信息失败，原因："+materialMap.get("msg"));
        }
        return R.ok();
    }
}
