package com.cloud.order.controller;
import ch.qos.logback.classic.pattern.SyslogStartConverter;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.vo.OmsRawMaterialFeedbackVo;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.service.IOmsRawMaterialFeedbackService;
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 原材料反馈信息  提供者
 *
 * @author ltq
 * @date 2020-06-22
 */
@RestController
@RequestMapping("feedback")
@Api(tags = "原材料反馈信息")
public class OmsRawMaterialFeedbackController extends BaseController {

    @Autowired
    private IOmsRawMaterialFeedbackService omsRawMaterialFeedbackService;
    @Autowired
    private IOmsProductionOrderService omsProductionOrderService;

    /**
     * 查询原材料反馈信息 
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询原材料反馈信息 ", response = OmsRawMaterialFeedback.class)
    public OmsRawMaterialFeedback get(Long id) {
        return omsRawMaterialFeedbackService.selectByPrimaryKey(id);

    }

    /**
     * 反馈信息处理（JIT反馈信息）-分页查询
     */
    @GetMapping("list")
    @ApiOperation(value = "反馈信息处理（JIT反馈信息）-分页查询", response = OmsRawMaterialFeedback.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "rawMaterialCode", value = "原材料物料号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateStart", value = "查询开始日期", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "checkDateEnd", value = "查询结束日期", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore OmsRawMaterialFeedback omsRawMaterialFeedback) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks = omsRawMaterialFeedbackService.listPage(omsRawMaterialFeedback,sysUser);
        return getDataTable(omsRawMaterialFeedbacks);
    }
    /**
     * 反馈信息处理-快捷修改查询
     */
    @GetMapping("queryProductOrder")
    @ApiOperation(value = "反馈信息处理-快捷修改查询", response = OmsProductionOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "bomVersion", value = "bom版本", required = true,paramType = "query", dataType = "String")
    })
    @HasPermissions("order:feedback:queryProductOrder")
    public TableDataInfo queryProductOrder(@ApiIgnore OmsProductionOrder omsProductionOrder) {
        SysUser sysUser = getUserInfo(SysUser.class);
        startPage();
        List<OmsProductionOrder> omsProductionOrders = omsProductionOrderService.queryProductOrder(omsProductionOrder,sysUser);
        return getDataTable(omsProductionOrders);
    }

    /**
     * 反馈信息处理-通过/驳回
     */
    @PostMapping("approval")
    @OperLog(title = "反馈信息处理-通过/驳回 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "反馈信息处理-通过/驳回 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "反馈信息主键字符串", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "approvalFlag", value = "通过/驳回标识", required = true,paramType = "query", dataType = "String")
    })
    @HasPermissions("order:feedback:approval")
    public R approval(@ApiIgnore OmsRawMaterialFeedback omsRawMaterialFeedback) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsRawMaterialFeedbackService.approval(omsRawMaterialFeedback,sysUser);
    }
    /**
     * 反馈信息处理-快捷修改-确认
     */
    @PostMapping("updateProductOrder")
    @OperLog(title = "反馈信息处理-快捷修改-确认 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "反馈信息处理-快捷修改-确认 ", response = R.class)
    @HasPermissions("order:feedback:updateProductOrder")
    public R updateProductOrder(@RequestBody List<OmsProductionOrder> list) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsRawMaterialFeedbackService.updateProductOrder(list,sysUser);
    }
    /**
     * 原材料评审-新增反馈
     */
    @PostMapping("save")
    @OperLog(title = "原材料评审-新增反馈  ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "原材料评审-新增反馈  ", response = R.class)
    @HasPermissions("order:feedback:save")
    public R addSave(@RequestBody List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsRawMaterialFeedbackService.insertFeedback(omsRawMaterialFeedbacks,sysUser);
    }

    /**
     * 修改保存原材料反馈信息 
     */
    @PostMapping("update")
    @OperLog(title = "修改保存原材料反馈信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存原材料反馈信息 ", response = R.class)
    public R editSave(@RequestBody OmsRawMaterialFeedback omsRawMaterialFeedback) {
        return toAjax(omsRawMaterialFeedbackService.updateByPrimaryKeySelective(omsRawMaterialFeedback));
    }

    /**
     * JIT反馈信息-删除
     */
    @PostMapping("remove")
    @OperLog(title = "JIT反馈信息-删除 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "JIT反馈信息-删除 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    @HasPermissions("order:feedback:remove")
    public R remove(@RequestParam(value = "ids",required = false) String ids,@RequestBody OmsRawMaterialFeedback omsRawMaterialFeedback) {
        SysUser sysUser = getUserInfo(SysUser.class);
        return omsRawMaterialFeedbackService.deleteByIds(ids,omsRawMaterialFeedback,sysUser);
    }

}
