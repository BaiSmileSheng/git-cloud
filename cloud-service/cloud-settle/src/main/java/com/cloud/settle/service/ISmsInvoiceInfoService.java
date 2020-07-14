package com.cloud.settle.service;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.vo.SmsInvoiceInfoSVo;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;

import java.util.List;

/**
 * 发票信息 Service接口
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
public interface ISmsInvoiceInfoService extends BaseService<SmsInvoiceInfo> {

    /**
     * 批量新增或修改保存发票信息
     * @param smsInvoiceInfoS 发票信息集合
     */
    R batchAddSaveOrUpdate(SmsInvoiceInfoSVo smsInvoiceInfoS);


    /**
     * 根据月度结算单号查询
     * @param mouthSettleId
     * @return
     */
	List<SmsInvoiceInfo> selectByMouthSettleId(String mouthSettleId);


}
