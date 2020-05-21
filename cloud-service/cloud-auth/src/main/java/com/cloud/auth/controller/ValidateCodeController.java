package com.cloud.auth.controller;

import com.cloud.common.constant.Constants;
import com.cloud.common.core.domain.R;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lihongxia
 * @Date 2020-05-20
 */
@RestController
@Api(tags = "获取验证码")
public class ValidateCodeController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DefaultKaptcha captchaProducerMath;

    @PostMapping("validateCode")
    @ApiOperation(value = "获取验证码", notes = "获取验证码接口")
    public R getValidateCode(){
        // 生成验证码
        String capText = captchaProducerMath.createText();
        String code = capText.substring(capText.lastIndexOf("@") + 1);
        // 保存验证码信息
        String randomStr = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set(Constants.DEFAULT_CODE_KEY + randomStr, code, 60, TimeUnit.SECONDS);
        Map<String,Object> map = new HashMap<>();
        map.put("captcha",code);
        map.put("randomStr",randomStr);
        return R.ok(map);
    }
}
