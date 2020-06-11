package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.webServicePO.QryPaysSoapRequest;
import com.cloud.settle.domain.webServicePO.QryPaysSoapResponse;

/**
 *
 *
 * @author Lihongxia
 * @date 2020-06-09
 */
public interface IQryPaysSoapService {

    /**
     * 定时任务调用查询付款结果更新月度结算信息
     * @return
     */
    R updateKmsStatus();

    /**
     * 查询付款结果
     * @param qryPaysSoapRequest 查询付款结果入参
     * @return 付款结果
     */
    QryPaysSoapResponse queryBill(QryPaysSoapRequest qryPaysSoapRequest);

}
