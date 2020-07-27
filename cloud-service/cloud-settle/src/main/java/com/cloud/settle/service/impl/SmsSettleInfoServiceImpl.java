package com.cloud.settle.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.enums.SettleInfoOrderStatusEnum;
import com.cloud.settle.mapper.SmsSettleInfoMapper;
import com.cloud.settle.service.ISmsSettleInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 加工费结算 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Service
public class SmsSettleInfoServiceImpl extends BaseServiceImpl<SmsSettleInfo> implements ISmsSettleInfoService {

    private static Logger logger = LoggerFactory.getLogger(SmsSettleInfoServiceImpl.class);

    @Autowired
    private SmsSettleInfoMapper smsSettleInfoMapper;

    /**
     * 计算加工费(定时任务调用)
     * @return 成功或失败
     */
    @Transactional
    @Override
    public R smsSettleInfoCalculate() {
        logger.info("加工费生成开始");
        //1.查询已关单的加工费结算信息表--加工费结算
        SmsSettleInfo smsSettleInfoReq = new SmsSettleInfo();
        smsSettleInfoReq.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_2.getCode());
        Example example = new Example(SmsSettleInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderStatus", smsSettleInfoReq.getOrderStatus());
        List<SmsSettleInfo> smsSettleInfoList = this.selectByExample(example);
        if(!CollectionUtils.isEmpty(smsSettleInfoList)){
            logger.info("加工费生成,无需要生成加工费的数据");
            return R.ok();
        }
        //2.计算加工费 (订单交货数量*sap加工费单价=生产订单加工费金额 )
        //3.修改加工费结算信息表--加工费结算金额,状态为待结算
        smsSettleInfoList.forEach(smsSettleInfo ->{
            if (null != smsSettleInfo.getMachiningPrice() && null != smsSettleInfo.getConfirmAmont()) {
                BigDecimal settlePrice = smsSettleInfo.getMachiningPrice().multiply(new BigDecimal(smsSettleInfo.getConfirmAmont()));
                smsSettleInfo.setSettlePrice(settlePrice);
                smsSettleInfo.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_11.getCode());
            }
        });
        updateBatchByPrimaryKeySelective(smsSettleInfoList);
        logger.info("加工费生成结束");
        return R.ok();
    }

    /**
     * 根据供应商编码、付款公司、订单状态、月份（基本开始日期）更新数据
     * @param updated
     * @param supplierCode
     * @param companyCode
     * @param orderStatus
     * @param month
     * @return
     */
	@Override
	public int updateBySupplierCodeAndCompanyCodeAndOrderStatusAndMonth(SmsSettleInfo updated,String supplierCode,String companyCode,String orderStatus,String month){
		 return smsSettleInfoMapper.updateBySupplierCodeAndCompanyCodeAndOrderStatusAndMonth(updated,supplierCode,companyCode,orderStatus,month);
	}

    @Override
    public List<SmsSettleInfo> selectForMonthSettle(String month, String orderStatus) {
        return smsSettleInfoMapper.selectForMonthSettle(month,orderStatus);
    }

    /**
     * 费用结算单明细（打印用）
     * @param smsSettleInfo
     * @return
     */
    @Override
    public R selectInfoForPrint(SmsSettleInfo smsSettleInfo) {
        if (BeanUtil.isEmpty(smsSettleInfo)) {
            return R.error("参数为空！");
        }
        Example example = new Example(SmsSettleInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("settleNo",smsSettleInfo.getSettleNo());
        //根据结算单号查询结算信息
        List<SmsSettleInfo> smsSettleInfoList = selectByExample(example);
        if (CollUtil.isEmpty(smsSettleInfoList)) {
            return R.error("无结算信息！");
        }
        for (SmsSettleInfo settleInfo : smsSettleInfoList) {
            Date actualEndDate=settleInfo.getActualEndDate();
            if (actualEndDate == null) {
                continue;
            }
            int dayNum = DateUtil.dayOfMonth(actualEndDate);
            settleInfo.setDayNum(dayNum);
            //合计金额（订单数量*不含税单价） 与结算金额相等
            settleInfo.setTotalMoney(settleInfo.getSettlePrice());
        }
        return R.data(smsSettleInfoList);
    }

    /**
     * 根据生产订单号批量修改
     * @param list
     * @return
     */
    @Override
    public R batchUpdateByProductOrderCode(List<SmsSettleInfo> list){
        int count = smsSettleInfoMapper.batchUpdateByProductOrderCode(list);
        return R.data(count);
    }
}
