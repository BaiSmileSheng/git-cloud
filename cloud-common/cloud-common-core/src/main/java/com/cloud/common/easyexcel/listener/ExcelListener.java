package com.cloud.common.easyexcel.listener;


import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.cloud.common.easyexcel.validator.EasyExcelValiHelper;
import com.cloud.common.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 *
 * @Author yuanhaoyue swithaoy@gmail.com
 * @Description 监听类，可以自定义
 * @Date 2018-06-05
 * @Time 16:58
 */
@Slf4j
public class ExcelListener extends AnalysisEventListener {

    /**
     * 自定义用于暂时存储data。
     * 可以通过实例获取该值
     */
    private List<Object> datas = new ArrayList<>();

    /**
     * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
     */
    @SneakyThrows(value = NoSuchFieldException.class)
    @Override
    public void invoke(Object object, AnalysisContext context) {
        //数据存储到list，供批量处理，或后续自己业务逻辑处理。
        String errMsg = EasyExcelValiHelper.validateEntity(object);
        if (StrUtil.isNotBlank(errMsg)) {
            throw new BusinessException(errMsg);
        }
        datas.add(object);
        //根据业务自行 do something
        doSomething();
        /*
        如数据过大，可以进行定量分批处理
        if(datas.size()<=100){
            datas.add(object);
        }else {
            doSomething();
            datas = new ArrayList<Object>();
        }
         */
    }

    /**
     * 根据业务自行实现该方法
     */
    private void doSomething() {
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        /*
            datas.clear();
            解析结束销毁不用的资源
         */
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败:{}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            log.error("第{}行，第{}列解析异常", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex());
            throw new BusinessException(StrUtil.format("第{}行，第{}列解析异常", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex()));
        }else{
            throw new BusinessException(exception.getMessage());
        }
    }
}
