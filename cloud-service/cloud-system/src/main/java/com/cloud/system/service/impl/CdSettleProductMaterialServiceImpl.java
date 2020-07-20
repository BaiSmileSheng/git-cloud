package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdSettleProductMaterialExcelImportErrorVo;
import com.cloud.system.domain.vo.CdSettleProductMaterialExcelImportVo;
import com.cloud.system.enums.OutSourceTypeEnum;
import com.cloud.system.enums.PriceTypeEnum;
import com.cloud.system.enums.PuttingOutEnum;
import com.cloud.system.mapper.CdMaterialExtendInfoMapper;
import com.cloud.system.mapper.CdMaterialPriceInfoMapper;
import com.cloud.system.service.ICdSettleProductMaterialExcelImportService;
import com.cloud.system.util.EasyExcelUtilOSS;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private CdMaterialPriceInfoMapper cdMaterialPriceInfoMapper;

    @Autowired
    private ICdSettleProductMaterialExcelImportService cdSettleProductMaterialExcelImportService;

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
        //填写描述
        setDesc(cdSettleProductMaterial);
        cdSettleProductMaterialMapper.insertSelective(cdSettleProductMaterial);
        return R.data(cdSettleProductMaterial.getId());
    }

    /**
     * 填写描述
     * @param cdSettleProductMaterial
     */
    private void setDesc(CdSettleProductMaterial cdSettleProductMaterial) {
        CdMaterialExtendInfo cdMaterialExtendInfo = cdMaterialExtendInfoMapper
                .selectOneByMaterialCode(cdSettleProductMaterial.getProductMaterialCode());
        if(null == cdMaterialExtendInfo || StringUtils.isBlank(cdMaterialExtendInfo.getMaterialDesc())){
            throw new BusinessException("请维护专用号描述");
        }
        cdSettleProductMaterial.setProductMaterialDesc(cdMaterialExtendInfo.getMaterialDesc());
        Example exampleMaterialPriceInfo = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteriaMaterialPriceInfo = exampleMaterialPriceInfo.createCriteria();
        criteriaMaterialPriceInfo.andEqualTo("materialCode",cdSettleProductMaterial.getRawMaterialCode());
        criteriaMaterialPriceInfo.andEqualTo("priceType", PriceTypeEnum.PRICE_TYPE_1.getCode());
        criteriaMaterialPriceInfo.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        CdMaterialPriceInfo cdMaterialPriceInfo = cdMaterialPriceInfoMapper.selectOneByExample(exampleMaterialPriceInfo);
        if(null ==cdMaterialPriceInfo || StringUtils.isBlank(cdMaterialPriceInfo.getMaterialDesc())){
            throw new BusinessException("请维护加工费号描述");
        }
        cdSettleProductMaterial.setRawMaterialDesc(cdMaterialPriceInfo.getMaterialDesc());
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
        setDesc(cdSettleProductMaterial);
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
        //查询所有加工费号
        Example exampleMaterialPriceInfo = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteriaMaterialPriceInfo = exampleMaterialPriceInfo.createCriteria();
        criteriaMaterialPriceInfo.andEqualTo("priceType", PriceTypeEnum.PRICE_TYPE_1.getCode());
        List<CdMaterialPriceInfo> materialPriceInfoList = cdMaterialPriceInfoMapper.selectByExample(exampleMaterialPriceInfo);
        if(CollectionUtils.isEmpty(materialPriceInfoList)){
            log.error("查可加工费号信息失败");
            throw new BusinessException("查可加工费号信息失败,请SAP成本价格表信息");
        }
        Map<String,CdMaterialPriceInfo> materialPriceInfoMap = materialPriceInfoList.stream().collect(Collectors.toMap(
                cdMaterialPriceInfo -> cdMaterialPriceInfo.getMaterialCode(),cdMaterialPriceInfo ->cdMaterialPriceInfo,(key1,key2) ->key2));

        for (CdSettleProductMaterialExcelImportVo cdSettleProductMaterialExcelImportVo: listImport) {
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            if (StringUtils.isBlank(cdSettleProductMaterialExcelImportVo.getProductMaterialCode())) {
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(StrUtil.format("专用号不能为空：{}",cdSettleProductMaterialExcelImportVo.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            CdMaterialExtendInfo cdMaterialExtendInfo = materialExtendInfoMap.get(cdSettleProductMaterialExcelImportVo.getProductMaterialCode());
            if(null == cdMaterialExtendInfo ||StringUtils.isBlank(cdMaterialExtendInfo.getMaterialDesc())){
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(StrUtil.format("专用号描述不存在请去物料扩展表维护信息：{}", cdSettleProductMaterialExcelImportVo.getProductMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            if (StringUtils.isBlank(cdSettleProductMaterialExcelImportVo.getRawMaterialCode())) {
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(StrUtil.format("加工费号不能为空：{}", cdSettleProductMaterialExcelImportVo.getRawMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            CdMaterialPriceInfo cdMaterialPriceInfo = materialPriceInfoMap.get(cdSettleProductMaterialExcelImportVo.getRawMaterialCode());
            if(null == cdMaterialPriceInfo || StringUtils.isBlank(cdMaterialPriceInfo.getMaterialDesc())){
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(StrUtil.format("加工费号描述不存在请去维护信息：{}", cdSettleProductMaterialExcelImportVo.getRawMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            if (StringUtils.isBlank(cdSettleProductMaterialExcelImportVo.getOutsourceWay())) {
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(StrUtil.format("委外方式不能为空：{}", cdSettleProductMaterialExcelImportVo.getOutsourceWay()));
                errDtos.add(errObjectDto);
                continue;
            }
            String outsourceWay = OutSourceTypeEnum.getCodeByMsg(cdSettleProductMaterialExcelImportVo.getOutsourceWay());
            if(StringUtils.isBlank(outsourceWay) || outsourceWay.equals(cdSettleProductMaterialExcelImportVo.getOutsourceWay())){
                errObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
                errObjectDto.setErrMsg(StrUtil.format("委外方式不存在：{}", cdSettleProductMaterialExcelImportVo.getOutsourceWay()));
                errDtos.add(errObjectDto);
                continue;
            }
            cdSettleProductMaterialExcelImportVo.setOutsourceWay(outsourceWay);
            cdSettleProductMaterialExcelImportVo.setProductMaterialDesc(cdMaterialExtendInfo.getMaterialDesc());
            cdSettleProductMaterialExcelImportVo.setRawMaterialDesc(cdMaterialPriceInfo.getMaterialDesc());
            sucObjectDto.setObject(cdSettleProductMaterialExcelImportVo);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }
}
