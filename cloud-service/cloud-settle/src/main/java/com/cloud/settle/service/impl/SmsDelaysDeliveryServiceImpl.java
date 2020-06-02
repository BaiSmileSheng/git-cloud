package com.cloud.settle.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.mail.MailService;
import com.cloud.settle.mapper.SmsDelaysDeliveryMapper;
import com.cloud.settle.service.ISequeceService;
import com.cloud.settle.service.ISmsDelaysDeliveryService;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteOssService;
import com.cloud.system.feign.RemoteUserService;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private ISequeceService sequeceService;

    @Autowired
    private RemoteOssService remoteOssService;

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
     * 查询延期交付索赔详情
     * @param id 主键id
     * @return 延期交付索赔详情(包含文件信息)
     */
    @Override
    public R selectById(Long id) {
        SmsDelaysDelivery smsDelaysDeliveryRes = this.selectByPrimaryKey(id);
        if(null != smsDelaysDeliveryRes || StringUtils.isNotBlank(smsDelaysDeliveryRes.getDelaysNo())){
            //索赔文件编号
            String claimOrderNo = smsDelaysDeliveryRes.getDelaysNo();
            List<SysOss> sysOssList = remoteOssService.listByOrderNo(claimOrderNo);
            Map<String,Object> map = new HashMap<>();
            map.put("smsQualityOrder",smsDelaysDeliveryRes);
            map.put("sysOssList",sysOssList);
            return R.ok(map);
        }

        return R.error("查询索赔单失败");
    }

    /**
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     * @return 成功或失败
     */
    @Transactional
    @Override
    public R batchAddDelaysDelivery() {
        //TODO 调用订单接口获取 待生成延期索赔的订单(每日凌晨取前一天订单状态为“关单”的排产订单，计算交货时间大于订单应交货日期时)
        List<SmsDelaysDelivery> smsDelaysDeliveryList = new ArrayList<>();
        //供应商编号
        Set<String>  supplierSet = new HashSet<>();
//        for(){
            SmsDelaysDelivery smsDelaysDelivery = changeOmsProductionOrder();
            smsDelaysDelivery.setDelaysAmount(DELAYS_AMOUNT);
            smsDelaysDelivery.setSubmitDate(new Date());
            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_1.getCode());
            //获取索赔单号
            StringBuffer qualityNoBuffer = new StringBuffer(DELAYS_ORDER_PRE);
            qualityNoBuffer.append(DateUtils.getDate().replace("-",""));
            String seq = sequeceService.selectSeq(DELAYS_SEQ_NAME,DELAYS_SEQ_LENGTH);
            qualityNoBuffer.append(seq);
            smsDelaysDelivery.setDelaysNo(qualityNoBuffer.toString());

//            smsDelaysDeliveryList.add(smsDelaysDelivery);
            supplierSet.add(smsDelaysDelivery.getSupplierCode());
//        }
        //插入延期索赔信息
        int count = smsDelaysDeliveryMapper.insertList(smsDelaysDeliveryList);
        //供应商V码对应的供应商信息
        Map<String,SysUser> mapSysUser = new HashMap<>();
        //获取供应商信息
        for(String supplierCode : supplierSet){
            SysUser sysUser = remoteUserService.findUserBySupplierCode(supplierCode);
            mapSysUser.put(supplierCode,sysUser);
        }
        //发送邮件
        String mailSubject = "延期索赔邮件";
        for(SmsDelaysDelivery smsDelaysDeliveryMail :smsDelaysDeliveryList ){
            String supplierCode = smsDelaysDeliveryMail.getSupplierCode();
            SysUser sysUser = mapSysUser.get(supplierCode);
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条延期交付订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(smsDelaysDelivery.getSupplierName()).append("+").append(supplierCode).append("+")
                    .append(sysUser.getCorporation()).append(" ").append("您有一条延期交付订单，订单号")
                    .append(smsDelaysDelivery.getProductOrderCode()).append(",请及时处理，如不处理，3天后系统自动确认，无法申诉");
            String toSupplier = sysUser.getEmail();
            mailService.sendTextMail(mailSubject,mailTextBuffer.toString(),toSupplier);
        }
        return R.ok();
    }


    /**
     * 将排产订单信息赋值给延期索赔信息
     * @return
     */
    private SmsDelaysDelivery changeOmsProductionOrder(){
        return new SmsDelaysDelivery();
    }

    /**
     * 延期索赔单供应商申诉(包含文件信息)
     * @param smsDelaysDeliveryReq 延期索赔信息
     * @return 延期索赔单供应商申诉结果成功或失败
     */
    //@GlobalTransactional
    @Override
    public R supplierAppeal(SmsDelaysDelivery smsDelaysDeliveryReq, MultipartFile[] files) {
        SmsDelaysDelivery selectSmsDelaysDelivery = this.selectByPrimaryKey(smsDelaysDeliveryReq.getId());
        Boolean flagSelect = (null == selectSmsDelaysDelivery || null == selectSmsDelaysDelivery.getDelaysStatus());
        if(flagSelect){
            logger.info("延期索赔单申诉异常 索赔单号:{}",smsDelaysDeliveryReq.getDelaysNo());
            return R.error("此延期索赔单不存在");
        }
        Boolean flagSelectStatus = DeplayStatusEnum.DELAYS_STATUS_1.getCode().equals(selectSmsDelaysDelivery.getDelaysStatus())
                ||DeplayStatusEnum.DELAYS_STATUS_7.getCode().equals(selectSmsDelaysDelivery.getDelaysStatus());
        if(!flagSelectStatus){
            logger.info("延期索赔单申诉状态异常 索赔单号:{}",smsDelaysDeliveryReq.getDelaysNo());
            return R.error("此延期索赔单不可再申诉");
        }
        //修改延期索赔单
        smsDelaysDeliveryReq.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_4.getCode());
        smsDelaysDeliveryReq.setComplaintDate(new Date());
        this.updateByPrimaryKeySelective(smsDelaysDeliveryReq);
        //修改文件信息
        String orderNo = selectSmsDelaysDelivery.getDelaysNo();
        R result = remoteOssService.updateListByOrderNo(orderNo,files);
        return result;
    }

    @Override
    public R supplierConfirm(String ids) {
        List<SmsDelaysDelivery> selectListResult =  smsDelaysDeliveryMapper.selectByIds(ids);
        for(SmsDelaysDelivery smsDelaysDelivery : selectListResult){
            Boolean flagResult = DeplayStatusEnum.DELAYS_STATUS_1.getCode().equals(smsDelaysDelivery.getDelaysStatus())
                    ||DeplayStatusEnum.DELAYS_STATUS_7.getCode().equals(smsDelaysDelivery.getDelaysStatus());
            if(!flagResult){
                return R.error("请确认延期索赔单状态是否为待供应商确认");
            }
            smsDelaysDelivery.setDelaysStatus(DeplayStatusEnum.DELAYS_STATUS_11.getCode());
            smsDelaysDelivery.setSettleFee(smsDelaysDelivery.getDelaysAmount());
            smsDelaysDelivery.setSupplierConfirmDate(new Date());
        }
        int count = this.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }
}
