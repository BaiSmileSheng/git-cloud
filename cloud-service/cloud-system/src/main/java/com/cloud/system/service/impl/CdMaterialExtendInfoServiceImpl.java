package com.cloud.system.service.impl;

import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.mapper.CdMaterialExtendInfoMapper;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import com.cloud.system.service.ISysInterfaceLogService;
import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料扩展信息 Service业务层处理
 *
 * @author lihongia
 * @date 2020-06-15
 */
@Service
public class CdMaterialExtendInfoServiceImpl extends BaseServiceImpl<CdMaterialExtendInfo> implements ICdMaterialExtendInfoService {

    private final Logger logger = LoggerFactory.getLogger(CdMaterialExtendInfoServiceImpl.class);

    @Autowired
    private CdMaterialExtendInfoMapper cdMaterialExtendInfoMapper;

    @Autowired
    private ISysInterfaceLogService sysInterfaceLogService;

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    @Override
    public R timeSycMaterialCode() {
        //1.查cd_material_extend_info 所有物料号
        Example example = new Example(CdMaterialExtendInfo.class);
        Example.Criteria criteria = example.createCriteria();
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = selectByExample(example);
        List<String> materialCodeList = cdMaterialExtendInfoList.stream().map(cdMaterialExtendInfo->{
            return cdMaterialExtendInfo.getMaterialCode();
        }).collect(Collectors.toList());
        //2.传SAP
        return fromSAPDDPS03(materialCodeList);
    }

    /**
     * 连接SAP 传输成品物料号
     * @param materialCodeList
     * @return
     */
    private R fromSAPDDPS03(List<String> materialCodeList) {
        JCoDestination destination;
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog();
        sysInterfaceLog.setAppId("SAP");
        sysInterfaceLog.setInterfaceName(SapConstants.ZSD_INT_DDPS_03);
        sysInterfaceLog.setCreateBy("定时任务");
        sysInterfaceLog.setCreateTime(new Date());

        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZSD_INT_DDPS_03);
            if (fm == null) {
                logger.error("传输成品物料接口 调用SAP获取ZSD_INT_DDPS_03函数失败");
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            //获取输入参数
            JCoTable inputTableW = fm.getTableParameterList().getTable("MATNR");
            for (String materialCode : materialCodeList) {
                inputTableW.appendRow();
                inputTableW.setValue("MATNR", materialCode);
            }

            sysInterfaceLog.setContent(materialCodeList + "");

            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);

            StringBuffer remarkBuffer = new StringBuffer("传输成品物料异常信息");
            //返回信息
            JCoTable outputTable = fm.getTableParameterList().getTable("OUTPUT");
            if (outputTable != null && outputTable.getNumRows() > 0) {
                //循环取table行数据
                for (int i = 0; i < outputTable.getNumRows(); i++) {
                    //设置指针位置
                    outputTable.setRow(i);
                    String flag = outputTable.getString("FLAG");
                    if(!"S".equals(flag)){
                        String materialCode = outputTable.getString("MATNR");
                        String msg = outputTable.getString("MESSAGE");
                        remarkBuffer.append(materialCode + msg);
                        sysInterfaceLog.setRemark(remarkBuffer.toString());
                        logger.error("传输成品物料异常信息异常 materialCode:{},res:{}",materialCode,msg);
                    }
                }
            }
            return R.ok();
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "传输成品物料接口异常: {}", w.toString());
            sysInterfaceLog.setRemark(e.getMessage());
            throw new BusinessException("传输成品物料接口异常");
        } finally {
            sysInterfaceLogService.insertSelective(sysInterfaceLog);
        }
    }

    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
	@Override
	public R selectMaterialCodeByLifeCycle(String lifeCycle){
		 return R.data(cdMaterialExtendInfoMapper.selectMaterialCodeByLifeCycle(lifeCycle));
	}
    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    @Override
    public R selectInfoInMaterialCodes(List<String> materialCodes) {
        return R.data(cdMaterialExtendInfoMapper.selectInfoInMaterialCodes(materialCodes));
    }
}
