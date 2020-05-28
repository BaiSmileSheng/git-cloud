package com.cloud.activiti.controller;

import com.cloud.activiti.consts.ActivitiConstant;
import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.domain.BizBusiness;
import com.cloud.activiti.service.IActTaskService;
import com.cloud.activiti.service.IBizBusinessService;
import com.cloud.common.constant.ActivitiProTitleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SettleTestAct;
import com.cloud.settle.feign.RemoteSettleTestActService;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.feign.RemoteUserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 流程测试 提供者
 *
 * @author cs
 * @date 2020-05-20
 */
@RestController
@RequestMapping("smsActTest")
public class ActSettleTestActController extends BaseController {
    @Autowired
    private IBizBusinessService bizBusinessService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private RemoteSettleTestActService remoteSettleTestActService;

    @Autowired
    private IActTaskService actTaskService;


    /**
     * 根据业务key获取数据
     *
     * @param businessKey
     * @return SettleTestAct
     * @author cs
     */
    @GetMapping("biz/{businessKey}")
    public R getBizInfoByTableId(@PathVariable("businessKey") String businessKey) {
        BizBusiness business = bizBusinessService.selectBizBusinessById(businessKey);
        if (null != business) {
            SettleTestAct settleTestAct = remoteSettleTestActService.get(Long.valueOf(business.getTableId()));
            return R.data(settleTestAct);
        }
        return R.error("no record");
    }

    /**
     * 开启流程
     *
     * @param settleTestAct
     * @return R 成功/失败
     */
    @PostMapping("save")
    public R addSave(@RequestBody SettleTestAct settleTestAct) {
        R r = remoteSettleTestActService.addSave(settleTestAct);

        if (r == null || r.get("data") == null) {
            return R.error();
        }
        if ("0".equals(r.get("code").toString())) {
            settleTestAct.setId(Long.valueOf(r.get("data").toString()));
            BizBusiness business = initBusiness(settleTestAct);
            bizBusinessService.insertBizBusiness(business);
            Map<String, Object> variables = Maps.newHashMap();
            variables.put("money", settleTestAct.getMoney());
            bizBusinessService.startProcess(business, variables);
        }
        return r;
    }

    /**
     * biz构造业务信息
     *
     * @param settleTestAct
     * @return
     * @author cs
     */
    private BizBusiness initBusiness(SettleTestAct settleTestAct) {
        BizBusiness business = new BizBusiness();
        business.setTableId(settleTestAct.getId().toString());
        business.setProcDefId(settleTestAct.getProcDefId());
        business.setTitle(ActivitiProTitleConstants.ACTIVITI_PRO_TITLE_SETTLE_TEST);
        business.setProcName(settleTestAct.getProcName());
        long userId = getCurrentUserId();
        business.setUserId(userId);
        SysUser user = remoteUserService.selectSysUserByUserId(userId);
        business.setApplyer(user.getUserName());
        business.setStatus(ActivitiConstant.STATUS_DEALING);
        business.setResult(ActivitiConstant.RESULT_DEALING);
        business.setApplyTime(new Date());
        return business;
    }

    /**
     * 流程审批
     * @param bizAudit
     * @return 成功/失败
     */
    @PostMapping("audit")
    public R audit(@RequestBody BizAudit bizAudit) {
        //可处理业务逻辑  测试
        BizBusiness bizBusiness = bizBusinessService.selectBizBusinessById(bizAudit.getBusinessKey().toString());
        if (bizBusiness == null) {
            return R.error();
        }
        SettleTestAct settleTestAct = remoteSettleTestActService.get(Long.valueOf(bizBusiness.getTableId()));
        if (settleTestAct == null) {
            return R.error();
        }
        settleTestAct.setRemark("我已经审批了，审批结果：" + bizAudit.getResult());
        R r=remoteSettleTestActService.editSave(settleTestAct);
        if("0".equals(r.get("code"))){
            //审批 推进工作流
            return actTaskService.audit(bizAudit, getCurrentUserId());
        }
        return R.error();
    }
}
