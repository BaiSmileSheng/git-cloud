package com.cloud.system.feign.factory;

import com.cloud.system.feign.RemoteDictDataService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RemoteDictDataFallbackFactory implements FallbackFactory<RemoteDictDataService> {

    @Override
    public RemoteDictDataService create(Throwable throwable) {
        log.error("RemoteDictDataService错误信息：{}",throwable.getMessage());
        return new RemoteDictDataService() {

            /**
             * 根据字典类型和字典键值查询字典数据信息
             *
             * @param dictType  字典类型
             * @param dictValue 字典键值
             * @return 字典标签
             */
            @Override
            public String getLabel(String dictType, String dictValue) {
                return null;
            }
        };
    }
}
