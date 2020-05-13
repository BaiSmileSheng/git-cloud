package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.file.OssException;
import com.cloud.common.utils.file.FileUtils;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.oss.*;
import com.cloud.system.service.ISysOssService;
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
    @Autowired
    private ISysOssService sysOssService;

    @Autowired
    private CloudStorageConfig cloudStorageConfig;

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
     */
    @PostMapping("upload")
    @HasPermissions("sys:oss:add")
    public R editSave(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new OssException("上传文件不能为空");
        }
        // 上传文件
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        HuawCloudStorageService storage = new HuawCloudStorageService(cloudStorageConfig);
        String url = storage.upload(file);
        // 保存文件信息
        SysOss ossEntity = new SysOss();
        ossEntity.setUrl(url);
        ossEntity.setFileSuffix(suffix);
        ossEntity.setCreateBy(getLoginName());
        ossEntity.setFileName(fileName);
        ossEntity.setCreateTime(new Date());
        ossEntity.setService(1);
        sysOssService.insertSysOss(ossEntity);
        return R.data(url);
    }

    /**
     * 下载文件
     *
     */
    @PostMapping("downLoad")
    public void downLoad(String fileName) throws IOException {
        // 下载文件
        HuawCloudStorageService storage = new HuawCloudStorageService(cloudStorageConfig);
        getResponse().setCharacterEncoding("utf-8");
        // 下载使用"application/octet-stream"更标准
        getResponse().setContentType("application/octet-stream");
        getResponse().setHeader("Content-Disposition",
                "attachment;filename=" + FileUtils.setFileDownloadHeader(getRequest(), fileName));
        storage.downLoad(fileName,getResponse().getOutputStream());
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
        HuawCloudStorageService storage = new HuawCloudStorageService(cloudStorageConfig);
        for(String id:ids.split(",")){
            SysOss sysOss = sysOssService.selectSysOssById(Long.parseLong(id));
            storage.deleteFile(sysOss.getUrl());
        }
        return toAjax(sysOssService.deleteSysOssByIds(ids));
    }
}
