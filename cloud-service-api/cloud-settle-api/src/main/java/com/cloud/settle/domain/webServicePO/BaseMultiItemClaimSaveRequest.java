
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
    @ApiModelProperty(value = "发票信息集合")
    @XmlElement(nillable = true)
    protected List<BaseClaimDetail> claimDetailList;
    @ApiModelProperty(value = "付款公司编码")
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
    @ApiModelProperty(value = "外围系统单号")
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
    @ApiModelProperty(value = "申请人工号")
    protected String userNo;
    @ApiModelProperty(value = "供应商编码")
    protected String vendorCode;

    /**
     * ��ȡannexSheets���Ե�ֵ��
     * 
     */
    public int getAnnexSheets() {
        return annexSheets;
    }

    /**
     * ����annexSheets���Ե�ֵ��
     * 
     */
    public void setAnnexSheets(int value) {
        this.annexSheets = value;
    }

    /**
     * ��ȡapplyDate���Ե�ֵ��
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
     * ����applyDate���Ե�ֵ��
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
     * ��ȡbusinessDesc���Ե�ֵ��
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
     * ����businessDesc���Ե�ֵ��
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
     * ��ȡbusinessType���Ե�ֵ��
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
     * ����businessType���Ե�ֵ��
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

    /**
     * ��ȡcompanyCode���Ե�ֵ��
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
     * ����companyCode���Ե�ֵ��
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
     * ��ȡcontractCode���Ե�ֵ��
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
     * ����contractCode���Ե�ֵ��
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
     * ��ȡcurrency���Ե�ֵ��
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
     * ����currency���Ե�ֵ��
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
     * ��ȡdepartmentCode���Ե�ֵ��
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
     * ����departmentCode���Ե�ֵ��
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
     * ��ȡlegContractNo���Ե�ֵ��
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
     * ����legContractNo���Ե�ֵ��
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
     * ��ȡorderCode���Ե�ֵ��
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
     * ����orderCode���Ե�ֵ��
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
     * ��ȡoriginDocNo���Ե�ֵ��
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
     * ����originDocNo���Ե�ֵ��
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
     * ��ȡoriginSystem���Ե�ֵ��
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
     * ����originSystem���Ե�ֵ��
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
     * ��ȡpayAccountCode���Ե�ֵ��
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
     * ����payAccountCode���Ե�ֵ��
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
     * ��ȡpayHbankCode���Ե�ֵ��
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
     * ����payHbankCode���Ե�ֵ��
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
     * ��ȡpayReason���Ե�ֵ��
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
     * ����payReason���Ե�ֵ��
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
     * ��ȡpaybleDate���Ե�ֵ��
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
     * ����paybleDate���Ե�ֵ��
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
     * ��ȡpaymentMethod���Ե�ֵ��
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
     * ����paymentMethod���Ե�ֵ��
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
     * ��ȡpaymentType���Ե�ֵ��
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
     * ����paymentType���Ե�ֵ��
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
     * ��ȡprojectCode���Ե�ֵ��
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
     * ����projectCode���Ե�ֵ��
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
     * ��ȡrecAccount���Ե�ֵ��
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
     * ����recAccount���Ե�ֵ��
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
     * ��ȡrecBank���Ե�ֵ��
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
     * ����recBank���Ե�ֵ��
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
     * ��ȡrecCity���Ե�ֵ��
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
     * ����recCity���Ե�ֵ��
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
     * ��ȡrecRegion���Ե�ֵ��
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
     * ����recRegion���Ե�ֵ��
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
     * ��ȡreserved1���Ե�ֵ��
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
     * ����reserved1���Ե�ֵ��
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
     * ��ȡreserved2���Ե�ֵ��
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
     * ����reserved2���Ե�ֵ��
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
     * ��ȡreserved3���Ե�ֵ��
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
     * ����reserved3���Ե�ֵ��
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
     * ��ȡreserved4���Ե�ֵ��
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
     * ����reserved4���Ե�ֵ��
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
     * ��ȡreserved5���Ե�ֵ��
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
     * ����reserved5���Ե�ֵ��
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
     * ��ȡreserved6���Ե�ֵ��
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
     * ����reserved6���Ե�ֵ��
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
     * ��ȡuserNo���Ե�ֵ��
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
     * ����userNo���Ե�ֵ��
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
     * ��ȡvendorCode���Ե�ֵ��
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
     * ����vendorCode���Ե�ֵ��
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
