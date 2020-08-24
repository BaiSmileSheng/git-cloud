package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdSettleProductMaterialExcelImportErrorVo;
import com.cloud.system.domain.vo.CdSettleProductMaterialExcelImportVo;
import com.cloud.system.enums.OutSourceTypeEnum;
import com.cloud.system.enums.PuttingOutEnum;
import com.cloud.system.mapper.CdMaterialExtendInfoMapper;
import com.cloud.system.mapper.CdMaterialPriceInfoMapper;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.system.service.ICdSettleProductMaterialExcelImportService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdSettleProductMaterialMapper;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.service.ICdSettleProductMaterialService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 物料号和加工费号对应关系 Service业务层处理
 *
 * @author cs
 * @date 2020-06-05
 */
@Service
@Slf4j
public class CdSettleProductMaterialServiceImpl extends BaseServiceImpl<CdSettleProductMaterial> implements ICdSettleProductMaterialService, ICdSettleProductMaterialExcelImportService {
    @Autowired
    private CdSettleProductMaterialMapper cdSettleProductMaterialMapper;

    @Autowired
    private CdMaterialExtendInfoMapper cdMaterialExtendInfoMapper;

    @Autowired
    private ICdSettleProductMaterialExcelImportService cdSettleProductMaterialExcelImportService;

    @Autowired
    private ICdMaterialInfoService cdMaterialInfoService;

    private static final String PRODUCT_MATERIAL_CODE = "productMaterialCode";

    private static final String OUTSOURCE_WAY = "outsourceWay";

    /**
     * 导入
     * @return 成功或失败
     */
    @GlobalTransactional
    @Override
    public R importMul(SysUser sysUser, MultipartFile file) throws Exception{
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(cdSettleProductMaterialExcelImportService,
                CdSettleProductMaterialExcelImportVo.class);
        EasyExcel.read(file.getInputStream(),CdSettleProductMaterialExcelImportVo.class,easyExcelListener).sheet().doRead();

        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)){
            List<CdSettleProductMaterial> successResult =successList.stream().map(excelImportSucObjectDto -> {
                CdSettleProductMaterial cdSettleProductMaterial = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(),
                        CdSettleProductMaterial.class);
                cdSettleProductMaterial.setCreateBy(sysUser.getLoginName());
                cdSettleProductMaterial.setUpdateBy(sysUser.getLoginName());
                cdSettleProductMaterial.setDelFlag(DeleteFlagConstants.NO_DELETED);
                return cdSettleProductMaterial;
            }).collect(Collectors.toList());
            cdSettleProductMaterialMapper.batchInsertOrUpdate(successResult);
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (!CollectionUtils.isEmpty(errList)){
            List<CdSettleProductMaterialExcelImportErrorVo> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                CdSettleProductMaterialExcelImportErrorVo cdSettleProductMaterialExcelImportErrorVo = BeanUtil.copyProperties(
                        excelImportErrObjectDto.getObject(),
                        CdSettleProductMaterialExcelImportErrorVo.class);
                cdSettleProductMaterialExcelImportErrorVo.setErrorMessage(excelImportErrObjectDto.getErrMsg());
                return cdSettleProductMaterialExcelImportErrorVo;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "物料号和加工费号维护导入错误信息.xlsx", "sheet",
                    new CdSettleProductMaterialExcelImportErrorVo());
        }
        return R.ok();
    }

    /**
     * 新增
     * @param cdSettleProductMaterial
     * @return
     */
    @Override
    public R insertProductMaterial(CdSettleProductMaterial cdSettleProductMaterial) {
        CdSettleProductMaterial cdSettleProductMaterialRes = getCdSettleProductMaterial(cdSettleProductMaterial);
        if(null != cdSettleProductMaterialRes){
            return R.error("此专用号对应的委外方式已维护加工费号");
        }
        cdSettleProductMaterialMapper.insertSelective(cdSettleProductMaterial);
        return R.data(cdSettleProductMaterial.getId());
    }

    /**
     * 查物料号信息
     * @param cdSettleProductMaterial
     * @return
     */
    private CdSettleProductMaterial getCdSettleProductMaterial(CdSettleProductMaterial cdSettleProductMaterial) {
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",cdSettleProductMaterial.getProductMaterialCode());
        criteria.andEqualTo("outsourceWay",cdSettleProductMaterial.getOutsourceWay());
        criteria.andEqualTo("delFlag",DeleteFlagConstants.NO_DELETED);
        return cdSettleProductMaterialMapper.selectOneByExample(example);
    }

    /**
     * 修改
     * @param cdSettleProductMaterial
     * @return
     */
    @Override
    public R updateProductMaterial(CdSettleProductMaterial cdSettleProductMaterial) {
        CdSettleProductMaterial cdSettleProductMaterialRes = getCdSettleProductMaterial(cdSettleProductMaterial);
        if(null != cdSettleProductMaterialRes && !cdSettleProductMaterial.getId().equals(cdSettleProductMaterialRes.getId())){
            return R.error("此专用号对应的委外方式已维护加工费号");
        }
        //填写描述
        cdSettleProductMaterialMapper.updateByPrimaryKeySelective(cdSettleProductMaterial);
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
        List<ExcelImportOtherObjectDto> otherDtos = new ArrayList<>();

        List<CdSettleProductMaterialExcelImportVo> listImport = (List<CdSettleProductMaterialExcelImportVo>) objects;

        //查到所有的可加工承揽的物料扩展信息
        Example exampleMaterialExtendInfo = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteriaMaterialExtendInfo = exampleMaterialExtendInfo.createCriteria();
        criteriaMaterialExtendInfo.andEqualTo("isPuttingOut", PuttingOutEnum.IS_PUTTING_OUT_1.getCode());
        List<CdMaterialExtendInfo> materialExtendInfoList = cdMaterialExtendInfoMapper.selectByExample(exampleMaterialExtendInfo);
        if(CollectionUtils.isEmpty(materialExtendInfoList)){
            log.error("查可加工承揽的物料扩展信息失败");
            throw new BusinessException("查可加工承揽的物料扩展信息不存在,请维护物料扩展表信息");
        }
        Map<String,CdMaterialExtendInfo> materialExtendInfoMap = materialExtendInfoList.stream().collect(Collectors.toMap(
                cdMaterialExtendInfo ->cdMaterialExtendInfo.getMaterialCode(),cdMaterialExtendInfo ->cdMaterialExtendInfo,
                (key1,key2) ->key2));
        //根据加工费号查物料主数据表 存在
        List<Dict> paramsMapList = listImport.stream().map(omsProductionOrder ->
                        new Dict().set(PRODUCT_MATERIAL_CODE, omsProductionOrder.getRawMaterialCode())
                        ).distinct().collect(toList());
        R cdMaterialInfoListR = cdMaterialInfoService.selectListByMaterialCodeList(paramsMapList);
        if(!cdMaterialInfoListR.isSuccess()){
            log.error("在物料主数据表查加工费号异常 res:{}",JSONObject.toJSONString(cdMaterialInfoListR));
            throw new BusinessException("在物料主数据表查加工费号异常");
        }
        List<CdMaterialInfo> cdMaterialInfoList = cdMaterialInfoListR.getCollectData(new TypeReference<List<CdMaterialInfo>>() {});
        //key是加工费号即此处的加工费号
        Map<String,CdMaterialInfo> cdMaterialInfoMap = cdMaterialInfoList.stream().collect(Collectors.toMap(
                cdMaterialInfo -> cdMaterialInfo.getMaterialCode(),cdMaterialInfo ->cdMaterialInfo,(key1,key2) ->key2));
        //查物料号和加工费号表(专用号+委外方式不存在)
        List<Dict> cdSettleProductMAterialMapList = listImport.stream().map(omsProductionOrder ->
                new Dict().set(PRODUCT_MATERIAL_CODE, omsProductionOrder.getProductMaterialCode())
                .set(OUTSOURCE_WAY,omsProductionOrder.getOutsourceWay())
        ).distinct().collect(toList());
        List<CdSettleProductMaterial> cdSettleProductMaterialList = cdSettleProductMaterialMapper.selectByIndexes(cdSettleProductMAterialMapList);
        Map<String,CdSettleProductMaterial> cdSettleProductMaterialMap = new HashMap<>();
        if(!CollectionUtils.isEmpty(cdSettleProductMaterialList)){
            cdSettleProductMaterialMap = cdSettleProductMaterialList.stream().collect(Collectors.toMap(
                    cdSettleProductMaterial -> cdSettleProductMaterial.getProductMaterialCode() + cdSettleProductMaterial.getOutsourceWay(),
                    cdSettleProductMaterial ->cdSettleProductMaterial,(key1,key2) ->key2));
        }
        //String 专用号+委外方式 用于判断导入数据的唯一性
        Set<String> codeSet = new HashSet<>();
        for (CdSettleProductMaterialExcelImportVo cdSettleProductMaterialExcelImportVo: listImport) {
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            CdSettleProductMaterial cdSettleProductMaterialReq = new CdSettleProductMaterial();
            BeanUtils.copyProperties(cdSettleProductMaterialExcelImportVo,cdSettleProductMaterialReq);

            StringBuffer errMsgBuffer = new StringBuffer();
            if (StringUtils.isBlank(cdSettleProductMaterialExcelImportVo.getProductMaterialCode())) {
                errMsgBuffer.append("专用号不能为空;");
            }
            if(StringUtils.isNotBlank(cdSettleProductMaterialExcelImportVo.getProductMaterialCode())){
                CdMaterialExtendInfo cdMaterialExtendInfo = materialExtendInfoMap.get(cdSettleProductMaterialExcelImportVo.getProductMaterialCode());
                if(null == cdMaterialExtendInfo ||StringUtils.isBlank(cdMaterialExtendInfo.getMaterialDesc())){
                    errMsgBuffer.append("专用号描述不存在请去成品物料信息维护;");
                }  else {
                    cdSettleProductMaterialReq.setProductMaterialDesc(cdMaterialExtendInfo.getMaterialDesc());
                }
            }
            if (StringUtils.isBlank(cdSettleProductMaterialExcelImportVo.getRawMaterialCode())) {
                errMsgBuffer.append("加工费号不能为空;");
            }
            if(StringUtils.isNotBlank(cdSettleProductMaterialExcelImportVo.getRawMaterialCode())){
                CdMaterialInfo cdMaterialInfo = cdMaterialInfoMap.get(cdSettleProductMaterialExcelImportVo.getRawMaterialCode());
                if(null == cdMaterialInfo || StringUtils.isBlank(cdMaterialInfo.getMaterialDesc())){
                    errMsgBuffer.append("加工费号描述不存在请去物料主数据表维护信息;");
                }else {
                    cdSettleProductMaterialReq.setRawMaterialDesc(cdMaterialInfo.getMaterialDesc());
                }
            }

            if (StringUtils.isBlank(cdSettleProductMaterialExcelImportVo.getOutsourceWay())) {
                errMsgBuffer.append("委外方式不能为空;");
            }
            if(StringUtils.isNotBlank(cdSettleProductMaterialExcelImportVo.getOutsourceWay())){
                String outsourceWay = OutSourceTypeEnum.getCodeByMsg(cdSettleProductMaterialExcelImportVo.getOutsourceWay());
                if(StringUtils.isBlank(outsourceWay) || outsourceWay.equals(cdSettleProductMaterialExcelImportVo.getOutsourceWay())){
                    errMsgBuffer.append("委外方式不存在;");
                } else {
                    cdSettleProductMaterialReq.setOutsourceWay(outsourceWay);
                }
            }
            if(StringUtils.isNotBlank(cdSettleProductMaterialExcelImportVo.getProductMaterialCode())
                    && StringUtils.isNotBlank(cdSettleProductMaterialExcelImportVo.getOutsourceWay())){
                String code = cdSettleProductMaterialExcelImportVo.getProductMaterialCode()+cdSettleProductMaterialExcelImportVo.getOutsourceWay();
                if(codeSet.contains(code)){
                    errMsgBuffer.append("此专用号和委外方式导入重复;");
                }
                codeSet.add(code);
                if(null != cdSettleProductMaterialMap.get(code)){
                    errMsgBuffer.append("此专用号和委外方式已存在加工费号;");
                }
            }
            String errMsgBufferString = errMsgBuffer.toString();
            if(StringUtils.isNotBlank(errMsgBufferString)){
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(errMsgBufferString);
                errDtos.add(errObjectDto);
                continue;
            }
            sucObjectDto.setObject(cdSettleProductMaterialReq);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }
}
