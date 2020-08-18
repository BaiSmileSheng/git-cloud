package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.constant.ActProcessContants;
import com.cloud.activiti.domain.entity.vo.ActBusinessVo;
import com.cloud.activiti.domain.entity.vo.ActStartProcessVo;
import com.cloud.activiti.feign.RemoteActOmsProductionOrderService;
import com.cloud.activiti.feign.RemoteActTaskService;
import com.cloud.common.constant.*;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.DTO.ExcelImportErrObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportOtherObjectDto;
import com.cloud.common.easyexcel.DTO.ExcelImportResult;
import com.cloud.common.easyexcel.DTO.ExcelImportSucObjectDto;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.easyexcel.listener.EasyWithErrorExcelListener;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.ListCommonUtil;
import com.cloud.common.utils.StringUtils;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.order.domain.entity.*;
import com.cloud.order.domain.entity.vo.Oms2weeksDemandOrderEditImport;
import com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo;
import com.cloud.order.domain.entity.vo.OmsProductionOrderMailVo;
import com.cloud.order.enums.ProductionOrderStatusEnum;
import com.cloud.order.mail.MailService;
import com.cloud.order.mapper.OmsProductionOrderMapper;
import com.cloud.order.service.*;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.order.webService.wms.OdsRawOrderOutStorageDTO;
import com.cloud.order.webService.wms.OutStorageResult;
import com.cloud.order.webService.wms.RfWebService;
import com.cloud.settle.domain.entity.SmsSettleInfo;
import com.cloud.settle.enums.SettleInfoOrderStatusEnum;
import com.cloud.settle.feign.RemoteSettleInfoService;
import com.cloud.system.domain.entity.*;
import com.cloud.system.domain.vo.SysUserRights;
import com.cloud.system.enums.OutSourceTypeEnum;
import com.cloud.system.feign.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.mail.MessagingException;
import javax.xml.namespace.QName;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * 排产订单 Service业务层处理
 *
 * @author cs
 * @date 2020-05-29
 */
@Service
@Slf4j
public class OmsProductionOrderServiceImpl extends BaseServiceImpl<OmsProductionOrder> implements IOmsProductionOrderService, IOmsProductOrderImportService {
    private static final String PRODUCT_FACTORY_CODE = "productFactoryCode";
    private static final String PRODUCT_MATERIAL_CODE = "productMaterialCode";
    private static final String PRODUCT_LINE_CODE = "productLineCode";
    private static final String BOM_VERSION = "bomVersion";
    private static final String PRODUCT_MATERIAL = "materialType";
    private static final String PRODUCT_MATERIAL_TYPE = "HALB";

    private static final String PRODUCT_ORDER_SEQ = "product_order_seq";
    private static final int PRODUCT_ORDER_LENGTH = 4;

    private static final String NO_OUTSOURCE_REMARK = "该物料只允许自制";

    private static final String NO_UPH_REMARK = "缺少UPH节拍数据";

    private static final String NO_QUOTA_REMARK = "缺少产品定员、分公司主管、班长信息";

    private static final String NO_LIFECYCLE_REMARK = "缺少生命周期信息";

    private static final String NO_BOM_REMARK = "缺少BOM清单数据";

    private static final String NO_MATERIAL_RRICE = "缺少SAP成本价格";

    private final static String YYYY_MM_DD = "yyyy-MM-dd";//时间格式

    private static final String ZN_ATTESTATION = "0";//zn认证，否
    //可否加工承揽
    private static final String IS_PUTTING_OUT_YES = "1";//可
    private static final String IS_PUTTING_OUT_NO = "0";//不可
    //加工承揽方式
    private static final String PUTTING_OUT_ZERO = "0";
    private static final String PUTTING_OUT_ONE = "1";
    private static final int WMS_PRODUCT_ORDER_LENGTH = 12;//向wms传生产订单号长度

    private static final String[] parsePatterns = {"yyyy.MM.dd", "yyyy/MM/dd"};

    @Value("${webService.findAllCodeForJIT.urlClaim}")
    private String urlClaim;

    @Value("${webService.findAllCodeForJIT.namespaceURL}")
    private String namespaceURL;

    @Value("${webService.findAllCodeForJIT.localPart}")
    private String localPart;


    @Autowired
    private OmsProductionOrderMapper omsProductionOrderMapper;
    @Autowired
    private RemoteMaterialExtendInfoService remoteMaterialExtendInfoService;
    @Autowired
    private RemoteMaterialService remoteMaterialService;
    @Autowired
    private RemoteFactoryLineInfoService remoteFactoryLineInfoService;
    @Autowired
    private RemoteBomService remoteBomService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;

    @Autowired
    private RemoteUserService userService;

    @Autowired
    private MailService mailService;
    @Autowired
    private IOmsProductionOrderDetailService omsProductionOrderDetailService;
    @Autowired
    private IOmsProductionOrderDetailDelService omsProductionOrderDetailDelService;
    @Autowired
    private IOmsProductionOrderDelService omsProductionOrderDelService;
    @Autowired
    private IOmsRawMaterialFeedbackService omsRawMaterialFeedbackService;
    @Autowired
    private RemoteActOmsProductionOrderService remoteActOmsProductionOrderService;
    @Autowired
    private RemoteCdProductOverdueService remoteCdProductOverdueService;
    @Autowired
    private IOrderFromSap601InterfaceService orderFromSap601InterfaceService;
    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;
    @Autowired
    private RemoteCdMaterialPriceInfoService remoteCdMaterialPriceInfoService;
    @Autowired
    private RemoteSettleInfoService remoteSettleInfoService;
    @Autowired
    private RemoteCdSettleProductMaterialService remoteCdSettleProductMaterialService;
    @Autowired
    private RemoteInterfaceLogService remoteInterfaceLogService;
    @Autowired
    private IOmsProductOrderImportService omsProductOrderImportService;
    @Autowired
    private RemoteActTaskService remoteActTaskService;

    /**
     * Description:  排产订单导入
     * Param: [list, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    @SneakyThrows
    @GlobalTransactional
    public R importProductOrder(MultipartFile file, SysUser sysUser) {
        EasyWithErrorExcelListener easyExcelListener = new EasyWithErrorExcelListener(omsProductOrderImportService, OmsProductionOrderExportVo.class);
        EasyExcel.read(file.getInputStream(), OmsProductionOrderExportVo.class, easyExcelListener).sheet().doRead();
        //全部数据
        List<ExcelImportSucObjectDto> excelList = easyExcelListener.getSuccessList();
        List<ExcelImportErrObjectDto> errObjectDtos = easyExcelListener.getErrList();
        //错误结果集 导出
        List<OmsProductionOrderExportVo> errorResults = new ArrayList<>();
        if (errObjectDtos.size() > 0){
            errorResults = errObjectDtos.stream().map(excelImportErrObjectDto -> {
                OmsProductionOrderExportVo omsProductionOrderExportVo = BeanUtil.copyProperties(excelImportErrObjectDto.getObject(), OmsProductionOrderExportVo.class);
                omsProductionOrderExportVo.setExportRemark(excelImportErrObjectDto.getErrMsg());
                return omsProductionOrderExportVo;
            }).collect(Collectors.toList());
        }
        if (ObjectUtil.isEmpty(excelList) || excelList.size() <= 0) {
            if (errorResults.size() > 0 ) {
                //导出excel
                return EasyExcelUtilOSS.writeExcel(errorResults, "排产订单导入失败数据.xlsx", "sheet", new OmsProductionOrderExportVo());
            }
            log.error("未解析出导入数据，请核实排产订单导入文件！");
            return R.error("未解析出导入数据，请核实排产订单导入文件！");
        }
        List<OmsProductionOrderExportVo> list = new ArrayList<>();
        if (excelList.size() > 0) {
            list = excelList.stream().map(excelImportSucObjectDto -> {
                OmsProductionOrderExportVo omsProductionOrderExportVo = BeanUtil.copyProperties(excelImportSucObjectDto.getObject(), OmsProductionOrderExportVo.class);
                return omsProductionOrderExportVo;
            }).collect(Collectors.toList());
        }
        //无法导入数据
        List<OmsProductionOrderExportVo> exportList = list.stream().filter(o -> StrUtil.isNotBlank(o.getExportRemark())).collect(toList());
        list = list.stream().filter(o -> !exportList.contains(o)).collect(Collectors.toList());
        if ((ObjectUtil.isEmpty(list) || list.size() <= 0)
                && (ObjectUtil.isNotEmpty(exportList) && exportList.size() > 0)) {
            exportList.addAll(errorResults);
            return EasyExcelUtilOSS.writeExcel(exportList, "排产订单导入失败数据.xlsx", "sheet", new OmsProductionOrderExportVo());
        }
        //1-8、排产订单号：根据生成规则生成排产订单号；
        List<OmsProductionOrder> omsProductionOrders = list.stream().map(o -> {
            OmsProductionOrder omsProductionOrder = new OmsProductionOrder();
            BeanUtils.copyProperties(o, omsProductionOrder);
            //获取排产订单号
            R seqMap = remoteSequeceService.selectSeq(PRODUCT_ORDER_SEQ, PRODUCT_ORDER_LENGTH);
            if (!seqMap.isSuccess()) {
                log.error("获取排产订单号失败,原因：" + seqMap.get("msg"));
                throw new BusinessException("获取排产订单号失败！");
            }
            String seq = seqMap.getStr("data");
            //PC+年月日+4位顺序号
            String orderCode = StrUtil.concat(true, "PC", DateUtils.dateTime(), seq);
            omsProductionOrder.setOrderCode(orderCode);
            omsProductionOrder.setCreateBy(sysUser.getLoginName());
            omsProductionOrder.setCreateTime(new Date());
            omsProductionOrder.setStatus(ProductOrderConstants.STATUS_ZERO);
            omsProductionOrder.setDelFlag("0");
            omsProductionOrder.setAuditStatus("0");
            omsProductionOrder.setProductStartDate(formatDateString(o.getProductStartDate()));
            omsProductionOrder.setProductEndDate(formatDateString(o.getProductEndDate()));
            omsProductionOrder.setDeliveryDate(formatDateString(o.getDeliveryDate()));
            return omsProductionOrder;
        }).collect(toList());
        //BOM拆解流程
        List<Dict> paramsMapList = list.stream().map(omsProductionOrder ->
                new Dict().set(PRODUCT_FACTORY_CODE, omsProductionOrder.getProductFactoryCode())
                        .set(PRODUCT_MATERIAL_CODE, omsProductionOrder.getProductMaterialCode())
                        .set(BOM_VERSION, omsProductionOrder.getBomVersion())
        ).distinct().collect(toList());
        R bomInfoMap = remoteBomService.selectBomList(paramsMapList);
        if (!bomInfoMap.isSuccess()) {
            log.error("调用system服务根据生产工厂、成品专用号、bom版本查询BOM信息失败：" + bomInfoMap.get("msg"));
            throw new BusinessException("调用system服务根据生产工厂、成品专用号、bom版本查询BOM信息失败!" + bomInfoMap.get("msg"));
        }
        List<CdBomInfo> bomInfoList = bomInfoMap.getCollectData(new TypeReference<List<CdBomInfo>>() {
        });
        R bomDisassemblyResult = bomDisassembly(omsProductionOrders, bomInfoList, sysUser);
        if (!bomDisassemblyResult.isSuccess()) {
            log.error("BOM拆解流程失败，原因：" + bomDisassemblyResult.get("msg"));
            return R.error("BOM拆解流程失败!");
        }
        if (omsProductionOrders.size() > 0) {
            omsProductionOrderMapper.insertList(omsProductionOrders);
            List<String> orderCodes = omsProductionOrders.stream().map(OmsProductionOrder::getOrderCode).collect(Collectors.toList());
            omsProductionOrders = omsProductionOrderMapper.selectByOrderCode(orderCodes);
        }
        //4、3版本审批校验，邮件通知排产员3版本审批
        List<OmsProductionOrder> checkOmsProductList = checkThreeVersion(omsProductionOrders, sysUser);
        //5、超期库存审批流程，邮件通知订单经理
        List<OmsProductionOrder> checkOverStockList = checkOverStock(checkOmsProductList, sysUser);
        //6、、超期未关闭订单审批校验，邮件通知工厂订单 - 工厂小微主 超期未关闭订单审批
        List<OmsProductionOrder> insertProductOrderList = checkOverdueNotCloseOrder(checkOverStockList, sysUser);
        //更新排产订单的审核状态
        if (insertProductOrderList.size() > 0) {
            omsProductionOrderMapper.updateBatchByPrimaryKeySelective(insertProductOrderList);
        }
        if (exportList.size() > 0) {
            return EasyExcelUtilOSS.writeExcel(exportList, "排产订单导入失败数据.xlsx", "sheet", new OmsProductionOrderExportVo());
        } else {
            return R.ok();
        }
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
        //其他导入数据
        List<ExcelImportOtherObjectDto> otherDtos = new ArrayList<>();
        List<OmsProductionOrderExportVo> listImport = (List<OmsProductionOrderExportVo>) objects;
        //校验非空
        ValidatorUtils.validateEntity(listImport);
        List<Dict> paramsMapList = listImport.stream().map(omsProductionOrder ->
                new Dict().set(PRODUCT_FACTORY_CODE, omsProductionOrder.getProductFactoryCode())
                        .set(PRODUCT_MATERIAL_CODE, omsProductionOrder.getProductMaterialCode())
                        .set(PRODUCT_LINE_CODE, omsProductionOrder.getProductLineCode())
                        .set(BOM_VERSION, omsProductionOrder.getBomVersion())
                        .set(PRODUCT_MATERIAL, PRODUCT_MATERIAL_TYPE)
        ).distinct().collect(toList());
        List<CdSettleProductMaterial> settleProductMaterials = listImport.stream()
                .filter(o -> !OutSourceTypeEnum.OUT_SOURCE_TYPE_ZZ.getCode().equals(o.getOutsourceType()))
                .map(omsProductionOrderExportVo -> CdSettleProductMaterial.builder().productMaterialCode(omsProductionOrderExportVo.getProductMaterialCode())
                        .outsourceWay(omsProductionOrderExportVo.getOutsourceType()).build()
                ).distinct().collect(toList());
        /** 查询非自制排产订单在SAP中是否存在数据*/
        Map<String,List<CdSettleProductMaterial>> map = new HashMap<>();
        map.put("list",settleProductMaterials);
        R materialPriceMap = remoteCdMaterialPriceInfoService.selectMaterialPrice(map);
        if (!materialPriceMap.isSuccess()) {
            log.error("查询非自制排产订单在SAP成本价格失败：" + materialPriceMap.get("msg"));
        }
        List<CdMaterialPriceInfo> materialPriceInfos = materialPriceMap.getCollectData(new TypeReference<List<CdMaterialPriceInfo>>() {
        });
        /**调用服务获取成品物料扩展信息  start*/
        //调用服务获取成品物料扩展信息
        R materialExtendMap = remoteMaterialExtendInfoService.selectByMaterialList(paramsMapList);
        if (!materialExtendMap.isSuccess()) {
            log.error("调用服务获取成品物料扩展信息失败！");
            throw new BusinessException("调用服务获取成品物料扩展信息失败!");
        }
        List<CdMaterialExtendInfo> materialExtendInfoList = materialExtendMap.getCollectData(new TypeReference<List<CdMaterialExtendInfo>>() {
        });
        if (CollectionUtil.isEmpty(materialExtendInfoList)) {
            log.error("根据成品专用号查询成品物料扩展信息为空！");
            throw new BusinessException("根据成品专用号查询成品物料扩展信息为空，请维护物料扩展信息数据！");
        }
        /**调用服务获取成品物料扩展信息  end*/

        /**调用服务获取物料主数据信息  start*/
        //调用服务获取物料主数据信息
        R materialInfoMap = remoteMaterialService.selectListByMaterialList(paramsMapList);
        if (!materialInfoMap.isSuccess()) {
            log.error("调用system服务根据成品物料、生产工厂、物料类型查询物料信息失败：" + materialInfoMap.get("msg"));
            throw new BusinessException("调用system服务根据成品物料、生产工厂、物料类型查询物料信息失败!" + materialInfoMap.get("msg"));
        }
        List<CdMaterialInfo> materialInfoList = materialInfoMap.getCollectData(new TypeReference<List<CdMaterialInfo>>() {
        });
        /**调用服务获取物料主数据信息  end*/

        /**调用服务获取工厂线体信息数据  start*/
        R factoryLineMap = remoteFactoryLineInfoService.selectListByMapList(paramsMapList);
        if (!factoryLineMap.isSuccess()) {
            log.error("调用system服务根据生产工厂、线体查询线体信息失败：" + factoryLineMap.get("msg"));
            throw new BusinessException("调用system服务根据生产工厂、线体查询线体信息失败!" + factoryLineMap.get("msg"));
        }
        //获取线体信息
        List<CdFactoryLineInfo> cdFactoryLineInfoList = factoryLineMap.getCollectData(new TypeReference<List<CdFactoryLineInfo>>() {
        });
        /**调用服务获取工厂线体信息数据  end*/

        /**调用服务获取BOM清单数据  start*/
        R bomInfoMap = remoteBomService.selectBomList(paramsMapList);
        if (!bomInfoMap.isSuccess()) {
            log.error("调用system服务根据生产工厂、成品专用号、bom版本查询BOM信息失败：" + bomInfoMap.get("msg"));
            throw new BusinessException("调用system服务根据生产工厂、成品专用号、bom版本查询BOM信息失败!" + bomInfoMap.get("msg"));
        }
        List<CdBomInfo> bomInfoList = bomInfoMap.getCollectData(new TypeReference<List<CdBomInfo>>() {
        });

        //1、数据校验，数据组织
        //1-1、判断可否加工承揽（cd_material_extend_info），如果否，则提示用户只允许自制，无法导入；如果是，则通过
        //获取不能加工承揽的物料信息
        List<String> noMaterialList = materialExtendInfoList.stream()
                .filter((CdMaterialExtendInfo m) -> IS_PUTTING_OUT_NO.equals(m.getIsPuttingOut()))
                .map(CdMaterialExtendInfo::getMaterialCode)
                .collect(toList());
        //1-2、UPH节拍：根据导入信息的成品物料号、生产工厂获取物料信息表（cd_material_info）中对应的UPH节拍；
        //匹配UPH节拍数据
        listImport.forEach(o -> materialInfoList.forEach(m -> {
            if (o.getProductFactoryCode().equals(m.getPlantCode())
                    && o.getProductMaterialCode().equals(m.getMaterialCode())) {
                o.setRhythm(m.getUph());
                o.setProductMaterialDesc(m.getMaterialDesc());
            }
        }));
        //计算用时：排产量/UPH节拍
        listImport.forEach(o -> {
            if (o.getRhythm() != null) {
                o.setUseTime(o.getProductNum()
                        .divide(o.getRhythm(), 2, BigDecimal.ROUND_HALF_UP));
            }
        });
        //1-3、产品定员：根据成品生产工厂、线体获取工厂线体关系表（cd_factory_line_info）中的产品定员；
        //1-6、分公司主管、班长：根据生产工厂、线体获取工厂线体关系表（cd_factory_line_info）中的分公司主管、班长的信息；
        //匹配产品定员信息,分公司主管、班长
        listImport.forEach(o -> cdFactoryLineInfoList.forEach(f -> {
            if (o.getProductFactoryCode().equals(f.getProductFactoryCode())
                    && o.getProductLineCode().equals(f.getProduceLineCode())) {
                o.setProductQuota(f.getProductQuota() == null ? 0 : f.getProductQuota());
                o.setBranchOffice(f.getBranchOffice());
                o.setMonitor(f.getMonitor());
            }
        }));

        //1-7、生命周期：根据成品专用号获取物料扩展信息表（cd_material_extend_info）中的生命周期；
        //匹配生命周期
        listImport.forEach(o -> materialExtendInfoList.forEach(m -> {
            if (o.getProductMaterialCode().equals(m.getMaterialCode())) {
                o.setLifeCycle(m.getLifeCycle());
            }
        }));

        //校验导入字段数据，设置导入失败原因
        Map<String, List<CdBomInfo>> bomMap =
                bomInfoList.stream().collect(Collectors.groupingBy((bom) -> getBomGroupKey(bom)));
        listImport.forEach(o -> {
            ExcelImportSucObjectDto sucObjectDto = new ExcelImportSucObjectDto();
            if ((PUTTING_OUT_ZERO.equals(o.getOutsourceType())
                    || PUTTING_OUT_ONE.equals(o.getOutsourceType()))
                    && noMaterialList.contains(o.getProductMaterialCode())) {
                String exportRemark = o.getExportRemark() == null ? "" : o.getExportRemark() + "，";
                o.setExportRemark(exportRemark + NO_OUTSOURCE_REMARK);
            }
            if (o.getRhythm() == null || StringUtils.isBlank(o.getRhythm().toString())
                    || o.getRhythm().compareTo(BigDecimal.ZERO) == 0) {
                String exportRemark = o.getExportRemark() == null ? "" : o.getExportRemark() + "，";
                o.setExportRemark(exportRemark + NO_UPH_REMARK);
            }
            //筛选没有产品定员、分公司主管、班长的信息
            if ((StringUtils.isBlank(String.valueOf(o.getProductQuota()))
                    || o.getProductQuota() == 0)
                    || (StringUtils.isBlank(o.getBranchOffice())
                    && StringUtils.isBlank(o.getMonitor()))) {
                String exportRemark = o.getExportRemark() == null ? "" : o.getExportRemark() + "，";
                o.setExportRemark(exportRemark + NO_QUOTA_REMARK);
            }
            //筛选没有生命周期的数据
            if (StringUtils.isBlank(o.getLifeCycle())) {
                String exportRemark = o.getExportRemark() == null ? "" : o.getExportRemark() + "，";
                o.setExportRemark(exportRemark + NO_LIFECYCLE_REMARK);
            }
            //筛选没有bom清单的数据
            List<CdBomInfo> bomInfos =
                    bomMap.get(StrUtil.concat(true, o.getProductMaterialCode(), o.getProductFactoryCode(), o.getBomVersion()));
            if (ObjectUtil.isEmpty(bomInfos) || bomInfos.size() <= 0) {
                String exportRemark = o.getExportRemark() == null ? "" : o.getExportRemark() + "，";
                o.setExportRemark(exportRemark + NO_BOM_REMARK);
            }
            if (!OutSourceTypeEnum.OUT_SOURCE_TYPE_ZZ.getCode().equals(o.getOutsourceType())) {
                String exportRemark = o.getExportRemark() == null ? "" : o.getExportRemark() + "，";
                if (ObjectUtil.isEmpty(materialPriceInfos) || materialPriceInfos.size() <= 0) {
                    o.setExportRemark(exportRemark + NO_MATERIAL_RRICE);
                } else {
                    List<String> materialCode = materialPriceInfos.stream().map(CdMaterialPriceInfo::getMaterialCode).collect(toList());
                    //根据成品物料号+加工承揽方式查询加工费号
                    R productMaterialMap =
                            remoteCdSettleProductMaterialService.selectOne(o.getProductMaterialCode(), o.getOutsourceType());
                    if (productMaterialMap.isSuccess()) {
                        CdSettleProductMaterial cdSettleProductMaterial = productMaterialMap.getData(CdSettleProductMaterial.class);
                        if (!materialCode.contains(cdSettleProductMaterial.getRawMaterialCode())) {
                            o.setExportRemark(exportRemark + NO_MATERIAL_RRICE);
                        }
                    }
                }
            }
            sucObjectDto.setObject(o);
            successDtos.add(sucObjectDto);
        });
        return new ExcelImportResult(successDtos, errDtos, otherDtos);
    }

    /**
     * Description: 删除排产订单
     * Param: [ids]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    @GlobalTransactional
    public R deleteByIdString(OmsProductionOrder order, SysUser sysUser) {
        String ids = order.getIds();
        List<OmsProductionOrder> omsProductionOrders = new ArrayList<>();
        if (StrUtil.isNotBlank(ids)) {
            omsProductionOrders = omsProductionOrderMapper.selectByIds(ids);
        } else {
            //增加删除状态的判断  2020-08-17  ltq
            if (StrUtil.isNotBlank(order.getStatus()) && !order.getStatus().equals(ProductOrderConstants.STATUS_ZERO)) {
                log.info("非待评审状态订单不可删除！");
                return R.error("非待评审状态订单不可删除！");
            }
            Example example = checkParams(order,sysUser);
            example.getOredCriteria().get(0).andEqualTo("status",ProductOrderConstants.STATUS_ZERO);
            omsProductionOrders = omsProductionOrderMapper.selectByExample(example);
            ids= omsProductionOrders.stream().map(o ->o.getId().toString()).collect(Collectors.joining(","));
        }
        if (omsProductionOrders.size() <= 0) {
            log.info("根据前台传参未查询出排产订单数据，直接返回成功！");
            return R.ok();
        }
        List<String> orderCodeList = omsProductionOrders.stream()
                .map(OmsProductionOrder::getOrderCode).collect(toList());
        Map<String,Object> map = new HashMap<>();
        map.put("userName",sysUser.getLoginName());
        map.put("orderCodeList",orderCodeList);
        R deleteActMap = remoteActTaskService.deleteByOrderCode(map);
        if (!deleteActMap.isSuccess()){
            log.error("删除审批流程失败，原因："+deleteActMap.get("msg"));
            return R.error("删除审批流程失败，原因："+deleteActMap.get("msg"));
        }
        for (OmsProductionOrder omsProductionOrder : omsProductionOrders) {
            if (ProductOrderConstants.STATUS_FOUR.equals(omsProductionOrder.getStatus())
                    || ProductOrderConstants.STATUS_FIVE.equals(omsProductionOrder.getStatus())
                    || ProductOrderConstants.STATUS_SIX.equals(omsProductionOrder.getStatus())) {
                log.error("待传SAP、传SAP中、已传SAP的排产订单不可删除！");
                return R.error("待传SAP、传SAP中、已传SAP的排产订单不可删除！");
            }
        }
        StringBuffer orderCodeBuffer = new StringBuffer();
        List<OmsProductionOrderDel> omsProductionOrderDels = omsProductionOrders.stream().map(o -> {
            orderCodeBuffer.append("\'").append(o.getOrderCode()).append("\',");
            OmsProductionOrderDel omsProductionOrderDel = new OmsProductionOrderDel();
            BeanUtils.copyProperties(o, omsProductionOrderDel);
            omsProductionOrderDel.setId(null);
            omsProductionOrderDel.setCreateBy(sysUser.getLoginName());
            omsProductionOrderDel.setCreateTime(new Date());
            return omsProductionOrderDel;
        }).collect(toList());
        String orderCodes = orderCodeBuffer.substring(0, orderCodeBuffer.length() - 1);
        //查询明细数据
        R detailMap = omsProductionOrderDetailService.selectListByOrderCodes(orderCodes);
        if (!detailMap.isSuccess()) {
            log.error("查询明细数据失败！");
            return R.error("查询明细数据失败!");
        }
        List<OmsProductionOrderDetail> omsProductionOrderDetails =
                detailMap.getCollectData(new TypeReference<List<OmsProductionOrderDetail>>() {
                });
        StringBuffer detailIdBuffer = new StringBuffer();
        if (ObjectUtil.isNotEmpty(omsProductionOrderDetails) && omsProductionOrderDetails.size() > 0) {
            //转类型
            List<OmsProductionOrderDetailDel> omsProductionOrderDetailDels = omsProductionOrderDetails
                    .stream().map(d -> {
                        detailIdBuffer.append("\'").append(d.getId()).append("\',");
                        OmsProductionOrderDetailDel omsProductionOrderDetailDel = new OmsProductionOrderDetailDel();
                        BeanUtils.copyProperties(d, omsProductionOrderDetailDel);
                        omsProductionOrderDetailDel.setId(null);
                        omsProductionOrderDetailDel.setCreateBy(sysUser.getLoginName());
                        omsProductionOrderDetailDel.setCreateTime(new Date());
                        return omsProductionOrderDetailDel;
                    }).collect(toList());
            String detailIds = detailIdBuffer.substring(0, detailIdBuffer.length() - 1);
            //排产订单明细转存删除表
            omsProductionOrderDetailDelService.insertList(omsProductionOrderDetailDels);
            //删除排产订单明细数据
            omsProductionOrderDetailService.deleteByIds(detailIds);
        }
        //将删除的排产订单存到删除表中
        omsProductionOrderDelService.insertList(omsProductionOrderDels);
        //删除排产订单表数据
        omsProductionOrderMapper.deleteByIds(ids);

        return R.ok();
    }

    /**
     * Description:  排产订单修改
     * Param: [omsProductionOrder, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public R updateSave(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        //根据ID查询排产订单数据
        OmsProductionOrder productionOrder = omsProductionOrderMapper.selectByPrimaryKey(omsProductionOrder.getId());
        if (productionOrder == null) {
            log.error("根据排产订单ID查询数据为空！");
            return R.error("根据排产订单ID查询数据为空！");
        }
        omsProductionOrder.setOrderCode(productionOrder.getOrderCode());
        omsProductionOrder.setProductMaterialCode(productionOrder.getProductMaterialCode());
        omsProductionOrder.setProductFactoryCode(productionOrder.getProductFactoryCode());
        omsProductionOrder.setBomVersion(productionOrder.getBomVersion());
        omsProductionOrder.setStatus(productionOrder.getStatus());
        omsProductionOrder.setProductStartDate(productionOrder.getProductStartDate());
        //待传SAP，传SAP中，已传SAP状态的数据不可修改
        if (ProductOrderConstants.STATUS_FOUR.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_FIVE.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_SIX.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_SEVEN.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_EIGHT.equals(productionOrder.getStatus())) {
            log.info("待传SAP、传SAP中、已传SAP、传SAP异常、已关单状态的记录不可修改！");
            return R.error("待传SAP、传SAP中、已传SAP、传SAP异常、已关单状态的记录不可修改！");
        }
        List<String> rawMaterialCodes = new ArrayList<>();
        if (ProductOrderConstants.STATUS_ONE.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_TWO.equals(productionOrder.getStatus())) {
            //”反馈中“、“待调整”状态的数据
            //根据成品专用号、生产工厂、基本开始日期查询原材料反馈信息表记录
            List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks =
                    omsRawMaterialFeedbackService.select(OmsRawMaterialFeedback.builder()
                            .productMaterialCode(productionOrder.getProductMaterialCode())
                            .productFactoryCode(productionOrder.getProductFactoryCode())
                            .productStartDate(productionOrder.getProductStartDate())
                            .bomVersion(productionOrder.getBomVersion())
                            .status("0").build());
            //根据成品专用号、生产工厂、基本开始日期查询除当前订单外的其他排产订单记录
            Example example = new Example(OmsProductionOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("productMaterialCode", omsProductionOrder.getProductMaterialCode());
            criteria.andEqualTo("productFactoryCode", omsProductionOrder.getProductFactoryCode());
            criteria.andEqualTo("productStartDate", omsProductionOrder.getProductStartDate());
            criteria.andEqualTo("bomVersion",productionOrder.getBomVersion());
            criteria.andEqualTo("status",ProductOrderConstants.STATUS_ONE);
            criteria.andNotEqualTo("id", omsProductionOrder.getId());
            List<OmsProductionOrder> omsProductionOrders = omsProductionOrderMapper.selectByExample(example);
            //计算成品专用号、生产工厂、基本开始日期下的总排产量
            BigDecimal orderSum = omsProductionOrders.stream()
                    .map(OmsProductionOrder::getProductNum).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderSum = orderSum.add(omsProductionOrder.getProductNum());
            //如果存在反馈信息记录，判断修改后的量，如果小于等于反馈信息记录的成品满足量，
            //则更新反馈信息表记录状态为“通过”，排产订单状态“待评审”
            int checkCount = 0;
            for (OmsRawMaterialFeedback omsRawMaterialFeedback : omsRawMaterialFeedbacks) {
                if (orderSum.compareTo(omsRawMaterialFeedback.getProductContentNum()) <= 0) {
                    omsRawMaterialFeedback.setStatus(RawMaterialFeedbackConstants.STATUS_ONE);
                    omsRawMaterialFeedbackService.updateByPrimaryKeySelective(omsRawMaterialFeedback);
                    checkCount++;
                } else {
                    rawMaterialCodes.add(omsRawMaterialFeedback.getRawMaterialCode());
                }
            }
            //如果满足数量的记录条数与反馈信息总条数相同，则排产订单状态为“待评审”，反之“反馈中”
            if (omsRawMaterialFeedbacks.size() == checkCount) {
                omsProductionOrder.setStatus(ProductOrderConstants.STATUS_ZERO);
                omsProductionOrders.forEach(o -> o.setStatus(ProductOrderConstants.STATUS_ZERO));
                //更新其他排产订单的状态
                omsProductionOrderMapper.updateBatchByPrimaryKeySelective(omsProductionOrders);
            }
        } else if (ProductOrderConstants.STATUS_THREE.equals(productionOrder.getStatus())) {
            //“已评审”状态的排产订单
            //如果排产量向下调整，则无需JIT重新评审，状态无需变更；
            //如果排产量向上调整，需JIT重新评审，即状态更新为“待评审”，排产订单明细状态更新为“未确认”；
            if (omsProductionOrder.getProductNum().compareTo(productionOrder.getProductNum()) > 0) {
                omsProductionOrder.setStatus(ProductOrderConstants.STATUS_ZERO);
            }
        }
        //更新排产订单
        int updateCount = omsProductionOrderMapper.updateByPrimaryKeySelective(omsProductionOrder);
        if (updateCount <= 0) {
            log.error("更新排产订单失败！");
            throw new BusinessException("更新排产订单失败！");
        }
        //bom拆解
        //查询bom清单，根据生产工厂、成品专用号、bom版本
        Dict dict = new Dict();
        dict.put(PRODUCT_FACTORY_CODE, omsProductionOrder.getProductFactoryCode());
        dict.put(PRODUCT_MATERIAL_CODE, omsProductionOrder.getProductMaterialCode());
        dict.put(BOM_VERSION, omsProductionOrder.getBomVersion());
        List<Dict> dicts = new ArrayList<>();
        dicts.add(dict);
        R bomMap = remoteBomService.selectBomList(dicts);
        if (!bomMap.isSuccess()) {
            log.error("获取bom清单数据失败：" + bomMap.get("msg"));
            throw new BusinessException("获取bom清单数据失败:" + bomMap.get("msg"));
        }
        List<CdBomInfo> bomInfos = bomMap.getCollectData(new TypeReference<List<CdBomInfo>>() {
        });
        if (CollUtil.isEmpty(bomInfos)) {
            log.error("获取bom清单数据为空！");
            throw new BusinessException("获取bom清单数据为空!");
        }
        List<OmsProductionOrderDetail> omsProductionOrderDetails = new ArrayList<>();
        bomInfos.forEach(bom -> {
            //判断排产订单明细的状态
            String detailStatus = rawMaterialCodes.contains(bom.getRawMaterialCode())
                    ? ProductOrderConstants.DETAIL_STATUS_TWO : ProductOrderConstants.DETAIL_STATUS_ZERO;
            //计算原材料排产量
            BigDecimal rawMaterialProductNum = bom.getBomNum()
                    .divide(bom.getBasicNum(), 2, BigDecimal.ROUND_HALF_UP).multiply(omsProductionOrder.getProductNum());
            OmsProductionOrderDetail omsProductionOrderDetail = OmsProductionOrderDetail.builder()
                    .productOrderCode(omsProductionOrder.getOrderCode())
                    .productFactoryCode(omsProductionOrder.getProductFactoryCode())
                    .materialCode(bom.getRawMaterialCode())
                    .materialDesc(bom.getRawMaterialDesc())
                    .bomNum(bom.getBomNum())
                    .basicNum(bom.getBasicNum())
                    .rawMaterialProductNum(rawMaterialProductNum)
                    .unit(bom.getComponentUnit())
                    .bomVersion(omsProductionOrder.getBomVersion())
                    .productStartDate(omsProductionOrder.getProductStartDate())
                    .purchaseGroup(bom.getPurchaseGroup())
                    .storagePoint(bom.getStoragePoint())
                    .status(detailStatus)
                    .delFlag(DeleteFlagConstants.NO_DELETED)
                    .build();
            omsProductionOrderDetail.setCreateTime(new Date());
            omsProductionOrderDetail.setCreateBy(sysUser.getLoginName());
            omsProductionOrderDetails.add(omsProductionOrderDetail);
        });
        omsProductionOrderDetailService.delectByProductOrderCode(productionOrder.getOrderCode());
        omsProductionOrderDetailService.insertList(omsProductionOrderDetails);
        if (omsProductionOrder.getStatus().equals(ProductOrderConstants.STATUS_ZERO)) {
            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JIT);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
                throw new BusinessException("获取权限用户列表失败!");
            }
            List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            //获取JIT邮箱信息
            Set<SysUser> sysUsers = new HashSet<>();
            sysUserRightsList.forEach(u ->
                    omsProductionOrderDetails.forEach(o -> {
                        if (u.getProductFactorys().contains(o.getProductFactoryCode())
                                && u.getPurchaseGroups().contains(o.getPurchaseGroup())
                                && StrUtil.isNotBlank(u.getEmail())) {
                            sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                        }
                    })
            );
            //发送邮件
            sysUsers.forEach(u -> {
                String email = u.getEmail();
                String context = u.getUserName() + EmailConstants.RAW_MATERIAL_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                mailService.sendTextMail(email, EmailConstants.TITLE_RAW_MATERIAL_REVIEW, context);
            });
        }
        return R.ok();
    }

    /**
     * Description:  确认下达
     * Param: [omsProductionOrder, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/23
     */
    @Override
    @GlobalTransactional
    public R confirmRelease(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        List<OmsProductionOrder> omsProductionOrderList = new ArrayList<>();
        if (BeanUtil.isEmpty(omsProductionOrder)) {
            criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            criteria.andEqualTo("status", ProductOrderConstants.STATUS_THREE);
            criteria.andNotEqualTo("auditStatus", ProductOrderConstants.AUDIT_STATUS_ONE);
            omsProductionOrderList = omsProductionOrderMapper.selectByExample(example);
        } else {
            String ids = omsProductionOrder.getIds();
            if (StrUtil.isNotBlank(ids)) {
                omsProductionOrderList = omsProductionOrderMapper.selectByIds(ids);
                for (OmsProductionOrder productionOrder : omsProductionOrderList) {
                    if (!productionOrder.getStatus().equals(ProductOrderConstants.STATUS_THREE)) {
                        return R.error("非“已评审”状态的排产订单不可确认下达！");
                    }
                    if (productionOrder.getAuditStatus().equals(ProductOrderConstants.AUDIT_STATUS_ONE)
                            || productionOrder.getAuditStatus().equals(ProductOrderConstants.AUDIT_STATUS_THREE)) {
                        return R.error("审核中、审核驳回状态的排产订单不可确认下达！");
                    }
                }
            } else if (BeanUtil.isNotEmpty(omsProductionOrder)) {
                if (StrUtil.isNotBlank(omsProductionOrder.getStatus())
                        && !ProductOrderConstants.STATUS_THREE.equals(omsProductionOrder.getStatus())) {
                    log.error("确认下达传入排产订单状态非已评审！");
                    return R.error("只可以下达已评审的排产订单！");
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getAuditStatus())
                        && (omsProductionOrder.getAuditStatus().equals(ProductOrderConstants.AUDIT_STATUS_ONE)
                        || omsProductionOrder.getAuditStatus().equals(ProductOrderConstants.AUDIT_STATUS_THREE))) {
                    log.error("确认下达传入排产订单审核状态为审核中，不可下达！");
                    return R.error("只可以下达非审核中、审核驳回的排产订单！");
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getProductFactoryCode())) {
                    criteria.andEqualTo("productFactoryCode", omsProductionOrder.getProductFactoryCode());
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getProductLineCode())) {
                    criteria.andEqualTo("productLineCode", omsProductionOrder.getProductLineCode());
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getStatus())) {
                    criteria.andEqualTo("status", omsProductionOrder.getStatus());
                } else {
                    criteria.andEqualTo("status", ProductOrderConstants.STATUS_THREE);
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getAuditStatus())) {
                    criteria.andEqualTo("auditStatus", omsProductionOrder.getAuditStatus());
                } else {
                    List<String> auditStatusList = new ArrayList<>();
                    auditStatusList.add(ProductOrderConstants.AUDIT_STATUS_ZERO);
                    auditStatusList.add(ProductOrderConstants.AUDIT_STATUS_TWO);
                    criteria.andIn("auditStatus",auditStatusList);
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
                    criteria.andEqualTo("productMaterialCode", omsProductionOrder.getProductMaterialCode());
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getCheckDateStart())) {
                    if (ProductOrderConstants.DATE_TYPE_ONE.equals(omsProductionOrder.getDateType())) {
                        criteria.andGreaterThanOrEqualTo("deliveryDate", omsProductionOrder.getCheckDateStart());
                    } else if (ProductOrderConstants.DATE_TYPE_TWO.equals(omsProductionOrder.getDateType())) {
                        criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getCheckDateStart());
                    } else if (ProductOrderConstants.DATE_TYPE_THREE.equals(omsProductionOrder.getDateType())) {
                        criteria.andGreaterThanOrEqualTo("productEndDate", omsProductionOrder.getCheckDateStart());
                    }
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getCheckDateEnd())) {
                    if (ProductOrderConstants.DATE_TYPE_ONE.equals(omsProductionOrder.getDateType())) {
                        criteria.andLessThanOrEqualTo("deliveryDate", omsProductionOrder.getCheckDateEnd());
                    } else if (ProductOrderConstants.DATE_TYPE_TWO.equals(omsProductionOrder.getDateType())) {
                        criteria.andLessThanOrEqualTo("productStartDate", omsProductionOrder.getCheckDateEnd());
                    } else if (ProductOrderConstants.DATE_TYPE_THREE.equals(omsProductionOrder.getDateType())) {
                        criteria.andLessThanOrEqualTo("productEndDate", omsProductionOrder.getCheckDateEnd());
                    }
                }
                if (StrUtil.isNotBlank(omsProductionOrder.getOrderType())) {
                    criteria.andEqualTo("orderType", omsProductionOrder.getOrderType());
                }
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
                omsProductionOrderList = omsProductionOrderMapper.selectByExample(example);
            }
        }
        if (ObjectUtil.isEmpty(omsProductionOrderList) || omsProductionOrderList.size() <= 0) {
            log.error("根据前台传参未查询出排产订单！");
            return R.ok();
        }
        //闸口校验
        List<OmsProductionOrder> updateOrderList = checkGate(omsProductionOrderList, sysUser);
        omsProductionOrderMapper.updateBatchByPrimaryKeySelective(updateOrderList);
        return R.ok();
    }

    /**
     * Description:排产订单分页查询
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/23
     */
    @Override
    public List<OmsProductionOrder> selectPageInfo(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        Example example = checkParams(omsProductionOrder, sysUser);
        return omsProductionOrderMapper.selectByExample(example);
    }
    /**
     * Description:  组织参数
     * Param: [omsProductionOrder, sysUser]
     * return: tk.mybatis.mapper.entity.Example
     * Author: ltq
     * Date: 2020/8/10
     */
    private Example checkParams(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
            String[] productMaterialCodeS = omsProductionOrder.getProductMaterialCode().split(",");
            List<String> productMaterialCodeList = new ArrayList<>();
            for (String productMaterialCode : productMaterialCodeS) {
                String regex = "\\s*|\t|\r|\n";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(productMaterialCode);
                String productMaterialCodeReq = m.replaceAll("");
                productMaterialCodeList.add(productMaterialCodeReq);
            }
            criteria.andIn("productMaterialCode", productMaterialCodeList);
        }
        if (StringUtils.isNotBlank(omsProductionOrder.getProductOrderCode())) {
            String[] productOrderCodeS = omsProductionOrder.getProductOrderCode().split(",");
            List<String> productOrderCodeList = new ArrayList<>();
            for (String productOrderCode : productOrderCodeS) {
                String regex = "\\s*|\t|\r|\n";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(productOrderCode);
                String productOrderCodeReq = m.replaceAll("");
                productOrderCodeList.add(productOrderCodeReq);
            }
            criteria.andIn("productOrderCode", productOrderCodeList);
        }
        //增加排产订单号查询条件，多排产订单号查询，逗号隔开  2020-08-04  ltq
        if (StringUtils.isNotBlank(omsProductionOrder.getOrderCode())) {
            String[] orderCodes = omsProductionOrder.getOrderCode().split(",");
            List<String> orderCodeList = new ArrayList<>();
            for (String orderCOde : orderCodes) {
                String regex = "\\s*|\t|\r|\n";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(orderCOde);
                String productOrderCodeReq = m.replaceAll("");
                orderCodeList.add(productOrderCodeReq);
            }
            criteria.andIn("orderCode", orderCodeList);
        }
        //增加审核状态查询条件  2020-08-04 ltq
        if (StrUtil.isNotBlank(omsProductionOrder.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", omsProductionOrder.getAuditStatus());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsProductionOrder.getProductFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductLineCode())) {
            criteria.andEqualTo("productLineCode", omsProductionOrder.getProductLineCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getStatus())) {
            criteria.andEqualTo("status", omsProductionOrder.getStatus());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getCheckDateStart())) {
            if (ProductOrderConstants.DATE_TYPE_ONE.equals(omsProductionOrder.getDateType())) {
                criteria.andGreaterThanOrEqualTo("deliveryDate", omsProductionOrder.getCheckDateStart());
            } else if (ProductOrderConstants.DATE_TYPE_TWO.equals(omsProductionOrder.getDateType())) {
                criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getCheckDateStart());
            } else if (ProductOrderConstants.DATE_TYPE_THREE.equals(omsProductionOrder.getDateType())) {
                criteria.andGreaterThanOrEqualTo("productEndDate", omsProductionOrder.getCheckDateStart());
            }
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getCheckDateEnd())) {
            if (ProductOrderConstants.DATE_TYPE_ONE.equals(omsProductionOrder.getDateType())) {
                criteria.andLessThanOrEqualTo("deliveryDate", omsProductionOrder.getCheckDateEnd());
            } else if (ProductOrderConstants.DATE_TYPE_TWO.equals(omsProductionOrder.getDateType())) {
                criteria.andLessThanOrEqualTo("productStartDate", omsProductionOrder.getCheckDateEnd());
            } else if (ProductOrderConstants.DATE_TYPE_THREE.equals(omsProductionOrder.getDateType())) {
                criteria.andLessThanOrEqualTo("productEndDate", omsProductionOrder.getCheckDateEnd());
            }
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getOrderType())) {
            criteria.andEqualTo("orderType", omsProductionOrder.getOrderType());
        }
        criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
        return example;
    }

    /**
     * Description:  排产订单导出
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo>
     * Author: ltq
     * Date: 2020/6/23
     */
    @Override
    public List<OmsProductionOrder> exportAll(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        Example example = checkParams(omsProductionOrder, sysUser);
        return omsProductionOrderMapper.selectByExample(example);
    }

    /**
     * Description:  反馈信息处理-快捷修改查询
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    public List<OmsProductionOrder> queryProductOrder(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsProductionOrder.getProductFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductStartDate())) {
            criteria.andEqualTo("productStartDate", omsProductionOrder.getProductStartDate());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getBomVersion())) {
            criteria.andEqualTo("bomVersion", omsProductionOrder.getBomVersion());
        }
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //排产员根据生产工厂权限查询
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)) {
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            }
        }
        List<OmsProductionOrder> list = omsProductionOrderMapper.selectByExample(example);
        return list;
    }

    /**
     * Description: 根据工厂、专用号、基本开始日期查询
     * Param:
     * return:
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    public List<OmsProductionOrder> selectByFactoryAndMaterialAndStartDate(List<OmsProductionOrder> list) {
        return omsProductionOrderMapper.selectByFactoryAndMaterialAndStartDate(list);
    }

    /**
     * Description:  根据排产订单号查询
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    public List<OmsProductionOrder> selectByOrderCode(List<String> list) {
        return omsProductionOrderMapper.selectByOrderCode(list);
    }

    /**
     * Description: 根据排产订单号批量更新
     * Param: [list]
     * return: int
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    public void updateByOrderCode(List<OmsProductionOrder> list) {
        omsProductionOrderMapper.updateByOrderCode(list);
    }

    private String fetchGroupKey(CdBomInfo cdBomInfo) {
        return cdBomInfo.getProductMaterialCode() + cdBomInfo.getProductFactoryCode() + cdBomInfo.getVersion();
    }

    /**
     * Description:  3版本闸口
     * Param: [list, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/19
     */
    private List<OmsProductionOrder> checkThreeVersion(List<OmsProductionOrder> list, SysUser sysUser) {
        Set<OmsProductionOrder> checkList = new HashSet<>();
        list.forEach(o -> {
            if (ProductOrderConstants.BOM_VERSION_THREE.equals(o.getBomVersion())
                    && o.getProductNum().compareTo(ProductOrderConstants.BOM_VERSION_THREE_NUM) > 0) {
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
                checkList.add(o);
            }
        });
        if (checkList.size() > 0) {
            //  3版本审批流程
            List<ActStartProcessVo> processVos = checkList.stream().map(o -> {
                ActStartProcessVo actStartProcessVo = new ActStartProcessVo();
                actStartProcessVo.setOrderId(o.getId().toString());
                actStartProcessVo.setOrderCode(o.getOrderCode());
                return actStartProcessVo;
            }).collect(toList());
            ActBusinessVo actBusinessVo = ActBusinessVo.builder().key(ActProcessContants.ACTIVITI_THREE_VERSION_REVIEW)
                    .userId(sysUser.getUserId())
                    .userName(sysUser.getUserName())
                    .title(ActProcessContants.ACTIVITI_PRO_TITLE_THREE_VERSION)
                    .processVoList(processVos).build();
            R r = remoteActOmsProductionOrderService.startActProcess(actBusinessVo);
            if (!r.isSuccess()) {
                log.error("开启排产订单3版本审批流失败，原因：" + r.get("msg"));
                throw new BusinessException("开启排产订单3版本审批流失败!");
            }

            String email = sysUser.getEmail();
            if (StrUtil.isNotBlank(email)) {
                String context = sysUser.getUserName() + EmailConstants.THREE_VERSION_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                mailService.sendTextMail(email, EmailConstants.TITLE_THREE_VERSION_REVIEW, context);
            }
        }
        return list;
    }

    /**
     * Description:  超期未关闭订单审批校验
     * Param: [list, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/19
     */
    private List<OmsProductionOrder> checkOverdueNotCloseOrder(List<OmsProductionOrder> list, SysUser sysUser) {
        List<OmsProductionOrder> omsProductionOrders = omsProductionOrderMapper.selectByFactoryAndMaterialAndLine(list);
        Set<OmsProductionOrder> checkOrders = new HashSet<>();
        list.forEach(o ->
                omsProductionOrders.forEach(order -> {
                    if (o.getProductFactoryCode().equals(order.getProductFactoryCode())
                            && o.getProductMaterialCode().equals(order.getProductMaterialCode())
                            && o.getProductLineCode().equals(order.getProductLineCode())) {
                        o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
                        checkOrders.add(o);
                    }
                })
        );
        if (checkOrders.size() > 0) {
            //  超期未关闭订单审批流程
            List<ActStartProcessVo> processVos = checkOrders.stream().map(o -> {
                ActStartProcessVo actStartProcessVo = new ActStartProcessVo();
                actStartProcessVo.setOrderId(o.getId().toString());
                actStartProcessVo.setOrderCode(o.getOrderCode());
                return actStartProcessVo;
            }).collect(toList());
            ActBusinessVo actBusinessVo = ActBusinessVo.builder().key(ActProcessContants.ACTIVITI_OVERDUE_NOT_CLOSE_ORDER_REVIEW)
                    .userId(sysUser.getUserId())
                    .userName(sysUser.getUserName())
                    .title(ActProcessContants.ACTIVITI_PRO_TITLE_OVERDUE_NOT_CLOSE)
                    .processVoList(processVos).build();
            R r = remoteActOmsProductionOrderService.startActProcess(actBusinessVo);
            if (!r.isSuccess()) {
                log.error("开启排产订单超期未关闭订单审批流程失败，原因：" + r.get("msg"));
                throw new BusinessException("开启排产订单超期未关闭订单审批流程失败!");
            }

            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_ORDER);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
            } else {
                List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
                });
                Set<SysUser> sysUsers = new HashSet<>();
                sysUserRightsList.forEach(u ->
                        checkOrders.forEach(o -> {
                            if (u.getProductFactorys().contains(o.getProductFactoryCode()) && StrUtil.isNotBlank(u.getEmail())) {
                                sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                            }
                        })
                );
                //发送邮件
                sysUsers.forEach(u -> {
                    String email = u.getEmail();
                    String context = u.getUserName() + EmailConstants.OVERDUE_NOT_CLOSE_ORDER_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                    mailService.sendTextMail(email, EmailConstants.TITLE_OVERDUE_NOT_CLOSE_ORDER_REVIEW, context);
                });
            }
        }
        return list;
    }

    /**
     * Description: 超期库存闸口校验
     * Param: [list, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/24
     */
    private List<OmsProductionOrder> checkOverStock(List<OmsProductionOrder> list, SysUser sysUser) {
        Set<OmsProductionOrder> omsProductionOrders = new HashSet<>();
        list.forEach(o -> {
            R overStockMap = remoteCdProductOverdueService.selectOverStockByFactoryAndMaterial(CdProductOverdue
                    .builder().productFactoryCode(o.getProductFactoryCode())
                    .productMaterialCode(o.getProductMaterialCode()).build());
            if (!overStockMap.isSuccess()) {
                log.info("根据工厂、物料号查询超期库存" + overStockMap.get("msg"));
            }
            List<CdProductOverdue> productOverdues =
                    overStockMap.getCollectData(new TypeReference<List<CdProductOverdue>>() {
                    });
            if (BeanUtil.isNotEmpty(productOverdues) && productOverdues.size() > 0) {
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
                omsProductionOrders.add(o);
            }
        });
        if (omsProductionOrders.size() > 0) {
            List<ActStartProcessVo> processVos = omsProductionOrders.stream().map(o -> {
                ActStartProcessVo actStartProcessVo = new ActStartProcessVo();
                actStartProcessVo.setOrderId(o.getId().toString());
                actStartProcessVo.setOrderCode(o.getOrderCode());
                return actStartProcessVo;
            }).collect(toList());
            ActBusinessVo actBusinessVo = ActBusinessVo.builder().key(ActProcessContants.ACTIVITI_OVERDUE_STOCK_ORDER_REVIEW)
                    .userId(sysUser.getUserId())
                    .userName(sysUser.getUserName())
                    .title(ActProcessContants.ACTIVITI_PRO_TITLE_OVERDUE_STOCK)
                    .processVoList(processVos).build();
            R r = remoteActOmsProductionOrderService.startActProcess(actBusinessVo);
            if (!r.isSuccess()) {
                log.error("开启排产订单超期未关闭订单审批流程失败，原因：" + r.get("msg"));
                throw new BusinessException("开启排产订单超期未关闭订单审批流程失败!");
            }

            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_ORDER);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
            } else {
                List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
                });
                Set<SysUser> sysUsers = new HashSet<>();
                sysUserRightsList.forEach(u ->
                        omsProductionOrders.forEach(o -> {
                            if (u.getProductFactorys().contains(o.getProductFactoryCode()) && StrUtil.isNotBlank(u.getEmail())) {
                                sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                            }
                        })
                );
                //发送邮件
                sysUsers.forEach(u -> {
                    String email = u.getEmail();
                    String context = u.getUserName() + EmailConstants.OVER_STOCK_CONTEXT + EmailConstants.ORW_URL;
                    mailService.sendTextMail(email, EmailConstants.TITLE_OVER_STOCK, context);
                });
            }
        }
        return list;
    }

    /**
     * Description:  BOM拆解
     * Param: [omsProductionOrders, bomInfoList, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/19
     */
    private R bomDisassembly(List<OmsProductionOrder> omsProductionOrders, List<CdBomInfo> bomInfoList, SysUser sysUser) {
        //2、BOM拆解
        //2-1、根据成品专用号、生产工厂、BOM版本查询BOM清单表（cd_bom_info），获取BOM清单数据
        //按照成品专用号、生产工厂、BOM版本进行分组
        Map<String, List<CdBomInfo>> bomMap = bomInfoList.stream()
                .collect(Collectors.groupingBy((bom) -> fetchGroupKey(bom)));
        //2-2、计算排产订单原材料排产量
        List<OmsProductionOrderDetail> omsProductionOrderDetails = new ArrayList<>();
        omsProductionOrders.forEach(o -> {
            String key = o.getProductMaterialCode() + o.getProductFactoryCode() + o.getBomVersion();
            List<CdBomInfo> bomInfos = bomMap.get(key);
            bomInfos.forEach(bom -> {
                //计算原材料排产量
                BigDecimal rawMaterialProductNum = bom.getBomNum()
                        .divide(bom.getBasicNum(), 2, BigDecimal.ROUND_HALF_UP).multiply(o.getProductNum());
                OmsProductionOrderDetail omsProductionOrderDetail = OmsProductionOrderDetail.builder()
                        .productOrderCode(o.getOrderCode())
                        .productFactoryCode(o.getProductFactoryCode())
                        .materialCode(bom.getRawMaterialCode())
                        .materialDesc(bom.getRawMaterialDesc())
                        .bomNum(bom.getBomNum())
                        .basicNum(bom.getBasicNum())
                        .rawMaterialProductNum(rawMaterialProductNum)
                        .unit(bom.getComponentUnit())
                        .bomVersion(o.getBomVersion())
                        .productStartDate(o.getProductStartDate())
                        .purchaseGroup(bom.getPurchaseGroup())
                        .storagePoint(bom.getStoragePoint())
                        .status("0")
                        .delFlag("0")
                        .build();
                omsProductionOrderDetail.setCreateTime(new Date());
                omsProductionOrderDetail.setCreateBy(sysUser.getLoginName());
                omsProductionOrderDetails.add(omsProductionOrderDetail);
            });
        });
        if (omsProductionOrderDetails.size() <= 0) {
            log.info("无拆解后的排产订单明细！");
            return R.ok();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<OmsProductionOrderDetail>> list =
                objectMapper.convertValue(ListCommonUtil.subCollection(omsProductionOrderDetails, 100), new TypeReference<List<List<OmsProductionOrderDetail>>>() {
                });
        list.forEach(orderDetails -> omsProductionOrderDetailService.insertList(orderDetails));
        //3、邮件通知JIT评审
        //排产订单明细去重
        List<OmsProductionOrderDetail> detailList = omsProductionOrderDetails.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(()
                                -> new TreeSet<>(Comparator.comparing(o -> o.getProductFactoryCode() + o.getMaterialCode())))
                        , ArrayList::new));

        //获取权限用户列表
        R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JIT);
        if (!userRightsMap.isSuccess()) {
            log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
        } else {
            List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            //3-1、获取JIT邮箱信息
            Set<SysUser> sysUsers = new HashSet<>();
            sysUserRightsList.forEach(u ->
                    detailList.forEach(o -> {
                        if (u.getProductFactorys().contains(o.getProductFactoryCode())
                                && u.getPurchaseGroups().contains(o.getPurchaseGroup())
                                && StrUtil.isNotBlank(u.getEmail())) {
                            sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                        }
                    })
            );
            //3-2、发送邮件
            sysUsers.forEach(u -> {
                String email = u.getEmail();
                String contexts = u.getUserName() + EmailConstants.RAW_MATERIAL_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                mailService.sendTextMail(email, EmailConstants.TITLE_RAW_MATERIAL_REVIEW, contexts);
            });
        }
        return R.ok();
    }

    /**
     * Description:  删除重复数据
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo>
     * Author: ltq
     * Date: 2020/6/23
     */
    private List<OmsProductionOrderExportVo> deleteOldProductOrder(List<OmsProductionOrderExportVo> list) {
        List<OmsProductionOrderExportVo> checkOrderStatus = new ArrayList<>();
        list.forEach(o -> {
            OmsProductionOrder omsProductionOrder = omsProductionOrderMapper.selectOne(OmsProductionOrder.builder()
                    .productFactoryCode(o.getProductFactoryCode())
                    .productMaterialCode(o.getProductMaterialCode())
                    .productLineCode(o.getProductLineCode())
                    .productStartDate(o.getProductStartDate())
                    .productEndDate(o.getProductEndDate())
                    .bomVersion(o.getBomVersion()).build());
            if (BeanUtil.isNotEmpty(omsProductionOrder)) {
                if (omsProductionOrder.getStatus().equals(ProductOrderConstants.STATUS_ZERO)) {
                    omsProductionOrderMapper.deleteByPrimaryKey(omsProductionOrder.getId());
                    omsProductionOrderDetailService.delectByProductOrderCode(omsProductionOrder.getOrderCode());
                } else {
                    OmsProductionOrderExportVo omsProductionOrderExportVo = new OmsProductionOrderExportVo();
                    BeanUtils.copyProperties(omsProductionOrder, omsProductionOrderExportVo);
                    checkOrderStatus.add(omsProductionOrderExportVo);
                }
            }
        });
        return checkOrderStatus;
    }

    /**
     * Description:  排产订单下达SAP前闸口校验（T+2、ZN认证）
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.OmsProductionOrder>
     * Author: ltq
     * Date: 2020/6/23
     */
    private List<OmsProductionOrder> checkGate(List<OmsProductionOrder> list, SysUser sysUser) {
        List<OmsProductionOrder> addOrderList = new ArrayList<>();
        List<OmsProductionOrder> znOrderList = new ArrayList<>();
        StringBuffer orderCodeBuffer = new StringBuffer();
        list.forEach(o -> {
            //如果订单交付日期 - 当前日期 <= 2 为追加
            String dateNow = DateUtils.getDate();
            int contDay = DateUtils.dayDiffSt(o.getProductStartDate(), dateNow, YYYY_MM_DD);
            if (contDay <= 2) {
                orderCodeBuffer.append("\'").append(o.getOrderCode()).append("\',");
                addOrderList.add(o);
                o.setOrderClass(ProductOrderConstants.ORDER_CLASS_TWO);
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
            }
            R materialExtendMap = remoteMaterialExtendInfoService.selectOneByMaterialCode(o.getProductMaterialCode());
            if (!materialExtendMap.isSuccess()) {
                log.error("根据成品专用号查询物料扩展信息记录失败，原因：" + materialExtendMap.get("msg"));
                throw new BusinessException("根据成品专用号查询物料扩展信息记录失败");
            }
            CdMaterialExtendInfo cdMaterialExtendInfo = materialExtendMap.getData(CdMaterialExtendInfo.class);
            if (BeanUtil.isEmpty(cdMaterialExtendInfo)) {
                log.error("根据成品专用号查询物料扩展信息记录为空！");
                throw new BusinessException("根据成品专用号查询物料扩展信息记录为空，请及时维护物料扩展信息数据！");
            }
            if (cdMaterialExtendInfo.getIsZnAttestation().equals(ZN_ATTESTATION)) {
                znOrderList.add(o);
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
            }
        });
        if (addOrderList.size() > 0) {
            //T+2追加订单审批流程
            List<ActStartProcessVo> processVos = addOrderList.stream().map(o -> {
                ActStartProcessVo actStartProcessVo = new ActStartProcessVo();
                actStartProcessVo.setOrderId(o.getId().toString());
                actStartProcessVo.setOrderCode(o.getOrderCode());
                return actStartProcessVo;
            }).collect(toList());
            ActBusinessVo actBusinessVo = ActBusinessVo.builder().key(ActProcessContants.ACTIVITI_ADD_REVIEW)
                    .userId(sysUser.getUserId())
                    .userName(sysUser.getUserName())
                    .title(ActProcessContants.ACTIVITI_PRO_TITLE_ADD)
                    .processVoList(processVos).build();
            R r = remoteActOmsProductionOrderService.startActProcess(actBusinessVo);
            if (!r.isSuccess()) {
                log.error("开启排产订单T+2追加订单审批流程失败，原因：" + r.get("msg"));
                throw new BusinessException("开启排产订单T+2追加订单审批流程失败!");
            }

            //获取国产件JIT处长
            List<SysUserRights> sysUserRightsJitGcList = new ArrayList<>();
            R jitUserRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JITCZGC);
            if (!jitUserRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + jitUserRightsMap.get("msg"));
            } else {
                sysUserRightsJitGcList = jitUserRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
                });
            }
            //获取进口件JIT处长信息
            List<SysUserRights> sysUserRightsJitJkList = new ArrayList<>();
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JITCZJK);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
            } else {
                sysUserRightsJitJkList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
                });
            }
            sysUserRightsJitGcList.addAll(sysUserRightsJitJkList);
            //发送邮件
            sysUserRightsJitGcList.forEach(u -> {
                String email = u.getEmail();
                String contexts = u.getUserName() + EmailConstants.ADD_ORDER_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                mailService.sendTextMail(email, EmailConstants.TITLE_ADD_ORDER_REVIEW, contexts);
            });
        }
        //ZN 认证邮件通知
        if (znOrderList.size() > 0) {
            //开启ZN认证审批流程task
            List<ActStartProcessVo> processVos = znOrderList.stream().map(o -> {
                ActStartProcessVo actStartProcessVo = new ActStartProcessVo();
                actStartProcessVo.setOrderId(o.getId().toString());
                actStartProcessVo.setOrderCode(o.getOrderCode());
                return actStartProcessVo;
            }).collect(toList());
            ActBusinessVo actBusinessVo = ActBusinessVo.builder().key(ActProcessContants.ACTIVITI_ZN_REVIEW)
                    .userId(sysUser.getUserId())
                    .userName(sysUser.getUserName())
                    .title(ActProcessContants.ACTIVITI_PRO_TITLE_ZN)
                    .processVoList(processVos).build();
            R r = remoteActOmsProductionOrderService.startActProcess(actBusinessVo);
            if (!r.isSuccess()) {
                log.error("开启排产订单ZN认证审批流程失败，原因：" + r.get("msg"));
                throw new BusinessException("开启排产订单ZN认证审批流程失败!");
            }

            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_ZLGCS);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
            } else {
                List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
                });
                //发送邮件
                sysUserRightsList.forEach(u -> {
                    String email = u.getEmail();
                    String contexts = u.getUserName() + EmailConstants.ZN_REVIEW_CONTEXT + EmailConstants.ORW_URL;
                    mailService.sendTextMail(email, EmailConstants.TITLE_ZN_REVIEW, contexts);
                });
            }

        }
        List<OmsProductionOrder> checkListAll = new ArrayList<>();
        checkListAll.addAll(addOrderList);
        checkListAll.addAll(znOrderList);
        List<OmsProductionOrder> listAllDistinct = checkListAll.stream().distinct().collect(toList());
        list.forEach(o -> {
                    if (!listAllDistinct.contains(o)) {
                        o.setStatus(ProductOrderConstants.STATUS_FOUR);
                    }
                }
        );
        return list;

    }


    /**
     * 下达SAP
     *
     * @param ids
     * @return
     */
    @Override
    public R giveSAP(String ids) {
        List<OmsProductionOrder> list = new ArrayList<>();
        if (StringUtils.isNotBlank(ids)) {
            list = omsProductionOrderMapper.selectByIds(ids);
            //状态不是待传SAP或传SAP异常返回错误信息
            StringBuffer stringBuffer = new StringBuffer();
            list.forEach(omsProductionOrder -> {
                String status = omsProductionOrder.getStatus();
                Boolean statusFlag = ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_DCSAP.getCode().equals(status)
                        || ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPYC.getCode().equals(status);
                if (!statusFlag) {
                    stringBuffer.append("排产订单号")
                            .append(omsProductionOrder.getOrderCode())
                            .append("不允许下达SAP");
                    log.error("此排产订单不允许下达SAP 排产订单号:{},状态:{}", omsProductionOrder.getOrderCode(), status);
                }
            });
            if (stringBuffer.length() > 0) {
                return R.error(stringBuffer.toString());
            }
        }
        //1.获取list
        if (CollectionUtils.isEmpty(list)) {
            Example example = new Example(OmsProductionOrder.class);
            Example.Criteria criteria = example.createCriteria();
            List<String> statusList = new ArrayList<>();
            statusList.add(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_DCSAP.getCode());
            statusList.add(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPYC.getCode());
            criteria.andIn("status", statusList);
            list = omsProductionOrderMapper.selectByExample(example);
        }
        if (CollectionUtils.isEmpty(list)) {
            return R.ok("没有需要下达SAP的数据");
        }
        //2.下达SAP
        R resultSAP = orderFromSap601InterfaceService.createProductOrderFromSap601(list);
        if (!resultSAP.isSuccess()) {
            log.error("下达SAP调用SAP接口异常res:{}", JSONObject.toJSONString(resultSAP));
            return resultSAP;
        }
        //3.修改排产订单状态
        List<OmsProductionOrder> listSapRes = (List<OmsProductionOrder>) resultSAP.get("data");
        listSapRes.forEach(omsProductionOrder -> {
            if ("S".equals(omsProductionOrder.getSapFlag())) {
                omsProductionOrder.setStatus(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPZ.getCode());
            } else {
                omsProductionOrder.setStatus(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPYC.getCode());
            }
        });

        omsProductionOrderMapper.batchUpdateByOrderCode(listSapRes);
        return R.ok();
    }

    /**
     * 定时获取生产订单号
     *
     * @return
     */
    @GlobalTransactional
    @Override
    public R timeSAPGetProductOrderCode() {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        List<String> statusList = new ArrayList<>();
        statusList.add(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPZ.getCode());
        criteria.andIn("status", statusList);
        List<OmsProductionOrder> list = omsProductionOrderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return R.ok("无需要传SAP数据");
        }
        //1.调用SAP获取生产订单号
        R resultSAP = orderFromSap601InterfaceService.queryProductOrderFromSap601(list);
        if (!resultSAP.isSuccess()) {
            log.error("调用SAP获取生产订单号接口异常res:{}", JSONObject.toJSONString(resultSAP));
            return resultSAP;
        }

        List<OmsProductionOrder> listSapRes = (List<OmsProductionOrder>) resultSAP.get("data");
        if (CollectionUtils.isEmpty(listSapRes)) {
            return R.ok("获取生产订单号SAP没有数据");
        }
        List<OmsProductionOrder> successList = new ArrayList<>();
        listSapRes.forEach(omsProductionOrder -> {
            if ("S".equals(omsProductionOrder.getSapFlag())) {
                omsProductionOrder.setStatus(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode());
                omsProductionOrder.setSapMessages("生产订单创建成功成功");
                successList.add(omsProductionOrder);
            } else if("W".equals(omsProductionOrder.getSapFlag())){
                omsProductionOrder.setSapMessages("生产订单创建中");
            }
        });
        //2.修改数据
        omsProductionOrderMapper.batchUpdateByOrderCode(listSapRes);
        //3.生成加工费结算单
        if (CollectionUtils.isEmpty(successList)) {
            return R.ok("获取生产订单号后无需要生成加工费的数据");
        }
        List<String> orderOrderList = successList.stream().map(omsProductionOrder -> {
            return omsProductionOrder.getOrderCode();
        }).collect(toList());
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderMapper.selectByOrderCode(orderOrderList);
        if (!CollectionUtils.isEmpty(omsProductionOrderList)) {
            List<SmsSettleInfo> smsSettleInfoList = changeSmsSettleInfo(omsProductionOrderList);
            if (CollectionUtils.isEmpty(smsSettleInfoList)) {
                return R.ok("无需要生成加工费的数据");
            }
            R result = remoteSettleInfoService.batchInsert(smsSettleInfoList);
            if (!result.isSuccess()) {
                log.error("新增加工费结算失败 res:{}", JSONObject.toJSONString(result));
                throw new BusinessException(result.get("msg").toString());
            }
        }
        return R.ok();
    }

    /**
     * 转化加工费结算信息
     *
     * @param omsProductionOrderList
     * @return
     */
    private List<SmsSettleInfo> changeSmsSettleInfo(List<OmsProductionOrder> omsProductionOrderList) {
        List<Dict> dictList = omsProductionOrderList.stream().map(omsProductionOrder -> {
            Dict dict = new Dict();
            dict.put(PRODUCT_FACTORY_CODE, omsProductionOrder.getProductFactoryCode());
            dict.put(PRODUCT_LINE_CODE, omsProductionOrder.getProductLineCode());
            return dict;
        }).collect(toList());
        R factoryLineInfoListR = remoteFactoryLineInfoService.selectListByMapList(dictList);
        if (!factoryLineInfoListR.isSuccess()) {
            log.error("获取线体对应的供应商信息失败 res:{}", JSONObject.toJSONString(factoryLineInfoListR));
            throw new BusinessException("获取线体对应的供应商信息失败");
        }
        List<CdFactoryLineInfo> cdFactoryLineInfoList = factoryLineInfoListR.getCollectData(new TypeReference<List<CdFactoryLineInfo>>() {
        });
        //key 工厂+线体
        Map<String, CdFactoryLineInfo> supplierMap = cdFactoryLineInfoList.stream().collect(toMap(cdFactoryLineInfo ->
                        cdFactoryLineInfo.getProductFactoryCode() + cdFactoryLineInfo.getProduceLineCode(), cdFactoryLineInfo -> cdFactoryLineInfo,
                (key1, key2) -> key2));
        //获取采购组织
        R resultFactory = remoteFactoryInfoService.listAll();
        if (!resultFactory.isSuccess()) {
            log.error("remoteFactoryInfoService.listAll() 异常res:{}", JSONObject.toJSONString(resultFactory));
            throw new BusinessException("获取采购组织信息异常");
        }
        List<CdFactoryInfo> cdFactoryInfoList = resultFactory.getCollectData(new TypeReference<List<CdFactoryInfo>>() {
        });
        Map<String, CdFactoryInfo> cdFactoryInfoMap = cdFactoryInfoList.stream().collect(Collectors.toMap(cdFactoryInfo ->
                        cdFactoryInfo.getFactoryCode(),
                cdFactoryInfo -> cdFactoryInfo, (key1, key2) -> key2));

        List<SmsSettleInfo> smsSettleInfoList = new ArrayList<>();
        omsProductionOrderList.forEach(omsProductionOrder -> {
            String outsourceType = omsProductionOrder.getOutsourceType();
            Boolean flag = OutSourceTypeEnum.OUT_SOURCE_TYPE_BWW.getCode().equals(outsourceType)
                    || OutSourceTypeEnum.OUT_SOURCE_TYPE_QWW.getCode().equals(outsourceType);
            if (flag) {
                SmsSettleInfo smsSettleInfo = new SmsSettleInfo();
                smsSettleInfo.setLineNo(omsProductionOrder.getProductLineCode());
                String key = omsProductionOrder.getProductFactoryCode() + omsProductionOrder.getProductLineCode();
                CdFactoryLineInfo cdFactoryLineInfo = supplierMap.get(key);
                if (null == cdFactoryLineInfo || StringUtils.isBlank(cdFactoryLineInfo.getSupplierCode())) {
                    throw new BusinessException("请维护工厂" + omsProductionOrder.getProductFactoryCode()
                            + "线体" + omsProductionOrder.getProductLineCode() + "对应的供应商信息");
                }
                smsSettleInfo.setSupplierCode(cdFactoryLineInfo.getSupplierCode());
                smsSettleInfo.setSupplierName(cdFactoryLineInfo.getSupplierDesc());
                smsSettleInfo.setFactoryCode(omsProductionOrder.getProductFactoryCode());
                smsSettleInfo.setProductOrderCode(omsProductionOrder.getProductOrderCode());
                smsSettleInfo.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_1.getCode());
                smsSettleInfo.setProductMaterialCode(omsProductionOrder.getProductMaterialCode());
                smsSettleInfo.setProductMaterialName(omsProductionOrder.getProductMaterialDesc());
                smsSettleInfo.setBomVersion(omsProductionOrder.getBomVersion());
                smsSettleInfo.setOrderAmount(omsProductionOrder.getProductNum().intValue());
                smsSettleInfo.setOutsourceWay(outsourceType);
                CdFactoryInfo cdFactoryInfo = cdFactoryInfoMap.get(omsProductionOrder.getProductFactoryCode());
                if (null == cdFactoryInfo) {
                    log.error("获取工厂信息失败 工厂:{}", omsProductionOrder.getProductFactoryCode());
                    throw new BusinessException("请维护工厂" + omsProductionOrder.getProductFactoryCode()
                            + "信息");
                }
                String purchaseOrg = cdFactoryInfo.getPurchaseOrg();
                if (StringUtils.isBlank(purchaseOrg)) {
                    log.error("获取工厂对应采购组织信息失败 工厂:{}", omsProductionOrder.getProductFactoryCode());
                    throw new BusinessException("请维护工厂" + omsProductionOrder.getProductFactoryCode()
                            + "对应的采购组织信息");
                }
                String companyCode = cdFactoryInfo.getCompanyCode();
                if (StringUtils.isBlank(companyCode)) {
                    log.error("获取工厂对应公司信息失败 工厂:{}", omsProductionOrder.getProductFactoryCode());
                    throw new BusinessException("请维护工厂" + omsProductionOrder.getProductFactoryCode()
                            + "对应的公司信息");
                }
                smsSettleInfo.setCompanyCode(companyCode);
                //根据物料号和委外方式cd_settle_product_material查加工费号
                R settleProductMaterialR = remoteCdSettleProductMaterialService.selectOne(omsProductionOrder.getProductMaterialCode(),
                        outsourceType);
                if (!settleProductMaterialR.isSuccess()) {
                    log.error("根据物料号和委外方式查加工费号异常 专用号:{},委外方式:{},res:{}", omsProductionOrder.getProductMaterialCode(),
                            outsourceType, JSONObject.toJSON(settleProductMaterialR));
                    throw new BusinessException("根据物料号和委外方式查加工费号异常" + settleProductMaterialR.get("msg").toString());
                }
                CdSettleProductMaterial cdSettleProductMaterial = settleProductMaterialR.getData(CdSettleProductMaterial.class);
                //加工费号
                String rawMaterialCode = cdSettleProductMaterial.getRawMaterialCode();
                //根据加工费号,供应商,采购组织 查加工费
                R maResult = remoteCdMaterialPriceInfoService.selectOneByCondition(rawMaterialCode, purchaseOrg,
                        cdFactoryLineInfo.getSupplierCode());
                if (!maResult.isSuccess()) {
                    log.error("获取加工费失败 加工费号:{},采购组织:{},供应商:{}", rawMaterialCode,
                            purchaseOrg, cdFactoryLineInfo.getSupplierCode());
                    throw new BusinessException("获取加工费失败" + maResult.get("msg").toString());
                }
                CdMaterialPriceInfo cdMaterialPriceInfo = maResult.getData(CdMaterialPriceInfo.class);
                if (null == cdMaterialPriceInfo.getProcessPrice()) {
                    throw new BusinessException("请维护物料号" + omsProductionOrder.getProductMaterialCode()
                            + "供应商" + cdFactoryLineInfo.getSupplierCode() + "采购组织" + purchaseOrg + "对应的加工费");
                }
                smsSettleInfo.setMachiningPrice(cdMaterialPriceInfo.getProcessPrice());
                smsSettleInfo.setDelFlag(DeleteFlagConstants.NO_DELETED);
                smsSettleInfo.setProductStartDate(DateUtils.dateTime(YYYY_MM_DD, omsProductionOrder.getProductStartDate()));
                smsSettleInfo.setProductEndDate(DateUtils.dateTime(YYYY_MM_DD, omsProductionOrder.getProductEndDate()));
                smsSettleInfo.setActualEndDate(omsProductionOrder.getActualEndDate());
                smsSettleInfo.setConfirmAmont(0);
                smsSettleInfo.setCreateBy("定时任务");
                smsSettleInfo.setCreateTime(new Date());
                smsSettleInfoList.add(smsSettleInfo);
            }
        });
        return smsSettleInfoList;
    }

    /**
     * 邮件推送
     *
     * @return
     */
    @Override
    public R mailPush() {
        //1.查已传SAP的数据
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode());
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderMapper.selectByExample(example);
        //key 是分公司主管
        Map<String, List<OmsProductionOrder>> branchOfficeMap = new HashMap<>();
        //key 是班长
        Map<String, List<OmsProductionOrder>> monitorMap = new HashMap<>();
        omsProductionOrderList.forEach(omsProductionOrder -> {
            String branchOffice = omsProductionOrder.getBranchOffice();
            String monitor = omsProductionOrder.getMonitor();
            if (branchOfficeMap.containsKey(branchOffice)) {
                List<OmsProductionOrder> omsProductionOrderListGet = branchOfficeMap.get(branchOffice);
                omsProductionOrderListGet.add(omsProductionOrder);
                branchOfficeMap.put(branchOffice, omsProductionOrderListGet);
            } else if(StringUtils.isNotBlank(branchOffice)) {
                List<OmsProductionOrder> omsProductionOrderListNew = new ArrayList<>();
                omsProductionOrderListNew.add(omsProductionOrder);
                branchOfficeMap.put(branchOffice, omsProductionOrderListNew);
            }
            if (monitorMap.containsKey(monitor)) {
                List<OmsProductionOrder> omsProductionOrderListGet = monitorMap.get(monitor);
                omsProductionOrderListGet.add(omsProductionOrder);
                monitorMap.put(monitor, omsProductionOrderListGet);
            } else if(StringUtils.isNotBlank(monitor)) {
                List<OmsProductionOrder> omsProductionOrderListNew = new ArrayList<>();
                omsProductionOrderListNew.add(omsProductionOrder);
                monitorMap.put(monitor, omsProductionOrderListNew);
            }
        });
        //2.获取工厂线体信息
        R factoryLineInfoResult = remoteFactoryLineInfoService.listByExample(new CdFactoryLineInfo());
        if (!factoryLineInfoResult.isSuccess()) {
            log.error("获取工厂线体信息失败res:{}", JSONObject.toJSONString(factoryLineInfoResult));
            return factoryLineInfoResult;
        }
        List<CdFactoryLineInfo> cdFactoryLineInfoList = factoryLineInfoResult.getCollectData(new TypeReference<List<CdFactoryLineInfo>>() {
        });
        //主管对应的邮箱
        Map<String, CdFactoryLineInfo> branchOfficeFactoryLineMap = new HashMap<>();
        //班长对应的邮箱
        Map<String, CdFactoryLineInfo> monitorFactoryLineMap = new HashMap<>();
        cdFactoryLineInfoList.forEach(cdFactoryLineInfo -> {
            String branchOffice = cdFactoryLineInfo.getBranchOffice();
            String monitor = cdFactoryLineInfo.getMonitor();
            if (!branchOfficeFactoryLineMap.containsKey(branchOffice)) {
                branchOfficeFactoryLineMap.put(branchOffice, cdFactoryLineInfo);
            }
            if (!monitorFactoryLineMap.containsKey(monitor)) {
                monitorFactoryLineMap.put(monitor, cdFactoryLineInfo);
            }
        });

        //3.发送邮件
        log.info("邮件推送发送邮件开始");
        branchOfficeMap.keySet().forEach(branchOffice -> {
            List<OmsProductionOrder> productionOrderList = branchOfficeMap.get(branchOffice);
            CdFactoryLineInfo branchOfficeLineInfo = branchOfficeFactoryLineMap.get(branchOffice);
            if (null == branchOfficeLineInfo || StringUtils.isBlank(branchOfficeLineInfo.getBranchOfficeEmail())) {
                log.error("邮件推送时获取主管邮箱异常 branchOffice:{}", branchOffice);
            } else {
                String to = branchOfficeLineInfo.getBranchOfficeEmail();
                sendMail(productionOrderList, to);
            }
        });
        monitorMap.keySet().forEach(monitor -> {
            List<OmsProductionOrder> productionOrderList = monitorMap.get(monitor);
            CdFactoryLineInfo monitorLineInfo = monitorFactoryLineMap.get(monitor);
            if (null == monitorLineInfo || StringUtils.isBlank(monitorLineInfo.getBranchOfficeEmail())) {
                log.error("邮件推送时获取班长邮箱异常 monitor:{}", monitor);
            } else {
                String to = monitorLineInfo.getBranchOfficeEmail();
                sendMail(productionOrderList, to);
            }
        });
        log.info("邮件推送发送邮件结束");
        return R.ok();
    }


    /**
     * 发送邮件
     *
     * @param productionOrderList
     * @param to
     */
    private R sendMail(List<OmsProductionOrder> productionOrderList, String to) {
        List<OmsProductionOrderMailVo> productionOrderMailVoList = productionOrderList.stream().map(omsProductionOrde ->
                BeanUtil.copyProperties(omsProductionOrde, OmsProductionOrderMailVo.class)).collect(Collectors.toList());
        log.info("发送邮件开始");
        String fileName = "排产订单已下达SAP信息.xlsx";
        String subject = "排产订单已下达SAP信息";
        String content = "排产订单已下达SAP信息";
        String sheetName = "排产订单已下达SAP信息";
        List<List<String>> excelHeader = mailPushExcellHeader();
        R r = EasyExcelUtil.writeExcelWithHead(productionOrderMailVoList, fileName, sheetName, new OmsProductionOrderMailVo(), excelHeader);
        String path = r.getStr("msg");
        try {
            mailService.sendAttachmentMail(to, subject, content, new String[]{path});
        } catch (MessagingException me) {
            StringWriter w = new StringWriter();
            me.printStackTrace(new PrintWriter(w));
            log.error("发送邮件异常:{}", w.toString());
            throw new BusinessException("发送邮件异常");
        } catch (UnsupportedEncodingException ue) {
            StringWriter w = new StringWriter();
            ue.printStackTrace(new PrintWriter(w));
            log.error("发送邮件异常:{}", w.toString());
            throw new BusinessException("发送邮件异常");
        }
        FileUtil.del(path);
        return r;
    }

    /**
     * 邮件推送动态获取表头
     *
     * @return
     */
    private List<List<String>> mailPushExcellHeader() {

        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<>();
        // 第 n 行 的表头
        List<String> headTitle0 = new ArrayList<>();
        String date = DateUtils.getDate();
        headTitle0.add(date);
        headTitle0.add("分公司");
        List<String> headTitle1 = new ArrayList<>();
        headTitle1.add(date);
        headTitle1.add("班长");
        List<String> headTitle2 = new ArrayList<>();
        headTitle2.add(date);
        headTitle2.add("线号");
        List<String> headTitle3 = new ArrayList<>();
        headTitle3.add(date);
        headTitle3.add("订单批次号");
        List<String> headTitle4 = new ArrayList<>();
        headTitle4.add("智能电子生产部日生产定单计划");
        headTitle4.add("成品专用号");
        List<String> headTitle5 = new ArrayList<>();
        headTitle5.add("智能电子生产部日生产定单计划");
        headTitle5.add("成品描述");
        List<String> headTitle6 = new ArrayList<>();
        headTitle6.add("智能电子生产部日生产定单计划");
        headTitle6.add("PCB专用号");
        List<String> headTitle7 = new ArrayList<>();
        headTitle7.add("智能电子生产部日生产定单计划");
        headTitle7.add("排产订单数量");
        List<String> headTitle8 = new ArrayList<>();
        headTitle8.add("智能电子生产部日生产定单计划");
        headTitle8.add("基本开始日期");
        List<String> headTitle9 = new ArrayList<>();
        headTitle9.add("智能电子生产部日生产定单计划");
        headTitle9.add("顺序");
        List<String> headTitle10 = new ArrayList<>();
        headTitle10.add("智能电子生产部日生产定单计划");
        headTitle10.add("事业部T-1交货");
        List<String> headTitle11 = new ArrayList<>();
        headTitle11.add("智能电子生产部日生产定单计划");
        headTitle11.add("UPH");
        List<String> headTitle12 = new ArrayList<>();
        headTitle12.add("智能电子生产部日生产定单计划");
        headTitle12.add("产品用时");
        List<String> headTitle13 = new ArrayList<>();
        headTitle13.add("智能电子生产部日生产定单计划");
        headTitle13.add("产品定员");
        List<String> headTitle14 = new ArrayList<>();
        headTitle14.add("智能电子生产部日生产定单计划");
        headTitle14.add("版本");
        List<String> headTitle15 = new ArrayList<>();
        headTitle15.add("智能电子生产部日生产定单计划");
        headTitle15.add("发往地");
        List<String> headTitle16 = new ArrayList<>();
        headTitle16.add("智能电子生产部日生产定单计划");
        headTitle16.add("老品/新品");
        List<String> headTitle17 = new ArrayList<>();
        headTitle17.add("智能电子生产部日生产定单计划");
        headTitle17.add("产品状态");
        List<String> headTitle18 = new ArrayList<>();
        headTitle18.add("智能电子生产部日生产定单计划");
        headTitle18.add("是否卡萨帝");
        headList.add(headTitle0);
        headList.add(headTitle1);
        headList.add(headTitle2);
        headList.add(headTitle3);
        headList.add(headTitle4);
        headList.add(headTitle5);
        headList.add(headTitle6);
        headList.add(headTitle7);
        headList.add(headTitle8);
        headList.add(headTitle9);
        headList.add(headTitle10);
        headList.add(headTitle11);
        headList.add(headTitle12);
        headList.add(headTitle13);
        headList.add(headTitle14);
        headList.add(headTitle15);
        headList.add(headTitle16);
        headList.add(headTitle17);
        headList.add(headTitle18);
        return headList;
    }

    /**
     * 订单刷新
     *
     * @param ids
     * @return
     */
    @Override
    public R orderRefresh(String ids) {
        //1.查数据
        List<OmsProductionOrder> list = omsProductionOrderMapper.selectByIds(ids);
        //2.调用SAP
        R resultSAP = orderFromSap601InterfaceService.queryProductOrderFromSap601(list);
        if (!resultSAP.isSuccess()) {
            log.error("调用SAP获取生产订单号接口异常res:{}", JSONObject.toJSONString(resultSAP));
            return resultSAP;
        }
        //3.修改数据库
        List<OmsProductionOrder> listSapRes = (List<OmsProductionOrder>) resultSAP.get("data");
        if (CollectionUtils.isEmpty(listSapRes)) {
            return R.ok("订单刷新无需更新数据");
        }
        listSapRes.forEach(omsProductionOrder -> {
            if ("S".equals(omsProductionOrder.getSapFlag())) {
                omsProductionOrder.setNewVersion(omsProductionOrder.getBomVersion());
                omsProductionOrder.setBomVersion("");
            }
        });
        //修改数据
        omsProductionOrderMapper.batchUpdateByOrderCode(listSapRes);
        return R.ok();
    }

    @GlobalTransactional
    @Override
    public R timeGetConfirmAmont() {
        //注意:wms订单号12位,不足前面补零;sap获取的10位,存入订单系统的10位,在wms获取后截取
        //1.查已传SAP的排产订单
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode());
        criteria.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        List<OmsProductionOrder> omsProductionOrderListReq = omsProductionOrderMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(omsProductionOrderListReq)) {
            return R.ok("无需要更新入库量的数据");
        }
        //将集合工厂分组,key 工厂号 value 生产订单号的集合
        Map<String, List<String>> wmsMap = new HashMap<>();
        omsProductionOrderListReq.forEach(omsProductionOrder -> {
            String factoryCode = omsProductionOrder.getProductFactoryCode();
            String productOrderCode = omsProductionOrder.getProductOrderCode();
            if (wmsMap.containsKey(factoryCode)) {
                List<String> productOrderCodeList = wmsMap.get(factoryCode);
                productOrderCodeList.add(productOrderCode);
            } else {
                List<String> productOrderCodeList = new ArrayList<>();
                productOrderCodeList.add(productOrderCode);
                wmsMap.put(factoryCode, productOrderCodeList);
            }
        });

        //2.调用wms系统 获取入库数量
        List<OmsProductionOrder> omsProductionOrderListGet = new ArrayList<>();
        wmsMap.keySet().forEach(factoryCode -> {
            List<String> productOrderCodeList = wmsMap.get(factoryCode);
            //调用wms系统
            OutStorageResult outStorageResult = wmsGetDeliveryNum(productOrderCodeList, factoryCode);
            log.info("调用wms系统获取入库数量");
            if (null == outStorageResult || "0".equals(outStorageResult.getStatus())) {
                log.error("调用wms系统异常 factoryCode:{},productOrderCodeList:{},res:{}", factoryCode, wmsMap.get(factoryCode),
                        JSONObject.toJSONString(outStorageResult));
                throw new BusinessException("调用wms系统异常" + outStorageResult.getMsg());
            }
            List<OdsRawOrderOutStorageDTO> odsRawOrderOutStorageDTOList = outStorageResult.getData();
            log.info("wms获取的数据量size:{}",odsRawOrderOutStorageDTOList.size());
            odsRawOrderOutStorageDTOList.forEach(odsRawOrderOutStorageDTORes -> {
                OmsProductionOrder omsProductionOrder = new OmsProductionOrder();
                Date actualEndDate = DateUtils.convertToDate(odsRawOrderOutStorageDTORes.getGmtCreate());
                omsProductionOrder.setActualEndDate(actualEndDate);//最后入库时间
                String prdOrderNo = odsRawOrderOutStorageDTORes.getPrdOrderNo();
                omsProductionOrder.setProductOrderCode(prdOrderNo);//生产订单号
                omsProductionOrder.setDeliveryNum(new BigDecimal(odsRawOrderOutStorageDTORes.getProInAmount()));//入库数量
                omsProductionOrderListGet.add(omsProductionOrder);
            });

        });

        //3.更新排产订单和加工费结算表(如果订单数量与交货数量一致则更新订单状态为已关单,更新实际结束时间actual_end_date)
        //key是生产订单号
        if (CollectionUtils.isEmpty(omsProductionOrderListGet)) {
            return R.ok("SAP没有数据");
        }

        Map<String, OmsProductionOrder> omsProductionOrderMap = omsProductionOrderListReq.stream().collect(Collectors.toMap(
                omsProductionOrder -> omsProductionOrder.getProductOrderCode(),
                omsProductionOrder -> omsProductionOrder, (key1, key2) -> key2));
        List<SmsSettleInfo> smsSettleInfoList = new ArrayList<>();
        omsProductionOrderListGet.forEach(omsProductionOrder -> {
            SmsSettleInfo smsSettleInfo = new SmsSettleInfo();
            smsSettleInfo.setProductOrderCode(omsProductionOrder.getProductOrderCode());
            int confirmAmont = (omsProductionOrder.getDeliveryNum() == null) ? 0 : omsProductionOrder.getDeliveryNum().intValueExact();
            smsSettleInfo.setConfirmAmont(confirmAmont);
            smsSettleInfo.setUpdateBy("定时任务");

            omsProductionOrder.setUpdateBy("定时任务");
            OmsProductionOrder omsProductionOrderRes = omsProductionOrderMap.get(omsProductionOrder.getProductOrderCode());
            //交货数量与订单数量相等
            if (omsProductionOrder.getDeliveryNum().compareTo(omsProductionOrderRes.getProductNum()) == 0
                    || omsProductionOrder.getDeliveryNum().compareTo(omsProductionOrderRes.getProductNum()) == 1) {
                smsSettleInfo.setActualEndDate(omsProductionOrder.getActualEndDate());
                smsSettleInfo.setOrderStatus(SettleInfoOrderStatusEnum.ORDER_STATUS_2.getCode());
                omsProductionOrder.setStatus(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
            }
            smsSettleInfoList.add(smsSettleInfo);
        });
        //4.修改排产订单入库数量,完成时间
        omsProductionOrderMapper.batchUpdateByProductOrderCode(omsProductionOrderListGet);
        //根据生产订单号更新排产订单和加工费结算
        R result = remoteSettleInfoService.batchUpdateByProductOrderCode(smsSettleInfoList);
        if (!result.isSuccess()) {
            log.error("定时任务更新加工费结算入库量异常 res:{}", JSONObject.toJSONString(result));
            throw new BusinessException(result.get("msg").toString());
        }
        return R.ok();
    }

    /**
     * wms获取入库数量
     *
     * @return
     */
    public OutStorageResult wmsGetDeliveryNum(List<String> productOrderCodeList, String factoryCode) {
        log.info("调用wms系统获取入库数量");
        SysInterfaceLog sysInterfaceLog = new SysInterfaceLog();
        sysInterfaceLog.setAppId("wms");
        sysInterfaceLog.setInterfaceName("getQryPays");
        sysInterfaceLog.setContent("调用wms系统获取入库数量");
        /** url：webservice 服务端提供的服务地址，结尾必须加 "?wsdl"*/
        URL url = null;
        try {
            url = new URL(urlClaim);
            /** QName 表示 XML 规范中定义的限定名称,QName 的值包含名称空间 URI、本地部分和前缀 */
            QName qName = new QName(namespaceURL, localPart);
            javax.xml.ws.Service service = javax.xml.ws.Service.create(url, qName);
            RfWebService rfWebService = service.getPort(RfWebService.class);
            OdsRawOrderOutStorageDTO odsRawOrderOutStorageDTO = new OdsRawOrderOutStorageDTO();
            odsRawOrderOutStorageDTO.setSapFactoryCode(factoryCode);
            odsRawOrderOutStorageDTO.setPrdOrderNoList(productOrderCodeList);
            OutStorageResult outStorageResult = rfWebService.findAllCodeForJIT(odsRawOrderOutStorageDTO);
            return outStorageResult;
        } catch (Exception e) {
            sysInterfaceLog.setResults("调用wms系统获取入库数量异常");
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.error(
                    "调用wms系统获取入库数量异常: {}", w.toString());
            throw new BusinessException("调用wms系统获取入库数量异常");
        } finally {
            remoteInterfaceLogService.saveInterfaceLog(sysInterfaceLog);
        }
    }

    private String getBomGroupKey(CdBomInfo cdBomInfo) {
        return StrUtil.concat(true, cdBomInfo.getProductMaterialCode(), cdBomInfo.getProductFactoryCode(), cdBomInfo.getVersion());
    }

    /**
     * Description:  日期字符串格式转换
     * Param: [dateStr]
     * return: java.lang.String
     * Author: ltq
     * Date: 2020/7/3
     */
    private String formatDateString(String dateStr) {
        boolean isSuccess = true;
        if (dateStr == null) {
            return null;
        }
        try {
            Date date = DateUtils.parseDate(dateStr, parsePatterns);
        } catch (ParseException e) {
            isSuccess = false;
        }
        if (isSuccess) {
            dateStr = StrUtil.replace(dateStr, ".", "-");
            dateStr = StrUtil.replace(dateStr, "/", "-");
        }
        return dateStr;
    }
}
