package com.cloud.system.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.file.OssException;
import com.cloud.common.utils.StringUtils;
import com.cloud.common.utils.ValidatorUtils;
import com.cloud.common.utils.file.FileUtils;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.oss.*;
import com.cloud.system.oss.CloudConstant.CloudService;
import com.cloud.system.oss.valdator.AliyunGroup;
import com.cloud.system.oss.valdator.HuaweiGroup;
import com.cloud.system.service.ISysConfigService;
import com.cloud.system.service.ISysOssService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * 文件上传 提供者
 *
 * @author zmr
 * @date 2019-05-16
 */
@RestController
@RequestMapping("oss")
public class SysOssController extends BaseController {
    private final static String KEY = CloudConstant.CLOUD_STORAGE_CONFIG_KEY;

    @Autowired
    private ISysOssService sysOssService;

    @Autowired
    private ISysConfigService sysConfigService;

    /**
     * 云存储配置信息
     */
    @RequestMapping("config")
    @HasPermissions("sys:oss:config")
    public CloudStorageConfig config() {
        String jsonconfig = sysConfigService.selectConfigByKey(CloudConstant.CLOUD_STORAGE_CONFIG_KEY);
        // 获取云存储配置信息
        CloudStorageConfig config = JSON.parseObject(jsonconfig, CloudStorageConfig.class);
        return config;
    }

    /**
     * 保存云存储配置信息
     */
    @RequestMapping("saveConfig")
    @HasPermissions("sys:oss:config")
    public R saveConfig(CloudStorageConfig config) {
        // 校验类型
        ValidatorUtils.validateEntity(config);
        if (config.getType() == CloudService.HUAWEIYUN.getValue()) {
            // 校验华为云数据
            ValidatorUtils.validateEntity(config, HuaweiGroup.class);
        } else if (config.getType() == CloudService.ALIYUN.getValue()) {
            // 校验阿里云数据
            ValidatorUtils.validateEntity(config, AliyunGroup.class);
        }
        return toAjax(sysConfigService.updateValueByKey(KEY, new Gson().toJson(config)));
    }

    /**
     * 查询文件上传
     */
    @GetMapping("get/{id}")
    public SysOss get(@PathVariable("id") Long id) {
        return sysOssService.selectSysOssById(id);
    }

    /**
     * 查询文件上传列表
     */
    @GetMapping("list")
    public R list(SysOss sysOss) {
        startPage();
        return result(sysOssService.selectSysOssList(sysOss));
    }

    /**
     * 修改
     */
    @PostMapping("update")
    @HasPermissions("sys:oss:edit")
    public R editSave(@RequestBody SysOss sysOss) {
        return toAjax(sysOssService.updateSysOss(sysOss));
    }

    /**
     * 修改保存文件上传
     *
     * @throws IOException
     */
    @PostMapping("upload")
    @HasPermissions("sys:oss:add")
    public R editSave(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new OssException("上传文件不能为空");
        }
        // 上传文件
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        CloudStorageService storage = OSSFactory.build();
        String url = storage.uploadSuffix(file.getBytes(), suffix);
        // 保存文件信息
        SysOss ossEntity = new SysOss();
        ossEntity.setUrl(url);
        ossEntity.setFileSuffix(suffix);
        ossEntity.setCreateBy(getLoginName());
        ossEntity.setFileName(fileName);
        ossEntity.setCreateTime(new Date());
        ossEntity.setService(storage.getService());
        return toAjax(sysOssService.insertSysOss(ossEntity));
    }

    /**
     * 下载文件
     *
     */
    @PostMapping("downLoad")
    public void downLoad(String url,String fileName) throws IOException {
        String realName = new String();
        if(StringUtils.isNotBlank(fileName)){
            realName=fileName;
        }else{
            realName = url;
        }
        CloudStorageService storage = OSSFactory.build();

        getResponse().setCharacterEncoding("utf-8");
        // 下载使用"application/octet-stream"更标准
        getResponse().setContentType("application/octet-stream");
        getResponse().setHeader("Content-Disposition",
                "attachment;filename=" + FileUtils.setFileDownloadHeader(getRequest(), realName));
        storage.downLoad(url,getResponse().getOutputStream());
    }

    /**
     * 删除文件上传
     */
    @PostMapping("remove")
    @HasPermissions("sys:oss:remove")
    public R remove(String ids) {
        if(ids==null){
            return R.error("参数为空!!");
        }
        CloudStorageService storage = OSSFactory.build();
        for(String id:ids.split(",")){
            SysOss sysOss = sysOssService.selectSysOssById(Long.parseLong(id));
            storage.deleteFile(sysOss.getUrl());
        }
        return toAjax(sysOssService.deleteSysOssByIds(ids));
    }


}
