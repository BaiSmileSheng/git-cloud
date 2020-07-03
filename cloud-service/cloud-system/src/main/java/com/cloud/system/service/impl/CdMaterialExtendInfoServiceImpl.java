package com.cloud.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
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
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.CdMaterialExtendInfoImportErrorVo;
import com.cloud.system.enums.PuttingOutEnum;
import com.cloud.system.enums.ZnAttestationEnum;
import com.cloud.system.enums.LifeCycleEnum;
import com.cloud.system.enums.ProductTypeEnum;
import com.cloud.system.mapper.CdMaterialExtendInfoMapper;
import com.cloud.system.service.ICdMaterialExtendInfoExcelImportService;
import com.cloud.system.service.ICdMaterialExtendInfoService;
import com.cloud.system.util.EasyExcelUtilOSS;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        List<CdMaterialExtendInfo> cdMaterialExtendInfoList = selectByExample(example);
        List<String> materialCodeList = cdMaterialExtendInfoList.stream().map(cdMaterialExtendInfo->{
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
                    if(!"S".equals(flag)){
                        String materialCode = outputTable.getString("MATNR");
                        String msg = outputTable.getString("MESSAGE");
                        remarkBuffer.append(materialCode + msg);
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
            throw new BusinessException("传输成品物料接口异常");
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

    /**
     * 导入
     */
    @Override
    public R importMaterialExtendInfo(MultipartFile file, SysUser sysUser) throws IOException {
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(cdMaterialExtendInfoExcelImportService,
                CdMaterialExtendInfo.class);
        EasyExcel.read(file.getInputStream(),CdMaterialExtendInfo.class,easyExcelListener).sheet().doRead();

        //可以导入的结果集 插入
        List<ExcelImportSucObjectDto> successList=easyExcelListener.getSuccessList();
        if (!CollectionUtils.isEmpty(successList)){
            List<CdMaterialExtendInfo> successResult =successList.stream().map(excelImportSucObjectDto -> {
                CdMaterialExtendInfo cdMaterialExtendInfo = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(),
                        CdMaterialExtendInfo.class);
                cdMaterialExtendInfo.setCreateBy(sysUser.getLoginName());
                return cdMaterialExtendInfo;
            }).collect(Collectors.toList());
            cdMaterialExtendInfoMapper.batchInsertOrUpdate(successResult);
        }
        //错误结果集 导出
        List<ExcelImportErrObjectDto> errList = easyExcelListener.getErrList();
        if (!CollectionUtils.isEmpty(errList)){
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

    @Override
    public <T> ExcelImportResult checkImportExcel(List<T> objects) {
        if (CollUtil.isEmpty(objects)) {
            throw new BusinessException("无导入数据！");
        }
        //错误数据
        List<ExcelImportErrObjectDto> errDtos = new ArrayList<>();
        //可导入数据
        List<ExcelImportSucObjectDto> successDtos = new ArrayList<>();
        List<ExcelImportOtherObjectDto> otherDtos = new ArrayList<>();

        List<CdMaterialExtendInfo> listImport = (List<CdMaterialExtendInfo>) objects;

        for (CdMaterialExtendInfo cdMaterialExtendInfo : listImport) {
            ExcelImportErrObjectDto errObjectDto = new ExcelImportErrObjectDto();
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();

            if (StringUtils.isBlank(cdMaterialExtendInfo.getMaterialCode())) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("成品专用号不能为空：{}", cdMaterialExtendInfo.getMaterialCode()));
                errDtos.add(errObjectDto);
                continue;
            }
            if (StringUtils.isBlank(cdMaterialExtendInfo.getMaterialDesc())) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("成品描述不能为空：{}", cdMaterialExtendInfo.getMaterialDesc()));
                errDtos.add(errObjectDto);
                continue;
            }
            if (StringUtils.isBlank(cdMaterialExtendInfo.getProductType())) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("产品类别不能为空：{}", cdMaterialExtendInfo.getProductType()));
                errDtos.add(errObjectDto);
                continue;
            }
            String productType = cdMaterialExtendInfo.getProductType();
            String productTypeCode = ProductTypeEnum.getCodeByMsg(productType);
            if (StringUtils.isBlank(productTypeCode)) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("产品类别不存在", cdMaterialExtendInfo.getProductType()));
                errDtos.add(errObjectDto);
                continue;
            }
            cdMaterialExtendInfo.setProductType(productTypeCode);
            if (StringUtils.isBlank(cdMaterialExtendInfo.getLifeCycle())) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("生命周期不能为空：{}", cdMaterialExtendInfo.getLifeCycle()));
                errDtos.add(errObjectDto);
                continue;
            }
            String lifeCycle = cdMaterialExtendInfo.getLifeCycle();
            String lifeCycleCode = LifeCycleEnum.getCodeByMsg(lifeCycle);
            if (StringUtils.isBlank(lifeCycleCode)) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("生命周期存在：{}", cdMaterialExtendInfo.getLifeCycle()));
                errDtos.add(errObjectDto);
                continue;
            }
            cdMaterialExtendInfo.setLifeCycle(lifeCycleCode);
            if (StringUtils.isBlank(cdMaterialExtendInfo.getIsPuttingOut())) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("可否加工承揽不能为空：{}", cdMaterialExtendInfo.getIsPuttingOut()));
                errDtos.add(errObjectDto);
                continue;
            }
            String isPuttingOut = cdMaterialExtendInfo.getIsPuttingOut();
            String isPuttingOutCode = PuttingOutEnum.getMsgByCode(isPuttingOut);
            if (StringUtils.isBlank(isPuttingOutCode)) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("可否加工方式不存在：{}", cdMaterialExtendInfo.getIsPuttingOut()));
                errDtos.add(errObjectDto);
                continue;
            }
            cdMaterialExtendInfo.setIsPuttingOut(isPuttingOutCode);
            if (StringUtils.isBlank(cdMaterialExtendInfo.getIsZnAttestation())) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("是否ZN认证不能为空：{}", cdMaterialExtendInfo.getIsZnAttestation()));
                errDtos.add(errObjectDto);
                continue;
            }
            String isZnAttestation = cdMaterialExtendInfo.getIsZnAttestation();
            String isZnAttestatioCode = ZnAttestationEnum.getCodeByMsg(isZnAttestation);
            if (StringUtils.isBlank(isZnAttestatioCode)) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("是否ZN认证方式不存在：{}", cdMaterialExtendInfo.getIsZnAttestation()));
                errDtos.add(errObjectDto);
                continue;
            }
            cdMaterialExtendInfo.setIsZnAttestation(isZnAttestatioCode);
            if (null == cdMaterialExtendInfo.getEstablishDate()) {
                errObjectDto.setObject(cdMaterialExtendInfo);
                errObjectDto.setErrMsg(StrUtil.format("是否ZN认证不能为空：{}", cdMaterialExtendInfo.getEstablishDate()));
                errDtos.add(errObjectDto);
                continue;
            }
            cdMaterialExtendInfo.setCreateTime(new Date());
            cdMaterialExtendInfo.setDelFlag("0");
            sucObjectDto.setObject(cdMaterialExtendInfo);
            successDtos.add(sucObjectDto);
        }
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }
}
