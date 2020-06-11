package com.cloud.settle.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsQualityTypeMapper;
import com.cloud.settle.domain.entity.SmsQualityType;
import com.cloud.settle.service.ISmsQualityTypeService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 质量索赔扣款类型和扣款标准Service业务层处理
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@Service
public class SmsQualityTypeServiceImpl extends BaseServiceImpl<SmsQualityType> implements ISmsQualityTypeService {
    @Autowired
    private SmsQualityTypeMapper smsQualityTypeMapper;


}
