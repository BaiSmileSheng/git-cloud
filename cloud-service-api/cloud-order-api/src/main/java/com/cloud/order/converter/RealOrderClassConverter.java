package com.cloud.order.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.cloud.order.enums.RealOrderClassEnum;

/**
 * 订单分类
 * @author lihongxia
 */
public class RealOrderClassConverter implements Converter<String> {


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
            return RealOrderClassEnum.getCodeByMsg(stringValue);
        }

        @Override
        public CellData convertToExcelData(String string, ExcelContentProperty excelContentProperty, GlobalConfiguration globalConfiguration) {
            return new CellData(RealOrderClassEnum.getMsgByCode(string));
        }
}
