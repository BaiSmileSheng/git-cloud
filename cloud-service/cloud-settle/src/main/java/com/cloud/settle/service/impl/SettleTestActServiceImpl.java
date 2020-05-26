package com.cloud.settle.service.impl;

import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SettleTestAct;
import com.cloud.settle.mapper.SettleTestActMapper;
import com.cloud.settle.service.ISettleTestActService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 测试审批流Service业务层处理
 *
 * @author cs
 * @date 2020-05-20
 */
@Service
public class SettleTestActServiceImpl extends BaseServiceImpl<SettleTestAct> implements ISettleTestActService {
    @Autowired
    private SettleTestActMapper settleTestActMapper;


}
