package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
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
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.domain.vo.CdProductOverdueExcelImportErrorVo;
import com.cloud.system.domain.vo.CdProductOverdueImportVo;
import com.cloud.system.feign.RemoteFactoryInfoService;
import com.cloud.system.mapper.CdProductOverdueMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.system.service.ICdProductOverdueExcelImportService;
import com.cloud.system.service.ICdProductOverdueService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 超期库存 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-17
 */
@Service
public class CdProductOverdueServiceImpl extends BaseServiceImpl<CdProductOverdue> implements ICdProductOverdueService ,ICdProductOverdueExcelImportService{

    private static Logger logger = LoggerFactory.getLogger(CdProductOverdueServiceImpl.class);

    @Autowired
    private CdProductOverdueMapper cdProductOverdueMapper;

    @Autowired
    private ICdProductOverdueExcelImportService cdProductOverdueExcelImportService;


    @Autowired
    private ICdMaterialInfoService cdMaterialInfoService;

    /**
     * 导入数据 先根据创建人删除再新增
     * @param file
     * @return
     */
    @Transactional
    @Override
    public R importFactoryStorehouse(MultipartFile file, String loginName) throws IOException {

        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(cdProductOverdueExcelImportService, CdProductOverdueImportVo.class);
        EasyExcel.read(file.getInputStream(),CdProductOverdueImportVo.class,easyExcelListener).sheet().doRead();

        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)){
            List<CdProductOverdue> successResult =successList.stream().map(excelImportSucObjectDto -> {
                CdProductOverdue cdProductOverdue = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(), CdProductOverdue.class);
                cdProductOverdue.setCreateBy(loginName);
                cdProductOverdue.setUpdateBy(loginName);
                return cdProductOverdue;
            }).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(successResult)){
            Example example = new Example(CdProductOverdue.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("createBy",loginName);
            cdProductOverdueMapper.deleteByExample(example);
            cdProductOverdueMapper.insertList(successResult);
            }
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (!CollectionUtils.isEmpty(errList)){
            List<CdProductOverdueExcelImportErrorVo> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                CdProductOverdueExcelImportErrorVo cdProductOverdueExcelImportErrorVo = BeanUtil.copyProperties(excelImportErrObjectDto.getObject(),
                        CdProductOverdueExcelImportErrorVo.class);
                cdProductOverdueExcelImportErrorVo.setErrorMessage(excelImportErrObjectDto.getErrMsg());
                return cdProductOverdueExcelImportErrorVo;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "超期库存导入错误信息.xlsx", "sheet", new CdProductOverdueExcelImportErrorVo());
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

        List<CdProductOverdueImportVo> listImport = (List<CdProductOverdueImportVo>) objects;

        for(CdProductOverdueImportVo cdProductOverdue:listImport){
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            CdProductOverdue cdProductOverdueReq = new CdProductOverdue();
            BeanUtil.copyProperties(cdProductOverdue,cdProductOverdueReq);

            StringBuffer errMsgBuffer = new StringBuffer();
            String productMaterialCode = cdProductOverdue.getProductMaterialCode();
            if(StringUtils.isBlank(productMaterialCode)){
                errMsgBuffer.append("超期物料号不能为空;");
            }
            String factoryCode = cdProductOverdue.getProductFactoryCode();
            if(StringUtils.isBlank(factoryCode)){
                errMsgBuffer.append("工厂编号不能为空;");
            }
            if(StringUtils.isNotBlank(productMaterialCode) && StringUtils.isNotBlank(factoryCode)){
                Example example = new Example(CdMaterialInfo.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("materialCode", productMaterialCode);
                criteria.andEqualTo("plantCode", factoryCode);
                List<CdMaterialInfo> cdMaterialInfoList = cdMaterialInfoService.selectByExample(example);
                if(CollectionUtils.isEmpty(cdMaterialInfoList)
                        || null == cdMaterialInfoList.get(0)){
                    errMsgBuffer.append("对应工厂超期物料号不存在,请在物料主数据维护;");
                }else if(StringUtils.isBlank(cdMaterialInfoList.get(0).getMaterialDesc())){
                    errMsgBuffer.append("超期物料号描述不存在,请维护;");
                }else{
                    cdProductOverdueReq.setProductMaterialDesc(cdMaterialInfoList.get(0).getMaterialDesc());
                }
            }
            String errMsgBufferString = errMsgBuffer.toString();
            if(StringUtils.isNotBlank(errMsgBufferString)){
                errObjectDto.setObject(cdProductOverdue);
                errObjectDto.setErrMsg(errMsgBufferString);
                errDtos.add(errObjectDto);
                continue;
            }
            cdProductOverdueReq.setCreateTime(new Date());
            cdProductOverdueReq.setUpdateTime(new Date());
            cdProductOverdueReq.setDelFlag("0");
            sucObjectDto.setObject(cdProductOverdueReq);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos,errDtos,otherDtos);
    }
}
