package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import com.cloud.common.core.service.BaseService;

/**
 * 发票信息 Service接口
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
public interface ISmsInvoiceInfoService extends BaseService<SmsInvoiceInfo> {

    /**
     * 批量新增或修改保存发票信息
     * @param smsInvoiceInfo 发票信息集合
     */
    R batchAddSaveOrUpdate(SmsInvoiceInfo smsInvoiceInfo);

}
