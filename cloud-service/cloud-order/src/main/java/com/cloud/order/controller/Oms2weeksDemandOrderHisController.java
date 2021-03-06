package com.cloud.order.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderHis;
import com.cloud.order.service.IOms2weeksDemandOrderHisService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * T+1-T+2周需求历史  提供者
 *
 * @author cs
 * @date 2020-06-12
 */
@RestController
@RequestMapping("weeksDemandOrderHis")
public class Oms2weeksDemandOrderHisController extends BaseController {

    @Autowired
    private IOms2weeksDemandOrderHisService oms2weeksDemandOrderHisService;

    /**
     * 查询T+1-T+2周需求历史
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询T+1-T+2周需求历史 ", response = Oms2weeksDemandOrderHis.class)
    public Oms2weeksDemandOrderHis get(Long id) {
        return oms2weeksDemandOrderHisService.selectByPrimaryKey(id);

    }

    /**
     * 查询T+1-T+2周需求历史 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "T+1-T+2周需求历史 查询分页", response = Oms2weeksDemandOrderHis.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(Oms2weeksDemandOrderHis oms2weeksDemandOrderHis) {
        Example example = new Example(Oms2weeksDemandOrderHis.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<Oms2weeksDemandOrderHis> oms2weeksDemandOrderHisList = oms2weeksDemandOrderHisService.selectByExample(example);
        return getDataTable(oms2weeksDemandOrderHisList);
    }


    /**
     * 新增保存T+1-T+2周需求历史
     */
    @PostMapping("save")
    @OperLog(title = "新增保存T+1-T+2周需求历史 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存T+1-T+2周需求历史 ", response = R.class)
    public R addSave(@RequestBody Oms2weeksDemandOrderHis oms2weeksDemandOrderHis) {
        oms2weeksDemandOrderHisService.insertSelective(oms2weeksDemandOrderHis);
        return R.data(oms2weeksDemandOrderHis.getId());
    }

    /**
     * 修改保存T+1-T+2周需求历史
     */
    @PostMapping("update")
    @OperLog(title = "修改保存T+1-T+2周需求历史 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存T+1-T+2周需求历史 ", response = R.class)
    public R editSave(@RequestBody Oms2weeksDemandOrderHis oms2weeksDemandOrderHis) {
        return toAjax(oms2weeksDemandOrderHisService.updateByPrimaryKeySelective(oms2weeksDemandOrderHis));
    }

    /**
     * 删除T+1-T+2周需求历史
     */
    @PostMapping("remove")
    @OperLog(title = "删除T+1-T+2周需求历史 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除T+1-T+2周需求历史 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(oms2weeksDemandOrderHisService.deleteByIds(ids));
    }

}
