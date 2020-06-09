package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsScrapOrder;

import java.util.List;

/**
 * 报废申请 Service接口
 *
 * @author cs
 * @date 2020-05-29
 */
public interface ISmsScrapOrderService extends BaseService<SmsScrapOrder> {

    /**
     * 编辑报废申请单功能  --有状态校验
     * @param smsScrapOrder
     * @return
     */
    R editSave(SmsScrapOrder smsScrapOrder);

    /**
     * 新增保存报废申请
     * @param smsScrapOrder
     * @return
     */
    R addSave(SmsScrapOrder smsScrapOrder);

    /**
     * 删除报废申请
     * @param ids
     * @return
     */
    R remove(String ids);

    /**
     * 根据月份和状态查询
     * @param month
     * @param scrapStatus
     * @return
     */
    List<SmsScrapOrder> selectByMonthAndStatus(String month, List<String> scrapStatus);
}
