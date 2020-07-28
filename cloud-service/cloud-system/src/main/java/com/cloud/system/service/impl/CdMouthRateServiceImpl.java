package com.cloud.system.service.impl;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.CdMouthRate;
import com.cloud.system.mapper.CdMouthRateMapper;
import com.cloud.system.service.ICdMouthRateService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

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


    @Override
    public R insertMouthRate(CdMouthRate cdMouthRate) {
        Example example = new Example(CdMouthRate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("yearMouth", cdMouthRate.getYearMouth());
        criteria.andEqualTo("currency", cdMouthRate.getCurrency());
        criteria.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        List<CdMouthRate> cdMouthRateList = cdMouthRateMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(cdMouthRateList)){
            throw new BusinessException("同一月份同一原币类型仅能维护一个汇率");
        }
        int count = cdMouthRateMapper.insertSelective(cdMouthRate);
        return R.data(cdMouthRate.getId());
    }

    @Override
    public R updateMouthRate(CdMouthRate cdMouthRate) {
        Date now = new Date();
        String nowDate = DateFormatUtils.format(now, "yyyyMM");
        if(!nowDate.equals(cdMouthRate.getYearMouth())){
            throw new BusinessException("仅可修改当月汇率");
        }
        Example example = new Example(CdMouthRate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("yearMouth", cdMouthRate.getYearMouth());
        criteria.andEqualTo("currency", cdMouthRate.getCurrency());
        criteria.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        List<CdMouthRate> cdMouthRateList = cdMouthRateMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(cdMouthRateList)){
            cdMouthRateList.forEach(cdMouthRate1 -> {
                if(!cdMouthRate1.getId().equals(cdMouthRate.getId())){
                    throw new BusinessException("同一月份同一原币类型仅能维护一个汇率");
                }
            });
        }
        int count = cdMouthRateMapper.updateByPrimaryKeySelective(cdMouthRate);
        return R.data(count);
    }
}
