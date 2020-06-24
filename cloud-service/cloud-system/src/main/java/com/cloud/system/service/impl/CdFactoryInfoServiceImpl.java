package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.mapper.CdFactoryInfoMapper;
import com.cloud.system.service.ICdFactoryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 工厂信息 Service业务层处理
 *
 * @author cs
 * @date 2020-06-03
 */
@Service
public class CdFactoryInfoServiceImpl extends BaseServiceImpl<CdFactoryInfo> implements ICdFactoryInfoService {

    @Autowired
    private CdFactoryInfoMapper cdFactoryInfoMapper;

    /**
     * 根据公司V码查询
     * @param companyCodeV
     * @return
     */
	@Override
	public R selectAllByCompanyCodeV(String companyCodeV){
		 return R.data(cdFactoryInfoMapper.selectAllByCompanyCodeV(companyCodeV));
	}

	/**
	 * 查询所有公司编码
	 * @return
	 */
	@Override
	public R selectAllCompanyCode(){
		 return R.data(cdFactoryInfoMapper.selectAllCompanyCode());
	}









    }
