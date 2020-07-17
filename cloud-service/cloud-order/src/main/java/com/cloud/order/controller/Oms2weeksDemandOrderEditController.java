package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import com.cloud.order.domain.entity.vo.OmsDemandOrderGatherEditImportTemplete;
import com.cloud.order.service.IOms2weeksDemandOrderEditService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * T+1-T+2周需求导入  提供者
 *
 * @author cs
 * @date 2020-06-22
 */
@RestController
@RequestMapping("oms2weeksDemandOrderEdit")
@Api(tags = "2周需求")
public class Oms2weeksDemandOrderEditController extends BaseController {

    @Autowired
    private IOms2weeksDemandOrderEditService oms2weeksDemandOrderEditService;

    /**
     * 查询T+1-T+2周需求导入
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询T+1-T+2周需求导入 ", response = Oms2weeksDemandOrderEdit.class)
    public R get(Long id) {
        return R.data(oms2weeksDemandOrderEditService.selectByPrimaryKey(id));

    }

    /**
     * 查询T+1-T+2周需求导入 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "T+1-T+2周需求导入 查询分页", response = Oms2weeksDemandOrderEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productType", value = "产品类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lifeCycle", value = "生命周期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "交付开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "交付结束日期", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        Example example = listCondition(oms2weeksDemandOrderEdit);
        SysUser sysUser = getUserInfo(SysUser.class);
        if(!sysUser.isAdmin()&&CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            example.and().andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
        }
        startPage();
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList = oms2weeksDemandOrderEditService.selectByExample(example);
        return getDataTable(oms2weeksDemandOrderEditList);
    }

    /**
     * Example查询时的条件
     * @param oms2weeksDemandOrderEdit
     * @return
     */
    Example listCondition(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit){
        Example example = new Example(Oms2weeksDemandOrderEdit.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode",oms2weeksDemandOrderEdit.getProductMaterialCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",oms2weeksDemandOrderEdit.getProductFactoryCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getCustomerCode())) {
            criteria.andEqualTo("customerCode",oms2weeksDemandOrderEdit.getCustomerCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getOrderFrom())) {
            criteria.andEqualTo("orderFrom",oms2weeksDemandOrderEdit.getOrderFrom() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getAuditStatus())) {
            criteria.andEqualTo("auditStatus",oms2weeksDemandOrderEdit.getAuditStatus() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getStatus())) {
            criteria.andEqualTo("status",oms2weeksDemandOrderEdit.getStatus() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getProductType())) {
            criteria.andEqualTo("productType",oms2weeksDemandOrderEdit.getProductType() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getLifeCycle())) {
            criteria.andEqualTo("lifeCycle",oms2weeksDemandOrderEdit.getLifeCycle() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("deliveryDate",oms2weeksDemandOrderEdit.getBeginTime() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrderEdit.getEndTime())) {
            criteria.andLessThanOrEqualTo("deliveryDate", oms2weeksDemandOrderEdit.getEndTime() );
        }
        return example;
    }

    /**
     * T+1、T+2草稿计划导入
     * @param file
     * @return
     */
    @PostMapping("importExcel")
    @ApiOperation(value = "T+1、T+2草稿计划导入 ", response = R.class)
    @HasPermissions("order:oms2weeksDemandOrderEdit:importExcel")
    public R importExcel(MultipartFile file) {
        return oms2weeksDemandOrderEditService.import2weeksDemandEdit(file,getUserInfo(SysUser.class));
    }

    /**
     * T+1、T+2草稿计划导入模板
     */
    @GetMapping("importTemplete")
    @ApiOperation(value = "T+1、T+2草稿计划导入模板")
    @HasPermissions("order:oms2weeksDemandOrderEdit:importTemplete")
    public R importTemplete() {
        return EasyExcelUtilOSS.writeExcel(new ArrayList<>(), "T+1-T+2周需求导入.xlsx", "sheet", new OmsDemandOrderGatherEditImportTemplete());
    }

    /**
     * 新增保存T+1-T+2周需求导入
     */
    @PostMapping("save")
    @OperLog(title = "新增保存T+1-T+2周需求导入 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存T+1-T+2周需求导入 ", response = R.class)
    public R addSave(@RequestBody Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        oms2weeksDemandOrderEditService.insertSelective(oms2weeksDemandOrderEdit);
        return R.data(oms2weeksDemandOrderEdit.getId());
    }

    /**
     * 修改保存T+1-T+2周需求导入
     */
    @PostMapping("update")
    @OperLog(title = "修改保存T+1-T+2周需求导入 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存T+1-T+2周需求导入 ", response = R.class)
    @HasPermissions("order:oms2weeksDemandOrderEdit:editSave")
    public R editSave(@RequestBody Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        return oms2weeksDemandOrderEditService.updateWithLimit(oms2weeksDemandOrderEdit);
    }

    /**
     * 删除T+1-T+2周需求导入
     */
    @PostMapping("remove")
    @OperLog(title = "删除T+1-T+2周需求导入 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除T+1-T+2周需求导入 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    @HasPermissions("order:oms2weeksDemandOrderEdit:remove")
    public R remove(@RequestBody String ids) {
        return oms2weeksDemandOrderEditService.deleteWithLimit(ids);
    }

    /**
     * 删除T+1-T+2周需求导入（已下达SAP）
     */
    @PostMapping("removeWithXDSAP")
    @OperLog(title = "删除T+1-T+2周需求导入已下达SAP） ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除T+1-T+2周需求导入已下达SAP） ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    @HasPermissions("order:oms2weeksDemandOrderEdit:removeWithXDSAP")
    public R removeWithXDSAP(@RequestBody String ids) {
        return oms2weeksDemandOrderEditService.deleteWithLimit(ids);
    }


    /**
     * 计划需求导入-导出
     */
    @GetMapping("export")
    @ApiOperation(value = "计划需求导入-导出", response = Oms2weeksDemandOrderEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "auditStatus", value = "审核状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productType", value = "产品类型", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "lifeCycle", value = "生命周期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "交付开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "交付结束日期", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:oms2weeksDemandOrderEdit:export")
    public R export(@ApiIgnore() Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        Example example = listCondition(oms2weeksDemandOrderEdit);
        SysUser sysUser = getUserInfo(SysUser.class);
        if(!sysUser.isAdmin()&&CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            example.and().andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
        }
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList = oms2weeksDemandOrderEditService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(oms2weeksDemandOrderEditList, "T+1-T+2周需求导入.xlsx", "sheet", new Oms2weeksDemandOrderEdit());
    }

    /**
     * 确认下达
     * @param ids
     * @return
     */
    @PostMapping("confirmRelease")
    @ApiOperation(value = "确认下达 ", response = R.class)
    @ApiParam(name = "ids", value = "需确认下达数据的id")
    @HasPermissions("order:oms2weeksDemandOrderEdit:confirmRelease")
    public R confirmRelease(String ids){
        return oms2weeksDemandOrderEditService.confirmRelease(ids);
    }


    /**
     * T+1、T+2草稿计划对比分析分页查询
     */
    @GetMapping("t1t2GatherList")
    @ApiOperation(value = "T+1、T+2草稿计划对比分析分页查询", response = Oms2weeksDemandOrderEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
    })
    public TableDataInfo t1t2GatherList(@ApiIgnore Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        //先分页查询去重的物料号和工厂
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList = oms2weeksDemandOrderEditService.selectDistinctMaterialCodeAndFactoryCode(oms2weeksDemandOrderEdit,sysUser);
        if (CollectionUtil.isNotEmpty(oms2weeksDemandOrderEditList)) {
            TableDataInfo info=getDataTable(oms2weeksDemandOrderEditList);
            //根据前面分页查询的物料号和工厂查询出相关信息并组织数据结构
            R rReturn = oms2weeksDemandOrderEditService.t1t2GatherList(oms2weeksDemandOrderEditList);
            if (rReturn.isSuccess()) {
                List<Oms2weeksDemandOrderEdit> listReturn=rReturn.getCollectData(new TypeReference<List<Oms2weeksDemandOrderEdit>>() {});
                info.setRows(listReturn);
                return info;
            }
        }
        return getDataTable(new ArrayList<>());
    }

    /**
     * T+1、T+2草稿计划对比分析 导出
     */
    @GetMapping("t1t2GatherListExport")
    @ApiOperation(value = "T+1、T+2草稿计划对比分析导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
    })
    @HasPermissions("order:oms2weeksDemandOrderEdit:t1t2GatherListExport")
    public R t1t2GatherListExport(@ApiIgnore Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        return oms2weeksDemandOrderEditService.t1t2GatherListExport(oms2weeksDemandOrderEdit,getUserInfo(SysUser.class));
    }

    /**
     *T+1、T+2草稿计划对比分析下达SAP分页
     */
    @GetMapping("toSAPlist")
    @ApiOperation(value = "T+1、T+2草稿计划对比分析下达SAP分页", response = Oms2weeksDemandOrderEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "交付开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "交付结束日期", required = false, paramType = "query", dataType = "String")

    })
    public TableDataInfo toSAPlist(@ApiIgnore Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        Example example = listCondition(oms2weeksDemandOrderEdit);
        startPage();
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList = oms2weeksDemandOrderEditService.selectByExample(example);
        return getDataTable(oms2weeksDemandOrderEditList);
    }

    /**
     * 下达SAP 2周需求传SAP
     * @param ids
     * @return
     */
    @PostMapping("toSAP")
    @ApiOperation(value = "下达SAP")
    @HasPermissions("order:oms2weeksDemandOrderEdit:toSAP")
    public R toSAP(@RequestParam("ids") List<Long> ids){
        SysUser sysUser = getUserInfo(SysUser.class);
        return oms2weeksDemandOrderEditService.toSAP(ids,sysUser);
    }


    /**
     * SAP601创建订单接口定时任务（ZPP_INT_DDPS_02）
     * @return
     */
    @PostMapping("queryPlanOrderCodeFromSap601")
    @ApiOperation(value = "定时任务获取计划订单号")
    public R queryPlanOrderCodeFromSap601(){
        return oms2weeksDemandOrderEditService.queryPlanOrderCodeFromSap601();
    }

    /**
     * 修改保存T+1-T+2周需求导入(无业务)
     */
    @PostMapping("updateOrderEdit")
    @OperLog(title = "修改保存T+1-T+2周需求导入 ", businessType = BusinessType.UPDATE)
    public R updateOrderEdit(@RequestBody Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        return toAjax(oms2weeksDemandOrderEditService.updateByPrimaryKeySelective(oms2weeksDemandOrderEdit));
    }
}
