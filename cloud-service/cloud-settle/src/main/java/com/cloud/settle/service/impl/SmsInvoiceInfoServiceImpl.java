package com.cloud.settle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.PO.SmsInvoiceInfoS;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.enums.MonthSettleStatusEnum;
import com.cloud.settle.mapper.SmsInvoiceInfoMapper;
import com.cloud.settle.service.ISmsInvoiceInfoService;
import com.cloud.settle.service.ISmsMouthSettleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
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
     * @param smsInvoiceInfoS 发票信息集合
     */
    @Override
    public R batchAddSaveOrUpdate(SmsInvoiceInfoS smsInvoiceInfoS) {
        String mouthSettleId = smsInvoiceInfoS.getMouthSettleId();
        logger.info("批量新增或修改保存发票信息 结算单号:{}",mouthSettleId);
        //1.校验状态,只有待结算才可以录入发票
        Example example= new Example(SmsMouthSettle.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andEqualTo("settleNo",mouthSettleId);
        SmsMouthSettle smsMouthSettle = smsMouthSettleService.selectOneByExample(example);
        if(null == smsMouthSettle || !MonthSettleStatusEnum.YD_SETTLE_STATUS_DJS.getCode().equals(smsMouthSettle.getSettleStatus())){
            throw new BusinessException("月度结算单已确认,不允许修改发票");
        }
        List<SmsInvoiceInfo> smsInvoiceInfoList = smsInvoiceInfoS.getSmsInvoiceInfoList();
        //2.按结算单号先删除后增加
        Example exampleInvoiceInfo = new Example(SmsInvoiceInfo.class);
        Example.Criteria criteriaInvoiceInfo = exampleInvoiceInfo.createCriteria();
        criteriaInvoiceInfo.andEqualTo("mouthSettleId",mouthSettleId);
        smsInvoiceInfoMapper.deleteByExample(exampleInvoiceInfo);
        BigDecimal invoiceFeeAll = BigDecimal.ZERO;
        for(SmsInvoiceInfo smsInvoiceInfo : smsInvoiceInfoList){
            smsInvoiceInfo.setMouthSettleId(mouthSettleId);
            smsInvoiceInfo.setDelFlag(DeleteFlagConstants.NO_DELETED);
            invoiceFeeAll = invoiceFeeAll.add(smsInvoiceInfo.getInvoiceAmount());
        }
        smsInvoiceInfoMapper.insertList(smsInvoiceInfoList);

        //3.修改月度结算单发票金额
        SmsMouthSettle smsMouthSettleReq = new SmsMouthSettle();
        smsMouthSettleReq.setId(smsMouthSettle.getId());
        smsMouthSettleReq.setInvoiceFee(invoiceFeeAll);
        smsMouthSettleService.updateByPrimaryKeySelective(smsMouthSettleReq);
        return R.ok();
    }

    /**
     * 根据月度结算单号查询
     * @param mouthSettleId
     * @return
     */
	@Override
	public List<SmsInvoiceInfo> selectByMouthSettleId(String mouthSettleId){
		 return smsInvoiceInfoMapper.selectByMouthSettleId(mouthSettleId);
	}



}
