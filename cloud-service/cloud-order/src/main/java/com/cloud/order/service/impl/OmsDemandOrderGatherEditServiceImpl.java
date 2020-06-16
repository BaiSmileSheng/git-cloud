package com.cloud.order.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import com.cloud.order.mapper.OmsDemandOrderGatherEditMapper;
import com.cloud.order.service.IOmsDemandOrderGatherEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 滚动计划需求操作 Service业务层处理
 *
 * @author cs
 * @date 2020-06-16
 */
@Service
public class OmsDemandOrderGatherEditServiceImpl extends BaseServiceImpl<OmsDemandOrderGatherEdit> implements IOmsDemandOrderGatherEditService {
    @Autowired
    private OmsDemandOrderGatherEditMapper omsDemandOrderGatherEditMapper;


    }
