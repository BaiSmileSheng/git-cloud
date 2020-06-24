
package com.cloud.settle.domain.webServicePO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>baseMultiItemClaimSaveRequest complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="baseMultiItemClaimSaveRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="annexSheets" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="applyDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="businessDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="businessType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="claimDetailList" type="{http://gems.haier.com/IfBaseClaim_Srv}baseClaimDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="companyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="currency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="departmentCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="legContractNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originDocNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originSystem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payAccountCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payHbankCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="payReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paybleDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentMethod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="paymentType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="projectCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="recAccount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="recBank" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="recCity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="recRegion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reserved1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reserved2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reserved3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reserved4" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="reserved5" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="reserved6" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="userNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vendorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseMultiItemClaimSaveRequest", propOrder = {
    "annexSheets",
    "applyDate",
    "businessDesc",
    "businessType",
    "claimDetailList",
    "companyCode",
    "contractCode",
    "currency",
    "departmentCode",
    "legContractNo",
    "orderCode",
    "originDocNo",
    "originSystem",
    "payAccountCode",
    "payHbankCode",
    "payReason",
    "paybleDate",
    "paymentMethod",
    "paymentType",
    "projectCode",
    "recAccount",
    "recBank",
    "recCity",
    "recRegion",
    "reserved1",
    "reserved2",
    "reserved3",
    "reserved4",
    "reserved5",
    "reserved6",
    "userNo",
    "vendorCode"
})
@ApiModel(value = "创建报账单入参")
public class BaseMultiItemClaimSaveRequest {

    @ApiModelProperty(value = "业务描述，默认传：加工承揽费用",required = false)
    protected String businessDesc = "加工承揽费用";//业务描述，默认传：加工承揽费用
    @ApiModelProperty(value = "附单据数，默认1",required = false)
    protected int annexSheets = 1;//附单据数，默认1
    @ApiModelProperty(value = "申请日期（yyyy-MM-dd）",required = false)
    protected String applyDate;
    @ApiModelProperty(value = "业务类型，默认传：666617",required = false)
    protected String businessType = "666617";//业务类型，默认传：666617
    @ApiModelProperty(value = "发票信息集合",required = true)
    @XmlElement(nillable = true)
    protected List<BaseClaimDetail> claimDetailList;
    @ApiModelProperty(value = "付款公司编码",required = true)
    protected String companyCode;
    @ApiModelProperty(value = "合同号",required = false)
    protected String contractCode;
    @ApiModelProperty(value = "币种编码，默认CNY",required = false)
    protected String currency = "CNY";//币种编码，默认CNY
    @ApiModelProperty(value = "预算体编码    由业务提供每个公司默认预算体信息，Kems系统进行默认",required = false)
    protected String departmentCode;
    @ApiModelProperty(value = "LEG合同号",required = false)
    protected String legContractNo;
    @ApiModelProperty(value = "订单号",required = false)
    protected String orderCode;
    @ApiModelProperty(value = "外围系统单号",required = true)
    protected String originDocNo;
    @ApiModelProperty(value = "来源系统，  默认传：DDPS",required = false)
    protected String originSystem = "DDPS";
    @ApiModelProperty(value = "付款账号，若不传按系统配置带出",required = false)
    protected String payAccountCode;
    @ApiModelProperty(value = "付款开户行，若不传按系统配置带出",required = false)
    protected String payHbankCode;
    @ApiModelProperty(value = "",required = false)
    protected String payReason;
    @ApiModelProperty(value = "计划付款日期（yyyy-MM-dd",required = false)
    protected String paybleDate;
    @ApiModelProperty(value = "付款方式，默认财务公司承兑支付       CWGSCDZF",required = false)
    protected String paymentMethod = "CWGSCDZF";//付款方式，默认财务公司承兑支付       CWGSCDZF
    @ApiModelProperty(value = "发票支付方式（传01、02、03），其中01-发票先到，02-发票后到，03-无发票  传：01",required = false)
    protected String paymentType = "01";
    @ApiModelProperty(value = "立项号",required = false)
    protected String projectCode;
    @ApiModelProperty(value = "",required = false)
    protected String recAccount;
    @ApiModelProperty(value = "",required = false)
    protected String recBank;
    @ApiModelProperty(value = "",required = false)
    protected String recCity;
    @ApiModelProperty(value = "",required = false)
    protected String recRegion;
    @ApiModelProperty(value = "保留字段")
    protected String reserved1;
    @ApiModelProperty(value = "保留字段")
    protected String reserved2;
    @ApiModelProperty(value = "保留字段")
    protected String reserved3;
    @ApiModelProperty(value = "保留字段")
    protected BigDecimal reserved4;
    @ApiModelProperty(value = "保留字段")
    protected BigDecimal reserved5;
    @ApiModelProperty(value = "保留字段")
    protected BigDecimal reserved6;
    @ApiModelProperty(value = "申请人工号",required = true)
    protected String userNo;
    @ApiModelProperty(value = "供应商编码",required = true)
    protected String vendorCode;

    /**
     * 获取annexSheets属性的值。
     *
     */
    public int getAnnexSheets() {
        return annexSheets;
    }

    /**
     * 设置annexSheets属性的值。
     *
     */
    public void setAnnexSheets(int value) {
        this.annexSheets = value;
    }

    /**
     * 获取applyDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getApplyDate() {
        return applyDate;
    }

    /**
     * 设置applyDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApplyDate(String value) {
        this.applyDate = value;
    }

    /**
     * 获取businessDesc属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBusinessDesc() {
        return businessDesc;
    }

    /**
     * 设置businessDesc属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBusinessDesc(String value) {
        this.businessDesc = value;
    }

    /**
     * 获取businessType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * 设置businessType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBusinessType(String value) {
        this.businessType = value;
    }

    /**
     * Gets the value of the claimDetailList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the claimDetailList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClaimDetailList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BaseClaimDetail }
     *
     *
     */
    public List<BaseClaimDetail> getClaimDetailList() {
        if (claimDetailList == null) {
            claimDetailList = new ArrayList<BaseClaimDetail>();
        }
        return this.claimDetailList;
    }

    public void setClaimDetailList(List<BaseClaimDetail> claimDetailList) {
        this.claimDetailList = claimDetailList;
    }

    /**
     * 获取companyCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * 设置companyCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCompanyCode(String value) {
        this.companyCode = value;
    }

    /**
     * 获取contractCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * 设置contractCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContractCode(String value) {
        this.contractCode = value;
    }

    /**
     * 获取currency属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置currency属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * 获取departmentCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDepartmentCode() {
        return departmentCode;
    }

    /**
     * 设置departmentCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDepartmentCode(String value) {
        this.departmentCode = value;
    }

    /**
     * 获取legContractNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLegContractNo() {
        return legContractNo;
    }

    /**
     * 设置legContractNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLegContractNo(String value) {
        this.legContractNo = value;
    }

    /**
     * 获取orderCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderCode() {
        return orderCode;
    }

    /**
     * 设置orderCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderCode(String value) {
        this.orderCode = value;
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
     * 获取originSystem属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOriginSystem() {
        return originSystem;
    }

    /**
     * 设置originSystem属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOriginSystem(String value) {
        this.originSystem = value;
    }

    /**
     * 获取payAccountCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPayAccountCode() {
        return payAccountCode;
    }

    /**
     * 设置payAccountCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPayAccountCode(String value) {
        this.payAccountCode = value;
    }

    /**
     * 获取payHbankCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPayHbankCode() {
        return payHbankCode;
    }

    /**
     * 设置payHbankCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPayHbankCode(String value) {
        this.payHbankCode = value;
    }

    /**
     * 获取payReason属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPayReason() {
        return payReason;
    }

    /**
     * 设置payReason属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPayReason(String value) {
        this.payReason = value;
    }

    /**
     * 获取paybleDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaybleDate() {
        return paybleDate;
    }

    /**
     * 设置paybleDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaybleDate(String value) {
        this.paybleDate = value;
    }

    /**
     * 获取paymentMethod属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * 设置paymentMethod属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaymentMethod(String value) {
        this.paymentMethod = value;
    }

    /**
     * 获取paymentType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * 设置paymentType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaymentType(String value) {
        this.paymentType = value;
    }

    /**
     * 获取projectCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 设置projectCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProjectCode(String value) {
        this.projectCode = value;
    }

    /**
     * 获取recAccount属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRecAccount() {
        return recAccount;
    }

    /**
     * 设置recAccount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRecAccount(String value) {
        this.recAccount = value;
    }

    /**
     * 获取recBank属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRecBank() {
        return recBank;
    }

    /**
     * 设置recBank属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRecBank(String value) {
        this.recBank = value;
    }

    /**
     * 获取recCity属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRecCity() {
        return recCity;
    }

    /**
     * 设置recCity属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRecCity(String value) {
        this.recCity = value;
    }

    /**
     * 获取recRegion属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRecRegion() {
        return recRegion;
    }

    /**
     * 设置recRegion属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRecRegion(String value) {
        this.recRegion = value;
    }

    /**
     * 获取reserved1属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReserved1() {
        return reserved1;
    }

    /**
     * 设置reserved1属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReserved1(String value) {
        this.reserved1 = value;
    }

    /**
     * 获取reserved2属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReserved2() {
        return reserved2;
    }

    /**
     * 设置reserved2属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReserved2(String value) {
        this.reserved2 = value;
    }

    /**
     * 获取reserved3属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReserved3() {
        return reserved3;
    }

    /**
     * 设置reserved3属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReserved3(String value) {
        this.reserved3 = value;
    }

    /**
     * 获取reserved4属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getReserved4() {
        return reserved4;
    }

    /**
     * 设置reserved4属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setReserved4(BigDecimal value) {
        this.reserved4 = value;
    }

    /**
     * 获取reserved5属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getReserved5() {
        return reserved5;
    }

    /**
     * 设置reserved5属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setReserved5(BigDecimal value) {
        this.reserved5 = value;
    }

    /**
     * 获取reserved6属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getReserved6() {
        return reserved6;
    }

    /**
     * 设置reserved6属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setReserved6(BigDecimal value) {
        this.reserved6 = value;
    }

    /**
     * 获取userNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserNo() {
        return userNo;
    }

    /**
     * 设置userNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserNo(String value) {
        this.userNo = value;
    }

    /**
     * 获取vendorCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVendorCode() {
        return vendorCode;
    }

    /**
     * 设置vendorCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVendorCode(String value) {
        this.vendorCode = value;
    }

}