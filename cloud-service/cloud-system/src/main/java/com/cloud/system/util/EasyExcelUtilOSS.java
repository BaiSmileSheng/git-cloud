package com.cloud.system.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.write.handler.AbstractRowWriteHandler;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.cloud.common.core.domain.R;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.easyexcel.SheetExcelData;
import com.cloud.system.oss.CloudStorageService;
import com.cloud.system.oss.OSSFactory;

import java.io.File;
import java.util.List;

/**
 *  导出excel 上传到华为云
 */
public class EasyExcelUtilOSS {


    /**
     * 导出 Excel ：一个 sheet，带表头
     *
     * @param list      数据 list，要导出的实体
     * @param fileName  导出的文件名，需要加后缀
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射对象
     */
    public static R writeExcel(List<?> list, String fileName, String sheetName, Object object) {
        R r = EasyExcelUtil.writeExcel(list, fileName, sheetName, object);
        if (!r.isSuccess()) {
            return r;
        }
        String path = r.getStr("msg");
        return uplloadExcel(path,fileName);
    }

    /**
     * 导出 Excel ：一个 sheet，合并行单元格
     *
     * @param list      数据 list，要导出的实体
     * @param fileName  导出的文件名，需要加后缀
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射对象
     * @param cellWriteHandler    行合并
     */
    public static R writePostilExcel(List<?> list, String fileName, String sheetName, Object object, CellWriteHandler cellWriteHandler) {
        R r = EasyExcelUtil.writePostilExcel(list, fileName, sheetName, object,cellWriteHandler);
        if (!r.isSuccess()) {
            return r;
        }
        String path = r.getStr("msg");
        return uplloadExcel(path,fileName);
    }
    /**
     * 导出 Excel ：一个 sheet，带表头,表头含有批注
     *
     * @param list      数据 list，要导出的实体
     * @param fileName  导出的文件名，需要加后缀
     * @param sheetName 导入文件的 sheet 名
     * @param object    映射对象
     * @param rowWriteHandler    批注
     */
    public static R writePostilExcel(List<?> list, String fileName, String sheetName, Object object, AbstractRowWriteHandler rowWriteHandler) {
        R r = EasyExcelUtil.writePostilExcel(list, fileName, sheetName, object,rowWriteHandler);
        if (!r.isSuccess()) {
            return r;
        }
        String path = r.getStr("msg");
        return uplloadExcel(path,fileName);
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
        R r = EasyExcelUtil.writeExcelWithHead(list, fileName, sheetName, object,head);
        if (!r.isSuccess()) {
            return r;
        }
        String path = r.getStr("msg");
        return uplloadExcel(path,fileName);
    }

    /**
     * 导出多sheet
     *
     * @param fileName           文件名
     * @param sheetExcelDataList sheet对象
     */
    public static R writeMultiExcel(String fileName, List<SheetExcelData> sheetExcelDataList) {
        R r = EasyExcelUtil.writeMultiExcel(fileName, sheetExcelDataList);
        if (!r.isSuccess()) {
            return r;
        }
        String path = r.getStr("msg");
        return uplloadExcel(path,fileName);
    }

    /**
     * 上传到华为云
     * @param path
     * @param fileName
     * @return
     */
    static R uplloadExcel(String path, String fileName) {
        File file = FileUtil.file(path);
        String suffix = path.substring(path.lastIndexOf("."));
        CloudStorageService storage = OSSFactory.build();
        String url = storage.uploadSuffix(FileUtil.readBytes(file), suffix);
        FileUtil.del(file);
        R rReturn = new R();
        rReturn.set("msg", fileName);
        rReturn.set("data", url);
        return rReturn;
    }
}
