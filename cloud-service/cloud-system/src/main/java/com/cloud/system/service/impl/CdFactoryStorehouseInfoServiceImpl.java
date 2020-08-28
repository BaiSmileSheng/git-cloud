package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
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
import com.cloud.system.domain.vo.CdFactoryStorehouseInfoExportVo;
import com.cloud.system.domain.vo.CdFactoryStorehouseInfoImportErrorVo;
import com.cloud.system.mapper.CdFactoryStorehouseInfoMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdFactoryStorehouseInfoExcelImportService;
import com.cloud.system.service.ICdFactoryStorehouseInfoService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private ICdFactoryInfoService cdFactoryInfoService;

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
                CdFactoryStorehouseInfoExportVo.class);
        EasyExcel.read(file.getInputStream(),CdFactoryStorehouseInfoExportVo.class,easyExcelListener).sheet().doRead();

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

        List<CdFactoryStorehouseInfoExportVo> listImport = (List<CdFactoryStorehouseInfoExportVo>) objects;

        R  factoryInfoListR = cdFactoryInfoService.selectAllFactoryCode();
        if(!factoryInfoListR.isSuccess()){
            throw new BusinessException("查工厂信息失败" + factoryInfoListR.get("msg").toString());
        }
        List<String> factoryInfoList= factoryInfoListR.getCollectData(new TypeReference<List<String>>() {});
        for (CdFactoryStorehouseInfoExportVo cdFactoryStorehouseInfo: listImport) {
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            CdFactoryStorehouseInfo cdFactoryStorehouseInfoReq = new CdFactoryStorehouseInfo();
            BeanUtils.copyProperties(cdFactoryStorehouseInfo,cdFactoryStorehouseInfoReq);
            StringBuffer errMsgBuffer = new StringBuffer();
            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getProductFactoryCode())) {
                errMsgBuffer.append("生产工厂编码不能为空;");
            }
            String productFactoryCode = cdFactoryStorehouseInfo.getProductFactoryCode();
            if(StringUtils.isNotBlank(productFactoryCode) && !factoryInfoList.contains(productFactoryCode)){
                errMsgBuffer.append("生产工厂编码不存在,请维护;");
            }
            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getCustomerCode())) {
                errMsgBuffer.append("客户编码不能为空;");
            }

            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getStorehouseFrom())) {
                errMsgBuffer.append("发货库位不能为空;");
            }
            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getStorehouseTo())) {
                errMsgBuffer.append("接收库位不能为空;");
            }

            if (StringUtils.isBlank(cdFactoryStorehouseInfo.getLeadTime())) {
                errMsgBuffer.append("提前量不能为空;");
            }
            if(StringUtils.isNotBlank(cdFactoryStorehouseInfo.getLeadTime())){
                String leadTime = cdFactoryStorehouseInfo.getLeadTime();
                if(!leadTime.matches("^[0-9]+$")){
                    errMsgBuffer.append("提前量请只填写数字;");
                }
            }
            String errMsgBufferString = errMsgBuffer.toString();
            if(StringUtils.isNotBlank(errMsgBufferString)){
                errObjectDto.setObject(cdFactoryStorehouseInfo);
                errObjectDto.setErrMsg(errMsgBufferString);
                errDtos.add(errObjectDto);
                continue;
            }
            cdFactoryStorehouseInfoReq.setCreateTime(new Date());
            cdFactoryStorehouseInfoReq.setDelFlag("0");
            sucObjectDto.setObject(cdFactoryStorehouseInfoReq);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }
}

