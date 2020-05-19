package com.cloud.system.feign.factory;

import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.feign.RemoteOssService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


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

            /**
             * 根据订单编号查询文件上传列表
             * @param orderNo
             * @return
             */
            @Override
            public List<SysOss> listByOrderNo(String orderNo) {
                return null;
            }
        };
    }
}
