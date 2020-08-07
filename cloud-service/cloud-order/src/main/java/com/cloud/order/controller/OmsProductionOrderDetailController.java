package com.cloud.order.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.domain.entity.vo.OmsProductionOrderDetailExportVo;
import com.cloud.order.domain.entity.vo.OmsProductionOrderDetailVo;
import com.cloud.order.service.IOmsProductionOrderDetailService;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 排产订单明细  提供者
 *
 * @author ltq
 * @date 2020-06-19
 */
@RestController
@RequestMapping("productOrderDetail")
@Api(tags = "排产订单原材料明细")
public class OmsProductionOrderDetailController extends BaseController {

    @Autowired
    private IOmsProductionOrderDetailService omsProductionOrderDetailService;

    /**
     * 查询排产订单明细
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询排产订单明细 ", response = OmsProductionOrderDetail.class)
    public OmsProductionOrderDetail get(Long id) {
        return omsProductionOrderDetailService.selectByPrimaryKey(id);

    }

    /**
     * 原材料评审-列表分页查询
     */
    @GetMapping("list")
    @ApiOperation(value = "原材料评审-列表分页查询", response = OmsProductionOrderDetail.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "原材料物料", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "purchaseGroup", value = "采购组", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productOrderDetail:list")
    public TableDataInfo list(@ApiIgnore OmsProductionOrderDetail omsProductionOrderDetail) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        List<OmsProductionOrderDetailVo> list  =
                omsProductionOrderDetailService.listPageInfo(omsProductionOrderDetail,sysUser);
        return getDataTable(list);
    }

    /**
     * 原材料评审-导出
     */
    @GetMapping("export")
    @ApiOperation(value = "原材料评审-导出", response = OmsProductionOrderDetail.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "原材料物料", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "purchaseGroup", value = "采购组", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productOrderDetail:export")
    public R export(@ApiIgnore OmsProductionOrderDetail omsProductionOrderDetail) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderDetailService.exportList(omsProductionOrderDetail,sysUser);
    }
    /**
     * 原材料评审-反馈按钮，排产信息查询
     */
    @GetMapping("selectProductOrder")
    @ApiOperation(value = "原材料评审-反馈按钮，排产信息查询", response = OmsRawMaterialFeedback.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "原材料物料", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productOrderDetail:selectProductOrder")
    public R selectProductOrder(@ApiIgnore OmsProductionOrderDetail omsProductionOrderDetail){
        return omsProductionOrderDetailService.selectProductOrder(omsProductionOrderDetail);
    }

    /**
     * 原材料确认-列表分页查询
     */
    @GetMapping("commitListPageInfo")
    @ApiOperation(value = "原材料确认-列表分页查询", response = OmsProductionOrderDetail.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "materialCode", value = "原材料物料", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkStartDate", value = "查询开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkEndDate", value = "查询结束日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态，0：未确认，1：已确认，2：反馈中'", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:productOrderDetail:commitListPageInfo")
    public TableDataInfo commitListPageInfo(@ApiIgnore OmsProductionOrderDetail omsProductionOrderDetail) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        List<OmsProductionOrderDetail> list  =
                omsProductionOrderDetailService.commitListPageInfo(omsProductionOrderDetail,sysUser);
        return getDataTable(list);
    }
    /**
     * 原材料确认-确认按钮
     */
    @PostMapping("commitProductOrderDetail")
    @ApiOperation(value = "原材料确认-确认按钮", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderDetailList",value = "选中记录List",dataType = "List",required = false ),
            @ApiImplicitParam(name = "materialCode", value = "原材料物料号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "checkStartDate", value = "查询开始日期", required = false, dataType = "String"),
            @ApiImplicitParam(name = "checkEndDate", value = "查询结束日期", required = false, dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, dataType = "String")
    })
    @HasPermissions("order:productOrderDetail:commitProductOrderDetail")
    public R commitProductOrderDetail(@RequestBody OmsProductionOrderDetail omsProductionOrderDetail){
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsProductionOrderDetailService.commitProductOrderDetail(omsProductionOrderDetail.getOrderDetailList(),omsProductionOrderDetail,sysUser);
    }
    /**
     * 新增保存排产订单明细
     */
    @PostMapping("save")
    @OperLog(title = "新增保存排产订单明细 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存排产订单明细 ", response = R.class)
    public R addSave(@RequestBody OmsProductionOrderDetail omsProductionOrderDetail) {
        omsProductionOrderDetailService.insertSelective(omsProductionOrderDetail);
        return R.data(omsProductionOrderDetail.getId());
    }

    /**
     * 修改保存排产订单明细
     */
    @PostMapping("update")
    @OperLog(title = "修改保存排产订单明细 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存排产订单明细 ", response = R.class)
    public R editSave(@RequestBody OmsProductionOrderDetail omsProductionOrderDetail) {
        return toAjax(omsProductionOrderDetailService.updateByPrimaryKeySelective(omsProductionOrderDetail));
    }

    /**
     * 删除排产订单明细
     */
    @PostMapping("remove")
    @OperLog(title = "删除排产订单明细 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除排产订单明细 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductionOrderDetailService.deleteByIds(ids));
    }

}
