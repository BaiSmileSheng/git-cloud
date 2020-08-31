package com.cloud.system.util;

import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.enums.ProductTypeEnum;
import com.cloud.system.enums.ZnAttestationEnum;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

/**
 * 成品物料扩展easyExcel加批注
 * @Author Lihongxia
 * @Date 2020-07-22
 */
public class MaterialExtendInfoWriteHandler extends AbstractRowWriteHandler {

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            // 在第一行 第二列创建一个产品类别批注
            StringBuffer productTypeBuffer = new StringBuffer("请填写:");
            ProductTypeEnum.init();
            for (ProductTypeEnum pt : ProductTypeEnum.values()) {
                productTypeBuffer.append(pt.getMsg()).append(",");
            }
            String productTypeString =  productTypeBuffer.toString();
            String productType = productTypeString.substring(0,productTypeString.length()-1);
            XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor(0, 0, 0, 0, (short)1, 0, (short)5, 2);
            Comment comment = drawingPatriarch.createCellComment(xssfClientAnchor);
            // 输入批注信息
            comment.setString(new XSSFRichTextString(productType));
            // 将批注添加到单元格对象中 从0开始计算 第1行第2列
            sheet.getRow(0).getCell(1).setCellComment(comment);
            //在第一行 第三列创建一个产品类别批注
            StringBuffer lifeCycleBuffer = new StringBuffer("请填写:");
            for (LifeCycleEnum pt : LifeCycleEnum.values()) {
                lifeCycleBuffer.append(pt.getMsg()).append(",");
            }
            String lifeCycleString = lifeCycleBuffer.toString();
            String lifeCycle = lifeCycleString.substring(0,lifeCycleString.length()-1);
            XSSFClientAnchor xssfClientAnchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short)2, 0, (short)4, 2);
            Comment comment1 = drawingPatriarch.createCellComment(xssfClientAnchor1);
            // 输入批注信息
            comment1.setString(new XSSFRichTextString(lifeCycle));
            // 将批注添加到单元格对象中 从0开始计算 第1行第3列
            sheet.getRow(0).getCell(2).setCellComment(comment);

            //在第一行 第四列创建一个可否加工承揽的批注
            StringBuffer znAttestationBuffer = new StringBuffer("请填写:");
            for (ZnAttestationEnum pt : ZnAttestationEnum.values()) {
                znAttestationBuffer.append(pt.getMsg()).append(",");
            }
            String znAttestationString = (null == znAttestationBuffer) ? " " : znAttestationBuffer.toString();
            String znAttestation = znAttestationString.substring(0,znAttestationString.length()-1);
            XSSFClientAnchor xssfClientAnchor3 = new XSSFClientAnchor(0, 0, 0, 0, (short)3, 0, (short)5, 2);
            Comment comment3 = drawingPatriarch.createCellComment(xssfClientAnchor3);
            // 输入批注信息
            comment3.setString(new XSSFRichTextString(znAttestation));
            // 将批注添加到单元格对象中 从0开始计算 第1行第5列
            sheet.getRow(0).getCell(3).setCellComment(comment);

        }
    }
}
