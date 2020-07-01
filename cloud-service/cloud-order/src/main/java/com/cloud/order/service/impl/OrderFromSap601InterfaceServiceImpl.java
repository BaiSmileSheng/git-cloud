package com.cloud.order.service.impl;

import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.service.IOrderFromSap601InterfaceService;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Order服务 - sap601系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/4
 */
@Service
@Slf4j
public class OrderFromSap601InterfaceServiceImpl implements IOrderFromSap601InterfaceService {
    /**
     * @Description: 获取SAP系统生产订单
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/4
     */
    @Override
    public R queryProductOrderFromSap601(List<OmsProductionOrder> list) {
        log.info("================获取SAP系统生产订单方法  start================");
        JCoDestination destination = null;
        if (list.size() <= 0) {
            log.error("================获取SAP系统生产订单方法,传入参数为空================");
            return R.error("获取SAP系统生产订单方法,传入参数为空！");
        }
        //定义返回的data体
        List<OmsProductionOrder> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_05);
            if (fm == null) {
                log.error("================获取SAP系统生产订单接口函数为空================");
                return R.error("获取SAP系统生产订单接口函数为空!");
            }
            JCoTable inputTable = fm.getTableParameterList().getTable("INPUT");
            for (OmsProductionOrder omsProductionOrder : list) {
                inputTable.appendRow();
                inputTable.setValue("ABLAD", omsProductionOrder.getOrderCode());
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    OmsProductionOrder omsProductionOrder = new OmsProductionOrder();
                    omsProductionOrder.setOrderCode(outTableOutput.getString("ABLAD"));//排产订单号
                    omsProductionOrder.setProductOrderCode(outTableOutput.getString("AUFNR"));//生产订单号
                    omsProductionOrder.setProductMaterialCode(outTableOutput.getString("MATNR"));//成品物料号
                    omsProductionOrder.setProductFactoryCode(outTableOutput.getString("WERKS"));//生产工厂
                    omsProductionOrder.setOrderType(outTableOutput.getString("AUART"));//订单类型
                    omsProductionOrder.setProductStartDate(outTableOutput.getString("GSTRP"));//基本开始日期
                    omsProductionOrder.setProductEndDate(outTableOutput.getString("GLTRP"));//基本结束日期
                    omsProductionOrder.setProductNum(outTableOutput.getBigDecimal("GAMNG"));//订单数量
                    omsProductionOrder.setDestination(outTableOutput.getString("LGORT"));//地点、发往地
                    omsProductionOrder.setProductLineCode(outTableOutput.getString("CY_SEQNR"));//线体
                    omsProductionOrder.setBomVersion(outTableOutput.getString("STLAL"));//BOM版本
                    dataList.add(omsProductionOrder);
                }
            } else {
                log.error("获取生产订单数据为空！");
                return R.error("获取生产订单数据为空！");
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }
        log.info("================获取SAP系统生产订单方法  end================");
        return R.data(dataList);
    }

    /**
     * @Description: 联动SAP创建生产订单
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/4
     */
    @Override
    public R createProductOrderFromSap601(List<OmsProductionOrder> list) {
        log.info("================联动SAP创建生产订单方法  start================");
        JCoDestination destination = null;
        if (list.size() <= 0) {
            log.error("================联动SAP创建生产订单方法,传入参数为空================");
            return R.error("联动SAP创建生产订单方法,传入参数为空！");
        }
        //定义返回的data体
        List<OmsProductionOrder> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_02);
            if (fm == null) {
                log.error("================获取SAP系统创建生产订单接口函数为空================");
                return R.error("获取SAP系统创建生产订单接口函数为空!");
            }
            JCoTable inputTable = fm.getTableParameterList().getTable("INPUT");
            for (OmsProductionOrder omsProductionOrder : list) {
                inputTable.appendRow();
                inputTable.setValue("MATNR", omsProductionOrder.getProductMaterialCode());
                inputTable.setValue("WERKS", omsProductionOrder.getProductFactoryCode());
                inputTable.setValue("AUART", omsProductionOrder.getOrderType());
                inputTable.setValue("GSTRP", omsProductionOrder.getProductStartDate());
                inputTable.setValue("GLTRP", omsProductionOrder.getProductEndDate());
                inputTable.setValue("GAMNG", omsProductionOrder.getProductNum());
                inputTable.setValue("LGORT", omsProductionOrder.getDestination());
                inputTable.setValue("ABLAD", omsProductionOrder.getOrderCode());
                inputTable.setValue("CY_SEQNR", omsProductionOrder.getProductLineCode());
                inputTable.setValue("VERID", omsProductionOrder.getBomVersion());
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    OmsProductionOrder omsProductionOrder = new OmsProductionOrder();
                    omsProductionOrder.setSapFlag(outTableOutput.getString("TYPE"));
                    omsProductionOrder.setOrderCode(outTableOutput.getString("ABLAD"));//排产订单号
                    omsProductionOrder.setSapMessages(outTableOutput.getString("MESSAGE"));
                    dataList.add(omsProductionOrder);
                }
            } else {
                log.error("联动SAP创建生产订单返回信息为空！");
                return R.error("联动SAP创建生产订单返回信息为空！");
            }
        } catch (Exception e) {
            log.error("联动SAP创建生产订单方法异常:" + e);
            throw new BusinessException("联动SAP创建生产订单方法异常:"+e);
        }
        log.info("================联动SAP创建生产订单方法  end================");
        return R.data(dataList);
    }
}
