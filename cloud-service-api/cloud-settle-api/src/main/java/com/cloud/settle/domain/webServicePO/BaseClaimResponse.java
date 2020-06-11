
package com.cloud.settle.domain.webServicePO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>baseClaimResponse complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="baseClaimResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="failReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gemsDocNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originDocNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="successFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseClaimResponse", propOrder = {
    "failReason",
    "gemsDocNo",
    "originDocNo",
    "successFlag"
})
@ApiModel(value = "创建报账单返回信息")
public class BaseClaimResponse {

    @ApiModelProperty(value = "返回信息")
    protected String failReason;
    @ApiModelProperty(value = "GEMS单据号")
    protected String gemsDocNo;
    @ApiModelProperty(value = "外围系统单据号")
    protected String originDocNo;
    @ApiModelProperty(value = "GEMS返回标记，S成功，F失败")
    protected String successFlag;

    /**
     * 获取failReason属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFailReason() {
        return failReason;
    }

    /**
     * 设置failReason属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFailReason(String value) {
        this.failReason = value;
    }

    /**
     * 获取gemsDocNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGemsDocNo() {
        return gemsDocNo;
    }

    /**
     * 设置gemsDocNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGemsDocNo(String value) {
        this.gemsDocNo = value;
    }

    /**
     * 获取originDocNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOriginDocNo() {
        return originDocNo;
    }

    /**
     * 设置originDocNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOriginDocNo(String value) {
        this.originDocNo = value;
    }

    /**
     * 获取successFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSuccessFlag() {
        return successFlag;
    }

    /**
     * 设置successFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSuccessFlag(String value) {
        this.successFlag = value;
    }

}

