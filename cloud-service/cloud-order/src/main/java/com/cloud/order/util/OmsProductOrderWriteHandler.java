package com.cloud.order.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.feign.RemoteDictDataService;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 排产订单导入模板加批注
 * @Author ltq
 * @Date 2020-08-03
 */
public class OmsProductOrderWriteHandler extends AbstractRowWriteHandler {

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();
            RemoteDictDataService remoteDictDataService = ApplicationContextUtil.getBean(RemoteDictDataService.class);

            // 在第一行 第一列 SAP订单类型 批注
            List<SysDictData> list = remoteDictDataService.getType("sap_order_type");
            String orderTypeStr=list.stream()
                    .map(t -> t.getDictLabel())
                    .collect(Collectors.joining(StrUtil.COMMA));
            XSSFClientAnchor xssfClientAnchorOrderType = new XSSFClientAnchor(0, 0, 0, 0, (short)0, 0, (short)5, 4);
            Comment commentOrderType = drawingPatriarch.createCellComment(xssfClientAnchorOrderType);
            // 输入批注信息
            commentOrderType.setString(new XSSFRichTextString("SAP订单类型:"+orderTypeStr));
            sheet.getRow(0).getCell(0).setCellComment(commentOrderType);
            String dateStr = "日期格式：yyyy-MM-dd";
            XSSFClientAnchor xssfClientAnchor = new XSSFClientAnchor(0, 0, 0, 0, (short)7, 0, (short)8, 1);
            Comment comment = drawingPatriarch.createCellComment(xssfClientAnchor);
            // 输入批注信息
            comment.setString(new XSSFRichTextString(dateStr));
            // 将批注添加到单元格对象中
            sheet.getRow(0).getCell(7).setCellComment(comment);
            XSSFClientAnchor xssfClientAnchor1 = new XSSFClientAnchor(0, 0, 0, 0, (short)8, 0, (short)9, 1);
            Comment comment1 = drawingPatriarch.createCellComment(xssfClientAnchor1);
            // 输入批注信息
            comment1.setString(new XSSFRichTextString(dateStr));
            sheet.getRow(0).getCell(8).setCellComment(comment1);
            XSSFClientAnchor xssfClientAnchor2 = new XSSFClientAnchor(0, 0, 0, 0, (short)10, 0, (short)11, 2);
            Comment comment2 = drawingPatriarch.createCellComment(xssfClientAnchor2);
            // 输入批注信息
            comment2.setString(new XSSFRichTextString(dateStr));
            sheet.getRow(0).getCell(10).setCellComment(comment2);
            String outTypeStr = "加工承揽方式：成品、半成品、自制";
            XSSFClientAnchor xssfClientAnchor3 = new XSSFClientAnchor(0, 0, 0, 0, (short)17, 0, (short)18, 2);
            Comment comment3 = drawingPatriarch.createCellComment(xssfClientAnchor3);
            // 输入批注信息
            comment3.setString(new XSSFRichTextString(outTypeStr));
            sheet.getRow(0).getCell(17).setCellComment(comment3);
            String orderType = "订单分类：正常、追加、储备、、新品、返修";
            XSSFClientAnchor xssfClientAnchor4 = new XSSFClientAnchor(0, 0, 0, 0, (short)18, 0, (short)19, 2);
            Comment comment4 = drawingPatriarch.createCellComment(xssfClientAnchor4);
            // 输入批注信息
            comment4.setString(new XSSFRichTextString(orderType));
            sheet.getRow(0).getCell(18).setCellComment(comment4);
        }
    }
}
