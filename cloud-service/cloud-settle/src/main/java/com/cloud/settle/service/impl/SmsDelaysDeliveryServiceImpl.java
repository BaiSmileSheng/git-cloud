package com.cloud.settle.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.enums.DeplayStatusEnum;
import com.cloud.settle.mail.MailService;
import com.cloud.settle.mapper.SmsDelaysDeliveryMapper;
import com.cloud.settle.service.ISmsDelaysDeliveryService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private SmsDelaysDeliveryMapper smsDelaysDeliveryMapper;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MailService mailService;

    private final static BigDecimal DELAYS_AMOUNT = new BigDecimal(-2000);//延期索赔金额
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
}
