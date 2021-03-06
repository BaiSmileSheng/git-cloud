package com.cloud.system.service.impl;

import cn.hutool.core.lang.Dict;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.easyexcel.SheetExcelData;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdProductInProduction;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdProductStockDetailVo;
import com.cloud.system.domain.vo.CdProductStockExportVo;
import com.cloud.system.enums.GetStockEnum;
import com.cloud.system.mapper.CdProductInProductionMapper;
import com.cloud.system.mapper.CdProductPassageMapper;
import com.cloud.system.mapper.CdProductStockMapper;
import com.cloud.system.mapper.CdProductWarehouseMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import com.cloud.system.service.ICdProductInProductionService;
import com.cloud.system.service.ICdProductPassageService;
import com.cloud.system.service.ICdProductStockService;
import com.cloud.system.service.ICdProductWarehouseService;
import com.cloud.system.service.ISysDictDataService;
import com.cloud.system.service.ISysInterfaceLogService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.cloud.system.util.ExcelStockCellMergeStrategy;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private ISysInterfaceLogService sysInterfaceLogService;

    @Autowired
    private ICdMaterialExtendInfoService cdMaterialExtendInfoService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private DataSourceTransactionManager dstManager;

    @Autowired
    private CdProductInProductionMapper cdProductInProductionMapper;

    @Autowired
    private CdProductPassageMapper cdProductPassageMapper;

    @Autowired
    private CdProductWarehouseMapper cdProductWarehouseMapper;

    private static final String FACTORY_REJECTSTORE_RELATION = "factory_rejectstore_relation";//字典表中类型 sap成品库存信息中不良成品存放的库位

    private static final String NO_STOCK_TYPE = "1";//不良成品库位标记

    private static final String STOCK_TYPE = "0";//良成品库位标记

    private static final double SMALL_SIZE = 1000;//获取成品库存数据每次传输物料号最大数量

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
     * 导出成品库存主表列表
     * @param cdProductStock  成品库存主表信息
     * @return
     */
    @Override
    public R export(CdProductStock cdProductStock) {

        List<String> productMaterialCodeList = new ArrayList<>();
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            String[] productMaterialCodeS = cdProductStock.getProductMaterialCode().split(",");
            for(String productMaterialCode : productMaterialCodeS){
                String regex = "\\s*|\t|\r|\n";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(productMaterialCode);
                String productMaterialCodeReq = m.replaceAll("");
                productMaterialCodeList.add(productMaterialCodeReq);
            }
        }
        List<CdProductStock> cdProductStockList = listByCondition(cdProductStock,productMaterialCodeList);//主数据

        List<CdProductInProduction>  cdProductInProductionList = listProductInProduction(cdProductStock,productMaterialCodeList);//在产
        //key 专用号+工厂号
        Map<String,List<CdProductInProduction>> productInProductionMap = new HashMap<>();
        for(CdProductInProduction cdProductInProduction : cdProductInProductionList){
            String key = cdProductInProduction.getProductMaterialCode() + cdProductInProduction.getProductFactoryCode();
            if(productInProductionMap.containsKey(key)){
                List<CdProductInProduction> list = productInProductionMap.get(key);
                list.add(cdProductInProduction);
            }else {
                List<CdProductInProduction> list = new ArrayList<>();
                list.add(cdProductInProduction);
                productInProductionMap.put(key,list);
            }
        }

        List<CdProductPassage> cdProductPassageList = listProductPassage(cdProductStock,productMaterialCodeList);//在途
        //key 专用号+工厂号
        Map<String,List<CdProductPassage>> productPassageMap = new HashMap<>();
        for(CdProductPassage cdProductPassage : cdProductPassageList){
            String key = cdProductPassage.getProductMaterialCode() + cdProductPassage.getProductFactoryCode();
            if(productPassageMap.containsKey(key)){
                List<CdProductPassage> list = productPassageMap.get(key);
                list.add(cdProductPassage);
            }else {
                List<CdProductPassage> list = new ArrayList<>();
                list.add(cdProductPassage);
                productPassageMap.put(key,list);
            }
        }

        List<CdProductWarehouse> cdProductWarehouseList = listProductWarehouse(cdProductStock,STOCK_TYPE,productMaterialCodeList);//在库
        //key 专用号+工厂号
        Map<String,List<CdProductWarehouse>> productWarehouseMap = new HashMap<>();
        for(CdProductWarehouse cdProductWarehouse : cdProductWarehouseList){
            String key = cdProductWarehouse.getProductMaterialCode() + cdProductWarehouse.getProductFactoryCode();
            if(productWarehouseMap.containsKey(key)){
                List<CdProductWarehouse> list = productWarehouseMap.get(key);
                list.add(cdProductWarehouse);
            }else {
                List<CdProductWarehouse> list = new ArrayList<>();
                list.add(cdProductWarehouse);
                productWarehouseMap.put(key,list);
            }
        }

        List<CdProductWarehouse>  cdProductWarehouseListB = listProductWarehouse(cdProductStock,NO_STOCK_TYPE,productMaterialCodeList);//不良
        //key 专用号+工厂号
        Map<String,List<CdProductWarehouse>> productWarehouseMapB = new HashMap<>();
        for(CdProductWarehouse cdProductWarehouse : cdProductWarehouseListB){
            String key = cdProductWarehouse.getProductMaterialCode() + cdProductWarehouse.getProductFactoryCode();
            if(productWarehouseMapB.containsKey(key)){
                List<CdProductWarehouse> list = productWarehouseMapB.get(key);
                list.add(cdProductWarehouse);
            }else {
                List<CdProductWarehouse> list = new ArrayList<>();
                list.add(cdProductWarehouse);
                productWarehouseMapB.put(key,list);
            }
        }
        List<CdProductStockExportVo> cdProductStockExportVoList = new ArrayList<>();
        for(CdProductStock cdProductStock1 : cdProductStockList){
            String key = cdProductStock1.getProductMaterialCode() + cdProductStock1.getProductFactoryCode();
            List<CdProductInProduction> productInProductionList = productInProductionMap.get(key);
            List<CdProductPassage> productPassageList = productPassageMap.get(key);
            List<CdProductWarehouse> productWarehouseList = productWarehouseMap.get(key);
            List<CdProductWarehouse> productWarehouseListB = productWarehouseMapB.get(key);
            //找出当前物料号+工厂号 最多的数据
            List<Integer> sizeList = new ArrayList<>();
            int inSize = CollectionUtils.isEmpty(productInProductionList) ? 0 : productInProductionList.size();
            sizeList.add(inSize);
            int passageSize = CollectionUtils.isEmpty(productPassageList) ? 0 : productPassageList.size();
            sizeList.add(passageSize);
            int warehouseSize = CollectionUtils.isEmpty(productWarehouseList) ? 0 : productWarehouseList.size();
            sizeList.add(warehouseSize);
            int warehouseSizeB = CollectionUtils.isEmpty(productWarehouseListB) ? 0 : productWarehouseListB.size();
            sizeList.add(warehouseSizeB);
            Collections.sort(sizeList);
            Integer maxSizeValue = sizeList.get(sizeList.size()-1);
            for(int i = 0; i < maxSizeValue; i++){
                CdProductStockExportVo cdProductStockExportVo = new CdProductStockExportVo();
                //构造数据
                setStockMessage(cdProductStock1, cdProductStockExportVo);
                setInProductionMessage(inSize,productInProductionList,i,cdProductStockExportVo);
                setPassageMessage(passageSize,productPassageList, i, cdProductStockExportVo);
                setWarehouseMessage(warehouseSize,productWarehouseList, i, cdProductStockExportVo);
                setWarehouseBMessage(warehouseSizeB,productWarehouseListB, i, cdProductStockExportVo);
                cdProductStockExportVoList.add(cdProductStockExportVo);
            }
        }

        return exportExcel(cdProductStockList, cdProductStockExportVoList);
    }

    private R exportExcel(List<CdProductStock> cdProductStockList,List<CdProductStockExportVo> cdProductStockExportVoList){
        String fileName = EasyExcelUtil.getAbsoluteFile(DateUtils.dateTimeNow() + "成品库存.xlsx");
        ExcelWriter excelWriter = EasyExcel.write(fileName).build();
        R r = new R();
        try{
            WriteSheet writeSheet1 = EasyExcel.writerSheet(1, "成品库存")
                    .head(CdProductStock.class)
                    .registerWriteHandler(EasyExcelUtil.setHorizontalCellStyleStrategy(13, 11))
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();

            int[] mergeColumnIndex = {0,1,2,3};
            WriteSheet writeSheet2 = EasyExcel.writerSheet(2,"成品库存详情")
                    .head(CdProductStockExportVo.class)
                    .registerWriteHandler(EasyExcelUtil.setHorizontalCellStyleStrategy(13, 11))
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(new ExcelStockCellMergeStrategy(2,mergeColumnIndex)).build();

            excelWriter.write(cdProductStockList, writeSheet1);
            excelWriter.write(cdProductStockExportVoList, writeSheet2);
        }catch (Exception e){
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        }finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        if (!r.isSuccess()) {
            return r;
        }
        String path = fileName;
        return EasyExcelUtilOSS.uplloadExcel(path,"成品库存.xlsx");
    }

    /**
     * 导出数据 加主数据信息
     * @param cdProductStock1
     * @param cdProductStockExportVo
     */
    private void setStockMessage(CdProductStock cdProductStock1, CdProductStockExportVo cdProductStockExportVo) {
        cdProductStockExportVo.setProductMaterialCode(cdProductStock1.getProductMaterialCode());
        cdProductStockExportVo.setProductMaterialDesc(cdProductStock1.getProductMaterialDesc());
        cdProductStockExportVo.setProductFactoryCode(cdProductStock1.getProductFactoryCode());
        cdProductStockExportVo.setSumNum(cdProductStock1.getSumNum());
        cdProductStockExportVo.setStockKNum(cdProductStock1.getStockKNum());
        cdProductStockExportVo.setCreateTime(cdProductStock1.getCreateTime());
    }

    /**
     * 导出数据 加在产信息
     * @param productInProductionList
     * @param i
     * @param cdProductStockExportVo
     */
    private void setInProductionMessage(int inSize,List<CdProductInProduction> productInProductionList,
                                        int i, CdProductStockExportVo cdProductStockExportVo) {
        if(inSize > i){
            cdProductStockExportVo.setInProductionVersion(productInProductionList.get(i).getInProductionVersion());
            cdProductStockExportVo.setInProductionNum(productInProductionList.get(i).getInProductionNum());
        }
    }

    /**
     * 导出数据 加不良信息
     * @param productWarehouseListB
     * @param i
     * @param cdProductStockExportVo
     */
    private void setWarehouseBMessage(int warehouseSizeB,List<CdProductWarehouse> productWarehouseListB, int i,
                                      CdProductStockExportVo cdProductStockExportVo) {
        if(warehouseSizeB > i){
            cdProductStockExportVo.setStorehouseB(productWarehouseListB.get(i).getStorehouse());
            cdProductStockExportVo.setWarehouseNumB(productWarehouseListB.get(i).getWarehouseNum());
        }
    }

    /**
     * 导出数据 加在库信息
     * @param productWarehouseList
     * @param i
     * @param cdProductStockExportVo
     */
    private void setWarehouseMessage(int warehouseSize,List<CdProductWarehouse> productWarehouseList,
                                     int i, CdProductStockExportVo cdProductStockExportVo) {
        if(warehouseSize > i){
            cdProductStockExportVo.setStorehouse(productWarehouseList.get(i).getStorehouse());
            cdProductStockExportVo.setWarehouseNum(productWarehouseList.get(i).getWarehouseNum());
        }
    }

    /**
     * 导出数据 加在途信息
     * @param productPassageList
     * @param i
     * @param cdProductStockExportVo
     */
    private void setPassageMessage(int passageSize,List<CdProductPassage> productPassageList, int i,
                                   CdProductStockExportVo cdProductStockExportVo) {
        if(passageSize > i){
            cdProductStockExportVo.setStorehouseFrom(productPassageList.get(i).getStorehouseFrom());
            cdProductStockExportVo.setStorehouseTo(productPassageList.get(i).getStorehouseTo());
            cdProductStockExportVo.setPassageNum(productPassageList.get(i).getPassageNum());
        }
    }

    @Override
    public R selectList(List<CdProductStock> list) {
        return R.data(cdProductStockMapper.selectByList(list));
    }

    /**
     * 根据工厂，专用号分组取成品库存
     * @param dicts
     * @return
     */
    @Override
    public R selectProductStockToMap(List<Dict> dicts) {
        return R.data(cdProductStockMapper.selectProductStockToMap(dicts));
    }

    /**
     * 查主表数据
     *
     * @param cdProductStock
     * @return
     */
    private List<CdProductStock> listByCondition(CdProductStock cdProductStock,List<String> productMaterialCodeList) {
        Example example = new Example(CdProductStock.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductStock.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());

        }
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            criteria.andIn("productMaterialCode", productMaterialCodeList);
        }
        List<CdProductStock> cdProductStockList = selectByExample(example);
        return cdProductStockList;
    }

    /**
     * 查在产数据
     * @param cdProductStock
     * @return
     */
    private List<CdProductInProduction> listProductInProduction(CdProductStock cdProductStock,List<String> productMaterialCodeList){
        Example example = new Example(CdProductInProduction.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductStock.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());

        }
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            criteria.andIn("productMaterialCode", productMaterialCodeList);
        }
        criteria.andGreaterThan("inProductionNum",0);
        List<CdProductInProduction> cdProductInProductionList = cdProductInProductionService.selectByExample(example);
        return cdProductInProductionList;
    }

    /**
     * 查在途信息
     * @param cdProductStock
     * @return
     */
    private List<CdProductPassage> listProductPassage(CdProductStock cdProductStock,List<String> productMaterialCodeList ){
        Example example = new Example(CdProductPassage.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductStock.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());

        }
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            criteria.andIn("productMaterialCode", productMaterialCodeList);
        }
        criteria.andGreaterThan("passageNum",0);
        List<CdProductPassage> productPassageList = cdProductPassageService.selectByExample(example);
        return productPassageList;
    }

    /**
     * 查在库信息
     * @param cdProductStock
     * @param stockType
     * @return
     */
    private List<CdProductWarehouse> listProductWarehouse(CdProductStock cdProductStock,String stockType,List<String> productMaterialCodeList){
        Example example = new Example(CdProductWarehouse.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdProductStock.getProductFactoryCode())){
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());

        }
        if(StringUtils.isNotBlank(cdProductStock.getProductMaterialCode())){
            criteria.andIn("productMaterialCode", productMaterialCodeList);
        }
        criteria.andEqualTo("stockType",stockType);
        List<CdProductWarehouse> cdProductWarehouseList = cdProductWarehouseService.selectByExample(example);
        return cdProductWarehouseList;
    }

    /**
     * 实时单条同步成品库存
     * @param cdProductStockList
     * @param sysUser
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public R sycProductStock(List<CdProductStock> cdProductStockList, SysUser sysUser) {

        //根据工厂在字典表里获取不良成品 库位
        //key 工厂,value 工厂对应的不良库位
        Map<String,List<String>> storehouseMap = new HashMap<>();
        cdProductStockList.forEach(cdProductStock -> {
            List<String> storehouseList = sysDictDataService.selectListDictLabel(FACTORY_REJECTSTORE_RELATION,cdProductStock.getProductFactoryCode());
            storehouseMap.put(cdProductStock.getProductFactoryCode(),storehouseList);
        });
        //1.获取SAP数据,转化为VO changeSAPToCdProductStockDetailVo
        CdProductStockDetailVo cdProductStockDetailVo = currentSAPProductStock(cdProductStockList,storehouseMap,sysUser);
        //2.构造组装数据
        structuraldata(cdProductStockDetailVo);
        //3.根据物料号和工厂编号修改库存主数据,再删除其他库存数据插入数据库
        R r = sycProductStockDB(cdProductStockDetailVo,sysUser);
        if(!r.isSuccess()){
            throw new BusinessException(r.get("msg").toString());
        }
        return R.ok();
    }

    /**
     * 根据物料号和工厂编号修改库存主数据,再删除其他库存数据插入数据库
     * @param cdProductStockDetailVo
     * @param sysUser
     * @return
     */
    private R sycProductStockDB(CdProductStockDetailVo cdProductStockDetailVo,SysUser sysUser){
        //在产
        List<CdProductInProduction>  productInProductionList = cdProductStockDetailVo.getCdProductInProductionList();
        //在途
        List<CdProductPassage> productPassagesList = cdProductStockDetailVo.getCdProductPassageList();
        //在库
        List<CdProductWarehouse>  productWarehousesList = cdProductStockDetailVo.getCdProductWarehouseList();
       //主库存
        List<CdProductStock> productStockList = cdProductStockDetailVo.getCdProductStockList();
        if(productStockList.size() == 0){
            logger.info("在SAP没有获取到数据,productStockListSize:{}",productStockList.size());
            throw new BusinessException("在SAP没有获取到数据");
        }
        //计算成品主库存中可用库存
        setSumNum(productStockList);
        productStockList.forEach(cdProductStock -> {
            Example example = new Example(CdProductStock.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());
            criteria.andEqualTo("productMaterialCode", cdProductStock.getProductMaterialCode());
            //更新成品主库存
            cdProductStock.setUpdateBy(sysUser.getLoginName());
            int count = cdProductStockMapper.updateByExampleSelective(cdProductStock,example);
            if(count == 0){
                throw new BusinessException("系统正在同步SAP成品库存,请稍后再试");
            }
            //在产删除
            Example exampleInProduction = new Example(CdProductInProduction.class);
            Example.Criteria criteriaInProduction = exampleInProduction.createCriteria();
            criteriaInProduction.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());
            criteriaInProduction.andEqualTo("productMaterialCode", cdProductStock.getProductMaterialCode());
            cdProductInProductionMapper.deleteByExample(exampleInProduction);
            //在途删除
            Example examplePassages= new Example(CdProductPassage.class);
            Example.Criteria criteriaPassages = examplePassages.createCriteria();
            criteriaPassages.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());
            criteriaPassages.andEqualTo("productMaterialCode", cdProductStock.getProductMaterialCode());
            cdProductPassageMapper.deleteByExample(examplePassages);
            //在库删除
            Example exampleWarehouse= new Example(CdProductWarehouse.class);
            Example.Criteria criteriaWarehouse= exampleWarehouse.createCriteria();
            criteriaWarehouse.andEqualTo("productFactoryCode", cdProductStock.getProductFactoryCode());
            criteriaWarehouse.andEqualTo("productMaterialCode", cdProductStock.getProductMaterialCode());
            cdProductWarehouseMapper.deleteByExample(exampleWarehouse);
        });

        //在产新增
        if(!CollectionUtils.isEmpty(productInProductionList)){
            productInProductionList.forEach(productInProduction ->{
                productInProduction.setCreateBy(sysUser.getLoginName());
                productInProduction.setUpdateBy(sysUser.getLoginName());
            });
            cdProductInProductionMapper.insertList(productInProductionList);
        }

        //在途新增
        if(!CollectionUtils.isEmpty(productPassagesList)){
            productPassagesList.forEach(productPassage ->{
                productPassage.setCreateBy(sysUser.getLoginName());
                productPassage.setUpdateBy(sysUser.getLoginName());
            });
            cdProductPassageMapper.insertList(productPassagesList);
        }
        //在库新增
        if(!CollectionUtils.isEmpty(productWarehousesList)){
            productWarehousesList.forEach(productWarehouse -> {
                productWarehouse.setCreateBy(sysUser.getLoginName());
                productWarehouse.setUpdateBy(sysUser.getLoginName());
            } );
            cdProductWarehouseMapper.insertList(productWarehousesList);
        }
        return R.ok();
    }

    /**
     * 实时单条获取sap成品库存信息 并插入日志
     * @param cdProductStockList
     * @param storehouseMap 不良库存
     * @param sysUser
     * @return
     */
    private CdProductStockDetailVo currentSAPProductStock(List<CdProductStock> cdProductStockList,Map<String,List<String>> storehouseMap,
                                                          SysUser sysUser) {
        JCoDestination destination;
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZSD_INT_DDPS_05);
            if (fm == null) {
                logger.error("获取成品库存成品信息 调用SAP获取ZSD_INT_DDPS_05函数失败");
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoTable inputTableW = fm.getTableParameterList().getTable("WERKS");
            JCoTable inputTableM = fm.getTableParameterList().getTable("MATNR");
            cdProductStockList.forEach(cdProductStock -> {
                inputTableW.appendRow();
                inputTableW.setValue("WERKS",cdProductStock.getProductFactoryCode());

                inputTableM.appendRow();
                inputTableM.setValue("MATNR",cdProductStock.getProductMaterialCode().toUpperCase());
            });

            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //将SAP获取的数据转化为CdProductStockDetailVo
            CdProductStockDetailVo cdProductStockDetail = changeSAPToCdProductStockDetailVo(storehouseMap, fm);
            return cdProductStockDetail;
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "单条获取sap成品库存信息异常: {}", w.toString());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 定时任务同步成品库存
     * @return
     */
    @Override
    public R timeSycProductStock() {
        //1.获取工厂全部信息cd_factory_info
        Example exampleFactoryInfo = new Example(CdFactoryInfo.class);
        List<CdFactoryInfo> cdFactoryInfoList = cdFactoryInfoService.selectByExample(exampleFactoryInfo);
        List<String> factoryCodelist = cdFactoryInfoList.stream().map(cdFactoryInfo->{
            return cdFactoryInfo.getFactoryCode();
        }).collect(Collectors.toList());
        //2.获取成品物料编号
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isGetStock", GetStockEnum.IS_GET_STOCK_1.getCode());
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = cdMaterialExtendInfoService.selectByExample(example);
        List<String> materialCodeList = cdMaterialExtendInfoList.stream().map(cdMaterialExtendInfo ->{
            return cdMaterialExtendInfo.getMaterialCode().toUpperCase();
        }).collect(Collectors.toList());
        //3.调用SAP  ZSD_INT_DDPS_02 获取SAP成品库存信息
        //4.汇总数据 插入主表数据库
        disposeProductStock(factoryCodelist,materialCodeList);
        return R.ok();
    }

    /**
     *  处理SAP库存 存入数据库
     * @param factoryCodeList 工厂编号
     * @param materialCodeList 物料编号
     * @return
     */
    private void disposeProductStock(List<String> factoryCodeList,List<String> materialCodeList){
        double size = materialCodeList.size();
        double smallSize = SMALL_SIZE;
        int materialExtendInfoCount = (int) Math.ceil(size / smallSize);
        int deleteFlag = 0; //删除成品库存表标记 1时删除
        for(int i=0; i < factoryCodeList.size(); i++ ){
            String factoryCode = factoryCodeList.get(i);
            //根据工厂在字典表里获取不良成品 库位
            Map<String,List<String>> storehouseMap = new HashMap<>();
            List<String> storehouseList = sysDictDataService.selectListDictLabel(FACTORY_REJECTSTORE_RELATION,factoryCode);
            storehouseMap.put(factoryCode,storehouseList);
            for(int j=0; j <materialExtendInfoCount; j++ ){
                int startCont = (int) (j * SMALL_SIZE);
                int nextI = j + 1;
                int endCount = (int) (nextI * SMALL_SIZE);
                if (endCount > materialCodeList.size()) {
                    endCount = materialCodeList.size();
                }
                List<String> materials = new ArrayList<>();
                materials = materialCodeList.subList(startCont,endCount);

                //2.调用SAP  ZSD_INT_DDPS_02 获取SAP成品库存信息
                logger.info("调用SAP  ZSD_INT_DDPS_02 获取SAP成品库存信息 factoryCode:{},materials:{}",factoryCode,null);
                CdProductStockDetailVo cdProductStockDetail = sycSAPProductStock(factoryCode,materials,storehouseMap);
                //3.构造组装数据
                structuraldata(cdProductStockDetail);
                //4.插入数据库
                deleteFlag ++;
                if (deleteFlag == 1) {
                    insertDb(cdProductStockDetail,Boolean.TRUE);
                } else {
                    taskInsertStockDb(cdProductStockDetail, Boolean.FALSE);
                }
            }
        }
    }

    /**
     * 构造数据
     * @param cdProductStockDetail
     */
    private void structuraldata(CdProductStockDetailVo cdProductStockDetail) {
        //成品库存主表 寄售不足列表
        List<CdProductStock> cdProductStockList = cdProductStockDetail.getCdProductStockList();
        //成品库存在产明细
        List<CdProductInProduction> cdProductInProductionList = cdProductStockDetail.getCdProductInProductionList();
        //成品库存在途明细
        List<CdProductPassage> cdProductPassageList = cdProductStockDetail.getCdProductPassageList();
        //成品库存在库明细
        List<CdProductWarehouse> cdProductWarehouseList = cdProductStockDetail.getCdProductWarehouseList();
        //3.汇总数据 插入数据库  key是工厂+物料号
        //寄售不足Map
        Map<String,CdProductStock> cdProductStockMap = new HashMap<>();
        cdProductStockList.forEach(cdProductStock ->{
            String key = cdProductStock.getProductFactoryCode()+cdProductStock.getProductMaterialCode();
            if(cdProductStockMap.containsKey(key)){
                CdProductStock cdProductStock1 = cdProductStockMap.get(key);
                BigDecimal stockKNumO = cdProductStock1.getStockKNum();
                BigDecimal stockKNumX = cdProductStock.getStockKNum();
                BigDecimal stockKNum = stockKNumO.add(stockKNumX);
                cdProductStock1.setStockKNum(stockKNum);
                cdProductStockMap.put(key,cdProductStock1);
            }else {
                CdProductStock cdProductStock1 = new CdProductStock();
                cdProductStock1.setProductFactoryCode(cdProductStock.getProductFactoryCode());
                cdProductStock1.setProductMaterialCode(cdProductStock.getProductMaterialCode());
                cdProductStock1.setProductMaterialDesc(cdProductStock.getProductMaterialDesc());
                cdProductStock1.setStockKNum(cdProductStock.getStockKNum());
                cdProductStock1.setUnit(cdProductStock.getUnit());
                cdProductStock1.setCreateBy("定时任务");
                cdProductStock1.setCreateTime(new Date());
                cdProductStock1.setUpdateBy("定时任务");
                cdProductStock1.setUpdateTime(new Date());
                cdProductStock1.setDelFlag(DeleteFlagConstants.NO_DELETED);
                cdProductStockMap.put(key,cdProductStock1);
            }
        });
        //在产
        Map<String,CdProductInProduction> cdProductInProductionMap = new HashMap<>();
        cdProductInProductionList.forEach(cdProductInProduction ->{
            String key = cdProductInProduction.getProductFactoryCode()+cdProductInProduction.getProductMaterialCode();
            if(cdProductInProductionMap.containsKey(key)){
                CdProductInProduction cdProductInProduction1 = cdProductInProductionMap.get(key);
                BigDecimal inProductionNumX = cdProductInProduction1.getInProductionNum();
                BigDecimal inProductionNumO = cdProductInProduction.getInProductionNum();
                BigDecimal inProductionNum = inProductionNumX.add(inProductionNumO);
                cdProductInProduction1.setInProductionNum(inProductionNum);
                cdProductInProductionMap.put(key,cdProductInProduction1);
            }else {
                CdProductInProduction cdProductInProduction1 = new CdProductInProduction();
                cdProductInProduction1.setProductFactoryCode(cdProductInProduction.getProductFactoryCode());
                cdProductInProduction1.setProductMaterialCode(cdProductInProduction.getProductMaterialCode());
                cdProductInProduction1.setProductMaterialDesc(cdProductInProduction.getProductMaterialDesc());
                cdProductInProduction1.setInProductionNum(cdProductInProduction.getInProductionNum());
                cdProductInProduction1.setUnit(cdProductInProduction.getUnit());
                cdProductInProduction1.setCreateBy("定时任务");
                cdProductInProduction1.setCreateTime(new Date());
                cdProductInProduction1.setUpdateBy("定时任务");
                cdProductInProduction1.setUpdateTime(new Date());
                cdProductInProduction1.setDelFlag(DeleteFlagConstants.NO_DELETED);
                cdProductInProductionMap.put(key,cdProductInProduction1);
            }
        });
        //在途
        Map<String,CdProductPassage> cdProductPassageMap = new HashMap<>();
        cdProductPassageList.forEach(cdProductPassage ->{
            String key = cdProductPassage.getProductFactoryCode()+cdProductPassage.getProductMaterialCode();
            if(cdProductPassageMap.containsKey(key)){
                CdProductPassage cdProductPassage1 = cdProductPassageMap.get(key);
                BigDecimal passageNumX = cdProductPassage1.getPassageNum();
                BigDecimal passageNumO = cdProductPassage.getPassageNum();
                BigDecimal passageNum = passageNumX.add(passageNumO);
                cdProductPassage1.setPassageNum(passageNum);
                cdProductPassageMap.put(key,cdProductPassage1);
            }else {
                CdProductPassage cdProductPassage1 = new CdProductPassage();
                cdProductPassage1.setProductFactoryCode(cdProductPassage.getProductFactoryCode());
                cdProductPassage1.setProductMaterialCode(cdProductPassage.getProductMaterialCode());
                cdProductPassage1.setProductMaterialCode(cdProductPassage.getProductMaterialDesc());
                cdProductPassage1.setPassageNum(cdProductPassage.getPassageNum());
                cdProductPassage1.setUnit(cdProductPassage.getUnit());
                cdProductPassage1.setCreateBy("定时任务");
                cdProductPassage1.setCreateTime(new Date());
                cdProductPassage1.setUpdateBy("定时任务");
                cdProductPassage1.setUpdateTime(new Date());
                cdProductPassage1.setDelFlag(DeleteFlagConstants.NO_DELETED);
                cdProductPassageMap.put(key,cdProductPassage1);
            }
        });
        //在库库存(根据SAP在库库存去掉不良库存)
        Map<String,CdProductStock> cdProductStockMapL = new HashMap<>();
        //不良库存(根据SAP在库库存标记为不良库存)
        Map<String,CdProductStock> cdProductStockMapB = new HashMap<>();
        //将在库库存 分为不良库存和在库库存汇总
        changeLandB(cdProductWarehouseList,cdProductStockMapL,cdProductStockMapB);

        //增量CdProductStock数据
        List<CdProductStock> productStockList = tabulateData(cdProductStockMap, cdProductInProductionMap,
                cdProductPassageMap,cdProductStockMapL, cdProductStockMapB);

        cdProductStockDetail.setCdProductStockList(productStockList);
    }

    /**
     * 插入数据库
     * @param cdProductStockDetail
     * @param flag
     */
    private void taskInsertStockDb(final CdProductStockDetailVo cdProductStockDetail, final Boolean flag) {
        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    insertDb(cdProductStockDetail, flag);
                } catch (Exception e) {
                    StringWriter w = new StringWriter();
                    e.printStackTrace(new PrintWriter(w));
                    logger.error("插入成品库存异常 e:{}", w.toString());
                }
            }
        });
    }

    /**
     * 插入数据库
     * @param cdProductStockDetail
     * @param flag
     */
    private void insertDb(CdProductStockDetailVo cdProductStockDetail, Boolean flag){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        TransactionStatus transaction = dstManager.getTransaction(def); // 获得事务状态
        try {
            if(flag){
                cdProductPassageService.deleteAll();
                cdProductInProductionService.deleteAll();
                cdProductWarehouseService.deleteAll();
                cdProductStockMapper.deleteAll();
            }
            List<CdProductInProduction>  productInProductionList = cdProductStockDetail.getCdProductInProductionList();
            logger.info("在产成品库存 插入数据库开始");
            if(!CollectionUtils.isEmpty(productInProductionList)){
                cdProductInProductionService.insertList(productInProductionList);
            }
            logger.info("在产成品库存 插入数据库结束 数量:{}",productInProductionList.size());
            List<CdProductPassage> productPassagesList = cdProductStockDetail.getCdProductPassageList();
            logger.info("在途成品库存 插入数据库开始");
            if(!CollectionUtils.isEmpty(productPassagesList)){
                cdProductPassageService.insertList(productPassagesList);
            }
            logger.info("在途成品库存 插入数据库结束 数量:{}",productPassagesList.size());

            List<CdProductWarehouse>  productWarehousesList = cdProductStockDetail.getCdProductWarehouseList();
            logger.info("在库成品库存 插入数据库开始");
            if(!CollectionUtils.isEmpty(productWarehousesList)){
                cdProductWarehouseService.insertList(productWarehousesList);
            }
            logger.info("在库成品库存 插入数据库结束 数量:{}",productWarehousesList.size());
            List<CdProductStock> productStockList = cdProductStockDetail.getCdProductStockList();
            logger.info("成品库存主数据 插入数据库开始");
            if(!CollectionUtils.isEmpty(productStockList)){
                /**
                 * 计算可用库存
                 */
                setSumNum(productStockList);
                cdProductStockMapper.insertList(productStockList);
            }
            logger.info("成品库存主数据 插入数据库结束 数量:{}",productStockList.size());
            dstManager.commit(transaction);
        }catch (Exception e){
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "获取成品库存异常: {}", w.toString());
            logger.info("获取成品库存异常");
            dstManager.rollback(transaction);
        }
    }

    /**
     * 计算可用库存
     * @param productStockList
     */
    private void setSumNum(List<CdProductStock> productStockList) {
        productStockList.forEach(productStock -> {
            BigDecimal sumNum;
            BigDecimal stockPNum = productStock.getStockPNum();
            BigDecimal stockWNum = productStock.getStockWNum();
            BigDecimal stockINum = productStock.getStockINum();
            BigDecimal stockKNum = productStock.getStockKNum();
            if (stockKNum.compareTo(BigDecimal.ZERO) == -1) {
                sumNum = stockPNum.add(stockWNum).add(stockINum).add(stockKNum);
            } else {
                sumNum = stockPNum.add(stockWNum).add(stockINum);
            }
            productStock.setSumNum(sumNum);
        });
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
                    cdProductStock.setUpdateBy("定时任务");
                    cdProductStock.setUpdateTime(new Date());
                    cdProductStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
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
                    cdProductStock.setUpdateBy("定时任务");
                    cdProductStock.setUpdateTime(new Date());
                    cdProductStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
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
                cdProductStock.setUpdateBy("定时任务");
                cdProductStock.setUpdateTime(new Date());
                cdProductStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
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
                cdProductStock.setUpdateBy("定时任务");
                cdProductStock.setUpdateTime(new Date());
                cdProductStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
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
                BigDecimal rejectsNumX = cdProductStock.getRejectsNum();
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
     * @param factoryCode 工厂编号
     * @param materialCodeList 物料号
     * @param storehouseMap 不良品存放库位
     * @return
     */
    private CdProductStockDetailVo sycSAPProductStock(String factoryCode, List<String> materialCodeList,Map<String,List<String>> storehouseMap) {
        JCoDestination destination;
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
            inputTableW.appendRow();
            inputTableW.setValue("WERKS",factoryCode);
            if(!CollectionUtils.isEmpty(materialCodeList)){
                JCoTable inputTableM = fm.getTableParameterList().getTable("MATNR");
                for(String materialCode : materialCodeList){
                    inputTableM.appendRow();
                    inputTableM.setValue("MATNR",materialCode.toUpperCase());
                }
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //将SAP获取的数据转化为CdProductStockDetailVo
            CdProductStockDetailVo cdProductStockDetail = changeSAPToCdProductStockDetailVo(storehouseMap, fm);
            return cdProductStockDetail;
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "获取sap成品库存信息异常: {}", w.toString());
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 将SAP获取的数据转化为CdProductStockDetailVo
     * @param storehouseMap
     * @param fm
     * @return
     */
    private CdProductStockDetailVo changeSAPToCdProductStockDetailVo(Map<String,List<String>> storehouseMap, JCoFunction fm) {
        //在产库存
        JCoTable outputZC = fm.getTableParameterList().getTable("OUTPUT_ZC");
        //转换对象插入数据库
        List<CdProductInProduction> productInProductionList = getInProductionList(outputZC);
        //在途库存
        JCoTable outputZT = fm.getTableParameterList().getTable("OUTPUT_ZT");
        List<CdProductPassage> productPassageList = getPassageList(outputZT);
        //在库库存
        JCoTable outputZK = fm.getTableParameterList().getTable("OUTPUT_ZK");
        List<CdProductWarehouse> productWarehouseList = getWarehouseList(outputZK,storehouseMap);
        //寄售不足库存
        JCoTable outputJS = fm.getTableParameterList().getTable("OUTPUT_JS");
        List<CdProductStock> productStockList = getJSStock(outputJS);

        CdProductStockDetailVo cdProductStockDetail = new CdProductStockDetailVo();
        cdProductStockDetail.setCdProductInProductionList(productInProductionList);
        cdProductStockDetail.setCdProductStockList(productStockList);
        cdProductStockDetail.setCdProductPassageList(productPassageList);
        cdProductStockDetail.setCdProductWarehouseList(productWarehouseList);
        return cdProductStockDetail;
    }

    /**
     * 将SAP在产成品库存转换成 CdProductInProduction
     * @param outputZC 在产成品库存表
     * @return List<CdProductInProduction> 在产成品库存集合
     */
    private List<CdProductInProduction> getInProductionList(JCoTable outputZC){
        logger.info("将SAP在产成品库存转换成 CdProductInProduction 插入数据库开始");
        List<CdProductInProduction> productInProductionList = new ArrayList<>();
        //从输出table中获取每一行数据
        if (outputZC != null && outputZC.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputZC.getNumRows(); i++) {
                //设置指针位置
                outputZC.setRow(i);
                CdProductInProduction cdMaterialPriceInfo = changeInProduction(outputZC);
                productInProductionList.add(cdMaterialPriceInfo);
            }
        }
        return productInProductionList;
    }

    /**
     * 将SAP在产成品库存转换成 CdProductInProduction
     * @param outputZC 在产成品库存表
     * @return 在产成品库存
     */
    private CdProductInProduction changeInProduction(JCoTable outputZC){
        CdProductInProduction cdProductInProduction = new CdProductInProduction();
        cdProductInProduction.setProductFactoryCode(outputZC.getString("WERKS"));
        cdProductInProduction.setProductMaterialCode(outputZC.getString("MATNR"));
        cdProductInProduction.setProductMaterialDesc(outputZC.getString("MAKTX"));
        cdProductInProduction.setInProductionVersion(outputZC.getString("VERID"));
        cdProductInProduction.setInProductionNum(outputZC.getBigDecimal("MENGE_Z"));
        cdProductInProduction.setUnit(outputZC.getString("ERFME"));
        cdProductInProduction.setCreateBy("定时任务");
        cdProductInProduction.setCreateTime(new Date());
        cdProductInProduction.setUpdateBy("定时任务");
        cdProductInProduction.setUpdateTime(new Date());
        cdProductInProduction.setDelFlag(DeleteFlagConstants.NO_DELETED);
        return cdProductInProduction;
    }

    /**
     * 将SAP在途成品库存转换成 CdProductInProduction
     * @param outputZT 在途成品表
     * @return List<CdProductPassage> 在途成品库存集合
     */
    private List<CdProductPassage> getPassageList(JCoTable outputZT){
        logger.info("将SAP在途成品库存转换成 CdProductInProduction 插入数据库开始");
        List<CdProductPassage> productPassagesList = new ArrayList<>();
        //从输出table中获取每一行数据
        if (outputZT != null && outputZT.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputZT.getNumRows(); i++) {
                //设置指针位置
                outputZT.setRow(i);
                CdProductPassage cdProductPassage = changePassage(outputZT);
                productPassagesList.add(cdProductPassage);
            }
        }
        return productPassagesList;
    }

    /**
     * 将SAP在途成品库存转换成 CdProductPassage
     * @param outputZT 在途成品表
     * @return 在途成品库存
     */
    private CdProductPassage changePassage(JCoTable outputZT){
        CdProductPassage cdProductPassage = new CdProductPassage();
        cdProductPassage.setProductFactoryCode(outputZT.getString("WERKS"));
        cdProductPassage.setProductMaterialCode(outputZT.getString("MATNR"));
        cdProductPassage.setProductMaterialDesc(outputZT.getString("MAKTX"));
        cdProductPassage.setUnit(outputZT.getString("ERFME"));
        cdProductPassage.setStorehouseFrom(outputZT.getString("LGORT_F"));
        cdProductPassage.setStorehouseTo(outputZT.getString("LGORT_J"));
        cdProductPassage.setPassageNum(outputZT.getBigDecimal("MENGE"));
        cdProductPassage.setCreateBy("定时任务");
        cdProductPassage.setCreateTime(new Date());
        cdProductPassage.setUpdateBy("定时任务");
        cdProductPassage.setUpdateTime(new Date());
        cdProductPassage.setDelFlag(DeleteFlagConstants.NO_DELETED);
        return cdProductPassage;
    }

    /**
     * 将SAP在库成品库存转换成 CdProductInProduction
     * @param outputZK 在库成品库存表
     * @return List<CdProductWarehouse> 在库成品库存集合
     */
    private List<CdProductWarehouse> getWarehouseList(JCoTable outputZK,Map<String,List<String>> storehouseMap){
        logger.info("将SAP在库成品库存转换成 CdProductWarehouse 插入数据库开始");
        List<CdProductWarehouse> productWarehousesList = new ArrayList<>();

        //从输出table中获取每一行数据
        if (outputZK != null && outputZK.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputZK.getNumRows(); i++) {
                //设置指针位置
                outputZK.setRow(i);
                CdProductWarehouse cdProductWarehouse = changeWarehouse(outputZK,storehouseMap);
                productWarehousesList.add(cdProductWarehouse);
            }
        }
        return productWarehousesList;
    }

    /**
     * 将SAP在库成品库存转换成 CdProductWarehouse
     * @param outputZK 在库成品库存表
     * @return CdProductWarehouse 在库成品库存转
     */
    private CdProductWarehouse changeWarehouse(JCoTable outputZK,Map<String,List<String>> storehouseMap){
        CdProductWarehouse cdProductWarehouse = new CdProductWarehouse();
        cdProductWarehouse.setProductFactoryCode(outputZK.getString("WERKS"));
        cdProductWarehouse.setProductMaterialCode(outputZK.getString("MATNR"));
        cdProductWarehouse.setProductMaterialDesc(outputZK.getString("MAKTX"));
        cdProductWarehouse.setStorehouse(outputZK.getString("LGORT"));
        cdProductWarehouse.setWarehouseNum(outputZK.getBigDecimal("LABST"));
        cdProductWarehouse.setUnit(outputZK.getString("ERFME"));
        cdProductWarehouse.setCreateBy("定时任务");
        cdProductWarehouse.setCreateTime(new Date());
        cdProductWarehouse.setUpdateBy("定时任务");
        cdProductWarehouse.setUpdateTime(new Date());
        cdProductWarehouse.setDelFlag(DeleteFlagConstants.NO_DELETED);
        //根据工厂在字典表里获取不良成品 库位
        //根据工厂查所对应的不良货位,如果库存地点是不良货位则设置类型为1
        List<String> storehouseList = storehouseMap.get(cdProductWarehouse.getProductFactoryCode());
        Boolean flagStockType = storehouseList.contains(outputZK.getString("LGORT"));
        if(flagStockType){
            cdProductWarehouse.setStockType(NO_STOCK_TYPE);
        }else{
            cdProductWarehouse.setStockType(STOCK_TYPE);
        }
        return cdProductWarehouse;
    }

    /**
     * 将SAP寄售不足成品库存转换成 CdProductStock 库存主表
     * @param outputJS  表
     * @return
     */
    private List<CdProductStock> getJSStock(JCoTable outputJS){
        logger.info("将SAP寄售不足成品库存转换成 CdProductStock 库存主表");
        List<CdProductStock> productStockList = new ArrayList<>();
        //从输出table中获取每一行数据
        if (outputJS != null && outputJS.getNumRows() > 0) {
            //循环取table行数据
            for (int i = 0; i < outputJS.getNumRows(); i++) {
                //设置指针位置
                outputJS.setRow(i);
                CdProductStock cdProductStock = changeJSStock(outputJS);
                productStockList.add(cdProductStock);
            }
        }
        return productStockList;
    }

    /**
     * 将SAP寄售不足成品库存转换成 CdProductStock 库存主表
     * @param outputJS  表
     * @return CdProductStock 库存主表
     */
    private CdProductStock changeJSStock(JCoTable outputJS){
        CdProductStock cdProductStock = new CdProductStock();
        cdProductStock.setProductFactoryCode(outputJS.getString("WERKS"));
        cdProductStock.setProductMaterialCode(outputJS.getString("MATNR"));
        cdProductStock.setProductMaterialDesc(outputJS.getString("MAKTX"));
        cdProductStock.setStockKNum(outputJS.getBigDecimal("CY"));
        cdProductStock.setUnit(outputJS.getString("ERFME"));
        cdProductStock.setCreateBy("定时任务");
        cdProductStock.setCreateTime(new Date());
        cdProductStock.setUpdateBy("定时任务");
        cdProductStock.setUpdateTime(new Date());
        cdProductStock.setDelFlag(DeleteFlagConstants.NO_DELETED);
        return cdProductStock;
    }
}
