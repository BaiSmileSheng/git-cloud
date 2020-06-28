package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.system.domain.vo.CdProductOverdueExcelImportErrorVo;
import com.cloud.system.service.ICdProductOverdueExcelImportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdProductOverdueMapper;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.service.ICdProductOverdueService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 超期库存 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-17
 */
@Service
public class CdProductOverdueServiceImpl extends BaseServiceImpl<CdProductOverdue> implements ICdProductOverdueService {
    @Autowired
    private CdProductOverdueMapper cdProductOverdueMapper;

    @Autowired
    private ICdProductOverdueExcelImportService cdProductOverdueExcelImportService;

    /**
     * 导入数据 先根据创建人删除再新增
     * @param file
     * @return
     */
    @Transactional
    @Override
    public R importFactoryStorehouse(MultipartFile file, long loginId) throws IOException {

        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(cdProductOverdueExcelImportService, CdProductOverdue.class);
        EasyExcel.read(file.getInputStream(),CdProductOverdue.class,easyExcelListener).sheet().doRead();

        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)){
            List<CdProductOverdue> successResult =successList.stream().map(excelImportSucObjectDto -> {
                CdProductOverdue cdProductOverdue = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(), CdProductOverdue.class);
                return cdProductOverdue;
            }).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(successResult)){
            Example example = new Example(CdProductOverdue.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("createBy",loginId);
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
            return EasyExcelUtil.writeExcel(errorResults, "超期库存导入错误信息.xlsx", "sheet", new CdProductOverdueExcelImportErrorVo());
        }
        return R.ok();
    }
}
