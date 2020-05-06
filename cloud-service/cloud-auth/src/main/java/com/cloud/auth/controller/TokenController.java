package com.cloud.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.cloud.system.domain.entity.SysDept;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.auth.form.LoginForm;
import com.cloud.auth.service.AccessTokenService;
import com.cloud.auth.service.SysLoginService;
import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysUser;

@RestController
@Api(tags = "获取token")
public class TokenController {
    @Autowired
    private AccessTokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("login")
    @ApiOperation(value = "获取token", notes = "登录接口代替")
    public R login(@RequestBody LoginForm form) {
        // 用户登录
        SysUser user = sysLoginService.login(form.getUsername(), form.getPassword());
        //将用户的物料数据权限放到redis
        tokenService.userScopeRedis(user.getUserId());
        // 获取登录token
        return R.ok(tokenService.createToken(user));
    }

    @PostMapping("logout")
    public R logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        SysUser user = tokenService.queryByToken(token);
        if (null != user) {
            sysLoginService.logout(user.getLoginName());
            tokenService.expireToken(user.getUserId());
        }
        return R.ok();
    }
}
