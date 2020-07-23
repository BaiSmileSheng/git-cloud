package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.feign.RemoteActOmsOrderMaterialOutService;
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
import com.cloud.order.domain.entity.Oms2weeksDemandOrder;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEditHis;
import com.cloud.order.domain.entity.vo.DayAndNumsGatherVO;
import com.cloud.order.domain.entity.vo.Oms2weeksDemandOrderEditExportVO;
import com.cloud.order.domain.entity.vo.Oms2weeksDemandOrderEditImport;
import com.cloud.order.enums.*;
import com.cloud.order.mapper.Oms2weeksDemandOrderEditMapper;
import com.cloud.order.mapper.Oms2weeksDemandOrderMapper;
import com.cloud.order.service.IOms2weeksDemandOrderEditHisService;
import com.cloud.order.service.IOms2weeksDemandOrderEditImportService;
import com.cloud.order.service.IOms2weeksDemandOrderEditService;
import com.cloud.order.service.IOms2weeksDemandOrderService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.enums.MaterialTypeEnum;
import com.cloud.system.feign.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.conn.jco.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * T+1-T+2周需求导入 Service业务层处理
 *
 * @author cs
 * @date 2020-06-22
 */
@Service
@Slf4j
public class Oms2weeksDemandOrderEditServiceImpl extends BaseServiceImpl<Oms2weeksDemandOrderEdit> implements IOms2weeksDemandOrderEditService ,IOms2weeksDemandOrderEditImportService{
    @Autowired
    private Oms2weeksDemandOrderEditMapper oms2weeksDemandOrderEditMapper;
    @Autowired
    private Oms2weeksDemandOrderMapper oms2weeksDemandOrderMapper;
    @Autowired
    private IOms2weeksDemandOrderEditImportService oms2weeksDemandOrderEditImportService;
    @Autowired
    private RemoteMaterialService remoteMaterialService;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private IOms2weeksDemandOrderEditHisService oms2weeksDemandOrderEditHisService;
    @Autowired
    private IOms2weeksDemandOrderService oms2weeksDemandOrderService;

    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;

    @Autowired
    private RemoteActOmsOrderMaterialOutService remoteActOmsOrderMaterialOutService;

    private static final String TABLE_NAME = "oms2weeks_demand_order_edit";//对应的表名

    /**
     * T+1、T+2草稿计划导入
     * @param file
     * @return
     */
    @Override
    @SneakyThrows
    @Transactional
    public R import2weeksDemandEdit(MultipartFile file, SysUser sysUser) {
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(oms2weeksDemandOrderEditImportService, Oms2weeksDemandOrderEditImport.class);
        EasyExcel.read(file.getInputStream(),Oms2weeksDemandOrderEditImport.class,easyExcelListener).sheet().doRead();
        //需要审核的结果
        List<ExcelImportOtherObjectDto> auditList=easyExcelListener.getOtherList();
        List<Oms2weeksDemandOrderEdit> auditResult = new ArrayList<>();
        if (auditList.size() > 0){
            auditResult =auditList.stream().map(excelImportAuditObjectDto -> {
                Oms2weeksDemandOrderEdit weeksDemandOrderEdit = BeanUtil.copyProperties(excelImportAuditObjectDto.getObject(), Oms2weeksDemandOrderEdit.class);
                return weeksDemandOrderEdit;
            }).collect(Collectors.toList());
        }
        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (successList.size() > 0){
            List<Oms2weeksDemandOrderEdit> successResult =successList.stream().map(excelImportSucObjectDto -> {
                Oms2weeksDemandOrderEdit weeksDemandOrderEdit = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(), Oms2weeksDemandOrderEdit.class);
                return weeksDemandOrderEdit;
            }).collect(Collectors.toList());
            import2weeksDemandEdit(successResult,auditResult,sysUser);
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (errList.size() > 0){
            List<Oms2weeksDemandOrderEditImport> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                Oms2weeksDemandOrderEditImport oms2weeksDemandOrderEditImport = BeanUtil.copyProperties(excelImportErrObjectDto.getObject(), Oms2weeksDemandOrderEditImport.class);
                oms2weeksDemandOrderEditImport.setErrorMsg(excelImportErrObjectDto.getErrMsg());
                return oms2weeksDemandOrderEditImport;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "T+1、T+2草稿计划导入错误信息.xlsx", "sheet", new Oms2weeksDemandOrderEditImport());
        }
        return R.ok();
    }

    /**
     * T+1、T+2草稿计划汇总
     * @param successList 成功结果集
     * @param auditList 需要审核的结果集
     * @return
     */
    @Override
    @Transactional
    public R import2weeksDemandEdit(List<Oms2weeksDemandOrderEdit> successList, List<Oms2weeksDemandOrderEdit> auditList, SysUser sysUser) {
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
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEdits=oms2weeksDemandOrderEditMapper.selectAll();
        if (!CollUtil.isEmpty(oms2weeksDemandOrderEdits)) {
            Oms2weeksDemandOrderEdit weeksDemandOrderEditOld = CollUtil.getFirst(oms2weeksDemandOrderEdits);
            String versionOld = weeksDemandOrderEditOld.getVersion();
            if (StrUtil.equals(version, versionOld)) {
                //相同周：根据登录人、客户编码删除原有的，插入新的
                List<String> customerList = oms2weeksDemandOrderEdits.stream()
                        .filter(dto -> StrUtil.equals(sysUser.getLoginName(), dto.getCreateBy()))
                        .map(dtoMap-> dtoMap.getCustomerCode()).distinct().collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(customerList)) {
                    deleteByCreateByAndCustomerCode(weeksDemandOrderEditOld.getCreateBy(), customerList);
                }
            }else{
                //上周：全部插入历史表，删除原有的，插入新的
                List<Oms2weeksDemandOrderEditHis> listHis= oms2weeksDemandOrderEdits.stream().map(weeksDemandOrderEdit ->
                        BeanUtil.copyProperties(weeksDemandOrderEdit,Oms2weeksDemandOrderEditHis.class)).collect(Collectors.toList());
                oms2weeksDemandOrderEditHisService.insertList(listHis);
                deleteByCreateByAndCustomerCode(null,null);
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
    public int deleteByCreateByAndCustomerCode(String createBy, List<String> customerCodes) {
        return oms2weeksDemandOrderEditMapper.deleteByCreateByAndCustomerCode(createBy,customerCodes);
    }


    /**
     * 导入数据检查
     * @param objects
     * @param <T>
     * @return
     */
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

        List<Oms2weeksDemandOrderEditImport> listImport = (List<Oms2weeksDemandOrderEditImport>) objects;
        List<Oms2weeksDemandOrderEdit> list= listImport.stream().map(oms2weeksDemandOrderEditImport ->
                BeanUtil.copyProperties(oms2weeksDemandOrderEditImport,Oms2weeksDemandOrderEdit.class)).collect(Collectors.toList());

        //因数量较大，一次性取出cd_material_info物料描述，cd_material_extend_info生命周期，cd_factory_info公司编码
        R rCompanyList=remoteFactoryInfoService.getAllCompanyCode();
        if (!rCompanyList.isSuccess()) {
            throw new BusinessException("无工厂信息，请到基础信息维护！");
        }
        List<String> companyCodeList = rCompanyList.getCollectData(new TypeReference<List<String>>() {});
        //取导入数据的所有物料号
        List<String> materialCodeList=list.stream().map(weeksDemandOrderEdit->{
            return weeksDemandOrderEdit.getProductMaterialCode();
        }).distinct().collect(Collectors.toList());
        //查询导入数据的物料扩展信息
        R rMateiralExt = remoteMaterialExtendInfoService.selectInfoInMaterialCodes(materialCodeList);
        if (!rMateiralExt.isSuccess()) {
            throw new BusinessException("导入数据的物料扩展信息无数据，请到基础信息维护！");
        }
        Map<String, CdMaterialExtendInfo> materialExtendInfoMap = rMateiralExt.getCollectData(new TypeReference<Map<String, CdMaterialExtendInfo>>() {});
        //key:物料号 value：物料信息
        R rMaterial = remoteMaterialService.selectInfoByInMaterialCodeAndMaterialType(materialCodeList, MaterialTypeEnum.WLLX_HALB.getCode());
        if (!rMaterial.isSuccess()) {
            throw new BusinessException("导入数据的物料信息无数据，请到基础信息维护！");
        }
        Map<String, List<CdMaterialInfo>> mapMaterial = rMaterial.getCollectData(new TypeReference<Map<String, List<CdMaterialInfo>>>() {});
        //需要审核的数据list
        List<Oms2weeksDemandOrderEdit> listNeedAudti = CollUtil.newArrayList();
        Date date = DateUtil.date();

        for(Oms2weeksDemandOrderEdit weeksDemandOrderEdit:list){
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();
            ExcelImportOtherObjectDto othObjectDto = new ExcelImportOtherObjectDto();

            //交付日期
            Date dateDelivery = weeksDemandOrderEdit.getDeliveryDate();
            //判断是否是T+1，T+2之外的数据
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
                if (subNum >= 3) {
                    bo = true;
                }
                if (bo) {
                    errObjectDto.setObject(weeksDemandOrderEdit);
                    errObjectDto.setErrMsg("不能导入T+1、T+2周之外的数据");
                    errDtos.add(errObjectDto);
                    continue;
                }
            } else {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg("交付日期不能是当前或历史周");
                errDtos.add(errObjectDto);
                continue;
            }

            String factoryCode = weeksDemandOrderEdit.getProductFactoryCode();
            if (!CollUtil.contains(companyCodeList, factoryCode)) {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg(StrUtil.format("不存在此工厂：{}", factoryCode));
                errDtos.add(errObjectDto);
                continue;
            }
            //物料描述赋值
            List<CdMaterialInfo> materialInfos = mapMaterial.get(weeksDemandOrderEdit.getProductMaterialCode());
            if (CollUtil.isEmpty(materialInfos)) {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg(StrUtil.format("不存在物料信息：{}", weeksDemandOrderEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            //根据工厂、物料号获取单一对象
            Optional<CdMaterialInfo> cdMaterialInfoOpt = materialInfos.stream()
                    .filter(ma -> StrUtil.equals(ma.getPlantCode(), factoryCode))
                    .findFirst();
            if (!cdMaterialInfoOpt.isPresent()) {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg(StrUtil.format("不存在物料号：{},工厂：{}的物料数据，请维护！",
                        weeksDemandOrderEdit.getProductMaterialCode(),factoryCode));
                errDtos.add(errObjectDto);
                continue;
            }
            CdMaterialInfo cdMaterialInfo = cdMaterialInfoOpt.get();
            weeksDemandOrderEdit.setProductMaterialDesc(cdMaterialInfo.getMaterialDesc());
            weeksDemandOrderEdit.setPurchaseGroupCode(cdMaterialInfo.getPurchaseGroupCode());
            weeksDemandOrderEdit.setUnit(cdMaterialInfo.getPrimaryUom());
            //需求订单号
            R seqresult = remoteSequeceService.selectSeq("2weeks_demand_order_seq", 4);
            if(!seqresult.isSuccess()){
                throw new BusinessException("查序列号失败");
            }
            String seq = seqresult.getStr("data");
            String demandOrderCode = StrUtil.concat(true, "DM", DateUtils.dateTime(), seq);
            weeksDemandOrderEdit.setDemandOrderCode(demandOrderCode);
            //订单来源
            weeksDemandOrderEdit.setOrderFrom(OrderFromEnum.getCodeByMsg(weeksDemandOrderEdit.getOrderFrom()));
            //年
            Date deliveryDate = weeksDemandOrderEdit.getDeliveryDate();
            int year = DateUtil.year(deliveryDate);
            weeksDemandOrderEdit.setYear(StrUtil.toString(year));
            //周
            //key:生产工厂、客户编码、成品物料号、交付日期,周数
            int weekNum  = DateUtil.weekOfYear(deliveryDate);
            //判断今天是不是周天，如果是周天，则周数+1
            if (DateUtil.dayOfWeek(deliveryDate)==1) {
                weekNum = weekNum+1;
            }
            weeksDemandOrderEdit.setWeeks(StrUtil.toString(weekNum));
            weeksDemandOrderEdit.setStatus(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
            //判断是否下市，下市则进入审批
            CdMaterialExtendInfo extendInfo = materialExtendInfoMap.get(weeksDemandOrderEdit.getProductMaterialCode());
            if (extendInfo==null) {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg(StrUtil.format("物料号{}：无生命周期及产品类别信息！",weeksDemandOrderEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            String productType = extendInfo.getProductType();
            String lifeCyle = extendInfo.getLifeCycle();
            if (StrUtil.isEmpty(productType)) {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg(StrUtil.format("物料号{}：无产品类别信息！",weeksDemandOrderEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            } else if (StrUtil.isEmpty(lifeCyle)) {
                errObjectDto.setObject(weeksDemandOrderEdit);
                errObjectDto.setErrMsg(StrUtil.format("物料号{}：无生命周期信息！",weeksDemandOrderEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            weeksDemandOrderEdit.setProductType(productType);
            weeksDemandOrderEdit.setLifeCycle(lifeCyle);
            if (StrUtil.equals(LifeCycleEnum.SMZQ_XS.getCode(),lifeCyle)) {
                //已下市
                weeksDemandOrderEdit.setAuditStatus(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHZ.getCode());
                othObjectDto.setObject(weeksDemandOrderEdit);
                otherDtos.add(othObjectDto);
            }else{
                weeksDemandOrderEdit.setAuditStatus(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH.getCode());
            }
            weeksDemandOrderEdit.setCreateTime(date);
            weeksDemandOrderEdit.setDelFlag("0");
            sucObjectDto.setObject(weeksDemandOrderEdit);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos,errDtos,otherDtos);
    }

    /**
     * 带逻辑更新
     * @param oms2weeksDemandOrderEdit
     * @return
     */
    @Override
    public R updateWithLimit(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        if (oms2weeksDemandOrderEdit == null ||
                oms2weeksDemandOrderEdit.getId() == null
                ||oms2weeksDemandOrderEdit.getOrderNum()==null) {
            return R.error("参数为空！");
        }
        Oms2weeksDemandOrderEdit oms2weeksDemandOrderEditNew = selectByPrimaryKey(oms2weeksDemandOrderEdit.getId());
        String status = oms2weeksDemandOrderEditNew.getStatus();
        List<String> UnAllowStatusList = CollUtil.newArrayList(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode(),
                Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPZ.getCode(),
                Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP.getCode());
        if (CollUtil.contains(UnAllowStatusList, status)) {
            return R.error("此状态数据不允许修改");
        }
        oms2weeksDemandOrderEditNew.setOrderNum(oms2weeksDemandOrderEdit.getOrderNum());
        if (StrUtil.equals(status, Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getCode())) {
            oms2weeksDemandOrderEditNew.setStatus(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode());
        }
        int i=updateByPrimaryKeySelective(oms2weeksDemandOrderEditNew);
        return i > 0 ? R.ok() : R.error();
    }

    /**
     * 带逻辑删除
     * @param ids
     * @return
     */
    @Override
    public R deleteWithLimit(String ids) {
        if (StrUtil.isEmpty(ids)) {
            return R.error("参数为空！");
        }
        List<String> list = CollUtil.newArrayList(ids.split(StrUtil.COMMA));
        for (String id : list) {
            Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit = selectByPrimaryKey(Long.valueOf(id));
            if (!StrUtil.equals(oms2weeksDemandOrderEdit.getStatus()
                    , DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP.getCode())||
            StrUtil.equals(oms2weeksDemandOrderEdit.getAuditStatus()
                    , DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHZ.getCode())) {
                return R.error(StrUtil.format("此状态数据不允许删除！需求订单号：{}",oms2weeksDemandOrderEdit.getDemandOrderCode()));
            }
        }
        deleteByIds(ids);
        return R.ok();
    }

    /**
     * 带逻辑删除已下达SAP数据
     * @param ids
     * @return
     */
    @Override
    public R deleteWithLimitXDSAP(String ids) {
        if (StrUtil.isEmpty(ids)) {
            return R.error("参数为空！");
        }
        List<String> list = CollUtil.newArrayList(ids.split(StrUtil.COMMA));
        for (String id : list) {
            Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit = selectByPrimaryKey(Long.valueOf(id));
            if (!StrUtil.equals(oms2weeksDemandOrderEdit.getStatus()
                    , DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP.getCode())) {
                return R.error(StrUtil.format("此状态数据不允许删除！需求订单号：{}",oms2weeksDemandOrderEdit.getDemandOrderCode()));
            }
        }
        deleteByIds(ids);
        return R.ok();
    }

    /**
     * 确认下达
     * @param ids
     * @return
     */
    @Override
    public R confirmRelease(String ids) {
        Example example = new Example(Oms2weeksDemandOrderEdit.class);
        Example.Criteria criteria = example.createCriteria();
        //允许下达的审核状态
        List<String> auditStatusList = CollUtil.newArrayList(Weeks2DemandOrderEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH.getCode()
                ,Weeks2DemandOrderEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHWC.getCode());
        if (StrUtil.isEmpty(ids)) {
            //如果参数为空，则查询初始状态,无需审核、审核完成的数据
            criteria.andEqualTo("status", Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
            criteria.andIn("auditStatus", auditStatusList);
        }else{
            //查询参数id的数据
            List<String> list = CollUtil.newArrayList(ids.split(StrUtil.COMMA));
            criteria.andIn("id", list);
        }
        List<Oms2weeksDemandOrderEdit> list = selectByExample(example);
        if (CollUtil.isEmpty(list)) {
            return R.error("无数据需要下达！");
        }
        for (Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit : list) {
            String status = oms2weeksDemandOrderEdit.getStatus();
            String auditStatus = oms2weeksDemandOrderEdit.getAuditStatus();
            if (!StrUtil.equals(status, Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode())) {
                return R.error(StrUtil.format("此状态数据不允许确认下达！需求订单号：{}",oms2weeksDemandOrderEdit.getDemandOrderCode()));
            }else if(!CollUtil.contains(auditStatusList,auditStatus)) {
                return R.error(StrUtil.format("此审核状态数据不允许确认下达！需求订单号：{}",oms2weeksDemandOrderEdit.getDemandOrderCode()));
            }
            oms2weeksDemandOrderEdit.setStatus(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode());
        }
        updateBatchByPrimaryKeySelective(list);
        return R.ok();
    }

    /**
     * 查询不重复的物料号和工厂
     * @param oms2weeksDemandOrderEdit
     * @return
     */
    public List<Oms2weeksDemandOrderEdit> selectDistinctMaterialCodeAndFactoryCode(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit, SysUser sysUser) {
        //如果是排产员，需要加上工厂权限
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            List<String> factoryList=Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(","));
            if (CollUtil.isNotEmpty(factoryList)) {
                oms2weeksDemandOrderEdit.setProductFactoryList(factoryList);
            }
        }
        List<Oms2weeksDemandOrderEdit> list = oms2weeksDemandOrderEditMapper.selectDistinctMaterialCodeAndFactoryCode(oms2weeksDemandOrderEdit);
        return list;
    }

    /**
     * T+1、T+2草稿计划对比分析分页查询
     * @param listDistant 不重复的物料和工厂
     * @return
     */
    @Override
    public R t1t2GatherList(List<Oms2weeksDemandOrderEdit> listDistant) {
        //t+1,t+2手工导入数据
        List<Oms2weeksDemandOrderEdit> listAllArtificial = oms2weeksDemandOrderEditMapper.selectInfoInMaterialCodeAndFactoryCode(listDistant);
        //不重复的物料和工厂
        List<Oms2weeksDemandOrder> listDistantInterface= listDistant.stream().map(oms2weeksDemandOrderEdit ->
                        BeanUtil.copyProperties(oms2weeksDemandOrderEdit,Oms2weeksDemandOrder.class)).collect(Collectors.toList());
        //t+1,t+2接口数据
        List<Oms2weeksDemandOrder> listAllInterface = oms2weeksDemandOrderMapper.selectInfoInMaterialCodeAndFactoryCode(listDistantInterface);

        //根据物料号，工厂分组(t+1,t+2手工导入)
        Map<String,List<Oms2weeksDemandOrderEdit>> mapGroupArtificial=listAllArtificial.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKeyArtificial(e)));

        //根据物料号，工厂分组(t+1,t+2接口)
        Map<String,List<Oms2weeksDemandOrder>> mapGroupInterface=listAllInterface.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKeyInterface(e)));

        List<Oms2weeksDemandOrderEdit> listReturn = new ArrayList<>();
        Date date = DateUtil.date();
        Date startDate;
        Date endDate;
        //判断今天是不是周天，如果是周天，则开始时间为下个周天,否则为这个周天
        if (DateUtil.dayOfWeek(date)==1) {
            startDate=DateUtil.endOfWeek(DateUtil.nextWeek());
        }else {
            startDate=DateUtil.endOfWeek(date);
        }
        endDate = DateUtil.offsetDay(startDate, 13);
        List<DateTime> listDate=DateUtil.rangeToList(startDate,endDate, DateField.DAY_OF_YEAR);
        mapGroupArtificial.forEach((keyCode,listArtificial)->{
            //listArtificial为手工导入列表
            //listInterface为接口列表
            List<Oms2weeksDemandOrder> listInterface = mapGroupInterface.get(keyCode);
            List<DayAndNumsGatherVO> dayNumList = new ArrayList<>();
            //取第一条数据作为返回的一行数据
            Oms2weeksDemandOrderEdit dto = CollUtil.getFirst(listArtificial);
            //key为交付日期 value为订单量
            Map<String,Long> mapNumArtificial=listArtificial.stream().collect(Collectors.groupingBy(
                    Oms2weeksDemandOrderEdit::getDeliveryDateStr,Collectors.summingLong(Oms2weeksDemandOrderEdit::getOrderNum)));
            Map<String, Long> mapNumInterface = null;
            if (CollectionUtil.isNotEmpty(listInterface)) {
                mapNumInterface=listInterface.stream().collect(Collectors.groupingBy(
                        Oms2weeksDemandOrder::getDeliveryDateStr,Collectors.summingLong(Oms2weeksDemandOrder::getOrderNum)));
            }
            //11周数据
            for (Date day : listDate) {
                String dayStr = DateUtil.formatDate(day);
                Long interfaceNum = 0L;
                if (mapNumInterface != null) {
                    interfaceNum = mapNumInterface.get(dayStr)==null?0L:mapNumInterface.get(dayStr);
                }
                Long artificialNum = mapNumArtificial.get(dayStr)==null?0L:mapNumArtificial.get(dayStr);
                Long differenceNum = interfaceNum.longValue()-artificialNum.longValue();
                DayAndNumsGatherVO numInfo = new DayAndNumsGatherVO().builder()
                        .day(dayStr).interfaceNum(interfaceNum)
                        .artificialNum(artificialNum)
                        .differenceNum(differenceNum).build();
                dayNumList.add(numInfo);
            }
            dto.setDayDataList(dayNumList);
            listReturn.add(dto);
        });
        return R.data(listReturn);
    }

    /**
     * T+1、T+2草稿计划对比分析 导出
     * @param oms2weeksDemandOrderEdit
     * @param sysUser
     * @return
     */
    @Override
    public R t1t2GatherListExport(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit, SysUser sysUser) {
        Example exampleEdit = new Example(Oms2weeksDemandOrderEdit.class);
        Example.Criteria criteriaEdit = exampleEdit.createCriteria();
        Example example = new Example(Oms2weeksDemandOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getProductMaterialCode())) {
            criteriaEdit.andEqualTo("productMaterialCode",oms2weeksDemandOrderEdit.getProductMaterialCode() );
            criteria.andEqualTo("productMaterialCode",oms2weeksDemandOrderEdit.getProductMaterialCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getProductFactoryCode())) {
            criteriaEdit.andEqualTo("productFactoryCode",oms2weeksDemandOrderEdit.getProductFactoryCode() );
            criteria.andEqualTo("productFactoryCode",oms2weeksDemandOrderEdit.getProductFactoryCode() );
        }
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            criteriaEdit.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
        }
        //手动导入数据
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList = selectByExample(exampleEdit);
        //接口数据
        List<Oms2weeksDemandOrder> oms2weeksDemandOrderList = oms2weeksDemandOrderService.selectByExample(exampleEdit);
        //根据物料号，工厂分组(t+1,t+2手工导入)
        Map<String,List<Oms2weeksDemandOrderEdit>> mapGroupArtificial=oms2weeksDemandOrderEditList.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKeyArtificial(e)));

        //根据物料号，工厂分组(t+1,t+2接口)
        Map<String,List<Oms2weeksDemandOrder>> mapGroupInterface=oms2weeksDemandOrderList.stream()
                .collect(Collectors.groupingBy(e -> fetchGroupKeyInterface(e)));
        List<Oms2weeksDemandOrderEditExportVO> listReturn = new ArrayList<>();
        Date date = DateUtil.date();
        Date startDate;
        Date endDate;
        //判断今天是不是周天，如果是周天，则开始时间为下个周天,否则为这个周天
        if (DateUtil.dayOfWeek(date)==1) {
            startDate=DateUtil.endOfWeek(DateUtil.nextWeek());
        }else {
            startDate=DateUtil.endOfWeek(date);
        }
        endDate = DateUtil.offsetDay(startDate, 13);
        List<DateTime> listDate=DateUtil.rangeToList(startDate,endDate, DateField.DAY_OF_YEAR);
        mapGroupArtificial.forEach((keyCode,listArtificial)->{
            //listArtificial为手工导入列表
            //listInterface为接口列表
            List<Oms2weeksDemandOrder> listInterface = mapGroupInterface.get(keyCode);

            //取第一条数据作为返回的一行数据
            Oms2weeksDemandOrderEdit dto = CollUtil.getFirst(listArtificial);
            //key为交付日期 value为订单量
            Map<String,Long> mapNumArtificial=listArtificial.stream().collect(Collectors.groupingBy(
                    Oms2weeksDemandOrderEdit::getDeliveryDateStr,Collectors.summingLong(Oms2weeksDemandOrderEdit::getOrderNum)));
            Map<String, Long> mapNumInterface = null;
            if (CollectionUtil.isNotEmpty(listInterface)) {
                mapNumInterface=listInterface.stream().collect(Collectors.groupingBy(
                        Oms2weeksDemandOrder::getDeliveryDateStr,Collectors.summingLong(Oms2weeksDemandOrder::getOrderNum)));
            }
            //11周数据
            for (Date day : listDate) {
                String dayStr = DateUtil.formatDate(day);
                Long interfaceNum = 0L;
                if (mapNumInterface != null) {
                    interfaceNum = mapNumInterface.get(dayStr)==null?0L:mapNumInterface.get(dayStr);
                }
                Long artificialNum = mapNumArtificial.get(dayStr)==null?0L:mapNumArtificial.get(dayStr);
                Long differenceNum = Math.abs((interfaceNum.longValue()-artificialNum.longValue()));
                Oms2weeksDemandOrderEditExportVO info = new Oms2weeksDemandOrderEditExportVO().builder()
                        .day(dayStr).interfaceNum(interfaceNum)
                        .artificialNum(artificialNum)
                        .differenceNum(differenceNum)
                        .productMaterialCode(dto.getProductMaterialCode())
                        .productFactoryCode(dto.getProductFactoryCode())
                        .unit(dto.getUnit()).build();
                listReturn.add(info);
            }
        });
        return EasyExcelUtilOSS.writeExcel(listReturn, "T+1、T+2草稿计划对比分析.xlsx", "sheet", new Oms2weeksDemandOrderEditExportVO());
    }

    /**
     *  手工导入组合key
     * @param cd
     * @return
     */
    private static String fetchGroupKeyArtificial(Oms2weeksDemandOrderEdit cd){
        return StrUtil.concat(true, cd.getProductMaterialCode(), StrUtil.COMMA
                ,cd.getProductFactoryCode());
    }
    /**
     *  T+1、T+2草稿计划对比分析组合key
     * @param cd
     * @return
     */
    private static String fetchGroupKeyInterface(Oms2weeksDemandOrder cd){
        return StrUtil.concat(true, cd.getProductMaterialCode(), StrUtil.COMMA
                ,cd.getProductFactoryCode());
    }


    /**
     * 下达SAP 2周需求传SAP
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public R toSAP(List<Long> ids,SysUser sysUser,Oms2weeksDemandOrderEdit weeksDemandOrderEdit) {
        //只能下达待传SAP和传SAP异常的数据
        List<String> statusList = CollUtil.newArrayList(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getCode()
                ,Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getCode());
        Example example = new Example(Oms2weeksDemandOrderEdit.class);
        if (CollUtil.isEmpty(ids)) {
            Example.Criteria criteria = example.createCriteria();
            if (StrUtil.isNotEmpty(weeksDemandOrderEdit.getProductMaterialCode())) {
                criteria.andEqualTo("productMaterialCode",weeksDemandOrderEdit.getProductMaterialCode() );
            }
            if (StrUtil.isNotEmpty(weeksDemandOrderEdit.getProductFactoryCode())) {
                criteria.andEqualTo("productFactoryCode",weeksDemandOrderEdit.getProductFactoryCode() );
            }
            if (StrUtil.isNotEmpty(weeksDemandOrderEdit.getStatus())) {
                if (CollectionUtil.contains(statusList, weeksDemandOrderEdit.getStatus())) {
                    criteria.andEqualTo("status", weeksDemandOrderEdit.getStatus());
                } else {
                    return R.error(StrUtil.format("只允许状态为{}与{}的数据下达SAP"
                            ,Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getMsg()
                            ,Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getMsg()));
                }
            }else{
                criteria.andIn("status",statusList );
            }
            if (StrUtil.isNotEmpty(weeksDemandOrderEdit.getBeginTime())) {
                criteria.andGreaterThanOrEqualTo("deliveryDate",weeksDemandOrderEdit.getBeginTime() );
            }
            if (StrUtil.isNotEmpty(weeksDemandOrderEdit.getEndTime())) {
                criteria.andLessThanOrEqualTo("deliveryDate", weeksDemandOrderEdit.getEndTime() );
            }
        }else{
            example.and().andIn("id", ids);
        }
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList=selectByExample(example);
        boolean checkBo = oms2weeksDemandOrderEditList.stream().allMatch(oms2weeksDemandOrderEdit -> statusList.contains(oms2weeksDemandOrderEdit.getStatus()));
        if (!checkBo) {
            return R.error(StrUtil.format("只允许下达状态为：{}或{}的数据",
                    Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_DCSAP.getMsg(),
                    Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPYC.getMsg()));
        }
        if (CollectionUtil.isEmpty(oms2weeksDemandOrderEditList)) {
            return R.error("无符合条件下达SAP的数据！");
        }
        ids = oms2weeksDemandOrderEditList.stream().map(wo -> wo.getId()).collect(Collectors.toList());
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName(SapConstants.ZPP_INT_DDPS_02)
                .content(StrUtil.format("参数为oms2weeks_demand_order_edit表id：{}",CollUtil.join(ids, "#"))).build();
        //检查数据状态
        List<Oms2weeksDemandOrderEdit> successList = new ArrayList<>();
        try {
            //创建与SAP的连接
            JCoDestination destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_02);
            if (fm == null) {
                log.error("================2周需求下达SAP传生产订单信息接口函数为空================");
                throw new RuntimeException("2周需求下达SAP传生产订单信息接口函数为空");
            }
            //获取输入参数
            JCoTable inputTable = fm.getTableParameterList().getTable("INPUT");
            oms2weeksDemandOrderEditList.forEach(oms2weeksDemandOrderEdit->{
                //附加表的最后一个新行,行指针,它指向新添加的行。
                inputTable.appendRow();
                inputTable.setValue("MATNR", oms2weeksDemandOrderEdit.getProductMaterialCode().toUpperCase());
                inputTable.setValue("WERKS", oms2weeksDemandOrderEdit.getProductFactoryCode());
                inputTable.setValue("AUART", oms2weeksDemandOrderEdit.getOrderType());
//                inputTable.setValue("GSTRP", oms2weeksDemandOrderEdit.getProductStartDate());
//                inputTable.setValue("GLTRP", oms2weeksDemandOrderEdit.getProductEndDate());
                inputTable.setValue("GAMNG", oms2weeksDemandOrderEdit.getOrderNum());
                inputTable.setValue("LGORT", oms2weeksDemandOrderEdit.getPlace());
                inputTable.setValue("ABLAD", oms2weeksDemandOrderEdit.getDemandOrderCode());
//                inputTable.setValue("CY_SEQNR", oms2weeksDemandOrderEdit.getProductLineCode());
                inputTable.setValue("VERID", oms2weeksDemandOrderEdit.getBomVersion());
            });
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //循环取table行数据
                StringBuffer sapBuffer = new StringBuffer();
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit = new Oms2weeksDemandOrderEdit();
                    oms2weeksDemandOrderEdit.setDemandOrderCode(outTableOutput.getString("ABLAD"));//排产订单号
                    oms2weeksDemandOrderEdit.setSapMessages(outTableOutput.getString("MESSAGE"));
                    oms2weeksDemandOrderEdit.setStatus(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPZ.getCode());
                    successList.add(oms2weeksDemandOrderEdit);
                    String messageOne = StrUtil.format("ABLAD:{},MESSAGE:{};"
                            ,outTableOutput.getString("ABLAD"),outTableOutput.getString("MESSAGE"));
                    sapBuffer.append(messageOne);
                }
                sysInterfaceLog.setResults(sapBuffer.toString());
                updateBatchByDemandOrderCode(successList);
            } else {
                sysInterfaceLog.setResults("SAP返回数据为空！");
                log.error("2周需求下达SAP传生产订单信息返回信息为空！");
                return R.error("2周需求下达SAP传生产订单信息返回信息为空！");
            }
        } catch (JCoException e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }finally {
            sysInterfaceLog.setCreateBy(sysUser.getLoginName());
            sysInterfaceLog.setCreateTime(DateUtil.date());
            sysInterfaceLog.setRemark("2周需求下达SAP传生产订单信息");
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
    public int updateBatchByDemandOrderCode(List<Oms2weeksDemandOrderEdit> list){
        return oms2weeksDemandOrderEditMapper.updateBatchByDemandOrderCode(list);
    }

    /**
     * SAP601创建订单接口定时任务（ZPP_INT_DDPS_02）
     * @return
     */
    @Override
    public R queryPlanOrderCodeFromSap601() {
        log.info("================(2周需求)获取SAP系统生产订单方法  start================");
        //查询传SAP中状态的数据
        Example example = new Example(Oms2weeksDemandOrderEdit.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CSAPZ.getCode());
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList=selectByExample(example);
        if (CollUtil.isEmpty(oms2weeksDemandOrderEditList)) {
            return R.ok();
        }
        List<String> demandOrderCodeList = oms2weeksDemandOrderEditList.stream().map(oms2weeksDemandOrderEdit -> oms2weeksDemandOrderEdit.getDemandOrderCode()).collect(Collectors.toList());
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog().builder()
                .appId("SAP").interfaceName(SapConstants.ZPP_INT_DDPS_05)
                .content(StrUtil.format("参数为oms2weeks_demand_order_edit表id：{}",CollUtil.join(demandOrderCodeList,","))).build();

        //定义返回的data体
        List<Oms2weeksDemandOrderEdit> dataList = new ArrayList<>();
        try {
            //创建与SAP的连接
            JCoDestination destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZPP_INT_DDPS_05);
            if (fm == null) {
                log.error("================获取SAP系统生产订单接口函数为空================");
                return R.error("获取SAP系统生产订单接口函数为空!");
            }
            JCoTable inputTable = fm.getTableParameterList().getTable("INPUT");
            for (Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit : oms2weeksDemandOrderEditList) {
                inputTable.appendRow();
                inputTable.setValue("ABLAD", oms2weeksDemandOrderEdit.getDemandOrderCode());
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
                //循环取table行数据
                StringBuffer sapBuffer = new StringBuffer();
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit = new Oms2weeksDemandOrderEdit();
                    oms2weeksDemandOrderEdit.setDemandOrderCode(outTableOutput.getString("ABLAD"));//需求订单号
                    oms2weeksDemandOrderEdit.setPlanOrderOrder(outTableOutput.getString("AUFNR"));//计划订单号
                    oms2weeksDemandOrderEdit.setStatus(Weeks2DemandOrderEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_YCSAP.getCode());//已传SAP
                    dataList.add(oms2weeksDemandOrderEdit);
                    String messageOne = StrUtil.format("ABLAD:{},AUFNR:{};"
                            ,outTableOutput.getString("ABLAD"),outTableOutput.getString("AUFNR"));
                    sapBuffer.append(messageOne);
                }
            } else {
                sysInterfaceLog.setResults("返回数据为空！");
                log.error("获取生产订单数据为空！");
                return R.error("获取生产订单数据为空！");
            }
        } catch (Exception e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }finally {
            sysInterfaceLog.setCreateBy("定时任务");
            sysInterfaceLog.setCreateTime(DateUtil.date());
            sysInterfaceLog.setRemark("2周需求下达SAP");
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
        log.info("================(2周需求)获取SAP系统生产订单方法  end================");

        return R.ok();
    }
}
