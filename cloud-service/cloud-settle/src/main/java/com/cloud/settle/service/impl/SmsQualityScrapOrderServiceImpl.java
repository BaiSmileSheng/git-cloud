package com.cloud.settle.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.constant.ActProcessContants;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActProcessEmailUserVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.activiti.feign.RemoteActSmsQualityScrapOrderService;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.settle.domain.entity.*;
import com.cloud.settle.enums.CurrencyEnum;
import com.cloud.settle.enums.QualityScrapOrderStatusEnum;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.enums.ScrapOrderStatusEnum;
import com.cloud.settle.mail.MailService;
import com.cloud.settle.service.ISmsQualityScrapOrderLogService;
import com.cloud.system.domain.entity.*;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.enums.SettleRatioEnum;
import com.cloud.system.feign.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.conn.jco.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsQualityScrapOrderMapper;
import com.cloud.settle.service.ISmsQualityScrapOrderService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 质量报废Service业务层处理
 *
 * @author ltq
 * @date 2020-12-10
 */
@Service
@Slf4j
public class SmsQualityScrapOrderServiceImpl extends BaseServiceImpl<SmsQualityScrapOrder> implements ISmsQualityScrapOrderService {
    @Autowired
    private SmsQualityScrapOrderMapper smsQualityScrapOrderMapper;
    @Autowired
    private RemoteMaterialService remoteMaterialService;
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
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteCdProductWarehouseService remoteCdProductWarehouseService;
    @Autowired
    private RemoteCdScrapMonthNoService remoteCdScrapMonthNoService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    @Autowired
    private DataSourceTransactionManager dstManager;
    @Autowired
    private RemoteSupplierInfoService remoteSupplierInfoService;
    @Autowired
    private RemoteOssService remoteOssService;
    @Autowired
    private ISmsQualityScrapOrderLogService smsQualityScrapOrderLogService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private RemoteActSmsQualityScrapOrderService remoteActSmsQualityScrapOrderService;
    @Autowired
    private MailService mailService;

    private static final String SAVE = "0";
    private static final String SUBMIT = "1";

    /**
     * 质量部报废新增
     * @author ltq
     * @date 2020-12-07
     */
    @Override
    @GlobalTransactional
    public R insertQualityScrap(SmsQualityScrapOrder smsQualityScrapOrder, SysUser sysUser) {
        log.info("===========新增质量部报废申请================");
        //数据字段非空校验
        R checkMap = checkParamNull(smsQualityScrapOrder);
        if (!checkMap.isSuccess()) {
            log.error("执行数据字段非空校验方法返回错误：" + checkMap.get("msg"));
            throw new BusinessException(checkMap.getStr("msg"));
        }
        //数据字段合规校验
        try {
            smsQualityScrapOrder = checkParamData(smsQualityScrapOrder);
        } catch (Exception e) {
            log.error("执行数据字段正确性校验方法返回错误：" + e.getMessage());
            throw new BusinessException("执行数据字段正确性校验方法返回错误：" + e.getMessage());
        }
        R seqResult = remoteSequeceService.selectSeq("quality_scrap_seq", 4);
        if(!seqResult.isSuccess()){
            throw new BusinessException("获取序列号失败");
        }
        String seq = seqResult.getStr("data");
        StringBuffer scrapNo = new StringBuffer();
        //ZLBBF+年月日+4位顺序号
        scrapNo.append("ZLBBF").append(DateUtils.dateTime()).append(seq);
        smsQualityScrapOrder.setScrapNo(scrapNo.toString());
        smsQualityScrapOrder.setCreateBy(sysUser.getLoginName());
        smsQualityScrapOrder.setCreateTime(new Date());
        if (SAVE.equals(smsQualityScrapOrder.getSaveOrSubmit())) {
            smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DTJ.getCode());
        } else {
            smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_GYSDQR.getCode());
        }
        int insertCount = insertSelective(smsQualityScrapOrder);
        if (insertCount <= 0) {
            log.error("新增质量部报废申请失败！");
            throw new BusinessException("新增质量部报废申请失败！");
        }
        return R.ok();
    }
    /**
     * 质量部报废新增-多条
     * @author ltq
     * @date 2020-12-07
     */
    @Override
    @GlobalTransactional
    public R insertQualityScrapList(List<SmsQualityScrapOrder> smsQualityScrapOrders, SysUser sysUser) {
        smsQualityScrapOrders.forEach(smsQualityScrapOrder -> {
            R r = insertQualityScrap(smsQualityScrapOrder,sysUser);
            if(!r.isSuccess()){
                throw new BusinessException(r.getStr("msg"));
            }
        });
        return R.ok();
    }
    /**
     * 质量部报废更新
     * @author ltq
     * @date 2020-12-07
     */
    @Override
    @GlobalTransactional
    public R updateQualityScrap(SmsQualityScrapOrder smsQualityScrapOrder, SysUser sysUser) {
        log.info("===========更新质量部报废申请================");
        if (!QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DTJ.getCode().equals(smsQualityScrapOrder.getScrapStatus())) {
            log.error("非[待提交]状态的订单不可编辑！");
            return R.error("非[待提交]状态的订单不可编辑！");
        }
        //数据字段非空校验
        R checkMap = checkParamNull(smsQualityScrapOrder);
        if (!checkMap.isSuccess()) {
            log.error("执行数据字段非空校验方法返回错误：" + checkMap.get("msg"));
            throw new BusinessException(checkMap.getStr("msg"));
        }
        //数据字段合规校验
        try {
            smsQualityScrapOrder = checkParamData(smsQualityScrapOrder);
        } catch (Exception e) {
            log.error("执行数据字段正确性校验方法返回错误：" + e.getMessage());
            throw new BusinessException("执行数据字段正确性校验方法返回错误：" + e.getMessage());
        }
        smsQualityScrapOrder.setUpdateBy(sysUser.getLoginName());
        smsQualityScrapOrder.setUpdateTime(new Date());
        if (SAVE.equals(smsQualityScrapOrder.getSaveOrSubmit())) {
            smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DTJ.getCode());
        } else {
            smsQualityScrapOrder.setSubmitDate(DateUtil.date());
            smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_GYSDQR.getCode());
        }
        int updateCount = updateByPrimaryKeySelective(smsQualityScrapOrder);
        if (updateCount <= 0) {
            log.error("更新质量部报废申请失败！");
            throw new BusinessException("更新质量部报废申请失败！");
        }
        return R.ok();
    }

    /**
     * 质量部报废删除
     * @param ids
     * @return SmsQualityScrapOrder
     */
    @Override
    @GlobalTransactional
    public R remove(String ids) {
        log.info(StrUtil.format("质量部报废删除开始：id为{}", ids));
        if(StrUtil.isBlank(ids)){
            throw new BusinessException("传入参数不能为空！");
        }
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            checkCondition(Long.valueOf(id));
        }
        int rows = deleteByIds(ids);
        return rows > 0 ? R.ok() : R.error("删除失败！");
    }

    /**
     * 质量部报废提交
     * @param id
     * @return R
     */
    @Override
    @GlobalTransactional
    public R commitQualityScrap(String id,SysUser sysUser) {
        log.info(StrUtil.format("质量部报废提交开始：id为{}", id));
        if(StrUtil.isBlank(id)){
            throw new BusinessException("传入参数不能为空！");
        }
        //校验状态是否是未提交
        checkCondition(Long.valueOf(id));
        SmsQualityScrapOrder smsQualityScrapOrder = selectByPrimaryKey(id);
        smsQualityScrapOrder.setSubmitDate(new Date());
        smsQualityScrapOrder.setUpdateBy(sysUser.getLoginName());
        smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_GYSDQR.getCode());
        int rows = updateByPrimaryKeySelective(smsQualityScrapOrder);
        return rows > 0 ? R.ok() : R.error("提交失败！");
    }

    @Override
    @GlobalTransactional
    public R updatePriceJob() {
        //计算月份：上个月
        String month = DateUtil.format(DateUtil.lastMonth(), "yyyyMM");
        //质量部报废系数
        CdSettleRatio cdSettleRatioBF = remoteSettleRatioService.selectByClaimType(SettleRatioEnum.SPLX_ZLBBF.getCode());
        if (cdSettleRatioBF == null) {
            log.error("(月度结算定时任务)质量部报废索赔系数未维护！");
            throw new BusinessException("质量部报废索赔系数未维护！");
        }

        //从SAP销售价格表取值（销售组织、物料号、有效期）
        //查询上个月、待结算的物耗申请中的物料号  用途是查询SAP成本价 更新到物耗表
        List<String> materialCodeList = smsQualityScrapOrderMapper.selectMaterialByMonthAndStatus(month, CollUtil.newArrayList(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DJS.getCode()));
        Map<String, CdSapSalePrice> sapPrice = new ConcurrentHashMap<>();
        if (materialCodeList != null) {
            log.info(StrUtil.format("(月度结算定时任务)质量部报废申请需要更新销售价格的物料号:{}", materialCodeList.toString()));
            String now = DateUtil.now();
            String materialCodeStr = StrUtil.join(",", materialCodeList);
            //根据前面查出的物料号查询SAP成本价 map key:物料号  value:CdMaterialPriceInfo
            sapPrice = remoteCdSapSalePriceInfoService.selectPriceByInMaterialCodeAndDate(materialCodeStr, now, now);
        }
        //取得计算月份、待结算的报废数据
        List<SmsQualityScrapOrder> smsQualityScrapOrderList = selectByMonthAndStatus(month, CollUtil.newArrayList(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DJS.getCode()));
        //循环报废，计算索赔金额
        if (CollectionUtil.isNotEmpty(smsQualityScrapOrderList)) {
            for (SmsQualityScrapOrder smsQualityScrapOrder : smsQualityScrapOrderList) {
                CdSapSalePrice cdSapSalePrice = sapPrice.get(smsQualityScrapOrder.getProductMaterialCode()+smsQualityScrapOrder.getCompanyCode());
                if (BeanUtil.isNotEmpty(cdSapSalePrice)) {
                    //如果没有找到SAP销售价格，则更新备注
                    log.info(StrUtil.format("(定时任务)SAP销售价格未同步的物料号:{}", smsQualityScrapOrder.getProductMaterialCode()));
                    smsQualityScrapOrder.setRemark("SAP销售价格未同步！");
                    updateByPrimaryKeySelective(smsQualityScrapOrder);
                    continue;
                }
                smsQualityScrapOrder.setCurrency(cdSapSalePrice.getConditionsMonetary());
                BigDecimal settleFee = new BigDecimal(cdSapSalePrice.getSalePrice())
                        .divide(new BigDecimal(cdSapSalePrice.getUnitPricing()),6, BigDecimal.ROUND_HALF_UP)
                        .multiply(smsQualityScrapOrder.getScrapAmount());
                BigDecimal ratio = cdSettleRatioBF.getRatio();//报废索赔系数
                settleFee = settleFee.multiply(ratio);
                //如果是外币，还要 除以数额*汇率
                if (StrUtil.isEmpty(smsQualityScrapOrder.getCurrency())) {
                    throw new BusinessException(StrUtil.format("{}质量部报废单未维护币种", smsQualityScrapOrder.getScrapNo()));
                }
                if (!StrUtil.equals(CurrencyEnum.CURRENCY_CNY.getCode(), smsQualityScrapOrder.getCurrency())) {
                    //查询指定月汇率
                    R rRate = remoteCdMouthRateService.findRateByYearMouth(month,smsQualityScrapOrder.getCurrency());
                    if (!rRate.isSuccess()) {
                        throw new BusinessException(StrUtil.format("{}月份未维护{}币种费率", month,smsQualityScrapOrder.getCurrency()));
                    }
                    CdMouthRate cdMouthRate = rRate.getData(CdMouthRate.class);
                    BigDecimal rate = cdMouthRate.getRate();//汇率
                    BigDecimal rateAmount = cdMouthRate.getAmount();//数额
                    settleFee = settleFee.divide(rateAmount,6, BigDecimal.ROUND_HALF_UP).multiply(rate);
                    smsQualityScrapOrder.setRate(rate);
                }
                smsQualityScrapOrder.setMaterialPrice(new BigDecimal(cdSapSalePrice.getSalePrice()));
                smsQualityScrapOrder.setSettleFee(settleFee);
            }
            updateBatchByPrimaryKeySelective(smsQualityScrapOrderList);
        }
        return R.ok();
    }

    @Override
    public List<SmsQualityScrapOrder> selectByMonthAndStatus(String month, List<String> scrapStatus) {
        return smsQualityScrapOrderMapper.selectByMonthAndStatus(month,scrapStatus);
    }

    @Override
    @GlobalTransactional
    public R confirm(String ids,SysUser sysUser) {
        log.info(StrUtil.format("质量部报废供应商确认开始：id为{}", ids));
        if(StrUtil.isBlank(ids)){
            throw new BusinessException("传入参数不能为空！");
        }
        for(String id:ids.split(",")){
            //校验状态是否是未提交
            checkConfirmCondition(Long.valueOf(id));
        }
        List<SmsQualityScrapOrder> smsQualityScrapOrderList = smsQualityScrapOrderMapper.selectByIds(ids);
        smsQualityScrapOrderList.forEach(smsQualityScrapOrder -> {
            smsQualityScrapOrder.setUpdateBy(sysUser.getLoginName());
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
            TransactionStatus transaction = dstManager.getTransaction(def); // 获得事务状态
            R sap261Map = autidSuccessToSAP261(smsQualityScrapOrder);
            if (!sap261Map.isSuccess()) {
                log.error("质量部报废订单传SAP系统失败，原因："+sap261Map.get("msg"));
                dstManager.rollback(transaction);
                throw new BusinessException("质量部报废订单传SAP系统失败，原因："+sap261Map.get("msg"));
            }
            dstManager.commit(transaction);
        });
        return R.ok();
    }

    @Override
    public List<Map<String, String>> selectMaterialAndCompanyCodeGroupBy(String month, List<String> scrapStatus) {
        return smsQualityScrapOrderMapper.selectMaterialAndCompanyCodeGroupBy(month,scrapStatus);
    }

    @Override
    @GlobalTransactional
    public R appealSupplier(Long id, String complaintDescription, String ossIds,SysUser sysUser) {
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传附件id不能为空");
        }
        //1.查询质量部报废数据,判断状态是否是待确认,待确认可修改
        SmsQualityScrapOrder smsQualityScrapOrder = smsQualityScrapOrderMapper.selectByPrimaryKey(id);
        if (!BeanUtil.isNotEmpty(smsQualityScrapOrder)) {
            throw new BusinessException("质量部报废订单不存在");
        }
        Boolean flagResult = QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_GYSDQR.getCode().equals(smsQualityScrapOrder.getScrapStatus())
                || QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_SHBH.getCode().equals(smsQualityScrapOrder.getScrapStatus());
        if (!flagResult) {
            throw new BusinessException("此质量部报废不可申诉");
        }
        Example example = new Example(SmsQualityScrapOrderLog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qualityNo",smsQualityScrapOrder.getScrapNo());
        criteria.andEqualTo("qualityId",smsQualityScrapOrder.getId());
        List<SmsQualityScrapOrderLog> orderLogList = smsQualityScrapOrderLogService.selectByExample(example);
        String orderLogStatus = "1";
        String orderStatus = QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_ZLJLSH.getCode();
        String actKey = ActProcessContants.ACTIVITI_QUALITY_SCRAP_REVIEW_ZLGCS;
        String actTitle = ActProcessContants.ACTIVITI_BF_TITLE_QUALITY_SCRAP_ZLGCS;
        String roleKey = RoleConstants.ROLE_KEY_ZLGCS;
        if (CollectionUtil.isNotEmpty(orderLogList)) {
            if (orderLogList.size() == 1) {
                orderLogStatus = "2";
                orderStatus = QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_ZLBZSH.getCode();
                actKey = ActProcessContants.ACTIVITI_QUALITY_SCRAP_REVIEW_ZLBBZ;
                actTitle = ActProcessContants.ACTIVITI_BF_TITLE_QUALITY_SCRAP_ZLBBZ;
                roleKey = RoleConstants.ROLE_KEY_ZLBBZ;
            } else if (orderLogList.size() == 2) {
                orderLogStatus = "3";
                orderStatus = QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_ZLPTZSH.getCode();
                actKey = ActProcessContants.ACTIVITI_QUALITY_SCRAP_REVIEW_ZLPTZ;
                actTitle = ActProcessContants.ACTIVITI_BF_TITLE_QUALITY_SCRAP_ZLPTZ;
                roleKey = RoleConstants.ROLE_KEY_ZLPTZ;
            }
        }
        R rUser = remoteUserService.selectUserByMaterialCodeAndRoleKey(
                smsQualityScrapOrder.getFactoryCode(),
                roleKey);
        if(!rUser.isSuccess()){
            log.error("原材料报废审批开启失败，查询审核人为空！");
            throw new BusinessException("原材料报废审批开启失败，查询审核人为空！");
        }
        List<SysUserVo> users = rUser.getCollectData(new TypeReference<List<SysUserVo>>() {});
        SysUserVo userVo = users.get(0);
        Set<String> userIds = new HashSet<>();
        userIds.add(userVo.getUserId().toString());
        ActStartProcessVo actStartProcessVo = ActStartProcessVo
                .builder()
                .orderId(smsQualityScrapOrder.getId().toString())
                .orderCode(smsQualityScrapOrder.getScrapNo())
                .userIds(userIds).build();
        List<ActStartProcessVo> processVos = new ArrayList<>();
        processVos.add(actStartProcessVo);
        ActBusinessVo actBusinessVo = ActBusinessVo.builder()
                .title(actTitle)
                .key(actKey)
                .userId(sysUser.getUserId())
                .userName(sysUser.getUserName()).build();
        actBusinessVo.setProcessVoList(processVos);
        R actStartMap = remoteActSmsQualityScrapOrderService.addSave(actBusinessVo);
        if (!actStartMap.isSuccess()) {
            log.error("开启质量部报废-申诉审批流程失败，原因："+actStartMap.get("msg"));
            throw new BusinessException("开启质量部报废-申诉审批流程失败!");
        }
        SmsQualityScrapOrderLog smsQualityScrapOrderLog = SmsQualityScrapOrderLog.builder().qualityNo(smsQualityScrapOrder.getScrapNo())
                .qualityId(smsQualityScrapOrder.getId())
                .complaintDate(new Date())
                .complaintDescription(complaintDescription)
                .procNo(orderLogStatus)
                .build();
        smsQualityScrapOrderLog.setCreateBy(sysUser.getLoginName());
        smsQualityScrapOrderLog.setCreateTime(new Date());
        smsQualityScrapOrderLogService.insertSelective(smsQualityScrapOrderLog);
        //2.修改质量部报废订单信息
        smsQualityScrapOrder.setScrapStatus(orderStatus);
        smsQualityScrapOrder.setComplaintDate(new Date());
        smsQualityScrapOrder.setComplaintDescription(complaintDescription);
        smsQualityScrapOrder.setUpdateBy(sysUser.getLoginName());
        smsQualityScrapOrder.setUpdateTime(new Date());
        smsQualityScrapOrderMapper.updateByPrimaryKeySelective(smsQualityScrapOrder);
        //3.根据订单号新增文件
        String orderNo = smsQualityScrapOrder.getScrapNo();
        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        try{
//            mailService.sendTextMail(userVo.getEmail(),EmailConstants.TITLE_QUALITY_SCRAP
//                    ,userVo.getUserName() + EmailConstants.QUALITY_SCRAP_CONTEXT + EmailConstants.ORW_URL);
        }catch (Exception e) {
            log.error("质量部报废申诉审批邮件通知发送失败！");
            throw new BusinessException("质量部报废申诉审批邮件通知发送失败！");
        }
        return uplodeFileResult;
    }
    /**
     * 审批流更新业务数据
     */
    @Override
    @GlobalTransactional
    public R updateAct(SmsQualityScrapOrder smsQualityScrapOrder, Integer result,String comment,String auditor) {
        log.info("===========质量部报废申诉审批-更新业务表数据============");
        if (!BeanUtil.isNotEmpty(smsQualityScrapOrder)) {
            return R.error("更新业务表数据,传入参数为空！");
        }
        Example example = new Example(SmsQualityScrapOrderLog.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qualityId",smsQualityScrapOrder.getId());
        criteria.andEqualTo("result","0");
        SmsQualityScrapOrderLog smsQualityScrapOrderLog = smsQualityScrapOrderLogService.findByExampleOne(example);
        if (!BeanUtil.isNotEmpty(smsQualityScrapOrderLog)) {
            return R.error("更新业务表数据,查询失败！");
        }
        smsQualityScrapOrderLog.setResult(result);
        smsQualityScrapOrderLog.setComment(comment);
        smsQualityScrapOrderLog.setAuditor(auditor);
        smsQualityScrapOrderLog.setAuditTime(new Date());
        smsQualityScrapOrderMapper.updateByPrimaryKeySelective(smsQualityScrapOrder);
        smsQualityScrapOrderLogService.updateByPrimaryKeySelective(smsQualityScrapOrderLog);
        return R.ok();
    }

    /**
     * 传SAP261
     * @param smsQualityScrapOrder
     * @return
     */
    public R autidSuccessToSAP261(SmsQualityScrapOrder smsQualityScrapOrder) {
        Date date = DateUtil.date();
        SysInterfaceLog sysInterfaceLog = SysInterfaceLog.builder()
                .appId("SAP").interfaceName(SapConstants.ZESP_IM_001).build();
        //成品报废库位默认0088，如果0088没有库存就选择0188
        String lgort = "0088";
        CdProductWarehouse cdProductWarehouse = CdProductWarehouse.builder()
                .productMaterialCode(smsQualityScrapOrder.getProductMaterialCode())
                .productFactoryCode(smsQualityScrapOrder.getFactoryCode())
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
        R rNo = remoteCdScrapMonthNoService.findOne(yearMouth,smsQualityScrapOrder.getFactoryCode());
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
            inputTable.setValue("BKTXT", StrUtil.concat(true,smsQualityScrapOrder.getSupplierCode(),smsQualityScrapOrder.getScrapNo()));//凭证抬头文本  V码+报废单号
            inputTable.setValue("WERKS", smsQualityScrapOrder.getFactoryCode());//工厂
            inputTable.setValue("LGORT", lgort);//库存地点
            inputTable.setValue("MATNR", smsQualityScrapOrder.getProductMaterialCode().toUpperCase());//物料号
            inputTable.setValue("ERFME", smsQualityScrapOrder.getStuffUnit());//基本计量单位
            inputTable.setValue("ERFMG", smsQualityScrapOrder.getScrapAmount());//数量
            inputTable.setValue("AUFNR", cdScrapMonthNo.getOrderNo());//每月维护一次订单号
            String content = StrUtil.format("BWARTWA:{},BKTXT:{},WERKS:{},LGORT:{},MATNR:{}" +
                            ",ERFME:{},ERFMG:{},AUFNR:{}","261",
                    StrUtil.concat(true,smsQualityScrapOrder.getSupplierCode(),smsQualityScrapOrder.getScrapNo()),
                    smsQualityScrapOrder.getFactoryCode(),lgort,smsQualityScrapOrder.getProductMaterialCode(),
                    smsQualityScrapOrder.getStuffUnit(),smsQualityScrapOrder.getScrapAmount(),cdScrapMonthNo.getOrderNo());
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
                        smsQualityScrapOrder.setPostingNo(outTableOutput.getString("MBLNR"));
                        smsQualityScrapOrder.setSapDate(date);
                        smsQualityScrapOrder.setSapRemark(outTableOutput.getString("MESSAGE"));
                        smsQualityScrapOrder.setScrapStatus(QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DJS.getCode());
                        smsQualityScrapOrder.setSapStoreage(lgort);
                        smsQualityScrapOrder.setSupplierConfirmDate(date);
                        updateByPrimaryKeySelective(smsQualityScrapOrder);
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
    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return SmsQualityScrapOrder
     */
    public R checkConfirmCondition(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsQualityScrapOrder smsQualityScrapOrder = selectByPrimaryKey(id);
        if (smsQualityScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_GYSDQR.getCode().equals(smsQualityScrapOrder.getScrapStatus())
                && !QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_SHBH.getCode().equals(smsQualityScrapOrder.getScrapStatus())) {
            throw new BusinessException("只能确认[供应商待确认]和[供应商待确认(申诉驳回)]状态的订单！");
        }
        return R.data(smsQualityScrapOrder);
    }

    /**
     * 校验状态是否是未提交，如果不是则抛出错误
     * @param id
     * @return SmsQualityScrapOrder
     */
    public R checkCondition(Long id){
        if (id==null) {
            throw new BusinessException("ID不能为空！");
        }
        SmsQualityScrapOrder smsQualityScrapOrder = selectByPrimaryKey(id);
        if (smsQualityScrapOrder == null) {
            throw new BusinessException("未查询到此数据！");
        }
        if (!QualityScrapOrderStatusEnum.ZLBBF_ORDER_STATUS_DTJ.getCode().equals(smsQualityScrapOrder.getScrapStatus())) {
            throw new BusinessException("已提交的数据不能操作！");
        }
        return R.data(smsQualityScrapOrder);
    }

    public R checkParamNull(SmsQualityScrapOrder smsQualityScrapOrder) {
        log.info("==========校验参数是否为空开始==========");
        if (!BeanUtil.isNotEmpty(smsQualityScrapOrder)) {
            return R.error("传入参数不能为空！");
        }
        if (!StrUtil.isNotBlank(smsQualityScrapOrder.getProductMaterialCode())) {
            return R.error("成品专用号不能为空！");
        } else {
            smsQualityScrapOrder.setProductMaterialCode(smsQualityScrapOrder.getProductMaterialCode().trim());
        }
        if (!StrUtil.isNotBlank(smsQualityScrapOrder.getFactoryCode())) {
            return R.error("生产工厂不能为空！");
        }else {
            smsQualityScrapOrder.setFactoryCode(smsQualityScrapOrder.getFactoryCode().trim());
        }
        if (!StrUtil.isNotBlank(smsQualityScrapOrder.getSupplierCode())) {
            return R.error("供应商编码不能为空！");
        }else {
            smsQualityScrapOrder.setSupplierCode(smsQualityScrapOrder.getSupplierCode().trim());
        }
        if (!StrUtil.isNotBlank(smsQualityScrapOrder.getScrapAmount().toString())) {
            return R.error("报废数量不能为空！");
        }
        if (!StrUtil.isNotBlank(smsQualityScrapOrder.getStation())) {
            return R.error("工位不能为空！");
        }else {
            smsQualityScrapOrder.setStation(smsQualityScrapOrder.getStation().trim());
        }
        log.info("==========校验参数是否为空结束==========");
        return R.ok();
    }

    public SmsQualityScrapOrder checkParamData(SmsQualityScrapOrder smsQualityScrapOrder) {
        //根据原材料、工厂获取物料信息
        R materialMap = remoteMaterialService.getByMaterialCode(smsQualityScrapOrder.getProductMaterialCode().trim()
                , smsQualityScrapOrder.getFactoryCode().trim());
        if (!materialMap.isSuccess()) {
            log.error("根据原材料、工厂查询物料主数据信息失败，原因：" + materialMap.get("msg"));
            throw new BusinessException("根据原材料、工厂查询物料主数据信息失败，原因：" + materialMap.get("msg"));
        }
        CdMaterialInfo cdMaterialInfo = materialMap.getData(CdMaterialInfo.class);
        smsQualityScrapOrder.setStuffUnit(cdMaterialInfo.getPrimaryUom());
        smsQualityScrapOrder.setProductMaterialName(cdMaterialInfo.getMaterialDesc());
        //校验+获取工厂信息
        R factoryMap = remoteFactoryInfoService.selectOneByFactory(smsQualityScrapOrder.getFactoryCode());
        if (!factoryMap.isSuccess()) {
            log.error("查询" + smsQualityScrapOrder.getFactoryCode() + "工厂信息失败，原因：" + factoryMap.get("msg"));
            throw new BusinessException("查询" + smsQualityScrapOrder.getFactoryCode() + "工厂信息失败，原因：" + factoryMap.get("msg"));
        }
        CdFactoryInfo factoryInfo = factoryMap.getData(CdFactoryInfo.class);
        smsQualityScrapOrder.setCompanyCode(factoryInfo.getCompanyCode());
        R supplierMap = remoteSupplierInfoService.selectOneBySupplierCode(smsQualityScrapOrder.getSupplierCode().trim());
        if (!supplierMap.isSuccess()) {
            log.error("查询"+smsQualityScrapOrder.getSupplierCode()+"供应商信息失败，原因："+supplierMap.get("msg"));
            throw new BusinessException("查询"+smsQualityScrapOrder.getSupplierCode()+"供应商信息失败，原因："+supplierMap.get("msg"));
        }
        CdSupplierInfo cdSupplierInfo = supplierMap.getData(CdSupplierInfo.class);
        smsQualityScrapOrder.setSupplierName(cdSupplierInfo.getCorporation());
        return smsQualityScrapOrder;
    }
}
