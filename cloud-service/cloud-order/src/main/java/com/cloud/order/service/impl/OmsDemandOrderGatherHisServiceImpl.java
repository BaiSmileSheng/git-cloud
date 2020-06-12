package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.OmsDemandOrderGatherHis;
import com.cloud.order.mapper.OmsDemandOrderGatherHisMapper;
import com.cloud.order.service.IOmsDemandOrderGatherHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 滚动计划需求历史 Service业务层处理
 *
 * @author cs
 * @date 2020-06-12
 */
@Service
public class OmsDemandOrderGatherHisServiceImpl extends BaseServiceImpl<OmsDemandOrderGatherHis> implements IOmsDemandOrderGatherHisService {
    @Autowired
    private OmsDemandOrderGatherHisMapper omsDemandOrderGatherHisMapper;


    }
