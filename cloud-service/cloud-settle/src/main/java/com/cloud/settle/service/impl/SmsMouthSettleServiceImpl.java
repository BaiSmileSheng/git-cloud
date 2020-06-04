package com.cloud.settle.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.settle.domain.entity.SmsMouthSettle;
import com.cloud.settle.mapper.SmsMouthSettleMapper;
import com.cloud.settle.service.ISmsMouthSettleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 月度结算信息 Service业务层处理
 *
 * @author cs
 * @date 2020-06-04
 */
@Service
public class SmsMouthSettleServiceImpl extends BaseServiceImpl<SmsMouthSettle> implements ISmsMouthSettleService {
    @Autowired
    private SmsMouthSettleMapper smsMouthSettleMapper;


    }
