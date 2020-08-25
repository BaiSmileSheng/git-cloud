package com.cloud.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.service.IOrderFromSap800InterfaceService;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: order服务 - SAP800系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/5
 */
@Slf4j
@Service
public class OrderFromSap800InterfaceServiceImpl implements IOrderFromSap800InterfaceService {
    /**
     * Description: 获取SAP800系统13周PR需求
     * Param: [startDate, endDate]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/5
     */
    @Override
    public R queryDemandPRFromSap800(Date startDate, Date endDate) {
        log.info("================获取SAP800系统13周PR需求方法  start================");
        JCoDestination destination = null;
        if (startDate == null || endDate == null) {
            log.error("================获取SAP800系统13周PR需求方法,传入参数为空================");
            return R.error("获取SAP800系统13周PR需求方法,传入参数为空！");
        }
        //定义返回的data体
        List<OmsInternalOrderRes> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP800);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZMM_PR_KAS);
            if (fm == null) {
                log.error("================获取SAP800系统13周PR需求接口函数为空================");
                return R.error("获取SAP800系统13周PR需求接口函数为空!");
            }
            JCoParameterList jCoFields = fm.getImportParameterList();
            String startDateStr = DateUtil.format(startDate, "yyyy-MM-dd");
            String endDateStr = DateUtil.format(endDate, "yyyy-MM-dd");

            jCoFields.setValue("BEGIN_DATE", startDateStr);
            jCoFields.setValue("END_DATE", endDateStr);
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList outParam = fm.getExportParameterList();
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(outParam.getString("FLAG"))) {
                //从输出table中获取每一行数据
                if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                    log.info(StrUtil.format("===================获取SAP PR数据{}条========================",outTableOutput.getNumRows()));
                    //循环取table行数据
                    for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                        //设置指针位置
                        outTableOutput.setRow(i);
                        OmsInternalOrderRes omsInternalOrderRes = new OmsInternalOrderRes();
                        omsInternalOrderRes.setOrderCode(outTableOutput.getString("BANFN"));//采购申请号
                        omsInternalOrderRes.setOrderLineCode(outTableOutput.getString("BNFPO"));//采购申请行号
                        omsInternalOrderRes.setProductMaterialCode(outTableOutput.getString("MATNR"));//成品物料号
                        omsInternalOrderRes.setProductMaterialDesc(outTableOutput.getString("TXZ01"));//成品物料号
                        omsInternalOrderRes.setCustomerCode(outTableOutput.getString("WERKS"));//客户编码
                        omsInternalOrderRes.setCustomerDesc(outTableOutput.getString("NAME1"));//客户描述
                        omsInternalOrderRes.setPurchaseGroupCode(outTableOutput.getString("EKGRP"));//采购组
                        omsInternalOrderRes.setPurchaseGroupDesc(outTableOutput.getString("EKNAM"));//采购组描述
                        omsInternalOrderRes.setSupplierCode(outTableOutput.getString("FLIEF"));//供应商编码
                        omsInternalOrderRes.setSupplierDesc(outTableOutput.getString("NAME_LF"));//供应商描述
                        omsInternalOrderRes.setDeliveryDate(outTableOutput.getString("LFDAT"));//交货日期
                        omsInternalOrderRes.setOrderNum(outTableOutput.getBigDecimal("MENGE"));//订单量
                        omsInternalOrderRes.setUnit(outTableOutput.getString("MEINS"));//单位
                        omsInternalOrderRes.setCreateDatePr(outTableOutput.getString("FRGDT"));//批准日期
                        omsInternalOrderRes.setProjectType(outTableOutput.getString("PSTYP"));//项目类别
                        omsInternalOrderRes.setMrpRange(outTableOutput.getString("BERID"));//MRP范围
                        omsInternalOrderRes.setMarker("PR");
                        omsInternalOrderRes.setDelFlag("0");
                        omsInternalOrderRes.setCreateBy("定时任务");
                        omsInternalOrderRes.setCreateTime(new Date());
                        dataList.add(omsInternalOrderRes);
                    }
                } else {
                    log.error("获取SAP800系统13周PR需求返回信息为空！");
                    return R.error("获取SAP800系统13周PR需求返回信息为空！");
                }
            } else {
                log.error("获取SAP800系统13周PR需求数据失败：" + outParam.getString("MESSAGE"));
                return R.error(outParam.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("获取SAP800系统13周PR需求方法异常:" + e);
            throw new BusinessException("获取SAP800系统13周PR需求方法异常:" + e);
        }
        log.info("===============获取SAP800系统13周PR需求方法  end================");
        return R.data(dataList);
    }

    /**
     * Description: 获取SAP800系统PO真单
     * Param: [startDate, endDate]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/5
     */
    @Override
    public R queryDemandPOFromSap800(Date startDate, Date endDate) {
        log.info("================获取SAP800系统PO真单方法  start================");
        JCoDestination destination;
        if (startDate == null || endDate == null) {
            log.error("================获取SAP800系统PO真单方法,传入参数为空================");
            return R.error("获取SAP800系统PO真单方法,传入参数为空！");
        }
        //定义返回的data体
        List<OmsInternalOrderRes> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP800);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZMM_PO_KAS);
            if (fm == null) {
                log.error("================获取SAP800系统PO真单接口函数为空================");
                return R.error("获取SAP800系统PO真单接口函数为空!");
            }
            JCoParameterList jCoFields = fm.getImportParameterList();
            jCoFields.setValue("BEGIN_DATE", startDate);
            jCoFields.setValue("END_DATE", endDate);
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList outParam = fm.getExportParameterList();
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(outParam.getString("FLAG"))) {
                //从输出table中获取每一行数据
                if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                    //循环取table行数据
                    for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                        //设置指针位置
                        outTableOutput.setRow(i);
                        OmsInternalOrderRes omsInternalOrderRes = OmsInternalOrderRes.builder()
                                .sapDelFlag(outTableOutput.getString("LOEKZ"))//采购凭证中的删除标识
                                .orderCode(outTableOutput.getString("EBELN"))//采购申请号
                                .orderLineCode(outTableOutput.getString("EBELP"))//采购申请行号
                                .deliveryFlag(outTableOutput.getString("ELIKZ"))//交货已完成标识
                                .productMaterialCode(outTableOutput.getString("MATNR"))//成品物料号
                                .productMaterialDesc(outTableOutput.getString("TXZ01"))//成品物料描述
                                .customerCode(outTableOutput.getString("WERKS"))//客户编码
                                .customerDesc(outTableOutput.getString("NAME1"))//客户描述
                                .purchaseGroupCode(outTableOutput.getString("EKGRP"))//采购组
                                .purchaseGroupDesc(outTableOutput.getString("EKNAM"))//采购组描述
                                .supplierCode(outTableOutput.getString("LIFNR"))//供应商编码
                                .supplierDesc(outTableOutput.getString("NAME_LF"))//供应商描述
                                .deliveryDate(outTableOutput.getString("EINDT"))//交货日期
                                .orderNum(outTableOutput.getBigDecimal("MENGE"))//订单量
                                .unit(outTableOutput.getString("MEINS"))//单位
                                .deliveryNum(outTableOutput.getBigDecimal("WEMNG"))//已交货数量
                                .createDatePr(outTableOutput.getString("BEDAT"))//批准日期
                                .projectType(outTableOutput.getString("PSTYP"))//项目类别
                                .mrpRange(outTableOutput.getString("BERID"))//MRP范围
                                .poType(outTableOutput.getString("BSART"))//PO类型
                                .marker("PO")
                                .delFlag("0")
                                .build();
                        omsInternalOrderRes.setCreateTime(new Date());
                        omsInternalOrderRes.setCreateBy("定时任务");
                        dataList.add(omsInternalOrderRes);
                    }
                } else {
                    log.error("获取SAP800系统PO真单返回信息为空！");
                    return R.error("获取SAP800系统PO真单返回信息为空！");
                }
            } else {
                log.error("获取SAP800系统PO真单数据失败：" + outParam.getString("MESSAGE"));
                return R.error(outParam.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("获取SAP800系统PO真单方法异常:" + e);
            throw new BusinessException("获取SAP800系统PO真单方法异常:" + e);
        }
        log.info("===============获取SAP800系统PO真单方法  end================");
        return R.data(dataList);
    }
}
