package com.cloud.order.easyexcel;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.cloud.common.utils.spring.ApplicationContextUtil;
import com.cloud.order.enums.OrderFromEnum;
import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.feign.RemoteDictDataService;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 2周导入模板加批注
 * @Author cs
 * @Date 2020-07-30
 */
public class Week2OrderGatherEditWriteHandler extends AbstractRowWriteHandler {

    @Override
    public void afterRowDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Integer relativeRowIndex, Boolean isHead) {
        if (isHead) {
            Sheet sheet = writeSheetHolder.getSheet();
            Drawing<?> drawingPatriarch = sheet.createDrawingPatriarch();

            // 在第一行 第一列 订单来源 批注
            String orderFromStr=Arrays.stream(OrderFromEnum.values())
                    .map(t -> t.getMsg())
                    .collect(Collectors.joining(StrUtil.COMMA));
            XSSFClientAnchor xssfClientAnchorOrderFrom = new XSSFClientAnchor(0, 0, 0, 0, (short)0, 0, (short)1, 1);
            Comment commentOrderFrom = drawingPatriarch.createCellComment(xssfClientAnchorOrderFrom);
            // 输入批注信息
            commentOrderFrom.setString(new XSSFRichTextString(orderFromStr));
            // 将批注添加到单元格对象中 从0开始计算 第1行第2列
            sheet.getRow(0).getCell(1).setCellComment(commentOrderFrom);

            RemoteDictDataService remoteDictDataService = ApplicationContextUtil.getBean(RemoteDictDataService.class);

            // 在第一行 第二列 SAP订单类型 批注
            List<SysDictData> list = remoteDictDataService.getType("sap_order_type");
            String orderTypeStr=list.stream()
                    .map(t -> t.getDictLabel())
                    .collect(Collectors.joining(StrUtil.COMMA));
            XSSFClientAnchor xssfClientAnchorOrderType = new XSSFClientAnchor(0, 0, 0, 0, (short)1, 0, (short)6, 4);
            Comment commentOrderType = drawingPatriarch.createCellComment(xssfClientAnchorOrderType);
            // 输入批注信息
            commentOrderType.setString(new XSSFRichTextString(orderTypeStr));
            // 将批注添加到单元格对象中 从0开始计算 第1行第2列
            sheet.getRow(0).getCell(2).setCellComment(commentOrderType);

            // 在第一行 第10列 交付日期 批注
            XSSFClientAnchor xssfClientAnchorDate = new XSSFClientAnchor(0, 0, 0, 0, (short)9, 0, (short)12, 1);
            Comment commentDate = drawingPatriarch.createCellComment(xssfClientAnchorDate);
            // 输入批注信息
            commentDate.setString(new XSSFRichTextString("例：2020-07-07"));
            // 将批注添加到单元格对象中 从0开始计算 第1行第2列
            sheet.getRow(0).getCell(10).setCellComment(commentDate);

            // 在第一行 第11列 地点 批注
            XSSFClientAnchor xssfClientAnchorRelation = new XSSFClientAnchor(0, 0, 0, 0, (short)10, 0, (short)12, 1);
            Comment commentRelation = drawingPatriarch.createCellComment(xssfClientAnchorRelation);
            // 输入批注信息
            commentRelation.setString(new XSSFRichTextString("交货库位"));
            // 将批注添加到单元格对象中 从0开始计算 第1行第2列
            sheet.getRow(0).getCell(11).setCellComment(commentRelation);
        }
    }
}
