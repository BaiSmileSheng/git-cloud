package com.cloud.system.feign.factory;

import com.cloud.common.core.domain.R;
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
        log.error("RemoteOssService错误信息：{}",throwable.getMessage());
        return new RemoteOssService() {


            /**
             * 上传文件到华为云
             * @param file
             * @return
             */
            @Override
            public R uploadFile(MultipartFile file, String orderNo) {
                return R.error("服务器拥挤，请稍后再试！");
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
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             *
             * @param orderNo 订单编号
             * @return R 包含List<SysOss> 文件上传集合
             */
            @Override
            public R listByOrderNo(String orderNo) {
                log.error("RemoteOssService.listByOrderNo错误信息：{}",throwable.getMessage());
                return R.error("服务器拥挤，请稍后再试！");
            }

            /**
             * 根据订单编号修改文件上传列表
             * @param orderNo 订单编号
             * @param files 文件数组
             * @return 成功或失败
             */
            @Override
            public R updateListByOrderNo(String orderNo, MultipartFile[] files) {
                return R.error("根据订单号修改文件信息失败");
            }

            /**
             * 根据订单编号删除文件上传列表
             * @param orderNo 订单编号
             * @return 成功或失败
             */
            @Override
            public R deleteListByOrderNo(String orderNo) {
                return R.error("根据订单号删除文件信息失败");
            }
        };
    }
}
