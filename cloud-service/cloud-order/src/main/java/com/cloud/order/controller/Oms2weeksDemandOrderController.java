package com.cloud.order.controller;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.Oms2weeksDemandOrder;
import com.cloud.order.service.IOms2weeksDemandOrderService;
import com.cloud.order.util.EasyExcelUtilOSS;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * T+1-T+2周需求  提供者
 *
 * @author cs
 * @date 2020-06-12
 */
@RestController
@RequestMapping("weeksDemandOrder")
@Api(tags = "T+1、T+2草稿计划-接入")
public class Oms2weeksDemandOrderController extends BaseController {

    @Autowired
    private IOms2weeksDemandOrderService oms2weeksDemandOrderService;

    /**
     * 查询T+1-T+2周需求
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询T+1-T+2周需求 ", response = Oms2weeksDemandOrder.class)
    public Oms2weeksDemandOrder get(Long id) {
        return oms2weeksDemandOrderService.selectByPrimaryKey(id);

    }

    /**
     * T+1、T+2草稿计划-接入分页
     */
    @GetMapping("list")
    @ApiOperation(value = "T+1、T+2草稿计划-接入分页", response = Oms2weeksDemandOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "交付开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "交付结束日期", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(Oms2weeksDemandOrder oms2weeksDemandOrder) {
        Example example = listCondition(oms2weeksDemandOrder);
        startPage();
        List<Oms2weeksDemandOrder> oms2weeksDemandOrderList = oms2weeksDemandOrderService.selectByExample(example);
        return getDataTable(oms2weeksDemandOrderList);
    }

    /**
     * Example查询时的条件
     * @param oms2weeksDemandOrder
     * @return
     */
    Example listCondition(Oms2weeksDemandOrder oms2weeksDemandOrder){
        Example example = new Example(Oms2weeksDemandOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(oms2weeksDemandOrder.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode",oms2weeksDemandOrder.getProductMaterialCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrder.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",oms2weeksDemandOrder.getProductFactoryCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrder.getCustomerCode())) {
            criteria.andEqualTo("customerCode",oms2weeksDemandOrder.getCustomerCode() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrder.getOrderFrom())) {
            criteria.andEqualTo("orderFrom",oms2weeksDemandOrder.getOrderFrom() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrder.getBeginTime())) {
            criteria.andGreaterThanOrEqualTo("deliveryDate",oms2weeksDemandOrder.getBeginTime() );
        }
        if (StrUtil.isNotEmpty(oms2weeksDemandOrder.getEndTime())) {
            criteria.andLessThanOrEqualTo("deliveryDate", oms2weeksDemandOrder.getEndTime() );
        }
        return example;
    }

    /**
     * 计划需求导入-导出
     */
    @GetMapping("export")
    @ApiOperation(value = "计划需求导入-导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productMaterialCode", value = "成品专用号", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "生产工厂", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "customerCode", value = "客户编码", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "交付开始日期", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "交付结束日期", required = false, paramType = "query", dataType = "String")
    })
    @HasPermissions("order:weeksDemandOrder:export")
    public R export(@ApiIgnore() Oms2weeksDemandOrder oms2weeksDemandOrder) {
        Example example = listCondition(oms2weeksDemandOrder);
        List<Oms2weeksDemandOrder> oms2weeksDemandOrders = oms2weeksDemandOrderService.selectByExample(example);
        return EasyExcelUtilOSS.writeExcel(oms2weeksDemandOrders, "T+1、T+2草稿计划-接入.xlsx", "sheet", new Oms2weeksDemandOrder());
    }

    /**
     * 新增保存T+1-T+2周需求
     */
    @PostMapping("save")
    @OperLog(title = "新增保存T+1-T+2周需求 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存T+1-T+2周需求 ", response = R.class)
    public R addSave(@RequestBody Oms2weeksDemandOrder oms2weeksDemandOrder) {
        oms2weeksDemandOrderService.insertSelective(oms2weeksDemandOrder);
        return R.data(oms2weeksDemandOrder.getId());
    }

    /**
     * 修改保存T+1-T+2周需求
     */
    @PostMapping("update")
    @OperLog(title = "修改保存T+1-T+2周需求 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存T+1-T+2周需求 ", response = R.class)
    public R editSave(@RequestBody Oms2weeksDemandOrder oms2weeksDemandOrder) {
        return toAjax(oms2weeksDemandOrderService.updateByPrimaryKeySelective(oms2weeksDemandOrder));
    }

    /**
     * 删除T+1-T+2周需求
     */
    @PostMapping("remove")
    @OperLog(title = "删除T+1-T+2周需求 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除T+1-T+2周需求 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(oms2weeksDemandOrderService.deleteByIds(ids));
    }

}
