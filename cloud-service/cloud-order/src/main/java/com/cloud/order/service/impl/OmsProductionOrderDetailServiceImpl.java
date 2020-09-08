package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.ProductOrderConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.bean.BeanUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.domain.entity.vo.*;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.service.IOmsRawMaterialFeedbackService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.CdRawMaterialStock;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteCdRawMaterialStockService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsProductionOrderDetailMapper;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.service.IOmsProductionOrderDetailService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 排产订单明细 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-19
 */
@Service
@Slf4j
public class OmsProductionOrderDetailServiceImpl extends BaseServiceImpl<OmsProductionOrderDetail> implements IOmsProductionOrderDetailService {
    @Autowired
    private OmsProductionOrderDetailMapper omsProductionOrderDetailMapper;
    @Autowired
    private RemoteCdRawMaterialStockService remoteCdRawMaterialStockService;
    @Autowired
    private IOmsProductionOrderService omsProductionOrderService;
    @Autowired
    private IOmsRawMaterialFeedbackService omsRawMaterialFeedbackService;

    /**
     * Description:  根据orderCodes 查询
     * Param: [orderCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    public R selectListByOrderCodes(String orderCodes) {
        List<OmsProductionOrderDetail> list = omsProductionOrderDetailMapper.selectListByOrderCodes(orderCodes);
        R r = new R();
        r.put("code", 0);
        r.put("data", list);
        return r;
    }

    /**
     * Description:根据排产订单号删除明细数据
     * Param: [productOrderCode]
     * return: int
     * Author: ltq
     * Date: 2020/6/24
     */
    @Override
    public int delectByProductOrderCode(String productOrderCode) {
        return omsProductionOrderDetailMapper.deleteByProductOrderCode(productOrderCode);
    }

    /**
     * Description:  原材料评审-分页查询
     * Param: [omsProductionOrderDetail, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderDetailVo>
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    public List<OmsProductionOrderDetailVo> listPageInfo(OmsProductionOrderDetail omsProductionOrderDetail, SysUser sysUser) {
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //JIT根据生产工厂、采购组权限查询
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_JIT)) {
                List<String> factoryList = Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(","));
                List<String> purchaseList = Arrays.asList(DataScopeUtil.getUserPurchaseScopes(sysUser.getUserId()).split(","));
                String factorys = factoryList.stream().map(f -> "\'" + f + "\'").collect(Collectors.joining(","));
                String purchases = purchaseList.stream().map(p -> "\'" + p + "\'").collect(Collectors.joining(","));
                omsProductionOrderDetail.setProductFactoryQuery(factorys);
                omsProductionOrderDetail.setPurchaseGroupQuery(purchases);
            }
        }
        List<OmsProductionOrderDetailVo> list = omsProductionOrderDetailMapper.selectListPageInfo(omsProductionOrderDetail);
        if (ObjectUtil.isEmpty(list) || list.size() <= 0) {
            log.info("原材料评审分页查询数据为空！");
            return list;
        }
        //查询库存
        List<CdRawMaterialStock> cdRawMaterialStocks = list.stream().map(o -> {
            CdRawMaterialStock cdRawMaterialStock = new CdRawMaterialStock();
            cdRawMaterialStock.setProductFactoryCode(o.getProductFactoryCode());
            cdRawMaterialStock.setRawMaterialCode(o.getMaterialCode());
            return cdRawMaterialStock;
        }).collect(Collectors.toList());
        R stockMap = remoteCdRawMaterialStockService.selectByList(cdRawMaterialStocks);
        if (!stockMap.isSuccess()) {
            log.error("查询原材料库存失败,原因：" + stockMap.get("msg"));
            throw new BusinessException("查询原材料库存失败,原因：" + stockMap.get("msg"));
        }
        List<CdRawMaterialStock> rawMaterialStocks =
                stockMap.getCollectData(new TypeReference<List<CdRawMaterialStock>>() {
                });
        String[] days = getDays();
        List<String> dayList = Arrays.asList(days);
        //匹配库存，查询
        list.forEach(o -> {
            BigDecimal stockCurrent = rawMaterialStocks.stream().filter(s ->o.getProductFactoryCode().equals(s.getProductFactoryCode())
                    && o.getMaterialCode().equals(s.getRawMaterialCode())).map(CdRawMaterialStock::getCurrentStock).reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal stockNum = o.getStockNum() == null || StrUtil.isBlank(o.getStockNum().toString()) ? BigDecimal.ZERO : o.getStockNum();
            o.setStockNum(stockNum.add(stockCurrent));
            //可用库存
            BigDecimal currenStock = o.getStockNum();
            List<RawMaterialReviewDetailVo> rawMaterialReviewDetailVos = omsProductionOrderDetailMapper.selectRawMaterialDay(OmsProductionOrderDetail.builder()
                    .productFactoryCode(o.getProductFactoryCode())
                    .materialCode(o.getMaterialCode())
                    .purchaseGroup(o.getPurchaseGroup())
                    .build());
            List<String> detailDays = rawMaterialReviewDetailVos.stream().map(RawMaterialReviewDetailVo::getProductStartDate).collect(Collectors.toList());
            List<String> addDays = dayList.stream().filter(day -> !detailDays.contains(day)).collect(Collectors.toList());
            addDays.forEach(day ->
                    rawMaterialReviewDetailVos.add(RawMaterialReviewDetailVo.builder()
                            .gapNum(BigDecimal.ZERO)
                            .productNum(BigDecimal.ZERO)
                            .productStartDate(day)
                            .build())
            );
            //按照开始日期升序排序
            rawMaterialReviewDetailVos.sort(Comparator.comparing(RawMaterialReviewDetailVo::getProductStartDate));
            //计算缺口量
            for (RawMaterialReviewDetailVo rawMaterialReviewDetailVo : rawMaterialReviewDetailVos) {
                currenStock = currenStock.subtract(rawMaterialReviewDetailVo.getProductNum());
                rawMaterialReviewDetailVo.setGapNum(currenStock);
            }
            o.setList(rawMaterialReviewDetailVos);
        });
        return list;
    }

    /**
     * Description:  原材料评审导出
     * Param: [omsProductionOrderDetail, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    public R exportList(OmsProductionOrderDetail omsProductionOrderDetail, SysUser sysUser) {
        //查询数据记录
        List<OmsProductionOrderDetailVo> list = listPageInfo(omsProductionOrderDetail, sysUser);
        String[] days = getDays();
        List<OmsProductionOrderDetailExportVo> exportVos = new ArrayList<>();
        list.forEach(o -> {
            OmsProductionOrderDetailExportVo omsProductionOrderDetailExportVo = BeanUtil.copyProperties(o, OmsProductionOrderDetailExportVo.class);
            List<RawMaterialReviewDetailVo> rawMaterialReviewDetailVos = o.getList();
            Map<String, BigDecimal> mapNum = rawMaterialReviewDetailVos.stream()
                    .collect(Collectors.toMap(RawMaterialReviewDetailVo::getProductStartDate, RawMaterialReviewDetailVo::getProductNum));
            //应张宇舰张工要求，增加原材料评审导出差异量数据  2020-09-03  ltq
            Map<String, BigDecimal> gapNum = rawMaterialReviewDetailVos.stream()
                    .collect(Collectors.toMap(RawMaterialReviewDetailVo::getProductStartDate, RawMaterialReviewDetailVo::getGapNum));
            int index = 1;
            for (String day : days) {
                BigDecimal dayNum = mapNum.get(day);
                BigDecimal gapDayNum = gapNum.get(day);
                String dayStr = StrUtil.toString(index);
                try {
                    Method method = OmsProductionOrderDetailExportVo.class.getMethod(StrUtil.format("setDay{}", dayStr), BigDecimal.class);
                    Method gapMethod = OmsProductionOrderDetailExportVo.class.getMethod(StrUtil.format("setDay{}Gap", dayStr), BigDecimal.class);
                    method.invoke(omsProductionOrderDetailExportVo, dayNum);
                    gapMethod.invoke(omsProductionOrderDetailExportVo, gapDayNum);
                } catch (IllegalAccessException e) {
                    throw new BusinessException("系统拥挤，请稍后再试！（Invoke）");
                } catch (InvocationTargetException e) {
                    throw new BusinessException("系统拥挤，请稍后再试！（Invoke）");
                } catch (NoSuchMethodException e) {
                    throw new BusinessException("系统拥挤，请稍后再试！（Invoke）");
                }
                index++;
            }
            exportVos.add(omsProductionOrderDetailExportVo);
        });
        List<List<String>> headList = headList(days);
        return EasyExcelUtilOSS.writeExcelWithHead(exportVos, "原材料评审表.xlsx", "sheet", new OmsProductionOrderDetailExportVo(), headList);
    }

    /**
     * Description: 反馈按钮，排产信息查询
     * Param: [omsProductionOrderDetail]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    public R selectProductOrder(OmsProductionOrderDetail omsProductionOrderDetail) {

        if (StrUtil.isBlank(omsProductionOrderDetail.getProductFactoryCode())) {
            log.error("生产工厂为空！");
            return R.error("生产工厂不能为空！");
        } else if (StrUtil.isBlank(omsProductionOrderDetail.getMaterialCode())) {
            log.error("原材料号为空！");
            return R.error("原材料号不能为空！");
        } else if (StrUtil.isBlank(omsProductionOrderDetail.getProductStartDate())) {
            log.error("基本开始日期为空！");
            return R.error("基本开始日期不能为空！");
        } else if (StrUtil.isBlank(omsProductionOrderDetail.getPurchaseGroup())) {
            log.error("采购组为空！");
            return R.error("采购组不能为空！");
        }
        //根据原材料号、生产工厂、开始日期查询排产订单明细，获取排产订单号
        Example example = new Example(OmsProductionOrderDetail.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productFactoryCode", omsProductionOrderDetail.getProductFactoryCode());
        criteria.andEqualTo("materialCode", omsProductionOrderDetail.getMaterialCode());
        criteria.andEqualTo("productStartDate", omsProductionOrderDetail.getProductStartDate());
        criteria.andEqualTo("purchaseGroup", omsProductionOrderDetail.getPurchaseGroup());
        List<OmsProductionOrderDetail> omsProductionOrderDetails =
                omsProductionOrderDetailMapper.selectByExample(example);
        if (ObjectUtil.isEmpty(omsProductionOrderDetails) || omsProductionOrderDetails.size() <= 0) {
            log.info("根据原材料号、生产工厂、开始日期查询排产订单明细为空！");
            return R.ok();
        }
        OmsProductionOrderDetail orderDetail = omsProductionOrderDetails.get(0);
        Set<String> detailStatus =
                omsProductionOrderDetails.stream().map(OmsProductionOrderDetail::getStatus).collect(Collectors.toSet());
        if (detailStatus.size() <= 1 && detailStatus.contains(ProductOrderConstants.DETAIL_STATUS_ONE)) {
            log.error("原材料物料号：" + orderDetail.getMaterialCode() + "在" + orderDetail.getProductStartDate() + "日，已经确认，不可反馈！");
            return R.error("原材料物料号：" + orderDetail.getMaterialCode() + "在" + orderDetail.getProductStartDate() + "日，已经确认，不可反馈！");
        }
        //根据排产订单号查询排产订单表，根据成品专用号进行汇总
        //获取排产订单号List
        List<String> orderCodeList = omsProductionOrderDetails.stream()
                .map(OmsProductionOrderDetail::getProductOrderCode).distinct().collect(Collectors.toList());
        List<OmsProductionOrder> productionOrders = omsProductionOrderService.selectByOrderCode(orderCodeList);
        //只统计无需审核和审核完成的排产订单数据
        List<OmsProductionOrder> orderList = productionOrders.stream()
                .filter(o -> o.getAuditStatus().equals(ProductOrderConstants.AUDIT_STATUS_ZERO)
                        || o.getAuditStatus().equals(ProductOrderConstants.AUDIT_STATUS_TWO)).collect(Collectors.toList());
        //排产订单状态待评审、反馈中的分组
        Map<String, List<OmsProductionOrder>> mapOne = orderList.stream().filter(o -> o.getStatus().equals(ProductOrderConstants.STATUS_ZERO)
                || o.getStatus().equals(ProductOrderConstants.STATUS_ONE)).collect(Collectors.groupingBy((o) -> fetchGroupKey(o)));
        //排产订单其他状态的分组
        Map<String, List<OmsProductionOrder>> mapThree = orderList.stream().filter(o -> !o.getStatus().equals(ProductOrderConstants.STATUS_ZERO)
                && !o.getStatus().equals(ProductOrderConstants.STATUS_ONE)).collect(Collectors.groupingBy((o) -> fetchGroupKey(o)));
        //按照成品专用号、bom版本进行分组

        List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks = new ArrayList<>();
        mapOne.forEach((key, value) -> {
            OmsRawMaterialFeedback omsRawMaterialFeedback = getFeedback(value,omsProductionOrderDetails,omsProductionOrderDetail,ProductOrderConstants.STATUS_ZERO);
            omsRawMaterialFeedbacks.add(omsRawMaterialFeedback);
        });
        mapThree.forEach((key, value) -> {
            OmsRawMaterialFeedback omsRawMaterialFeedback = getFeedback(value,omsProductionOrderDetails,omsProductionOrderDetail,ProductOrderConstants.STATUS_ONE);
            omsRawMaterialFeedbacks.add(omsRawMaterialFeedback);
        });
        if (ObjectUtil.isEmpty(omsRawMaterialFeedbacks) || omsRawMaterialFeedbacks.size() <= 0) {
            return R.ok();
        }
        return R.data(omsRawMaterialFeedbacks);
    }
    /**
     * Description:  组织返回的数据
     * Param: [omsProductionOrders, omsProductionOrderDetails, omsProductionOrderDetail]
     * return: com.cloud.order.domain.entity.OmsRawMaterialFeedback
     * Author: ltq
     * Date: 2020/8/13
     */
    private OmsRawMaterialFeedback getFeedback(List<OmsProductionOrder> omsProductionOrders,
                                               List<OmsProductionOrderDetail> omsProductionOrderDetails,
                                               OmsProductionOrderDetail omsProductionOrderDetail,String status){
        //成品排产量
        BigDecimal productNum = omsProductionOrders.stream()
                .map(OmsProductionOrder::getProductNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        OmsProductionOrder omsProductionOrder = omsProductionOrders.get(0);
        List<String> orderCodes = omsProductionOrders.stream()
                .map(OmsProductionOrder::getOrderCode).collect(Collectors.toList());
        List<OmsProductionOrderDetail> list = omsProductionOrderDetails.stream()
                .filter(detail -> orderCodes.contains(detail.getProductOrderCode())).collect(Collectors.toList());
        //原材料排产量
        BigDecimal rawMaterialNum = list.stream()
                .map(OmsProductionOrderDetail::getRawMaterialProductNum).reduce(BigDecimal.ZERO, BigDecimal::add);
        //原材料评审-反馈按钮查询增加回显 2020-08-04  ltq
        Example example1 = new Example(OmsRawMaterialFeedback.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("productFactoryCode", omsProductionOrderDetail.getProductFactoryCode());
        criteria1.andEqualTo("rawMaterialCode", omsProductionOrderDetail.getMaterialCode());
        criteria1.andEqualTo("productStartDate", omsProductionOrderDetail.getProductStartDate());
        criteria1.andEqualTo("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        criteria1.andEqualTo("bomVersion", omsProductionOrder.getBomVersion());
        OmsRawMaterialFeedback omsRawMaterialFeedback = omsRawMaterialFeedbackService.findByExampleOne(example1);
        if (BeanUtil.isEmpty(omsRawMaterialFeedback)) {
            omsRawMaterialFeedback = OmsRawMaterialFeedback.builder()
                    .ids(omsProductionOrder.getProductMaterialCode() + omsProductionOrder.getBomVersion())
                    .productMaterialCode(omsProductionOrder.getProductMaterialCode())
                    .productMaterialDesc(omsProductionOrder.getProductMaterialDesc())
                    .bomVersion(omsProductionOrder.getBomVersion())
                    .productNum(productNum)
                    .rawMaterialNum(rawMaterialNum)
                    .status(status)
                    .productStartDate(omsProductionOrder.getProductStartDate())
                    .build();
        } else {
            omsRawMaterialFeedback.setStatus(status);
            if (ProductOrderConstants.STATUS_ONE.equals(status)) {
                omsRawMaterialFeedback = OmsRawMaterialFeedback.builder()
                        .ids(omsProductionOrder.getProductMaterialCode() + omsProductionOrder.getBomVersion())
                        .productMaterialCode(omsProductionOrder.getProductMaterialCode())
                        .productMaterialDesc(omsProductionOrder.getProductMaterialDesc())
                        .bomVersion(omsProductionOrder.getBomVersion())
                        .productNum(productNum)
                        .rawMaterialNum(rawMaterialNum)
                        .status(status)
                        .productStartDate(omsProductionOrder.getProductStartDate())
                        .build();
            }
        }
        return omsRawMaterialFeedback;
    }

    /**
     * Description: 原材料确认-列表查询
     * Param: [omsProductionOrderDetail, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    public List<OmsProductionOrderDetail> commitListPageInfo(OmsProductionOrderDetail omsProductionOrderDetail, SysUser sysUser) {
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //JIT根据生产工厂、采购组权限查询
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_JIT)) {
                List<String> factoryList = Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(","));
                List<String> purchaseList = Arrays.asList(DataScopeUtil.getUserPurchaseScopes(sysUser.getUserId()).split(","));
                String factorys = factoryList.stream().map(f -> "\'" + f + "\'").collect(Collectors.joining(","));
                String purchases = purchaseList.stream().map(p -> "\'" + p + "\'").collect(Collectors.joining(","));
                omsProductionOrderDetail.setProductFactoryQuery(factorys);
                omsProductionOrderDetail.setPurchaseGroupQuery(purchases);
            }
        }
        return omsProductionOrderDetailMapper.selectCommitListPageInfo(omsProductionOrderDetail);
    }

    /**
     * Description:  根据list查询
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrderDetail>
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    public List<OmsProductionOrderDetail> selectListByList(List<OmsRawMaterialFeedback> list) {
        return omsProductionOrderDetailMapper.selectListByList(list);
    }

    /**
     * Description: 原材料确认
     * Param: [omsProductionOrderDetail, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R commitProductOrderDetail(List<OmsProductionOrderDetail> list, OmsProductionOrderDetail omsProductionOrderDetail, SysUser sysUser) {
        if (ObjectUtil.isEmpty(list) || list.size() <= 0) {
            if (!BeanUtils.checkObjAllFieldsIsNull(omsProductionOrderDetail)) {
                if (StrUtil.isNotBlank(omsProductionOrderDetail.getStatus())
                        && (ProductOrderConstants.DETAIL_STATUS_ONE.equals(omsProductionOrderDetail.getStatus())
                        || ProductOrderConstants.DETAIL_STATUS_TWO.equals(omsProductionOrderDetail.getStatus()))) {
                    return R.error("只能确认未确认的数据！");
                }
            } else {
                log.error("原材料确认,传入参数为空！");
                return R.error("传入参数为空！");
            }
            if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                //JIT根据生产工厂、采购组权限查询
                if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_JIT)) {
                    List<String> factoryList = Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(","));
                    List<String> purchaseList = Arrays.asList(DataScopeUtil.getUserPurchaseScopes(sysUser.getUserId()).split(","));
                    String factorys = factoryList.stream().map(f -> "\'" + f + "\'").collect(Collectors.joining(","));
                    String purchases = purchaseList.stream().map(p -> "\'" + p + "\'").collect(Collectors.joining(","));
                    omsProductionOrderDetail.setProductFactoryQuery(factorys);
                    omsProductionOrderDetail.setPurchaseGroupQuery(purchases);
                    omsProductionOrderDetail.setStatus(ProductOrderConstants.DETAIL_STATUS_ZERO);
                    list.add(omsProductionOrderDetail);
                } else {
                    return R.error("该操作只允许工厂JIT操作！");
                }
            }
        }
        /**更新反馈信息记录的状态为“未审核已确认”*/
        //后因为反馈的原材料修改为不可以确认，故注释掉该部分代码  2020-08-14  ltq
        //根据生产工厂、原材料物料、开始日期查询未审核的原材料反馈信息
        /*List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks = list.stream().map(o -> {
            OmsRawMaterialFeedback omsRawMaterialFeedback = new OmsRawMaterialFeedback();
            omsRawMaterialFeedback.setProductFactoryCode(o.getProductFactoryCode());
            omsRawMaterialFeedback.setRawMaterialCode(o.getMaterialCode());
            omsRawMaterialFeedback.setProductStartDate(o.getProductStartDate());
            omsRawMaterialFeedback.setStatus("0");
            omsRawMaterialFeedback.setUpdateBy(sysUser.getLoginName());
            return omsRawMaterialFeedback;
        }).collect(Collectors.toList());
        //更新反馈信息状态
        omsRawMaterialFeedbackService.updateBatchByList(omsRawMaterialFeedbacks);*/
        /**更新排产订单、排产订单明细的状态*/
        //根据生产工厂、原材料物料、开始日期查询排产订单明细
        //修改查询逻辑，只查询审核状态为“无需审核”，“审核完成”的排产订单明细数据
        List<OmsProductionOrderDetail> omsProductionOrderDetails =
                omsProductionOrderDetailMapper.selectByFactoryDateMaterialList(list);
        if (ObjectUtil.isEmpty(omsProductionOrderDetails) || omsProductionOrderDetails.size() <= 0) {
            log.error("未查询到排产订单明细！");
            return R.error("未查询到排产订单明细！");
        }
        List<String> productOrderCodeList = omsProductionOrderDetails.stream()
                .map(o -> o.getProductOrderCode()).distinct().collect(Collectors.toList());
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("orderCode",productOrderCodeList);
        criteria.andEqualTo("status",ProductOrderConstants.STATUS_TWO);
        List<OmsProductionOrder> productionOrders = omsProductionOrderService.selectByExample(example);
        if (ObjectUtil.isNotEmpty(productionOrders) && productionOrders.size() > 0) {
            List<String> orderCodeList = productionOrders.stream()
                    .map(OmsProductionOrder::getOrderCode).collect(Collectors.toList());
            omsProductionOrderDetails = omsProductionOrderDetails.stream()
                    .filter(o -> !orderCodeList.contains(o.getProductOrderCode())).collect(Collectors.toList());
        }
        if (omsProductionOrderDetails.size() <= 0) {
            if (ObjectUtil.isNotEmpty(productionOrders) && productionOrders.size() > 0) {
                String orderCodeStr = productionOrders.stream()
                        .map(OmsProductionOrder::getOrderCode).distinct().collect(Collectors.joining(","));
                log.info("确认通过部分数据，以下排产订单尚未调整，请联系排产员调整后再确认！"+orderCodeStr);
                return R.error("确认通过部分数据，以下排产订单尚未调整，请联系排产员调整后再确认！"+orderCodeStr);
            }
            log.info("无可更新排产订单明细数据！");
            return R.ok();
        }
        //设置排产订单明细状态为“已确认”
        omsProductionOrderDetails.forEach(detail -> {
            detail.setStatus(ProductOrderConstants.DETAIL_STATUS_ONE);
            detail.setUpdateBy(sysUser.getLoginName());
            detail.setUpdateTime(new Date());
        });
        int updatDetailCount = omsProductionOrderDetailMapper.updateBatchByPrimaryKeySelective(omsProductionOrderDetails);
        if (updatDetailCount <= 0) {
            log.error("更新排产订单明细状态失败！");
            return R.error("更新排产订单明细状态失败！");
        }
        //组织排产订单号
        List<String> orderCodes = omsProductionOrderDetails.stream()
                .map(OmsProductionOrderDetail::getProductOrderCode).distinct().collect(Collectors.toList());
        //根据排产订单号查询明细数据
        List<OmsProductionOrderDetail> omsProductionOrderDetailList =
                omsProductionOrderDetailMapper.selectByOrderCodeList(orderCodes);
        //按照排产订单号进行分组
        Map<String, List<OmsProductionOrderDetail>> map = omsProductionOrderDetailList
                .stream().collect(Collectors.groupingBy(OmsProductionOrderDetail::getProductOrderCode));
        List<OmsProductionOrder> orderList = new ArrayList<>();
        map.forEach((key, value) -> {
            //统计排产订单明细数据已确认的条数
            long count = value.stream()
                    .filter(detail -> ProductOrderConstants.DETAIL_STATUS_ONE.equals(detail.getStatus())).count();
            long countAll = value.size();
            //已确认条数和总条数如果一致，则排产订单已评审
            if (count == countAll) {
                OmsProductionOrder omsProductionOrder = OmsProductionOrder.builder()
                        .orderCode(key)
                        .status(ProductOrderConstants.STATUS_THREE)
                        .build();
                omsProductionOrder.setUpdateBy(sysUser.getLoginName());
                orderList.add(omsProductionOrder);
            }
        });
        if (orderList.size() > 0) {
            omsProductionOrderService.updateByOrderCode(orderList);
        }
        if (ObjectUtil.isNotEmpty(productionOrders) && productionOrders.size() > 0) {
            String orderCodeStr = productionOrders.stream()
                    .map(OmsProductionOrder::getOrderCode).distinct().collect(Collectors.joining(","));
            return R.error("确认通过部分数据，以下排产订单尚未调整，请联系排产员调整后再确认！"+orderCodeStr);
        }
        return R.ok();
    }

    /**
     * Description:  根据排产订单号批量更新状态
     * Param: [list]
     * return: void
     * Author: ltq
     * Date: 2020/6/30
     */
    @Override
    public void updateBatchByProductOrderCode(List<OmsProductionOrderDetail> list) {
        log.info("XID:" + RootContext.getXID());
        omsProductionOrderDetailMapper.updateBatchByProductOrderCode(list);
    }

    private String fetchGroupKey(OmsProductionOrder omsProductionOrder) {
        return omsProductionOrder.getProductMaterialCode() + omsProductionOrder.getBomVersion();
    }

    /**
     * Description:  获取14天日期数组
     * Param: []
     * return: java.lang.String[]
     * Author: ltq
     * Date: 2020/6/29
     */
    private String[] getDays() {
        int[] days = NumberUtil.range(0, 13);
        String[] dates = new String[14];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i : days) {
            String day = dateTimeFormatter.format(LocalDate.now().plusDays(i));
            dates[i] = day;
        }
        return dates;
    }

    private List<List<String>> headList(String[] days) {
        List<List<String>> headList = new ArrayList<List<String>>();
        List<String> headTitle0 = new ArrayList<String>();
        List<String> headTitle1 = new ArrayList<String>();
        List<String> headTitle2 = new ArrayList<String>();
        List<String> headTitle3 = new ArrayList<String>();
        List<String> headTitle4 = new ArrayList<String>();
        List<String> headTitle5 = new ArrayList<String>();
        headTitle0.add("原材料号");
        headTitle1.add("原材料描述");
        headTitle2.add("生产工厂");
        headTitle3.add("单位");
        headTitle4.add("采购组");
        headTitle5.add("可用库存");
        headList.add(headTitle0);
        headList.add(headTitle1);
        headList.add(headTitle2);
        headList.add(headTitle3);
        headList.add(headTitle4);
        headList.add(headTitle5);
        for (String day : days) {
            day = StrUtil.sub(day,day.indexOf("-")+1,day.length());
            List<String> headTitle6 = new ArrayList<String>();
            headTitle6.add(day);
            List<String> headTitle7 = new ArrayList<String>();
            headTitle7.add(StrUtil.concat(true,day,"缺口"));
            headList.add(headTitle6);
            headList.add(headTitle7);
        }
        return headList;
    }
}
