package com.cloud.settle.feign;

import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SettleTestAct;
import com.cloud.settle.feign.factory.RemoteSettleTestActFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 测试审批流 Feign服务层
 *
 * @author cs
 * @date 2020-05-20
 */
@FeignClient(name = ServiceNameConstants.SETTLE_SERVICE, fallbackFactory = RemoteSettleTestActFallbackFactory.class)
public interface RemoteSettleTestActService {
    /**
     * 根据ID查询测试审批流列表
     * @param id
     * @return SettleTestAct
     */
    @GetMapping("settleTest/get")
    SettleTestAct get(@RequestParam("id") Long id);

    /**
     * 新增或保存测试审批流
     * @param settleTestAct
     * @return id
     */
    @PostMapping("settleTest/save")
    R addSave(SettleTestAct settleTestAct);

    /**
     * 修改保存测试审批流
     * @param settleTestAct
     * @return 是否成功
     */
    @PostMapping("settleTest/update")
    R editSave(@RequestBody SettleTestAct settleTestAct);
}
