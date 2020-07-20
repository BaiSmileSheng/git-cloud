package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdFactoryStorehouseInfoImportErrorVo;
import com.cloud.system.mapper.CdFactoryStorehouseInfoMapper;
import com.cloud.system.service.ICdFactoryStorehouseInfoExcelImportService;
import com.cloud.system.service.ICdFactoryStorehouseInfoService;
import com.cloud.system.util.EasyExcelUtilOSS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工厂库位 Service业务层处理
 *
 * @author cs
 * @date 2020-06-15
 */
@Service
public class CdFactoryStorehouseInfoServiceImpl extends BaseServiceImpl<CdFactoryStorehouseInfo> implements ICdFactoryStorehouseInfoService , ICdFactoryStorehouseInfoExcelImportService {
    @Autowired
    private CdFactoryStorehouseInfoMapper cdFactoryStorehouseInfoMapper;
    @Autowired
    private ICdFactoryStorehouseInfoExcelImportService cdFactoryStorehouseInfoExcelImportService;

    /**
     * 根据工厂，客户编码分组取接收库位
     *
     * @param dicts
     * @return
     */
    @Override
    public R selectStorehouseToMap(List<Dict> dicts) {
        return R.data(cdFactoryStorehouseInfoMapper.selectStorehouseToMap(dicts));
    }

    @Override
    public R importFactoryStorehouse(MultipartFile file, SysUser sysUser)throws IOException {
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(cdFactoryStorehouseInfoExcelImportService,
                CdFactoryStorehouseInfo.class);
        EasyExcel.read(file.getInputStream(),CdFactoryStorehouseInfo.class,easyExcelListener).sheet().doRead();

        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)){
            List<CdFactoryStorehouseInfo> successResult =successList.stream().map(excelImportSucObjectDto -> {
                CdFactoryStorehouseInfo cdFactoryStorehouseInfo = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(),
                        CdFactoryStorehouseInfo.class);
                cdFactoryStorehouseInfo.setCreateBy(sysUser.getLoginName());
                return cdFactoryStorehouseInfo;
            }).collect(Collectors.toList());
            cdFactoryStorehouseInfoMapper.batchInsertOrUpdate(successResult);
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (!CollectionUtils.isEmpty(errList)){
            List<CdFactoryStorehouseInfoImportErrorVo> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                CdFactoryStorehouseInfoImportErrorVo cdFactoryStorehouseInfoImportErrorVo = BeanUtil.copyProperties(
                        excelImportErrObjectDto.getObject(),
                        CdFactoryStorehouseInfoImportErrorVo.class);
                cdFactoryStorehouseInfoImportErrorVo.setErrorMessage(excelImportErrObjectDto.getErrMsg());
                return cdFactoryStorehouseInfoImportErrorVo;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "交货提前量导入错误信息.xlsx", "sheet",
                    new CdFactoryStorehouseInfoImportErrorVo());
        }
        return R.ok();
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

        List<CdFactoryStorehouseInfo> listImport = (List<CdFactoryStorehouseInfo>) objects;

        for (CdFactoryStorehouseInfo cdFactoryStorehouseInfo: listImport) {
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getProductFactoryCode())) {
                errObjectDto.setObject(cdFactoryStorehouseInfo);
                errObjectDto.setErrMsg(StrUtil.format("生产工厂编码不能为空：{}", cdFactoryStorehouseInfo.getProductFactoryCode()));
                errDtos.add(errObjectDto);
                continue;
            }

            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getCustomerCode())) {
                errObjectDto.setObject(cdFactoryStorehouseInfo);
                errObjectDto.setErrMsg(StrUtil.format("客户编码不能为空：{}", cdFactoryStorehouseInfo.getCustomerCode()));
                errDtos.add(errObjectDto);
                continue;
            }

            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getStorehouseFrom())) {
                errObjectDto.setObject(cdFactoryStorehouseInfo);
                errObjectDto.setErrMsg(StrUtil.format("发货库位不能为空：{}", cdFactoryStorehouseInfo.getStorehouseFrom()));
                errDtos.add(errObjectDto);
                continue;
            }
            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getStorehouseTo())) {
                errObjectDto.setObject(cdFactoryStorehouseInfo);
                errObjectDto.setErrMsg(StrUtil.format("接收库位不能为空：{}", cdFactoryStorehouseInfo.getStorehouseTo()));
                errDtos.add(errObjectDto);
                continue;
            }

            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getLeadTime())) {
                errObjectDto.setObject(cdFactoryStorehouseInfo);
                errObjectDto.setErrMsg(StrUtil.format("提前量不能为空：{}", cdFactoryStorehouseInfo.getLeadTime()));
                errDtos.add(errObjectDto);
                continue;
            }

            cdFactoryStorehouseInfo.setCreateTime(new Date());
            cdFactoryStorehouseInfo.setDelFlag("0");
            sucObjectDto.setObject(cdFactoryStorehouseInfo);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }
}

