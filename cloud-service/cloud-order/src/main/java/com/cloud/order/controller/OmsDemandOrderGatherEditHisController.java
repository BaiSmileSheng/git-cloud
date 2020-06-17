package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEditHis;
import com.cloud.order.service.IOmsDemandOrderGatherEditHisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 滚动计划需求操作历史  提供者
 *
 * @author cs
 * @date 2020-06-16
 */
@RestController
@RequestMapping("demandOrderGatherEditHis")
public class OmsDemandOrderGatherEditHisController extends BaseController {

    @Autowired
    private IOmsDemandOrderGatherEditHisService omsDemandOrderGatherEditHisService;

    /**
     * 查询滚动计划需求操作历史
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询滚动计划需求操作历史 ", response = OmsDemandOrderGatherEditHis.class)
    public OmsDemandOrderGatherEditHis get(Long id) {
        return omsDemandOrderGatherEditHisService.selectByPrimaryKey(id);

    }

    /**
     * 查询滚动计划需求操作历史 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "滚动计划需求操作历史 查询分页", response = OmsDemandOrderGatherEditHis.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsDemandOrderGatherEditHis omsDemandOrderGatherEditHis) {
        Example example = new Example(OmsDemandOrderGatherEditHis.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsDemandOrderGatherEditHis> omsDemandOrderGatherEditHisList = omsDemandOrderGatherEditHisService.selectByExample(example);
        return getDataTable(omsDemandOrderGatherEditHisList);
    }


    /**
     * 新增保存滚动计划需求操作历史
     */
    @PostMapping("save")
    @OperLog(title = "新增保存滚动计划需求操作历史 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存滚动计划需求操作历史 ", response = R.class)
    public R addSave(@RequestBody OmsDemandOrderGatherEditHis omsDemandOrderGatherEditHis) {
        omsDemandOrderGatherEditHisService.insertSelective(omsDemandOrderGatherEditHis);
        return R.data(omsDemandOrderGatherEditHis.getId());
    }

    /**
     * 修改保存滚动计划需求操作历史
     */
    @PostMapping("update")
    @OperLog(title = "修改保存滚动计划需求操作历史 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存滚动计划需求操作历史 ", response = R.class)
    public R editSave(@RequestBody OmsDemandOrderGatherEditHis omsDemandOrderGatherEditHis) {
        return toAjax(omsDemandOrderGatherEditHisService.updateByPrimaryKeySelective(omsDemandOrderGatherEditHis));
    }

    /**
     * 删除滚动计划需求操作历史
     */
    @PostMapping("remove")
    @OperLog(title = "删除滚动计划需求操作历史 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除滚动计划需求操作历史 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsDemandOrderGatherEditHisService.deleteByIds(ids));
    }

}
