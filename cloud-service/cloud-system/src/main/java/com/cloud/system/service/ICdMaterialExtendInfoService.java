package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.common.core.service.BaseService;

/**
 * 物料扩展信息 Service接口
 *
 * @author lihongia
 * @date 2020-06-15
 */
public interface ICdMaterialExtendInfoService extends BaseService<CdMaterialExtendInfo> {

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    R timeSycMaterialCode();
}
