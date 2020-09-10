package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.feign.RemoteActOmsOrderMaterialOutService;
import com.cloud.activiti.feign.RemoteActTaskService;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.order.domain.entity.*;
import com.cloud.order.domain.entity.vo.OmsDemandOrderGatherEditExport;
import com.cloud.order.enums.DemandOrderGatherEditAuditStatusEnum;
import com.cloud.order.enums.DemandOrderGatherEditStatusEnum;
import com.cloud.order.enums.OrderFromEnum;
import com.cloud.order.enums.ProductTypeOrderEnum;
import com.cloud.order.mapper.OmsDemandOrderGatherEditMapper;
import com.cloud.order.service.IOmsDemandOrderGatherEditHisService;
import com.cloud.order.service.IOmsDemandOrderGatherEditImportService;
import com.cloud.order.service.IOmsDemandOrderGatherEditService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.*;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.feign.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.conn.jco.*;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 滚动计划需求操作 Service业务层处理
 *
 * @author cs
 * @date 2020-06-16
 */
@Slf4j
@Service
public class OmsDemandOrderGatherEditServiceImpl extends BaseServiceImpl<OmsDemandOrderGatherEdit> implements IOmsDemandOrderGatherEditService, IOmsDemandOrderGatherEditImportService {
    @Autowired
    private OmsDemandOrderGatherEditMapper omsDemandOrderGatherEditMapper;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;
    @Autowired
    private RemoteMaterialService remoteMaterialService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private IOmsDemandOrderGatherEditHisService omsDemandOrderGatherEditHisService;
    @Autowired
    private IOmsDemandOrderGatherEditImportService omsDemandOrderGatherEditImportService;
    @Autowired
    private RemoteProductStockService remoteProductStockService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    @Autowired
    private RemoteActOmsOrderMaterialOutService remoteActOmsOrderMaterialOutService;
    @Autowired
    private RemoteFactoryStorehouseInfoService remoteFactoryStorehouseInfoService;
    @Autowired
    private RemoteActTaskService remoteActTaskService;

    private static final String TABLE_NAME = "oms_demand_order_gather_edit";

    @Override
    public R updateWithLimit(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        if (omsDemandOrderGatherEdit == null ||
                omsDemandOrderGatherEdit.getId() == null
                ||omsDemandOrderGatherEdit.getOrderNum()==null) {
            return R.error("参数为空！");
        }
        OmsDemandOrderGatherEdit omsDemandOrderGatherEditNew = selectByPrimaryKey(omsDemandOrderGatherEdit.getId());
        String status = omsDemandOrderGatherEditNew.getStatus();
        List<String> UnAllowStatusList = CollUtil.newArrayList(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode(),
                DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPZ.getCode(),
                DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP.getCode());
        if (CollUtil.contains(UnAllowStatusList, status)) {
            return R.error("此状态数据不允许修改");
        }
        omsDemandOrderGatherEditNew.setOrderNum(omsDemandOrderGatherEdit.getOrderNum());
        if (StrUtil.equals(status, DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getCode())) {
            omsDemandOrderGatherEditNew.setStatus(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode());
        }
        omsDemandOrderGatherEditNew.setUpdateBy(omsDemandOrderGatherEdit.getUpdateBy());
        int i=updateByPrimaryKeySelective(omsDemandOrderGatherEditNew);
        return i > 0 ? R.ok() : R.error();
    }

    /**
     * 带逻辑删除
     * @param ids
     * @return
     */
    @Override
    @GlobalTransactional
    public R deleteWithLimit(String ids,OmsDemandOrderGatherEdit omsDemandOrderGatherEditVo,SysUser sysUser) {
        Example example = new Example(Oms2weeksDemandOrderEdit.class);
        Example.Criteria criteria = example.createCriteria();
        List<OmsDemandOrderGatherEdit> omsDemandOrderGatherEditList ;
        List<String> canStatus = CollectionUtil.newArrayList(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode(),
                DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getCode());
        if (StrUtil.isEmpty(ids)) {
            if(!sysUser.isAdmin()&&CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            }
            if(!sysUser.isAdmin()&&CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_SCBJL)){
                criteria.andEqualTo("orderFrom", OrderFromEnum.OUT_SOURCE_TYPE_QWW.getCode());
            }
            listCondition(omsDemandOrderGatherEditVo,criteria);
            criteria.andIn("status", canStatus);
            omsDemandOrderGatherEditList = selectByExample(example);
            if (CollectionUtil.isEmpty(omsDemandOrderGatherEditList)) {
                return R.error("无可删除数据！");
            }
            List idList = omsDemandOrderGatherEditList.stream().map(OmsDemandOrderGatherEdit::getId).collect(Collectors.toList());
            ids = CollectionUtil.join(idList, StrUtil.COMMA);
        }else{
            List<String> list = CollUtil.newArrayList(ids.split(StrUtil.COMMA));
            example.and().andIn("id", list);
            List<OmsDemandOrderGatherEdit> listAll = selectByExample(example);
            if (CollectionUtil.isEmpty(listAll)) {
                return R.error("无可删除数据！");
            }
            Boolean bo=listAll.stream()
                    .anyMatch(s -> !CollectionUtil.contains(canStatus,s.getStatus()));
            if(bo){
                return R.error("非初始或传SAP异常状态的数据不允许删除！");
            }
            omsDemandOrderGatherEditList = listAll;
        }
        if (CollectionUtil.isNotEmpty(omsDemandOrderGatherEditList)) {
            List<String> orderCodeList = omsDemandOrderGatherEditList.stream()
                    .map(OmsDemandOrderGatherEdit::getDemandOrderCode).collect(toList());
            Map<String,Object> map = new HashMap<>();
            map.put("userName",sysUser.getLoginName());
            map.put("orderCodeList",orderCodeList);
            R deleteActMap = remoteActTaskService.deleteByOrderCode(map);
            if (!deleteActMap.isSuccess()){
                log.error("删除审批流程失败，原因："+deleteActMap.get("msg"));
                throw new BusinessException("删除审批流程失败，原因："+deleteActMap.get("msg"));
            }
        }
        deleteByIdsWL(ids);
        return R.ok();
    }

    /**
     * 确认下达
     * @param ids
     * @return
     */
    @Override
    public R confirmRelease(String ids,OmsDemandOrderGatherEdit omsDemandOrderGatherEditParam,SysUser sysUser) {
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        Example.Criteria criteria = example.createCriteria();
        //允许下达的审核状态
        List<String> auditStatusList = CollUtil.newArrayList(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH.getCode()
                ,DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHWC.getCode());
        if (StrUtil.isEmpty(ids)) {
            if(!sysUser.isAdmin()&&CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            }
            if(!sysUser.isAdmin()&&CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_SCBJL)){
                criteria.andEqualTo("orderFrom", OrderFromEnum.OUT_SOURCE_TYPE_QWW.getCode());
            }
            //如果参数为空，则查询初始状态,无需审核、审核完成的数据
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEditParam.getStatus())) {
                if (StrUtil.equals(omsDemandOrderGatherEditParam.getStatus(), DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode())) {
                    criteria.andEqualTo("status", DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
                } else {
                    return R.error(StrUtil.format("只允许状态为{}的数据下达!",DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getMsg()));
                }
            }
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEditParam.getAuditStatus())) {
                if (CollUtil.contains(auditStatusList, omsDemandOrderGatherEditParam.getAuditStatus())) {
                    criteria.andEqualTo("auditStatus", omsDemandOrderGatherEditParam.getAuditStatus());
                }else{
                    return R.error(StrUtil.format("[{}]审核状态数据不允许下达！",DemandOrderGatherEditStatusEnum.getMsgByCode(omsDemandOrderGatherEditParam.getAuditStatus())));
                }
            } else {
                criteria.andIn("auditStatus", auditStatusList);
            }
            listCondition(omsDemandOrderGatherEditParam,criteria);
        }else{
            //查询参数id的数据
            List<String> list = CollUtil.newArrayList(ids.split(StrUtil.COMMA));
            criteria.andIn("id", list);
        }
        List<OmsDemandOrderGatherEdit> list = selectByExample(example);
        if (CollUtil.isEmpty(list)) {
            return R.error("无数据需要下达！");
        }
        for (OmsDemandOrderGatherEdit omsDemandOrderGatherEdit : list) {
            String status = omsDemandOrderGatherEdit.getStatus();
            String auditStatus = omsDemandOrderGatherEdit.getAuditStatus();
            if (!StrUtil.equals(status, DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode())) {
                return R.error(StrUtil.format("此状态数据不允许确认下达！需求订单号：{}",omsDemandOrderGatherEdit.getDemandOrderCode()));
            }else if(!CollUtil.contains(auditStatusList,auditStatus)) {
                return R.error(StrUtil.format("此审核状态数据不允许确认下达！需求订单号：{}",omsDemandOrderGatherEdit.getDemandOrderCode()));
            }
            omsDemandOrderGatherEdit.setStatus(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode());
            omsDemandOrderGatherEdit.setUpdateBy(sysUser.getLoginName());
        }
        updateBatchByPrimaryKeySelective(list);
        return R.ok();
    }

    /**
     * Example查询时的条件
     * @param omsDemandOrderGatherEdit
     * @return
     */
    void listCondition(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,Example.Criteria criteria){
        if (omsDemandOrderGatherEdit == null) {
            return;
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode",omsDemandOrderGatherEdit.getProductMaterialCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",omsDemandOrderGatherEdit.getProductFactoryCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getCustomerCode())) {
            criteria.andEqualTo("customerCode",omsDemandOrderGatherEdit.getCustomerCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getOrderFrom())) {
            criteria.andEqualTo("orderFrom",omsDemandOrderGatherEdit.getOrderFrom() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductType())) {
            criteria.andEqualTo("productType",omsDemandOrderGatherEdit.getProductType() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getLifeCycle())) {
            criteria.andEqualTo("lifeCycle",omsDemandOrderGatherEdit.getLifeCycle() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("deliveryDate",omsDemandOrderGatherEdit.getBeginTime() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getEndTime())) {
            criteria.andLessThanOrEqualTo("deliveryDate", DateUtil.parse(omsDemandOrderGatherEdit.getEndTime()).offset(DateField.DAY_OF_MONTH,1) );
        }
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
        //其他导入数据
        List<ExcelImportOtherObjectDto> otherDtos = new ArrayList<>();

        List<OmsDemandOrderGatherEditImport> listImport = (List<OmsDemandOrderGatherEditImport>) objects;
        List<OmsDemandOrderGatherEdit> list= listImport.stream().map(demandOrderGatherEditIMport ->
                BeanUtil.copyProperties(demandOrderGatherEditIMport,OmsDemandOrderGatherEdit.class)).collect(Collectors.toList());

        //因数量较大，一次性取出cd_material_info物料描述，cd_material_extend_info生命周期，cd_factory_info工厂编码
        R rFactoryList=remoteFactoryInfoService.getAllFactoryCode();
        if (!rFactoryList.isSuccess()) {
            throw new BusinessException("无工厂信息，请到基础信息维护！");
        }
        List<String> factoryCodeList = rFactoryList.getCollectData(new TypeReference<List<String>>() {});
        //取导入数据的所有物料号
        List<String> materialCodeList=list.stream().map(demandOrderGatherEdit->{
            return demandOrderGatherEdit.getProductMaterialCode();
        }).distinct().collect(Collectors.toList());
        //查询导入数据的物料扩展信息
        R rMateiralExt = remoteMaterialExtendInfoService.selectInfoInMaterialCodes(materialCodeList);
        if (!rMateiralExt.isSuccess()) {
            throw new BusinessException("导入数据的物料扩展信息无数据，请到基础信息维护！");
        }
        Map<String, CdMaterialExtendInfo> materialExtendInfoMap = rMateiralExt.getCollectData(new TypeReference<Map<String, CdMaterialExtendInfo>>() {});
        //key:物料号 value：物料信息
        //MaterialTypeEnum.WLLX_HALB.getCode()
        R rMaterial = remoteMaterialService.selectInfoByInMaterialCodeAndMaterialType(materialCodeList, null);
        if (!rMaterial.isSuccess()) {
            throw new BusinessException("导入数据的物料信息无数据，请到基础信息维护！");
        }
        Map<String, List<CdMaterialInfo>> mapMaterial = rMaterial.getCollectData(new TypeReference<Map<String, List<CdMaterialInfo>>>() {});
        //需要审核的数据list
        List<OmsDemandOrderGatherEdit> listNeedAudti = CollUtil.newArrayList();
        Date date = DateUtil.date();

        //获取不重复的物料号和工厂Map
        List<Dict> maps = list.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("customerCode",s.getCustomerCode())).distinct().collect(Collectors.toList());
        //获取库位
        R rStoreHouse = remoteFactoryStorehouseInfoService.selectStorehouseToMap(maps);
        if(!rStoreHouse.isSuccess()){
            throw new BusinessException("获取接收库位失败！");
        }
        Map<String, Map<String, String>> storehouseMap = rStoreHouse.getCollectData(new TypeReference<Map<String, Map<String, String>>>() {});
        if (MapUtil.isEmpty(storehouseMap)) {
            throw new BusinessException("获取接收库位失败！");
        }

        //数据版本
        int nowYear = DateUtil.year(date);
        int nowWeek = DateUtil.weekOfYear(date);
        if (DateUtil.dayOfWeek(date)==1) {
            nowWeek = nowWeek+1;
        }
        String version = StrUtil.concat(true,StrUtil.toString(nowYear),StrUtil.toString(nowWeek) );
        //查询已导入数据
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        example.and().andNotEqualTo("status",DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
        List<OmsDemandOrderGatherEdit> hisList = omsDemandOrderGatherEditMapper.selectByExample(example);
        for(OmsDemandOrderGatherEdit demandOrderGatherEdit:list){
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();
            ExcelImportOtherObjectDto othObjectDto = new ExcelImportOtherObjectDto();
            StringBuffer errMsg = new StringBuffer();

            boolean flag = hisList.stream().anyMatch(s -> StrUtil.equals(s.getProductMaterialCode(), demandOrderGatherEdit.getProductMaterialCode())
                    && StrUtil.equals(s.getCustomerCode(), demandOrderGatherEdit.getCustomerCode())
                    && StrUtil.equals(s.getProductFactoryCode(), demandOrderGatherEdit.getProductFactoryCode())
                    && StrUtil.equals(s.getBomVersion(), demandOrderGatherEdit.getBomVersion())
                    && StrUtil.equals(s.getVersion(), version)
                    && DateUtil.compare(s.getDeliveryDate(), demandOrderGatherEdit.getDeliveryDate()) == 0);
            if (flag) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg("非初始状态数据不允许重复导入！");
                errDtos.add(errObjectDto);
                continue;
            }
            //交付日期
            Date dateDelivery = demandOrderGatherEdit.getDeliveryDate();
            //判断交付日期是否是T+1和T+2周
            if (dateDelivery.compareTo(date) > 0) {
                int nowWeekNum = DateUtil.weekOfYear(date);
                int deliveryWeekNum = DateUtil.weekOfYear(dateDelivery);
                if (DateUtil.dayOfWeek(dateDelivery)==1) {
                    deliveryWeekNum += 1;
                }
                if (DateUtil.dayOfWeek(date)==1) {
                    nowWeekNum += 1;
                }
                int subNum=deliveryWeekNum - nowWeekNum;
                Boolean bo = false;
                if (subNum < 3) {
                    bo = true;
                }
                if (bo) {
                    errObjectDto.setObject(demandOrderGatherEdit);
                    errObjectDto.setErrMsg("不能导入T+1、T+2周数据");
                    errDtos.add(errObjectDto);
                    continue;
                }
            } else {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg("交付日期不能是当前或历史周");
                errDtos.add(errObjectDto);
                continue;
            }

            String factoryCode = demandOrderGatherEdit.getProductFactoryCode();
            if (!CollUtil.contains(factoryCodeList, factoryCode)) {
                errMsg.append(StrUtil.format("不存在此工厂：{};", factoryCode));
            }
            //物料描述赋值
            List<CdMaterialInfo> materialInfos = mapMaterial.get(demandOrderGatherEdit.getProductMaterialCode());
            if (CollUtil.isEmpty(materialInfos)) {
                errMsg.append(StrUtil.format("不存在物料信息：{};", demandOrderGatherEdit.getProductMaterialCode()));
            }else{
                //根据工厂、物料号获取单一对象
                Optional<CdMaterialInfo> cdMaterialInfoOpt = materialInfos.stream()
                        .filter(ma -> StrUtil.equals(ma.getPlantCode(), factoryCode))
                        .findFirst();
                if (!cdMaterialInfoOpt.isPresent()) {
                    errMsg.append(StrUtil.format("不存在物料号：{},工厂：{}的物料数据，请维护！",
                            demandOrderGatherEdit.getProductMaterialCode(), factoryCode));
                } else {
                    CdMaterialInfo cdMaterialInfo = cdMaterialInfoOpt.get();
                    demandOrderGatherEdit.setProductMaterialDesc(cdMaterialInfo.getMaterialDesc());
                    demandOrderGatherEdit.setPurchaseGroupCode(cdMaterialInfo.getPurchaseGroupCode());
                    demandOrderGatherEdit.setUnit(cdMaterialInfo.getPrimaryUom());
                }
            }
            String seq = RandomUtil.randomNumbers(6)+ com.cloud.common.utils.RandomUtil.randomInt(2);
            String demandOrderCode = StrUtil.concat(true, "DM", DateUtils.dateTime(), seq);
            demandOrderGatherEdit.setDemandOrderCode(demandOrderCode);
            //订单来源
            demandOrderGatherEdit.setOrderFrom(OrderFromEnum.getCodeByMsg(demandOrderGatherEdit.getOrderFrom()));
            //年
            Date deliveryDate = demandOrderGatherEdit.getDeliveryDate();
            int year = DateUtil.year(deliveryDate);
            demandOrderGatherEdit.setYear(StrUtil.toString(year));
            //周
            //key:生产工厂、客户编码、成品物料号、交付日期,周数
            int weekNum  = DateUtil.weekOfYear(deliveryDate);
            //判断今天是不是周天，如果是周天，则周数+1
            if (DateUtil.dayOfWeek(deliveryDate)==1) {
                weekNum = weekNum+1;
            }
            demandOrderGatherEdit.setWeeks(StrUtil.toString(weekNum));
            demandOrderGatherEdit.setStatus(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
            //判断地点是否存在
            Map<String, String> storeHouseMap = storehouseMap.get(StrUtil.concat(true, demandOrderGatherEdit.getProductFactoryCode(), demandOrderGatherEdit.getCustomerCode()));
            if (storeHouseMap == null) {
                errMsg.append(StrUtil.format("工厂：{}，客户编码{}无工厂库位信息！",demandOrderGatherEdit.getProductFactoryCode(),demandOrderGatherEdit.getCustomerCode()));
            }
            if (storeHouseMap != null && !StrUtil.equals(demandOrderGatherEdit.getPlace(), storeHouseMap.get("storehouseTo"))) {
                errMsg.append(StrUtil.format("对应地点应为：{};", storeHouseMap.get("storehouseTo")));
            }

            //判断是否下市，下市则进入审批
            CdMaterialExtendInfo extendInfo = materialExtendInfoMap.get(demandOrderGatherEdit.getProductMaterialCode());
            String productType = new String();
            String lifeCyle = new String();
            if(extendInfo!=null){
                productType = extendInfo.getProductType();
                lifeCyle = extendInfo.getLifeCycle();
            }
//            if (extendInfo==null) {
//                errMsg.append(StrUtil.format("物料号{}：无生命周期信息！",demandOrderGatherEdit.getProductMaterialCode()));
//            }else{

                //2020/09/09 王姐要求放开
//                if (StrUtil.isEmpty(productType)) {
//                    errMsg.append(StrUtil.format("物料号{}：无产品类别信息！", demandOrderGatherEdit.getProductMaterialCode()));
//                }
//                if (StrUtil.isEmpty(lifeCyle)) {
//                    errMsg.append(StrUtil.format("物料号{}：无生命周期信息！", demandOrderGatherEdit.getProductMaterialCode()));
//                }
//            }
            if (StrUtil.isNotEmpty(errMsg)) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(errMsg.toString());
                errDtos.add(errObjectDto);
                continue;
            }
            demandOrderGatherEdit.setProductType(productType);
            demandOrderGatherEdit.setLifeCycle(lifeCyle);
            if (StrUtil.equals(LifeCycleEnum.SMZQ_XS.getCode(),lifeCyle)) {
                //已下市
                demandOrderGatherEdit.setAuditStatus(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHZ.getCode());
                othObjectDto.setObject(demandOrderGatherEdit);
                otherDtos.add(othObjectDto);
            }else{
                demandOrderGatherEdit.setAuditStatus(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH.getCode());
            }
            demandOrderGatherEdit.setCreateTime(date);
            demandOrderGatherEdit.setDelFlag("0");
            sucObjectDto.setObject(demandOrderGatherEdit);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos,errDtos,otherDtos);
    }

//    public static void main(String[] args) {
//        Set<String> s = new HashSet<>();
//        ArrayUtil.rand
//        for (int i = 0; i < 10500; i++) {
//            s.add(RandomUtil.randomNumbers(6)+ com.cloud.common.utils.RandomUtil.randomInt(2));
//        }
//        Console.log(s.size());
//    }

    /**
     * 需求数据导入
     * @param successList 成功结果集
     * @param auditList 需要审核的结果集
     * @return
     */
    @Override
    public R importDemandGatherEdit(List<OmsDemandOrderGatherEdit> successList,List<OmsDemandOrderGatherEdit> auditList, SysUser sysUser) {
        //1、判断工厂编码
        //2、取物料描述
        //3、判断生命周期
        //4、如果是上周数据，则全部插入历史表，删除原有的，插入新的，如果是新的，则根据登录人、客户编码删除原有的，插入新的
        if (CollUtil.isEmpty(successList)) {
            return R.error("无需要插入的数据！");
        }
        Date date = DateUtil.date();
        //数据版本
        int nowYear = DateUtil.year(date);
        int nowWeek = DateUtil.weekOfYear(date);
        if (DateUtil.dayOfWeek(date)==1) {
            nowWeek = nowWeek+1;
        }
        String version = StrUtil.concat(true,StrUtil.toString(nowYear),StrUtil.toString(nowWeek) );
        successList.forEach(dto->{
            dto.setVersion(version);
            dto.setCreateBy(sysUser.getLoginName());
        });

        //获取表里数据的数据版本
        List<OmsDemandOrderGatherEdit> demandOrderGatherEditOlds=omsDemandOrderGatherEditMapper.selectAll();
        if (!CollUtil.isEmpty(demandOrderGatherEditOlds)) {
            OmsDemandOrderGatherEdit demandOrderGatherEditOld = CollUtil.getFirst(demandOrderGatherEditOlds);
            String versionOld = demandOrderGatherEditOld.getVersion();
            if (StrUtil.equals(version, versionOld)) {
                //相同周：根据登录人、客户编码删除原有的，插入新的
                List<String> customerList = successList.stream()
                        .filter(dto -> StrUtil.equals(sysUser.getLoginName(), dto.getCreateBy()))
                        .map(dtoMap-> dtoMap.getCustomerCode()).distinct().collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(customerList)) {
                    deleteByCreateByAndCustomerCode(sysUser.getLoginName(), customerList,DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
                }
            }else{
                //上周：全部插入历史表，删除原有的，插入新的
                List<OmsDemandOrderGatherEditHis> listHis= demandOrderGatherEditOlds.stream().map(demandOrderGatherEdit ->
                        BeanUtil.copyProperties(demandOrderGatherEdit,OmsDemandOrderGatherEditHis.class)).collect(Collectors.toList());
                omsDemandOrderGatherEditHisService.insertList(listHis);
                deleteByCreateByAndCustomerCode(null,null,null);
            }
        }
        insertList(successList);

        //下市数据进入审批 审批数据：successList 筛选状态是审核中的
        if(!CollectionUtils.isEmpty(successList)){
            log.info("开启下市审批流");
            OmsOrderMaterialOutVo auditResultReq = new OmsOrderMaterialOutVo();
            List<OmsOrderMaterialOutVo> omsOrderMaterialOutVoList = new ArrayList<>();
            successList.forEach(omsDemandOrderGatherEdit -> {
                if(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHZ.getCode()
                        .equals(omsDemandOrderGatherEdit.getAuditStatus())){
                    OmsOrderMaterialOutVo omsOrderMaterialOutVo = new OmsOrderMaterialOutVo();
                    omsOrderMaterialOutVo.setLoginId(sysUser.getUserId());
                    omsOrderMaterialOutVo.setCreateBy(sysUser.getLoginName());
                    omsOrderMaterialOutVo.setOrderCode(omsDemandOrderGatherEdit.getDemandOrderCode());
                    omsOrderMaterialOutVo.setId(omsDemandOrderGatherEdit.getId());
                    omsOrderMaterialOutVo.setTableName(TABLE_NAME);
                    omsOrderMaterialOutVo.setFactoryCode(omsDemandOrderGatherEdit.getProductFactoryCode());
                    omsOrderMaterialOutVoList.add(omsOrderMaterialOutVo);
                }
            });
            if(!CollectionUtils.isEmpty(omsOrderMaterialOutVoList)){
                auditResultReq.setOmsOrderMaterialOutVoList(omsOrderMaterialOutVoList);
                R auditResultR = remoteActOmsOrderMaterialOutService.addSave(auditResultReq);
                if(!auditResultR.isSuccess()){
                    log.error("下市的数据开启审批流失败 e:{}",auditResultR.toString());
                    throw new BusinessException("下市的数据开启审批流失败");
                }
            }
        }
        return R.ok();
    }

    /**
     * 根据创建人和客户编码删除
     * @param createBy
     * @param customerCodes
     * @return
     */
	@Override
	public int deleteByCreateByAndCustomerCode(String createBy,List<String> customerCodes,String status){
        if (createBy != null && customerCodes != null && status != null) {
            //查询是否有已开启审批的数据
            Example example = new Example(OmsDemandOrderGatherEdit.class);
            example.and().andEqualTo("createBy", createBy)
                    .andIn("customerCode", customerCodes)
                    .andEqualTo("status", status);
            List<OmsDemandOrderGatherEdit> list = selectByExample(example);
            if (CollectionUtil.isNotEmpty(list)) {
                List<String> orderCodeList = list.stream()
                        .map(OmsDemandOrderGatherEdit::getDemandOrderCode).collect(toList());
                Map<String,Object> map = new HashMap<>();
                map.put("userName",createBy);
                map.put("orderCodeList",orderCodeList);
                R deleteActMap = remoteActTaskService.deleteByOrderCode(map);
                if (!deleteActMap.isSuccess()){
                    log.error("删除审批流程失败，原因："+deleteActMap.get("msg"));
                    throw new BusinessException("删除审批流程失败，原因："+deleteActMap.get("msg"));
                }
            }
        }
		 return omsDemandOrderGatherEditMapper.deleteByCreateByAndCustomerCode(createBy,customerCodes,status);
	}


    @Override
    @SneakyThrows
    @GlobalTransactional
    public R importDemandGatherEdit(MultipartFile file,SysUser sysUser) {
        ProductTypeOrderEnum.init();
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(omsDemandOrderGatherEditImportService,OmsDemandOrderGatherEditImport.class);
        EasyExcel.read(file.getInputStream(),OmsDemandOrderGatherEditImport.class,easyExcelListener).sheet().doRead();
        //需要审核的结果
        List<ExcelImportOtherObjectDto> auditList=easyExcelListener.getOtherList();
        List<OmsDemandOrderGatherEdit> auditResult = new ArrayList<>();
        if (auditList.size() > 0){
            auditResult =auditList.stream().map(excelImportAuditObjectDto -> {
                OmsDemandOrderGatherEdit demandOrderGatherEdit = BeanUtil.copyProperties(excelImportAuditObjectDto.getObject(), OmsDemandOrderGatherEdit.class);
                return demandOrderGatherEdit;
            }).collect(Collectors.toList());
        }
        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (CollectionUtil.isNotEmpty(successList) && CollectionUtil.isEmpty(errList)) {
            List<OmsDemandOrderGatherEdit> successResult = successList.stream().map(excelImportSucObjectDto -> {
                OmsDemandOrderGatherEdit demandOrderGatherEdit = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(), OmsDemandOrderGatherEdit.class);
                return demandOrderGatherEdit;
            }).collect(Collectors.toList());
            importDemandGatherEdit(successResult, auditResult, sysUser);
        }
        //错误结果集 导出
        if (errList.size() > 0){
            List<OmsDemandOrderGatherEditImport> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                OmsDemandOrderGatherEditImport omsDemandOrderGatherEditImport = BeanUtil.copyProperties(excelImportErrObjectDto.getObject(), OmsDemandOrderGatherEditImport.class);
                omsDemandOrderGatherEditImport.setErrorMsg(excelImportErrObjectDto.getErrMsg());
                return omsDemandOrderGatherEditImport;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "需求导入错误信息.xlsx", "sheet", new OmsDemandOrderGatherEditImport());
        }
        return R.ok();
    }

    /**
     * 13周滚动需求汇总分页查询
     * @param listDistant 不重复的物料和工厂
     * @return
     */
    @Override
    public R week13DemandGatherList(List<OmsDemandOrderGatherEdit> listDistant) {
        //包括周的数据
        List<OmsDemandOrderGatherEdit> listAll = omsDemandOrderGatherEditMapper.selectInfoInMaterialCodeAndFactoryCode(listDistant);
        //根据物料号，工厂分组
        Map<String,List<OmsDemandOrderGatherEdit>> mapGroup=listAll.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKey(e)));

        List<OmsDemandOrderGatherEdit> listReturn = new ArrayList<>();
        Date date = DateUtil.date();
        int weekNum  = DateUtil.weekOfYear(date);
        //判断今天是不是周天，如果是周天，则周数+1
        if (DateUtil.dayOfWeek(date)==1) {
            weekNum += 1;
        }
        //从T+3周开始
        int gatherWeek = weekNum + 3;
        int[] weekRange= NumberUtil.range(gatherWeek, gatherWeek+10);
        mapGroup.forEach((keyCode,list)->{
            List<WeekAndNumGatherDTO> weekNumList = new ArrayList<>();
            //取第一条数据作为返回的一行数据
            OmsDemandOrderGatherEdit dto = CollUtil.getFirst(list);
            //key为周 value为订单量
            Map<String,Long> mapNum=list.stream().collect(Collectors.groupingBy(
                    OmsDemandOrderGatherEdit::getWeeks,Collectors.summingLong(OmsDemandOrderGatherEdit::getOrderNum)));
            //11周数据
            for (int week : weekRange) {
                String weekStr = StrUtil.toString(week);
                WeekAndNumGatherDTO weekInfo = new WeekAndNumGatherDTO().builder()
                        .weeks(weekStr).orderNum(mapNum.get(weekStr)==null?0L:mapNum.get(weekStr)).build();
                //开始日期  结束日期
                int year = DateUtil.thisYear();
                int thisWeek = DateUtil.thisWeekOfYear();
                //如果thisWeek比week大，则week属于下一年
                if (NumberUtil.compare(week, thisWeek) < 0) {
                    year += 1;
                }
                Map<String,String> mapDateRange=DateUtils.weekToDayFormate(year, week);
                weekInfo.setStartDate(mapDateRange.get("startDate"));
                weekInfo.setEndDate(mapDateRange.get("endDate"));
                weekNumList.add(weekInfo);
            }
            dto.setWeekDataList(weekNumList);
            listReturn.add(dto);
        });
        return R.data(listReturn);
    }

    /**
     * 13周需求组合key
     * @param cd
     * @return
     */
    private static String fetchGroupKey(OmsDemandOrderGatherEdit cd){
        return StrUtil.concat(true, cd.getProductMaterialCode(), StrUtil.COMMA
                ,cd.getProductFactoryCode());
    }

    /**
     * 成品库存组合key
     * @param cd
     * @return
     */
    private static String fetchGroupKey(CdProductStock cd){
        return StrUtil.concat(true, cd.getProductMaterialCode(), StrUtil.COMMA
                ,cd.getProductFactoryCode());
    }

    /**
     * 查询不重复的物料号和工厂
     * @param omsDemandOrderGatherEdit
     * @return
     */
    @Override
    public List<OmsDemandOrderGatherEdit> selectDistinctMaterialCodeAndFactoryCode(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,SysUser sysUser) {
        //如果是排产员，需要加上工厂权限
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            List<String> factoryList=Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(","));
            if (CollUtil.isNotEmpty(factoryList)) {
                omsDemandOrderGatherEdit.setProductFactoryList(factoryList);
            }
        }
        List<OmsDemandOrderGatherEdit> list = omsDemandOrderGatherEditMapper.selectDistinctMaterialCodeAndFactoryCode(omsDemandOrderGatherEdit);
        return list;
    }

    /**
     * 13周滚动需求汇总 导出
     * @param omsDemandOrderGatherEdit
     * @param sysUser
     * @return
     */
    @Override
    public R week13DemandGatherExport(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit,SysUser sysUser) {
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode",omsDemandOrderGatherEdit.getProductMaterialCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",omsDemandOrderGatherEdit.getProductFactoryCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getOrderFrom())) {
            criteria.andEqualTo("orderFrom",omsDemandOrderGatherEdit.getOrderFrom() );
        }
        //如果是排产员，需要加上工厂权限
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            example.and().andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
        }
        List<OmsDemandOrderGatherEdit> omsDemandOrderGatherEditList = selectByExample(example);
        if(CollUtil.isEmpty(omsDemandOrderGatherEditList)){
            return R.error("导出数据为空!");
        }
        Date date = DateUtil.date();
        int weekNum  = DateUtil.weekOfYear(date);
        //判断今天是不是周天，如果是周天，则周数+1
        if (DateUtil.dayOfWeek(date)==1) {
            weekNum += 1;
        }
        //从T+3周开始
        int gatherWeek = weekNum + 3;
        //所有周数 11周
        int[] weekRange= NumberUtil.range(gatherWeek, gatherWeek+10);
        //查出全部数据了
        Map<String,List<OmsDemandOrderGatherEdit>> mapGroup=omsDemandOrderGatherEditList.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKey(e)));
        //查询库存
        List<Dict> maps = omsDemandOrderGatherEditList.stream().map(s -> new Dict().set("productFactoryCode",s.getProductFactoryCode())
                .set("productMaterialCode",s.getProductMaterialCode())).distinct().collect(Collectors.toList());
        R rProStock = remoteProductStockService.selectProductStockToMap(maps);
        if (!rProStock.isSuccess()) {
            return rProStock;
        }
        //导出数据有关的工厂、专用号 成品库存信息
        List<CdProductStock> listProStock=rProStock.getCollectData(new TypeReference<List<CdProductStock>>() {});
        Map<String,List<CdProductStock>> mapProStock=listProStock.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKey(e)));
        List<OmsDemandOrderGatherEditExport> listExport = new ArrayList<>();
        //根据工厂和物料号分组了
        mapGroup.forEach((key,list)->{
            List<CdProductStock> stockList=mapProStock.get(key);
            BigDecimal stockNum = BigDecimal.ZERO;
            if (CollUtil.isNotEmpty(stockList)) {
                CdProductStock stock = CollUtil.getFirst(stockList);
                BigDecimal stockWNum = stock.getStockWNum();//在库
                BigDecimal stockINum = stock.getStockINum();//在途
                BigDecimal stockPNum = stock.getStockPNum();//在产
                BigDecimal stockKNum = stock.getStockKNum();//寄售不足
                stockNum = stockWNum.add(stockINum.add(stockPNum).subtract(stockKNum));
            }
            //map key:周  value:数量
            Map<String,Long> mapNum=list.stream().collect(Collectors.groupingBy(
                    OmsDemandOrderGatherEdit::getWeeks,Collectors.summingLong(OmsDemandOrderGatherEdit::getOrderNum)));
            OmsDemandOrderGatherEdit dto = CollUtil.getFirst(list);
            OmsDemandOrderGatherEditExport export = BeanUtil.copyProperties(dto,OmsDemandOrderGatherEditExport.class);
            int index = 0;
            Long totalDemandNum = 0L;
            for (int week : weekRange) {
                String weekStr = StrUtil.toString(week);
                String setIndexStr = StrUtil.toString(index + 3);
                Long orderNum = 0L;
                if (mapNum.get(weekStr) != null) {
                    orderNum = mapNum.get(weekStr);
                }
                totalDemandNum = totalDemandNum+orderNum;
                try {
                    Method method = OmsDemandOrderGatherEditExport.class.getMethod(StrUtil.format("setT{}Num",setIndexStr),Long.class);
                    method.invoke(export ,orderNum);
                } catch (IllegalAccessException e) {
                    throw new BusinessException("系统拥挤，请稍后再试！（Invoke）");
                } catch (InvocationTargetException e) {
                    throw new BusinessException("系统拥挤，请稍后再试！（Invoke）");
                }catch (NoSuchMethodException e) {
                    throw new BusinessException("系统拥挤，请稍后再试！（Invoke）");
                }
                index++;
            }
            export.setTotalDemandNum(totalDemandNum);
            export.setStockNum(stockNum);
            listExport.add(export);
        });
        List<List<String>> headList = headList(weekRange);
         return EasyExcelUtilOSS.writeExcelWithHead(listExport, "13周滚动需求汇总.xlsx", "sheet", new OmsDemandOrderGatherEditExport(),headList);
    }


    /**
     * 13周需求导出表头数据
     * @param weekRange
     * @return
     */
    List<List<String>> headList(int[] weekRange) {
        List<List<String>> headList = new ArrayList<List<String>>();
        List<String> headTitle0 = new ArrayList<String>();
        List<String> headTitle1 = new ArrayList<String>();
        List<String> headTitle2 = new ArrayList<String>();
        List<String> headTitle3 = new ArrayList<String>();
        List<String> headTitle4 = new ArrayList<String>();
        List<String> headTitle5 = new ArrayList<String>();
        headTitle0.add("专用号");
        headTitle1.add("专用号描述");
        headTitle2.add("客户编码");
        headTitle3.add("客户名称");
        headTitle4.add("生产工厂");
        headTitle5.add("单位");
        headList.add(headTitle0);
        headList.add(headTitle1);
        headList.add(headTitle2);
        headList.add(headTitle3);
        headList.add(headTitle4);
        headList.add(headTitle5);

        for (int week : weekRange) {
            List<String> headTitle6 = new ArrayList<String>();
            String weekStr = StrUtil.toString(week);
            //开始日期  结束日期
            int year = DateUtil.thisYear();
            int thisWeek = DateUtil.thisWeekOfYear();
            //如果thisWeek比week大，则week属于下一年
            if (NumberUtil.compare(week, thisWeek) < 0) {
                year += 1;
            }
            Map<String,String> mapDateRange=DateUtils.weekToDayFormate(year, week);
            String startDate = mapDateRange.get("startDate");
            String endDate = mapDateRange.get("endDate");
            Date sDate = DateUtils.string2Date(startDate, DateUtils.YYYY_MM_DD);
            Date eDate = DateUtils.string2Date(endDate, DateUtils.YYYY_MM_DD);
            startDate = DateUtils.parseDateToStr(DateUtils.MM_dd, sDate);
            endDate = DateUtils.parseDateToStr(DateUtils.MM_dd, eDate);
            headTitle6.add(StrUtil.format("{}-{}",startDate,endDate));
            headList.add(headTitle6);
        }
        List<String> headTitle17 = new ArrayList<String>();
        List<String> headTitle18 = new ArrayList<String>();
        headTitle17.add("需求总量");
        headTitle18.add("库存量");
        headList.add(headTitle17);
        headList.add(headTitle18);
        return headList;
    }

    /**
     * 下达SAP(13周需求下达SAP创建生产订单)
     * @param ids
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R toSAP(List<Long> ids,SysUser sysUser,OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        //只能下达待传SAP和传SAP异常的数据
        List<String> statusList = CollUtil.newArrayList(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode()
                ,DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getCode());
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        if (CollUtil.isEmpty(ids)) {
            Example.Criteria criteria = example.createCriteria();
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductMaterialCode())) {
                criteria.andEqualTo("productMaterialCode",omsDemandOrderGatherEdit.getProductMaterialCode() );
            }
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductFactoryCode())) {
                criteria.andEqualTo("productFactoryCode",omsDemandOrderGatherEdit.getProductFactoryCode() );
            }
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getStatus())) {
                if (CollectionUtil.contains(statusList, omsDemandOrderGatherEdit.getStatus())) {
                    criteria.andEqualTo("status", omsDemandOrderGatherEdit.getStatus());
                } else {
                    return R.error(StrUtil.format("只允许状态为{}与{}的数据下达SAP"
                            ,DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getMsg()
                            ,DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getMsg()));
                }
            }else{
                criteria.andIn("status",statusList );
            }
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getBeginTime())) {
                criteria.andGreaterThanOrEqualTo("deliveryDate",omsDemandOrderGatherEdit.getBeginTime() );
            }
            if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getEndTime())) {
                criteria.andLessThanOrEqualTo("deliveryDate", omsDemandOrderGatherEdit.getEndTime() );
            }
        }else{
            example.and().andIn("id", ids);
        }
        List<OmsDemandOrderGatherEdit> demandOrderGatherEdits=selectByExample(example);
        if (CollectionUtil.isEmpty(demandOrderGatherEdits)) {
            return R.error("无符合条件下达SAP的数据！");
        }
        boolean checkBo = demandOrderGatherEdits.stream().allMatch(demandOrderGatherEdit -> statusList.contains(demandOrderGatherEdit.getStatus()));
        if (!checkBo) {
            return R.error(StrUtil.format("只允许下达状态为：{}或{}的数据",
                    DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getMsg(),
                    DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getMsg()));
        }
        ids = demandOrderGatherEdits.stream().map(wo -> wo.getId()).collect(Collectors.toList());
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName(SapConstants.ZPP_INT_DDPS_04)
                .content(StrUtil.format("参数为oms_demand_order_gather_edit表id：{}",CollUtil.join(ids, "#"))).build();
        //检查数据状态
        try {
            //创建与SAP的连接
            JCoDestination destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_04);
            if (fm == null) {
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoTable inputTable = fm.getTableParameterList().getTable("INPUT");
            demandOrderGatherEdits.forEach(demandOrderGatherEdit->{
                //附加表的最后一个新行,行指针,它指向新添加的行。
                inputTable.appendRow();
                inputTable.setValue("XQDDH",demandOrderGatherEdit.getDemandOrderCode());//唯一单号
                inputTable.setValue("PAART", demandOrderGatherEdit.getOrderType());//订单类型
                inputTable.setValue("MATNR", demandOrderGatherEdit.getProductMaterialCode().toUpperCase());//物料号
                inputTable.setValue("PWWRK", demandOrderGatherEdit.getProductFactoryCode());//工厂
                inputTable.setValue("BERID", demandOrderGatherEdit.getMrpRange());//MRP 范围
                inputTable.setValue("VERID", demandOrderGatherEdit.getBomVersion());//生产版本
                inputTable.setValue("GSMNG", demandOrderGatherEdit.getOrderNum());//数量
                inputTable.setValue("PSTTR", demandOrderGatherEdit.getDeliveryDate());//计划订单上的订单开始日期
                inputTable.setValue("LGORT", demandOrderGatherEdit.getPlace());//库存地点
            });
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //返回成功的数据
                List<OmsDemandOrderGatherEdit> successList = new ArrayList<>();
                //返回失败的数据
                List<OmsDemandOrderGatherEdit> failList = new ArrayList<>();
                StringBuffer sapBuffer = new StringBuffer();
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    String demandCode = outTableOutput.getString("XQDDH");//唯一单号
                    String flag = outTableOutput.getString("FLAG");
                    String message = outTableOutput.getString("MESSAGE");
                    String plnum = outTableOutput.getString("PLNUM");//计划订单号
                    String messageOne = StrUtil.format("XQDDH:{},FLAG:{},MESSAGE:{},PLNUM:{};"
                            ,demandCode,flag,message,plnum);
                    sapBuffer.append(messageOne);
                    if (SapConstants.SAP_RESULT_TYPE_SUCCESS.equals(flag)) {
                        //成功：状态改为已传SAP，更新计划订单号
                        OmsDemandOrderGatherEdit edit = new OmsDemandOrderGatherEdit().builder()
                                .demandOrderCode(demandCode)
                                .status(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP.getCode())
                                .sapMessages(message)
                                .planOrderOrder(plnum).build();
                        edit.setUpdateBy(sysUser.getLoginName());
                        successList.add(edit);
                    }else{
                        //失败：更新状态为传SAP异常
                        OmsDemandOrderGatherEdit edit = new OmsDemandOrderGatherEdit().builder()
                                .demandOrderCode(demandCode)
                                .status(DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getCode())
                                .sapMessages(message)
                                .planOrderOrder(plnum).build();
                        edit.setUpdateBy(sysUser.getLoginName());
                        failList.add(edit);
                    }
                }
                sysInterfaceLog.setResults(sapBuffer.toString());
                if (CollUtil.isNotEmpty(successList)) {
                    omsDemandOrderGatherEditMapper.updateBatchByDemandOrderCode(successList);
                }
                if (CollUtil.isNotEmpty(failList)) {
                    omsDemandOrderGatherEditMapper.updateBatchByDemandOrderCode(failList);
                }
                return R.ok(StrUtil.format("下达SAP成功：{}条，异常：{}条",
                        successList==null?0:successList.size(),
                        failList==null?0:failList.size()));
            }
        } catch (JCoException e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }finally {
            sysInterfaceLog.setCreateBy(sysUser.getLoginName());
            sysInterfaceLog.setCreateTime(DateUtil.date());
            sysInterfaceLog.setRemark("下达SAP(13周需求下达SAP创建生产订单)");
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
        return R.ok();
    }

    /**
     * 根据需求订单号批量更新
     * @param list
     * @return
     */
	@Override
	public int updateBatchByDemandOrderCode(List<OmsDemandOrderGatherEdit> list){
		 return omsDemandOrderGatherEditMapper.updateBatchByDemandOrderCode(list);
	}
}
