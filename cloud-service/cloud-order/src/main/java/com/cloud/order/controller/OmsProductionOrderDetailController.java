package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.service.IOmsProductionOrderDetailService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
     * 查询排产订单明细 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "排产订单明细 查询分页", response = OmsProductionOrderDetail.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsProductionOrderDetail omsProductionOrderDetail) {
        Example example = new Example(OmsProductionOrderDetail.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsProductionOrderDetail> omsProductionOrderDetailList = omsProductionOrderDetailService.selectByExample(example);
        return getDataTable(omsProductionOrderDetailList);
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
