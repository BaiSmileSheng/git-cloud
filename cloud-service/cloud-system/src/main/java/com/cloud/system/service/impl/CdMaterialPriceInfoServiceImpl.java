package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.settle.domain.entity.SmsSupplementaryOrder;
import com.cloud.settle.enums.MaterialPriceInfoSAPEnum;
import com.cloud.settle.feign.RemoteSmsSupplementaryOrderService;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.system.domain.entity.SysInterfaceLog;
import com.cloud.system.enums.PriceTypeEnum;
import com.cloud.system.mapper.CdMaterialPriceInfoMapper;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import com.cloud.system.service.ICdSettleProductMaterialService;
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
import java.util.stream.Collectors;

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
    @Autowired
    private ICdSettleProductMaterialService cdSettleProductMaterialService;

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
    public R synPriceJGF() {
        //1.调SAP接口查加工费
        List<CdMaterialPriceInfo> cdMaterialPriceInfoListJ= selectSapCharges("JGF",new ArrayList<>());
        cdMaterialPriceInfoListJ.forEach(cdMaterialPriceInfo ->{
            //净价值即加工费
            cdMaterialPriceInfo.setProcessPrice(cdMaterialPriceInfo.getNetWorth());
            cdMaterialPriceInfo.setPriceType(PriceTypeEnum.PRICE_TYPE_1.getCode());
        });
        //3.新增 cd_material_price_info
        cdMaterialPriceInfoMapper.batchInsertOrUpdate(cdMaterialPriceInfoListJ);
        return R.ok();
    }

    @Transactional
    @Override
    public R synPriceYCL() {

        //1.查询sms_supplementary_order 待结算状态的物料编号 调SAP接口查原材料价格
        String createTimeStart = DateUtils.getMonthFirstTime(-1);
        String createTimeEnd = DateUtils.getDate();
        R rMaterialList= remoteSmsSupplementaryOrderService.listByTime(createTimeStart,createTimeEnd);
        if (!rMaterialList.isSuccess()) {
            return rMaterialList;
        }
        //2.调SAP接口查原材料价格
        List<SmsSupplementaryOrder> materialList = rMaterialList.getCollectData(new TypeReference<List<SmsSupplementaryOrder>>() {});
        List<String> materialCodeList =  new ArrayList<>();
        materialList.forEach(smsSupplementaryOrder ->{
            if(!materialCodeList.contains(smsSupplementaryOrder.getRawMaterialCode())){
                materialCodeList.add(smsSupplementaryOrder.getRawMaterialCode());
            }
        });
        List<CdMaterialPriceInfo> cdMaterialPriceInfoListY = selectSapCharges("YCL",materialCodeList);
        cdMaterialPriceInfoListY.forEach(cdMaterialPriceInfo -> {
            cdMaterialPriceInfo.setPriceType(PriceTypeEnum.PRICE_TYPE_0.getCode());
        });
        cdMaterialPriceInfoMapper.batchInsertOrUpdate(cdMaterialPriceInfoListY);
        return R.ok();
    }
    /**
     * Description:  根据成品物料号查询SAP成本价格
     * Param: [materialCodes]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/8/3
     */
    @Override
    public R selectMaterialPrice(List<CdSettleProductMaterial> list) {
        Example example = new Example(CdSettleProductMaterial.class);
        Example.Criteria criteria = example.createCriteria();
        if (ObjectUtil.isEmpty(list) || list.size() <= 0) {
            return R.error("排产订单导入查询非自制订单的SAP价格，传入参数为空!");
        }
        List<CdSettleProductMaterial> settleProductMaterials = new ArrayList<>();
        list.forEach(o ->{
            criteria.andEqualTo("productMaterialCode",o.getProductMaterialCode());
            criteria.andEqualTo("outsourceWay",o.getOutsourceWay());
            CdSettleProductMaterial cdSettleProductMaterial = cdSettleProductMaterialService.findByExampleOne(example);
            if (BeanUtil.isNotEmpty(cdSettleProductMaterial)) {
                settleProductMaterials.add(cdSettleProductMaterial);
            }
        });
        if (ObjectUtil.isEmpty(settleProductMaterials) || settleProductMaterials.size() <= 0) {
            logger.error("根据物料号查询物料与加工费号关系数据为空！");
            return R.error("根据物料号查询物料与加工费号关系数据为空！");
        }
        List<String> rawMaterialCodeList = settleProductMaterials
                .stream().filter(s -> StrUtil.isNotBlank(s.getRawMaterialCode()))
                .map(CdSettleProductMaterial::getRawMaterialCode)
                .collect(Collectors.toList());
        Example materialPriceExample = new Example(CdMaterialPriceInfo.class);
        Example.Criteria materialPriceCriteria = materialPriceExample.createCriteria();
        if (ObjectUtil.isEmpty(rawMaterialCodeList) || rawMaterialCodeList.size() <=  0) {
            return R.error("排产订单导入查询非自制订单的SAP价格，未查询出加工费号！");
        }
        materialPriceCriteria.andIn("materialCode",rawMaterialCodeList);
        List<CdMaterialPriceInfo> cdMaterialPriceInfos =
                cdMaterialPriceInfoMapper.selectByExample(materialPriceExample);
        return R.data(cdMaterialPriceInfos);
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
            if(!CollectionUtils.isEmpty(materialCodeList)){
                //获取输入参数
                JCoTable inputTable = fm.getTableParameterList().getTable("T_INPUT");
                materialCodeList.forEach(materialCode -> {
                    inputTable.appendRow();
                    inputTable.setValue("MATNR",materialCode.toUpperCase());
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
            sysInterfaceLog.setResults("调SAP接口查加工费/原材料价格异常");
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "调SAP接口查加工费/原材料价格 : {}", w.toString());
            throw new BusinessException(e.getMessage());
        }finally {
            sysInterfaceLogService.insertSelectiveNoTransactional(sysInterfaceLog);
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
        cdMaterialPriceInfo.setMemberCode(outTableOutput.getString("LIFNR"));
        cdMaterialPriceInfo.setMemberName(outTableOutput.getString("NAME1"));
        cdMaterialPriceInfo.setPurchasingGroup(outTableOutput.getString("EKGRP"));
        cdMaterialPriceInfo.setPurchasingOrganization(outTableOutput.getString("EKORG"));
        cdMaterialPriceInfo.setTaxCode(outTableOutput.getString("MWSKZ"));
        cdMaterialPriceInfo.setNetWorth(outTableOutput.getBigDecimal("NETPR"));
        cdMaterialPriceInfo.setKbetr(outTableOutput.getString("KBETR1"));
        cdMaterialPriceInfo.setCurrency(outTableOutput.getString("WAERS"));
        cdMaterialPriceInfo.setPriceUnit(outTableOutput.getString("KPEIN"));
        cdMaterialPriceInfo.setUnit(outTableOutput.getString("KMEIN"));
        cdMaterialPriceInfo.setAgencyFee(outTableOutput.getBigDecimal("KBETR2"));
        cdMaterialPriceInfo.setBeginDate(outTableOutput.getDate("DATAB"));
        cdMaterialPriceInfo.setEndDate(outTableOutput.getDate("DATBI"));
        cdMaterialPriceInfo.setSapCreatedDate(outTableOutput.getDate("ERDAT"));
        return cdMaterialPriceInfo;
    }

}
