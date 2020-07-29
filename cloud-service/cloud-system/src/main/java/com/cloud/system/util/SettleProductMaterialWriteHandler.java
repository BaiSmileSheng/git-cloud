package com.cloud.system.util;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.cloud.settle.enums.OutsourceWayEnum;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * 物料号加工费号easyExcel加批注
 * @Author Lihongxia
 * @Date 2020-07-22
 */
public class SettleProductMaterialWriteHandler extends AbstractRowWriteHandler {

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            // 在第一行 第三列列创建一个产品类别批注
            StringBuffer outsourceWayBuffer = new StringBuffer("请填写:");
            for (OutsourceWayEnum pt : OutsourceWayEnum.values()) {
                outsourceWayBuffer.append(pt.getMsg()).append(",");
            }
            String productTypeString =  outsourceWayBuffer.toString();
            String productType = productTypeString.substring(0,productTypeString.length()-1);
            XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor(0, 0, 0, 0, (short)2, 0, (short)5, 2);
            Comment comment = drawingPatriarch.createCellComment(xssfClientAnchor);
            // 输入批注信息
            comment.setString(new XSSFRichTextString(productType));
            // 将批注添加到单元格对象中 从0开始计算 第1行第3列
            sheet.getRow(0).getCell(2).setCellComment(comment);
        }
    }
}
