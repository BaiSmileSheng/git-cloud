package com.cloud.order.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.cloud.order.enums.LifeCycleEnum;

/**
 * 生命周期
 * @author lihongxia
 */
public class LifeCycleConverter implements Converter<String> {


        @Override
        public Class supportJavaTypeKey() {
            return String.class;
        }

        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public String convertToJavaData(CellData cellData, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration)  {
            String stringValue = cellData.getStringValue();
            return LifeCycleEnum.getCodeByMsg(stringValue);
        }

        @Override
        public CellData convertToExcelData(String string, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
            return new CellData(LifeCycleEnum.getMsgByCode(string));
        }
}
