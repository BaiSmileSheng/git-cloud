package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsDemandOrderGather;
import com.cloud.order.service.IOmsDemandOrderGatherService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 滚动计划需求  提供者
 *
 * @author cs
 * @date 2020-06-12
 */
@RestController
@RequestMapping("demandOrderGather")
@Api(tags = "滚动计划需求")
public class OmsDemandOrderGatherController extends BaseController {

    @Autowired
    private IOmsDemandOrderGatherService omsDemandOrderGatherService;

    /**
     * 查询滚动计划需求
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询滚动计划需求 ", response = OmsDemandOrderGather.class)
    public OmsDemandOrderGather get(Long id) {
        return omsDemandOrderGatherService.selectByPrimaryKey(id);

    }

    /**
     * 查询滚动计划需求 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "滚动计划需求 查询分页", response = OmsDemandOrderGather.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsDemandOrderGather omsDemandOrderGather) {
        Example example = new Example(OmsDemandOrderGather.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsDemandOrderGather> omsDemandOrderGatherList = omsDemandOrderGatherService.selectByExample(example);
        return getDataTable(omsDemandOrderGatherList);
    }


    /**
     * 新增保存滚动计划需求
     */
    @PostMapping("save")
    @OperLog(title = "新增保存滚动计划需求 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存滚动计划需求 ", response = R.class)
    public R addSave(@RequestBody OmsDemandOrderGather omsDemandOrderGather) {
        omsDemandOrderGatherService.insertSelective(omsDemandOrderGather);
        return R.data(omsDemandOrderGather.getId());
    }

    /**
     * 修改保存滚动计划需求
     */
    @PostMapping("update")
    @OperLog(title = "修改保存滚动计划需求 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存滚动计划需求 ", response = R.class)
    public R editSave(@RequestBody OmsDemandOrderGather omsDemandOrderGather) {
        return toAjax(omsDemandOrderGatherService.updateByPrimaryKeySelective(omsDemandOrderGather));
    }

    /**
     * 删除滚动计划需求
     */
    @PostMapping("remove")
    @OperLog(title = "删除滚动计划需求 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除滚动计划需求 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsDemandOrderGatherService.deleteByIds(ids));
    }

    /**
     * 周五需求数据汇总
     * @return
     */
    @GetMapping("gatherDemandOrderFriday")
    @ApiOperation(value = "周五需求数据汇总 ", response = R.class)
    public R gatherDemandOrderFriday(){
        return omsDemandOrderGatherService.gatherDemandOrderFriday();
    }

    /**
     * 周一需求数据汇总
     * @return
     */
    @GetMapping("gatherDemandOrderMonday")
    @ApiOperation(value = "周一需求数据汇总 ", response = R.class)
    public R gatherDemandOrderMonday(){
        return omsDemandOrderGatherService.gatherDemandOrderMonday();
    }
}
