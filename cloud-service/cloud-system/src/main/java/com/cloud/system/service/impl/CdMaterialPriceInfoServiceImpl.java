package com.cloud.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.settle.enums.MaterialPriceInfoSAPEnum;
import com.cloud.settle.enums.SupplementaryOrderStatusEnum;
import com.cloud.settle.feign.RemoteSmsSupplementaryOrderService;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.mapper.CdMaterialPriceInfoMapper;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import com.cloud.system.service.ISysInterfaceLogService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.conn.jco.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SAP成本价格 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Service
public class CdMaterialPriceInfoServiceImpl extends BaseServiceImpl<CdMaterialPriceInfo> implements ICdMaterialPriceInfoService {

    protected final Logger logger = LoggerFactory.getLogger(CdMaterialPriceInfoServiceImpl.class);

    @Autowired
    private CdMaterialPriceInfoMapper cdMaterialPriceInfoMapper;

    @Autowired
    private ISysInterfaceLogService sysInterfaceLogService;

    @Autowired
    private RemoteSmsSupplementaryOrderService remoteSmsSupplementaryOrderService;

    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     *
     * @param materialCode
     * @return CdMaterialPriceInfo
     */
    @Override
    public R checkSynchroSAP(String materialCode) {
        if (StrUtil.isBlank(materialCode)) {
            throw new BusinessException("参数：物料号为空！");
        }
        String dateStr = DateUtils.getTime();
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        //根据物料号  有效期查询SAP价格
        criteria.andEqualTo("materialCode", materialCode)
                .andLessThanOrEqualTo("beginDate", dateStr)
                .andGreaterThanOrEqualTo("endDate", dateStr);
        List<CdMaterialPriceInfo> materialPrices = selectByExample(example);
        if (materialPrices == null || materialPrices.size() == 0) {
            throw new BusinessException("物料号未同步SAP价格！");
        }
        CdMaterialPriceInfo materialPrice = materialPrices.get(0);
        if (materialPrice == null || materialPrice.getProcessPrice() == null || materialPrice.getProcessPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("物料号未同步SAP价格！");
        }
        return R.data(materialPrice);
    }

    /**
     * 根据物料号查询
     *
     * @param materialCodes
     * @param beginDate
     * @param endDate
     * @return Map<materialCode   ,   CdMaterialPriceInfo>
     */
    @Override
    public Map<String, CdMaterialPriceInfo> selectPriceByInMaterialCodeAndDate(List<String> materialCodes, String beginDate, String endDate) {
        return cdMaterialPriceInfoMapper.selectPriceByInMaterialCodeAndDate(materialCodes, beginDate, endDate);
    }

    @Transactional
    @Override
    public R synPrice() {

        //1.查询sms_supplementary_order 待结算状态的物料编号 调SAP接口查原材料价格
        R rMaterialCode = remoteSmsSupplementaryOrderService.materialCodeListByStatus(SupplementaryOrderStatusEnum.WH_ORDER_STATUS_DJS.getCode());
        if (!rMaterialCode.isSuccess()) {
            return rMaterialCode;
        }
        List<String> materialCodeList=rMaterialCode.getCollectData(new TypeReference<List<String>>() {});
         List<CdMaterialPriceInfo> cdMaterialPriceInfoListY = selectSapCharges("YCL",materialCodeList);
        //2.调SAP接口查加工费
        List<CdMaterialPriceInfo> cdMaterialPriceInfoListJ= selectSapCharges("JGF",new ArrayList<>());
        //3.删除cd_material_price_info的所有信息
        cdMaterialPriceInfoMapper.deleteAll();
        //4.新增 cd_material_price_info
        cdMaterialPriceInfoMapper.insertList(cdMaterialPriceInfoListY);
        cdMaterialPriceInfoMapper.insertList(cdMaterialPriceInfoListJ);
        return R.ok();
    }

    /**
     * 调SAP接口查加工费/原材料价格
     * @param lifnr 费用类型
     */
    private List<CdMaterialPriceInfo> selectSapCharges(String lifnr,List<String> materialCodeList) {

        //费用集合
        List<CdMaterialPriceInfo> chargsList = new ArrayList<>();
        JCoDestination destination;
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog();
        sysInterfaceLog.setAppId("SAP");
        sysInterfaceLog.setInterfaceName(SapConstants.ZMM_INT_DDPS_01);
        sysInterfaceLog.setContent("查加工费/原材料价格");
        try {
            //创建与SAP的连接
            destination = JCoDestinationManager.getDestination(SapConstants.ABAP_AS_SAP601);
            //获取repository
            JCoRepository repository = destination.getRepository();
            //获取函数信息
            JCoFunction fm = repository.getFunction(SapConstants.ZMM_INT_DDPS_01);
            if (fm == null) {
                logger.error("调用SAP获取ZMM_INT_DDPS_01函数失败");
                throw new RuntimeException("Function does not exists in SAP system.");
            }
            fm.getImportParameterList().setValue("FYLX",lifnr);
            if("YCL".equals(lifnr)){
                if(CollectionUtils.isEmpty(materialCodeList)){
                    return new ArrayList<>();
                }
                //获取输入参数
                JCoTable inputTable = fm.getTableParameterList().getTable("T_INPUT");
                materialCodeList.forEach(materialCode -> {
                    inputTable.appendRow();
                    inputTable.setValue("MATNR",materialCode);
                });
            }
            //执行函数
            JCoContext.begin(destination);
            fm.execute(destination);
            JCoContext.end(destination);
            //获取返回的Table
            JCoParameterList exportParameter = fm.getExportParameterList();
            String eType = exportParameter.getString("E_TYPE");
            String msg = MaterialPriceInfoSAPEnum.getMsgByCode(eType);
            if (!MaterialPriceInfoSAPEnum.TYPE_S.getCode().equals(eType)) {
                logger.error("SAP返回错误信息：eType {},msg {}", eType,msg);
                sysInterfaceLog.setResults(msg);
                sysInterfaceLogService.insertUseGeneratedKeys(sysInterfaceLog);
                throw new BusinessException(msg);
            }

            JCoTable outTableOutput = fm.getTableParameterList().getTable("T_OUTPUT");
            //从输出table中获取每一行数据
            if (outTableOutput != null && outTableOutput.getNumRows() > 0) {
                //循环取table行数据
                for (int i = 0; i < outTableOutput.getNumRows(); i++) {
                    //设置指针位置
                    outTableOutput.setRow(i);
                    CdMaterialPriceInfo cdMaterialPriceInfo = changeTable(outTableOutput);
                    chargsList.add(cdMaterialPriceInfo);
                }
            }
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "调SAP接口查加工费/原材料价格 : {}", w.toString());
            throw new BusinessException(e.getMessage());
        }

        return chargsList;
    }

    /**
     * 返回信息转换成对象
     * @param outTableOutput
     * @return
     */
    private CdMaterialPriceInfo changeTable(JCoTable outTableOutput){
        CdMaterialPriceInfo cdMaterialPriceInfo = new CdMaterialPriceInfo();
        cdMaterialPriceInfo.setMaterialCode(outTableOutput.getString("MATNR"));
        cdMaterialPriceInfo.setMaterialDesc(outTableOutput.getString("MAKTX"));
        cdMaterialPriceInfo.setMemberCode(outTableOutput.getString("MAKTX"));
        cdMaterialPriceInfo.setMemberName(outTableOutput.getString("NAME1"));
        cdMaterialPriceInfo.setPurchasingGroup(outTableOutput.getString("EKGRP"));
        cdMaterialPriceInfo.setTaxCode(outTableOutput.getString("MWSKZ"));
        cdMaterialPriceInfo.setNetWorth(outTableOutput.getBigDecimal("NETPR"));
        cdMaterialPriceInfo.setKbetr(outTableOutput.getString("KBETR1"));
        cdMaterialPriceInfo.setCurrency(outTableOutput.getString("WAERS"));
        cdMaterialPriceInfo.setPriceUnit(outTableOutput.getString("KPEIN"));
        cdMaterialPriceInfo.setAgencyFee(outTableOutput.getBigDecimal("KBETR"));
        cdMaterialPriceInfo.setBeginDate(outTableOutput.getDate("DATAB"));
        cdMaterialPriceInfo.setEndDate(outTableOutput.getDate("DATBI"));
        cdMaterialPriceInfo.setSapCreatedDate(outTableOutput.getDate("ERDAT"));
        return cdMaterialPriceInfo;
    }

}
