package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdProductPassageMapper;
import com.cloud.system.domain.entity.CdProductPassage;
import com.cloud.system.service.ICdProductPassageService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 成品库存在途明细 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-12
 */
@Service
public class CdProductPassageServiceImpl extends BaseServiceImpl<CdProductPassage> implements ICdProductPassageService {
    @Autowired
    private CdProductPassageMapper cdProductPassageMapper;

    /**
     * 删除全表
     * @return
     */
    @Override
    public R deleteAll() {
        cdProductPassageMapper.deleteAll();
        return null;
    }

    @Override
    public R selectByList(List<CdProductPassage> list) {
        return R.data(cdProductPassageMapper.selectByList(list));
    }
}
