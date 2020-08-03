
package com.cloud.order.webService.wms;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>wmsEntity complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="wmsEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activeFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cosmoType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdByName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="createdDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="createdDateMaxStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="createdDateMinStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="createdDateStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="factoryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gmtCreate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gmtCreateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gmtCreateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gmtCreateStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gmtModified" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gmtModifiedMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gmtModifiedMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="gmtModifiedStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastModifiedBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdByName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastUpdDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastUpdDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastUpdDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastUpdDateStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rows" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sapFactoryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="schema" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined10" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined6" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined7" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined8" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userDefined9" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wmsEntity", propOrder = {
    "activeFlag",
    "cosmoType",
    "createBy",
    "createdBy",
    "createdByName",
    "createdDate",
    "createdDateMax",
    "createdDateMaxStr",
    "createdDateMin",
    "createdDateMinStr",
    "createdDateStr",
    "factoryCode",
    "gmtCreate",
    "gmtCreateMax",
    "gmtCreateMin",
    "gmtCreateStr",
    "gmtModified",
    "gmtModifiedMax",
    "gmtModifiedMin",
    "gmtModifiedStr",
    "lastModifiedBy",
    "lastUpdBy",
    "lastUpdByName",
    "lastUpdDate",
    "lastUpdDateMax",
    "lastUpdDateMin",
    "lastUpdDateStr",
    "page",
    "remark",
    "rows",
    "sapFactoryCode",
    "schema",
    "sign",
    "user",
    "userDefined1",
    "userDefined10",
    "userDefined2",
    "userDefined3",
    "userDefined4",
    "userDefined5",
    "userDefined6",
    "userDefined7",
    "userDefined8",
    "userDefined9"
})
@XmlSeeAlso({
    OdsRawOrderOutStorageDTO.class,
})
public class WmsEntity {

    protected String activeFlag;
    protected String cosmoType;
    protected String createBy;
    protected String createdBy;
    protected String createdByName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdDateMax;
    protected String createdDateMaxStr;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdDateMin;
    protected String createdDateMinStr;
    protected String createdDateStr;
    protected String factoryCode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gmtCreate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gmtCreateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gmtCreateMin;
    protected String gmtCreateStr;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gmtModified;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gmtModifiedMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gmtModifiedMin;
    protected String gmtModifiedStr;
    protected String lastModifiedBy;
    protected String lastUpdBy;
    protected String lastUpdByName;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdDateMin;
    protected String lastUpdDateStr;
    protected int page;
    protected String remark;
    protected int rows;
    protected String sapFactoryCode;
    protected String schema;
    protected String sign;
    protected String user;
    protected String userDefined1;
    protected String userDefined10;
    protected String userDefined2;
    protected String userDefined3;
    protected String userDefined4;
    protected String userDefined5;
    protected String userDefined6;
    protected String userDefined7;
    protected String userDefined8;
    protected String userDefined9;

    /**
     * 获取activeFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getActiveFlag() {
        return activeFlag;
    }

    /**
     * 设置activeFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActiveFlag(String value) {
        this.activeFlag = value;
    }

    /**
     * 获取cosmoType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCosmoType() {
        return cosmoType;
    }

    /**
     * 设置cosmoType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCosmoType(String value) {
        this.cosmoType = value;
    }

    /**
     * 获取createBy属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * 设置createBy属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreateBy(String value) {
        this.createBy = value;
    }

    /**
     * 获取createdBy属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * 设置createdBy属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * 获取createdByName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreatedByName() {
        return createdByName;
    }

    /**
     * 设置createdByName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreatedByName(String value) {
        this.createdByName = value;
    }

    /**
     * 获取createdDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    /**
     * 设置createdDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreatedDate(XMLGregorianCalendar value) {
        this.createdDate = value;
    }

    /**
     * 获取createdDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreatedDateMax() {
        return createdDateMax;
    }

    /**
     * 设置createdDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreatedDateMax(XMLGregorianCalendar value) {
        this.createdDateMax = value;
    }

    /**
     * 获取createdDateMaxStr属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreatedDateMaxStr() {
        return createdDateMaxStr;
    }

    /**
     * 设置createdDateMaxStr属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreatedDateMaxStr(String value) {
        this.createdDateMaxStr = value;
    }

    /**
     * 获取createdDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getCreatedDateMin() {
        return createdDateMin;
    }

    /**
     * 设置createdDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setCreatedDateMin(XMLGregorianCalendar value) {
        this.createdDateMin = value;
    }

    /**
     * 获取createdDateMinStr属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreatedDateMinStr() {
        return createdDateMinStr;
    }

    /**
     * 设置createdDateMinStr属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreatedDateMinStr(String value) {
        this.createdDateMinStr = value;
    }

    /**
     * 获取createdDateStr属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreatedDateStr() {
        return createdDateStr;
    }

    /**
     * 设置createdDateStr属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreatedDateStr(String value) {
        this.createdDateStr = value;
    }

    /**
     * 获取factoryCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFactoryCode() {
        return factoryCode;
    }

    /**
     * 设置factoryCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFactoryCode(String value) {
        this.factoryCode = value;
    }

    /**
     * 获取gmtCreate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置gmtCreate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setGmtCreate(XMLGregorianCalendar value) {
        this.gmtCreate = value;
    }

    /**
     * 获取gmtCreateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getGmtCreateMax() {
        return gmtCreateMax;
    }

    /**
     * 设置gmtCreateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setGmtCreateMax(XMLGregorianCalendar value) {
        this.gmtCreateMax = value;
    }

    /**
     * 获取gmtCreateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getGmtCreateMin() {
        return gmtCreateMin;
    }

    /**
     * 设置gmtCreateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setGmtCreateMin(XMLGregorianCalendar value) {
        this.gmtCreateMin = value;
    }

    /**
     * 获取gmtCreateStr属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGmtCreateStr() {
        return gmtCreateStr;
    }

    /**
     * 设置gmtCreateStr属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGmtCreateStr(String value) {
        this.gmtCreateStr = value;
    }

    /**
     * 获取gmtModified属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置gmtModified属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setGmtModified(XMLGregorianCalendar value) {
        this.gmtModified = value;
    }

    /**
     * 获取gmtModifiedMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getGmtModifiedMax() {
        return gmtModifiedMax;
    }

    /**
     * 设置gmtModifiedMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setGmtModifiedMax(XMLGregorianCalendar value) {
        this.gmtModifiedMax = value;
    }

    /**
     * 获取gmtModifiedMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getGmtModifiedMin() {
        return gmtModifiedMin;
    }

    /**
     * 设置gmtModifiedMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setGmtModifiedMin(XMLGregorianCalendar value) {
        this.gmtModifiedMin = value;
    }

    /**
     * 获取gmtModifiedStr属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGmtModifiedStr() {
        return gmtModifiedStr;
    }

    /**
     * 设置gmtModifiedStr属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGmtModifiedStr(String value) {
        this.gmtModifiedStr = value;
    }

    /**
     * 获取lastModifiedBy属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * 设置lastModifiedBy属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastModifiedBy(String value) {
        this.lastModifiedBy = value;
    }

    /**
     * 获取lastUpdBy属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastUpdBy() {
        return lastUpdBy;
    }

    /**
     * 设置lastUpdBy属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastUpdBy(String value) {
        this.lastUpdBy = value;
    }

    /**
     * 获取lastUpdByName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastUpdByName() {
        return lastUpdByName;
    }

    /**
     * 设置lastUpdByName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastUpdByName(String value) {
        this.lastUpdByName = value;
    }

    /**
     * 获取lastUpdDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastUpdDate() {
        return lastUpdDate;
    }

    /**
     * 设置lastUpdDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastUpdDate(XMLGregorianCalendar value) {
        this.lastUpdDate = value;
    }

    /**
     * 获取lastUpdDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastUpdDateMax() {
        return lastUpdDateMax;
    }

    /**
     * 设置lastUpdDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastUpdDateMax(XMLGregorianCalendar value) {
        this.lastUpdDateMax = value;
    }

    /**
     * 获取lastUpdDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastUpdDateMin() {
        return lastUpdDateMin;
    }

    /**
     * 设置lastUpdDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastUpdDateMin(XMLGregorianCalendar value) {
        this.lastUpdDateMin = value;
    }

    /**
     * 获取lastUpdDateStr属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLastUpdDateStr() {
        return lastUpdDateStr;
    }

    /**
     * 设置lastUpdDateStr属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLastUpdDateStr(String value) {
        this.lastUpdDateStr = value;
    }

    /**
     * 获取page属性的值。
     *
     */
    public int getPage() {
        return page;
    }

    /**
     * 设置page属性的值。
     *
     */
    public void setPage(int value) {
        this.page = value;
    }

    /**
     * 获取remark属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置remark属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRemark(String value) {
        this.remark = value;
    }

    /**
     * 获取rows属性的值。
     *
     */
    public int getRows() {
        return rows;
    }

    /**
     * 设置rows属性的值。
     *
     */
    public void setRows(int value) {
        this.rows = value;
    }

    /**
     * 获取sapFactoryCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSapFactoryCode() {
        return sapFactoryCode;
    }

    /**
     * 设置sapFactoryCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSapFactoryCode(String value) {
        this.sapFactoryCode = value;
    }

    /**
     * 获取schema属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSchema() {
        return schema;
    }

    /**
     * 设置schema属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSchema(String value) {
        this.schema = value;
    }

    /**
     * 获取sign属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSign() {
        return sign;
    }

    /**
     * 设置sign属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSign(String value) {
        this.sign = value;
    }

    /**
     * 获取user属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置user属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * 获取userDefined1属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined1() {
        return userDefined1;
    }

    /**
     * 设置userDefined1属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined1(String value) {
        this.userDefined1 = value;
    }

    /**
     * 获取userDefined10属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined10() {
        return userDefined10;
    }

    /**
     * 设置userDefined10属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined10(String value) {
        this.userDefined10 = value;
    }

    /**
     * 获取userDefined2属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined2() {
        return userDefined2;
    }

    /**
     * 设置userDefined2属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined2(String value) {
        this.userDefined2 = value;
    }

    /**
     * 获取userDefined3属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined3() {
        return userDefined3;
    }

    /**
     * 设置userDefined3属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined3(String value) {
        this.userDefined3 = value;
    }

    /**
     * 获取userDefined4属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined4() {
        return userDefined4;
    }

    /**
     * 设置userDefined4属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined4(String value) {
        this.userDefined4 = value;
    }

    /**
     * 获取userDefined5属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined5() {
        return userDefined5;
    }

    /**
     * 设置userDefined5属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined5(String value) {
        this.userDefined5 = value;
    }

    /**
     * 获取userDefined6属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined6() {
        return userDefined6;
    }

    /**
     * 设置userDefined6属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined6(String value) {
        this.userDefined6 = value;
    }

    /**
     * 获取userDefined7属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined7() {
        return userDefined7;
    }

    /**
     * 设置userDefined7属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined7(String value) {
        this.userDefined7 = value;
    }

    /**
     * 获取userDefined8属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined8() {
        return userDefined8;
    }

    /**
     * 设置userDefined8属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined8(String value) {
        this.userDefined8 = value;
    }

    /**
     * 获取userDefined9属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserDefined9() {
        return userDefined9;
    }

    /**
     * 设置userDefined9属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserDefined9(String value) {
        this.userDefined9 = value;
    }

}
