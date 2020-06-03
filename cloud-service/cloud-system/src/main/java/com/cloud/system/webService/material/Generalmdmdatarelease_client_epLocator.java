/**
 * Generalmdmdatarelease_client_epLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cloud.system.webService.material;

import com.cloud.system.config.MdmConnConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Generalmdmdatarelease_client_epLocator extends org.apache.axis.client.Service implements com.cloud.system.webService.material.Generalmdmdatarelease_client_ep {
    @Autowired
    private  MdmConnConfig mdmConnConfig;


    public Generalmdmdatarelease_client_epLocator() {
    }

    public Generalmdmdatarelease_client_epLocator(MdmConnConfig mdmConnConfig) {
        this.mdmConnConfig = mdmConnConfig;
        this.GeneralMDMDataRelease_pt_address = mdmConnConfig.getServiceUrl();
    }


    public Generalmdmdatarelease_client_epLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Generalmdmdatarelease_client_epLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for GeneralMDMDataRelease_pt
    private java.lang.String GeneralMDMDataRelease_pt_address = "";

    public java.lang.String getGeneralMDMDataRelease_ptAddress() {
        return GeneralMDMDataRelease_pt_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String GeneralMDMDataRelease_ptWSDDServiceName = "GeneralMDMDataRelease_pt";

    public java.lang.String getGeneralMDMDataRelease_ptWSDDServiceName() {
        return GeneralMDMDataRelease_ptWSDDServiceName;
    }

    public void setGeneralMDMDataRelease_ptWSDDServiceName(java.lang.String name) {
        GeneralMDMDataRelease_ptWSDDServiceName = name;
    }

    public com.cloud.system.webService.material.GeneralMDMDataRelease getGeneralMDMDataRelease_pt() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(GeneralMDMDataRelease_pt_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getGeneralMDMDataRelease_pt(endpoint);
    }

    public com.cloud.system.webService.material.GeneralMDMDataRelease getGeneralMDMDataRelease_pt(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.cloud.system.webService.material.GeneralMDMDataReleaseBindingStub _stub = new com.cloud.system.webService.material.GeneralMDMDataReleaseBindingStub(portAddress, this);
            _stub.setPortName(getGeneralMDMDataRelease_ptWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setGeneralMDMDataRelease_ptEndpointAddress(java.lang.String address) {
        GeneralMDMDataRelease_pt_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.cloud.system.webService.material.GeneralMDMDataRelease.class.isAssignableFrom(serviceEndpointInterface)) {
                com.cloud.system.webService.material.GeneralMDMDataReleaseBindingStub _stub = new com.cloud.system.webService.material.GeneralMDMDataReleaseBindingStub(new java.net.URL(GeneralMDMDataRelease_pt_address), this);
                _stub.setPortName(getGeneralMDMDataRelease_ptWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
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
        java.lang.String inputPortName = portName.getLocalPart();
        if ("GeneralMDMDataRelease_pt".equals(inputPortName)) {
            return getGeneralMDMDataRelease_pt();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://xmlns.oracle.com/Interface/GeneralMDMDataRelease/GeneralMDMDataRelease", "generalmdmdatarelease_client_ep");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://xmlns.oracle.com/Interface/GeneralMDMDataRelease/GeneralMDMDataRelease", "GeneralMDMDataRelease_pt"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("GeneralMDMDataRelease_pt".equals(portName)) {
            setGeneralMDMDataRelease_ptEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
