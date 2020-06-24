package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEditHis;
import com.cloud.order.mapper.OmsDemandOrderGatherEditHisMapper;
import com.cloud.order.service.IOmsDemandOrderGatherEditHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 滚动计划需求操作历史 Service业务层处理
 *
 * @author cs
 * @date 2020-06-16
 */
@Service
public class OmsDemandOrderGatherEditHisServiceImpl extends BaseServiceImpl<OmsDemandOrderGatherEditHis> implements IOmsDemandOrderGatherEditHisService {
    @Autowired
    private OmsDemandOrderGatherEditHisMapper omsDemandOrderGatherEditHisMapper;


    }
