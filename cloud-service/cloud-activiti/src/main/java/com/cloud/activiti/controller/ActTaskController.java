/*
 * @(#)ActTaskController.java 2020年1月7日 下午6:15:46
 * Copyright 2020 zmr, Inc. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.activiti.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizAuditService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.activiti.vo.HiTaskVo;
import com.cloud.activiti.vo.RuTask;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.PageDomain;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>File：ActTaskController.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月7日 下午6:15:46</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@RestController
@RequestMapping("task")
@Api(tags = "审核流转")
public class ActTaskController extends BaseController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private IBizAuditService bizAuditService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private IActTaskService actTaskService;

    @Autowired
    private IBizBusinessService bizBusinessService;


    /**
     * task待办
     *
     * @return
     * @author zmr
     */
    @RequestMapping(value = "ing",method = RequestMethod.GET)
//    @ApiOperation(value = "我的代办")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "orderNo", value = "订单号", required = false, paramType = "query", dataType = "String")
//    })
    public R ing(@ApiIgnore RuTask ruTask, PageDomain page) {
        List<RuTask> list = new ArrayList<>();
        Long userId = getCurrentUserId();
        TaskQuery query = taskService.createTaskQuery().taskCandidateOrAssigned(userId + "").orderByTaskCreateTime()
                .desc();
        if (StrUtil.isNotBlank(ruTask.getProcessDefKey())) {
            query.processDefinitionKey(ruTask.getProcessDefKey());
        }

        long count = query.count();
        int first = (page.getPageNum() - 1) * page.getPageSize();
        List<Task> taskList = query.listPage(first, page.getPageSize());
        if (taskList.size() > 0) {
            // 转换vo
            taskList.forEach(e -> {
                RuTask rt = new RuTask(e);
                List<IdentityLink> identityLinks = runtimeService
                        .getIdentityLinksForProcessInstance(rt.getProcInstId());
                for (IdentityLink ik : identityLinks) {
                    // 关联发起人
                    if ("starter".equals(ik.getType()) && StrUtil.isNotBlank(ik.getUserId())) {
                        rt.setApplyer(
                                remoteUserService.selectSysUserByUserId(Long.parseLong(ik.getUserId())).getUserName());
                    }
                }
                // 关联业务key
                ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(rt.getProcInstId())
                        .singleResult();
                BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(pi.getBusinessKey());
                if (bizBusiness != null) {
                    rt.setOrderNo(bizBusiness.getOrderNo());
                }
                rt.setBusinessKey(pi.getBusinessKey());
                rt.setProcessName(pi.getName());
                rt.setProcessDefKey(pi.getProcessDefinitionKey());
                rt.setProcessDefName(pi.getProcessDefinitionName());
                list.add(rt);
            });
        }
        Map<String, Object> map = Maps.newHashMap();
        //如果有业务订单号的查询条件
        if (StrUtil.isNotBlank(ruTask.getOrderNo())) {
            List<RuTask> listNew=list.stream().filter(e -> StrUtil.equals(e.getOrderNo(), ruTask.getOrderNo())).collect(Collectors.toList());
            map.put("rows", listNew);
            map.put("total", CollUtil.isEmpty(listNew)?0:listNew.size() );
        }else {
            map.put("rows", list);
            map.put("total", count);
        }
        map.put("pageNum", page.getPageNum());
        return R.ok(map);
    }

    /**
     * task 已办
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "done",method = RequestMethod.GET)
    @ApiOperation(value = "我的已办")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "orderNo", value = "订单号", required = false, paramType = "query", dataType = "String")
    })
    public R done(@ApiIgnore HiTaskVo hiTaskVo) {
        startPage();
        hiTaskVo.setAuditorId(getCurrentUserId());
        hiTaskVo.setDeleteReason(ActivitiConstant.REASON_COMPLETED);
        return result(bizAuditService.getHistoryTaskList(hiTaskVo));
    }

    /**
     * task 流转历史
     *
     * @param hiTaskVo
     * @return
     * @author zmr
     */
    @RequestMapping(value = "flow")
    public R flow(HiTaskVo hiTaskVo) {
        startPage();
        return result(bizAuditService.getHistoryTaskList(hiTaskVo));
    }

    /**
     * task 流转历史  一个订单有多次审核
     *
     * @param tableId
     * @param procDefKey
     * @return
     */
    @GetMapping(value = "flowList")
    @ApiOperation(value = "审核历史-多次", response = HiTaskVo.class)
    public R flow(String tableId, String procDefKey) {
        startPage();
        return result(bizAuditService.getHistoryTaskList(tableId, procDefKey));
    }

    /**
     * 审批  只作为示例
     *
     * @param bizAudit
     * @return
     * @author zmr
     */
    @PostMapping("audit")
    public R audit(@RequestBody BizAudit bizAudit) {
        //审批 推进工作流
        return actTaskService.audit(bizAudit, getCurrentUserId());
    }

    /**
     * 批量审批
     *
     * @param bizAudit
     * @return
     */
    @PostMapping("audit/batch")
    public R auditBatch(@RequestBody BizAudit bizAudit) {
        return actTaskService.auditBatch(bizAudit, getCurrentUserId());
    }

    /**
     * remove审批记录 逻辑删除
     */
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(bizAuditService.deleteBizAuditLogic(ids));
    }
}
