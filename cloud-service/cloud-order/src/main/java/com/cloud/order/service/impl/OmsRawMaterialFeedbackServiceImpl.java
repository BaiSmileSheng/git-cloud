package com.cloud.order.service.impl;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.order.mapper.OmsRawMaterialFeedbackMapper;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.service.IOmsRawMaterialFeedbackService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
/**
 * 原材料反馈信息 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-22
 */
@Service
public class OmsRawMaterialFeedbackServiceImpl extends BaseServiceImpl<OmsRawMaterialFeedback> implements IOmsRawMaterialFeedbackService {
    @Autowired
    private OmsRawMaterialFeedbackMapper omsRawMaterialFeedbackMapper;


    }
