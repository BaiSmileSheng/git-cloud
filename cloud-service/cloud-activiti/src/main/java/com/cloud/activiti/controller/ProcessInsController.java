package com.cloud.activiti.controller;

import cn.hutool.core.util.StrUtil;
import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.activiti.service.IHistoryInfoService;
import com.cloud.activiti.vo.HiProcInsVo;
import com.cloud.activiti.vo.ProcessInsVo;
import com.cloud.activiti.vo.RuTask;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.PageDomain;
import com.cloud.system.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>File：ProcessInsController.java</p>
 * <p>Title: 流程实例</p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午1:27:18</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("process/ins/")
public class ProcessInsController extends BaseController {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private IHistoryInfoService historyInfoService;

    @Autowired
    private RemoteUserService userService;

    @Autowired
    private IBizBusinessService bizBusinessService;

    /**
     * 挂起、激活流程实例
     */
    @RequestMapping(value = "update/{processInstanceId}/{state}")
    public R updateState(@PathVariable("state") String state,
                         @PathVariable("processInstanceId") String processInstanceId) {
        if (state.equals("active")) {
            runtimeService.activateProcessInstanceById(processInstanceId);
            log.info("已激活ID为:{}的流程实例", processInstanceId);
        } else if (state.equals("suspend")) {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            log.info("已挂起ID为:{}的流程实例", processInstanceId);
        }
        return R.ok();
    }

    /**
     * 获取任务列表
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "tasks")
    public R tasks(PageDomain page) {
        List<Task> tasks = taskService.createTaskQuery().listPage(page.getPageSize() * (page.getPageNum() - 1),
                page.getPageSize());
        long count = runtimeService.createExecutionQuery().count();
        List<RuTask> list = new ArrayList<>();
        for (Task task : tasks) {
            list.add(new RuTask(task));
        }
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("rows", list);
        m.put("pageNum", page.getPageNum());
        m.put("total", count);
        return R.ok(m);
    }

    @RequestMapping(value = "runs")
    public R getList(PageDomain page, String name, String key, String processInstanceId) {
        List<ProcessInsVo> list = new ArrayList<>();
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc();
        if (StrUtil.isNotBlank(name)) {
            query.processInstanceNameLike("%" + name + "%");
        }
        if (StrUtil.isNotBlank(key)) {
            query.processDefinitionKey(key);
        }
        if (StrUtil.isNotBlank(processInstanceId)) {
            query.processInstanceId(processInstanceId);
        }
        long count = query.count();
        int first = (page.getPageNum() - 1) * page.getPageSize();
        List<ProcessInstance> processInstanceList = query.listPage(first, page.getPageSize());
        processInstanceList.forEach(e -> {
            list.add(new ProcessInsVo(e));
        });
        list.forEach(e -> {
            List<HistoricIdentityLink> identityLinks = historyService
                    .getHistoricIdentityLinksForProcessInstance(e.getId());
            for (HistoricIdentityLink hik : identityLinks) {
                // 关联发起人
                if ("starter".equals(hik.getType()) && StrUtil.isNotBlank(hik.getUserId())) {
                    e.setApplyer(userService.selectSysUserByUserId(Long.valueOf(hik.getUserId())).getLoginName());
                }
            }
            // 关联当前任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(e.getId()).list();
            if (taskList != null&&taskList.size()>0) {
                e.setCurrTaskName(taskList.get(0).getName());
            }
        });
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("rows", list);
        m.put("pageNum", page.getPageNum());
        m.put("total", count);
        return R.ok(m);
    }

    @RequestMapping(value = "finished")
    public R finush(HiProcInsVo hiProcInsVo) {
        startPage();
        return result(historyInfoService.getHiProcInsListDone(hiProcInsVo));
    }

    /**
     * 删除运行中实例
     *
     * @param ids
     * @param reason
     * @param delBusiness 是否删除业务
     * @return
     * @author zmr
     */
    @PostMapping(value = "remove")
    public R remove(String ids, String reason) {
        if (StrUtil.isBlank(reason)) {
            reason = "";
        }
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            // 关联业务状态结束
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
            BizBusiness bizBusiness = new BizBusiness();
            bizBusiness.setId(Long.valueOf(pi.getBusinessKey()));
            bizBusiness.setCurrentTask(ActivitiConstant.END_TASK_NAME);
            bizBusiness.setStatus(ActivitiConstant.STATUS_CANCELED);
            bizBusiness.setResult(ActivitiConstant.RESULT_CANCELED);
            bizBusinessService.updateBizBusiness(bizBusiness);
            runtimeService.deleteProcessInstance(id, reason);
        }
        return R.ok();
    }

    /**
     * 删除已经结束的实例
     *
     * @param ids
     * @return
     * @author zmr
     */
    @RequestMapping("remove/his")
    public R removeHis(String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            historyService.deleteHistoricProcessInstance(id);
        }
        return R.ok();
    }
}
