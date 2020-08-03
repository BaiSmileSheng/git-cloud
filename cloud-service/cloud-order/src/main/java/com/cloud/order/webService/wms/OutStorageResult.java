
package com.cloud.order.webService.wms;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>outStorageResult complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="outStorageResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="data" type="{http://controller.rf.web.hand.ws.cosmo.haier.com/}odsRawOrderOutStorageDTO" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="msg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "outStorageResult", propOrder = {
        "data",
        "msg",
        "status"
})
public class OutStorageResult {

    @XmlElement(nillable = true)
    protected List<OdsRawOrderOutStorageDTO> data;
    protected String msg;
    protected String status;

    /**
     * Gets the value of the data property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the data property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getData().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OdsRawOrderOutStorageDTO }
     *
     *
     */
    public List<OdsRawOrderOutStorageDTO> getData() {
        if (data == null) {
            data = new ArrayList<OdsRawOrderOutStorageDTO>();
        }
        return this.data;
    }

    /**
     * 获取msg属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置msg属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMsg(String value) {
        this.msg = value;
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

}
