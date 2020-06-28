package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdFactoryLineInfo;
import com.cloud.system.feign.RemoteFactoryLineInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteFactoryLineInfoFallbackFactory implements FallbackFactory<RemoteFactoryLineInfoService> {

    @Override
    public RemoteFactoryLineInfoService create(Throwable throwable) {

        return new RemoteFactoryLineInfoService() {
            /**
             * 查询工厂线体关系
             * @param cdFactoryLineInfo
             * @return List<CdFactoryLineInfo>
             */
            @Override
            public R listByExample(CdFactoryLineInfo cdFactoryLineInfo) {
                log.error("RemoteFactoryLineInfoService.listByExample错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 查询工厂线体关系
             * @param supplierCode
             * @return 逗号分隔线体编号
             */
            @Override
            public R selectLineCodeBySupplierCode(String supplierCode) {
                log.error("RemoteFactoryLineInfoService.selectLineCodeBySupplierCode错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 根据线体查询信息
             * @param produceLineCode
             * @param factoryCode
             * @return 供应商编码
             */
            @Override
            public R selectInfoByCodeLineCode(String produceLineCode,String factoryCode) {
                log.error("RemoteFactoryLineInfoService.selectInfoByCodeLineCode错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
            /**
             * 定时任务获取工厂线体关系数据，并保存
             * @return R
             */
            @Override
            public R saveFactoryLineInfo() {
                log.error("RemoteFactoryLineInfoService.saveFactoryLineInfo错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }
        };
    }
}
