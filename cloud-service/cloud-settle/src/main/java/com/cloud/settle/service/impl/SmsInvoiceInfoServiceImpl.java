package com.cloud.settle.service.impl;

import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.settle.domain.entity.vo.SmsInvoiceInfoSVo;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
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
    @Transactional(rollbackFor=Exception.class)
    public R batchAddSaveOrUpdate(SmsInvoiceInfoSVo smsInvoiceInfoS) {
        String mouthSettleId = smsInvoiceInfoS.getMouthSettleId();
        logger.info("批量新增或修改保存发票信息 结算单号:{}",mouthSettleId);
        //1.校验状态,只有待结算才可以录入发票
        Example example= new Example(SmsMouthSettle.class);
        Example.Criteria criteria= example.createCriteria();
        criteria.andEqualTo("settleNo",mouthSettleId);
        criteria.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        SmsMouthSettle smsMouthSettle = smsMouthSettleService.selectOneByExample(example);
        if(null == smsMouthSettle || !MonthSettleStatusEnum.YD_SETTLE_STATUS_DFPLR.getCode().equals(smsMouthSettle.getSettleStatus())){
            throw new BusinessException("发票已提交,不允许修改发票");
        }
        //2.校验金额相差不足1才可提交,按结算单号先删除后增加
        List<SmsInvoiceInfo> smsInvoiceInfoList = smsInvoiceInfoS.getSmsInvoiceInfoList();
        BigDecimal invoiceFeeAll = BigDecimal.ZERO;
        if(!CollectionUtils.isEmpty(smsInvoiceInfoList)){
            for(SmsInvoiceInfo smsInvoiceInfo : smsInvoiceInfoList){
                smsInvoiceInfo.setMouthSettleId(mouthSettleId);
                smsInvoiceInfo.setDelFlag(DeleteFlagConstants.NO_DELETED);
                invoiceFeeAll = invoiceFeeAll.add(smsInvoiceInfo.getInvoiceAmount());
            }
        }
        BigDecimal includeTaxeFee = smsMouthSettle.getIncludeTaxeFee();
        BigDecimal includeTaxeFeeX = includeTaxeFee.subtract(BigDecimal.ONE);
        BigDecimal includeTaxeFeeD = includeTaxeFee.add(BigDecimal.ONE);
        //如果比最小值小 比最大值大,则不允许修改
        if ("2".equals(smsInvoiceInfoS.getTypeFlag())) {//2、提交
            if(invoiceFeeAll.compareTo(includeTaxeFeeX) == -1
                    || invoiceFeeAll.compareTo(includeTaxeFeeD) == 1){
                throw new BusinessException("请确认填写发票总金额和月度结算含税金额相差不到1");
            }
        }
        Example exampleInvoiceInfo = new Example(SmsInvoiceInfo.class);
        Example.Criteria criteriaInvoiceInfo = exampleInvoiceInfo.createCriteria();
        criteriaInvoiceInfo.andEqualTo("mouthSettleId",mouthSettleId);
        smsInvoiceInfoMapper.deleteByExample(exampleInvoiceInfo);
        if(!CollectionUtils.isEmpty(smsInvoiceInfoList)){
            smsInvoiceInfoMapper.insertList(smsInvoiceInfoList);
        }
        //3.修改月度结算单发票金额
        SmsMouthSettle smsMouthSettleReq = new SmsMouthSettle();
        smsMouthSettleReq.setId(smsMouthSettle.getId());
        smsMouthSettleReq.setInvoiceFee(invoiceFeeAll);
        smsMouthSettleService.updateByPrimaryKeySelective(smsMouthSettleReq);
        return R.data(smsMouthSettle);
    }

    /**
     * 发票提交传kems
     * @param smsInvoiceInfoS 发票信息集合
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R commit(SmsInvoiceInfoSVo smsInvoiceInfoS) {
        R r = batchAddSaveOrUpdate(smsInvoiceInfoS);
        if (!r.isSuccess()) {
            throw new BusinessException(r.get("msg").toString());
        }
        SmsMouthSettle smsMouthSettle = r.getData(SmsMouthSettle.class);
        //传KMS
        smsMouthSettleService.createMultiItemClaim(smsMouthSettle);
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
