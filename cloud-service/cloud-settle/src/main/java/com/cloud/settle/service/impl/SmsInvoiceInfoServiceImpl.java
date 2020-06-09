package com.cloud.settle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.enums.MonthSettleStatusEnum;
import com.cloud.settle.service.ISmsMouthSettleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsInvoiceInfoMapper;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import com.cloud.settle.service.ISmsInvoiceInfoService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 发票信息 Service业务层处理
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@Service
public class SmsInvoiceInfoServiceImpl extends BaseServiceImpl<SmsInvoiceInfo> implements ISmsInvoiceInfoService {

    private static Logger logger = LoggerFactory.getLogger(SmsInvoiceInfoServiceImpl.class);

    @Autowired
    private SmsInvoiceInfoMapper smsInvoiceInfoMapper;

    @Autowired
    private ISmsMouthSettleService smsMouthSettleService;

    /**
     * 批量新增或修改保存发票信息
     * @param smsInvoiceInfo 发票信息集合
     */
    @Override
    public R batchAddSaveOrUpdate(SmsInvoiceInfo smsInvoiceInfo) {
        SmsInvoiceInfo smsInvoiceInfoFist = smsInvoiceInfo.getSmsInvoiceInfoList().get(0);
        String mouthSettleId = smsInvoiceInfoFist.getMouthSettleId();
        logger.info("批量新增或修改保存发票信息 结算单号:{}",smsInvoiceInfo.getMouthSettleId());
        SmsMouthSettle smsMouthSettle = smsMouthSettleService.selectByPrimaryKey(mouthSettleId);
        if(null == smsMouthSettle || !MonthSettleStatusEnum.YD_SETTLE_STATUS_DJS.getCode().equals(smsMouthSettle.getSettleStatus())){
            throw new BusinessException("月度结算单已确认,不允许修改发票");
        }
        List<SmsInvoiceInfo> smsInvoiceInfoList = smsInvoiceInfo.getSmsInvoiceInfoList();
        //新增的
        List<SmsInvoiceInfo> smsInvoiceInfoAddList = new ArrayList<>();
        //修改的
        List<SmsInvoiceInfo> smsInvoiceInfoUpdateList = new ArrayList<>();
        for(SmsInvoiceInfo smsInvoiceInfoReq : smsInvoiceInfoList){
            if(null != smsInvoiceInfoReq.getId()){
                smsInvoiceInfoUpdateList.add(smsInvoiceInfoReq);
            }else{
                smsInvoiceInfoReq.setDelFlag(DeleteFlagConstants.NO_DELETED);
                smsInvoiceInfoReq.setCreateTime(new Date());
                smsInvoiceInfoAddList.add(smsInvoiceInfoReq);
            }
        }
        if(!CollectionUtils.isEmpty(smsInvoiceInfoAddList)){
           int countAdd = smsInvoiceInfoMapper.insertList(smsInvoiceInfoAddList);
           if(countAdd == 0){
               logger.error("批量新增或修改保存发票信息时异常 smsInvoiceInfoAddList:{}", JSONObject.toJSONString(smsInvoiceInfoAddList));
               throw new BusinessException("批量新增或修改保存发票信息时异常");
           }
        }
        if(!CollectionUtils.isEmpty(smsInvoiceInfoUpdateList)){
            int countUpdate = smsInvoiceInfoMapper.updateBatchByPrimaryKeySelective(smsInvoiceInfoUpdateList);
            if(countUpdate == 0){
                logger.error("批量新增或修改保存发票信息时异常 smsInvoiceInfoUpdateList:{}", JSONObject.toJSONString(smsInvoiceInfoUpdateList));
                throw new BusinessException("批量新增或修改保存发票信息时异常");
            }
        }
        return R.ok();
    }
}
