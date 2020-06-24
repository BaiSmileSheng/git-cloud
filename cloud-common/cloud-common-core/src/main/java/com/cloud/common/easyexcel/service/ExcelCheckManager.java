package com.cloud.common.easyexcel.service;

import com.cloud.common.easyexcel.DTO.ExcelImportResult;

import java.util.List;

/**
 * @auther cs
 * @date 2020/6/18 9:30
 * @description excel校验接口
 */
public interface ExcelCheckManager<T> {
    /**
     * @description: 校验方法
     * @param objects
     * @throws
     * @return com.cec.moutai.common.easyexcel.ExcelImportResult
     */
    <T> ExcelImportResult checkImportExcel(List<T> objects);
}
