package com.cloud.settle.controller;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SettleTestAct;
import com.cloud.settle.service.ISettleTestActService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 测试审批流 提供者
 *
 * @author cs
 * @date 2020-05-20
 */
@RestController
@RequestMapping("settleTest")
public class SettleTestActController extends BaseController {

    @Autowired
    private ISettleTestActService settleTestActService;

    /**
     * 查询测试审批流列表
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询${tableComment}", response = SettleTestAct.class)
    public SettleTestAct get(Long id) {
        return settleTestActService.selectByPrimaryKey(id);

    }

    /**
     * 查询测试审批流列表
     */
    @GetMapping("list")
    @ApiOperation(value = "${tableComment}查询分页", response = SettleTestAct.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SettleTestAct settleTestAct) {
        Example example = new Example(SettleTestAct.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<SettleTestAct> settleTestActList = settleTestActService.selectByExample(example);
        return getDataTable(settleTestActList);
    }


    /**
     * 新增保存测试审批流
     */
    @PostMapping("save")
    @OperLog(title = "新增保存测试审批流", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存测试审批流", response = SettleTestAct.class)
    public R addSave(@RequestBody SettleTestAct settleTestAct) {
        settleTestActService.insertSelective(settleTestAct);
        return R.data(settleTestAct.getId());
    }

    /**
     * 修改保存测试审批流
     */
    @PostMapping("update")
    @OperLog(title = "修改保存测试审批流", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存测试审批流", response = SettleTestAct.class)
    public R editSave(@RequestBody SettleTestAct settleTestAct) {
        return toAjax(settleTestActService.updateByPrimaryKeySelective(settleTestAct));
    }

    /**
     * 删除测试审批流
     */
    @PostMapping("remove")
    @OperLog(title = "删除测试审批流", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除测试审批流", response = SettleTestAct.class)
    public R remove(String ids) {
        return toAjax(settleTestActService.deleteByIds(ids));
    }

}
