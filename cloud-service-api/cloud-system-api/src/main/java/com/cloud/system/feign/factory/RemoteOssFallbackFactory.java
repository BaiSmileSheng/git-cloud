package com.cloud.system.feign.factory;

import com.cloud.system.feign.RemoteOssService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Component
public class RemoteOssFallbackFactory implements FallbackFactory<RemoteOssService> {

    @Override
    public RemoteOssService create(Throwable throwable) {
        log.error(throwable.getMessage());
        return new RemoteOssService() {


            @Override
            public String uploadFile(MultipartFile file) {
                return null;
            }

            @Override
            public void downLoad(String url,String fileName) {

            }

            @Override
            public String remove(String ids) {
                return null;
            }
        };
    }
}
