
package com.cloud.order.webService.wms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>odsRawOrderOutStorageDTO complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="odsRawOrderOutStorageDTO">
 *   &lt;complexContent>
 *     &lt;extension base="{http://controller.rf.web.hand.ws.cosmo.haier.com/}wmsEntity">
 *       &lt;sequence>
 *         &lt;element name="abcClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountingMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountingStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="auxFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="batchNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="charg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="checkOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="correspondingOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dcQty" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="diffFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fillDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fillDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fillDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="fillName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fillType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstHvDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstHvDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="firstHvDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="hangBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hangFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hangReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hangTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="hvName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hzHvAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="hzHvType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kdHvType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="kdHvaaAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="ktAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="lastHvDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastHvDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastHvDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastMoDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastMoDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastMoDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="locLineCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lockFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="materialCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="materialDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="moName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrpCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrpCtrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mrps" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="operatorDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="operatorDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="operatorDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="operatorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderAmout" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="orderLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOutId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderOutType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderRreason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pickMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prdLine" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prdMaterialCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prdMaterialDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prdOrder" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prdOrderNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prdOrderNoList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="prdOrderType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proInAmount" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="process" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="productLineCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="purchaseGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="purchaseGroupDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="qtQty" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="requireDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="requireDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="requireDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="rgekz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rsnum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rspos" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ryHvType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sapFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sapMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sendSpot" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="seqAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="serialNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shoutNum" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="singleConsume" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="sobkz" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stgQty" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="supplyCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="totalBackAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="totalDisuseAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="totalFillAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="totalHvAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="totalMoAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="totalStoAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="totalVirtualOrderAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="tyHvAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="whCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="wkposCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zeroDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="zeroDateMax" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="zeroDateMin" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="zeroFlag" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zeroName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zeroReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "odsRawOrderOutStorageDTO", propOrder = {
        "abcClass",
        "accountingMessage",
        "accountingStatus",
        "accountingTime",
        "amount",
        "auxFlag",
        "batchNumber",
        "charg",
        "checkOrder",
        "correspondingOrder",
        "dataSource",
        "dcQty",
        "diffFlag",
        "fillDate",
        "fillDateMax",
        "fillDateMin",
        "fillName",
        "fillType",
        "firstHvDate",
        "firstHvDateMax",
        "firstHvDateMin",
        "hangBy",
        "hangFlag",
        "hangReason",
        "hangTime",
        "hvName",
        "hzHvAmount",
        "hzHvType",
        "kdHvType",
        "kdHvaaAmount",
        "ktAmount",
        "lastHvDate",
        "lastHvDateMax",
        "lastHvDateMin",
        "lastMoDate",
        "lastMoDateMax",
        "lastMoDateMin",
        "locLineCode",
        "lockFlag",
        "materialCode",
        "materialDesc",
        "moName",
        "mrpCode",
        "mrpCtrl",
        "mrps",
        "operatorDate",
        "operatorDateMax",
        "operatorDateMin",
        "operatorName",
        "orderAmout",
        "orderLine",
        "orderNo",
        "orderOutId",
        "orderOutType",
        "orderRreason",
        "pickMode",
        "prdLine",
        "prdMaterialCode",
        "prdMaterialDesc",
        "prdOrder",
        "prdOrderNo",
        "prdOrderNoList",
        "prdOrderType",
        "proInAmount",
        "process",
        "productLineCode",
        "purchaseGroup",
        "purchaseGroupDesc",
        "qtQty",
        "requireDate",
        "requireDateMax",
        "requireDateMin",
        "rgekz",
        "rsnum",
        "rspos",
        "ryHvType",
        "sapFlag",
        "sapMessage",
        "sendSpot",
        "seqAmount",
        "serialNo",
        "shoutNum",
        "singleConsume",
        "sobkz",
        "status",
        "stgQty",
        "supplyCode",
        "totalBackAmount",
        "totalDisuseAmount",
        "totalFillAmount",
        "totalHvAmount",
        "totalMoAmount",
        "totalStoAmount",
        "totalVirtualOrderAmount",
        "tyHvAmount",
        "unit",
        "whCode",
        "wkposCode",
        "zeroDate",
        "zeroDateMax",
        "zeroDateMin",
        "zeroFlag",
        "zeroName",
        "zeroReason"
})
public class OdsRawOrderOutStorageDTO
        extends WmsEntity
{

    protected String abcClass;
    protected String accountingMessage;
    protected String accountingStatus;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar accountingTime;
    protected BigDecimal amount;
    protected String auxFlag;
    protected String batchNumber;
    protected String charg;
    protected String checkOrder;
    protected String correspondingOrder;
    protected String dataSource;
    protected BigDecimal dcQty;
    protected String diffFlag;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fillDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fillDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fillDateMin;
    protected String fillName;
    protected String fillType;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstHvDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstHvDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstHvDateMin;
    protected String hangBy;
    protected String hangFlag;
    protected String hangReason;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar hangTime;
    protected String hvName;
    protected BigDecimal hzHvAmount;
    protected String hzHvType;
    protected String kdHvType;
    protected BigDecimal kdHvaaAmount;
    protected BigDecimal ktAmount;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastHvDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastHvDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastHvDateMin;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastMoDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastMoDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastMoDateMin;
    protected String locLineCode;
    protected String lockFlag;
    protected String materialCode;
    protected String materialDesc;
    protected String moName;
    protected String mrpCode;
    protected String mrpCtrl;
    @XmlElement(nillable = true)
    protected List<String> mrps;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar operatorDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar operatorDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar operatorDateMin;
    protected String operatorName;
    protected BigDecimal orderAmout;
    protected String orderLine;
    protected String orderNo;
    protected String orderOutId;
    protected String orderOutType;
    protected String orderRreason;
    protected String pickMode;
    protected String prdLine;
    protected String prdMaterialCode;
    protected String prdMaterialDesc;
    protected String prdOrder;
    protected String prdOrderNo;
    @XmlElement(nillable = true)
    protected List<String> prdOrderNoList;
    protected String prdOrderType;
    protected String proInAmount;
    protected String process;
    protected String productLineCode;
    protected String purchaseGroup;
    protected String purchaseGroupDesc;
    protected BigDecimal qtQty;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requireDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requireDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requireDateMin;
    protected String rgekz;
    protected String rsnum;
    protected String rspos;
    protected String ryHvType;
    protected String sapFlag;
    protected String sapMessage;
    protected String sendSpot;
    protected BigDecimal seqAmount;
    protected String serialNo;
    protected BigDecimal shoutNum;
    protected BigDecimal singleConsume;
    protected String sobkz;
    protected String status;
    protected BigDecimal stgQty;
    protected String supplyCode;
    protected BigDecimal totalBackAmount;
    protected BigDecimal totalDisuseAmount;
    protected BigDecimal totalFillAmount;
    protected BigDecimal totalHvAmount;
    protected BigDecimal totalMoAmount;
    protected BigDecimal totalStoAmount;
    protected BigDecimal totalVirtualOrderAmount;
    protected BigDecimal tyHvAmount;
    protected String unit;
    protected String whCode;
    protected String wkposCode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar zeroDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar zeroDateMax;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar zeroDateMin;
    protected String zeroFlag;
    protected String zeroName;
    protected String zeroReason;

    /**
     * 获取abcClass属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAbcClass() {
        return abcClass;
    }

    /**
     * 设置abcClass属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAbcClass(String value) {
        this.abcClass = value;
    }

    /**
     * 获取accountingMessage属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccountingMessage() {
        return accountingMessage;
    }

    /**
     * 设置accountingMessage属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccountingMessage(String value) {
        this.accountingMessage = value;
    }

    /**
     * 获取accountingStatus属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccountingStatus() {
        return accountingStatus;
    }

    /**
     * 设置accountingStatus属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccountingStatus(String value) {
        this.accountingStatus = value;
    }

    /**
     * 获取accountingTime属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getAccountingTime() {
        return accountingTime;
    }

    /**
     * 设置accountingTime属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setAccountingTime(XMLGregorianCalendar value) {
        this.accountingTime = value;
    }

    /**
     * 获取amount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * 设置amount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * 获取auxFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAuxFlag() {
        return auxFlag;
    }

    /**
     * 设置auxFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAuxFlag(String value) {
        this.auxFlag = value;
    }

    /**
     * 获取batchNumber属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * 设置batchNumber属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBatchNumber(String value) {
        this.batchNumber = value;
    }

    /**
     * 获取charg属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCharg() {
        return charg;
    }

    /**
     * 设置charg属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCharg(String value) {
        this.charg = value;
    }

    /**
     * 获取checkOrder属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCheckOrder() {
        return checkOrder;
    }

    /**
     * 设置checkOrder属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCheckOrder(String value) {
        this.checkOrder = value;
    }

    /**
     * 获取correspondingOrder属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCorrespondingOrder() {
        return correspondingOrder;
    }

    /**
     * 设置correspondingOrder属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCorrespondingOrder(String value) {
        this.correspondingOrder = value;
    }

    /**
     * 获取dataSource属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * 设置dataSource属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataSource(String value) {
        this.dataSource = value;
    }

    /**
     * 获取dcQty属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getDcQty() {
        return dcQty;
    }

    /**
     * 设置dcQty属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setDcQty(BigDecimal value) {
        this.dcQty = value;
    }

    /**
     * 获取diffFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDiffFlag() {
        return diffFlag;
    }

    /**
     * 设置diffFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDiffFlag(String value) {
        this.diffFlag = value;
    }

    /**
     * 获取fillDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getFillDate() {
        return fillDate;
    }

    /**
     * 设置fillDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setFillDate(XMLGregorianCalendar value) {
        this.fillDate = value;
    }

    /**
     * 获取fillDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getFillDateMax() {
        return fillDateMax;
    }

    /**
     * 设置fillDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setFillDateMax(XMLGregorianCalendar value) {
        this.fillDateMax = value;
    }

    /**
     * 获取fillDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getFillDateMin() {
        return fillDateMin;
    }

    /**
     * 设置fillDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setFillDateMin(XMLGregorianCalendar value) {
        this.fillDateMin = value;
    }

    /**
     * 获取fillName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFillName() {
        return fillName;
    }

    /**
     * 设置fillName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFillName(String value) {
        this.fillName = value;
    }

    /**
     * 获取fillType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFillType() {
        return fillType;
    }

    /**
     * 设置fillType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFillType(String value) {
        this.fillType = value;
    }

    /**
     * 获取firstHvDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getFirstHvDate() {
        return firstHvDate;
    }

    /**
     * 设置firstHvDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setFirstHvDate(XMLGregorianCalendar value) {
        this.firstHvDate = value;
    }

    /**
     * 获取firstHvDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getFirstHvDateMax() {
        return firstHvDateMax;
    }

    /**
     * 设置firstHvDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setFirstHvDateMax(XMLGregorianCalendar value) {
        this.firstHvDateMax = value;
    }

    /**
     * 获取firstHvDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getFirstHvDateMin() {
        return firstHvDateMin;
    }

    /**
     * 设置firstHvDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setFirstHvDateMin(XMLGregorianCalendar value) {
        this.firstHvDateMin = value;
    }

    /**
     * 获取hangBy属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHangBy() {
        return hangBy;
    }

    /**
     * 设置hangBy属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHangBy(String value) {
        this.hangBy = value;
    }

    /**
     * 获取hangFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHangFlag() {
        return hangFlag;
    }

    /**
     * 设置hangFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHangFlag(String value) {
        this.hangFlag = value;
    }

    /**
     * 获取hangReason属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHangReason() {
        return hangReason;
    }

    /**
     * 设置hangReason属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHangReason(String value) {
        this.hangReason = value;
    }

    /**
     * 获取hangTime属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getHangTime() {
        return hangTime;
    }

    /**
     * 设置hangTime属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setHangTime(XMLGregorianCalendar value) {
        this.hangTime = value;
    }

    /**
     * 获取hvName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHvName() {
        return hvName;
    }

    /**
     * 设置hvName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHvName(String value) {
        this.hvName = value;
    }

    /**
     * 获取hzHvAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getHzHvAmount() {
        return hzHvAmount;
    }

    /**
     * 设置hzHvAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setHzHvAmount(BigDecimal value) {
        this.hzHvAmount = value;
    }

    /**
     * 获取hzHvType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHzHvType() {
        return hzHvType;
    }

    /**
     * 设置hzHvType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHzHvType(String value) {
        this.hzHvType = value;
    }

    /**
     * 获取kdHvType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKdHvType() {
        return kdHvType;
    }

    /**
     * 设置kdHvType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKdHvType(String value) {
        this.kdHvType = value;
    }

    /**
     * 获取kdHvaaAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getKdHvaaAmount() {
        return kdHvaaAmount;
    }

    /**
     * 设置kdHvaaAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setKdHvaaAmount(BigDecimal value) {
        this.kdHvaaAmount = value;
    }

    /**
     * 获取ktAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getKtAmount() {
        return ktAmount;
    }

    /**
     * 设置ktAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setKtAmount(BigDecimal value) {
        this.ktAmount = value;
    }

    /**
     * 获取lastHvDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastHvDate() {
        return lastHvDate;
    }

    /**
     * 设置lastHvDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastHvDate(XMLGregorianCalendar value) {
        this.lastHvDate = value;
    }

    /**
     * 获取lastHvDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastHvDateMax() {
        return lastHvDateMax;
    }

    /**
     * 设置lastHvDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastHvDateMax(XMLGregorianCalendar value) {
        this.lastHvDateMax = value;
    }

    /**
     * 获取lastHvDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastHvDateMin() {
        return lastHvDateMin;
    }

    /**
     * 设置lastHvDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastHvDateMin(XMLGregorianCalendar value) {
        this.lastHvDateMin = value;
    }

    /**
     * 获取lastMoDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastMoDate() {
        return lastMoDate;
    }

    /**
     * 设置lastMoDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastMoDate(XMLGregorianCalendar value) {
        this.lastMoDate = value;
    }

    /**
     * 获取lastMoDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastMoDateMax() {
        return lastMoDateMax;
    }

    /**
     * 设置lastMoDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastMoDateMax(XMLGregorianCalendar value) {
        this.lastMoDateMax = value;
    }

    /**
     * 获取lastMoDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getLastMoDateMin() {
        return lastMoDateMin;
    }

    /**
     * 设置lastMoDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setLastMoDateMin(XMLGregorianCalendar value) {
        this.lastMoDateMin = value;
    }

    /**
     * 获取locLineCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLocLineCode() {
        return locLineCode;
    }

    /**
     * 设置locLineCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLocLineCode(String value) {
        this.locLineCode = value;
    }

    /**
     * 获取lockFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLockFlag() {
        return lockFlag;
    }

    /**
     * 设置lockFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLockFlag(String value) {
        this.lockFlag = value;
    }

    /**
     * 获取materialCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaterialCode() {
        return materialCode;
    }

    /**
     * 设置materialCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaterialCode(String value) {
        this.materialCode = value;
    }

    /**
     * 获取materialDesc属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMaterialDesc() {
        return materialDesc;
    }

    /**
     * 设置materialDesc属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMaterialDesc(String value) {
        this.materialDesc = value;
    }

    /**
     * 获取moName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMoName() {
        return moName;
    }

    /**
     * 设置moName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMoName(String value) {
        this.moName = value;
    }

    /**
     * 获取mrpCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMrpCode() {
        return mrpCode;
    }

    /**
     * 设置mrpCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMrpCode(String value) {
        this.mrpCode = value;
    }

    /**
     * 获取mrpCtrl属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMrpCtrl() {
        return mrpCtrl;
    }

    /**
     * 设置mrpCtrl属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMrpCtrl(String value) {
        this.mrpCtrl = value;
    }

    /**
     * Gets the value of the mrps property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mrps property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMrps().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getMrps() {
        if (mrps == null) {
            mrps = new ArrayList<String>();
        }
        return this.mrps;
    }

    /**
     * 获取operatorDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getOperatorDate() {
        return operatorDate;
    }

    /**
     * 设置operatorDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setOperatorDate(XMLGregorianCalendar value) {
        this.operatorDate = value;
    }

    /**
     * 获取operatorDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getOperatorDateMax() {
        return operatorDateMax;
    }

    /**
     * 设置operatorDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setOperatorDateMax(XMLGregorianCalendar value) {
        this.operatorDateMax = value;
    }

    /**
     * 获取operatorDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getOperatorDateMin() {
        return operatorDateMin;
    }

    /**
     * 设置operatorDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setOperatorDateMin(XMLGregorianCalendar value) {
        this.operatorDateMin = value;
    }

    /**
     * 获取operatorName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * 设置operatorName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOperatorName(String value) {
        this.operatorName = value;
    }

    /**
     * 获取orderAmout属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getOrderAmout() {
        return orderAmout;
    }

    /**
     * 设置orderAmout属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setOrderAmout(BigDecimal value) {
        this.orderAmout = value;
    }

    /**
     * 获取orderLine属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderLine() {
        return orderLine;
    }

    /**
     * 设置orderLine属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderLine(String value) {
        this.orderLine = value;
    }

    /**
     * 获取orderNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置orderNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderNo(String value) {
        this.orderNo = value;
    }

    /**
     * 获取orderOutId属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderOutId() {
        return orderOutId;
    }

    /**
     * 设置orderOutId属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderOutId(String value) {
        this.orderOutId = value;
    }

    /**
     * 获取orderOutType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderOutType() {
        return orderOutType;
    }

    /**
     * 设置orderOutType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderOutType(String value) {
        this.orderOutType = value;
    }

    /**
     * 获取orderRreason属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOrderRreason() {
        return orderRreason;
    }

    /**
     * 设置orderRreason属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOrderRreason(String value) {
        this.orderRreason = value;
    }

    /**
     * 获取pickMode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPickMode() {
        return pickMode;
    }

    /**
     * 设置pickMode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPickMode(String value) {
        this.pickMode = value;
    }

    /**
     * 获取prdLine属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrdLine() {
        return prdLine;
    }

    /**
     * 设置prdLine属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrdLine(String value) {
        this.prdLine = value;
    }

    /**
     * 获取prdMaterialCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrdMaterialCode() {
        return prdMaterialCode;
    }

    /**
     * 设置prdMaterialCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrdMaterialCode(String value) {
        this.prdMaterialCode = value;
    }

    /**
     * 获取prdMaterialDesc属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrdMaterialDesc() {
        return prdMaterialDesc;
    }

    /**
     * 设置prdMaterialDesc属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrdMaterialDesc(String value) {
        this.prdMaterialDesc = value;
    }

    /**
     * 获取prdOrder属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrdOrder() {
        return prdOrder;
    }

    /**
     * 设置prdOrder属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrdOrder(String value) {
        this.prdOrder = value;
    }

    /**
     * 获取prdOrderNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrdOrderNo() {
        return prdOrderNo;
    }

    /**
     * 设置prdOrderNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrdOrderNo(String value) {
        this.prdOrderNo = value;
    }

    /**
     * Gets the value of the prdOrderNoList property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prdOrderNoList property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrdOrderNoList().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPrdOrderNoList() {
        if (prdOrderNoList == null) {
            prdOrderNoList = new ArrayList<String>();
        }
        return this.prdOrderNoList;
    }

    public void setPrdOrderNoList(List<String> prdOrderNoList) {
        this.prdOrderNoList = prdOrderNoList;
    }

    /**
     * 获取prdOrderType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPrdOrderType() {
        return prdOrderType;
    }

    /**
     * 设置prdOrderType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPrdOrderType(String value) {
        this.prdOrderType = value;
    }

    /**
     * 获取proInAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProInAmount() {
        return proInAmount;
    }

    /**
     * 设置proInAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProInAmount(String value) {
        this.proInAmount = value;
    }

    /**
     * 获取process属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcess() {
        return process;
    }

    /**
     * 设置process属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcess(String value) {
        this.process = value;
    }

    /**
     * 获取productLineCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductLineCode() {
        return productLineCode;
    }

    /**
     * 设置productLineCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductLineCode(String value) {
        this.productLineCode = value;
    }

    /**
     * 获取purchaseGroup属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPurchaseGroup() {
        return purchaseGroup;
    }

    /**
     * 设置purchaseGroup属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPurchaseGroup(String value) {
        this.purchaseGroup = value;
    }

    /**
     * 获取purchaseGroupDesc属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPurchaseGroupDesc() {
        return purchaseGroupDesc;
    }

    /**
     * 设置purchaseGroupDesc属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPurchaseGroupDesc(String value) {
        this.purchaseGroupDesc = value;
    }

    /**
     * 获取qtQty属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getQtQty() {
        return qtQty;
    }

    /**
     * 设置qtQty属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setQtQty(BigDecimal value) {
        this.qtQty = value;
    }

    /**
     * 获取requireDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getRequireDate() {
        return requireDate;
    }

    /**
     * 设置requireDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setRequireDate(XMLGregorianCalendar value) {
        this.requireDate = value;
    }

    /**
     * 获取requireDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getRequireDateMax() {
        return requireDateMax;
    }

    /**
     * 设置requireDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setRequireDateMax(XMLGregorianCalendar value) {
        this.requireDateMax = value;
    }

    /**
     * 获取requireDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getRequireDateMin() {
        return requireDateMin;
    }

    /**
     * 设置requireDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setRequireDateMin(XMLGregorianCalendar value) {
        this.requireDateMin = value;
    }

    /**
     * 获取rgekz属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRgekz() {
        return rgekz;
    }

    /**
     * 设置rgekz属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRgekz(String value) {
        this.rgekz = value;
    }

    /**
     * 获取rsnum属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRsnum() {
        return rsnum;
    }

    /**
     * 设置rsnum属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRsnum(String value) {
        this.rsnum = value;
    }

    /**
     * 获取rspos属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRspos() {
        return rspos;
    }

    /**
     * 设置rspos属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRspos(String value) {
        this.rspos = value;
    }

    /**
     * 获取ryHvType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRyHvType() {
        return ryHvType;
    }

    /**
     * 设置ryHvType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRyHvType(String value) {
        this.ryHvType = value;
    }

    /**
     * 获取sapFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSapFlag() {
        return sapFlag;
    }

    /**
     * 设置sapFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSapFlag(String value) {
        this.sapFlag = value;
    }

    /**
     * 获取sapMessage属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSapMessage() {
        return sapMessage;
    }

    /**
     * 设置sapMessage属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSapMessage(String value) {
        this.sapMessage = value;
    }

    /**
     * 获取sendSpot属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSendSpot() {
        return sendSpot;
    }

    /**
     * 设置sendSpot属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSendSpot(String value) {
        this.sendSpot = value;
    }

    /**
     * 获取seqAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getSeqAmount() {
        return seqAmount;
    }

    /**
     * 设置seqAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setSeqAmount(BigDecimal value) {
        this.seqAmount = value;
    }

    /**
     * 获取serialNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * 设置serialNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSerialNo(String value) {
        this.serialNo = value;
    }

    /**
     * 获取shoutNum属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getShoutNum() {
        return shoutNum;
    }

    /**
     * 设置shoutNum属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setShoutNum(BigDecimal value) {
        this.shoutNum = value;
    }

    /**
     * 获取singleConsume属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getSingleConsume() {
        return singleConsume;
    }

    /**
     * 设置singleConsume属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setSingleConsume(BigDecimal value) {
        this.singleConsume = value;
    }

    /**
     * 获取sobkz属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSobkz() {
        return sobkz;
    }

    /**
     * 设置sobkz属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSobkz(String value) {
        this.sobkz = value;
    }

    /**
     * 获取status属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置status属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * 获取stgQty属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getStgQty() {
        return stgQty;
    }

    /**
     * 设置stgQty属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setStgQty(BigDecimal value) {
        this.stgQty = value;
    }

    /**
     * 获取supplyCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSupplyCode() {
        return supplyCode;
    }

    /**
     * 设置supplyCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSupplyCode(String value) {
        this.supplyCode = value;
    }

    /**
     * 获取totalBackAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalBackAmount() {
        return totalBackAmount;
    }

    /**
     * 设置totalBackAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalBackAmount(BigDecimal value) {
        this.totalBackAmount = value;
    }

    /**
     * 获取totalDisuseAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalDisuseAmount() {
        return totalDisuseAmount;
    }

    /**
     * 设置totalDisuseAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalDisuseAmount(BigDecimal value) {
        this.totalDisuseAmount = value;
    }

    /**
     * 获取totalFillAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalFillAmount() {
        return totalFillAmount;
    }

    /**
     * 设置totalFillAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalFillAmount(BigDecimal value) {
        this.totalFillAmount = value;
    }

    /**
     * 获取totalHvAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalHvAmount() {
        return totalHvAmount;
    }

    /**
     * 设置totalHvAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalHvAmount(BigDecimal value) {
        this.totalHvAmount = value;
    }

    /**
     * 获取totalMoAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalMoAmount() {
        return totalMoAmount;
    }

    /**
     * 设置totalMoAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalMoAmount(BigDecimal value) {
        this.totalMoAmount = value;
    }

    /**
     * 获取totalStoAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalStoAmount() {
        return totalStoAmount;
    }

    /**
     * 设置totalStoAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalStoAmount(BigDecimal value) {
        this.totalStoAmount = value;
    }

    /**
     * 获取totalVirtualOrderAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTotalVirtualOrderAmount() {
        return totalVirtualOrderAmount;
    }

    /**
     * 设置totalVirtualOrderAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTotalVirtualOrderAmount(BigDecimal value) {
        this.totalVirtualOrderAmount = value;
    }

    /**
     * 获取tyHvAmount属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getTyHvAmount() {
        return tyHvAmount;
    }

    /**
     * 设置tyHvAmount属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setTyHvAmount(BigDecimal value) {
        this.tyHvAmount = value;
    }

    /**
     * 获取unit属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置unit属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * 获取whCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWhCode() {
        return whCode;
    }

    /**
     * 设置whCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWhCode(String value) {
        this.whCode = value;
    }

    /**
     * 获取wkposCode属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWkposCode() {
        return wkposCode;
    }

    /**
     * 设置wkposCode属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWkposCode(String value) {
        this.wkposCode = value;
    }

    /**
     * 获取zeroDate属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getZeroDate() {
        return zeroDate;
    }

    /**
     * 设置zeroDate属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setZeroDate(XMLGregorianCalendar value) {
        this.zeroDate = value;
    }

    /**
     * 获取zeroDateMax属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getZeroDateMax() {
        return zeroDateMax;
    }

    /**
     * 设置zeroDateMax属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setZeroDateMax(XMLGregorianCalendar value) {
        this.zeroDateMax = value;
    }

    /**
     * 获取zeroDateMin属性的值。
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getZeroDateMin() {
        return zeroDateMin;
    }

    /**
     * 设置zeroDateMin属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setZeroDateMin(XMLGregorianCalendar value) {
        this.zeroDateMin = value;
    }

    /**
     * 获取zeroFlag属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getZeroFlag() {
        return zeroFlag;
    }

    /**
     * 设置zeroFlag属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setZeroFlag(String value) {
        this.zeroFlag = value;
    }

    /**
     * 获取zeroName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getZeroName() {
        return zeroName;
    }

    /**
     * 设置zeroName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setZeroName(String value) {
        this.zeroName = value;
    }

    /**
     * 获取zeroReason属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getZeroReason() {
        return zeroReason;
    }

    /**
     * 设置zeroReason属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setZeroReason(String value) {
        this.zeroReason = value;
    }

}
