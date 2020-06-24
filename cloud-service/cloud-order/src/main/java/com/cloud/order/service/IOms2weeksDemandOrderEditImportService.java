package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

/**
 * T+1-T+2周需求导入 Service接口
 *
 * @author cs
 * @date 2020-06-22
 */
public interface IOms2weeksDemandOrderEditImportService extends BaseService<Oms2weeksDemandOrderEdit> {
    /**
     * T+1、T+2草稿计划导入
     *
     * @param file
     * @param sysUser
     * @return
     */
    R import2weeksDemandGatherEdit(MultipartFile file, SysUser sysUser);
}
