package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrderAnalysis;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.domain.entity.vo.OmsProductionOrderAnalysisVo;
import com.cloud.order.domain.entity.vo.OmsRealOrderVo;
import com.cloud.order.mapper.OmsProductionOrderAnalysisMapper;
import com.cloud.order.service.IOmsProductionOrderAnalysisService;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.feign.RemoteCdProductPassageService;
import com.cloud.system.feign.RemoteCdProductStockService;
import com.cloud.system.feign.RemoteCdProductWarehouseService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 待排产订单分析 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-15
 */
@Service
@Slf4j
public class OmsProductionOrderAnalysisServiceImpl extends BaseServiceImpl<OmsProductionOrderAnalysis> implements IOmsProductionOrderAnalysisService {
    private static final String STOREHOUSE = "STOREHOUSE";
    private static final String STOREHOUSE_NUM = "STOREHOUSE_NUM";
    private static final String PASSAGE_NUM = "PASSAGE_NUM";
    private static final int DAYS = 14;

    private static final String DATA_SOURCE_INTERFACE = "0";
    @Autowired
    private OmsProductionOrderAnalysisMapper omsProductionOrderAnalysisMapper;
    @Autowired
    private IOmsRealOrderService omsRealOrderService;
    @Autowired
    private RemoteCdProductWarehouseService remoteCdProductWarehouseService;
    @Autowired
    private RemoteCdProductPassageService remoteCdProductPassageService;
    @Autowired
    private RemoteCdProductStockService remoteCdProductStockService;

    /**
     * Description: 待排产订单分析保存
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/17
     */
    @Override
    @GlobalTransactional
    public R saveAnalysisGather() {
        //1、获取14天需求数据
        Example example = new Example(OmsRealOrder.class);
        Example.Criteria criteria = example.createCriteria();
        SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.add(Calendar.DATE, DAYS);
        //应韩宁韩工要求，待排产订单分析从当天开始  2020-09-03  ltq
        criteria.andGreaterThanOrEqualTo("productDate", sft.format(new Date()));
        criteria.andLessThan("productDate", sft.format(date.getTime()));
        List<OmsRealOrder> omsRealOrders = omsRealOrderService.selectByExample(example);
        //增加数据空判断  2020-07-23 by ltq
        if (ObjectUtil.isEmpty(omsRealOrders) || omsRealOrders.size() <= 0) {
            log.error("待排产订单分析汇总查询真单记录为空！");
            return R.ok();
        }
        //提取修改生产日期但没有填写备注的真单数据 2020-07-23 by ltq
        List<OmsRealOrder> omsRealOrderList = omsRealOrders.stream()
                .filter(o ->"1".equals(o.getStatus()) && StrUtil.isBlank(o.getRemark())).collect(Collectors.toList());
        //过滤掉修改生产日期但没有填写备注的真单数据，该类数据不允许后续流程 2020-07-23 by ltq
        //去除内单接口接入的PO数据  2020-09-14  ltq
        List<OmsRealOrder> realOrders = omsRealOrders.stream()
                .filter(o -> !omsRealOrderList.contains(o)
                        && !o.getDataSource().equals(DATA_SOURCE_INTERFACE)).collect(Collectors.toList());
        R r = analysisGather(realOrders);
        if (!r.isSuccess()) {
            log.error("待排产订单分析汇总失败，原因：" + r.get("msg"));
            return R.error("待排产订单分析汇总失败，原因：" + r.get("msg"));
        }
        List<OmsProductionOrderAnalysis> list = r.getCollectData(
                new TypeReference<List<OmsProductionOrderAnalysis>>() {
                });
        omsProductionOrderAnalysisMapper.deleteAll();
        if (ObjectUtil.isNotEmpty(list) && list.size() > 0) {
            //新增
            omsProductionOrderAnalysisMapper.insertList(list);
        }
        return R.ok();
    }

    /**
     * Description:  待排产订单分析汇总
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/15
     */
    private R analysisGather(List<OmsRealOrder> omsRealOrders) {
        log.info("========待排产订单分析汇总 开始 =======");
        log.info("========汇总原始数据个数：" + omsRealOrders.size() + "========");
        Map<String, List<OmsRealOrder>> map = new HashMap<>();
        //1、按生产工厂、原材料物料分组
        log.info("========根据生产工厂、成品专用号分组========");
        omsRealOrders.forEach(res -> {
            String mapKey = StrUtil.concat(true, res.getProductFactoryCode(), res.getProductMaterialCode());
            List<OmsRealOrder> list = map.getOrDefault(mapKey, new ArrayList<>());
            list.add(res);
            map.put(mapKey, list);
        });
        //2、获取生产工厂、成品专用号的可用总库存
        R productStock = queryProductStock(omsRealOrders);
        if (!productStock.isSuccess()) {
            log.error("获取生产工厂、成品专用号可用库存失败:"+productStock.get("msg"));
        }
        R customerStock = queryCustomerStock(omsRealOrders);
        if (!customerStock.isSuccess()) {
            log.error("获取客户可用库存失败:"+customerStock.get("msg"));
        }
        Map<String, BigDecimal> stockMap = productStock.getCollectData(new TypeReference<Map<String, BigDecimal>>() {
        });
        Map<String, BigDecimal> customerStockMap = customerStock.getCollectData(new TypeReference<Map<String, BigDecimal>>() {
        });
        List<OmsProductionOrderAnalysis> analysisArrayList = new ArrayList<>();
        for (String key : map.keySet()) {
            List<OmsRealOrder> omsRealOrderList = map.get(key);
            //生产工厂、成品专用号的可用总库存
            BigDecimal stockNumSum = BigDecimal.ZERO;
            if (stockMap != null) {
                stockNumSum = stockMap.get(key);
            }
            log.info("========生产工厂、成品专用号" + key + "的可用总库存量为:" + stockNumSum + "==========");
            //判断可用总库存
            stockNumSum = stockNumSum == null ? BigDecimal.ZERO : stockNumSum;
            // 结余量
            BigDecimal allowance = stockNumSum;
            //3、将生产共产个、成品专用号下的数据按照生产日期进行分组
            Map<String, List<OmsRealOrder>> mapDay = omsRealOrderList.stream().collect(Collectors.groupingBy(OmsRealOrder::getProductDate));
            //4、按照key值升序
            mapDay = mapDay.entrySet().stream().
                    sorted(Map.Entry.comparingByKey()).
                    collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldVal, newVal) -> oldVal,
                            LinkedHashMap::new));
            Map<String, List<OmsRealOrder>> customerMap = new HashMap<>();
            for (String dayKey : mapDay.keySet()) {
                List<OmsRealOrder> mapDayList = mapDay.get(dayKey);
                OmsRealOrder realOrder = mapDayList.get(0);
                //需求总量
                BigDecimal demandTotalNum = mapDayList.stream().map(OmsRealOrder::getOrderNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                //客户缺口量
                BigDecimal customerGapNum = BigDecimal.ZERO;
                //存在缺口量的客户数
                long customerGaps = 0L;
                //客户总数
                long customerTotal = mapDayList.size();

                for (OmsRealOrder omsRealOrder : mapDayList) {
                    String customerKey = omsRealOrder.getCustomerCode();
                    List<OmsRealOrder> list = customerMap.get(customerKey);
                    //累计客户需求量
                    BigDecimal customerOrderNums = BigDecimal.ZERO;
                    if (ObjectUtil.isNotEmpty(list)) {
                        customerOrderNums = list.stream().map(OmsRealOrder::getOrderNum).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    //获取客户库存 = 库位库存 + 在途
                    String customerStockKey = StrUtil.concat(true, omsRealOrder.getProductFactoryCode(),
                            omsRealOrder.getProductMaterialCode(), omsRealOrder.getPlace());
                    BigDecimal customerStockNum = BigDecimal.ZERO;
                    if (customerStockMap != null) {
                        customerStockNum = customerStockMap.get(customerStockKey);
                    }
                    //判断客户库存量
                    customerStockNum = customerStockNum == null ? BigDecimal.ZERO : customerStockNum;
                    //定义成品客户可用库存库存
                    BigDecimal stockNum = customerStockNum.subtract(customerOrderNums);
                    if (stockNum.compareTo(omsRealOrder.getOrderNum()) < 0) {
                        //客户库位库存小于客户需求量
                        //缺口量客户数+1
                        customerGaps++;
                        //客户缺口量累计
                        customerGapNum = customerGapNum.add(stockNum.subtract(omsRealOrder.getOrderNum()));
                    }
                    List<OmsRealOrder> customerList = customerMap.getOrDefault(customerKey, new ArrayList<>());
                    customerList.add(omsRealOrder);
                    customerMap.put(customerKey, customerList);
                }
                //计算结余量
                allowance = allowance.subtract(demandTotalNum);
                OmsProductionOrderAnalysis omsProductionOrderAnalysis = OmsProductionOrderAnalysis.builder().
                        productFactoryCode(realOrder.getProductFactoryCode()).
                        orderFrom(realOrder.getOrderFrom()).
                        productFactoryDesc(realOrder.getProductFactoryDesc()).
                        productMaterialCode(realOrder.getProductMaterialCode()).
                        productMaterialDesc(realOrder.getProductMaterialDesc()).
                        demandOrderNum(demandTotalNum).
                        customerBreachNum(customerGapNum).
                        gapCustomer(customerGaps).
                        totalCustomer(customerTotal).
                        surplusNum(allowance).
                        unit(realOrder.getUnit()).
                        productDate(realOrder.getProductDate()).
                        delFlag("0").stockNum(stockNumSum).build();
                omsProductionOrderAnalysis.setCreateBy("定时任务");
                omsProductionOrderAnalysis.setCreateTime(new Date());
                analysisArrayList.add(omsProductionOrderAnalysis);

            }
        }
        log.info("========待排产订单分析汇总 结束 =======");
        return R.data(analysisArrayList);
    }

    /**
     * Description:  查询客户缺口量明细
     * Param: [omsRealOrder]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    @Override
    public R queryRealOrder(OmsRealOrder omsRealOrder) {
        //获取真单数据
        Example example = new Example(OmsRealOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(omsRealOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsRealOrder.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsRealOrder.getProductMaterialCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getProductDate())) {
            criteria.andLessThanOrEqualTo("productDate", omsRealOrder.getProductDate());
        }
        SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
        criteria.andGreaterThanOrEqualTo("productDate", sft.format(new Date()));
        criteria.andEqualTo("dataSource", "1");
        List<OmsRealOrder> omsRealOrders = omsRealOrderService.selectByExample(example);
        //提取修改生产日期但没有填写备注的真单数据 2020-07-23 by ltq
        List<OmsRealOrder> omsRealOrderNoRemark = omsRealOrders.stream()
                .filter(o ->"1".equals(o.getStatus()) && StrUtil.isBlank(o.getRemark())).collect(Collectors.toList());
        //过滤掉修改生产日期但没有填写备注的真单数据，该类数据不允许后续流程 2020-07-23 by ltq
        List<OmsRealOrder> realOrders = omsRealOrders.stream()
                .filter(o -> !omsRealOrderNoRemark.contains(o)).collect(Collectors.toList());
        Map<String, List<OmsRealOrder>> mapFactory = new HashMap<>();
        realOrders.forEach(ros -> {
            String key = StrUtil.concat(true, ros.getProductFactoryCode(), ros.getProductMaterialCode(), ros.getCustomerCode());
            List<OmsRealOrder> omsRealOrderList = mapFactory.getOrDefault(key, new ArrayList<>());
            omsRealOrderList.add(ros);
            mapFactory.put(key, omsRealOrderList);
        });
        List<OmsRealOrderVo> omsRealOrderVos = new ArrayList<>();
        mapFactory.forEach((k, v) -> {
            OmsRealOrder realOrder = v.get(0);
            OmsRealOrderVo omsRealOrderVo = OmsRealOrderVo.builder().productFactoryCode(realOrder.getProductFactoryCode())
                    .productFactoryDesc(realOrder.getProductFactoryDesc()).productMaterialCode(realOrder.getProductMaterialCode())
                    .productMaterialDesc(realOrder.getProductMaterialDesc()).customerCode(realOrder.getCustomerCode())
                    .customerDesc(realOrder.getCustomerDesc()).unit(realOrder.getUnit()).build();
            //到生产工厂、成品物料、客户的累计需求量
            BigDecimal totalOrderNum = v.stream().map(OmsRealOrder::getOrderNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            //获取到客户的在库和在途
            R r = getCustomerStockNum(realOrder);
            if (!r.isSuccess()) {
                log.error("获取到客户的库存（在途+在库）失败，原因：" + r.get("msg"));
                throw new BusinessException("获取到客户的库存（在途+在库）失败，原因：" + r.get("msg"));
            }
            Map<String, String> map = r.getCollectData(new TypeReference<Map<String, String>>() {
            });
            BigDecimal storehouseNum = new BigDecimal(map.get(STOREHOUSE_NUM));
            BigDecimal passageNum = new BigDecimal(map.get(PASSAGE_NUM));
            omsRealOrderVo.setStorehouse(map.get(STOREHOUSE));
            omsRealOrderVo.setStorehouseNum(storehouseNum);
            omsRealOrderVo.setPassageNum(passageNum);
            omsRealOrderVo.setTotalOrderNum(totalOrderNum);
            omsRealOrderVo.setBreachNum(storehouseNum.add(passageNum).subtract(totalOrderNum));
            v.forEach(rel -> {
                if (rel.getProductDate().equals(omsRealOrder.getProductDate())) {
                    omsRealOrderVo.setOrderNum(rel.getOrderNum());
                    omsRealOrderVos.add(omsRealOrderVo);
                }
            });
        });
        return R.data(omsRealOrderVos);
    }
    /**
     * Description:  分页查询
     * Param: [omsProductionOrderAnalysis]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderAnalysisVo>
     * Author: ltq
     * Date: 2020/6/18
     */
    @Override
    public List<OmsProductionOrderAnalysisVo> selectListPage(OmsProductionOrderAnalysis omsProductionOrderAnalysis) {
        List<OmsProductionOrderAnalysisVo> list = omsProductionOrderAnalysisMapper.selectListByGroup(omsProductionOrderAnalysis);
        if (ObjectUtil.isNotEmpty(list) && list.size() > 0){
            List<Dict> dictList = list.stream()
                    .map(omsProductionOrderAnalysisVo -> new Dict()
                            .set("productFactoryCode",omsProductionOrderAnalysisVo.getProductFactoryCode())
                            .set("productMaterialCode",omsProductionOrderAnalysisVo.getProductMaterialCode()))
                    .collect(Collectors.toList());
            List<OmsProductionOrderAnalysis> omsProductionOrderAnalyses =
                    omsProductionOrderAnalysisMapper.selectListByFactoryAndMaterial(dictList);
            Map<String,List<OmsProductionOrderAnalysis>> map = omsProductionOrderAnalyses
                    .stream().collect(Collectors.groupingBy((o) -> fetchGroupKey(o)));
            String[] days = getDays();
            List<String> dayList = Arrays.asList(days);
            list.forEach(item -> {
                String key = item.getProductFactoryCode()+item.getProductMaterialCode();
                List<OmsProductionOrderAnalysis> omsProductionOrderAnalyses1 = map.get(key);
                List<String> detailDays = omsProductionOrderAnalyses1.stream().map(OmsProductionOrderAnalysis::getProductDate).collect(Collectors.toList());
                List<String> addDays = dayList.stream().filter(day -> !detailDays.contains(day)).collect(Collectors.toList());
                OmsProductionOrderAnalysis orderAnalysis = omsProductionOrderAnalyses1.get(0);
                addDays.forEach(day ->
                    omsProductionOrderAnalyses1.add(OmsProductionOrderAnalysis.builder()
                            .orderFrom(orderAnalysis.getOrderFrom())
                            .productFactoryCode(orderAnalysis.getProductFactoryCode())
                            .productDate(day)
                            .productMaterialCode(orderAnalysis.getProductMaterialCode())
                            .productMaterialDesc(orderAnalysis.getProductMaterialDesc())
                            .stockNum(orderAnalysis.getStockNum())
                            .customerBreachNum(BigDecimal.ZERO)
                            .demandOrderNum(BigDecimal.ZERO)
                            .gapCustomer(0L)
                            .totalCustomer(0L)
                            .surplusNum(null)
                            .build())
                );
                //按照生产日期升序排序
                omsProductionOrderAnalyses1.sort(Comparator.comparing(OmsProductionOrderAnalysis::getProductDate));
                BigDecimal surplusNum = null;
                for (OmsProductionOrderAnalysis analysis : omsProductionOrderAnalyses1) {
                    if (analysis.getSurplusNum() == null && surplusNum == null) {
                        analysis.setSurplusNum(analysis.getStockNum());
                    } else if (analysis.getSurplusNum() == null && surplusNum != null){
                        analysis.setSurplusNum(surplusNum);
                    }
                    surplusNum = analysis.getSurplusNum();
                }
                item.setStockNum(orderAnalysis.getStockNum());
                item.setDataList(omsProductionOrderAnalyses1);
            });
        }
        return list;
    }

    /**
     * Description:  获取14天日期数组
     * Param: []
     * return: java.lang.String[]
     * Author: ltq
     * Date: 2020/6/29
     */
    private String[] getDays() {
        //应韩宁韩工要求，待排产订单分析从当天开始  2020-09-03  ltq
        int[] days = NumberUtil.range(0, 13);
        String[] dates = new String[14];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i : days) {
            String day = dateTimeFormatter.format(LocalDate.now().plusDays(i));
            dates[i] = day;
        }
        return dates;
    }
    /**
     * Description:  查询成品库存信息
     * Param: [cdProductStock]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/30
     */
    @Override
    public R getProductStock(CdProductStock cdProductStock) {
        return remoteCdProductStockService.findOneByExample(cdProductStock);
    }
    /**
     * Description:  查询需求量明细
     * Param: [omsRealOrder]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/7/27
     */
    @Override
    public R getDemandList(OmsRealOrder omsRealOrder) {
        //获取真单数据
        Example example = new Example(OmsRealOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(omsRealOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsRealOrder.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsRealOrder.getProductMaterialCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getProductDate())) {
            criteria.andEqualTo("productDate", omsRealOrder.getProductDate());
        }
        criteria.andEqualTo("dataSource", "1");
        List<OmsRealOrder> omsRealOrderList = omsRealOrderService.selectByExample(example);
        return R.data(omsRealOrderList);
    }

    /**
     * Description:  组织key值
     * Param: [omsProductionOrderAnalysis]
     * return: java.lang.String
     * Author: ltq
     * Date: 2020/6/18
     */
    private String fetchGroupKey(OmsProductionOrderAnalysis omsProductionOrderAnalysis){
        return omsProductionOrderAnalysis.getProductFactoryCode()+omsProductionOrderAnalysis.getProductMaterialCode();
    }

    /**
     * Description:  获取到客户的库存（在途+在库）
     * Param: [omsRealOrder]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/16
     */
    private R getCustomerStockNum(OmsRealOrder omsRealOrder) {
        // 根据库位获取库存信息
        CdProductWarehouse cdProductWarehouse = CdProductWarehouse.builder().
                productFactoryCode(omsRealOrder.getProductFactoryCode()).
                productMaterialCode(omsRealOrder.getProductMaterialCode())
                .storehouse(omsRealOrder.getPlace()).build();
        R storeResult = remoteCdProductWarehouseService.queryOneByExample(cdProductWarehouse);
        if (!storeResult.isSuccess()) {
            log.info("IOmsProductionOrderAnalysisService.getCustomerStockNum方法，获取客户库位库存失败!");
        }
        CdProductWarehouse productWarehouse = storeResult.getData(CdProductWarehouse.class);
        //在库量
        BigDecimal warehouseNum = (BeanUtil.isEmpty(productWarehouse) ||
                productWarehouse.getWarehouseNum() == null) ? BigDecimal.ZERO : productWarehouse.getWarehouseNum();
        //获取客户成品在途
        CdProductPassage cdProductPassage = CdProductPassage.builder().
                productFactoryCode(omsRealOrder.getProductFactoryCode()).
                productMaterialCode(omsRealOrder.getProductMaterialCode()).
                storehouseTo(omsRealOrder.getPlace()).build();
        R passageResult = remoteCdProductPassageService.queryOneByExample(cdProductPassage);
        if (!passageResult.isSuccess()) {
            log.info("IOmsProductionOrderAnalysisService.getCustomerStockNum方法，获取客户在途库存失败!");
        }
        List<CdProductPassage> productPassages = new ArrayList<>();
        if (passageResult.get("data") != null) {
            productPassages = passageResult.getCollectData(new TypeReference<List<CdProductPassage>>() {
            });
        }
        //客户在途总量
        BigDecimal passageNumTotal = BigDecimal.ZERO;
        if (productPassages.size() > 0) {
            passageNumTotal = productPassages.stream().map(CdProductPassage::getPassageNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        Map<String, String> map = new HashMap<>(3);
        map.put(STOREHOUSE, cdProductWarehouse.getStorehouse());
        map.put(STOREHOUSE_NUM, warehouseNum.toString());
        map.put(PASSAGE_NUM, passageNumTotal.toString());
        return R.data(map);
    }

    /**
     * Description:  查询生产工厂、成品专用号的库存
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/17
     */

    private R queryProductStock(List<OmsRealOrder> list) {
        //按照生产工厂、成品专用号去重
        list = list.stream()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing(omsRealOrder ->
                                                omsRealOrder.getProductFactoryCode() +
                                                        omsRealOrder.getProductMaterialCode()))),
                                ArrayList::new
                        )
                );
        List<CdProductStock> productStocks = new ArrayList<>();
        list.forEach(item -> {
            productStocks.add(CdProductStock.builder().
                    productFactoryCode(item.getProductFactoryCode()).
                    productMaterialCode(item.getProductMaterialCode()).
                    build());
        });
        //调用system查询库存信息
        R r = remoteCdProductStockService.queryOneByFactoryAndMaterial(productStocks);
        if (!r.isSuccess()) {
            log.error("IOmsProductionOrderAnalysisService.getFactoryStockNum方法， 获取到生产工厂、成品物料号的可用库存失败：" + r.get("msg"));
        }
        List<CdProductStock> cdProductStock = new ArrayList<>();
        if (r.get("data") != null) {
            cdProductStock = r.getCollectData(new TypeReference<List<CdProductStock>>() {
            });
        }
        //计算生产工厂、成品专用号的可用总库存
        log.info("========计算生产工厂、成品专用号的可用总库存==========");
        Map<String, BigDecimal> stockMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(cdProductStock) || cdProductStock.size() <= 0) {
            cdProductStock.forEach(item -> {
                String key = StrUtil.concat(true, item.getProductFactoryCode(), item.getProductMaterialCode());
                BigDecimal stockNumSum = item.getSumNum();
                stockMap.put(key, stockNumSum);
            });
        }
        return R.data(stockMap);
    }

    private R queryCustomerStock(List<OmsRealOrder> list) {
        //按照生产工厂、成品专用号、客户编码去重
        list = list.stream()
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(() ->
                                        new TreeSet<>(Comparator.comparing(omsRealOrder ->
                                                omsRealOrder.getProductFactoryCode() +
                                                        omsRealOrder.getProductMaterialCode() +
                                                        omsRealOrder.getCustomerCode()))),
                                ArrayList::new
                        )
                );
        List<CdProductWarehouse> productWarehouses = new ArrayList<>();
        List<CdProductPassage> productPassages = new ArrayList<>();
        list.forEach(item -> {
            productWarehouses.add(CdProductWarehouse.builder().
                    productFactoryCode(item.getProductFactoryCode()).
                    productMaterialCode(item.getProductMaterialCode()).
                    storehouse(item.getPlace()).
                    build());
        });
        list.forEach(item -> {
            productPassages.add(CdProductPassage.builder().
                    productFactoryCode(item.getProductFactoryCode()).
                    productMaterialCode(item.getProductMaterialCode()).
                    storehouseTo(item.getPlace()).
                    build());
        });
        R storeResult = remoteCdProductWarehouseService.queryByList(productWarehouses);
        if (!storeResult.isSuccess()) {
            log.info("IOmsProductionOrderAnalysisService.queryCustomerStock，获取客户库位库存失败!");
        }
        List<CdProductWarehouse> productWarehouseList = new ArrayList<>();
        if (storeResult.get("data") != null) {
            productWarehouseList = storeResult.getCollectData(new TypeReference<List<CdProductWarehouse>>() {
            });
        }
        Map<String, BigDecimal> productWarehouseMap = productWarehouseList.stream().
                collect(Collectors.toMap(k -> k.getProductFactoryCode() +
                        k.getProductMaterialCode() + k.getStorehouse(), CdProductWarehouse::getWarehouseNum));
        R passageResult = remoteCdProductPassageService.queryByList(productPassages);
        if (!passageResult.isSuccess()) {
            log.info("IOmsProductionOrderAnalysisService.queryCustomerStock，获取客户在途库存失败!");
        }
        List<CdProductPassage> productPassageList = new ArrayList<>();
        if (passageResult.get("data") != null ){
            productPassageList = passageResult.getCollectData(new TypeReference<List<CdProductPassage>>() {
            });
        }
//        Map<String, BigDecimal> productPassageMap = productPassageList.stream().
//                collect(Collectors.toMap(k -> k.getProductFactoryCode() +
//                        k.getProductMaterialCode() + k.getStorehouseTo(), CdProductPassage::getPassageNum,(key1,key2)->key2));
        Map<Object, BigDecimal> productPassageMap = productPassageList.stream().
                collect(Collectors.groupingBy(e -> fetchGroupKeyArtificial(e),Collectors.reducing(BigDecimal.ZERO,CdProductPassage::getPassageNum,BigDecimal::add)));

        productWarehouseMap.forEach((key, value) -> productPassageMap.merge(key, value, BigDecimal::add));
        return R.data(productPassageMap);
    }


    /**
     *  手工导入组合key
     * @param cd
     * @return
     */
    private static String fetchGroupKeyArtificial(CdProductPassage cd){
        return StrUtil.concat(true,
                cd.getProductFactoryCode(),cd.getProductMaterialCode(),cd.getStorehouseTo());
    }
}
