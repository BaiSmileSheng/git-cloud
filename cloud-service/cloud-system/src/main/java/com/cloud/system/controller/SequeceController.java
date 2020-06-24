package com.cloud.system.controller;

import com.cloud.common.core.domain.R;
import com.cloud.system.service.ISequeceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取序列号
 * @Author Lihongxia
 * @Date 2020-06-02
 */
@RestController
@RequestMapping("sequece")
@Api(tags = "获取序列号")
@Slf4j
public class SequeceController {

    @Autowired
    private ISequeceService sequeceService;

    /**
     * 获取序列号
     * @param name 序列名称
     * @param length 所需序列号长度
     * @return 序列号
     */
    @GetMapping("/selectSeq")
    @ApiOperation(value = "获取序列号",response = String.class)
    public R selectSeq(@RequestParam("name") String name,@RequestParam("length") int length){
        return R.data(sequeceService.selectSeq(name,length));
    }
}
