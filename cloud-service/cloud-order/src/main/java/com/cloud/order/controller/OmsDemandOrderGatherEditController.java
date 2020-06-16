package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.order.service.IOmsDemandOrderGatherEditService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 滚动计划需求操作  提供者
 *
 * @author cs
 * @date 2020-06-16
 */
@RestController
@RequestMapping("demandOrderGatherEdit")
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
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsDemandOrderGatherEdit omsDemandOrderGatherEdit) {
        Example example = new Example(OmsDemandOrderGatherEdit.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsDemandOrderGatherEdit> omsDemandOrderGatherEditList = omsDemandOrderGatherEditService.selectByExample(example);
        return getDataTable(omsDemandOrderGatherEditList);
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
        return toAjax(omsDemandOrderGatherEditService.updateByPrimaryKeySelective(omsDemandOrderGatherEdit));
    }

    /**
     * 删除滚动计划需求操作
     */
    @PostMapping("remove")
    @OperLog(title = "删除滚动计划需求操作 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除滚动计划需求操作 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsDemandOrderGatherEditService.deleteByIds(ids));
    }

}
