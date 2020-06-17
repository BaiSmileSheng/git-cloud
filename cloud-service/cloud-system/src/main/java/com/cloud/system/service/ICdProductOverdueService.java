package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.common.core.service.BaseService;

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
     * @param list
     * @return
     */
    R importFactoryStorehouse(List<CdProductOverdue> list);
}
