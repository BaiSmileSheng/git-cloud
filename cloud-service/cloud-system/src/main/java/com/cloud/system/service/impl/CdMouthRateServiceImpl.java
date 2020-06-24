package com.cloud.system.service.impl;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdMouthRate;
import com.cloud.system.mapper.CdMouthRateMapper;
import com.cloud.system.service.ICdMouthRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 汇率Service业务层处理
 *
 * @author cs
 * @date 2020-05-27
 */
@Service
public class CdMouthRateServiceImpl extends BaseServiceImpl<CdMouthRate> implements ICdMouthRateService {
    @Autowired
    private CdMouthRateMapper cdMouthRateMapper;


	/**
	 * 根据月份查询汇率
	 * @param yearMouth
	 * @return rate
	 */
	@Override
	public BigDecimal findRateByYearMouth(String yearMouth){
		return cdMouthRateMapper.findRateByYearMouth(yearMouth);
	}
}
