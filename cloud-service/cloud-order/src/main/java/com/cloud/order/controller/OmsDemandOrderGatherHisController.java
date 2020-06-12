package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsDemandOrderGatherHis;
import com.cloud.order.service.IOmsDemandOrderGatherHisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 滚动计划需求历史  提供者
 *
 * @author cs
 * @date 2020-06-12
 */
@RestController
@RequestMapping("demandOrderGatherHis")
public class OmsDemandOrderGatherHisController extends BaseController {

    @Autowired
    private IOmsDemandOrderGatherHisService omsDemandOrderGatherHisService;

    /**
     * 查询滚动计划需求历史
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询滚动计划需求历史 ", response = OmsDemandOrderGatherHis.class)
    public OmsDemandOrderGatherHis get(Long id) {
        return omsDemandOrderGatherHisService.selectByPrimaryKey(id);

    }

    /**
     * 查询滚动计划需求历史 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "滚动计划需求历史 查询分页", response = OmsDemandOrderGatherHis.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsDemandOrderGatherHis omsDemandOrderGatherHis) {
        Example example = new Example(OmsDemandOrderGatherHis.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsDemandOrderGatherHis> omsDemandOrderGatherHisList = omsDemandOrderGatherHisService.selectByExample(example);
        return getDataTable(omsDemandOrderGatherHisList);
    }


    /**
     * 新增保存滚动计划需求历史
     */
    @PostMapping("save")
    @OperLog(title = "新增保存滚动计划需求历史 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存滚动计划需求历史 ", response = R.class)
    public R addSave(@RequestBody OmsDemandOrderGatherHis omsDemandOrderGatherHis) {
        omsDemandOrderGatherHisService.insertSelective(omsDemandOrderGatherHis);
        return R.data(omsDemandOrderGatherHis.getId());
    }

    /**
     * 修改保存滚动计划需求历史
     */
    @PostMapping("update")
    @OperLog(title = "修改保存滚动计划需求历史 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存滚动计划需求历史 ", response = R.class)
    public R editSave(@RequestBody OmsDemandOrderGatherHis omsDemandOrderGatherHis) {
        return toAjax(omsDemandOrderGatherHisService.updateByPrimaryKeySelective(omsDemandOrderGatherHis));
    }

    /**
     * 删除滚动计划需求历史
     */
    @PostMapping("remove")
    @OperLog(title = "删除滚动计划需求历史 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除滚动计划需求历史 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsDemandOrderGatherHisService.deleteByIds(ids));
    }

}
