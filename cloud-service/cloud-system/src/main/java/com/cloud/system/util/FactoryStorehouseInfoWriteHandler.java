package com.cloud.system.util;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * 交货提前量easyExcel加批注
 * @Author Lihongxia
 * @Date 2020-07-22
 */
public class FactoryStorehouseInfoWriteHandler extends AbstractRowWriteHandler {

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            // 在第一行 第5列创建一个批注
            String s = "请仅填写数字例如:1";
            XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor(0, 0, 0, 0, (short)4, 0, (short)6, 2);
            Comment comment = drawingPatriarch.createCellComment(xssfClientAnchor);
            // 输入批注信息
            comment.setString(new XSSFRichTextString(s));
            // 将批注添加到单元格对象中 从0开始计算 第1行第5列
            sheet.getRow(0).getCell(4).setCellComment(comment);
        }
    }
}
