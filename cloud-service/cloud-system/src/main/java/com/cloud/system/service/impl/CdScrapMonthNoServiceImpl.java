package com.cloud.system.service.impl;

    import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdScrapMonthNo;
import com.cloud.system.mapper.CdScrapMonthNoMapper;
import com.cloud.system.service.ICdScrapMonthNoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 报废每月单号Service业务层处理
 *
 * @author cs
 * @date 2020-09-25
 */
@Service
public class CdScrapMonthNoServiceImpl extends BaseServiceImpl<CdScrapMonthNo> implements ICdScrapMonthNoService {
    @Autowired
    private CdScrapMonthNoMapper cdScrapMonthNoMapper;


    }
