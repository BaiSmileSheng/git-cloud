package com.cloud.activiti.controller;

import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 流程业务 提供者
 *
 * @author cloud
 * @date 2020-01-06
 */
@RestController
@RequestMapping("business")
@Api(tags = "流程业务")
public class BizBusinessController extends BaseController {
    @Autowired
    private IBizBusinessService bizBusinessService;

    /**
     * 查询流程业务
     */
    @GetMapping("get/{id}")
    public BizBusiness get(@PathVariable("id") String id) {
        return bizBusinessService.selectBizBusinessById(id);
    }

    /**
     * 查询流程业务列表
     */
    @GetMapping("list/my")
    @ApiOperation(value = "我的申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "title", value = "申请标题", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "状态", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "result", value = "审批结果", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = false, paramType = "query", dataType = "String")
    })
    public R list(@ApiIgnore BizBusiness bizBusiness) {
        startPage();
        bizBusiness.setUserId(getCurrentUserId());
        bizBusiness.setDelFlag(false);
        return result(bizBusinessService.selectBizBusinessList(bizBusiness));
    }

    /**
     * 新增保存流程业务
     */
    @PostMapping("save")
    public R addSave(@RequestBody BizBusiness bizBusiness) {
        bizBusiness.setUserId(getCurrentUserId());
        return toAjax(bizBusinessService.insertBizBusiness(bizBusiness));
    }

    /**
     * 修改保存流程业务
     */
    @PostMapping("update")
    public R editSave(@RequestBody BizBusiness bizBusiness) {
        return toAjax(bizBusinessService.updateBizBusiness(bizBusiness));
    }

    /**
     * 删除流程业务
     */
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(bizBusinessService.deleteBizBusinessLogic(ids));
    }

    /**
     * 根据procDefId和tableId查procInstId
     * @param procDefKey
     * @param tableId
     * @return
     */
    @GetMapping("selectByKeyAndTable")
    public R selectByKeyAndTable(@RequestParam("procDefKey") String procDefKey,@RequestParam("tableId") String tableId) {
        String procInstId = bizBusinessService.selectByKeyAndTable(procDefKey,tableId);
        return R.data(procInstId);
    }
}
