package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import com.alibaba.excel.EasyExcel;
import com.cloud.common.constant.SapConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdMaterialExtendInfoImportErrorVo;
import com.cloud.system.domain.vo.CdMaterialExtendInfoImportVo;
import com.cloud.system.enums.GetStockEnum;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.enums.ProductTypeEnum;
import com.cloud.system.enums.PuttingOutEnum;
import com.cloud.system.enums.ZnAttestationEnum;
import com.cloud.system.mapper.CdMaterialExtendInfoMapper;
import com.cloud.system.service.ICdMaterialExtendInfoExcelImportService;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import com.cloud.system.service.ICdMaterialInfoService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.sap.conn.jco.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
public class CdMaterialExtendInfoServiceImpl extends BaseServiceImpl<CdMaterialExtendInfo> implements ICdMaterialExtendInfoService, ICdMaterialExtendInfoExcelImportService {

    private final Logger logger = LoggerFactory.getLogger(CdMaterialExtendInfoServiceImpl.class);

    @Autowired
    private CdMaterialExtendInfoMapper cdMaterialExtendInfoMapper;
    @Autowired
    private ICdMaterialInfoService cdMaterialInfoService;

    @Autowired
    private ICdMaterialExtendInfoExcelImportService cdMaterialExtendInfoExcelImportService;

    private static final String PRODUCT_TYPE = "product_type";//数据字典类型 产品类别
    private static final String OUT_SOURCE_TYPE = "out_source_type";//数据字典类型 委外方式

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
        criteria.andEqualTo("isGetStock",GetStockEnum.IS_GET_STOCK_1.getCode());
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = selectByExample(example);
        List<String> materialCodeList = cdMaterialExtendInfoList.stream().map(cdMaterialExtendInfo -> {
            return cdMaterialExtendInfo.getMaterialCode();
        }).collect(Collectors.toList());
        //2.传SAP
        return fromSAPDDPS03(materialCodeList);
    }

    /**
     * Description:  根据多个成品专用号查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    @Override
    public R selectByMaterialCodeList(List<Dict> list) {
        return R.data(cdMaterialExtendInfoMapper.selectByMaterialCodeList(list));
    }

    /**
     * Description:  根据物料查询一条数据
     * Param: [materialCode]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/23
     */
    @Override
    public R selectOneByMaterialCode(String materialCode) {
        return R.data(cdMaterialExtendInfoMapper.selectOneByMaterialCode(materialCode));
    }

    /**
     * 连接SAP 传输成品物料号
     *
     * @param materialCodeList
     * @return
     */
    private R fromSAPDDPS03(List<String> materialCodeList) {
        JCoDestination destination;

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
                inputTableW.setValue("MATNR", materialCode.toUpperCase());
            }

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
                    if (!"S".equals(flag)) {
                        String materialCode = outputTable.getString("MATNR");
                        String msg = outputTable.getString("MESSAGE");
                        remarkBuffer.append(materialCode + msg);
                        logger.error("传输成品物料异常信息异常 materialCode:{},res:{}", materialCode, msg);
                    }
                }
            }
            return R.ok();
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            logger.error(
                    "传输成品物料接口异常: {}", w.toString());
            throw new BusinessException("传输成品物料接口异常");
        }
    }

    /**
     * 根据生命周期查询物料号集合
     *
     * @param lifeCycle
     * @return
     */
    @Override
    public R selectMaterialCodeByLifeCycle(String lifeCycle) {
        return R.data(cdMaterialExtendInfoMapper.selectMaterialCodeByLifeCycle(lifeCycle));
    }

    /**
     * 根据物料号集合查询
     *
     * @param materialCodes
     * @return
     */
    @Override
    public R selectInfoInMaterialCodes(List<String> materialCodes) {
        return R.data(cdMaterialExtendInfoMapper.selectInfoInMaterialCodes(materialCodes));
    }

    /**
     * 导入
     */
    @Override
    public R importMaterialExtendInfo(MultipartFile file, SysUser sysUser) throws IOException {
        ProductTypeEnum.init();
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(cdMaterialExtendInfoExcelImportService,
                CdMaterialExtendInfoImportVo.class);
        EasyExcel.read(file.getInputStream(), CdMaterialExtendInfoImportVo.class, easyExcelListener).sheet().doRead();

        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList = easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)) {
            List<CdMaterialExtendInfo> successResult = successList.stream().map(excelImportSucObjectDto -> {
                CdMaterialExtendInfo cdMaterialExtendInfo = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(),
                        CdMaterialExtendInfo.class);
                cdMaterialExtendInfo.setCreateBy(sysUser.getLoginName());
                cdMaterialExtendInfo.setUpdateBy(sysUser.getLoginName());
                cdMaterialExtendInfo.setUpdateTime(new Date());
                return cdMaterialExtendInfo;
            }).collect(Collectors.toList());
            cdMaterialExtendInfoMapper.batchInsertOrUpdate(successResult);
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (!CollectionUtils.isEmpty(errList)) {
            List<CdMaterialExtendInfoImportErrorVo> errorResults = errList.stream().map(excelImportErrObjectDto -> {
                CdMaterialExtendInfoImportErrorVo cdMaterialExtendInfoImportErrorVo = BeanUtil.copyProperties(
                        excelImportErrObjectDto.getObject(),
                        CdMaterialExtendInfoImportErrorVo.class);
                cdMaterialExtendInfoImportErrorVo.setErrorMessage(excelImportErrObjectDto.getErrMsg());
                return cdMaterialExtendInfoImportErrorVo;
            }).collect(Collectors.toList());
            //导出excel
            return EasyExcelUtilOSS.writeExcel(errorResults, "成品物料信息导入错误信息.xlsx", "sheet",
                    new CdMaterialExtendInfoImportErrorVo());
        }
        return R.ok();
    }
    /**
     * Description:  批量新增或更新
     * Param: [cdMaterialExtendInfos]
     * return:
     * Author: ltq
     * Date: 2020/9/7
     */
    @Override
    public void batchMaterialInsertOrUpdate(List<CdMaterialExtendInfo> cdMaterialExtendInfos) {
       cdMaterialExtendInfoMapper.batchMaterialInsertOrUpdate(cdMaterialExtendInfos);
    }

    @Override
    public <T> ExcelImportResult checkImportExcel(List<T> objects) {
        if (CollUtil.isEmpty(objects)) {
            return new ExcelImportResult(new ArrayList<>());
        }
        //错误数据
        List<ExcelImportErrObjectDto> errDtos = new ArrayList<>();
        //可导入数据
        List<ExcelImportSucObjectDto> successDtos = new ArrayList<>();
        List<ExcelImportOtherObjectDto> otherDtos = new ArrayList<>();

        List<CdMaterialExtendInfoImportVo> listImport = (List<CdMaterialExtendInfoImportVo>) objects;

        for (CdMaterialExtendInfoImportVo cdMaterialExtendInfo : listImport) {
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            CdMaterialExtendInfo cdMaterialExtendInfoReq = new CdMaterialExtendInfo();
            BeanUtils.copyProperties(cdMaterialExtendInfo,cdMaterialExtendInfoReq);
            StringBuffer errMsgBuffer = new StringBuffer();
            if (StringUtils.isBlank(cdMaterialExtendInfo.getMaterialCode())) {
                errMsgBuffer.append("成品专用号不能为空;");
            }
            if(StringUtils.isNotBlank(cdMaterialExtendInfo.getMaterialCode())){
                Example example = new Example(CdMaterialInfo.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("materialCode",cdMaterialExtendInfo.getMaterialCode());
                CdMaterialInfo materialInfo =
                        cdMaterialInfoService.findByExampleOne(example);
                if (BeanUtil.isEmpty(materialInfo)) {
                    errMsgBuffer.append("成品专用号在主数据中不存在,请维护;");
                } else {
                    cdMaterialExtendInfoReq.setEstablishDate(materialInfo.getMdmCreateTime());
                    cdMaterialExtendInfoReq.setMaterialDesc(materialInfo.getMaterialDesc());
                }
            }

            if(StringUtils.isNotBlank(cdMaterialExtendInfo.getProductType())){
                String productType = cdMaterialExtendInfo.getProductType();
                ProductTypeEnum.init();
                String productTypeCode = ProductTypeEnum.getCodeByMsg(productType);
                if (StringUtils.isBlank(productTypeCode) || productTypeCode.equals(productType)) {
                    errMsgBuffer.append("产品类别不存在;");
                }
                cdMaterialExtendInfoReq.setProductType(productTypeCode);
            }

            if (StringUtils.isBlank(cdMaterialExtendInfo.getLifeCycle())) {
                errMsgBuffer.append("生命周期不能为空;");
            }
            if(StringUtils.isNotBlank(cdMaterialExtendInfo.getLifeCycle())){
                String lifeCycle = cdMaterialExtendInfo.getLifeCycle();
                String lifeCycleCode = LifeCycleEnum.getCodeByMsg(lifeCycle);
                if (StringUtils.isBlank(lifeCycleCode) || lifeCycleCode.equals(lifeCycle)) {
                    errMsgBuffer.append("生命周期不存在;");
                }
                cdMaterialExtendInfoReq.setLifeCycle(lifeCycleCode);
            }

            if (StringUtils.isBlank(cdMaterialExtendInfo.getIsZnAttestation())) {
                errMsgBuffer.append("是否ZN认证不能为空;");
            }
            if(StringUtils.isNotBlank(cdMaterialExtendInfo.getIsZnAttestation())){
                String isZnAttestation = cdMaterialExtendInfo.getIsZnAttestation();
                String isZnAttestatioCode = ZnAttestationEnum.getCodeByMsg(isZnAttestation);
                if (StringUtils.isBlank(isZnAttestatioCode) || isZnAttestatioCode.equals(isZnAttestation)) {
                    errMsgBuffer.append("是否ZN认证方式不存在;");
                }
                cdMaterialExtendInfoReq.setIsZnAttestation(isZnAttestatioCode);
            }
            if(StringUtils.isBlank(cdMaterialExtendInfo.getIsGetStock())){
                cdMaterialExtendInfoReq.setIsGetStock(GetStockEnum.IS_GET_STOCK_0.getCode());
            }else {
                String isGetStock = cdMaterialExtendInfo.getIsGetStock();
                String isGetStockCode = GetStockEnum.getCodeByMsg(isGetStock);
                if (StringUtils.isBlank(isGetStockCode) || isGetStock.equals(isGetStockCode)) {
                    errMsgBuffer.append("获取库存不存在,请填写是或否;");
                }
                cdMaterialExtendInfoReq.setIsGetStock(isGetStockCode);
            }

            String errMsgBufferString = errMsgBuffer.toString();
            if(StringUtils.isNotBlank(errMsgBufferString)){
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(errMsgBufferString);
                errDtos.add(errObjectDto);
                continue;
            }
            cdMaterialExtendInfoReq.setIsPuttingOut(PuttingOutEnum.IS_PUTTING_OUT_1.getCode());
            cdMaterialExtendInfoReq.setCreateTime(new Date());
            cdMaterialExtendInfoReq.setDelFlag("0");
            sucObjectDto.setObject(cdMaterialExtendInfoReq);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }
}
