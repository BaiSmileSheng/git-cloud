package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEditHis;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEditImport;
import com.cloud.order.enums.DemandOrderGatherEditAuditStatusEnum;
import com.cloud.order.enums.DemandOrderGatherEditStatusEnum;
import com.cloud.order.enums.OrderFromEnum;
import com.cloud.order.mapper.OmsDemandOrderGatherEditMapper;
import com.cloud.order.service.IOmsDemandOrderGatherEditHisService;
import com.cloud.order.service.IOmsDemandOrderGatherEditImportService;
import com.cloud.order.service.IOmsDemandOrderGatherEditService;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.enums.MaterialTypeEnum;
import com.cloud.system.feign.RemoteFactoryInfoService;
import com.cloud.system.feign.RemoteMaterialExtendInfoService;
import com.cloud.system.feign.RemoteMaterialService;
import com.cloud.system.feign.RemoteSequeceService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 滚动计划需求操作 Service业务层处理
 *
 * @author cs
 * @date 2020-06-16
 */
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
        int i=updateByPrimaryKeySelective(omsDemandOrderGatherEditNew);
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
            OmsDemandOrderGatherEdit omsDemandOrderGatherEdit = selectByPrimaryKey(Long.valueOf(id));
            if (!StrUtil.equals(omsDemandOrderGatherEdit.getStatus(), DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode())) {
                return R.error(StrUtil.format("此状态数据不允许删除！需求订单号：{}",omsDemandOrderGatherEdit.getDemandOrderCode()));
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
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        Example.Criteria criteria = example.createCriteria();
        //允许下达的审核状态
        List<String> auditStatusList = CollUtil.newArrayList(DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_WXSH.getCode()
                ,DemandOrderGatherEditAuditStatusEnum.DEMAND_ORDER_GATHER_EDIT_AUDIT_STATUS_SHWC.getCode());
        if (StrUtil.isEmpty(ids)) {
            //如果参数为空，则查询初始状态,无需审核、审核完成的数据
            criteria.andEqualTo("status", DemandOrderGatherEditStatusEnum.DEMAND_ORDER_GATHER_EDIT_STATUS_CS.getCode());
            criteria.andIn("auditStatus", auditStatusList);
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
        }
        updateBatchByPrimaryKeySelective(list);
        return R.ok();
    }


    @Override
    public <T> ExcelImportResult checkImportExcel(List<T> objects) {
        if (CollUtil.isEmpty(objects)) {
            throw new BusinessException("无导入数据！");
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

        //因数量较大，一次性取出cd_material_info物料描述，cd_material_extend_info生命周期，cd_factory_info公司编码
        R rCompanyList=remoteFactoryInfoService.getAllCompanyCode();
        if (!rCompanyList.isSuccess()) {
            throw new BusinessException("无工厂信息，请到基础信息维护！");
        }
        List<String> companyCodeList = rCompanyList.getCollectData(new TypeReference<List<String>>() {});
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
        R rMaterial = remoteMaterialService.selectInfoByInMaterialCodeAndMaterialType(materialCodeList, MaterialTypeEnum.WLLX_HALB.getCode());
        if (!rMaterial.isSuccess()) {
            throw new BusinessException("导入数据的物料信息无数据，请到基础信息维护！");
        }
        Map<String, List<CdMaterialInfo>> mapMaterial = rMaterial.getCollectData(new TypeReference<Map<String, List<CdMaterialInfo>>>() {});
        //需要审核的数据list
        List<OmsDemandOrderGatherEdit> listNeedAudti = CollUtil.newArrayList();
        Date date = DateUtil.date();

        for(OmsDemandOrderGatherEdit demandOrderGatherEdit:list){
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();
            ExcelImportOtherObjectDto othObjectDto = new ExcelImportOtherObjectDto();
            String factoryCode = demandOrderGatherEdit.getProductFactoryCode();
            if (!CollUtil.contains(companyCodeList, factoryCode)) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(StrUtil.format("不存在此工厂：{}", factoryCode));
                errDtos.add(errObjectDto);
                continue;
            }
            //物料描述赋值
            List<CdMaterialInfo> materialInfos = mapMaterial.get(demandOrderGatherEdit.getProductMaterialCode());
            if (CollUtil.isEmpty(materialInfos)) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(StrUtil.format("不存在物料信息：{}", demandOrderGatherEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            //根据工厂、物料号获取单一对象
            Optional<CdMaterialInfo> cdMaterialInfoOpt = materialInfos.stream()
                    .filter(ma -> StrUtil.equals(ma.getPlantCode(), factoryCode))
                    .findFirst();
            if (!cdMaterialInfoOpt.isPresent()) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(StrUtil.format("不存在物料号：{},工厂：{}的物料数据，请维护！",
                        demandOrderGatherEdit.getProductMaterialCode(),factoryCode));
                errDtos.add(errObjectDto);
                continue;
            }
            CdMaterialInfo cdMaterialInfo = cdMaterialInfoOpt.get();
            demandOrderGatherEdit.setProductMaterialDesc(cdMaterialInfo.getMaterialDesc());
            demandOrderGatherEdit.setPurchaseGroupCode(cdMaterialInfo.getPurchaseGroupCode());
            demandOrderGatherEdit.setUnit(cdMaterialInfo.getPrimaryUom());
            //需求订单号
            R seqresult = remoteSequeceService.selectSeq("demand_order_gather_seq", 4);
            if(!seqresult.isSuccess()){
                throw new BusinessException("查序列号失败");
            }
            String seq = seqresult.getStr("data");
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
            //判断是否下市，下市则进入审批
            CdMaterialExtendInfo extendInfo = materialExtendInfoMap.get(demandOrderGatherEdit.getProductMaterialCode());
            if (extendInfo==null) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(StrUtil.format("物料号{}：无生命周期及产品类别信息！",demandOrderGatherEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            String productType = extendInfo.getProductType();
            String lifeCyle = extendInfo.getLifeCycle();
            if (StrUtil.isEmpty(productType)) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(StrUtil.format("物料号{}：无产品类别信息！",demandOrderGatherEdit.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            } else if (StrUtil.isEmpty(lifeCyle)) {
                errObjectDto.setObject(demandOrderGatherEdit);
                errObjectDto.setErrMsg(StrUtil.format("物料号{}：无生命周期信息！",demandOrderGatherEdit.getProductMaterialCode()));
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

    /**
     * 需求数据导入
     * @param successList 成功结果集
     * @param auditList 需要审核的结果集
     * @return
     */
    @Override
    @Transactional
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

        //TODO:下市数据进入审批 审批数据：auditList

        //获取表里数据的数据版本
        List<OmsDemandOrderGatherEdit> demandOrderGatherEditOlds=omsDemandOrderGatherEditMapper.selectAll();
        if (!CollUtil.isEmpty(demandOrderGatherEditOlds)) {
            OmsDemandOrderGatherEdit demandOrderGatherEditOld = CollUtil.getFirst(demandOrderGatherEditOlds);
            String versionOld = demandOrderGatherEditOld.getVersion();
            if (StrUtil.equals(version, versionOld)) {
                //相同周：根据登录人、客户编码删除原有的，插入新的
                List<String> customerList = demandOrderGatherEditOlds.stream()
                        .filter(dto -> StrUtil.equals(sysUser.getLoginName(), dto.getCreateBy()))
                        .map(dtoMap-> dtoMap.getCustomerCode()).distinct().collect(Collectors.toList());
                deleteByCreateByAndCustomerCode(demandOrderGatherEditOld.getCreateBy(), customerList);
            }else{
                //上周：全部插入历史表，删除原有的，插入新的
                List<OmsDemandOrderGatherEditHis> listHis= demandOrderGatherEditOlds.stream().map(demandOrderGatherEdit ->
                        BeanUtil.copyProperties(demandOrderGatherEdit,OmsDemandOrderGatherEditHis.class)).collect(Collectors.toList());
                omsDemandOrderGatherEditHisService.insertList(listHis);
                deleteByCreateByAndCustomerCode(null,null);
            }
        }
        insertList(successList);
        return R.ok();
    }

	@Override
	public int deleteByCreateByAndCustomerCode(String createBy,List<String> customerCodes){
		 return omsDemandOrderGatherEditMapper.deleteByCreateByAndCustomerCode(createBy,customerCodes);
	}

    public static void main(String[] args) {
        CdBomInfo cdBomInfo1 = new CdBomInfo().builder().productMaterialCode("11")
                .productMaterialDesc("哈哈哈").productFactoryCode("33").basicNum(1L).build();
        CdBomInfo cdBomInfo2 = new CdBomInfo().builder().productMaterialCode("11")
                .productMaterialDesc("呵呵呵").productFactoryCode("33").basicNum(2L).build();
        CdBomInfo cdBomInfo3 = new CdBomInfo().builder().productMaterialCode("22")
                .productMaterialDesc("嘿嘿").productFactoryCode("33").basicNum(3L).build();
        CdBomInfo cdBomInfo4 = new CdBomInfo().builder().productMaterialCode("22")
                .productMaterialDesc("呼呼").productFactoryCode("33").basicNum(4L).build();

        List<CdBomInfo> list = CollUtil.newArrayList(cdBomInfo1,cdBomInfo2,cdBomInfo3,cdBomInfo4);
        Map<String,List<CdBomInfo>> map=list.stream().collect(Collectors.groupingBy(e -> fetchGroupKey(e)));


        Map<String,Double> map1=list.stream().collect(Collectors.groupingBy(e ->
                fetchGroupKey(e),Collectors.summingDouble(CdBomInfo::getBasicNum)));

        List<Object> list1 = new ArrayList<>();
        list1.addAll(list);
        System.out.println("牛逼！");
    }

    private static String fetchGroupKey(CdBomInfo cd){
        return cd.getProductMaterialCode() +"#"+ cd.getProductFactoryCode();
    }

}
