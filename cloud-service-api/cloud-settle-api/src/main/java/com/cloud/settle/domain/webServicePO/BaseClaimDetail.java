
package com.cloud.settle.domain.webServicePO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>baseClaimDetail complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="baseClaimDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="applyAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="expenseItem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="invoiceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="invoiceNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="taxAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="taxRate" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseClaimDetail", propOrder = {
    "applyAmount",
    "expenseItem",
    "invoiceDate",
    "invoiceNo",
    "taxAmount",
    "taxRate"
})
@ApiModel(value = "发票信息")
public class BaseClaimDetail {

    @ApiModelProperty(value = "申请金额，若有发票，则为含税金额",required = true)
    protected BigDecimal applyAmount;
    @ApiModelProperty(value = "费用项目编码    ，传其他加工费编码：6666021702",required = false)
    protected String expenseItem = "6666021702";
    @ApiModelProperty(value = "发票日期（yyyy-MM-dd），若paymentType=01，则必填",required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar invoiceDate;
    @ApiModelProperty(value = "发票号，若paymentType=01，则必填",required = true)
    protected String invoiceNo;
    @ApiModelProperty(value = "税额，若paymentType=01，则必填",required = true)
    protected BigDecimal taxAmount;
    @ApiModelProperty(value = "税率（如13%，传0.13），若paymentType=01，则必填",required = true)
    protected BigDecimal taxRate;

    /**
     * 获取applyAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    /**
     * 设置applyAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setApplyAmount(BigDecimal value) {
        this.applyAmount = value;
    }

    /**
     * 获取expenseItem属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExpenseItem() {
        return expenseItem;
    }

    /**
     * 设置expenseItem属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExpenseItem(String value) {
        this.expenseItem = value;
    }

    /**
     * 获取invoiceDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * 设置invoiceDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setInvoiceDate(XMLGregorianCalendar value) {
        this.invoiceDate = value;
    }

    /**
     * 获取invoiceNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInvoiceNo() {
        return invoiceNo;
    }

    /**
     * 设置invoiceNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInvoiceNo(String value) {
        this.invoiceNo = value;
    }

    /**
     * 获取taxAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * 设置taxAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTaxAmount(BigDecimal value) {
        this.taxAmount = value;
    }

    /**
     * 获取taxRate属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * 设置taxRate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTaxRate(BigDecimal value) {
        this.taxRate = value;
    }

}
