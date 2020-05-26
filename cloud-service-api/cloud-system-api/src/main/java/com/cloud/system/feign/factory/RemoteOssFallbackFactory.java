package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
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


            /**
             * 上传文件到华为云
             * @param file
             * @return
             */
            @Override
            public R uploadFile(MultipartFile file) {
                return R.error();
            }

            /**
             * 下载文件
             * @param url   文件表url
             * @param fileName   下载文件名
             */
            @Override
            public void downLoad(String url,String fileName) {

            }

            /**
             * 删除文件上传
             * @param ids  文件表id  逗号分隔
             * @return
             */
            @Override
            public R remove(String ids) {
                return R.error();
            }

            /**
             *
             * @param orderNo 订单编号
             * @return R 包含List<SysOss> 文件上传集合
             */
            @Override
            public R listByOrderNo(String orderNo) {
                return null;
            }
        };
    }
}
