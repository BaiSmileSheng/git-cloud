package com.cloud.activiti.feign.factory;

import com.cloud.activiti.domain.entity.vo.OmsOrderMaterialOutVo;
import com.cloud.activiti.feign.RemoteActOmsOrderMaterialOutService;
import com.cloud.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteActOmsOrderMaterialOutFallbackFactory implements FallbackFactory<RemoteActOmsOrderMaterialOutService> {


    @Override
    public RemoteActOmsOrderMaterialOutService create(Throwable throwable) {

        return new RemoteActOmsOrderMaterialOutService(){

            /**
             * 导入时物料下市开启真单审批流程
             * @return 成功或失败
             */
            @Override
            public R addSave(OmsOrderMaterialOutVo omsOrderMaterialOutVo) {
                log.error("RemoteActOmsRealOrderService.addSave熔断错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
