package com.cloud.common.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.cloud.common.core.domain.R;
import com.cloud.common.easyexcel.listener.ExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.ToolUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 *
 * @Author yuanhaoyue swithaoy@gmail.com
 * @Description 工具类
 * @Date 2018-06-06
 * @Time 14:07
 */
public class EasyExcelUtil {
    /**
     * 读取某个Excel(一个 sheet)
     *
     * @param excel  文件
     * @param object 实体类映射
     * @param object sheetIndex 下标从0开始
     * @return Excel 数据 list
     */
    public static List<?> readSingleExcel(MultipartFile excel, Object object, int sheetIndex) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(excel.getInputStream(), object.getClass(), excelListener).build();
            ReadSheet readSheet = EasyExcel.readSheet(sheetIndex).build();
            excelReader.read(readSheet);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        } finally {
            // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
            excelReader.finish();
        }
        return excelListener.getDatas();
    }

    /**
     * 读取多个Excel(多个 sheet)
     *
     * @param excel  文件
     * @param object 实体类映射
     * @return Excel 数据 list
     */
    public static List<?> readMulExcel(MultipartFile excel, Object object) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReader excelReader = null;
        try {
            // 这里需要注意 ExcelListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个ExcelListener里面写
            EasyExcel.read(excel.getInputStream(), object.getClass(), excelListener).doReadAll();
            excelReader = EasyExcel.read(excel.getInputStream()).build();

            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            excelReader.read(readSheet);
        } catch (Exception e) {
            throw new BusinessException("导入Excel失败，请联系网站管理员！");
        } finally {
            // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
            excelReader.finish();
        }

        return excelListener.getDatas();

    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     *
     * @param list      数据 list，要导出的实体
     * @param fileName  导出的文件名，需要加后缀
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射对象
     */
    public static R writeExcel(List<?> list, String fileName, String sheetName, Object object) {
        fileName = getAbsoluteFile(DateUtils.dateTimeNow() + fileName);
        ExcelWriter excelWriter = EasyExcel.write(fileName, object.getClass())
                .registerWriteHandler(setHorizontalCellStyleStrategy(13, 11))
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
        try {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            excelWriter.write(list, writeSheet);
            return R.ok(fileName);
        } catch (Exception e) {
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    /**
     * 导出 Excel ：一个 sheet，动态表头
     *
     * @param list      数据 list，要导出的实体
     * @param fileName  导出的文件名，需要加后缀
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射对象
     */
    public static R writeExcelWithHead(List<?> list, String fileName, String sheetName, Object object,List<List<String>> head) {
        fileName = getAbsoluteFile(DateUtils.dateTimeNow() + fileName);
        Table table = new Table(1);

        ExcelWriter excelWriter = EasyExcel.write(fileName, object.getClass()).head(head)
                .registerWriteHandler(setHorizontalCellStyleStrategy(13, 11))
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
        try {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
            excelWriter.write(list, writeSheet);
            return R.ok(fileName);
        } catch (Exception e) {
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    public static String getAbsoluteFile(String filename) {
        String downloadPath = ToolUtil.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }

    /**
     * 导出多sheet
     *
     * @param fileName           文件名
     * @param sheetExcelDataList sheet对象
     * @throws IOException
     */
    public static R writeMultiExcel(String fileName, List<SheetExcelData> sheetExcelDataList) {
        fileName = getAbsoluteFile(DateUtils.dateTimeNow() + fileName);
        ExcelWriter excelWriter = EasyExcel.write(fileName).build();
        try {
            for (int i = 0, length = sheetExcelDataList.size(); i < length; i++) {
                WriteSheet writeSheet = EasyExcel.writerSheet(i + 1, sheetExcelDataList.get(i).getSheetName())
                        .head(sheetExcelDataList.get(i).getTClass())
                        .registerWriteHandler(setHorizontalCellStyleStrategy(13, 11))
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).build();
                excelWriter.write(sheetExcelDataList.get(i).getDataList(), writeSheet);
            }
            return R.ok(fileName);
        } catch (Exception e) {
            throw new BusinessException("导出Excel失败，请联系网站管理员！");
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }

    /**
     * 设置列表样式和内容样式，简单使用，作为参考，
     *
     * @param headFont    表头字体大小
     * @param contentFont 内容字体大小
     * @return
     */
    public static HorizontalCellStyleStrategy setHorizontalCellStyleStrategy(int headFont, int contentFont) {
        /**
         * 头的策略
         * 1，背景设置为白色
         * 2，设置字体大小
         */
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) headFont);
        headWriteCellStyle.setWriteFont(headWriteFont);
        /**
         * 内容的策略
         * 1，字体大小
         * 2，垂直居中
         * 3，水平居中
         * 4，边框样式
         */
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) contentFont);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        //设置 垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置 水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        //设置边框样式
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        contentWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK1.getIndex());
        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        HorizontalCellStyleStrategy horizontalCellStyleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        return horizontalCellStyleStrategy;
    }
}
