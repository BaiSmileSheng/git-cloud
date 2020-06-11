package com.cloud.settle.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;
import com.cloud.settle.domain.webServicePO.QryPaysSoapRequest;
import com.cloud.settle.domain.webServicePO.QryPaysSoapResponse;
import com.cloud.settle.service.IBaseMutilItemService;
import com.cloud.settle.service.IQryPaysSoapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 付款结果查询接口  提供者
 *
 * @author Lihongxia
 * @date 2020-06-10
 */
@RestController
@RequestMapping("qryPaysSoapController")
@Api(tags = "付款结果查询接口  提供者")
public class QryPaysSoapController extends BaseController {

    @Autowired
    private IQryPaysSoapService qryPaysSoapService;

    /**
     * 定时任务调用查询付款结果更新月度结算信息
     * @return
     */
    @PostMapping("updateKmsStatus")
    @ApiOperation(value = "定时任务调用查询付款结果更新月度结算信息",response = R.class)
    public R updateKmsStatus(){
        R result = qryPaysSoapService.updateKmsStatus();
        return result;
    }

    /**
     * 查询付款结果
     * @param qryPaysSoapRequest 查询付款结果入参
     * @return 付款结果
     */
    @GetMapping("queryBill")
    @ApiOperation(value = "查询付款结果" ,response = QryPaysSoapResponse.class)
    public QryPaysSoapResponse queryBill(QryPaysSoapRequest qryPaysSoapRequest) {
        QryPaysSoapResponse result = qryPaysSoapService.queryBill(qryPaysSoapRequest);
        return result;
    }

}
