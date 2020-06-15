package com.cloud.system.config;

import com.cloud.common.constant.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign配置
 * 使用FeignClient进行服务间调用，传递headers信息
 */
@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //添加token
        requestTemplate.header(Constants.CURRENT_ID, request.getHeader(Constants.CURRENT_ID));
    }
}
