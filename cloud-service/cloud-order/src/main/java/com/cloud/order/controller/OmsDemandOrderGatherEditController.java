package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsDemandOrderGather;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.order.service.IOmsDemandOrderGatherEditService;
import com.cloud.order.util.DataScopeUtil;
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
 * 滚动计划需求操作  提供者
 *
 * @author cs
 * @date 2020-06-16
 */
@RestController
@RequestMapping("demandOrderGatherEdit")
@Api(tags = "13周滚动需求-导入")
public class OmsDemandOrderGatherEditController extends BaseController {

    @Autowired
    private IOmsDemandOrderGatherEditService omsDemandOrderGatherEditService;



    /**
     * 查询滚动计划需求操作
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询滚动计划需求操作 ", response = OmsDemandOrderGatherEdit.class)
    public OmsDemandOrderGatherEdit get(Long id) {
        return omsDemandOrderGatherEditService.selectByPrimaryKey(id);

    }

    /**
     * 查询滚动计划需求操作 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "滚动计划需求操作 查询分页", response = OmsDemandOrderGatherEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
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
    public TableDataInfo list(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        Example example = listCondition(omsDemandOrderGatherEdit);
        SysUser sysUser = getUserInfo(SysUser.class);
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            example.and().andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
        }
        startPage();
        List<OmsDemandOrderGatherEdit> omsDemandOrderGatherEditList = omsDemandOrderGatherEditService.selectByExample(example);
        return getDataTable(omsDemandOrderGatherEditList);
    }

    /**
     * Example查询时的条件
     * @param omsDemandOrderGatherEdit
     * @return
     */
    Example listCondition(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit){
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode",omsDemandOrderGatherEdit.getProductMaterialCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",omsDemandOrderGatherEdit.getProductFactoryCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getCustomerCode())) {
            criteria.andEqualTo("customerCode",omsDemandOrderGatherEdit.getCustomerCode() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getOrderFrom())) {
            criteria.andEqualTo("orderFrom",omsDemandOrderGatherEdit.getOrderFrom() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getAuditStatus())) {
            criteria.andEqualTo("auditStatus",omsDemandOrderGatherEdit.getAuditStatus() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getStatus())) {
            criteria.andEqualTo("status",omsDemandOrderGatherEdit.getStatus() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getProductType())) {
            criteria.andEqualTo("productType",omsDemandOrderGatherEdit.getProductType() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getLifeCycle())) {
            criteria.andEqualTo("lifeCycle",omsDemandOrderGatherEdit.getLifeCycle() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("deliveryDate",omsDemandOrderGatherEdit.getBeginTime() );
        }
        if (StrUtil.isNotEmpty(omsDemandOrderGatherEdit.getEndTime())) {
            criteria.andLessThanOrEqualTo("deliveryDate",omsDemandOrderGatherEdit.getEndTime() );
        }
        return example;
    }

    /**
     * 新增保存滚动计划需求操作
     */
    @PostMapping("save")
    @OperLog(title = "新增保存滚动计划需求操作 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存滚动计划需求操作 ", response = R.class)
    public R addSave(@RequestBody OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        omsDemandOrderGatherEditService.insertSelective(omsDemandOrderGatherEdit);
        return R.data(omsDemandOrderGatherEdit.getId());
    }

    /**
     * 修改保存滚动计划需求操作
     */
    @PostMapping("update")
    @OperLog(title = "修改保存滚动计划需求操作 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存滚动计划需求操作 ", response = R.class)
    public R editSave(@RequestBody OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        return omsDemandOrderGatherEditService.updateWithLimit(omsDemandOrderGatherEdit);
    }

    /**
     * 删除滚动计划需求操作
     */
    @PostMapping("remove")
    @OperLog(title = "删除滚动计划需求操作 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除滚动计划需求操作 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return omsDemandOrderGatherEditService.deleteWithLimit(ids);
    }

    /**
     * 确认下达
     * @param ids
     * @return
     */
    @PostMapping("confirmRelease")
    @ApiOperation(value = "确认下达 ", response = R.class)
    @ApiParam(name = "ids", value = "需确认下达数据的id")
    public R confirmRelease(String ids){
        return omsDemandOrderGatherEditService.confirmRelease(ids);
    }

    /**
     * 需求数据导入
     * @param file
     * @return
     */
    @PostMapping("importExcel")
    @ApiOperation(value = "滚动计划需求操作导入 ", response = R.class)
    public R importExcel(MultipartFile file) {
        return omsDemandOrderGatherEditService.importDemandGatherEdit(file,getUserInfo(SysUser.class));
    }

    /**
     * 查询滚动计划需求 列表
     */
    @GetMapping("export")
    @ApiOperation(value = "计划需求导入-导出", response = OmsDemandOrderGather.class)
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
    public R export(@ApiIgnore() OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        Example example = listCondition(omsDemandOrderGatherEdit);
        SysUser sysUser = getUserInfo(SysUser.class);
        if(CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)){
            example.and().andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
        }
        startPage();
        List<OmsDemandOrderGatherEdit> omsDemandOrderGatherEditList = omsDemandOrderGatherEditService.selectByExample(example);
        return EasyExcelUtil.writeExcel(omsDemandOrderGatherEditList, "需求导入.xlsx", "sheet", new OmsDemandOrderGatherEdit());
    }

    /**
     * 查询滚动计划需求操作 列表
     */
    @GetMapping("week13DemandGatherList")
    @ApiOperation(value = "13周滚动需求汇总分页查询", response = OmsDemandOrderGatherEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false, paramType = "query", dataType = "String"),

    })
    public TableDataInfo week13DemandGatherList(@ApiIgnore OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        R r = omsDemandOrderGatherEditService.selectDistinctMaterialCodeAndFactoryCode(omsDemandOrderGatherEdit,sysUser);
        if (r.isSuccess()) {
            List<OmsDemandOrderGatherEdit> omsDemandOrderGatherEditList=r.getCollectData(new TypeReference<List<OmsDemandOrderGatherEdit>>() {});
            R rReturn = omsDemandOrderGatherEditService.week13DemandGatherList(omsDemandOrderGatherEditList);
            if (rReturn.isSuccess()) {
                List<OmsDemandOrderGatherEdit> listReturn=rReturn.getCollectData(new TypeReference<List<OmsDemandOrderGatherEdit>>() {});
                return getDataTable(listReturn);
            }
        }
        return getDataTable(new ArrayList<>());
    }

    /**
     * 13周滚动需求汇总 导出
     */
    @GetMapping("week13DemandGatherExport")
    @ApiOperation(value = "13周滚动需求汇总 导出", response = OmsDemandOrderGatherEdit.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderFrom", value = "订单来源", required = false, paramType = "query", dataType = "String"),
    })
    public R week13DemandGatherExport(@ApiIgnore() OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        return omsDemandOrderGatherEditService.week13DemandGatherExport(omsDemandOrderGatherEdit,getUserInfo(SysUser.class));
    }
}
