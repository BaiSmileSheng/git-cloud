package com.cloud.common.easyexcel.DTO;

import lombok.Data;

/**
 * @auther cs
 * @date 2020/6/18 9:28
 * @description excel单条数据导入结果
 */
@Data
public class ExcelImportErrObjectDto {
    private Object object;

    private String errMsg;

    public ExcelImportErrObjectDto(){}

    public ExcelImportErrObjectDto(Object object,String errMsg){
        this.object = object;
        this.errMsg = errMsg;
    }
}
