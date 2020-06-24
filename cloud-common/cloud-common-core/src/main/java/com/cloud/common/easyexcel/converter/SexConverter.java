package com.cloud.common.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

/**
 * @author 性别类型转化器
 */
public class SexConverter implements Converter<String> {

        public static final String MALE = "男";
        public static final String FEMALE = "女";

        @Override
        public Class supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public String convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            String stringValue = cellData.getStringValue();
            if (MALE.equals(stringValue)){
                return "1";
            }else {
                return "0";
            }
        }

        @Override
        public CellData convertToExcelData(String string, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            if ("1".equals(string)){
                return new CellData(MALE);
            }else {
                return new CellData(FEMALE);
            }
        }
}
