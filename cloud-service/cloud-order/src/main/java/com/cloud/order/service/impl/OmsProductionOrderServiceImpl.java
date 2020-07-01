package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.constant.ActProcessContants;
import com.cloud.activiti.feign.RemoteActOmsProductionOrderService;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.constant.ProductOrderConstants;
import com.cloud.common.constant.RawMaterialFeedbackConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsProductionOrderDel;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.domain.entity.OmsProductionOrderDetailDel;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.domain.entity.vo.OmsProductionOrderMailVo;
import com.cloud.order.domain.entity.vo.OmsProductionOrderVo;
import com.cloud.order.enums.ProductionOrderStatusEnum;
import com.cloud.order.mail.MailService;
import com.cloud.order.mapper.OmsProductionOrderMapper;
import com.cloud.order.service.IOmsProductionOrderDelService;
import com.cloud.order.service.IOmsProductionOrderDetailDelService;
import com.cloud.order.service.IOmsProductionOrderDetailService;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.service.IOmsRawMaterialFeedbackService;
import com.cloud.order.service.IOrderFromSap601InterfaceService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.CdMaterialInfo;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.po.SysUserRights;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteCdProductOverdueService;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import com.cloud.system.feign.RemoteMaterialExtendInfoService;
import com.cloud.system.feign.RemoteMaterialService;
import com.cloud.system.feign.RemoteSequeceService;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 排产订单 Service业务层处理
 *
 * @author cs
 * @date 2020-05-29
 */
@Service
@Slf4j
public class OmsProductionOrderServiceImpl extends BaseServiceImpl<OmsProductionOrder> implements IOmsProductionOrderService {
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

    private static final String NO_QUOTA_REMARK = "请维护产品定员、分公司主管、班长信息";

    private static final String NO_LIFECYCLE_REMARK = "请维护生命周期信息";

    private static final String CHECK_ORDER_STATUS = "系统中存在相同且在流程中的数据记录";

    private final static String YYYY_MM_DD = "yyyy-MM-dd";//时间格式

    private static final String ZN_ATTESTATION = "0";//zn认证，否


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
    /**
     * Description:  排产订单导入
     * Param: [list, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    public R importProductOrder(List<OmsProductionOrderVo> list, SysUser sysUser) {
        if (CollectionUtil.isEmpty(list)) {
            return R.error("导入数据不存在");
        }
        List<Dict> paramsMapList = list.stream().map(omsProductionOrder ->
                new Dict().set(PRODUCT_FACTORY_CODE, omsProductionOrder.getProductFactoryCode())
                        .set(PRODUCT_MATERIAL_CODE, omsProductionOrder.getProductMaterialCode())
                        .set(PRODUCT_LINE_CODE, omsProductionOrder.getProductLineCode())
                        .set(BOM_VERSION, omsProductionOrder.getBomVersion())
                        .set(PRODUCT_MATERIAL, PRODUCT_MATERIAL_TYPE)
        ).distinct().collect(toList());
        /**调用服务获取成品物料扩展信息  start*/
        //调用服务获取成品物料扩展信息
        R materialExtendMap = remoteMaterialExtendInfoService.selectByMaterialList(paramsMapList);
        if (!materialExtendMap.isSuccess()) {
            log.error("调用服务获取成品物料扩展信息失败！");
            return R.error("调用服务获取成品物料扩展信息失败!");
        }
        List<CdMaterialExtendInfo> materialExtendInfoList = materialExtendMap.getCollectData(new TypeReference<List<CdMaterialExtendInfo>>() {
        });
        if (CollectionUtil.isEmpty(materialExtendInfoList)) {
            log.error("根据成品专用号查询成品物料扩展信息为空！");
            return R.error("根据成品专用号查询成品物料扩展信息为空，请维护物料扩展信息数据！");
        }
        /**调用服务获取成品物料扩展信息  end*/

        /**调用服务获取物料主数据信息  start*/
        //调用服务获取物料主数据信息
        R materialInfoMap = remoteMaterialService.selectListByMaterialList(paramsMapList);
        if (!materialInfoMap.isSuccess()) {
            log.error("调用system服务根据成品物料、生产工厂、物料类型查询物料信息失败：" + materialInfoMap.get("msg"));
            return R.error("调用system服务根据成品物料、生产工厂、物料类型查询物料信息失败!");
        }
        List<CdMaterialInfo> materialInfoList = materialInfoMap.getCollectData(new TypeReference<List<CdMaterialInfo>>() {
        });
        /**调用服务获取物料主数据信息  end*/

        /**调用服务获取工厂线体信息数据  start*/
        R factoryLineMap = remoteFactoryLineInfoService.selectListByMapList(paramsMapList);
        if (!factoryLineMap.isSuccess()) {
            log.error("调用system服务根据生产工厂、线体查询线体信息失败：" + factoryLineMap.get("msg"));
            return R.error("调用system服务根据生产工厂、线体查询线体信息失败!");
        }
        //获取线体信息
        List<CdFactoryLineInfo> cdFactoryLineInfoList = factoryLineMap.getCollectData(new TypeReference<List<CdFactoryLineInfo>>() {
        });
        /**调用服务获取工厂线体信息数据  end*/

        /**调用服务获取BOM清单数据  start*/
        R bomInfoMap = remoteBomService.selectBomList(paramsMapList);
        if (!bomInfoMap.isSuccess()) {
            log.error("调用system服务根据生产工厂、成品专用号、bom版本查询BOM信息失败：" + bomInfoMap.get("msg"));
            return R.error("调用system服务根据生产工厂、成品专用号、bom版本查询BOM信息失败!");
        }
        List<CdBomInfo> bomInfoList = bomInfoMap.getCollectData(new TypeReference<List<CdBomInfo>>() {
        });
        /**调用服务获取BOM清单数据  start*/

        //1、数据校验，数据组织
        //1-1、判断可否加工承揽（cd_material_extend_info），如果否，则提示用户只允许自制，无法导入；如果是，则通过
        //获取不能加工承揽的物料信息
        List<String> noOutsource = new ArrayList<>();
        noOutsource.add("1");
        List<String> noMaterialList = materialExtendInfoList.stream()
                .filter((CdMaterialExtendInfo m) -> noOutsource.contains(m.getIsPuttingOut()))
                .map(CdMaterialExtendInfo::getMaterialCode)
                .collect(toList());
        //获取加工承揽的排产订单导入数据
        List<String> outsourceStr = new ArrayList<>();
        outsourceStr.add("0");
        outsourceStr.add("1");
        List<OmsProductionOrderVo> outsourceOrderList = list.stream().filter((OmsProductionOrderVo o) ->
                outsourceStr.contains(o.getOutsourceType())).collect(toList());
        //获取导入的加工承揽的但不允许加工承揽的排产订单,需返回给用户
        List<OmsProductionOrderVo> noOutsourceOrderList = outsourceOrderList.stream().filter((OmsProductionOrderVo o) ->
                noMaterialList.contains(o.getProductMaterialCode())).collect(toList());
        //定义失败原因
        noOutsourceOrderList.forEach(out -> out.setExportRemark(NO_OUTSOURCE_REMARK));
        //需导入数据
        List<OmsProductionOrderVo> omsProductionOrderVos = list.stream()
                .filter(item -> !noOutsourceOrderList.contains(item)).collect(toList());
        //1-2、UPH节拍：根据导入信息的成品物料号、生产工厂获取物料信息表（cd_material_info）中对应的UPH节拍；
        //匹配UPH节拍数据
        omsProductionOrderVos.forEach(o -> materialInfoList.forEach(m -> {
            if (o.getProductFactoryCode().equals(m.getPlantCode())
                    && o.getProductMaterialCode().equals(m.getMaterialCode())) {
                o.setRhythm(m.getUph());
                o.setProductMaterialDesc(m.getMaterialDesc());
            }
        }));
        //筛选没有UPH节拍的数据
        List<OmsProductionOrderVo> noUphProductOrders = omsProductionOrderVos.stream()
                .filter(o -> StringUtils.isBlank(o.getRhythm().toString())
                        || o.getRhythm().compareTo(BigDecimal.ZERO) == 0).collect(toList());
        //定义失败原因
        noUphProductOrders.forEach(uph -> uph.setExportRemark(NO_UPH_REMARK));
        omsProductionOrderVos = omsProductionOrderVos.stream()
                .filter(item -> !noUphProductOrders.contains(item)).collect(toList());
        //计算用时：排产量/UPH节拍
        omsProductionOrderVos.forEach(o ->
                o.setUseTime(o.getProductNum().divide(o.getRhythm(), 2, BigDecimal.ROUND_HALF_UP)));
        //1-3、产品定员：根据成品生产工厂、线体获取工厂线体关系表（cd_factory_line_info）中的产品定员；
        //1-6、分公司主管、班长：根据生产工厂、线体获取工厂线体关系表（cd_factory_line_info）中的分公司主管、班长的信息；
        //匹配产品定员信息,分公司主管、班长
        omsProductionOrderVos.forEach(o -> cdFactoryLineInfoList.forEach(f -> {
            if (o.getProductFactoryCode().equals(f.getProductFactoryCode())
                    && o.getProductLineCode().equals(f.getProduceLineCode())) {
                o.setProductQuota(f.getProductQuota());
                o.setBranchOffice(f.getBranchOffice());
                o.setMonitor(f.getMonitor());
            }
        }));
        //筛选没有产品定员、分公司主管、班长的信息
        List<OmsProductionOrderVo> noQuotaProductOrders = omsProductionOrderVos.stream()
                .filter(o ->
                        (StringUtils.isBlank(String.valueOf(o.getProductQuota()))
                                || o.getProductQuota() == 0)
                                || StringUtils.isBlank(o.getBranchOffice())
                                || StringUtils.isBlank(o.getMonitor()))
                .collect(toList());
        //定义失败原因
        noQuotaProductOrders.forEach(q -> q.setExportRemark(NO_QUOTA_REMARK));
        omsProductionOrderVos = omsProductionOrderVos.stream()
                .filter(item -> !noQuotaProductOrders.contains(item)).collect(toList());

        //1-7、生命周期：根据成品专用号获取物料扩展信息表（cd_material_extend_info）中的生命周期；
        //匹配生命周期
        omsProductionOrderVos.forEach(o -> materialExtendInfoList.forEach(m -> {
            if (o.getProductMaterialCode().equals(m.getMaterialCode())) {
                o.setLifeCycle(m.getLifeCycle());
            }
        }));
        //筛选没有生命周期的数据
        List<OmsProductionOrderVo> noLifeCycleProductOrders = omsProductionOrderVos.stream()
                .filter(o -> StringUtils.isBlank(o.getLifeCycle()))
                .collect(toList());
        noLifeCycleProductOrders.forEach(life -> life.setExportRemark(NO_LIFECYCLE_REMARK));
        omsProductionOrderVos = omsProductionOrderVos.stream()
                .filter(item -> !noLifeCycleProductOrders.contains(item))
                .collect(toList());

        //根据生产工厂、成品物料、线体号、开始日期、结束日期、bom版本删除库中数据
        List<OmsProductionOrderVo> checkOrderStatus = deleteOldProductOrder(omsProductionOrderVos);
        checkOrderStatus.forEach(o -> o.setExportRemark(CHECK_ORDER_STATUS));
        omsProductionOrderVos = omsProductionOrderVos.stream()
                .filter(item -> !checkOrderStatus.contains(item))
                .collect(toList());
        //1-8、排产订单号：根据生成规则生成排产订单号；
        List<OmsProductionOrder> omsProductionOrders = omsProductionOrderVos.stream().map(o -> {
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
            return omsProductionOrder;
        }).collect(toList());
        //BOM拆解流程
        R bomDisassemblyResult = bomDisassembly(omsProductionOrders, bomInfoList, sysUser);
        if (!bomDisassemblyResult.isSuccess()) {
            log.error("BOM拆解流程失败，原因：" + bomDisassemblyResult.get("msg"));
            return R.error("BOM拆解流程失败!");
        }

        //4、3版本审批校验，邮件通知排产员3版本审批
        List<OmsProductionOrder> checkOmsProductList = checkThreeVersion(omsProductionOrders, sysUser);

        //5、超期未关闭订单审批校验，邮件通知工厂订单 - 工厂小微主 超期未关闭订单审批
        List<OmsProductionOrder> insertProductOrderList = checkOverdueNotCloseOrder(checkOmsProductList, sysUser);
        if (insertProductOrderList.size() > 0) {
            omsProductionOrderMapper.insertList(insertProductOrderList);
        }
        //组织缺基础数据的订单
        List<OmsProductionOrderVo> exportProductOrder = new ArrayList<>();
        exportProductOrder.addAll(noLifeCycleProductOrders);
        exportProductOrder.addAll(noOutsourceOrderList);
        exportProductOrder.addAll(noQuotaProductOrders);
        exportProductOrder.addAll(noUphProductOrders);
        exportProductOrder.addAll(checkOrderStatus);
        if (exportProductOrder.size() > 0) {
            return EasyExcelUtilOSS.writeExcel(exportProductOrder, "排产订单.xlsx", "sheet", new OmsProductionOrderVo());
        } else {
            return R.ok();
        }
    }

    /**
     * Description: 删除排产订单
     * Param: [ids]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/22
     */
    @Override
    public R deleteByIdString(String ids, SysUser sysUser) {
        List<OmsProductionOrder> omsProductionOrders = omsProductionOrderMapper.selectByIds(ids);
        if (omsProductionOrders.size() <= 0) {
            log.info("根据排产订单ID没有查询出数据，直接返回成功！");
            return R.ok();
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
        //将删除的排产订单存到删除表中
        omsProductionOrderDelService.insertList(omsProductionOrderDels);
        //删除排产订单表数据
        omsProductionOrderMapper.deleteByIds(ids);
        //删除排产订单明细数据
        omsProductionOrderDetailService.deleteByIds(detailIds);
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
        //待传SAP，传SAP中，已传SAP状态的数据不可修改
        if (ProductOrderConstants.STATUS_FOUR.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_FIVE.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_SIX.equals(productionOrder.getStatus())) {
            log.info("待传SAP、传SAP中、已传SAP状态的记录不可修改！");
            return R.error("待传SAP、传SAP中、已传SAP状态的记录不可修改！");
        }
        if (ProductOrderConstants.STATUS_ONE.equals(productionOrder.getStatus())
                || ProductOrderConstants.STATUS_TWO.equals(productionOrder.getStatus())) {
            //”反馈中“、“待调整”状态的数据
            //根据排产订单号查询原材料反馈信息表记录
            List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks =
                    omsRawMaterialFeedbackService.select(OmsRawMaterialFeedback.builder()
                            .productOrderCode(productionOrder.getOrderCode()).status("0").build());
            //如果存在反馈信息记录，判断修改后的量，如果小于等于所有反馈信息记录的成品满足量，
            //则更新反馈信息表记录状态为“通过”，排产订单状态“已评审”，明细数据更新成“已确认”
            omsRawMaterialFeedbacks.forEach(f -> {
                if (omsProductionOrder.getProductNum().compareTo(f.getProductContentNum()) < 0) {
                    f.setStatus(RawMaterialFeedbackConstants.STATUS_ONE);
                }
            });
            //统计修改后的量小于等于反馈记录成品满足量的记录
            List<OmsRawMaterialFeedback> rawMaterialFeedbacks =
                    omsRawMaterialFeedbacks.stream().filter(r -> omsProductionOrder.getProductNum()
                            .compareTo(r.getProductContentNum()) < 0).collect(toList());
            //如果满足数量的记录条数与反馈信息总条数相同，则排产订单状态为“已评审”
            if (omsRawMaterialFeedbacks.size() == rawMaterialFeedbacks.size()) {
                omsProductionOrder.setStatus(ProductOrderConstants.STATUS_THREE);
            }
        } else if (ProductOrderConstants.STATUS_THREE.equals(productionOrder.getStatus())) {
            //“已评审”状态的排产订单
            //如果排产量向下调整，则无需JIT重新评审，状态无需变更；
            //如果排产量向上调整，需JIT重新评审，即状态更新为“待评审”，排产订单明细状态更新为“未确认”；
            if (omsProductionOrder.getProductNum().compareTo(productionOrder.getProductNum()) > 0) {
                omsProductionOrder.setStatus(ProductOrderConstants.STATUS_ZERO);
            }
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
            return R.error("获取bom清单数据失败!");
        }
        List<CdBomInfo> bomInfos = bomMap.getCollectData(new TypeReference<List<CdBomInfo>>() {
        });
        if (CollUtil.isEmpty(bomInfos)) {
            log.error("获取bom清单数据为空！");
            return R.error("获取bom清单数据为空!");
        }
        String detailStatus = omsProductionOrder.getStatus().equals(ProductOrderConstants.STATUS_ZERO)
                ? ProductOrderConstants.DETAIL_STATUS_ZERO : ProductOrderConstants.DETAIL_STATUS_ONE;
        List<OmsProductionOrderDetail> omsProductionOrderDetails = new ArrayList<>();
        bomInfos.forEach(bom -> {
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
                    .purchaseGroup(bom.getPurchaseGroup())
                    .storagePoint(bom.getStoragePoint())
                    .status(detailStatus)
                    .delFlag(DeleteFlagConstants.NO_DELETED)
                    .build();
            omsProductionOrderDetail.setCreateTime(new Date());
            omsProductionOrderDetail.setCreateBy(sysUser.getLoginName());
            omsProductionOrderDetails.add(omsProductionOrderDetail);
        });
        omsProductionOrderDetailService.insertList(omsProductionOrderDetails);
        if (omsProductionOrder.getStatus().equals(ProductOrderConstants.STATUS_ZERO)) {
            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JIT);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
                return R.error("获取权限用户列表失败!");
            }
            List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            //获取JIT邮箱信息
            Set<SysUser> sysUsers = new HashSet<>();
            sysUserRightsList.forEach(u ->
                    omsProductionOrderDetails.forEach(o -> {
                        if (u.getProductFactorys().contains(o.getProductFactoryCode())
                                && u.getPurchaseGroups().contains(o.getPurchaseGroup())) {
                            sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                        }
                    })
            );
            //发送邮件
            sysUsers.forEach(u -> {
                String email = u.getEmail();
                String context = u.getUserName() + EmailConstants.RAW_MATERIAL_REVIEW_CONTEXT;
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
    public R confirmRelease(OmsProductionOrder omsProductionOrder, SysUser sysUser) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        List<OmsProductionOrder> omsProductionOrderList = new ArrayList<>();
        if (BeanUtil.isEmpty(omsProductionOrder)) {
            criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            criteria.andEqualTo("status", ProductOrderConstants.STATUS_THREE);
            omsProductionOrderList = omsProductionOrderMapper.selectByExample(example);
        } else {
            String ids = omsProductionOrder.getIds();
            if (StrUtil.isNotBlank(ids)) {
                omsProductionOrderList = omsProductionOrderMapper.selectByIds(ids);
                for (OmsProductionOrder productionOrder : omsProductionOrderList) {
                    if (!productionOrder.getStatus().equals(ProductOrderConstants.STATUS_THREE)) {
                        return R.error("非“已评审”状态的排产订单不可确认下达！");
                    }
                }
            } else if (BeanUtil.isNotEmpty(omsProductionOrder)) {
                if (!ProductOrderConstants.STATUS_THREE.equals(omsProductionOrder.getStatus())) {
                    log.error("确认下达传入排产订单状态非已评审！");
                    return R.error("只可以下达已评审的排产订单！");
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
                if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
                    criteria.andLike("productMaterialCode", omsProductionOrder.getProductMaterialCode());
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
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsProductionOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsProductionOrder.getProductFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductLineCode())) {
            criteria.andEqualTo("productLineCode", omsProductionOrder.getProductLineCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getStatus())) {
            criteria.andEqualTo("status", omsProductionOrder.getStatus());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode", omsProductionOrder.getProductMaterialCode());
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
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //班长、分主管查询工厂下的数据
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_BZ)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_FZG)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)) {
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            }
        }
        return omsProductionOrderMapper.selectByExample(example);
    }

    /**
     * Description:  排产订单导出
     * Param: [omsProductionOrder, sysUser]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderVo>
     * Author: ltq
     * Date: 2020/6/23
     */
    @Override
    public List<OmsProductionOrder> exportAll(OmsProductionOrder omsProductionOrder, SysUser sysUser) {

        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsProductionOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsProductionOrder.getProductFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductLineCode())) {
            criteria.andEqualTo("productLineCode", omsProductionOrder.getProductLineCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getStatus())) {
            criteria.andEqualTo("status", omsProductionOrder.getStatus());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getCheckDateStart())) {
            if (ProductOrderConstants.DATE_TYPE_ONE.equals(omsProductionOrder.getDateType())) {
                criteria.andLessThanOrEqualTo("deliveryDate", omsProductionOrder.getCheckDateStart());
            } else if (ProductOrderConstants.DATE_TYPE_TWO.equals(omsProductionOrder.getDateType())) {
                criteria.andLessThanOrEqualTo("productStartDate", omsProductionOrder.getCheckDateStart());
            } else if (ProductOrderConstants.DATE_TYPE_THREE.equals(omsProductionOrder.getDateType())) {
                criteria.andLessThanOrEqualTo("productEndDate", omsProductionOrder.getCheckDateStart());
            }
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getCheckDateEnd())) {
            if (ProductOrderConstants.DATE_TYPE_ONE.equals(omsProductionOrder.getDateType())) {
                criteria.andGreaterThanOrEqualTo("deliveryDate", omsProductionOrder.getCheckDateEnd());
            } else if (ProductOrderConstants.DATE_TYPE_TWO.equals(omsProductionOrder.getDateType())) {
                criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getCheckDateEnd());
            } else if (ProductOrderConstants.DATE_TYPE_THREE.equals(omsProductionOrder.getDateType())) {
                criteria.andGreaterThanOrEqualTo("productEndDate", omsProductionOrder.getCheckDateEnd());
            }
        }
        if (StrUtil.isNotBlank(omsProductionOrder.getOrderType())) {
            criteria.andEqualTo("orderType", omsProductionOrder.getOrderType());
        }
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //班长、分主管查询工厂下的数据
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_BZ)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_FZG)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)) {
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
            }
        }
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderMapper.selectByExample(example);
        List<OmsProductionOrderVo> productionOrderVos = omsProductionOrderList.stream().map(o -> {
            OmsProductionOrderVo omsProductionOrderVo = new OmsProductionOrderVo();
            BeanUtils.copyProperties(o, omsProductionOrderVo);
            return omsProductionOrderVo;
        }).collect(Collectors.toList());
        return omsProductionOrderList;
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
        List<OmsProductionOrder> checkList = new ArrayList<>();
        list.forEach(o -> {
            if (ProductOrderConstants.BOM_VERSION_THREE.equals(o.getBomVersion())
                    && o.getProductNum().compareTo(ProductOrderConstants.BOM_VERSION_THREE_NUM) > 0) {
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_TWO);
                checkList.add(o);
            }
        });
        if (checkList.size() > 0) {
            //  3版本审批流程
            checkList.forEach(omsProductionOrder -> {
                R r = remoteActOmsProductionOrderService.startActProcess(ActProcessContants.ACTIVITI_THREE_VERSION_REVIEW
                        , omsProductionOrder.getId().toString(), omsProductionOrder.getOrderCode(), sysUser.getUserId()
                        , ActProcessContants.ACTIVITI_PRO_TITLE_THREE_VERSION);
                if (!r.isSuccess()) {
                    log.error("开启排产订单3版本审批流失败，原因：" + r.get("msg"));
                    throw new BusinessException("开启排产订单3版本审批流失败!");
                }
            });
            String email = sysUser.getEmail();
            String context = sysUser.getUserName() + EmailConstants.THREE_VERSION_REVIEW_CONTEXT;
            mailService.sendTextMail(email, EmailConstants.TITLE_THREE_VERSION_REVIEW, context);
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
        list.forEach(o ->
                omsProductionOrders.forEach(order -> {
                    if (o.getProductFactoryCode().equals(order.getProductFactoryCode())
                            && o.getProductMaterialCode().equals(order.getProductMaterialCode())
                            && o.getProductLineCode().equals(order.getProductLineCode())) {
                        o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_TWO);
                    }
                })
        );
        if (omsProductionOrders.size() > 0) {
            //  超期未关闭订单审批流程
            omsProductionOrders.forEach(omsProductionOrder -> {
                R r = remoteActOmsProductionOrderService.startActProcess(ActProcessContants.ACTIVITI_OVERDUE_NOT_CLOSE_ORDER_REVIEW
                        , omsProductionOrder.getId().toString(), omsProductionOrder.getOrderCode(), sysUser.getUserId()
                        , ActProcessContants.ACTIVITI_PRO_TITLE_OVERDUE_NOT_CLOSE);
                if (!r.isSuccess()) {
                    log.error("开启排产订单超期未关闭订单审批流程失败，原因：" + r.get("msg"));
                    throw new BusinessException("开启排产订单超期未关闭订单审批流程失败!");
                }
            });
            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_ORDER);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
            }
            List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            Set<SysUser> sysUsers = new HashSet<>();
            sysUserRightsList.forEach(u ->
                    omsProductionOrders.forEach(o -> {
                        if (u.getProductFactorys().contains(o.getProductFactoryCode())) {
                            sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                        }
                    })
            );
            //发送邮件
            sysUsers.forEach(u -> {
                String email = u.getEmail();
                String context = u.getUserName() + EmailConstants.OVERDUE_NOT_CLOSE_ORDER_REVIEW_CONTEXT;
                mailService.sendTextMail(email, EmailConstants.TITLE_OVERDUE_NOT_CLOSE_ORDER_REVIEW, context);
            });
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
        List<OmsProductionOrder> omsProductionOrders = new ArrayList<>();
        list.forEach(o -> {
            R overStockMap = remoteCdProductOverdueService.selectOverStockByFactoryAndMaterial(CdProductOverdue
                    .builder().productFactoryCode(o.getProductFactoryCode())
                    .productMaterialCode(o.getProductMaterialCode()).build());
            if (!overStockMap.isSuccess()) {
                log.error("根据工厂、物料号查询超期库存信息失败，原因：" + overStockMap.get("msg"));
            }
            List<CdProductOverdue> productOverdues =
                    overStockMap.getCollectData(new TypeReference<List<CdProductOverdue>>() {
                    });
            if (BeanUtil.isNotEmpty(productOverdues) && productOverdues.size() > 0) {
                o.setAuditStatus(ProductOrderConstants.AUDIT_STATUS_ONE);
            }
            omsProductionOrders.add(o);
        });
        if (omsProductionOrders.size() > 0) {
            omsProductionOrders.forEach(omsProductionOrder -> {
                R r = remoteActOmsProductionOrderService.startActProcess(ActProcessContants.ACTIVITI_OVERDUE_STOCK_ORDER_REVIEW
                        , omsProductionOrder.getId().toString(), omsProductionOrder.getOrderCode(), sysUser.getUserId()
                        , ActProcessContants.ACTIVITI_PRO_TITLE_OVERDUE_STOCK);
                if (!r.isSuccess()) {
                    log.error("开启排产订单超期未关闭订单审批流程失败，原因：" + r.get("msg"));
                    throw new BusinessException("开启排产订单超期未关闭订单审批流程失败!");
                }
            });
            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_ORDER);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
            }
            List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            Set<SysUser> sysUsers = new HashSet<>();
            sysUserRightsList.forEach(u ->
                    omsProductionOrders.forEach(o -> {
                        if (u.getProductFactorys().contains(o.getProductFactoryCode())) {
                            sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                        }
                    })
            );
            //发送邮件
            sysUsers.forEach(u -> {
                String email = u.getEmail();
                String context = u.getUserName() + EmailConstants.OVER_STOCK_CONTEXT;
                mailService.sendTextMail(email, EmailConstants.TITLE_OVER_STOCK, context);
            });
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
        omsProductionOrderDetailService.insertList(omsProductionOrderDetails);
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
            return R.error("获取权限用户列表失败!");
        }
        List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
        });
        //3-1、获取JIT邮箱信息
        Set<SysUser> sysUsers = new HashSet<>();
        sysUserRightsList.forEach(u ->
                detailList.forEach(o -> {
                    if (u.getProductFactorys().contains(o.getProductFactoryCode())
                            && u.getPurchaseGroups().contains(o.getPurchaseGroup())) {
                        sysUsers.add(SysUser.builder().userName(u.getUserName()).email(u.getEmail()).build());
                    }
                })
        );
        //3-2、发送邮件
        sysUsers.forEach(u -> {
            String email = u.getEmail();
            String contexts = u.getUserName() + EmailConstants.RAW_MATERIAL_REVIEW_CONTEXT;
            mailService.sendTextMail(email, EmailConstants.TITLE_RAW_MATERIAL_REVIEW, contexts);
        });
        return R.ok();
    }

    /**
     * Description:  删除重复数据
     * Param: [list]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsProductionOrderVo>
     * Author: ltq
     * Date: 2020/6/23
     */
    private List<OmsProductionOrderVo> deleteOldProductOrder(List<OmsProductionOrderVo> list) {
        List<OmsProductionOrderVo> checkOrderStatus = new ArrayList<>();
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
                    OmsProductionOrderVo omsProductionOrderVo = new OmsProductionOrderVo();
                    BeanUtils.copyProperties(omsProductionOrder, omsProductionOrderVo);
                    checkOrderStatus.add(omsProductionOrderVo);
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
            addOrderList.forEach(o -> {
                R r = remoteActOmsProductionOrderService.startActProcess(ActProcessContants.ACTIVITI_ADD_REVIEW
                        , o.getId().toString(), o.getOrderCode(), sysUser.getUserId(), ActProcessContants.ACTIVITI_PRO_TITLE_ADD);
                if (!r.isSuccess()) {
                    log.error("开启排产订单T+2追加订单审批流程失败，原因：" + r.get("msg"));
                    throw new BusinessException("开启排产订单T+2追加订单审批流程失败!");
                }
            });
            //获取国产件JIT处长
            R jitUserRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JITCZGC);
            if (!jitUserRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + jitUserRightsMap.get("msg"));
                throw new BusinessException("获取权限用户列表失败!");
            }
            List<SysUserRights> sysUserRightsJitGcList = jitUserRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            //获取进口件JIT处长信息
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_JITCZJK);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
                throw new BusinessException("获取权限用户列表失败!");
            }
            List<SysUserRights> sysUserRightsJitJkList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            sysUserRightsJitGcList.addAll(sysUserRightsJitJkList);
            //发送邮件
            sysUserRightsJitGcList.forEach(u -> {
                String email = u.getEmail();
                String contexts = u.getUserName() + EmailConstants.ADD_ORDER_REVIEW_CONTEXT;
                mailService.sendTextMail(email, EmailConstants.TITLE_ADD_ORDER_REVIEW, contexts);
            });
        }
        //ZN 认证邮件通知
        if (znOrderList.size() > 0) {
            //开启ZN认证审批流程
            znOrderList.forEach(o -> {
                R r = remoteActOmsProductionOrderService.startActProcess(ActProcessContants.ACTIVITI_ZN_REVIEW
                        , o.getId().toString(), o.getOrderCode(), sysUser.getUserId(), ActProcessContants.ACTIVITI_PRO_TITLE_ZN);
                if (!r.isSuccess()) {
                    log.error("开启排产订单ZN认证审批流程失败，原因：" + r.get("msg"));
                    throw new BusinessException("开启排产订单ZN认证审批流程失败!");
                }
            });
            //获取权限用户列表
            R userRightsMap = userService.selectUserRights(RoleConstants.ROLE_KEY_ZLGCS);
            if (!userRightsMap.isSuccess()) {
                log.error("获取权限用户列表失败：" + userRightsMap.get("msg"));
                throw new BusinessException("获取权限用户列表失败!");
            }
            List<SysUserRights> sysUserRightsList = userRightsMap.getCollectData(new TypeReference<List<SysUserRights>>() {
            });
            //发送邮件
            sysUserRightsList.forEach(u -> {
                String email = u.getEmail();
                String contexts = u.getUserName() + EmailConstants.ZN_REVIEW_CONTEXT;
                mailService.sendTextMail(email, EmailConstants.TITLE_ZN_REVIEW, contexts);
            });
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
        }
        //1.获取list
        if (CollectionUtils.isEmpty(list)) {
            Example example = new Example(OmsProductionOrder.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status", ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_DCSAP.getCode());
            list = omsProductionOrderMapper.selectByExample(example);
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
    @Override
    public R timeSAPGetProductOrderCode() {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPZ.getCode());
        List<OmsProductionOrder> list = omsProductionOrderMapper.selectByExample(example);
        //调用SAP获取生产订单号
        R resultSAP = orderFromSap601InterfaceService.queryProductOrderFromSap601(list);
        if (!resultSAP.isSuccess()) {
            log.error("调用SAP获取生产订单号接口异常res:{}", JSONObject.toJSONString(resultSAP));
            return resultSAP;
        }

        List<OmsProductionOrder> listSapRes = (List<OmsProductionOrder>) resultSAP.get("data");
        listSapRes.forEach(omsProductionOrder -> {
            omsProductionOrder.setStatus(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPZ.getCode());
        });
        //修改数据
        omsProductionOrderMapper.batchUpdateByOrderCode(listSapRes);
        return R.ok();
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
            } else {
                List<OmsProductionOrder> omsProductionOrderListNew = new ArrayList<>();
                omsProductionOrderListNew.add(omsProductionOrder);
                branchOfficeMap.put(branchOffice, omsProductionOrderListNew);
            }
            if (monitorMap.containsKey(monitor)) {
                List<OmsProductionOrder> omsProductionOrderListGet = monitorMap.get(monitor);
                omsProductionOrderListGet.add(omsProductionOrder);
                monitorMap.put(monitor, omsProductionOrderListGet);
            } else {
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

        //3.发送附件
        branchOfficeMap.keySet().forEach(branchOffice -> {
            List<OmsProductionOrder> productionOrderList = branchOfficeMap.get(branchOffice);
            CdFactoryLineInfo branchOfficeLineInfo = branchOfficeFactoryLineMap.get(branchOffice);
//            if(null == branchOfficeLineInfo){
//                log.error("请维护主管邮箱 branchOffice:{}",branchOffice);
//                throw new BusinessException("请维护主管邮箱");
//            }
//            String to = branchOfficeFactoryLineMap.get(branchOffice).getBranchOfficeEmail();
            String to = "1332549662@qq.com";
            sendMail(productionOrderList, to);
        });
        monitorMap.keySet().forEach(monitor -> {
            List<OmsProductionOrder> productionOrderList = monitorMap.get(monitor);
            CdFactoryLineInfo monitorLineInfo = monitorFactoryLineMap.get(monitor);
//            if(null == monitorLineInfo){
//                log.error("请维护班长邮箱 branchOffice:{}",monitor);
//                throw new BusinessException("请维护主管邮箱");
//            }
//            String to = monitorFactoryLineMap.get(monitor).getBranchOfficeEmail();
            String to = "1332549662@qq.com";
            sendMail(productionOrderList, to);
        });
        return R.ok();
    }


    /**
     * 发送邮件
     * @param productionOrderList
     * @param to
     */
    private void sendMail(List<OmsProductionOrder> productionOrderList, String to) {
        List<OmsProductionOrderMailVo> productionOrderMailVoList = productionOrderList.stream().map(omsProductionOrde ->
                BeanUtil.copyProperties(omsProductionOrde,OmsProductionOrderMailVo.class)).collect(Collectors.toList());
        log.info("发送邮件开始");
        String fileName = "排产订单已下达SAP信息";
        String subject = "排产订单已下达SAP信息";
        String content = "排产订单已下达SAP信息";
        String sheetName = "排产订单已下达SAP信息";
        List<List<String>> excelHeader = mailPushExcellHeader();
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            EasyExcel.write(out,OmsProductionOrderMailVo.class)
                    .head(excelHeader)
                    .sheet(fileName)
                    .doWrite(productionOrderMailVoList);
            ByteArrayInputStream iss = new ByteArrayInputStream(out.toByteArray());
            out.close();

            mailService.sendAttachmentsMail("1332549662@qq.com",subject,content,iss,fileName);
        } catch (Exception e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            log.info("发送邮件异常 error:{}",w.toString());
            throw new BusinessException("发送邮件异常");
        }finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        List<String> headTitle1 = new ArrayList<>();
        String date = DateUtils.getDate();
        headTitle0.add(date);
        headTitle0.add("分公司");
        headTitle0.add(date);
        headTitle1.add("班长");
        headTitle0.add(date);
        headTitle1.add("线号");
        headTitle0.add(date);
        headTitle1.add("订单批次号");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("成品专用号");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("成品描述");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("PCB专用号");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("排产订单数量");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("基本开始");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("顺序");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("事业部T-1交货");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("UPH");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("产品用时");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("产品定员");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("版本");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("版本");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("发往地");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("老品/新品");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("产品状态");
        headTitle0.add("智能电子生产部日生产定单计划");
        headTitle1.add("是否卡萨帝");

        headList.add(headTitle0);
        headList.add(headTitle1);
        return headList;
    }

    /**
     * 订单刷新
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
        listSapRes.forEach(omsProductionOrder -> {
            omsProductionOrder.setNewVersion(omsProductionOrder.getBomVersion());
            omsProductionOrder.setBomVersion("");
        });
        //修改数据
        omsProductionOrderMapper.batchUpdateByOrderCode(listSapRes);
        return R.ok();
    }
}
