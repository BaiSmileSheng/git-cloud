package com.cloud.common.easyexcel.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.service.ExcelCheckManager;
import com.cloud.common.easyexcel.validator.EasyExcelValiHelper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther cs
 * @date 2020/6/18 9:26
 * @description
 */
@Data
public class EasyWithErrorExcelListener<T>  extends AnalysisEventListener<T> {
    //成功结果集
    private List<ExcelImportSucObjectDto> successList = new ArrayList<>();

    //失败结果集
    private List<ExcelImportErrObjectDto> errList = new ArrayList<>();

    //其他结果集
    private List<ExcelImportOtherObjectDto> otherList = new ArrayList<>();

    //处理逻辑service
    private ExcelCheckManager excelCheckManager;

    private List<T> list = new ArrayList<>();

    //excel对象的反射类
    private Class<T> clazz;

    public EasyWithErrorExcelListener(ExcelCheckManager excelCheckManager){
        this.excelCheckManager = excelCheckManager;
    }

    public EasyWithErrorExcelListener(ExcelCheckManager excelCheckManager,Class<T> clazz){
        this.excelCheckManager = excelCheckManager;
        this.clazz = clazz;
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        String errMsg;
        try {
            //根据excel数据实体中的javax.validation + 正则表达式来校验excel数据
            errMsg = EasyExcelValiHelper.validateEntity(t);
        } catch (NoSuchFieldException e) {
            errMsg = "解析数据出错";
            e.printStackTrace();
        }
        if (!StrUtil.isBlank(errMsg)){
            ExcelImportErrObjectDto excelImportErrObjectDto = new ExcelImportErrObjectDto(t, errMsg);
            errList.add(excelImportErrObjectDto);
        }else{
            list.add(t);
        }
        //每1000条处理一次
        if (list.size() > 1000){
            //校验
            ExcelImportResult result = excelCheckManager.checkImportExcel(list);
            successList.addAll(result.getSuccessDtos());
            errList.addAll(result.getErrDtos());
            otherList.addAll(result.getOtherDtos());
            list.clear();
        }
    }

    //所有数据解析完成了 都会来调用
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        ExcelImportResult result = excelCheckManager.checkImportExcel(list);

        successList.addAll(result.getSuccessDtos());
        errList.addAll(result.getErrDtos());
        otherList.addAll(result.getOtherDtos());
        list.clear();
    }


//    /**
//     * @description: 校验excel头部格式，必须完全匹配
//     * @param headMap 传入excel的头部（第一行数据）数据的index,name
//     * @param context
//     */
//    @Override
//    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
//        super.invokeHeadMap(headMap, context);
//        if (clazz != null){
//            try {
//                Map<Integer, String> indexNameMap = getIndexNameMap(clazz);
//                Set<Integer> keySet = indexNameMap.keySet();
//                for (Integer key : keySet) {
//                    if (StrUtil.isBlank(headMap.get(key))){
//                        throw new ExcelAnalysisException("解析excel出错，请传入正确格式的excel");
//                    }
//                    if (!headMap.get(key).equals(indexNameMap.get(key))){
//                        throw new ExcelAnalysisException("解析excel出错，请传入正确格式的excel");
//                    }
//                }
//
//            } catch (NoSuchFieldException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * @description: 获取注解里ExcelProperty的value，用作校验excel
//     * @param clazz
//     */
//    public Map<Integer,String> getIndexNameMap(Class clazz) throws NoSuchFieldException {
//        Map<Integer,String> result = new HashMap<>();
//        Field field;
//        Field[] fields=clazz.getDeclaredFields();
//        for (int i = 0; i <fields.length ; i++) {
//            field=clazz.getDeclaredField(fields[i].getName());
//            field.setAccessible(true);
//            ExcelProperty excelProperty=field.getAnnotation(ExcelProperty.class);
//            if(excelProperty!=null){
//                int index = excelProperty.index();
//                String[] values = excelProperty.value();
//                StringBuilder value = new StringBuilder();
//                for (String v : values) {
//                    value.append(v);
//                }
//                result.put(index,value.toString());
//            }
//        }
//        return result;
//    }
}
