package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.RandomUtil;
import com.cloud.order.domain.entity.*;
import com.cloud.order.mapper.Oms2weeksDemandOrderMapper;
import com.cloud.order.mapper.OmsDemandOrderGatherMapper;
import com.cloud.order.service.*;
import com.cloud.system.feign.RemoteFactoryStorehouseInfoService;
import com.cloud.system.feign.RemoteSequeceService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 滚动计划需求 Service业务层处理
 *
 * @author cs
 * @date 2020-06-12
 */
@Slf4j
@Service
public class OmsDemandOrderGatherServiceImpl extends BaseServiceImpl<OmsDemandOrderGather> implements IOmsDemandOrderGatherService {
    @Autowired
    private OmsDemandOrderGatherMapper omsDemandOrderGatherMapper;
    @Autowired
    private IOmsDemandOrderGatherHisService omsDemandOrderGatherHisService;
    @Autowired
    private IOmsInternalOrderResService omsInternalOrderResService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private RemoteFactoryStorehouseInfoService remoteFactoryStorehouseInfoService;
    @Autowired
    private IOms2weeksDemandOrderService oms2weeksDemandOrderService;
    @Autowired
    private Oms2weeksDemandOrderMapper oms2weeksDemandOrderMapper;
    @Autowired
    private IOms2weeksDemandOrderHisService oms2weeksDemandOrderHisService;


    /**
     * 周五需求数据汇总
     * @return
     */
    @Override
    @Transactional(timeout = 60,rollbackFor=Exception.class)
    public R gatherDemandOrderFriday() {
        //将原来的汇总加到汇总历史
        List<OmsDemandOrderGather> omsDemandOrderGathers = omsDemandOrderGatherMapper.selectAll();
        if (omsDemandOrderGathers != null) {
            log.info("======================周五汇总：删除汇总表数据、插入历史数据  start============================");
            List<OmsDemandOrderGatherHis> listHis= omsDemandOrderGathers.stream().map(omsDemandOrderGather ->
                    BeanUtil.copyProperties(omsDemandOrderGather,OmsDemandOrderGatherHis.class)).collect(Collectors.toList());
            if (listHis != null &&listHis.size()>0) {
                //插入历史数据
                int insertHis=omsDemandOrderGatherHisService.insertList(listHis);
                if(insertHis>0){
                    //删除原来的汇总
                    omsDemandOrderGatherMapper.deleteAll();
                }
            }
            log.info("======================周五汇总：删除汇总表数据、插入历史数据  end============================");
        }
        //汇总
        log.info("==============周五汇总：汇总信息  start======================");
        R rGather=gatherDemandOrder(Week.FRIDAY.getValue());
        log.info(StrUtil.format("==============周五汇总：汇总信息结果{}  end======================",rGather.isSuccess()));
        if (!rGather.isSuccess()) {
            throw new BusinessException(rGather.getStr("msg")) ;
        }
        List<OmsDemandOrderGather> listGather = rGather.getCollectData(new TypeReference<List<OmsDemandOrderGather>>() {});
        //插入新汇总
        log.info("==============周五汇总：插入新汇总信息  start======================");
        if (listGather != null) {
            insertList(listGather);
        }
        log.info("==============周五汇总：插入新汇总信息  start======================");
        return R.ok();
    }

    /**
     * 周一需求数据汇总
     * @return
     */
    @Override
    @Transactional(timeout = 60,rollbackFor=Exception.class)
    public R gatherDemandOrderMonday() {
        //删除周五的汇总
        log.info("======================周一汇总：删除周五汇总  start=======================");
        omsDemandOrderGatherMapper.deleteAll();
        log.info("======================周一汇总：删除周五汇总  end=======================");
        //重新汇总
        log.info("======================周一汇总：开始13周需求汇总信息  start=======================");
        R rGather=gatherDemandOrder(Week.MONDAY.getValue());
        log.info("======================周一汇总：汇总13周需求信息结束  end=======================");
        if (!rGather.isSuccess()) {
            throw new BusinessException(rGather.getStr("msg")) ;
        }
        List<OmsDemandOrderGather> listGather = rGather.getCollectData(new TypeReference<List<OmsDemandOrderGather>>() {});
        //插入新汇总
        log.info("======================周一汇总：13周需求汇总插入开始  start=======================");
        if (listGather != null) {
            insertList(listGather);
        }
        log.info("======================周一汇总：13周需求汇总插入结束  end=======================");
        //将oms_2weeks_demand_order插入到oms_2weeks_demand_order_his
        List<Oms2weeksDemandOrder> oms2weeksDemandOrders = oms2weeksDemandOrderMapper.selectAll();
        if (oms2weeksDemandOrders != null) {
            log.info("======================周一汇总：原13周需求汇总插入历史  start=======================");
            List<Oms2weeksDemandOrderHis> listHis= oms2weeksDemandOrders.stream().map(oms2weeksDemandOrder ->
                    BeanUtil.copyProperties(oms2weeksDemandOrder,Oms2weeksDemandOrderHis.class)).collect(Collectors.toList());
            if (listHis != null &&listHis.size()>0) {
                int insertHis=oms2weeksDemandOrderHisService.insertList(listHis);
                if(insertHis>0){
                    //删除oms_2weeks_demand_order
                    oms2weeksDemandOrderMapper.deleteAll();
                }
            }
            log.info("======================周一汇总：原13周需求汇总插入历史  end=======================");
        }
        //按天汇总oms_2weeks_demand_order
        log.info("======================周一汇总：2周需求汇总开始  start=======================");
        R r = gather2WeeksDemandOrder();
        log.info("======================周一汇总：2周需求汇总结束  end=======================");
        if (!r.isSuccess()) {
            throw new BusinessException(r.getStr("msg")) ;
        }
        List<Oms2weeksDemandOrder> list = r.getCollectData(new TypeReference<List<Oms2weeksDemandOrder>>() {});
        //插入新汇总
        if (list != null) {
            log.info("======================周一汇总：2周需求汇总插入  start=======================");
            oms2weeksDemandOrderService.insertList(list);
            log.info("======================周一汇总：2周需求汇总插入  end=======================");
        }
        return R.ok();
    }

    /**
     * 按天汇总oms_2weeks_demand_order
     * @return
     */
    R gather2WeeksDemandOrder(){
        ///查询T+2，T+3数据
        Date date = DateUtil.date();
        //因为执行时间为周一，所以T+2周为这个周天开始
        String startTime = DateUtil.formatDate(DateUtil.endOfWeek(date));
        //结束时间为开始时间+13天
        String endTime = DateUtil.formatDate(DateUtil.dateNew(DateUtil.parse(startTime)).offsetNew(DateField.DAY_OF_WEEK, 13));
        Example example = new Example(OmsInternalOrderRes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("marker", "PR");
        criteria.andGreaterThanOrEqualTo("deliveryDate",startTime);
        criteria.andLessThanOrEqualTo("deliveryDate",endTime);
        Map<String, List<OmsInternalOrderRes>> mapRes=new ConcurrentHashMap<>();
        List<OmsInternalOrderRes> listRes = omsInternalOrderResService.selectByExample(example);
        listRes.forEach(res->{
            //key:生产工厂、客户编码、成品物料号、交付日期
            String key = StrUtil.concat(true,res.getProductFactoryCode(),
                    res.getCustomerCode(),res.getProductMaterialCode(),res.getDeliveryDate());
            List<OmsInternalOrderRes> resList = mapRes.getOrDefault(key, new ArrayList<>());
            resList.add(res);
            mapRes.put(key, resList);
        });
        //获取不重复的物料号和工厂Map
        List<Dict> maps = listRes.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("customerCode",s.getCustomerCode())).distinct().collect(Collectors.toList());
        //获取库位
        R rStoreHouse = remoteFactoryStorehouseInfoService.selectStorehouseToMap(maps);
        if(!rStoreHouse.isSuccess()){
            return R.error("获取接收库位失败！");
        }
        Map<String, Map<String, String>> storehouseMap = rStoreHouse.getCollectData(new TypeReference<Map<String, Map<String, String>>>() {});
        if (MapUtil.isEmpty(storehouseMap)) {
            return R.error("获取接收库位失败！");
        }
        List<Oms2weeksDemandOrder> oms2weeksDemandOrders = new CollUtil().newArrayList();
        mapRes.forEach((keyCode, resList)->{
            //按天汇总订单数量
            BigDecimal orderNumTotal = resList.stream().map(OmsInternalOrderRes::getOrderNum).reduce(BigDecimal.ZERO,BigDecimal::add);
            OmsInternalOrderRes res = resList.get(0);
            Date deliveryDate = DateUtil.parse(res.getDeliveryDate());
            String deliveryYear = StrUtil.toString(DateUtil.year(deliveryDate));//交付日期年
            int weekNum  = DateUtil.weekOfYear(deliveryDate);
            //判断今天是不是周天，如果是周天，则周数+1
            if (DateUtil.dayOfWeek(deliveryDate)==1) {
                weekNum = weekNum+1;
            }
            Map<String, String> storeHouseMap = storehouseMap.get(StrUtil.concat(true, res.getProductFactoryCode(), res.getCustomerCode()));
            String storeHouse = new String();
            if (storeHouseMap != null) {
                storeHouse=storeHouseMap.get("storehouseTo");
            }
            Oms2weeksDemandOrder oms2weeksDemandOrder = new Oms2weeksDemandOrder().builder()
                    .orderType("LA").orderFrom("1")
                    .productMaterialCode(res.getProductMaterialCode()).productMaterialDesc(res.getProductMaterialDesc())
                    .productFactoryCode(res.getProductFactoryCode()).productFactoryDesc(res.getProductFactoryDesc())
                    .customerCode(res.getCustomerCode()).customerDesc(res.getCustomerDesc())
                    .mrpRange(res.getMrpRange()).bomVersion(res.getVersion()).purchaseGroupCode(res.getPurchaseGroupCode())
                    .place(storeHouse).deliveryDate(deliveryDate).year(deliveryYear).unit(res.getUnit())
                    .weeks(Integer.toString(weekNum)).orderNum(orderNumTotal.longValue()).version(StrUtil.concat(true,DateUtils.dateTime(),Integer.toString(weekNum))).build();
            oms2weeksDemandOrder.setCreateBy("定时任务");
            oms2weeksDemandOrder.setCreateTime(date);
            oms2weeksDemandOrder.setDelFlag("0");
            oms2weeksDemandOrders.add(oms2weeksDemandOrder);
        });
        return R.data(oms2weeksDemandOrders);
    }

    /**
     * 汇总PR数据 返回汇总数据
     * @return
     */
    R gatherDemandOrder(int flag){
        //按照生产工厂、客户编码、成品物料号、交付日期字段，将到天的数据汇总到周，并转换交付日期至周数和年度，
        // 将汇总的周数据存到需求汇总表中（oms_demand_order_gather）
        Example example = new Example(OmsInternalOrderRes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("marker", "PR");
        //需求原始数据
        List<OmsInternalOrderRes> listRes = omsInternalOrderResService.selectByExample(example);
        Map<String, List<OmsInternalOrderRes>> mapRes=new ConcurrentHashMap<>();
        //生产工厂、客户编码、成品物料号、交付日期，周数分组
        listRes.forEach(res->{
            //key:生产工厂、客户编码、成品物料号、交付日期,周数
            int weekNum  = DateUtil.weekOfYear(DateUtil.parseDate(res.getDeliveryDate()));
            //判断今天是不是周天，如果是周天，则周数+1
            if (DateUtil.parseDate(res.getDeliveryDate()).dayOfWeek()==1) {
                weekNum = weekNum+1;
            }
            String key = StrUtil.concat(true,res.getProductFactoryCode(),
                    res.getCustomerCode(),res.getProductMaterialCode(),res.getDeliveryDate(),StrUtil.COMMA,Integer.toString(weekNum));
            List<OmsInternalOrderRes> resList = mapRes.getOrDefault(key, new ArrayList<>());
            resList.add(res);
            mapRes.put(key, resList);
        });
        //获取不重复的物料号和工厂Map (获取库位用)
        List<Dict> maps = listRes.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("customerCode",s.getCustomerCode())).distinct().collect(Collectors.toList());
        //获取库位
        R rStoreHouse = remoteFactoryStorehouseInfoService.selectStorehouseToMap(maps);
        if(!rStoreHouse.isSuccess()){
            return R.error("获取接收库位失败！");
        }
        Map<String, Map<String, String>> storehouseMap = rStoreHouse.getCollectData(new TypeReference<Map<String, Map<String, String>>>() {});
        if (MapUtil.isEmpty(storehouseMap)) {
            return R.error("获取接收库位失败！");
        }
        //返回的汇总数据
        List<OmsDemandOrderGather> demandOrderGathers = new CollUtil().newArrayList();
        mapRes.forEach((keyCode, resList)->{
            //计算汇总订单数量
            BigDecimal orderNumTotal = resList.stream().map(OmsInternalOrderRes::getOrderNum).reduce(BigDecimal.ZERO,BigDecimal::add);
            OmsInternalOrderRes res = resList.get(0);
            //库位地点
            String storeHouse = new String();
            if (storehouseMap.get(StrUtil.concat(true, res.getProductFactoryCode(), res.getCustomerCode())) != null) {
                storeHouse = storehouseMap.get(StrUtil.concat(true, res.getProductFactoryCode(), res.getCustomerCode())).get("storehouseTo");
            }
            //交付日期
            Date deliveryDate = DateUtil.parse(res.getDeliveryDate());
            String deliveryYear = StrUtil.toString(DateUtil.year(deliveryDate));//交付日期年
            String deliveryWeek = StrUtil.split(keyCode, StrUtil.COMMA)[1];//交付日期周数
//            R seqresult = remoteSequeceService.selectSeq("demand_order_gather_seq", 4);
//            if(!seqresult.isSuccess()){
//                throw new BusinessException("查序列号失败");
//            }
//            String seq = seqresult.getStr("data");
            String seq = RandomUtil.randomInt(6);
            //需求汇总单号
            String demandOrderCode = StrUtil.concat(true, "DM", DateUtils.dateTime(), seq);
            Date date = DateUtil.date();
            int thisWeek = NumberUtil.compare(flag , Week.FRIDAY.getValue())>0 ? DateUtil.weekOfYear(date) : DateUtil.weekOfYear(date) - 1;
            OmsDemandOrderGather omsDemandOrderGather = new OmsDemandOrderGather().builder()
                    .demandOrderCode(demandOrderCode).orderType("LA").orderFrom("1")
                    .productMaterialCode(res.getProductMaterialCode()).productMaterialDesc(res.getProductMaterialDesc())
                    .productFactoryCode(res.getProductFactoryCode()).productFactoryDesc(res.getProductFactoryDesc())
                    .customerCode(res.getCustomerCode()).customerDesc(res.getCustomerDesc())
                    .mrpRange(res.getMrpRange()).bomVersion(res.getVersion()).purchaseGroupCode(res.getPurchaseGroupCode())
                    .place(storeHouse).deliveryDate(deliveryDate).year(deliveryYear).unit(res.getUnit())
                    .weeks(deliveryWeek).orderNum(orderNumTotal.longValue())
                    .version(StrUtil.concat(true,StrUtil.toString(DateUtil.year(date)),StrUtil.toString(thisWeek))).build();
            omsDemandOrderGather.setCreateBy("定时任务");
            omsDemandOrderGather.setCreateTime(DateUtil.date());
            omsDemandOrderGather.setDelFlag("0");
            demandOrderGathers.add(omsDemandOrderGather);
        });
        return R.data(demandOrderGathers);
    }
}
