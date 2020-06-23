package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import com.cloud.order.service.IOms2weeksDemandOrderEditService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * T+1-T+2周需求导入  提供者
 *
 * @author cs
 * @date 2020-06-22
 */
@RestController
@RequestMapping("oms2weeksDemandOrderEdit")
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
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        Example example = new Example(Oms2weeksDemandOrderEdit.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<Oms2weeksDemandOrderEdit> oms2weeksDemandOrderEditList = oms2weeksDemandOrderEditService.selectByExample(example);
        return getDataTable(oms2weeksDemandOrderEditList);
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
    public R editSave(@RequestBody Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit) {
        return toAjax(oms2weeksDemandOrderEditService.updateByPrimaryKeySelective(oms2weeksDemandOrderEdit));
    }

    /**
     * 删除T+1-T+2周需求导入
     */
    @PostMapping("remove")
    @OperLog(title = "删除T+1-T+2周需求导入 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除T+1-T+2周需求导入 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(oms2weeksDemandOrderEditService.deleteByIds(ids));
    }

}
