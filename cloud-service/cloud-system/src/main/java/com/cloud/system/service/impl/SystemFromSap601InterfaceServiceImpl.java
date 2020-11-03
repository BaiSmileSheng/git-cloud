package com.cloud.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.*;
import com.cloud.system.service.*;
import com.sap.conn.jco.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.PrintWriter;
import java.io.StringWriter;
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

    @Autowired
    private ICdMaterialExtendInfoService cdMaterialExtendInfoService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private DataSourceTransactionManager dstManager;



    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.druid.master.url}")
    private String url;

    @Value("${spring.datasource.druid.master.username}")
    private String userName;

    @Value("${spring.datasource.druid.master.password}")
    private String password;

    private static final Integer MAX_SAP_RAW_MATERIAL_STOCK = 2000000;//sap原材料库存目前有78万数据,不会超过200万
    private static final double SMALL_SIZE = 500;//获取bom数据每次传输物料号最大数量

    private static final int SAP_RAW_MATERIAL_SIZE = 10000;//获取原材料库存数据每次获取数量

    private static final int BOM_INSERT_MAX_SIZE = 20000;//bom单次插入最大数据量

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
                inputTableMatnr.setValue("MATNR", materialCode.toUpperCase());
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(jCoFields.getString("ZTYPE"))) {
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
                        cdMaterialInfo.setUpdateBy("systemJob");
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
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(jCoFields.getString("ZTYPE"))) {
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
                        cdFactoryLineInfo.setProduceLineCode(StrUtil.toString(outTableOutput.getInt("CY_SEQNR")));
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
    public R queryRawMaterialStockFromSap601(List<String> factorys, List<String> materials,Integer startNum,Integer endNum) {
        JCoDestination destination = null;
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
            //开始序列号
            if(null != startNum){
                fm.getImportParameterList().setValue("BEGIN_NUM",startNum);
            }
            //结束序列号
            if(null != endNum){
                fm.getImportParameterList().setValue("END_NUM",endNum);
            }
            //工厂
            if(!CollectionUtils.isEmpty(factorys)){
                JCoTable inputTableWerks = fm.getTableParameterList().getTable("WERKS");
                for (String factoryCode : factorys) {
                    inputTableWerks.appendRow();
                    inputTableWerks.setValue("WERKS", factoryCode);
                }
            }
            //物料
            if (!CollectionUtils.isEmpty(materials)) {
                JCoTable inputTableMatnr = fm.getTableParameterList().getTable("MATNR");
                for (String materialCode : materials) {
                    inputTableMatnr.appendRow();
                    inputTableMatnr.setValue("MATNR", materialCode.toUpperCase());
                }
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(jCoFields.getString("ZTYPE"))) {
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
                        cdRawMaterialStock.setUpdateBy("定时调用");
                        cdRawMaterialStock.setUpdateTime(new Date());
                        cdRawMaterialStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
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
        return R.data(dataList);
    }

    /**
     * @Description: 获取BOM清单数据
     * @Param: [factorys, materials]
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/5
     */
    @Override
    public R queryBomInfoFromSap601(List<String> factorys, List<String> materials,String flag) {
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
            inputParams.setValue("IFLAG", flag);//标记
//            inputParams.setValue("E_DATUM", new Date());//当前日期，可填可不填
            //获取输入表参数
            JCoTable inputTableWerks = fm.getTableParameterList().getTable("WERKS");
            JCoTable inputTableMatnr = fm.getTableParameterList().getTable("MATNR");
            //工厂
            for (String factoryCode : factorys) {
                inputTableWerks.appendRow();
                inputTableWerks.setValue("WERKS", factoryCode);
            }
            if (!CollectionUtils.isEmpty(materials)) {
                //物料
                for (String materialCode : materials) {
                    inputTableMatnr.appendRow();
                    inputTableMatnr.setValue("MATNR", materialCode.toUpperCase());
                }
            }


            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(jCoFields.getString("FLAG"))) {
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
                        cdBomInfo.setProductMaterialDesc(outTableOutput.getString("BCPMS"));//成品物料描述
                        cdBomInfo.setProductFactoryCode(outTableOutput.getString("WERKS"));//工厂编码
                        cdBomInfo.setRawMaterialCode(outTableOutput.getString("IDNRK"));//原材料物料
                        cdBomInfo.setRawMaterialDesc(outTableOutput.getString("MAKTX"));//原材料物料描述
                        cdBomInfo.setBasicNum(outTableOutput.getBigDecimal("BMENG"));//基本数量
                        cdBomInfo.setBomNum(outTableOutput.getBigDecimal("DANHAO"));//单耗
                        cdBomInfo.setProductUnit(outTableOutput.getString("MMEIN"));//成品单位
                        cdBomInfo.setComponentUnit(outTableOutput.getString("MEINS"));//组件单位
                        String stlal = outTableOutput.getString("STLAL");
                        String version = (stlal == null) ? null : stlal.replaceAll("^(0+)", "");//BOM版本去掉前面的0
                        cdBomInfo.setVersion(version);//BOM版本
                        cdBomInfo.setPurchaseGroup(outTableOutput.getString("EKGRP"));//采购组
                        cdBomInfo.setCreateBy("定时任务");
                        cdBomInfo.setCreateTime(new Date());
                        cdBomInfo.setDelFlag(DeleteFlagConstants.NO_DELETED);
                        dataList.add(cdBomInfo);
                    }
                }
            } else {
                log.error("获取BOM清单数据失败 factorys:{},res:{}", factorys, jCoFields.getString("MESSAGE"));
                return R.error(jCoFields.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e);
            throw new BusinessException(e.getMessage());
        }
        return R.data(dataList);
    }

    @Override
    public R currentqueryRawMaterialStockFromSap601(List<CdRawMaterialStock> list, SysUser sysUser) {
        JCoDestination destination = null;
        //定义返回的data体
        List<CdRawMaterialStock> dataList = new ArrayList<>();
        try {

            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_07);
            if (fm == null) {
                log.error("==============实时获取原材料库存接口函数失败!================");
                return R.error("实时获取原材料库存接口函数失败!");
            }
            //获取输入参数
            JCoTable inputTableWerks = fm.getTableParameterList().getTable("WERKS");
            JCoTable inputTableMatnr = fm.getTableParameterList().getTable("MATNR");
            list.forEach(cdRawMaterialStock ->{
                inputTableWerks.appendRow();
                inputTableWerks.setValue("WERKS", cdRawMaterialStock.getProductFactoryCode());
                inputTableMatnr.appendRow();
                inputTableMatnr.setValue("MATNR",cdRawMaterialStock.getRawMaterialCode().toUpperCase());
            });
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的参数
            JCoParameterList jCoFields = fm.getExportParameterList();
            if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(jCoFields.getString("ZTYPE"))) {
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
                        cdRawMaterialStock.setCreateTime(new Date());
                        cdRawMaterialStock.setCreateBy(sysUser.getLoginName());
                        cdRawMaterialStock.setUpdateTime(new Date());
                        cdRawMaterialStock.setUpdateBy(sysUser.getLoginName());
                        cdRawMaterialStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
                        dataList.add(cdRawMaterialStock);
                    }
                }
            } else {
                log.error("实时获取原材料库存数据失败：" + jCoFields.getString("MESSAGE"));
                return R.error(jCoFields.getString("MESSAGE"));
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e);
            throw new BusinessException(e.getMessage());
        }
        return R.data(dataList);
    }

    /**
     * 定时获取BOM清单数据
     *
     * @return
     */
    @Transactional
    @Override
    public R sycBomInfo() {
        //记录信息
        StringBuffer msg = new StringBuffer("bom获取成功！");
        //1.获取工厂全部信息cd_factory_info
        Example exampleFactoryInfo = new Example(CdFactoryInfo.class);
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(exampleFactoryInfo);
        if (CollectionUtils.isEmpty(cdFactoryInfoList)) {
            throw new BusinessException("获取工厂信息失败");
        }
        //2.获取物料信息
        List<CdMaterialExtendInfo> materialExtendInfoList = getMaterial();
        if (CollectionUtils.isEmpty(materialExtendInfoList)) {
            throw new BusinessException("获取物料信息失败");
        }
        List<String> materialCodeList = materialExtendInfoList.stream().map(cdMaterialExtendInfo->{
            return cdMaterialExtendInfo.getMaterialCode().toUpperCase();
        }).collect(Collectors.toList());
        double size = materialCodeList.size();
        double smallSize = SMALL_SIZE;
        int materialExtendInfoCount = (int) Math.ceil(size / smallSize);
        //3.连接SAP获取数据
        int deleteFlag = 0; //删除bom表标记
        for (int z = 0; z < cdFactoryInfoList.size(); z++) {
            String factoryCode = cdFactoryInfoList.get(z).getFactoryCode();
            for (int i = 0; i < materialExtendInfoCount; i++) {
                int startCont = (int) (i * SMALL_SIZE);
                int nextI = i + 1;
                int endCount = (int) (nextI * SMALL_SIZE);
                if (endCount > materialExtendInfoList.size()) {
                    endCount = materialExtendInfoList.size();
                }
                List<String> materials = materialCodeList.subList(startCont,endCount);
                R result = queryBomInfoFromSap601(Arrays.asList(factoryCode), materials,SapConstants.ABAP_AS_SAP601_MUL);
                if (!result.isSuccess()) {
                    log.error("连接SAP获取BOM数据异常 factoryCode:{},materials:{},res:{}", factoryCode, materials, JSONObject.toJSON(result));
                    msg.append(StrUtil.format("连接SAP获取BOM数据异常 factoryCode:{},materials:{},res:{}", factoryCode, materials, JSONObject.toJSON(result)));
                    continue;
                }
                List<CdBomInfo> cdBomInfoList = (List<CdBomInfo>) result.get("data");
                log.info("获取SAP的bom数据size:{}",cdBomInfoList.size());
                msg.append(StrUtil.format("获取SAP的bom数据size:{}",cdBomInfoList.size()));
                deleteFlag++;
                if (deleteFlag == 1) {
                    insertBomDb(cdBomInfoList, Boolean.TRUE);
                } else {
                    insertBomDb(cdBomInfoList, Boolean.FALSE);
                }
            }
        }
        log.info("定时获取BOM清单数据结束");
        return R.ok(msg.toString());
    }


    /**
     * 插入bom数据库
     *
     * @param cdBomInfoList
     * @param flag
     */
    private void taskInsertBomDb(final List<CdBomInfo> cdBomInfoList, final Boolean flag) {
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    insertBomDb(cdBomInfoList, flag);
                } catch (Exception e) {
                    StringWriter w = new StringWriter();
                    e.printStackTrace(new PrintWriter(w));
                    log.error("插入bom清单异常 e:{}", w.toString());
                }
            }
        });
    }

    /**
     * 插入数据库
     *
     * @param cdBomInfoList
     * @param flag          是否是第一次插入数据库,若是,则删除全表
     */
    private void insertBomDb(final List<CdBomInfo> cdBomInfoList, final Boolean flag) {

        if (flag) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
            TransactionStatus transaction = dstManager.getTransaction(def); // 获得事务状态
            try{
                cdBomInfoService.deleteAll();
                dstManager.commit(transaction);
            }catch (Exception e){
                StringWriter w = new StringWriter();
                e.printStackTrace(new PrintWriter(w));
                log.error("删除bom清单异常 e:{}", w.toString());
                dstManager.rollback(transaction);
                throw new BusinessException("删除bom清单异常");
            }
        }
        if (!CollectionUtils.isEmpty(cdBomInfoList)) {
            double length = cdBomInfoList.size();
            int count = (int)Math.ceil(length/BOM_INSERT_MAX_SIZE);
            for(int i=0; i<count; i++) {
                int startList = i * BOM_INSERT_MAX_SIZE;
                int endList = i * BOM_INSERT_MAX_SIZE + BOM_INSERT_MAX_SIZE;
                if (endList > length) {
                    endList = (int) length;
                }
                List<CdBomInfo> cdBomInfoListSub = cdBomInfoList.subList(startList,endList);
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
                TransactionStatus transaction = dstManager.getTransaction(def); // 获得事务状态
                try{
                    cdBomInfoService.insertList(cdBomInfoListSub);
                    dstManager.commit(transaction);
                }catch (Exception e){
                    StringWriter w = new StringWriter();
                    e.printStackTrace(new PrintWriter(w));
                    log.error("插入bom清单异常 e:{}", w.toString());
                    dstManager.rollback(transaction);
                }
            }
        }
    }

    /**
     * 获取成品物料信息
     *
     * @return
     */
    private List<CdMaterialExtendInfo> getMaterial() {
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        return cdMaterialExtendInfoList;
    }

    @Transactional
    @Override
    public R sycRawMaterialStock() {
        //1.连接SAP获取数据
        int deleteFlag = 0;//删除标记
        int startInt = 0;
        int start = 1;//序列号起始值为1
        Boolean querySapFlag = Boolean.TRUE;
        while (querySapFlag){
            int startNum = startInt * SAP_RAW_MATERIAL_SIZE + start;
            int endNum = (startInt + 1) * SAP_RAW_MATERIAL_SIZE;
            R result = queryRawMaterialStockFromSap601(null, null,startNum,endNum);
            startInt ++;
            if (!result.isSuccess()) {
                log.error("连接SAP获取原材料库存数据异常 startNum:{},endNum:{},res:{}",startNum,endNum,JSONObject.toJSON(result));
            }
            if(result.isSuccess()){
                List<CdRawMaterialStock> list = (List<CdRawMaterialStock>) result.get("data");
                log.info("连接SAP获取原材料库存数据结束起始序号:{},结束序号:{},size:{}",startNum,endNum,list.size());
                deleteFlag ++;
                if(deleteFlag == 1){
                    insertRawMaterialStockDb(list, Boolean.TRUE);
                }else{
                    insertRawMaterialStockDb(list, Boolean.FALSE);
                }
                //跳出循环标记 单次查询数据<SAP_RAW_MATERIAL_SIZE
                if(list.size() < SAP_RAW_MATERIAL_SIZE){
                    querySapFlag = Boolean.FALSE;
                }
            }
            //避免调用SAP一直没数据死循环
            if(endNum > MAX_SAP_RAW_MATERIAL_STOCK){
                querySapFlag = Boolean.FALSE;
            }
        }
        log.info("获取原材料库存结束");
        return R.ok();
    }

    /**
     * 插入数据库
     *
     * @param list
     * @param flag          是否是第一次插入数据库,若是,则删除全表
     */
    private void insertRawMaterialStockDb(final List<CdRawMaterialStock> list, final Boolean flag) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        TransactionStatus transaction = dstManager.getTransaction(def); // 获得事务状态
        try {
            if (flag) {
                cdRawMaterialStockService.deleteAll();
            }
            if (!CollectionUtils.isEmpty(list)) {
                cdRawMaterialStockService.insertList(list);
            }
            dstManager.commit(transaction);
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.error("插入原材料异常 e:{}", w.toString());
            dstManager.rollback(transaction);
        }
    }
}
