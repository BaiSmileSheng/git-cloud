package com.cloud.system.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.cloud.system.enums.GetStockEnum;

/**
 * 是否ZN认证
 * @author lihongxia
 */
public class GetStockConverter implements Converter<String> {


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
            return GetStockEnum.getCodeByMsg(stringValue);
        }

        @Override
        public CellData convertToExcelData(String string, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
            return new CellData(GetStockEnum.getMsgByCode(string));
        }
}
