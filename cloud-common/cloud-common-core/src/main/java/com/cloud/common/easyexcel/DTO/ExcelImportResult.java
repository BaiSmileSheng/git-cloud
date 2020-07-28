package com.cloud.common.easyexcel.DTO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther cs
 * @date 2020/6/18 9:31
 * @description excel数据导入结果
 */
@Data

public class ExcelImportResult {
    private List<ExcelImportSucObjectDto> successDtos;

    private List<ExcelImportErrObjectDto> errDtos;

    private List<ExcelImportOtherObjectDto> otherDtos;

    public ExcelImportResult(List<ExcelImportSucObjectDto> successDtos, List<ExcelImportErrObjectDto> errDtos, List<ExcelImportOtherObjectDto> otherDtos){
        this.successDtos =successDtos;
        this.errDtos = errDtos;
        this.otherDtos = otherDtos;
    }

    public ExcelImportResult(List<ExcelImportErrObjectDto> errDtos){
        this.successDtos =new ArrayList<>();
        this.errDtos = errDtos;
        this.otherDtos = new ArrayList<>();
    }
}
