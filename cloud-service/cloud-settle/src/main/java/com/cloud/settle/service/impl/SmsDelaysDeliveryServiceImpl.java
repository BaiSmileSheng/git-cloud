package com.cloud.settle.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.feign.RemoteProductionOrderService;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.mail.MailService;
import com.cloud.settle.mapper.SmsDelaysDeliveryMapper;
import com.cloud.settle.service.ISmsDelaysDeliveryService;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import com.cloud.system.feign.RemoteOssService;
import com.cloud.system.feign.RemoteSequeceService;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 延期交付索赔 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Service
public class SmsDelaysDeliveryServiceImpl extends BaseServiceImpl<SmsDelaysDelivery> implements ISmsDelaysDeliveryService {

    private static Logger logger = LoggerFactory.getLogger(SmsDelaysDeliveryServiceImpl.class);

    @Autowired
    private SmsDelaysDeliveryMapper smsDelaysDeliveryMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteSequeceService remoteSequeceService;

    @Autowired
    private RemoteOssService remoteOssService;

    @Autowired
    private RemoteProductionOrderService remoteProductionOrderService;

    @Autowired
    private RemoteFactoryLineInfoService remoteFactoryLineInfoService;

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 索赔单序列号生成所对应的序列
     */
    private static final String DELAYS_SEQ_NAME = "delays_id";
    /**
     * 索赔单序列号生成所对应的序列长度
     */
    private static final int DELAYS_SEQ_LENGTH = 4;

    /**
     * 生成索赔单前缀
     */
    private static final String DELAYS_ORDER_PRE = "ZL";

    private final static BigDecimal DELAYS_AMOUNT = new BigDecimal(-2000);//延期索赔金额

    /**
     * 用于获取一天前的时间
     */
    private final static int dateBeforeOne = -1;

    /**
     * 查询延期交付索赔详情
     * @param id 主键id
     * @return 延期交付索赔详情(包含文件信息)
     */
    @Override
    public R selectById(Long id) {
        logger.info("根据id查询延期索赔单详情 id:{}",id);
        SmsDelaysDelivery smsDelaysDeliveryRes = smsDelaysDeliveryMapper.selectByPrimaryKey(id);
        if(null != smsDelaysDeliveryRes || StringUtils.isNotBlank(smsDelaysDeliveryRes.getDelaysNo())){
            Map<String,Object> map = new HashMap<>();
            //如果申诉过查文件
            if(StringUtils.isNotBlank(smsDelaysDeliveryRes.getComplaintDescription())){
                //索赔文件编号
                String claimOrderNo = smsDelaysDeliveryRes.getDelaysNo();
                R sysOssR = remoteOssService.listByOrderNo(claimOrderNo);
                if(!sysOssR.isSuccess()){
                    logger.error("根据id查询延期索赔单详情获取图片信息失败claimOrderNo:{},res:{}",
                            claimOrderNo, JSONObject.toJSON(sysOssR));
                    throw new BusinessException("根据id查询延期索赔单详情获取图片信息失败");
                }
                List<SysOss> sysOssList = sysOssR.getCollectData(new TypeReference<List<SysOss>>() {});
                map.put("sysOssList",sysOssList);
            }
            map.put("smsDelaysDelivery",smsDelaysDeliveryRes);
            return R.ok(map);
        }

        return R.error("查询索赔单失败");
    }

    /**
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     * @return 成功或失败
     */
    @GlobalTransactional
    @Override
    public R batchAddDelaysDelivery() {
        logger.info("定时任务调用批量新增保存延期交付索赔");
        //供应商编号
        Set<String> supplierSet = new HashSet<>();
        //1.获取延期索赔信息
        List<SmsDelaysDelivery> smsDelaysDeliveryList = changeOmsProductionOrder(supplierSet);

        //2.插入延期索赔信息
        int count = smsDelaysDeliveryMapper.insertList(smsDelaysDeliveryList);
        //供应商V码对应的供应商信息
        Map<String,SysUserVo> mapSysUser = new HashMap<>();
        //获取供应商信息
        for(String supplierCode : supplierSet){
            logger.info("新增保存延期交付索赔时获取供应商信息 supplierCode:{}",supplierCode);
            R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
            if(!sysUserR.isSuccess()){
                logger.error("新增保存延期交付索赔 供应商信息不存在supplierCode:{}", supplierCode);
                throw new BusinessException("新增保存延期交付索赔 供应商信息不存在");
            }
            SysUserVo sysUser = sysUserR.getData(SysUserVo.class);
            mapSysUser.put(supplierCode,sysUser);
        }
        //3.发送邮件
        String mailSubject = "延期索赔邮件";
        for(SmsDelaysDelivery smsDelaysDeliveryMail :smsDelaysDeliveryList ){
            String supplierCode = smsDelaysDeliveryMail.getSupplierCode();
            SysUserVo sysUser = mapSysUser.get(supplierCode);
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条延期交付订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(smsDelaysDeliveryMail.getSupplierName()).append("+").append(supplierCode).append("+")
                    .append(sysUser.getCorporation()).append(" ").append("您有一条延期交付订单，订单号")
                    .append(smsDelaysDeliveryMail.getDelaysNo()).append(",请及时处理，如不处理，3天后系统自动确认，无法申诉");
            String toSupplier = sysUser.getEmail();
            mailService.sendTextMail(toSupplier,mailTextBuffer.toString(),mailSubject);
        }
        return R.ok();
    }


    /**
     * 每日凌晨查询
     * 将查询的排产订单信息转换成延期索赔信息
     * supplierSet 供应商编号,为查供应商邮箱
     * @return
     */
    private List<SmsDelaysDelivery> changeOmsProductionOrder(Set<String> supplierSet){
        //每天凌晨获取昨天关单,基本结束时间<昨天的
        //获取昨天时间
        String date = DateUtils.getDaysTimeString(dateBeforeOne);
        List<SmsDelaysDelivery> smsDelaysDeliveryList = new ArrayList<>();
        R rRes = remoteProductionOrderService.listForDelays(date,date,DateUtils.getDate());
        if (!rRes.isSuccess()) {
            return null;
        }
        List<OmsProductionOrder> listRes=rRes.getCollectData(new TypeReference<List<OmsProductionOrder>>() {});
        for(OmsProductionOrder omsProductionOrderRes : listRes){
            SmsDelaysDelivery smsDelaysDelivery = new SmsDelaysDelivery();
            smsDelaysDelivery.setDelaysAmount(DELAYS_AMOUNT);
            smsDelaysDelivery.setSubmitDate(new Date());
            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_1.getCode());
            //1.获取索赔单号
            StringBuffer qualityNoBuffer = new StringBuffer(DELAYS_ORDER_PRE);
            qualityNoBuffer.append(DateUtils.getDate().replace("-",""));
            R seqR = remoteSequeceService.selectSeq(DELAYS_SEQ_NAME,DELAYS_SEQ_LENGTH);
            if(!seqR.isSuccess()){
                logger.info("新增保存延期交付索赔获取序列号失败 res:{}",JSONObject.toJSONString(seqR));
                throw new BusinessException("新增保存延期交付索赔获取序列号失败");
            }
            String seq = seqR.getStr("data");
            logger.info("新增保存延期交付索赔获取序列号 seq:{}",seq);
            qualityNoBuffer.append(seq);
            smsDelaysDelivery.setDelaysNo(qualityNoBuffer.toString());

            //将排产订单信息转换成延期索赔数据
            smsDelaysDelivery.setProductLineCode(omsProductionOrderRes.getProductLineCode());
            smsDelaysDelivery.setProductOrderCode(omsProductionOrderRes.getProductOrderCode());
            smsDelaysDelivery.setFactoryCode(omsProductionOrderRes.getProductFactoryCode());
            smsDelaysDelivery.setProductMaterialCode(omsProductionOrderRes.getProductMaterialCode());
            smsDelaysDelivery.setProductMaterialName(omsProductionOrderRes.getProductMaterialDesc());
            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_1.getCode());
            smsDelaysDelivery.setDeliveryDate(DateUtils.string2Date(omsProductionOrderRes.getProductEndDate(),YYYY_MM_DD));
            smsDelaysDelivery.setDeliveryDate(DateUtil.parseDate(omsProductionOrderRes.getProductEndDate()));
            smsDelaysDelivery.setActDeliveryDate(omsProductionOrderRes.getActualEndDate());
            smsDelaysDelivery.setCreateTime(new Date());
            smsDelaysDelivery.setDelFlag(DeleteFlagConstants.NO_DELETED);

            //根据线体获取供应商信息
            R rFactoryLineInfo=remoteFactoryLineInfoService
                    .selectInfoByCodeLineCode(omsProductionOrderRes.getProductLineCode(),
                            omsProductionOrderRes.getProductFactoryCode());
            if (!rFactoryLineInfo.isSuccess()) {
                throw new BusinessException(rFactoryLineInfo.getStr("msg"));
            }
            CdFactoryLineInfo cdFactoryLineInfo=rFactoryLineInfo.getData(CdFactoryLineInfo.class);
            if(null != cdFactoryLineInfo){
                smsDelaysDelivery.setSupplierCode(cdFactoryLineInfo.getSupplierCode());
                smsDelaysDelivery.setSupplierName(cdFactoryLineInfo.getSupplierDesc());
            }
            smsDelaysDeliveryList.add(smsDelaysDelivery);
            supplierSet.add(smsDelaysDelivery.getSupplierCode());
        }
        return smsDelaysDeliveryList;
    }

    /**
     * 延期索赔单供应商申诉(包含文件信息)
     * @param smsDelaysDeliveryReq 延期索赔信息
     * @return 延期索赔单供应商申诉结果成功或失败
     */
    @GlobalTransactional
    @Override
    public R supplierAppeal(SmsDelaysDelivery smsDelaysDeliveryReq,String ossIds) {
        logger.info("延期索赔单供应商申诉(包含文件信息) 单号:{}",smsDelaysDeliveryReq.getDelaysNo());
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        SmsDelaysDelivery selectSmsDelaysDelivery = smsDelaysDeliveryMapper.selectByPrimaryKey(smsDelaysDeliveryReq.getId());
        Boolean flagSelect = (null == selectSmsDelaysDelivery || null == selectSmsDelaysDelivery.getDelaysStatus());
        if(flagSelect){
            logger.info("延期索赔单申诉异常 索赔单号:{}",smsDelaysDeliveryReq.getDelaysNo());
            throw new BusinessException("此延期索赔单不存在");
        }
        Boolean flagSelectStatus = DeplayStatusEnum.DELAYS_STATUS_1.getCode().equals(selectSmsDelaysDelivery.getDelaysStatus())
                ||DeplayStatusEnum.DELAYS_STATUS_7.getCode().equals(selectSmsDelaysDelivery.getDelaysStatus());
        if(!flagSelectStatus){
            logger.info("延期索赔单申诉状态异常 索赔单号:{}",smsDelaysDeliveryReq.getDelaysNo());
            throw new  BusinessException("此延期索赔单不可再申诉");
        }
        //修改延期索赔单
        smsDelaysDeliveryReq.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_4.getCode());
        smsDelaysDeliveryReq.setComplaintDate(new Date());
        smsDelaysDeliveryMapper.updateByPrimaryKeySelective(smsDelaysDeliveryReq);
        //修改文件信息
        String orderNo = selectSmsDelaysDelivery.getDelaysNo();
        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        return uplodeFileResult;
    }

    @Override
    public R supplierConfirm(String ids) {
        List<SmsDelaysDelivery> selectListResult =  smsDelaysDeliveryMapper.selectByIds(ids);
        for(SmsDelaysDelivery smsDelaysDelivery : selectListResult){
            Boolean flagResult = DeplayStatusEnum.DELAYS_STATUS_1.getCode().equals(smsDelaysDelivery.getDelaysStatus())
                    ||DeplayStatusEnum.DELAYS_STATUS_7.getCode().equals(smsDelaysDelivery.getDelaysStatus());
            if(!flagResult){
                throw new BusinessException("请确认延期索赔单状态是否为待供应商确认");
            }
            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_11.getCode());
            smsDelaysDelivery.setSettleFee(smsDelaysDelivery.getDelaysAmount());
            smsDelaysDelivery.setSupplierConfirmDate(new Date());
        }
        int count = smsDelaysDeliveryMapper.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }

    /**
     * 48未确认超时发送邮件
     * @return 成功或失败
     */
    @Override
    public R overTimeSendMail() {
        //1.查询状态是待供应商确认的 提交时间<=2天前的 >3天前的
        String twoDate = DateUtils.getDaysTimeString(-2);
        String threeDate = DateUtils.getDaysTimeString(-3);
        List<SmsDelaysDelivery> smsDelaysDeliveryList = overTimeSelect(twoDate,threeDate);
        if(CollectionUtils.isEmpty(smsDelaysDeliveryList)){
            return R.ok();
        }
        //2.发送邮件
        for(SmsDelaysDelivery smsDelaysDelivery : smsDelaysDeliveryList){
            String supplierCode = smsDelaysDelivery.getSupplierCode();
            //根据供应商编号查询供应商信息
            R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
            if(!sysUserR.isSuccess()){
                logger.error("定时发送邮件时查询供应商信息失败供应商编号 supplierCode:{}",supplierCode);
                throw new BusinessException("定时发送邮件时查询供应商信息失败");
            }
            SysUserVo sysUser = sysUserR.getData(SysUserVo.class);
            String mailSubject = "延期索赔邮件";
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条延期索赔订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(smsDelaysDelivery.getSupplierName()).append("+").append(supplierCode).append("+")
                    .append(sysUser.getCorporation()).append(" ").append("您有一条延期索赔订单，订单号")
                    .append(smsDelaysDelivery.getDelaysNo()).append(",请及时处理，如不处理，1天后系统自动确认，无法申诉");
            String toSupplier = sysUser.getEmail();
            mailService.sendTextMail(toSupplier,mailTextBuffer.toString(),mailSubject);
        }
        return R.ok();
    }

    /**
     * 获取超时未确认的列表
     * @param submitDateStart 提交时间起始值
     * @param submitDateEnd 提交时间结束值
     * @return
     */
    private List<SmsDelaysDelivery> overTimeSelect(String submitDateStart,String submitDateEnd){
        Example example = new Example(SmsDelaysDelivery.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("delaysStatus",DeplayStatusEnum.DELAYS_STATUS_1);
        criteria.andGreaterThanOrEqualTo("submitDate",submitDateStart);
        criteria.andLessThan("submitDate",submitDateEnd);
        List<SmsDelaysDelivery> smsDelaysDeliveryList = smsDelaysDeliveryMapper.selectByExample(example);
        return smsDelaysDeliveryList;
    }
    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    @Override
    public R overTimeConfim() {
        //1.查询状态是待供应商确认的 提交时间<=3天前的 >4天前的
        String threeDate = DateUtils.getDaysTimeString(-3);
        String fourDate = DateUtils.getDaysTimeString(-4);
        List<SmsDelaysDelivery> smsDelaysDeliveryList = overTimeSelect(threeDate,fourDate);
        int count = 0;
        if(!CollectionUtils.isEmpty(smsDelaysDeliveryList)){
            for(SmsDelaysDelivery smsDelaysDelivery : smsDelaysDeliveryList){
                smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_11.getCode());
                smsDelaysDelivery.setSettleFee(smsDelaysDelivery.getDelaysAmount());
                smsDelaysDelivery.setSupplierConfirmDate(new Date());
            }
            count = smsDelaysDeliveryMapper.updateBatchByPrimaryKeySelective(smsDelaysDeliveryList);
        }
        return R.data(count);
    }
}