package com.cloud.settle.domain.webServicePO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 付款查询出参
 * @Author Lihongxia
 * @Date 2020-06-10
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlRootElement(name = "result")
public class QryPaysSoapResponse {

    @ApiModelProperty(value = "返回标志 返回结果状态标志，0 失败 1 成功 -1 验证失败,单据不存在 -2 异常")
    private String flag;

    @ApiModelProperty(value = "返回消息 消息内容")
    private String mess;

    @ApiModelProperty(value = "单据状态 S:成功，F:单据不存在，I:支付中，A:放弃支付")
    private String status;

    @ApiModelProperty(value = "付款单号 资金系统的付款单单据号 ")
    private String docno;

    @ApiModelProperty(value = "交易日期 付款成功日期只有成功的时候才有这个日期 格式：yyyyMMdd")
    private String tradedate;

    @ApiModelProperty(value = "kms单号")
    private String kmsNo;
}
