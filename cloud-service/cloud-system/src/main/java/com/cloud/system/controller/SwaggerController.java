package com.cloud.system.controller;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
@Api("测试用例")
public class SwaggerController {
    @ApiOperation(value = "hello ~", notes = "欢迎",response = SysUser.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "名字", required = true)})
    @GetMapping("/hell")
    @ApiResponses({
            @ApiResponse(code=200,message="成功"),
            @ApiResponse(code=500,message="发生错误")
    })
    public String get(String name) throws InterruptedException {
        Thread.sleep(20000);
        return "hello " + name;
    }

    @ApiOperation(value = "hello ~", notes = "欢迎", response = SysUser.class)
    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "名字", required = true)})
    @GetMapping("/hello")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "发生错误")
    })
    public String get1(String name) throws InterruptedException {
        Thread.sleep(20000);
        return "hello " + name;
    }
}
