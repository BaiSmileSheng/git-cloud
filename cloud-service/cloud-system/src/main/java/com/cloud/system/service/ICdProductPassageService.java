package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.common.core.service.BaseService;

import java.util.List;

/**
 * 成品库存在途明细 Service接口
 *
 * @author lihongxia
 * @date 2020-06-12
 */
public interface ICdProductPassageService extends BaseService<CdProductPassage> {

    /**
     * 删除全表
     * @return
     */
    R deleteAll();
    /**
     * 根据生产工厂、成品专用号、库位查询在途
     * @return list
     */
    R selectByList(List<CdProductPassage> list);
}
