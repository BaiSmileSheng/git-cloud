package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdFactoryStorehouseInfo;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


/**
 * 工厂库位 Service接口
 *
 * @author cs
 * @date 2020-06-15
 */
public interface ICdFactoryStorehouseInfoService extends BaseService<CdFactoryStorehouseInfo> {


    /**
     * 根据工厂，客户编码分组取接收库位
     * @param dicts
     * @return
     */
    R selectStorehouseToMap(List<Dict> dicts);

    /**
     * 导入
     * @return
     */
    R importFactoryStorehouse(MultipartFile file, SysUser sysUser)throws IOException;
}
