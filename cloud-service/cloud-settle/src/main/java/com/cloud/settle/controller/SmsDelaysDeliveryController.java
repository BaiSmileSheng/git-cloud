package com.cloud.settle.controller;

import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.settle.domain.entity.SmsDelaysDelivery;
import com.cloud.settle.service.ISmsDelaysDeliveryService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 延期交付索赔  提供者
 *
 * @author cs
 * @date 2020-06-01
 */
@RestController
@RequestMapping("delaysDelivery")
public class SmsDelaysDeliveryController extends BaseController {

    @Autowired
    private ISmsDelaysDeliveryService smsDelaysDeliveryService;

    /**
     * 查询延期交付索赔
     * @param id
     * @return 延期交付索赔信息
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询延期交付索赔 ", response = SmsDelaysDelivery.class)
    public SmsDelaysDelivery get(Long id) {
        return smsDelaysDeliveryService.selectByPrimaryKey(id);

    }

    /**
     * 查询延期交付索赔 列表
     * @param smsDelaysDelivery
     * @return 延期交付索赔列表
     */
    @GetMapping("list")
    @ApiOperation(value = "延期交付索赔 查询分页", response = SmsDelaysDelivery.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsDelaysDelivery smsDelaysDelivery) {
        Example example = new Example(SmsDelaysDelivery.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<SmsDelaysDelivery> smsDelaysDeliveryList = smsDelaysDeliveryService.selectByExample(example);
        return getDataTable(smsDelaysDeliveryList);
    }

    /**
     * 新增保存延期交付索赔
     * @param smsDelaysDelivery
     * @return 成功或失败
     */
    @PostMapping("save")
    @OperLog(title = "新增保存延期交付索赔 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存延期交付索赔 ", response = R.class)
    public R addSave(@RequestBody SmsDelaysDelivery smsDelaysDelivery) {
        smsDelaysDeliveryService.insertSelective(smsDelaysDelivery);
        return R.data(smsDelaysDelivery.getId());
    }


    /**
     * 定时任务调用批量新增保存延期交付索赔(并发送邮件)
     * @return 成功或失败
     */
    @PostMapping("batchAddDelaysDelivery")
    @OperLog(title = "批量新增保存延期交付索赔(并发送邮件) ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "批量新增保存延期交付索赔(并发送邮件) ", response = R.class)
    public R batchAddDelaysDelivery() {
        return smsDelaysDeliveryService.batchAddDelaysDelivery();
    }

    /**
     * 修改保存延期交付索赔
     * @param smsDelaysDelivery
     * @return 成功或失败
     */
    @PostMapping("update")
    @OperLog(title = "修改保存延期交付索赔 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存延期交付索赔 ", response = R.class)
    public R editSave(@RequestBody SmsDelaysDelivery smsDelaysDelivery) {
        return toAjax(smsDelaysDeliveryService.updateByPrimaryKeySelective(smsDelaysDelivery));
    }

    /**
     * 删除延期交付索赔
     * @param ids
     * @return 成功或失败
     */
    @PostMapping("remove")
    @OperLog(title = "删除延期交付索赔 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除延期交付索赔 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsDelaysDeliveryService.deleteByIds(ids));
    }

}
