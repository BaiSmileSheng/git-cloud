package com.cloud.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.service.ICdBomInfoService;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdRawMaterialStockService;
import com.cloud.system.service.SystemFromSap601InterfaceService;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: sap601系统接口
 * @Param:
 * @return:
 * @Author: ltq
 * @Date: 2020/6/2
 */
@Service
@Slf4j
public class SystemFromSap601InterfaceServiceImpl implements SystemFromSap601InterfaceService {


    @Autowired
    private ICdFactoryInfoService cdFactoryInfoService;

    @Autowired
    private ICdRawMaterialStockService cdRawMaterialStockService;

    @Autowired
    private ICdBomInfoService cdBomInfoService;
    /**
     * @Description: 获取uph数据
     * @Param: factorys, materials
     * @return:
     * @Author: ltq
     * @Date: 2020/6/2
     */
    @Override
    public R queryUphFromSap601(List<String> factorys, List<String> materials) {
        JCoDestination destination = null;
        if (factorys.size() <= 0) {
            log.info("============获取UPH数据接口传入工厂参数为空！===========");
            return R.error("获取UPH数据接口传入工厂参数为空!");
        }
        //定义返回的data体
        List<CdMaterialInfo> dataList = new ArrayList<>();
        try {

            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZMM_INT_DDPS_03);
            if (fm == null) {
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoTable inputTableWerks = fm.getTableParameterList().getTable("WERKS");
            JCoTable inputTableMatnr = fm.getTableParameterList().getTable("MATNR");
            //工厂
            for (String factoryCode : factorys) {
                inputTableWerks.appendRow();
                inputTableWerks.setValue("WERKS", factoryCode);
            }
            //物料
            for (String materialCode : materials) {
                inputTableMatnr.appendRow();
                inputTableMatnr.setValue("MATNR", materialCode);
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if ("S".equals(jCoFields.getString("ZTYPE"))) {
                //获取返回的Table
                JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
                //从输出table中获取每一行数据
                if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                    //循环取table行数据
                    for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                        //设置指针位置
                        outTableOutput.setRow(i);
                        CdMaterialInfo cdMaterialInfo = new CdMaterialInfo();
                        cdMaterialInfo.setMaterialCode(outTableOutput.getString("PLNBEZ"));
                        cdMaterialInfo.setPlantCode(outTableOutput.getString("WERKS"));
                        cdMaterialInfo.setUph(outTableOutput.getBigDecimal("UPH"));
                        dataList.add(cdMaterialInfo);
                    }
                }
            } else {
                log.error("获取uph数据失败：" + jCoFields.getString("MESSAGE"));
                return R.error(jCoFields.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e);
            throw new BusinessException(e.getMessage());
        }
        return R.data(dataList);
    }

    /**
     * @Description: 获取SAP系统工厂线体关系数据
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/2
     */
    @Override
    public R queryFactoryLineFromSap601() {
        JCoDestination destination = null;
        //定义返回的data体
        List<CdFactoryLineInfo> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_03);
            if (fm == null) {
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            String type = jCoFields.getString("ZTYPE");
            if ("S".equals(type)) {
                //获取返回的Table
                JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
                //从输出table中获取每一行数据
                if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                    //循环取table行数据
                    for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                        //设置指针位置
                        outTableOutput.setRow(i);
                        CdFactoryLineInfo cdFactoryLineInfo = new CdFactoryLineInfo();
                        cdFactoryLineInfo.setProductFactoryCode(outTableOutput.getString("WERKS"));
                        cdFactoryLineInfo.setProduceLineCode(outTableOutput.getString("CY_SEQNR"));
                        cdFactoryLineInfo.setSupplierCode(outTableOutput.getString("VENDOR"));
                        cdFactoryLineInfo.setBranchOffice(outTableOutput.getString("BOSS"));
                        cdFactoryLineInfo.setMonitor(outTableOutput.getString("BANZ"));
                        cdFactoryLineInfo.setAttribute(outTableOutput.getString("SHUX"));
                        dataList.add(cdFactoryLineInfo);
                    }
                }
            } else {
                log.error("获取工厂线体关系数据失败：" + jCoFields.getString("MESSAGE"));
                return R.error(jCoFields.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }
        return R.data(dataList);
    }

    /**
     * @Description: 获取原材料库存接口
     * @Param: [list]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @Override
    public R queryRawMaterialStockFromSap601(List<String> factorys, List<String> materials) {
        JCoDestination destination = null;
        if (factorys.size() <= 0) {
            log.info("============获取原材料库存接口传入工厂参数为空！===========");
            return R.error("获取原材料库存接口传入工厂参数为空!");
        }
        //定义返回的data体
        List<CdRawMaterialStock> dataList = new ArrayList<>();
        try {

            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_01);
            if (fm == null) {
                log.error("==============获取原材料库存接口函数失败!================");
                return R.error("获取原材料库存接口函数失败!");
            }
            //获取输入参数
            JCoTable inputTableWerks = fm.getTableParameterList().getTable("WERKS");
            JCoTable inputTableMatnr = fm.getTableParameterList().getTable("MATNR");
            //工厂
            for (String factoryCode : factorys) {
                inputTableWerks.appendRow();
                inputTableWerks.setValue("WERKS", factoryCode);
            }
            //物料
            if(!CollectionUtils.isEmpty(materials)){
                for (String materialCode : materials) {
                    inputTableMatnr.appendRow();
                    inputTableMatnr.setValue("MATNR", materialCode);
                }
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if ("S".equals(jCoFields.getString("ZTYPE"))) {
                //获取返回的Table
                JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
                //从输出table中获取每一行数据
                if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                    //循环取table行数据
                    for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                        //设置指针位置
                        outTableOutput.setRow(i);
                        CdRawMaterialStock cdRawMaterialStock = new CdRawMaterialStock();
                        cdRawMaterialStock.setProductFactoryCode(outTableOutput.getString("WERKS"));
                        cdRawMaterialStock.setRawMaterialCode(outTableOutput.getString("MATNR"));
                        cdRawMaterialStock.setRawMaterialDesc(outTableOutput.getString("MAKTX"));
                        cdRawMaterialStock.setCurrentStock(outTableOutput.getBigDecimal("LABST"));
                        cdRawMaterialStock.setUnit(outTableOutput.getString("KMEIN"));
                        cdRawMaterialStock.setCreateBy("定时调用");
                        cdRawMaterialStock.setCreateTime(new Date());
                        dataList.add(cdRawMaterialStock);
                    }
                }
            } else {
                log.error("获取原材料库存数据失败：" + jCoFields.getString("MESSAGE"));
                return R.error(jCoFields.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e);
            throw new BusinessException(e.getMessage());
        }
        R r = new R();
        r.set("data",dataList);
        return r;
    }

    /**
     * @Description: 获取BOM清单数据
     * @Param: [factorys, materials]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @Override
    public R queryBomInfoFromSap601(List<String> factorys, List<String> materials) {
        JCoDestination destination = null;
        if (factorys.size() <= 0) {
            log.info("============获取BOM清单数据接口传入工厂参数为空！===========");
            return R.error("获取BOM清单数据接口传入工厂参数为空!");
        }
        //定义返回的data体
        List<CdBomInfo> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_06);
            if (fm == null) {
                log.error("==============获取BOM清单数据接口函数失败!================");
                return R.error("获取BOM清单数据接口函数失败!");
            }
            //获取输入参数
            JCoParameterList inputParams = fm.getImportParameterList();
            inputParams.setValue("S_DATUM",new Date());//当前日期，可填可不填
            inputParams.setValue("E_DATUM",new Date());//当前日期，可填可不填
            //获取输入表参数
            JCoTable inputTableWerks = fm.getTableParameterList().getTable("WERKS");
            JCoTable inputTableMatnr = fm.getTableParameterList().getTable("MATNR");
            //工厂
            for (String factoryCode : factorys) {
                inputTableWerks.appendRow();
                inputTableWerks.setValue("WERKS", factoryCode);
            }
            if(!CollectionUtils.isEmpty(materials)){
                //物料
                for (String materialCode : materials) {
                    inputTableMatnr.appendRow();
                    inputTableMatnr.setValue("MATNR", materialCode);
                }
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if ("S".equals(jCoFields.getString("FLAG"))) {
                //获取返回的Table
                JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
                //从输出table中获取每一行数据
                if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                    //循环取table行数据
                    for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                        //设置指针位置
                        outTableOutput.setRow(i);
                        CdBomInfo cdBomInfo = new CdBomInfo();
                        cdBomInfo.setProductMaterialCode(outTableOutput.getString("MATNR"));//成品物料
                        cdBomInfo.setProductMaterialDesc(outTableOutput.getString("MAKTX"));//成品物料描述
                        cdBomInfo.setProductFactoryCode(outTableOutput.getString("WERKS"));//工厂编码
                        cdBomInfo.setRawMaterialCode(outTableOutput.getString("IDNRK"));//原材料物料
                        cdBomInfo.setRawMaterialDesc(outTableOutput.getString("STLAN"));//原材料物料描述
                        cdBomInfo.setBasicNum(outTableOutput.getLong("BMENG"));//基本数量
                        cdBomInfo.setBomNum(outTableOutput.getBigDecimal("DANHAO"));//单耗
                        cdBomInfo.setProductUnit(outTableOutput.getString("MMEIN"));//成品单位
                        cdBomInfo.setComponentUnit(outTableOutput.getString("MEINS"));//组件单位
                        cdBomInfo.setVersion(outTableOutput.getString("STLAL"));//BOM版本
                        cdBomInfo.setPurchaseGroup(outTableOutput.getString("EKGRP"));//采购组
                        dataList.add(cdBomInfo);
                    }
                }
            } else {
                log.error("获取BOM清单数据失败 factorys:{},res:{}", factorys,jCoFields.getString("MESSAGE"));
                return R.error(jCoFields.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e);
            throw new BusinessException(e.getMessage());
        }
        R result = new R();
        result.set("data",dataList);
        return result;
    }

    @Transactional
    @Override
    public R sycBomInfo() {
        //1.获取工厂全部信息cd_factory_info
        Example exampleFactoryInfo = new Example(CdFactoryInfo.class);
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(exampleFactoryInfo);

        //2.删除全部数据
        cdBomInfoService.deleteAll();
        //3.连接SAP获取数据
        for(CdFactoryInfo cdFactoryInfo : cdFactoryInfoList){
            String factoryCode = cdFactoryInfo.getFactoryCode();
            R result = queryBomInfoFromSap601(Arrays.asList(factoryCode),null);
            if(!result.isSuccess()){
                log.error("连接SAP获取BOM数据异常 req:{},res:{}",factoryCode, JSONObject.toJSON(result));
                continue;
            }
            List<CdBomInfo> cdBomInfoList = (List<CdBomInfo>)result.get("data");
            if(!CollectionUtils.isEmpty(cdBomInfoList)){
                cdBomInfoService.insertList(cdBomInfoList);
            }
        }
        return R.ok();
    }

    @Transactional
    @Override
    public R sycRawMaterialStock() {
        //1.获取工厂全部信息cd_factory_info
        Example exampleFactoryInfo = new Example(CdFactoryInfo.class);
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(exampleFactoryInfo);
        List<String> factoryCodelist = cdFactoryInfoList.stream().map(cdFactoryInfo->{
            return cdFactoryInfo.getFactoryCode();
        }).collect(Collectors.toList());
        R result = queryRawMaterialStockFromSap601(factoryCodelist,  null);
        if(!result.isSuccess()){
            return result;
        }
        List<CdRawMaterialStock> list = (List<CdRawMaterialStock>)result.get("data");
        cdRawMaterialStockService.deleteAll();
        cdRawMaterialStockService.insertList(list);
        return R.ok();
    }

}
