package com.cloud.settle.domain.webServicePO;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 付款查询入参
 * @Author Lihongxia
 * @Date 2020-06-10
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@XmlRootElement(name = "ROW")
public class QryPaysSoapRequest {

    @ApiModelProperty(value = "来源系统 FSC-报账系统，EAS-金蝶，DDPS-订单评审系统",required = true)
    private String ZSOURCE = "DDPS";

    @ApiModelProperty(value = "来源系统的唯一编号，请求号或者业务单据号。",required = true)
    private String DOC_NO;

    @ApiModelProperty(value = "备用字段01")
    private String BAK01;

    @ApiModelProperty(value = "备用字段02")
    private String BAK02;

    @ApiModelProperty(value = "备用字段03")
    private String BAK03;

}
