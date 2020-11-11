/**
 * ErpPayoutReceiveServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cloud.settle.webService.fm;

public class ErpPayoutReceiveServiceServiceLocator extends org.apache.axis.client.Service implements ErpPayoutReceiveServiceService {

    public ErpPayoutReceiveServiceServiceLocator() {
    }


    public ErpPayoutReceiveServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ErpPayoutReceiveServiceServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for QryPays
    private String QryPays_address = "http://10.202.88.22:8080/fm/services/QryPays";

    public String getQryPaysAddress() {
        return QryPays_address;
    }

    // The WSDD service name defaults to the port name.
    private String QryPaysWSDDServiceName = "QryPays";

    public String getQryPaysWSDDServiceName() {
        return QryPaysWSDDServiceName;
    }

    public void setQryPaysWSDDServiceName(String name) {
        QryPaysWSDDServiceName = name;
    }

    public ErpPayoutReceiveService getQryPays() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(QryPays_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQryPays(endpoint);
    }

    public ErpPayoutReceiveService getQryPays(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            QryPaysSoapBindingStub _stub = new QryPaysSoapBindingStub(portAddress, this);
            _stub.setPortName(getQryPaysWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQryPaysEndpointAddress(String address) {
        QryPays_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ErpPayoutReceiveService.class.isAssignableFrom(serviceEndpointInterface)) {
                QryPaysSoapBindingStub _stub = new QryPaysSoapBindingStub(new java.net.URL(QryPays_address), this);
                _stub.setPortName(getQryPaysWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("QryPays".equals(inputPortName)) {
            return getQryPays();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://10.133.28.51:8080/fm/services/QryPays", "ErpPayoutReceiveServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://10.133.28.51:8080/fm/services/QryPays", "QryPays"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("QryPays".equals(portName)) {
            setQryPaysEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
