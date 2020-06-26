package com.cloud.activiti.feign;

import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.feign.factory.RemoteActOmsOrderMaterialOutFallbackFactory;
import com.cloud.common.constant.ServiceNameConstants;
import com.cloud.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 下市审批工作流Feign服务层
 *
 * @author lihongxia
 * @date 2020-06-18
 */
@FeignClient(name = ServiceNameConstants.ACTIVITI_SERVICE, fallbackFactory = RemoteActOmsOrderMaterialOutFallbackFactory.class)
public interface RemoteActOmsOrderMaterialOutService {

    /**
     * 导入时物料下市开启真单审批流程
     * @return 成功或失败
     */
    @PostMapping("actOmsOrderMaterialOut/save")
    R addSave(@RequestBody OmsOrderMaterialOutVo omsOrderMaterialOutVo);
}
