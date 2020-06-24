package com.cloud.system.controller;

import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.sap.conn.jco.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @auther cs
 * @date 2020/5/9 9:31
 * @description
 */
@RestController
@RequestMapping("sapTest")
@Api(tags = "SAP直连测试")
@Slf4j
public class SAPTestController {
    private static final String ABAP_AS_SAP600 = "ABAP_AS_SAP600";


    @ApiOperation(value = "hello ~", notes = "直连SAP")
    @GetMapping("/connect")
    public R connectTest() {
        if(connect()!=null){
            return R.ok();
        }else {
            return R.error();
        }
    }

    /**
     * 获取SAP连接
     * @return	SAP连接对象
     */
    public  JCoDestination connect(){
        JCoDestination destination =null;
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(ABAP_AS_SAP600);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction("ZMM_INT_DDSP_CGXQ");
            if (fm == null) {
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoParameterList input = fm.getImportParameterList();
            input.setValue("INPUT_ERDAT_BEGIN",new Date());
            input.setValue("INPUT_ERDAT_END", new Date());
            //有表的情况，需要先获取表信息
//			JCoTable werksTable = fm.getTableParameterList().getTable("TAB_WERKS");
//			//一般有表的情况，都是可以传List的，所以可以循环往表中存参数
//			for (int i = 0; i < list.length; i++) {
//				//附加表的最后一个新行,行指针,它指向新添加的行。
//				werksTable.appendRow();
//				werksTable.setValue("param", list[i]);
//			}
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoTable outTableOutput = fm.getTableParameterList().getTable("OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    System.out.println(outTableOutput.getString("Param1"));
                    System.out.println(outTableOutput.getString("Param2"));
                }
            }
        } catch (JCoException e) {
            log.error("Connect SAP fault, error msg: " + e.toString());
            throw new BusinessException(e.getMessage());
        }
        return destination;
    }
}
