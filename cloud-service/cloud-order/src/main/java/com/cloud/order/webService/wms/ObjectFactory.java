
package com.cloud.order.webService.wms;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.haier.cosmo.ws.hand.web.rf.controller package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FindAllCodeForJIT_QNAME = new QName("http://controller.rf.web.hand.ws.cosmo.haier.com/", "findAllCodeForJIT");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.haier.cosmo.ws.hand.web.rf.controller
     * 
     */
    public ObjectFactory() {
    }


    /**
     * Create an instance of {@link OutStorageResult }
     * 
     */
    public OutStorageResult createOutStorageResult() {
        return new OutStorageResult();
    }

    /**
     * Create an instance of {@link OdsRawOrderOutStorageDTO }
     * 
     */
    public OdsRawOrderOutStorageDTO createOdsRawOrderOutStorageDTO() {
        return new OdsRawOrderOutStorageDTO();
    }

}
