package com.cloud.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.ProductOrderConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.utils.DateUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.enums.OrderFromEnum;
import com.cloud.order.enums.ProductStatementStatusEnum;
import com.cloud.order.enums.ProductionOrderStatusEnum;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.system.domain.entity.CdProductStock;
import com.cloud.system.feign.RemoteCdProductStockService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsProductStatementMapper;
import com.cloud.order.domain.entity.OmsProductStatement;
import com.cloud.order.service.IOmsProductStatementService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * T-1交付考核报 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-08-07
 */
@Slf4j
@Service
public class OmsProductStatementServiceImpl extends BaseServiceImpl<OmsProductStatement> implements IOmsProductStatementService {
    @Autowired
    private OmsProductStatementMapper omsProductStatementMapper;

    @Autowired
    private IOmsRealOrderService omsRealOrderService;

    @Autowired
    private RemoteCdProductStockService remoteCdProductStockService;

    private static final Long DELAYS_DAYS = 1L;//拖期交货时间1天


    /**
     * 定时汇总T-1交付考核报
     */
    @Override
    public R timeAddSave() {

        //1.更新未关闭的T-1交付的拖期天数
        Example example = new Example(OmsProductStatement.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status",ProductStatementStatusEnum.PRODUCT_STATEMENT_STATUS_0.getCode());
        criteria.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        List<OmsProductStatement> omsProductStatementList =  omsProductStatementMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(omsProductStatementList)){
            //将未关闭的拖期天数加1
            omsProductStatementList.forEach(omsProductStatement -> {
                Long delaysDays = (omsProductStatement.getDelaysDays() == null ? 0 : omsProductStatement.getDelaysDays());
                omsProductStatement.setDelaysDays(delaysDays+1);
            });
            omsProductStatementMapper.updateBatchByPrimaryKeySelective(omsProductStatementList);
        }

        //2.查真单 交货日期是当天的,外单
        Example exampleRealOrder = new Example(OmsRealOrder.class);
        Example.Criteria criteriaRealOrder = exampleRealOrder.createCriteria();
        String deliveryDate = DateUtils.getDate();
        criteriaRealOrder.andEqualTo("deliveryDate", deliveryDate);
        criteriaRealOrder.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        criteriaRealOrder.andEqualTo("orderFrom", OrderFromEnum.OUT_SOURCE_TYPE_QWW.getCode());
        List<OmsRealOrder> omsRealOrderList = omsRealOrderService.selectByExample(exampleRealOrder);
        if(CollectionUtils.isEmpty(omsRealOrderList)){
            return R.ok("没有需要汇总的数据");
        }

        //3.按专用号,工厂号查成品库存信息,
        List<CdProductStock> productStocks = new ArrayList<>();
        omsRealOrderList.forEach(omsRealOrder -> {
            CdProductStock cdProductStock = new CdProductStock();
            cdProductStock.setProductMaterialCode(omsRealOrder.getProductMaterialCode());
            cdProductStock.setProductFactoryCode(omsRealOrder.getProductFactoryCode());
            productStocks.add(cdProductStock);
        });
        R rCdProductStock = remoteCdProductStockService.queryOneByFactoryAndMaterial(productStocks);
        if (!rCdProductStock.isSuccess()) {
            log.error("获取到生产工厂、成品物料号的可用库存失败 res:{}", JSONObject.toJSONString(rCdProductStock));
            return R.error("获取可用库存失败");
        }
        List<CdProductStock> cdProductStockRes = rCdProductStock.getCollectData(new TypeReference<List<CdProductStock>>() {});
        Map<String,CdProductStock> productStockMap = cdProductStockRes.stream().collect(Collectors.toMap(
                cdProductStock ->cdProductStock.getProductMaterialCode()+cdProductStock.getProductFactoryCode(),
                cdProductStock -> cdProductStock,(key1,key2) -> key2));
        //4.按专用号,工厂号,交付日期汇总
        Map<String,OmsProductStatement> map = new HashMap<>();
        omsRealOrderList.forEach(omsRealOrder ->{
            String keyCdProductStock = omsRealOrder.getProductMaterialCode() + omsRealOrder.getProductFactoryCode();
            String key = omsRealOrder.getProductMaterialCode() + omsRealOrder.getProductFactoryCode() + omsRealOrder.getDeliveryDate();
            BigDecimal deliveryNum = (omsRealOrder.getOrderNum() == null ? BigDecimal.ZERO : omsRealOrder.getOrderNum());
            if(map.containsKey(key)){
                OmsProductStatement omsProductStatement = map.get(key);
                BigDecimal deliveryNumOld = (omsProductStatement.getDeliveryNum()  == null ? BigDecimal.ZERO : omsProductStatement.getDeliveryNum());
                BigDecimal deliveryNumNew = deliveryNumOld.add(deliveryNum);
                omsProductStatement.setDeliveryNum(deliveryNumNew);
            }else {
                BigDecimal sumNum = BigDecimal.ZERO;
                CdProductStock cdProductStock = productStockMap.get(keyCdProductStock);
                if(null != cdProductStock && null != cdProductStock.getStockWNum()){
                    sumNum = cdProductStock.getStockWNum();
                }
                OmsProductStatement omsProductStatement = new OmsProductStatement();
                omsProductStatement.setProductMaterialCode(omsRealOrder.getProductMaterialCode());
                omsProductStatement.setProductMaterialDesc(omsRealOrder.getProductMaterialDesc());
                omsProductStatement.setProductFactoryCode(omsRealOrder.getProductFactoryCode());
                omsProductStatement.setProductFactoryDesc(omsRealOrder.getProductFactoryDesc());
                omsProductStatement.setDeliveryDate(omsRealOrder.getDeliveryDate());
                omsProductStatement.setDeliveryNum(omsRealOrder.getOrderNum());
                omsProductStatement.setSumNum(sumNum);
                omsProductStatement.setUnit(omsRealOrder.getUnit());
                omsProductStatement.setDelFlag(DeleteFlagConstants.NO_DELETED);
                omsProductStatement.setStatus(ProductStatementStatusEnum.PRODUCT_STATEMENT_STATUS_0.getCode());
                omsProductStatement.setDelaysDays(DELAYS_DAYS);
                omsProductStatement.setCreateBy("定时任务");
                omsProductStatement.setCreateTime(new Date());
                map.put(key,omsProductStatement);
            }
        });
        //5.将map转成list插入数据库
        List<OmsProductStatement> omsProductStatements = map.values().stream().collect(Collectors.toList());
        omsProductStatementMapper.insertList(omsProductStatements);
        return R.ok();
    }
}
