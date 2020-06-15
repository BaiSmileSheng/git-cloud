package com.cloud.system.service.impl;

import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.CdProductInProduction;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.domain.po.CdProductStockDetail;
import com.cloud.system.mapper.CdProductStockMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.system.service.ICdProductInProductionService;
import com.cloud.system.service.ICdProductPassageService;
import com.cloud.system.service.ICdProductStockService;
import com.cloud.system.service.ICdProductWarehouseService;
import com.cloud.system.service.ISysDictDataService;
import com.cloud.system.service.ISysInterfaceLogService;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成品库存主 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@Service
public class CdProductStockServiceImpl extends BaseServiceImpl<CdProductStock> implements ICdProductStockService {

    private final Logger logger = LoggerFactory.getLogger(CdProductStockServiceImpl.class);

    @Autowired
    private CdProductStockMapper cdProductStockMapper;

    @Autowired
    private ICdProductInProductionService cdProductInProductionService;

    @Autowired
    private ICdProductPassageService cdProductPassageService;

    @Autowired
    private ICdProductWarehouseService cdProductWarehouseService;

    @Autowired
    private ISysDictDataService sysDictDataService;

    @Autowired
    private ICdFactoryInfoService cdFactoryInfoService;

    @Autowired
    private ICdMaterialInfoService cdMaterialInfoService;

    @Autowired
    private ISysInterfaceLogService sysInterfaceLogService;

    private static final String FACTORY_REJECTSTORE_RELATION = "factory_rejectstore_relation";//字典表中类型 sap成品库存信息中不良成品存放的库位

    private static final String NO_STOCK_TYPE = "1";//不良成品库位标记

    /**
     * 删除全表
     * @return
     */
    @Override
    public R deleteAll() {
        cdProductStockMapper.deleteAll();
        return R.ok();
    }

    /**
     * 同步成品库存
     * @param factoryCode 工厂编号
     * @param materialCode 物料编号
     * @return
     */
    @Transactional
    @Override
    public R sycProductStock(String factoryCode,String materialCode) {
        //根据工厂和物料号 获取物料号和物料描述
        Example exampleMaterialInfo = new Example(CdMaterialInfo.class);
        Example.Criteria criteria = exampleMaterialInfo.createCriteria();
        criteria.andEqualTo("plantCode",factoryCode);
        criteria.andEqualTo("materialCode",materialCode);
        List<CdMaterialInfo> cdMaterialInfoList = cdMaterialInfoService.selectByExample(exampleMaterialInfo);
        Map<String,String>  materialMap = cdMaterialInfoList.stream().collect(Collectors.toMap(
                CdMaterialInfo::getMaterialCode, CdMaterialInfo::getMaterialDesc));
        disposeProductStock(Arrays.asList(factoryCode),Arrays.asList(materialCode), materialMap);
        return R.ok();
    }

    /**
     * 定时任务同步成品库存
     * @return
     */
    @Transactional
    @Override
    public R timeSycProductStock() {
        //1.获取工厂全部信息cd_factory_info 和 物料号
        Example exampleFactoryInfo = new Example(CdFactoryInfo.class);
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(exampleFactoryInfo);
        List<String> factoryCodelist = cdFactoryInfoList.stream().map(cdFactoryInfo->{
            return cdFactoryInfo.getFactoryCode();
        }).collect(Collectors.toList());
        Example exampleMaterialInfo = new Example(CdMaterialInfo.class);
        Example.Criteria criteria = exampleMaterialInfo.createCriteria();
        List<CdMaterialInfo> cdMaterialInfoList = cdMaterialInfoService.selectByExample(exampleMaterialInfo);
        Map<String,String>  materialMap = cdMaterialInfoList.stream().collect(Collectors.toMap(
                CdMaterialInfo::getMaterialCode, CdMaterialInfo::getMaterialDesc));
        //2.调用SAP  ZSD_INT_DDPS_02 获取SAP成品库存信息   插入明细表
        //3.汇总数据 插入主表数据库
        disposeProductStock(factoryCodelist,null,materialMap);
        return R.ok();
    }

    /**
     *  处理SAP库存 存入数据库
     * @param factoryCodeList 工厂编号
     * @param materialCodeList 物料编号
     * @param materialMap 物料编号 和 物料描述
     * @return
     */
    private void disposeProductStock(List<String> factoryCodeList,List<String> materialCodeList,Map<String,String>  materialMap){
        //2.调用SAP  ZSD_INT_DDPS_02 获取SAP成品库存信息   插入明细表
        CdProductStockDetail cdProductStockDetail = sycSAPProductStock(factoryCodeList,materialCodeList,materialMap);
        //成品库存主表 寄售不足列表
        List<CdProductStock> cdProductStockList = cdProductStockDetail.getCdProductStockList();
        //成品库存在产明细
        List<CdProductInProduction> cdProductInProductionList = cdProductStockDetail.getCdProductInProductionList();
        //成品库存在途明细
        List<CdProductPassage> cdProductPassageList = cdProductStockDetail.getCdProductPassageList();
        //成品库存在库明细
        List<CdProductWarehouse> cdProductWarehouseList = cdProductStockDetail.getCdProductWarehouseList();
        //3.汇总数据 插入数据库  key是工厂+物料号
        Map<String,CdProductStock> cdProductStockMap = cdProductStockList.stream().collect(Collectors.toMap(
                cdProductStock -> cdProductStock.getProductFactoryCode()+cdProductStock.getProductMaterialCode(),
                cdProductStock -> cdProductStock));
        Map<String,CdProductInProduction> cdProductInProductionMap = cdProductInProductionList.stream().collect(Collectors.toMap(
                cdProductInProduction -> cdProductInProduction.getProductFactoryCode()+cdProductInProduction.getProductMaterialCode(),
                cdProductInProduction -> cdProductInProduction));
        Map<String,CdProductPassage> cdProductPassageMap = cdProductPassageList.stream().collect(Collectors.toMap(
                cdProductPassage -> cdProductPassage.getProductFactoryCode()+cdProductPassage.getProductMaterialCode(),
                cdProductPassage -> cdProductPassage));

        //在库库存(根据SAP在库库存去掉不良库存)
        Map<String,CdProductStock> cdProductStockMapL = new HashMap<>();
        //不良库存(根据SAP在库库存标记为不良库存)
        Map<String,CdProductStock> cdProductStockMapB = new HashMap<>();
        //将在库库存 分为不良库存和在库库存汇总
        changeLandB(cdProductWarehouseList,cdProductStockMapL,cdProductStockMapB);

        //增量CdProductStock数据
        List<CdProductStock> productStockList = tabulateData(cdProductStockMap, cdProductInProductionMap,
                cdProductPassageMap,cdProductStockMapL, cdProductStockMapB);
        //插入数据库
        cdProductStockMapper.deleteAll();
        cdProductStockMapper.insertList(productStockList);
    }

    /**
     * 将SAP在库库存 转换成在库库存和不良库存
     * @param cdProductWarehouseList  SAP在库库存
     * @param cdProductStockMapL 在库库存
     * @param cdProductStockMapB 不良库存
     */
    private void changeLandB(List<CdProductWarehouse> cdProductWarehouseList,Map<String,CdProductStock> cdProductStockMapL,
                             Map<String,CdProductStock> cdProductStockMapB){
        cdProductWarehouseList.forEach(cdProductWarehouse ->{
            //不良标记
            Boolean flagB = NO_STOCK_TYPE.equals(cdProductWarehouse.getStockType());
            String code = cdProductWarehouse.getProductFactoryCode()+cdProductWarehouse.getProductMaterialCode();
            if(flagB){
                if(cdProductStockMapB.containsKey(code)){
                    //累加数量
                    CdProductStock cdProductStock =  cdProductStockMapB.get(code);
                    BigDecimal rejectsNum = cdProductStock.getRejectsNum();
                    BigDecimal rejectsNumX = cdProductWarehouse.getWarehouseNum();
                    BigDecimal rejectsNumZ = rejectsNum.add(rejectsNumX);
                    cdProductStock.setRejectsNum(rejectsNumZ);
                }else{
                    CdProductStock cdProductStock = new CdProductStock();
                    cdProductStock.setProductFactoryCode(cdProductWarehouse.getProductFactoryCode());
                    cdProductStock.setProductMaterialCode(cdProductWarehouse.getProductMaterialCode());
                    cdProductStock.setProductMaterialDesc(cdProductWarehouse.getProductMaterialDesc());
                    cdProductStock.setRejectsNum(cdProductWarehouse.getWarehouseNum());
                    cdProductStock.setUnit(cdProductWarehouse.getUnit());
                    cdProductStock.setCreateBy("定时任务");
                    cdProductStock.setCreateTime(new Date());
                    cdProductStockMapB.put(code,cdProductStock);
                }
            }else{
                if(cdProductStockMapL.containsKey(code)){
                    //累加数量
                    CdProductStock cdProductStock =  cdProductStockMapL.get(code);
                    BigDecimal stockWNum = cdProductStock.getStockWNum();
                    BigDecimal stockWNumX = cdProductWarehouse.getWarehouseNum();
                    BigDecimal stockWNumZ = stockWNum.add(stockWNumX);
                    cdProductStock.setStockWNum(stockWNumZ);
                }else{
                    CdProductStock cdProductStock = new CdProductStock();
                    cdProductStock.setProductFactoryCode(cdProductWarehouse.getProductFactoryCode());
                    cdProductStock.setProductMaterialCode(cdProductWarehouse.getProductMaterialCode());
                    cdProductStock.setProductMaterialDesc(cdProductWarehouse.getProductMaterialDesc());
                    cdProductStock.setStockWNum(cdProductWarehouse.getWarehouseNum());
                    cdProductStock.setUnit(cdProductWarehouse.getUnit());
                    cdProductStock.setCreateBy("定时任务");
                    cdProductStock.setCreateTime(new Date());
                    cdProductStockMapL.put(code,cdProductStock);
                }
            }
        });
    }

    /**
     * 汇总为成品库存数据
      * @param cdProductStockMap
     * @param cdProductInProductionMap
     * @param cdProductPassageMap
     * @param cdProductStockMapL
     * @param cdProductStockMapB
     * @return
     */
    private List<CdProductStock> tabulateData(Map<String,CdProductStock> cdProductStockMap,Map<String,CdProductInProduction> cdProductInProductionMap,
                                              Map<String,CdProductPassage> cdProductPassageMap,Map<String,CdProductStock> cdProductStockMapL,
                                              Map<String,CdProductStock> cdProductStockMapB){
        cdProductInProductionMap.keySet().forEach(code ->{
            CdProductInProduction cdProductInProduction = cdProductInProductionMap.get(code);
            if(cdProductStockMap.containsKey(code)){
                //将更新 在产数量
                CdProductStock cdProductStock = cdProductStockMap.get(code);
                cdProductStock.setStockPNum(cdProductInProduction.getInProductionNum());
            }else{
                CdProductStock cdProductStock = new CdProductStock();
                cdProductStock.setProductFactoryCode(cdProductInProduction.getProductFactoryCode());
                cdProductStock.setProductMaterialCode(cdProductInProduction.getProductMaterialCode());
                cdProductStock.setProductMaterialDesc(cdProductInProduction.getProductMaterialDesc());
                cdProductStock.setStockPNum(cdProductInProduction.getInProductionNum());
                cdProductStock.setUnit(cdProductInProduction.getUnit());
                cdProductStock.setCreateBy("定时任务");
                cdProductStock.setCreateTime(new Date());
                cdProductStockMap.put(code,cdProductStock);
            }
        });
        cdProductPassageMap.keySet().forEach(code ->{
            CdProductPassage cdProductPassage = cdProductPassageMap.get(code);
            if(cdProductStockMap.containsKey(code)){
                //将更新 在途数量
                CdProductStock cdProductStock = cdProductStockMap.get(code);
                BigDecimal stockINum = cdProductStock.getStockINum();
                BigDecimal stockINumX = cdProductPassage.getPassageNum();
                BigDecimal stockINumZ = stockINum.add(stockINumX);
                cdProductStock.setStockINum(stockINumZ);
            }else{
                CdProductStock cdProductStock = new CdProductStock();
                cdProductStock.setProductFactoryCode(cdProductPassage.getProductFactoryCode());
                cdProductStock.setProductMaterialCode(cdProductPassage.getProductMaterialCode());
                cdProductStock.setProductMaterialDesc(cdProductPassage.getProductMaterialDesc());
                cdProductStock.setStockINum(cdProductPassage.getPassageNum());
                cdProductStock.setUnit(cdProductPassage.getUnit());
                cdProductStock.setCreateBy("定时任务");
                cdProductStock.setCreateTime(new Date());
                cdProductStockMap.put(code,cdProductStock);
            }
        });

        cdProductStockMapL.keySet().forEach(code ->{
            CdProductStock cdProductStock = cdProductStockMapL.get(code);
            if(cdProductStockMap.containsKey(code)){
                //将更新 在库数量
                CdProductStock cdProductStockRes = cdProductStockMap.get(code);
                BigDecimal stockWNum = cdProductStockRes.getStockWNum();
                BigDecimal stockWNumX = cdProductStock.getStockWNum();
                BigDecimal stockWNumZ = stockWNum.add(stockWNumX);
                cdProductStockRes.setStockWNum(stockWNumZ);
            }else{
                cdProductStockMap.put(code,cdProductStock);
            }
        });

        cdProductStockMapB.keySet().forEach(code ->{
            CdProductStock cdProductStock = cdProductStockMapB.get(code);
            if(cdProductStockMap.containsKey(code)){
                //将更新 不良数量
                CdProductStock cdProductStockRes = cdProductStockMap.get(code);
                BigDecimal rejectsNum = cdProductStockRes.getRejectsNum();
                BigDecimal rejectsNumX = cdProductStock.getStockWNum();
                BigDecimal rejectsNumZ = rejectsNum.add(rejectsNumX);
                cdProductStockRes.setRejectsNum(rejectsNumZ);
            }else {
                cdProductStockMap.put(code,cdProductStock);
            }
        });

        //将map转成list
        List<CdProductStock> productStockList = cdProductStockMap.values().stream().collect(Collectors.toList());
        return productStockList;
    }

    /**
     * 获取sap成品库存信息 并插入日志
     *
     * @param factoryCodeList 工厂编号
     * @param materialCodeList 物料号
     * @param materialMap key:物料号  value物料描述
     * @return
     */
    private CdProductStockDetail sycSAPProductStock(List<String> factoryCodeList,List<String> materialCodeList, Map<String,String> materialMap) {
        JCoDestination destination;
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog();
        sysInterfaceLog.setAppId("SAP");
        sysInterfaceLog.setInterfaceName(SapConstants.ZSD_INT_DDPS_02);
        sysInterfaceLog.setCreateBy("定时任务");
        sysInterfaceLog.setCreateTime(new Date());

        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZSD_INT_DDPS_02);
            if (fm == null) {
                logger.error("同步库存成品信息 调用SAP获取ZSD_INT_DDPS_02函数失败");
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoTable inputTableW = fm.getTableParameterList().getTable("WERKS");
            for(String factoryCode : factoryCodeList){
                inputTableW.appendRow();
                inputTableW.setValue("WERKS",factoryCode);
            }
            if(!CollectionUtils.isEmpty(materialCodeList)){
                JCoTable inputTableM = fm.getTableParameterList().getTable("MATNR");
                for(String materialCode : materialCodeList){
                    inputTableM.appendRow();
                    inputTableM.setValue("MATNR",materialCode);
                }
            }
            sysInterfaceLog.setContent(factoryCodeList + "");

            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);

            //在产库存
            JCoTable outputZC = fm.getTableParameterList().getTable("OUTPUT_ZC");
            //转换对象插入数据库
            List<CdProductInProduction> productInProductionList = insertInProduction(outputZC,materialMap);
            //在途库存
            JCoTable outputZT = fm.getTableParameterList().getTable("OUTPUT_ZT");
            List<CdProductPassage> productPassageList = insertPassage(outputZT,materialMap);
            //在库库存
            JCoTable outputZK = fm.getTableParameterList().getTable("OUTPUT_ZK");
            List<CdProductWarehouse> productWarehouseList = insertWarehouse(outputZK,materialMap);
            //寄售不足库存
            JCoTable outputJS = fm.getTableParameterList().getTable("OUTPUT_JS");
            List<CdProductStock> productStockList = getJSStock(outputJS,materialMap);

            CdProductStockDetail cdProductStockDetail = new CdProductStockDetail();
            cdProductStockDetail.setCdProductInProductionList(productInProductionList);
            cdProductStockDetail.setCdProductStockList(productStockList);
            cdProductStockDetail.setCdProductPassageList(productPassageList);
            cdProductStockDetail.setCdProductWarehouseList(productWarehouseList);
            return cdProductStockDetail;
        } catch (Exception e) {
            e.printStackTrace();
            sysInterfaceLog.setRemark(e.getMessage());
            throw new BusinessException("获取sap成品库存信息异常");
        }finally {
            sysInterfaceLogService.insertSelective(sysInterfaceLog);
        }
    }

    /**
     * 将SAP在产成品库存转换成 CdProductInProduction 插入数据库
     * @param outputZC 在产成品库存表
     * @param materialMap key 物料号 value:物料描述
     * @return List<CdProductInProduction> 在产成品库存集合
     */
    private List<CdProductInProduction> insertInProduction(JCoTable outputZC,Map<String,String> materialMap){
        logger.info("将SAP在产成品库存转换成 CdProductInProduction 插入数据库开始");
        List<CdProductInProduction> productInProductionList = new ArrayList<>();
        //从输出table中获取每一行数据
        if (outputZC != null && outputZC.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputZC.getNumRows(); i++) {
                //设置指针位置
                outputZC.setRow(i);
                CdProductInProduction cdMaterialPriceInfo = changeInProduction(outputZC,materialMap);
                productInProductionList.add(cdMaterialPriceInfo);
            }
        }
        logger.info("在产成品库存 插入数据库开始");
        cdProductInProductionService.deleteAll();
        if(!CollectionUtils.isEmpty(productInProductionList)){
            cdProductInProductionService.insertList(productInProductionList);
        }
        logger.info("在产成品库存 插入数据库结束");
        return productInProductionList;
    }

    /**
     * 将SAP在产成品库存转换成 CdProductInProduction
     * @param outputZC 在产成品库存表
     * @param materialMap key 物料号 value:物料描述
     * @return 在产成品库存
     */
    private CdProductInProduction changeInProduction(JCoTable outputZC,Map<String,String> materialMap){
        CdProductInProduction cdProductInProduction = new CdProductInProduction();
        cdProductInProduction.setProductFactoryCode(outputZC.getString("WERKS"));
        cdProductInProduction.setProductMaterialCode(outputZC.getString("MATNR"));
        cdProductInProduction.setProductMaterialDesc(materialMap.get(outputZC.getString("MATNR")));
        cdProductInProduction.setInProductionVersion(outputZC.getString("VERID"));
        cdProductInProduction.setInProductionNum(outputZC.getBigDecimal("MENGE_Z"));
        cdProductInProduction.setUnit(outputZC.getString("ERFME"));
        cdProductInProduction.setCreateBy("定时任务");
        cdProductInProduction.setCreateTime(new Date());
        cdProductInProduction.setDelFlag(DeleteFlagConstants.NO_DELETED);
        return cdProductInProduction;
    }

    /**
     * 将SAP在途成品库存转换成 CdProductInProduction 插入数据库
     * @param outputZT 在途成品表
     * @param materialMap key 物料号 value:物料描述
     * @return List<CdProductPassage> 在途成品库存集合
     */
    private List<CdProductPassage> insertPassage(JCoTable outputZT,Map<String,String> materialMap){
        logger.info("将SAP在途成品库存转换成 CdProductInProduction 插入数据库开始");
        List<CdProductPassage> productPassagesList = new ArrayList<>();
        //从输出table中获取每一行数据
        if (outputZT != null && outputZT.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputZT.getNumRows(); i++) {
                //设置指针位置
                outputZT.setRow(i);
                CdProductPassage cdProductPassage = changePassage(outputZT,materialMap);
                productPassagesList.add(cdProductPassage);
            }
        }
        logger.info("在途成品库存 插入数据库开始");
        cdProductPassageService.deleteAll();
        if(!CollectionUtils.isEmpty(productPassagesList)){
            cdProductPassageService.insertList(productPassagesList);
        }
        logger.info("在途成品库存 插入数据库结束");
        return productPassagesList;
    }

    /**
     * 将SAP在途成品库存转换成 CdProductPassage
     * @param outputZT 在途成品表
     * @param materialMap key 物料号 value:物料描述
     * @return 在途成品库存
     */
    private CdProductPassage changePassage(JCoTable outputZT,Map<String,String> materialMap){
        CdProductPassage cdProductPassage = new CdProductPassage();
        cdProductPassage.setProductFactoryCode(outputZT.getString("WERKS"));
        cdProductPassage.setProductMaterialCode(outputZT.getString("MATNR"));
        cdProductPassage.setProductMaterialDesc(materialMap.get(outputZT.getString("MATNR")));
        cdProductPassage.setUnit(outputZT.getString("ERFME"));
        cdProductPassage.setStorehouseFrom(outputZT.getString("LGORT_F"));
        cdProductPassage.setStorehouseTo(outputZT.getString("LGORT_J"));
        cdProductPassage.setPassageNum(outputZT.getBigDecimal("MENGE"));
        cdProductPassage.setCreateBy("定时任务");
        cdProductPassage.setCreateTime(new Date());
        return cdProductPassage;
    }

    /**
     * 将SAP在库成品库存转换成 CdProductInProduction 插入数据库
     * @param outputZK 在库成品库存表
     * @param materialMap key:物料号 value:物料描述
     * @return List<CdProductWarehouse> 在库成品库存集合
     */
    private List<CdProductWarehouse> insertWarehouse(JCoTable outputZK,Map<String,String> materialMap){
        logger.info("将SAP在库成品库存转换成 CdProductWarehouse 插入数据库开始");
        List<CdProductWarehouse> productWarehousesList = new ArrayList<>();

        //从输出table中获取每一行数据
        if (outputZK != null && outputZK.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputZK.getNumRows(); i++) {
                //设置指针位置
                outputZK.setRow(i);
                CdProductWarehouse cdProductWarehouse = changeWarehouse(outputZK,materialMap);
                productWarehousesList.add(cdProductWarehouse);
            }
        }
        logger.info("在库成品库存 插入数据库开始");
        cdProductWarehouseService.deleteAll();
        if(!CollectionUtils.isEmpty(productWarehousesList)){
            cdProductWarehouseService.insertList(productWarehousesList);
        }
        logger.info("在库成品库存 插入数据库结束");
        return productWarehousesList;
    }

    /**
     * 将SAP在库成品库存转换成 CdProductWarehouse
     * @param outputZK 在库成品库存表
     * @param materialMap key:物料号 value:物料描述
     * @return CdProductWarehouse 在库成品库存转
     */
    private CdProductWarehouse changeWarehouse(JCoTable outputZK,Map<String,String> materialMap){
        CdProductWarehouse cdProductWarehouse = new CdProductWarehouse();
        cdProductWarehouse.setProductFactoryCode(outputZK.getString("WERKS"));
        cdProductWarehouse.setProductMaterialCode(outputZK.getString("MATNR"));
        cdProductWarehouse.setProductMaterialDesc(materialMap.get(outputZK.getString("MATNR")));
        cdProductWarehouse.setStorehouse(outputZK.getString("LGORT"));
        cdProductWarehouse.setWarehouseNum(outputZK.getBigDecimal("LABST"));
        cdProductWarehouse.setUnit(outputZK.getString("ERFME"));
        cdProductWarehouse.setCreateBy("定时任务");
        cdProductWarehouse.setCreateTime(new Date());
        //根据工厂在字典表里获取不良成品 库位
        List<String> storehouseList = sysDictDataService.selectListDictLabel(FACTORY_REJECTSTORE_RELATION,outputZK.getString("WERKS"));
        //根据工厂查所对应的不良货位,如果库存地点是不良货位则设置类型为1
        Boolean flagStockType = storehouseList.contains(outputZK.getString("LGORT"));
        if(flagStockType){
            cdProductWarehouse.setStockType(NO_STOCK_TYPE);
        }
        return cdProductWarehouse;
    }

    /**
     * 将SAP寄售不足成品库存转换成 CdProductStock 库存主表
     * @param outputJS  表
     * @param materialMap  key:物料号 value:物料描述
     * @return
     */
    private List<CdProductStock> getJSStock(JCoTable outputJS,Map<String,String> materialMap){
        logger.info("将SAP寄售不足成品库存转换成 CdProductStock 库存主表");
        List<CdProductStock> productStockList = new ArrayList<>();
        //从输出table中获取每一行数据
        if (outputJS != null && outputJS.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputJS.getNumRows(); i++) {
                //设置指针位置
                outputJS.setRow(i);
                CdProductStock cdProductStock = changeJSStock(outputJS,materialMap);
                productStockList.add(cdProductStock);
            }
        }
        return productStockList;
    }

    /**
     * 将SAP寄售不足成品库存转换成 CdProductStock 库存主表
     * @param outputJS  表
     * @param materialMap  key:物料号 value:物料描述
     * @return CdProductStock 库存主表
     */
    private CdProductStock changeJSStock(JCoTable outputJS,Map<String,String> materialMap){
        CdProductStock cdProductStock = new CdProductStock();
        cdProductStock.setProductFactoryCode(outputJS.getString("WERKS"));
        cdProductStock.setProductMaterialCode(outputJS.getString("MATNR"));
        cdProductStock.setProductMaterialDesc(materialMap.get(outputJS.getString("MATNR")));
        cdProductStock.setStockKNum(outputJS.getBigDecimal("CY"));
        cdProductStock.setUnit(outputJS.getString("ERFME"));
        cdProductStock.setCreateBy("定时任务");
        cdProductStock.setCreateTime(new Date());
        return cdProductStock;
    }

}
