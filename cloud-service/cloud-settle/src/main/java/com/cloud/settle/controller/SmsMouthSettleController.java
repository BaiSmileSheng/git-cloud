package com.cloud.settle.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.service.ISmsMouthSettleService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 月度结算信息  提供者
 *
 * @author cs
 * @date 2020-06-04
 */
@RestController
@RequestMapping("mouthSettle")
public class SmsMouthSettleController extends BaseController {

    @Autowired
    private ISmsMouthSettleService smsMouthSettleService;

    /**
     * 查询月度结算信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询月度结算信息 ", response = SmsMouthSettle.class)
    public SmsMouthSettle get(Long id) {
        return smsMouthSettleService.selectByPrimaryKey(id);

    }

    /**
     * 查询月度结算信息 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "月度结算信息 查询分页", response = SmsMouthSettle.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsMouthSettle smsMouthSettle) {
        Example example = new Example(SmsMouthSettle.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<SmsMouthSettle> smsMouthSettleList = smsMouthSettleService.selectByExample(example);
        return getDataTable(smsMouthSettleList);
    }


    /**
     * 新增保存月度结算信息
     */
    @PostMapping("save")
    @OperLog(title = "新增保存月度结算信息 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存月度结算信息 ", response = R.class)
    public R addSave(@RequestBody SmsMouthSettle smsMouthSettle) {
        smsMouthSettleService.insertSelective(smsMouthSettle);
        return R.data(smsMouthSettle.getId());
    }

    /**
     * 修改保存月度结算信息
     */
    @PostMapping("update")
    @OperLog(title = "修改保存月度结算信息 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存月度结算信息 ", response = R.class)
    public R editSave(@RequestBody SmsMouthSettle smsMouthSettle) {
        return toAjax(smsMouthSettleService.updateByPrimaryKeySelective(smsMouthSettle));
    }

    /**
     * 删除月度结算信息
     */
    @PostMapping("remove")
    @OperLog(title = "删除月度结算信息 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除月度结算信息 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsMouthSettleService.deleteByIds(ids));
    }

    /**
     * 月度结算定时任务
     * 这是一个大工程
     * @return
     */
    @PostMapping("countMonthSettle")
    @ApiOperation(value = "月度结算定时任务", response = R.class)
    public R countMonthSettle(){
        return smsMouthSettleService.countMonthSettle();
    }

}
