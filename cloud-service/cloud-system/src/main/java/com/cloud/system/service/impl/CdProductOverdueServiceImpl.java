package com.cloud.system.service.impl;

import com.cloud.common.core.domain.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdProductOverdueMapper;
import com.cloud.system.domain.entity.CdProductOverdue;
import com.cloud.system.service.ICdProductOverdueService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 超期库存 Service业务层处理
 *
 * @author lihongxia
 * @date 2020-06-17
 */
@Service
public class CdProductOverdueServiceImpl extends BaseServiceImpl<CdProductOverdue> implements ICdProductOverdueService {
    @Autowired
    private CdProductOverdueMapper cdProductOverdueMapper;

    /**
     * 导入数据 先根据创建人删除再新增
     * @param list
     * @return
     */
    @Transactional
    @Override
    public R importFactoryStorehouse(List<CdProductOverdue> list) {

        if(!CollectionUtils.isEmpty(list)){
            String createBy = list.get(0).getCreateBy();
            if(StringUtils.isBlank(createBy)){
                return R.error("创建人不能为空");
            }
            Example example = new Example(CdProductOverdue.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("createBy",createBy);
            cdProductOverdueMapper.deleteByExample(example);
            cdProductOverdueMapper.insertList(list);
        }
        return R.ok();
    }
}
