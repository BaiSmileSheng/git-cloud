
package com.cloud.order.webService.wms;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "RfWebService", targetNamespace = "http://controller.rf.web.hand.ws.cosmo.haier.com/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface RfWebService {

    /**
     * 
     * @param param
     * @return
     *     returns com.haier.cosmo.ws.hand.web.rf.controller.OutStorageResult
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "findAllCodeForJIT", targetNamespace = "http://controller.rf.web.hand.ws.cosmo.haier.com/", className = "com.haier.cosmo.ws.hand.web.rf.controller.FindAllCodeForJIT")
    @ResponseWrapper(localName = "findAllCodeForJITResponse", targetNamespace = "http://controller.rf.web.hand.ws.cosmo.haier.com/", className = "com.haier.cosmo.ws.hand.web.rf.controller.FindAllCodeForJITResponse")
    public OutStorageResult findAllCodeForJIT(
            @WebParam(name = "param", targetNamespace = "")
                    OdsRawOrderOutStorageDTO param);

}