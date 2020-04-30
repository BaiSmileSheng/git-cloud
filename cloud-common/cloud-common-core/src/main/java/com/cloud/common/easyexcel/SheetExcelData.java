package com.cloud.common.easyexcel;

import lombok.Data;

import java.util.List;

/**
 * @Description sheet对象
 * @Author gourd
 * @Date 2020/1/3 12:48
 * @Version 1.0
 */
@Data
public class SheetExcelData<T> {

    /**
     * 数据
     */
    private List<T> dataList;

    /**
     * sheet名
     */
    private String sheetName;

    /**
     * 对象类型
     */
    private Class<T> tClass ;
}
