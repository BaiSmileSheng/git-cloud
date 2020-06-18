package com.cloud.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.enums.InternalOrderResEnum;
import com.cloud.order.enums.RealOrderEnum;
import com.cloud.order.mapper.OmsRealOrderMapper;
import com.cloud.order.service.IOmsInternalOrderResService;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteFactoryStorehouseInfoService;
import com.cloud.system.feign.RemoteSequeceService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 真单Service业务层处理
 *
 * @author ltq
 * @date 2020-06-15
 */
@Service
public class OmsRealOrderServiceImpl extends BaseServiceImpl<OmsRealOrder> implements IOmsRealOrderService {

    private static Logger logger = LoggerFactory.getLogger(OmsRealOrderServiceImpl.class);
    @Autowired
    private OmsRealOrderMapper omsRealOrderMapper;

    @Autowired
    private RemoteSequeceService remoteSequeceService;

    @Autowired
    private IOmsInternalOrderResService omsInternalOrderResService;

    @Autowired
    private RemoteFactoryStorehouseInfoService remoteFactoryStorehouseInfoService;

    @Autowired
    private RemoteBomService remoteBomService;

    private final static String YYYY_MM_DD = "yyyy-MM-dd";//时间格式

    private final static String ORDER_CODE_PRE = "RO";//订单号前缀

    /**
     * 订单号序列号生成所对应的序列
     */
    private static final String OMS_REAL_ORDER_SEQ_NAME = "oms_real_order_id";
    /**
     * 订单号序列号生成所对应的序列长度
     */
    private static final int OMS_REAL_ORDER_SEQ_LENGTH = 4;

    static final String BOM_VERSION_EIGHT = "8";

    static final String BOM_VERSION_NINE = "9";

    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     *
     * @return
     */
    @Override
    public R timeCollectToOmsRealOrder() {
        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  开始");
        //1.查询 oms_internal_order_res marker = po; delivery_flag 是未完成的;
        Example exampleInternal = new Example(OmsInternalOrderRes.class);
        Example.Criteria criteria = exampleInternal.createCriteria();
        criteria.andEqualTo("marker", InternalOrderResEnum.MARKER_PO.getCode());
        criteria.andNotEqualTo("deliveryFlag",InternalOrderResEnum.DELIVERY_FLAG_X);
        List<OmsInternalOrderRes> internalOrderResList = omsInternalOrderResService.selectByExample(exampleInternal);
        if(CollectionUtils.isEmpty(internalOrderResList)){
            logger.error("定时任务每天在获取到PO信息不存在");
            throw new BusinessException("定时任务每天在获取到PO信息不存在");
        }

        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  查询 oms_internal_order_res marker结束  ");

        //2.获取cd_factory_storehouse_info 工厂库位基础表
        //一个工厂,一个客户对应一个库位
        Map<String, CdFactoryStorehouseInfo> factoryStorehouseInfoMap = factoryStorehouseInfoMap(new CdFactoryStorehouseInfo());
        //3.获取不重复的物料号和工厂Map
        //获取bom版本
        Map<String, Map<String, String>> bomMap = bomMap(internalOrderResList);

        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  汇总开始  ");
        //4.将原始数据按照成品专用号、生产工厂、客户编码、交付日期将未完成交货的数据订单数量进行汇总
        Map<String, OmsRealOrder> omsRealOrderMap = getStringOmsRealOrderMap(internalOrderResList, factoryStorehouseInfoMap, bomMap,"定时任务");

        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  批量插入开始  ");
        //5.批量插入(生产工厂、客户编码、成品专用号、交付日期、订单种类唯一索引),存在就修改
        List<OmsRealOrder> omsRealOrdersList = omsRealOrderMap.values().stream().collect(Collectors.toList());
        omsRealOrderMapper.batchInsetOrUpdate(omsRealOrdersList);
        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  结束");
        return R.ok();
    }

    /**
     * 将原始数据按照成品专用号、生产工厂、客户编码、交付日期将未完成交货的数据订单数量进行汇总
     * @param internalOrderResList
     * @param factoryStorehouseInfoMap
     * @param bomMap
     * @return
     */
    private Map<String, OmsRealOrder> getStringOmsRealOrderMap(List<OmsInternalOrderRes> internalOrderResList,
                                                               Map<String, CdFactoryStorehouseInfo> factoryStorehouseInfoMap,
                                                               Map<String, Map<String, String>> bomMap,String createBy) {
        Map<String, OmsRealOrder> omsRealOrderMap = new HashMap<>();
        internalOrderResList.forEach(internalOrderRes -> {
            String productMaterialCode = internalOrderRes.getProductMaterialCode();
            String productFactoryCode = internalOrderRes.getProductFactoryCode();
            String customerCode = internalOrderRes.getCustomerCode();
            String deliveryDate = internalOrderRes.getDeliveryDate();
            BigDecimal orderNum = (internalOrderRes.getOrderNum() == null ? BigDecimal.ZERO : internalOrderRes.getOrderNum());
            String key = productMaterialCode + productFactoryCode + customerCode + deliveryDate;
            if (omsRealOrderMap.containsKey(key)) {
                OmsRealOrder omsRealOrder = omsRealOrderMap.get(key);
                BigDecimal orderNumRealO = omsRealOrder.getOrderNum();
                BigDecimal orderNumRealX = orderNumRealO.add(orderNum);
                omsRealOrder.setOrderNum(orderNumRealX);
            } else {
                //1.订单号生成规则 ZD+年月日+4位顺序号，循序号每日清零
                StringBuffer orderCodeBuffer = new StringBuffer(ORDER_CODE_PRE);
                orderCodeBuffer.append(DateUtils.getDate().replace("-", ""));
                R seqResult = remoteSequeceService.selectSeq(OMS_REAL_ORDER_SEQ_NAME, OMS_REAL_ORDER_SEQ_LENGTH);
                if (!seqResult.isSuccess()) {
                    logger.error("真单新增生成订单号时获取序列号异常 req:{},res:{}", OMS_REAL_ORDER_SEQ_NAME, JSONObject.toJSON(seqResult));
                    throw new BusinessException("获取序列号异常");
                }
                String seq = seqResult.getStr("data");
                orderCodeBuffer.append(seq);
                OmsRealOrder omsRealOrder = new OmsRealOrder();
                omsRealOrder.setOrderCode(orderCodeBuffer.toString());
                //订单类型
                omsRealOrder.setOrderType(InternalOrderResEnum.ORDER_TYPE_GN00.getCode());
                omsRealOrder.setOrderFrom(RealOrderEnum.ORDER_FROM_1.getCode());
                //订单类型 如果订单交付日期 - 当前日期 <= 2 为追加
                String dateNow = DateUtils.getDate();
                int contDay = DateUtils.dayDiffSt(internalOrderRes.getDeliveryDate(), dateNow, YYYY_MM_DD);
                if (contDay <= 2) {
                    omsRealOrder.setOrderClass(RealOrderEnum.ORDER_CLASS_2.getCode());
                } else {
                    omsRealOrder.setOrderClass(RealOrderEnum.ORDER_CLASS_1.getCode());
                }
                omsRealOrder.setProductMaterialCode(internalOrderRes.getProductMaterialCode());
                omsRealOrder.setProductMaterialDesc(internalOrderRes.getProductMaterialDesc());
                omsRealOrder.setCustomerCode(internalOrderRes.getCustomerCode());
                omsRealOrder.setCustomerDesc(internalOrderRes.getCustomerDesc());
                omsRealOrder.setProductFactoryCode(internalOrderRes.getProductFactoryCode());
                omsRealOrder.setProductFactoryDesc(internalOrderRes.getProductFactoryDesc());
                omsRealOrder.setMrpRange(internalOrderRes.getMrpRange());
                //通过生产工厂、客户编码去BOM清单表（cd_bom_info）中获取BOM的版本号，优先8、9版本，有8选8，没8取9，其他取最小版本
                String keyBom = StrUtil.concat(true, internalOrderRes.getProductMaterialCode(), productFactoryCode);
                //key:成品物料号+生产工厂
                if (bomMap.get(keyBom) != null) {
                    //获取BOM版本
                    String boms = bomMap.get(keyBom).get("version");//逗号分隔多版本拼接
                    List<String> bomList = StrUtil.splitTrim(boms,StrUtil.COMMA);
                    if (CollUtil.contains(bomList, BOM_VERSION_EIGHT)) {
                        //有8取8
                        internalOrderRes.setVersion(BOM_VERSION_EIGHT);
                    } else if (CollUtil.contains(bomList, BOM_VERSION_NINE)){
                        //有9取9
                        internalOrderRes.setVersion(BOM_VERSION_NINE);
                    }else{
                        //取最小的
                        internalOrderRes.setVersion(bomList.stream().min((c,d)->StrUtil.compare(c,d,true)).get());
                    }
                }
                omsRealOrder.setBomVersion(internalOrderRes.getVersion());
                omsRealOrder.setPurchaseGroupCode(internalOrderRes.getPurchaseGroupCode());
                omsRealOrder.setOrderNum(internalOrderRes.getOrderNum());
                omsRealOrder.setUnit(internalOrderRes.getUnit());
                omsRealOrder.setDeliveryDate(internalOrderRes.getDeliveryDate());
                CdFactoryStorehouseInfo cdFactoryStorehouseInfo = factoryStorehouseInfoMap.get(omsRealOrder.getCustomerCode()
                        + omsRealOrder.getProductFactoryCode());
                if(null == cdFactoryStorehouseInfo){
                    logger.error("此客户和工厂对应的工厂库存信息不存在 req:{}",omsRealOrder.getCustomerCode()
                            + omsRealOrder.getProductFactoryCode());
                    throw new BusinessException("此客户和工厂对应的工厂库存信息不存在");
                }
                //计算生产日期，根据生产工厂、客户编码去工厂库位基础表（cd_factory_storehouse_info）中获取提前量，交货日期 - 提前量 = 生产日期；
                String productDate = DateUtils.dayOffset(deliveryDate, Integer.parseInt(cdFactoryStorehouseInfo.getLeadTime()), YYYY_MM_DD);
                omsRealOrder.setProductDate(productDate);
                //匹配交货地点，根据生产工厂、客户编码去工厂库位基础表（cd_factory_storehouse_info）中获取对应的交货地点；
                omsRealOrder.setPlace(cdFactoryStorehouseInfo.getStorehouseTo());
                omsRealOrder.setDataSource(RealOrderEnum.DATA_SOURCE_0.getCode());
                omsRealOrder.setDelFlag(DeleteFlagConstants.NO_DELETED);
                omsRealOrder.setCreateBy(createBy);
                omsRealOrder.setCreateTime(new Date());
                omsRealOrder.setOrderNum(internalOrderRes.getOrderNum());
                omsRealOrderMap.put(key, omsRealOrder);
            }

        });
        return omsRealOrderMap;
    }

    /**
     * 获取工厂库位基础表信息
     *
     * @param cdFactoryStorehouseInfo
     * @return key  storehouseInfo.getCustomerCode()+storehouseInfo.getProductFactoryCode()
     */
    private Map<String, CdFactoryStorehouseInfo> factoryStorehouseInfoMap(CdFactoryStorehouseInfo cdFactoryStorehouseInfo) {
        R listFactoryStorehouseInfoResult = remoteFactoryStorehouseInfoService.listFactoryStorehouseInfo(JSONObject.toJSONString(cdFactoryStorehouseInfo));
        if (!listFactoryStorehouseInfoResult.isSuccess()) {
            logger.error("定时任务每天在获取到PO信息后 进行需求汇总remoteFactoryStorehouseInfoService.listFactoryStorehouseInfo res:{} ", JSONObject.toJSONString(listFactoryStorehouseInfoResult));
            throw new BusinessException("获取工厂库位基础表信息失败");
        }
        //一个工厂,一个客户对应一个库位
        List<CdFactoryStorehouseInfo> factoryStorehouseInfoLis = listFactoryStorehouseInfoResult.getCollectData(
                new TypeReference<List<CdFactoryStorehouseInfo>>() {
                });
        Map<String, CdFactoryStorehouseInfo> factoryStorehouseInfoMap = factoryStorehouseInfoLis.stream().collect(Collectors.toMap(
                storehouseInfo -> storehouseInfo.getCustomerCode() + storehouseInfo.getProductFactoryCode(),
                storehouseInfo -> storehouseInfo, (key1, key2) -> key2));

        return factoryStorehouseInfoMap;
    }

    /**
     * 获取bom信息
     * @param internalOrderResList
     * @return
     */
    private Map<String, Map<String, String>> bomMap(List<OmsInternalOrderRes> internalOrderResList){
        List<Dict> maps = internalOrderResList.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("productMaterialCode",s.getProductMaterialCode())).distinct().collect(Collectors.toList());
        //获取bom版本
        Map<String, Map<String, String>> bomMap = remoteBomService.selectVersionMap(maps);
        if (MapUtil.isEmpty(bomMap)) {
            logger.error("获取bom版本失败 req:{}",internalOrderResList);
            throw new BusinessException("获取bom版本失败！");
        }
        return bomMap;
    }
}
