/**
 * GeneralMDMDataRelease.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.cloud.system.webService.material;

public interface GeneralMDMDataRelease extends java.rmi.Remote {
    public void process(java.lang.String IN_SYS_NAME, java.lang.String IN_MASTER_TYPE, java.lang.String IN_TABLE_NAME, java.lang.String IN_STARTDATE, java.lang.String IN_ENDDATE, java.lang.String IN_PAGE, java.lang.String IN_BATCH_ID, javax.xml.rpc.holders.StringHolder OUT_PAGE, javax.xml.rpc.holders.StringHolder OUT_RESULT, javax.xml.rpc.holders.StringHolder OUT_RETCODE, javax.xml.rpc.holders.StringHolder OUT_ALL_NUM, javax.xml.rpc.holders.StringHolder OUT_PAGE_CON, javax.xml.rpc.holders.StringHolder OUT_ALL_COUNT, javax.xml.rpc.holders.StringHolder OUT_RETMSG, javax.xml.rpc.holders.StringHolder OUT_BATCH_ID) throws java.rmi.RemoteException;
}
