package com.cloud.system.controller;

import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.system.domain.entity.CdProductWarehouse;
import com.cloud.system.service.ICdProductWarehouseService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 成品库存在库明细  提供者
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@RestController
@RequestMapping("productWarehouse")
public class CdProductWarehouseController extends BaseController {

    @Autowired
    private ICdProductWarehouseService cdProductWarehouseService;

    /**
     * 查询成品库存在库明细
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询成品库存在库明细 ", response = CdProductWarehouse.class)
    public CdProductWarehouse get(Long id) {
        return cdProductWarehouseService.selectByPrimaryKey(id);

    }

    /**
     * 查询成品库存在库明细 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "成品库存在库明细 查询分页", response = CdProductWarehouse.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdProductWarehouse cdProductWarehouse) {
        Example example = new Example(CdProductWarehouse.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<CdProductWarehouse> cdProductWarehouseList = cdProductWarehouseService.selectByExample(example);
        return getDataTable(cdProductWarehouseList);
    }


    /**
     * 新增保存成品库存在库明细
     */
    @PostMapping("save")
    @OperLog(title = "新增保存成品库存在库明细 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存成品库存在库明细 ", response = R.class)
    public R addSave(@RequestBody CdProductWarehouse cdProductWarehouse) {
        cdProductWarehouseService.insertSelective(cdProductWarehouse);
        return R.data(cdProductWarehouse.getId());
    }

    /**
     * 修改保存成品库存在库明细
     */
    @PostMapping("update")
    @OperLog(title = "修改保存成品库存在库明细 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存成品库存在库明细 ", response = R.class)
    public R editSave(@RequestBody CdProductWarehouse cdProductWarehouse) {
        return toAjax(cdProductWarehouseService.updateByPrimaryKeySelective(cdProductWarehouse));
    }

    /**
     * 删除成品库存在库明细
     */
    @PostMapping("remove")
    @OperLog(title = "删除成品库存在库明细 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除成品库存在库明细 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdProductWarehouseService.deleteByIds(ids));
    }

}
