package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.feign.RemoteActOmsOrderMaterialOutService;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.domain.entity.vo.OmsRealOrderExcelImportErrorVo;
import com.cloud.order.domain.entity.vo.OmsRealOrderExcelImportVo;
import com.cloud.order.enums.*;
import com.cloud.order.mapper.OmsRealOrderMapper;
import com.cloud.order.service.IOmsInternalOrderResService;
import com.cloud.order.service.IOmsRealOrderExcelImportService;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.feign.RemoteDictDataService;
import com.cloud.system.feign.RemoteFactoryInfoService;
import com.cloud.system.feign.RemoteFactoryStorehouseInfoService;
import com.cloud.system.feign.RemoteMaterialExtendInfoService;
import com.cloud.system.feign.RemoteSequeceService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 真单Service业务层处理
 *
 * @author ltq
 * @date 2020-06-15
 */
@Service
public class OmsRealOrderServiceImpl extends BaseServiceImpl<OmsRealOrder> implements IOmsRealOrderExcelImportService,IOmsRealOrderService {

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
    private RemoteFactoryInfoService remoteFactoryInfoService;

    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;

    @Autowired
    private IOmsRealOrderExcelImportService omsRealOrderExcelImportService;

    @Autowired
    private RemoteActOmsOrderMaterialOutService remoteActOmsOrderMaterialOutService;

    @Autowired
    private RemoteDictDataService remoteDictDataService;

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

    private static String TABLE_NAME = "oms_real_order";

    /**
     * 修改保存真单
     * @param omsRealOrder 真单对象
     * @return
     */
    @Override
    public R editSaveOmsRealOrder(OmsRealOrder omsRealOrder, SysUser sysUser,long userId) {
        //修改仅能修改排产员查对应工厂的数据,业务经理查自己导入的
        List<String> factoryScopesList = new ArrayList<>();
        //获取排产员对应的工厂
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            factoryScopesList = Arrays.asList(DataScopeUtil.getUserFactoryScopes(userId).split(","));
        }
        OmsRealOrder omsRealOrderRes = omsRealOrderMapper.selectByPrimaryKey(omsRealOrder);
        if(!RealOrderDataSourceEnum.DATA_SOURCE_1.getCode().equals(omsRealOrderRes.getDataSource())){
            logger.error("修改保存真单异常 id:{},res:{}",omsRealOrder.getId(),JSONObject.toJSON(omsRealOrderRes));
            throw new BusinessException("非人工导入数据不可修改");
        }
        if(!CollectionUtils.isEmpty(factoryScopesList) && !factoryScopesList.contains(omsRealOrderRes.getProductFactoryCode())){
            logger.error("排产员仅可修改对应的工厂数据 id:{},res:{}",omsRealOrder.getId(),JSONObject.toJSON(omsRealOrderRes));
            throw new BusinessException("排产员仅可修改对应的工厂数据");
        }else{
            if(!sysUser.getLoginName().equals(omsRealOrderRes.getCreateBy())){
                logger.error("业务仅可修改自己导入的数据 id:{},loginName:{},res:{}",omsRealOrder.getId(),
                        sysUser.getLoginName(),JSONObject.toJSON(omsRealOrderRes));
                throw new BusinessException("业务仅可修改自己导入的数据");
            }
        }
        omsRealOrder.setStatus(RealOrderStatusEnum.STATUS_1.getCode());
        omsRealOrder.setUpdateBy(sysUser.getLoginName());
        omsRealOrderMapper.updateByPrimaryKeySelective(omsRealOrder);
        return R.ok();
    }

    @GlobalTransactional
    @Override
    public R importRealOrderFile(MultipartFile file, String orderFrom,SysUser sysUser) throws IOException {
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(omsRealOrderExcelImportService, OmsRealOrderExcelImportVo.class);
        EasyExcel.read(file.getInputStream(),OmsRealOrderExcelImportVo.class,easyExcelListener).sheet().doRead();
        //需要审核的结果
        List<ExcelImportOtherObjectDto> auditList=easyExcelListener.getOtherList();
        List<OmsRealOrder> auditResult = new ArrayList<>();
        if (!CollectionUtils.isEmpty(auditList)){
            auditResult =auditList.stream().map(excelImportAuditObjectDto -> {
                OmsRealOrder omsRealOrder = BeanUtil.copyProperties(excelImportAuditObjectDto.getObject(), OmsRealOrder.class);
                return omsRealOrder;
            }).collect(Collectors.toList());
        }
        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)){
            List<OmsRealOrder> successResult =successList.stream().map(excelImportSucObjectDto -> {
                OmsRealOrder omsRealOrder = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(), OmsRealOrder.class);
                return omsRealOrder;
            }).collect(Collectors.toList());
            R result = importOmsRealOrder(successResult,auditResult,sysUser,orderFrom);
            if(!result.isSuccess()){
                logger.error("导入时插入数据异常 res:{}", JSONObject.toJSONString(result));
                return result;
            }
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (!CollectionUtils.isEmpty(errList)){
            List<OmsRealOrderExcelImportErrorVo> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                OmsRealOrderExcelImportErrorVo omsRealOrderExcelImportErrorVo = BeanUtil.copyProperties(excelImportErrObjectDto.getObject(),
                        OmsRealOrderExcelImportErrorVo.class);
                omsRealOrderExcelImportErrorVo.setErrorMessage(excelImportErrObjectDto.getErrMsg());
                return omsRealOrderExcelImportErrorVo;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "真单导入错误信息.xlsx", "sheet", new OmsRealOrderExcelImportErrorVo());
        }
        return R.ok();
    }

    /**
     * 导入真单
     * @param successResult  需要导入的数据
     * @param auditResult  需要审核的数据
     * @param sysUser  用户信息
     * @param orderFrom  内单或外单
     * @return
     */
    private R importOmsRealOrder(List<OmsRealOrder> successResult, List<OmsRealOrder> auditResult, SysUser sysUser,String orderFrom) {
        if(CollectionUtils.isEmpty(successResult)){
            return R.error("无需要插入的数据");
        }
        successResult.forEach( omsRealOrder -> {
            String orderCode = getOrderCode();
            omsRealOrder.setOrderCode(orderCode);
            omsRealOrder.setDataSource(RealOrderDataSourceEnum.DATA_SOURCE_1.getCode());
            omsRealOrder.setOrderFrom(orderFrom);
            omsRealOrder.setCreateBy(sysUser.getLoginName());
            if(RealOrderFromEnum.ORDER_FROM_2.getCode().equals(orderFrom)){
                //交付日期即生产日期
                omsRealOrder.setProductDate(omsRealOrder.getDeliveryDate());
            }
        });
        logger.info("导入真单插入数据开始");
        omsRealOrderMapper.batchInsetOrUpdate(successResult);

        logger.info("导入真单开启审批流开始");
        //key 工厂编号 ,客户编号 物料号,交货日期,订单类型
        if(!CollectionUtils.isEmpty(auditResult)){
            Map<String,OmsRealOrder> auditResultMap = auditResult.stream().collect(Collectors.toMap(
                    omsRealOrder -> omsRealOrder.getProductFactoryCode() + omsRealOrder.getCustomerCode() + omsRealOrder.getProductMaterialCode()
                            + omsRealOrder.getDeliveryDate() + omsRealOrder.getOrderClass(),
                    cdProductStock -> cdProductStock,(key1,key2) ->key2));

            Map<String,OmsRealOrder> successResultMap = successResult.stream().collect(Collectors.toMap(
                    omsRealOrder -> omsRealOrder.getProductFactoryCode() + omsRealOrder.getCustomerCode() + omsRealOrder.getProductMaterialCode()
                            + omsRealOrder.getDeliveryDate() + omsRealOrder.getOrderClass(),
                    cdProductStock -> cdProductStock,(key1,key2) ->key2));

            OmsOrderMaterialOutVo auditResultReq = new OmsOrderMaterialOutVo();
            List<OmsOrderMaterialOutVo> omsOrderMaterialOutVoList = new ArrayList<>();
            auditResultMap.keySet().forEach(code -> {
                OmsRealOrder omsRealOrder = successResultMap.get(code);
                OmsOrderMaterialOutVo omsOrderMaterialOutVo = new OmsOrderMaterialOutVo();
                omsOrderMaterialOutVo.setLoginId(sysUser.getUserId());
                omsOrderMaterialOutVo.setCreateBy(sysUser.getLoginName());
                omsOrderMaterialOutVo.setOrderCode(omsRealOrder.getOrderCode());
                omsOrderMaterialOutVo.setId(omsRealOrder.getId());
                omsOrderMaterialOutVo.setTableName(TABLE_NAME);
                omsOrderMaterialOutVo.setFactoryCode(omsRealOrder.getProductFactoryCode());
                omsOrderMaterialOutVoList.add(omsOrderMaterialOutVo);
            });
            auditResultReq.setOmsOrderMaterialOutVoList(omsOrderMaterialOutVoList);
            R auditResultR = remoteActOmsOrderMaterialOutService.addSave(auditResultReq);
            if(!auditResultR.isSuccess()){
                logger.error("下市的数据开启审批流失败 e:{}",auditResultR.toString());
                throw new BusinessException("下市的数据开启审批流失败");
            }

        }
        return R.ok();
    }

    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     *
     * @return
     */
    @Transactional
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

        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  汇总开始  ");
        //3.将原始数据按照成品专用号、生产工厂、客户编码、交付日期将未完成交货的数据订单数量进行汇总
        Map<String, OmsRealOrder> omsRealOrderMap = getStringOmsRealOrderMap(internalOrderResList, factoryStorehouseInfoMap,"定时任务");

        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  批量插入开始  ");
        //4.批量插入(生产工厂、客户编码、成品专用号、交付日期、订单种类唯一索引),存在就修改
        List<OmsRealOrder> omsRealOrdersList = omsRealOrderMap.values().stream().collect(Collectors.toList());
        omsRealOrderMapper.batchInsetOrUpdate(omsRealOrdersList);
        logger.info("定时任务每天在获取到PO信息后 进行需求汇总  结束");
        return R.ok();
    }

    @Override
    public R deleteOmsRealOrder(String ids, OmsRealOrder omsRealOrder,SysUser sysUser,long currentUserId) {
        if(StringUtils.isNotBlank(ids)){
            List<OmsRealOrder> omsRealOrderSelectList =  omsRealOrderMapper.selectByIds(ids);
            if(CollectionUtils.isEmpty(omsRealOrderSelectList)){
                return R.error("数据不存在");
            }
            omsRealOrderSelectList.forEach(omsRealOrder1 -> {
                if(!RealOrderDataSourceEnum.DATA_SOURCE_1.getCode().equals(omsRealOrder1.getDataSource())){
                    logger.error("非人工导入数据不可删除 订单号:{}",omsRealOrder1.getOrderCode());
                    throw new BusinessException("非人工导入数据不可删除");
                }
            });
            int count = omsRealOrderMapper.deleteByIds(ids);
            return R.data(count);
        }
        Example example = assemblyConditions(omsRealOrder);
        //排产员查对应工厂的数据,业务经理查自己导入的
        if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)) {
            if (StringUtils.isBlank(omsRealOrder.getProductFactoryCode())) {
                example.and().andIn("productFactoryCode", Arrays.asList(
                        DataScopeUtil.getUserFactoryScopes(currentUserId).split(",")));
            }
        } else if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_SCBJL)){
            example.and().andEqualTo("createBy", sysUser.getLoginName());
        }
        example.and().andEqualTo("dataSource",RealOrderDataSourceEnum.DATA_SOURCE_1.getCode());
        int count = omsRealOrderMapper.deleteByExample(example);
        return R.data(count);
    }

    /**
     * 组装查询条件
     *
     * @return
     */
    private Example assemblyConditions(OmsRealOrder omsRealOrder) {
        Example example = new Example(OmsRealOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //专用号 工厂 交付日期  订单来源  订单分类  订单类型  客户编号  审核状态
        if (StringUtils.isNotBlank(omsRealOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsRealOrder.getProductMaterialCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsRealOrder.getProductFactoryCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getOrderFrom())) {
            criteria.andEqualTo("orderFrom", omsRealOrder.getOrderFrom());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getOrderType())) {
            criteria.andEqualTo("orderType", omsRealOrder.getOrderType());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getOrderClass())) {
            criteria.andEqualTo("orderClass", omsRealOrder.getOrderClass());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getCustomerCode())) {
            criteria.andEqualTo("customerCode", omsRealOrder.getCustomerCode());
        }
        if (StringUtils.isNotBlank(omsRealOrder.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", omsRealOrder.getAuditStatus());
        }

        if(StringUtils.isNotBlank(omsRealOrder.getBeginTime())){
            criteria.andGreaterThanOrEqualTo("deliveryDate",omsRealOrder.getBeginTime());
        }
        if(StringUtils.isNotBlank(omsRealOrder.getEndTime())){
            criteria.andLessThanOrEqualTo("deliveryDate", omsRealOrder.getEndTime());
        }
        return example;
    }

    /**
     * 将原始数据按照成品专用号、生产工厂、客户编码、交付日期将未完成交货的数据订单数量进行汇总
     * @param internalOrderResList
     * @param factoryStorehouseInfoMap
     * @return
     */
    private Map<String, OmsRealOrder> getStringOmsRealOrderMap(List<OmsInternalOrderRes> internalOrderResList,
                                                               Map<String, CdFactoryStorehouseInfo> factoryStorehouseInfoMap,
                                                              String createBy) {
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
                String orderCode = getOrderCode();
                OmsRealOrder omsRealOrder = new OmsRealOrder();
                omsRealOrder.setOrderCode(orderCode);
                //订单类型
                omsRealOrder.setOrderType(InternalOrderResEnum.ORDER_TYPE_GN00.getCode());
                omsRealOrder.setOrderFrom(RealOrderFromEnum.ORDER_FROM_1.getCode());
                //订单类型 如果订单交付日期 - 当前日期 <= 2 为追加
                String dateNow = DateUtils.getDate();
                int contDay = DateUtils.dayDiffSt(internalOrderRes.getDeliveryDate(), dateNow, YYYY_MM_DD);
                if (contDay <= 2) {
                    omsRealOrder.setOrderClass(RealOrderClassEnum.ORDER_CLASS_2.getCode());
                } else {
                    omsRealOrder.setOrderClass(RealOrderClassEnum.ORDER_CLASS_1.getCode());
                }
                omsRealOrder.setProductMaterialCode(internalOrderRes.getProductMaterialCode());
                omsRealOrder.setProductMaterialDesc(internalOrderRes.getProductMaterialDesc());
                omsRealOrder.setCustomerCode(internalOrderRes.getCustomerCode());
                omsRealOrder.setCustomerDesc(internalOrderRes.getCustomerDesc());
                omsRealOrder.setProductFactoryCode(internalOrderRes.getProductFactoryCode());
                omsRealOrder.setProductFactoryDesc(internalOrderRes.getProductFactoryDesc());
                omsRealOrder.setMrpRange(internalOrderRes.getMrpRange());
                omsRealOrder.setBomVersion(internalOrderRes.getVersion());
                omsRealOrder.setPurchaseGroupCode(internalOrderRes.getPurchaseGroupCode());
                omsRealOrder.setOrderNum(internalOrderRes.getOrderNum());
                omsRealOrder.setUnit(internalOrderRes.getUnit());
                omsRealOrder.setDeliveryDate(internalOrderRes.getDeliveryDate());
                omsRealOrder.setAuditStatus(RealOrderAduitStatusEnum.AUDIT_STATUS_WXSH.getCode());
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
                omsRealOrder.setDataSource(RealOrderDataSourceEnum.DATA_SOURCE_0.getCode());
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
     * 生成订单号 订单号生成规则 ZD+年月日+4位顺序号，循序号每日清零
     * @return
     */
    private String getOrderCode() {
        StringBuffer orderCodeBuffer = new StringBuffer(ORDER_CODE_PRE);
        orderCodeBuffer.append(DateUtils.getDate().replace("-", ""));
        R seqResult = remoteSequeceService.selectSeq(OMS_REAL_ORDER_SEQ_NAME, OMS_REAL_ORDER_SEQ_LENGTH);
        if (!seqResult.isSuccess()) {
            logger.error("真单新增生成订单号时获取序列号异常 req:{},res:{}", OMS_REAL_ORDER_SEQ_NAME, JSONObject.toJSON(seqResult));
            throw new BusinessException("获取序列号异常");
        }
        String seq = seqResult.getStr("data");
        orderCodeBuffer.append(seq);
        return orderCodeBuffer.toString();
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

    @Override
    public <T> ExcelImportResult checkImportExcel(List<T> objects) {
        if (CollUtil.isEmpty(objects)) {
            return new ExcelImportResult(new ArrayList<>());
        }

        //错误数据
        List<ExcelImportErrObjectDto> errDtos = new ArrayList<>();
        //可导入数据
        List<ExcelImportSucObjectDto> successDtos = new ArrayList<>();
        List<ExcelImportOtherObjectDto> otherDtos = new ArrayList<>();

        List<OmsRealOrderExcelImportVo> listImport = (List<OmsRealOrderExcelImportVo>) objects;

        //获取工厂库位基础表
        R listFactoryStorehouseInfoResult = remoteFactoryStorehouseInfoService.listFactoryStorehouseInfo(JSONObject.toJSONString(new CdFactoryStorehouseInfo()));
        if (!listFactoryStorehouseInfoResult.isSuccess()) {
            logger.error("获取工厂库位基础表信息失败res:{} ", JSONObject.toJSONString(listFactoryStorehouseInfoResult));
            throw new BusinessException("获取工厂库位基础表信息失败");
        }
        List<CdFactoryStorehouseInfo> listFactoryStorehouseInfo = listFactoryStorehouseInfoResult.getCollectData(new TypeReference<List<CdFactoryStorehouseInfo>>() {});
        List<String> customerCodeList = listFactoryStorehouseInfo.stream().map(CdFactoryStorehouseInfo::getCustomerCode).collect(Collectors.toList());
        Map<String, CdFactoryStorehouseInfo> factoryStorehouseInfoMap = listFactoryStorehouseInfo.stream().collect(Collectors.toMap(
                storehouseInfo -> storehouseInfo.getCustomerCode() + storehouseInfo.getProductFactoryCode(),
                storehouseInfo -> storehouseInfo, (key1, key2) -> key2));

        //因数量较大，一次性取出cd_material_info物料描述，cd_material_extend_info生命周期，cd_factory_info公司编码
        R rCompanyList=remoteFactoryInfoService.getAllCompanyCode();
        if (!rCompanyList.isSuccess()) {
            throw new BusinessException("无工厂信息，请到基础信息维护！");
        }
        List<String> companyCodeList = rCompanyList.getCollectData(new TypeReference<List<String>>() {});
        //取导入数据的所有物料号
        List<String> materialCodeList=listImport.stream().map(omsRealOrder->{
            return omsRealOrder.getProductMaterialCode();
        }).distinct().collect(Collectors.toList());
        //key:物料号 value：物料信息
        //查询导入数据的物料扩展信息
        R rMateiralExt = remoteMaterialExtendInfoService.selectInfoInMaterialCodes(materialCodeList);
        if (!rMateiralExt.isSuccess()) {
            throw new BusinessException("导入数据的物料扩展信息无数据，请到基础信息维护！");
        }
        Map<String, CdMaterialExtendInfo> materialExtendInfoMap = rMateiralExt.getCollectData(new TypeReference<Map<String, CdMaterialExtendInfo>>() {});
        Date date = DateUtil.date();

        for(OmsRealOrderExcelImportVo omsRealOrder : listImport){
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();
            ExcelImportOtherObjectDto othObjectDto = new ExcelImportOtherObjectDto();

            OmsRealOrder omsRealOrderReq = new OmsRealOrder();
            BeanUtils.copyProperties(omsRealOrder,omsRealOrderReq);
            StringBuffer errMsgBuffer = new StringBuffer();
            if(StringUtils.isBlank(omsRealOrder.getOrderType())){
                errMsgBuffer.append("SAP订单类型不能为空;");
            }
            //校验订单类型
            List<SysDictData> listSysDictData = remoteDictDataService.getType("sap_order_type");
            List<String> dictValueS = listSysDictData.stream().map(m -> m.getDictValue()).collect(Collectors.toList());
            if(!dictValueS.contains(omsRealOrder.getOrderType())){
                errMsgBuffer.append("此SAP订单类型不存在;");
            }
            String orderClassReq = omsRealOrder.getOrderClass();
            if(StringUtils.isNotBlank(orderClassReq)){
                String orderClass = RealOrderClassEnum.getCodeByMsg(orderClassReq);
                if(StringUtils.isBlank(orderClass) || orderClassReq.equals(orderClass)){
                    errMsgBuffer.append("不存在此订单种类;");
                }else{
                    omsRealOrderReq.setOrderClass(orderClass);
                }
            }

            String factoryCode = omsRealOrder.getProductFactoryCode();
            if(StringUtils.isBlank(factoryCode)){
                errMsgBuffer.append("工厂编号不能为空;");
            }
            if(StringUtils.isNotBlank(factoryCode) && !companyCodeList.contains(factoryCode)){
                errMsgBuffer.append("不存在此工厂,请维护;");
            }

            String productMaterialCode = omsRealOrder.getProductMaterialCode();
            if(StringUtils.isBlank(productMaterialCode)){
                errMsgBuffer.append("成品物料号不能为空;");
            }
            if(StringUtils.isNotBlank(productMaterialCode)){
                //物料描述赋值
                CdMaterialExtendInfo cdMaterialExtendInfo = materialExtendInfoMap.get(omsRealOrder.getProductMaterialCode());
                if (null == cdMaterialExtendInfo || StringUtils.isBlank(cdMaterialExtendInfo.getMaterialDesc())) {
                    errMsgBuffer.append("成品物料描述不存在,请维护;");
                } else {
                    omsRealOrderReq.setProductMaterialDesc(cdMaterialExtendInfo.getMaterialDesc());
                    String lifeCyle = cdMaterialExtendInfo.getLifeCycle();
                    if (StrUtil.equals(LifeCycleEnum.SMZQ_XS.getCode(),lifeCyle)) {
                        //已下市
                        omsRealOrderReq.setAuditStatus(RealOrderAduitStatusEnum.AUDIT_STATUS_SHZ.getCode());
                    }else{
                        omsRealOrderReq.setAuditStatus(RealOrderAduitStatusEnum.AUDIT_STATUS_WXSH.getCode());
                    }
                }
            }

            //客户编码
            String customerCode = omsRealOrder.getCustomerCode();
            if(StringUtils.isBlank(customerCode)){
                errMsgBuffer.append("客户编码不存在,请维护;");
            }
            if(StringUtils.isNotBlank(customerCode) && !customerCodeList.contains(customerCode)){
                errMsgBuffer.append("不存在此客户,请维护;");
            }
            if(StringUtils.isBlank(omsRealOrder.getCustomerDesc())){
                errMsgBuffer.append("客户名称不能为空;");
            }
            if(StringUtils.isBlank(omsRealOrder.getMrpRange())){
                errMsgBuffer.append("MRP范围不能为空;");
            }
            if(StringUtils.isBlank(omsRealOrder.getBomVersion())){
                errMsgBuffer.append("版本不能为空;");
            }
            if(StringUtils.isBlank(omsRealOrder.getDeliveryDate())){
                errMsgBuffer.append("交付日期不能为空;");
            }

            if(null == omsRealOrder.getOrderNum()){
                errMsgBuffer.append("订单不能为空;");
            }
            //地点
            String place = omsRealOrder.getPlace();
            if(StringUtils.isBlank(place)){
                errMsgBuffer.append("地点不能为空;");
            }
            if(StringUtils.isNotBlank(place)){
                CdFactoryStorehouseInfo cdFactoryStorehouseInfo = factoryStorehouseInfoMap.get(omsRealOrder.getCustomerCode()
                        + omsRealOrder.getProductFactoryCode());
                if (null == cdFactoryStorehouseInfo) {
                    errMsgBuffer.append("客户和生产工厂不对应此库位;");
                }else{
                    //计算生产日期，根据生产工厂、客户编码去工厂库位基础表（cd_factory_storehouse_info）中获取提前量，交货日期 - 提前量 = 生产日期；
                    String productDate = DateUtils.dayOffset(omsRealOrder.getDeliveryDate(), -Integer.parseInt(cdFactoryStorehouseInfo.getLeadTime()), YYYY_MM_DD);
                    omsRealOrderReq.setProductDate(productDate);
                }
            }
            String errMsgBufferString = errMsgBuffer.toString();
            if(StringUtils.isNotBlank(errMsgBufferString)){
                errObjectDto.setObject(omsRealOrder);
                errObjectDto.setErrMsg(errMsgBufferString);
                errDtos.add(errObjectDto);
                continue;
            }
            omsRealOrderReq.setStatus(RealOrderStatusEnum.STATUS_0.getCode());
            omsRealOrderReq.setCreateTime(date);
            omsRealOrderReq.setDelFlag("0");
            //下市需要审批的集合
            if(RealOrderAduitStatusEnum.AUDIT_STATUS_SHZ.getCode().equals(omsRealOrderReq.getAuditStatus())){
                othObjectDto.setObject(omsRealOrderReq);
                otherDtos.add(othObjectDto);
            }
            sucObjectDto.setObject(omsRealOrderReq);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos,errDtos,otherDtos);
    }
}
