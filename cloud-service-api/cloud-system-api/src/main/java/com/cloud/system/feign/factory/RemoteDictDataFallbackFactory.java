package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.feign.RemoteDictDataService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


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

            /**
             * 根据字典类型查询字典数据信息
             * 从redis中取值，过期时间一周
             * @param dictType 字典类型
             * @return 参数键值
             */
            @Override
            public List<SysDictData> getType(String dictType) {
                return null;
            }
        };
    }
}
