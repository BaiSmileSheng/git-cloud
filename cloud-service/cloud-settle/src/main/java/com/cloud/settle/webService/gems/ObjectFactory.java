
package com.cloud.settle.webService.gems;

import com.cloud.settle.domain.webServicePO.BaseClaimDetail;
import com.cloud.settle.domain.webServicePO.BaseClaimResponse;
import com.cloud.settle.domain.webServicePO.BaseMultiItemClaimSaveRequest;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.haier.gems.ifbaseclaim_srv package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {


    private final static QName _BaseClaimResponse_QNAME = new QName("http://gems.haier.com/IfBaseClaim_Srv", "BaseClaimResponse");
    private final static QName _BaseMultiItemClaimSaveRequest_QNAME = new QName("http://gems.haier.com/IfBaseClaim_Srv", "BaseMultiItemClaimSaveRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.haier.gems.ifbaseclaim_srv
     */
    public ObjectFactory() {
    }


    /**
     * Create an instance of {@link BaseClaimResponse }
     */
    public BaseClaimResponse createBaseClaimResponse() {
        return new BaseClaimResponse();
    }


    /**
     * Create an instance of {@link BaseClaimDetail }
     */
    public BaseClaimDetail createBaseClaimDetail() {
        return new BaseClaimDetail();
    }

    /**
     * Create an instance of {@link BaseMultiItemClaimSaveRequest }
     *
     */
    public BaseMultiItemClaimSaveRequest createBaseMultiItemClaimSaveRequest() {
        return new BaseMultiItemClaimSaveRequest();
    }


    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseClaimResponse }{@code >}}
     */
    @XmlElementDecl(namespace = "http://gems.haier.com/IfBaseClaim_Srv", name = "BaseClaimResponse")
    public JAXBElement<BaseClaimResponse> createBaseClaimResponse(BaseClaimResponse value) {
        return new JAXBElement<BaseClaimResponse>(_BaseClaimResponse_QNAME, BaseClaimResponse.class, null, value);
    }



    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseMultiItemClaimSaveRequest }{@code >}}
     */
    @XmlElementDecl(namespace = "http://gems.haier.com/IfBaseClaim_Srv", name = "BaseMultiItemClaimSaveRequest")
    public JAXBElement<BaseMultiItemClaimSaveRequest> createBaseMultiItemClaimSaveRequest(BaseMultiItemClaimSaveRequest value) {
        return new JAXBElement<BaseMultiItemClaimSaveRequest>(_BaseMultiItemClaimSaveRequest_QNAME, BaseMultiItemClaimSaveRequest.class, null, value);
    }

}
