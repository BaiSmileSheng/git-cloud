package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.service.IOmsProductionOrderService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

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

    /**
     * 查询排产订单
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询排产订单 ", response = OmsProductionOrder.class)
    public OmsProductionOrder get(Long id) {
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
            @ApiImplicitParam(name = "factoryDesc", value = "工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productLineCode", value = "线体", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productStartDate", value = "基本开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productEndDate", value = "到", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore() OmsProductionOrder omsProductionOrder) {
        Example example = new Example(OmsProductionOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("factoryDesc",omsProductionOrder.getFactoryCode())
                .andEqualTo("productLineCode",omsProductionOrder.getProductLineCode())
                .andEqualTo("status",omsProductionOrder.getStatus())
                .andLike("productMaterialCode", omsProductionOrder.getProductMaterialCode())
                .andGreaterThanOrEqualTo("productStartDate", omsProductionOrder.getProductStartDate())
                .andLessThanOrEqualTo("productEndDate", omsProductionOrder.getProductEndDate());
        startPage();
        List<OmsProductionOrder> omsProductionOrderList = omsProductionOrderService.selectByExample(example);
        return getDataTable(omsProductionOrderList);
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

}
