package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.ProductOrderConstants;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.vo.OmsProductionOrderExportVo;
import com.cloud.order.domain.entity.vo.OmsProductionOrderImportVo;
import com.cloud.order.enums.ProductionOrderDelaysFlagEnum;
import com.cloud.order.enums.ProductionOrderStatusEnum;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.order.util.OmsProductOrderWriteHandler;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排产订单  提供者
 *
 * @author cs
 * @date 2020-05-29
 */
@RestController
@RequestMapping("productionOrder")
@Api(tags = "排产订单")
public class OmsProductionOrderController extends BaseController {

    @Autowired
    private IOmsProductionOrderService omsProductionOrderService;

    @Autowired
    private RemoteFactoryLineInfoService remoteFactoryLineInfoService;

    /**
     * 查询排产订单
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询排产订单 ", response = OmsProductionOrder.class)
    public OmsProductionOrder get(@RequestParam("id") Long id) {
        return omsProductionOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询排产订单 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "排产订单 查询分页", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productEndDate", value = "到", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产单号", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
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
            criteria.andEqualTo("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        }
        if (StrUtil.isNotEmpty(omsProductionOrder.getProductStartDate())) {
            criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getProductStartDate());
        }
        if (StrUtil.isNotEmpty(omsProductionOrder.getProductEndDate())) {
            criteria.andLessThanOrEqualTo("productEndDate", omsProductionOrder.getProductEndDate());
        }
        if (StrUtil.isNotEmpty(omsProductionOrder.getProductOrderCode())) {
            criteria.andEqualTo("productOrderCode", omsProductionOrder.getProductOrderCode());
        }

        SysUser sysUser = getUserInfo(SysUser.class);
        if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {

//            String lineCodes = r.get("data").toString();
//            criteria.andIn("productLineCode",CollectionUtil.toList(lineCodes.split(",")));
//            criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
//
            //查询订单状态已下达和已关单的两个状态的订单
            //如果加工承揽方式是半成品，供应商也可以看到订单传SAP之前的状态
            List<String> statusList = CollectionUtil.toList(
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
            Example.Criteria outSourceCriteria = example.createCriteria();
            outSourceCriteria.orNotEqualTo("outsourceType",ProductOrderConstants.OUTSOURCE_TYPE_HALF_PRODUCT);
            outSourceCriteria.andIn("status", statusList);

            List<String> outSourceStatusList = CollectionUtil.toList(
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_DPS.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_FKZ.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_DTZ.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YTZ.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_DCSAP.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_CSAPZ.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode(),
                    ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
            outSourceCriteria.orEqualTo("outsourceType",ProductOrderConstants.OUTSOURCE_TYPE_HALF_PRODUCT);
            outSourceCriteria.andIn("status", outSourceStatusList);
            example.and(outSourceCriteria);

            CdFactoryLineInfo cdFactoryLineInfo = new CdFactoryLineInfo().builder()
                    .supplierCode(sysUser.getSupplierCode()).build();
            //根据登录用户V码查询工厂及生产线
            R r = remoteFactoryLineInfoService.listByExample(cdFactoryLineInfo);
            if (!r.isSuccess()) {
                return getDataTable(CollectionUtil.newArrayList());
            }
            List<CdFactoryLineInfo> factoryLineInfos=r.getCollectData(new TypeReference<List<CdFactoryLineInfo>>() {});
            Example.Criteria cLine = example.createCriteria();
            factoryLineInfos.forEach(factoryLineInfo->{
                cLine.orEqualTo("productFactoryCode", factoryLineInfo.getProductFactoryCode());
                cLine.andEqualTo("productLineCode", factoryLineInfo.getProduceLineCode());
            });
            example.and(cLine);
        }else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //班长、分主管查询工厂下的数据
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_BZ)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_FZG)) {
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        }
        startPage();
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        return getDataTable(omsProductionOrderList);
    }

    /**
     * 查询延期标记排产订单 列表
     */
    @GetMapping("listForDelays")
    @ApiOperation(value = "查询延期关单的排产订单", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productEndDateEnd", value = "基本开始日期截止值", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "actualEndDateStart", value = "实际开始日期起始值", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "actualEndDateEnd", value = "实际开始日期结束值", required = true, paramType = "query", dataType = "String")
    })
    public R listForDelays(){
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //查询delaysFlag为3的数据
        //关单：判断实际结束日期与开始日期是否同月或是否大于8天
        //未关单：判断当前日期与开始日期是否同月或是否大于8天
        criteria.andEqualTo("delaysFlag", ProductionOrderDelaysFlagEnum.PRODUCTION_ORDER_DELAYS_FLAG_3.getCode());
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        List<OmsProductionOrder> listGD = omsProductionOrderList.stream().filter(o ->
                ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode().equals(o.getStatus()))
                .collect(Collectors.toList());
        List<OmsProductionOrder> listGDDelays=listGD.stream().filter(o ->
                        DateUtil.between(o.getActualEndDate(), DateUtil.parseDate(o.getProductStartDate()), DateUnit.DAY) > 7
                                || DateUtil.month(o.getActualEndDate()) > DateUtil.month(DateUtil.parseDate(o.getProductStartDate())))
                .collect(Collectors.toList());
        List<OmsProductionOrder> listWGD = omsProductionOrderList.stream().filter(o ->
                !ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode().equals(o.getStatus()) &&
                        (DateUtil.between(DateUtil.date(), DateUtil.parseDate(o.getProductStartDate()), DateUnit.DAY) > 7
                                || DateUtil.thisMonth() > DateUtil.month(DateUtil.parseDate(o.getProductStartDate()))))
                .collect(Collectors.toList());
        listGDDelays.addAll(listWGD);
        return R.data(listGDDelays);
    }

    /**
     * 把delaysFlag=3、已关单、实际结束日期与基本开始日期小于等于7的数据更改把delaysFlag为0
     * @return
     */
    @GetMapping("updateNoNeedDelays")
    @ApiOperation(value = "更新不需要延期索赔的生产订单flag", response = OmsProductionOrder.class)
    public R updateNoNeedDelays(){
        return toAjax(omsProductionOrderService.updateDelaysFlag());
    }

    /**
     * 根据生产订单号查询排产订单信息
     *
     * @param prodctOrderCode
     * @return OmsProductionOrder
     */
    @GetMapping("selectByProdctOrderCode")
    @ApiOperation(value = "根据生产订单号查询排产订单信息 ", response = OmsProductionOrder.class)
    public R selectByProdctOrderCode(String prodctOrderCode) {
        if (StrUtil.isBlank(prodctOrderCode)) {
            throw new BusinessException("参数：生产订单号为空！");
        }
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productOrderCode", prodctOrderCode);
        OmsProductionOrder productionOrder = omsProductionOrderService.findByExampleOne(example);
        if(null == productionOrder){
            return R.error("排产订单信息不存在,请检查数据");
        }
        return R.data(productionOrder);
    }

    /**
     * 根据生产订单号查询排产订单信息和供应商信息
     * @param prodctOrderCode
     * @return OmsProductionOrder
     */
    @GetMapping("selectProInfoAndSupplierInfoByProdctOrderCode")
    @ApiOperation(value = "根据生产订单号查询排产订单信息和供应商信息 ", response = OmsProductionOrder.class)
    public R selectProInfoAndSupplierInfoByProdctOrderCode(String prodctOrderCode) {
        if (StrUtil.isBlank(prodctOrderCode)) {
            throw new BusinessException("参数：生产订单号为空！");
        }
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productOrderCode",prodctOrderCode);
        OmsProductionOrder productionOrder = omsProductionOrderService.findByExampleOne(example);
        if(null == productionOrder){
            return R.error("排产订单信息不存在,请检查数据");
        }
        R rFactory = remoteFactoryLineInfoService.selectInfoByCodeLineCode(productionOrder.getProductLineCode(),
                                                            productionOrder.getProductFactoryCode());
        if (!rFactory.isSuccess()) {
            return rFactory;
        }
        CdFactoryLineInfo factoryLineInfo = rFactory.getData(CdFactoryLineInfo.class);
        Dict dict = new Dict().set("productionOrder", productionOrder)
                .set("supplierCode",factoryLineInfo.getSupplierCode())
                .set("supplierName",factoryLineInfo.getSupplierDesc());
        return R.data(dict);
    }

    /**
     * 新增保存排产订单
     */
    @PostMapping("save")
    @OperLog(title = "新增保存排产订单 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存排产订单 ", response = R.class)
    public R addSave(@RequestBody OmsProductionOrder omsProductionOrder) {
        omsProductionOrderService.insertSelective(omsProductionOrder);
        return R.data(omsProductionOrder.getId());
    }

    /**
     * 修改保存排产订单
     */
    @PostMapping("update")
    @OperLog(title = "修改保存排产订单 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存排产订单 ", response = R.class)
    public R editSave(@RequestBody OmsProductionOrder omsProductionOrder) {
        return toAjax(omsProductionOrderService.updateByPrimaryKeySelective(omsProductionOrder));
    }

    /**
     * 删除排产订单
     */
    @PostMapping("remove")
    @OperLog(title = "删除排产订单 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除排产订单 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductionOrderService.deleteByIds(ids));
    }

    /**
     * 查询排产订单导出 列表
     */
    @GetMapping("export")
    @ApiOperation(value = "排产订单 导出", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productEndDate", value = "到", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productionOrder:export")
    public R export(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
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
            criteria.andEqualTo("productMaterialCode", omsProductionOrder.getProductMaterialCode());
        }
        if (StrUtil.isNotEmpty(omsProductionOrder.getProductStartDate())) {
            criteria.andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getProductStartDate());
        }
        if (StrUtil.isNotEmpty(omsProductionOrder.getProductEndDate())) {
            criteria.andLessThanOrEqualTo("productStartDate", omsProductionOrder.getProductEndDate());
        }
        //查询订单状态已下达和已关单的两个状态的订单
        List<String> statusList = CollectionUtil.toList(ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YCSAP.getCode(),
                ProductionOrderStatusEnum.PRODUCTION_ORDER_STATUS_YGD.getCode());
        criteria.andIn("status", statusList);

        SysUser sysUser = getUserInfo(SysUser.class);
        if (UserConstants.USER_TYPE_WB.equals(sysUser.getUserType())) {
            R r = remoteFactoryLineInfoService.selectLineCodeBySupplierCode(sysUser.getSupplierCode());
            if (r.get("data") == null || StrUtil.isBlank(r.get("data").toString())) {
                return R.error("数据为空！");
            }
            String lineCodes = r.get("data").toString();
            criteria.andIn("productLineCode", CollectionUtil.toList(lineCodes.split(",")));
        } else if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //班长、分主管查询工厂下的数据
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_BZ)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_FZG)) {
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
            }
        }
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(omsProductionOrderList, "生产订单.xlsx", "sheet", new OmsProductionOrder());
    }

    /**
     * 查询排产订单 列表
     */
    @GetMapping("selectAllPage")
    @ApiOperation(value = "排产订单导入-分页查询", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderCode", value = "排产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "createBy", value = "创建人", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productionOrder:selectAllPage")
    public TableDataInfo selectAllPage(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectPageInfo(omsProductionOrder,sysUser);
        return getDataTable(omsProductionOrderList);
    }

    /**
     * 排产订单导入-导出模板
     * @return
     */
    @GetMapping("exportTemplate")
    @HasPermissions("order:productionOrder:exportTemplate")
    @ApiOperation(value = "排产订单导入-导出模板", response = OmsProductionOrderExportVo.class)
    @OperLog(title = "排产订单导入-导出模板 ", businessType = BusinessType.EXPORT)
    public R exportTemplate(){
        String fileName = "排产订单.xlsx";
        return EasyExcelUtilOSS.writePostilExcel(Arrays.asList(),fileName,fileName,new OmsProductionOrderImportVo(),new OmsProductOrderWriteHandler());
    }

    /**
     * 排产订单导入-导入
     *
     * @return
     */
    @PostMapping("importProductOrder")
    @HasPermissions("order:productOrder:importProductOrder")
    @ApiOperation(value = "排产订单导入-导入", response = OmsProductionOrder.class)
    @OperLog(title = "排产订单导入-导入 ", businessType = BusinessType.IMPORT)
    public R importProductOrder(@RequestParam("file") MultipartFile file) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderService.importProductOrder(file, sysUser);
    }

    /**
     * 查询排产订单导出 列表
     */
    @GetMapping("exportAll")
    @OperLog(title = "排产订单导入-导出 ", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "排产订单导入-导出功能", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productionOrder:exportAll")
    public R exportAll(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        List<OmsProductionOrder> productionOrderVos = omsProductionOrderService.exportAll(omsProductionOrder,sysUser);
        return EasyExcelUtilOSS.writeExcel(productionOrderVos, "排产订单.xlsx", "sheet", new OmsProductionOrder());
    }

    /**
     * 排产订单下达SAP-导出功能
     */
    @GetMapping("exportSAP")
    @ApiOperation(value = "排产订单下达SAP-导出功能", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productOrderCode", value = "生产工厂号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productionOrder:exportSAP")
    public R exportSAP(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderService.exportSAP(omsProductionOrder,sysUser);
    }

    /**
     * 删除排产订单
     */
    @PostMapping("delete")
    @OperLog(title = "排产订单删除 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "排产订单删除 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productOrderCode", value = "生产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderCode", value = "排产订单号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ids", value = "id字符串", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productionOrder:delete")
    public R delete(@ApiIgnore OmsProductionOrder omsProductionOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderService.deleteByIdString(omsProductionOrder,sysUser);
    }
    /**
     * 更新保存排产订单
     */
    @PostMapping("updateSave")
    @OperLog(title = "更新保存排产订单 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "更新保存排产订单 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "排产订单主键", required = true,  dataType = "Long"),
            @ApiImplicitParam(name = "productNum", value = "排产量", required = true, dataType = "BigDecimal")
    })
    @HasPermissions("order:productOrder:updateSave")
    public R updateSave(@ApiIgnore OmsProductionOrder omsProductionOrder){
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderService.updateSave(omsProductionOrder,sysUser);
    }
    /**
     * 排产订单导入-确认下达
     */
    @PostMapping("confirmRelease")
    @OperLog(title = "排产订单导入-确认下达 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "排产订单导入-确认下达 ", response = R.class)
    @HasPermissions("order:productionOrder:confirmRelease")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ids", value = "id字符串", required = false, paramType = "query", dataType = "String")
    })
    public R confirmRelease(@ApiIgnore OmsProductionOrder omsProductionOrder){
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderService.confirmRelease(omsProductionOrder,sysUser);
    }


    /**
     * 下达SAP
     */
    @HasPermissions("order:productionOrder:giveSAP")
    @PostMapping("giveSAP")
    @OperLog(title = "下达SAP ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "下达SAP ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "ids", value = "id字符串", required = false, paramType = "query", dataType = "String")
    })
    public R giveSAP(@ApiIgnore OmsProductionOrder omsProductionOrder){
        R result = omsProductionOrderService.giveSAP(omsProductionOrder);
        return result;
    }

    /**
     * 定时任务SAP获取订单号
     */
    @PostMapping("timeSAPGetProductOrderCode")
    @OperLog(title = "SAP获取订单号 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时任务SAP获取订单号 ", response = R.class)
    public R timeSAPGetProductOrderCode(){
        R result = omsProductionOrderService.timeSAPGetProductOrderCode();
        return result;
    }

    /**
     * 生成加工结算信息
     * @return
     */
    @PostMapping("timeInsertSettleList")
    @OperLog(title = "生成加工结算信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "生成加工结算信息 ", response = R.class)
    public R timeInsertSettleList(){
        return omsProductionOrderService.insertSettleList();
    }

    /**
     * 邮件推送
     */
    @HasPermissions("order:productionOrder:mailPush")
    @PostMapping("mailPush")
    @OperLog(title = "邮件推送 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "邮件推送 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderType", value = "sap订单类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "dateType", value = "查询日期类型", required = false, paramType = "query", dataType = "String"),
    })
    public R mailPush(@ApiIgnore OmsProductionOrder omsProductionOrder){
        R result = omsProductionOrderService.mailPush(omsProductionOrder);
        return result;
    }

    /**
     * 订单刷新
     */
    @HasPermissions("order:productionOrder:orderRefresh")
    @PostMapping("orderRefresh")
    @OperLog(title = "订单刷新", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "订单刷新 ", response = R.class)
    public R orderRefresh(@RequestParam(value = "ids") String ids){
        R result = omsProductionOrderService.orderRefresh(ids);
        return result;
    }

    /**
     * 定时获取入库量
     * @return
     */
    @PostMapping("timeGetConfirmAmont")
    @OperLog(title = "定时获取入库量", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "定时获取入库量 ", response = R.class)
    public R timeGetConfirmAmont(){
        R result = omsProductionOrderService.timeGetConfirmAmont();
        return result;
    }

    /**
     * 线下导入刷数据  关单
     *
     * @return
     */
    @PostMapping("importProductOrderTest")
    @ApiOperation(value = "线下导入刷数据  关单")
    public R importProductOrderTest(@RequestParam("file") MultipartFile file) {
        return omsProductionOrderService.importProductOrderTest(file);
    }

    /**
     * 排产订单下达SAP删除排产订单
     */
    @PostMapping("deleteSAP")
    @OperLog(title = "排产订单下达SAP删除排产订单", businessType = BusinessType.DELETE)
    @ApiOperation(value = "排产订单下达SAP删除排产订单 ", response = R.class)
    @HasPermissions("order:productionOrder:deleteSAP")
    public R deleteSAP(@RequestParam("id") String id) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderService.deleteSAP(id,sysUser);
    }

    /**
     * 根据主键批量修改保存排产订单
     */
    @PostMapping("updateBatchByPrimary")
    @OperLog(title = "根据主键批量修改保存排产订单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "根据主键批量修改保存排产订单", response = R.class)
    public R updateBatchByPrimary(@RequestBody List<OmsProductionOrder> omsProductionOrderList) {
        return toAjax(omsProductionOrderService.updateBatchByPrimaryKeySelective(omsProductionOrderList));
    }
    /**
     * Description: 查询初始化中状态的排产订单
     * Param: []
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/10/16
     */
    @PostMapping("selectByStatusAct")
    public R selectByStatusAct(){
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", ProductOrderConstants.STATUS_INIT);
        List<OmsProductionOrder> omsProductionOrders = omsProductionOrderService.selectByExample(example);
        if (ObjectUtil.isEmpty(omsProductionOrders) || omsProductionOrders.size() <= 0) {
            return R.ok();
        }
        return R.data(omsProductionOrders);
    }
    /**
     * Description:  定时任务校验排产订单审批流
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/10/19
     */
    @PostMapping("checkProductOrderAct")
    public R checkProductOrderAct(@RequestBody List<OmsProductionOrder> list){
        return omsProductionOrderService.checkProductOrderAct(list);
    }
}
