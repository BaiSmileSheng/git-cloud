package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.common.core.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 超期库存 Service接口
 *
 * @author lihongxia
 * @date 2020-06-17
 */
public interface ICdProductOverdueService extends BaseService<CdProductOverdue> {

    /**
     * 导入数据 先根据创建人删除再新增
     * @param file
     * @return
     */
    R importFactoryStorehouse(MultipartFile file, long loginId) throws IOException;
}
