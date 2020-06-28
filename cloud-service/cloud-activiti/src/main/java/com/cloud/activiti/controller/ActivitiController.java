package com.cloud.activiti.controller;

import cn.hutool.core.bean.BeanUtil;
import com.cloud.activiti.domain.ActReProcdef;
import com.cloud.activiti.domain.entity.ProcessDefinitionAct;
import com.cloud.activiti.service.IActReProcdefService;
import com.cloud.activiti.vo.ReProcdef;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.settle.domain.entity.SmsScrapOrder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程控制接口
 *
 * @Auther: Ace Lee
 * @Date: 2019/3/5 15:04
 */
@RestController
@RequestMapping("prof")
@Api(tags = "获取流程实例")
public class ActivitiController extends BaseController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IActReProcdefService procdefService;

    /**
     * 启动一个流程
     *
     * @param key
     * @return
     * @author zmr
     */
    @GetMapping("start/{key}")
    public R start(@PathVariable("key") String key) {
        runtimeService.startProcessInstanceByKey(key);
        return R.ok();
    }

    @GetMapping("allLatest")
    public R list() {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery().latestVersion();
        List<ProcessDefinition> processDefinitions = query.list();
        List<ReProcdef> list = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitions) {
            ReProcdef reProcdef = new ReProcdef(processDefinition);
            list.add(reProcdef);
        }
        return R.ok().put("rows", list);
    }

    @GetMapping("list")
    @ApiOperation(value = "根据key获取流程实例", response = SmsScrapOrder.class)
    public R list(ActReProcdef actReProcdef) {
        startPage();
        return result(procdefService.selectList(actReProcdef));
    }

    @PostMapping("remove")
    public R deleteOne(String ids) {
        String[] idArr = ids.split(",");
        for (String id : idArr) {
            long count = runtimeService.createProcessInstanceQuery().deploymentId(id).count();
            if (count > 0) {
                return R.error("流程正在运行中，无法删除");
            } else {
                // 根据deploymentID删除定义的流程，普通删除
                repositoryService.deleteDeployment(id);
            }
            // 强制删除
            // repositoryService.deleteDeployment(id, true);
            // System.out.println("强制删除--流程定义删除成功");
        }
        return R.ok();
    }
    /**
     * Description:  根据Key值获取最新版流程实例
     * Param: [key]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/24
     */
    @PostMapping("getByKey")
    @ApiOperation(value = "根据Key值获取最新版流程实例",response = R.class)
    public R getByKey(@RequestParam(value = "key") String key){

        // 使用repositoryService查询单个流程实例
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
        if (BeanUtil.isEmpty(processDefinition)) {
            logger.error("根据Key值查询流程实例失败!");
            return R.error("根据Key值查询流程实例失败！");
        }
        ProcessDefinitionAct processDefinitionAct =
                ProcessDefinitionAct.builder()
                        .id(processDefinition.getId())
                        .name(processDefinition.getName())
                        .category(processDefinition.getCategory())
                        .deploymentId(processDefinition.getDeploymentId())
                        .description(processDefinition.getDescription())
                        .diagramResourceName(processDefinition.getDiagramResourceName())
                        .resourceName(processDefinition.getResourceName())
                        .tenantId(processDefinition.getTenantId())
                        .version(processDefinition.getVersion()).build();
        return R.data(processDefinitionAct);
    }
}
