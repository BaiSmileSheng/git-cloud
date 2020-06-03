package com.cloud.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdBom;
import com.cloud.system.mapper.CdBomMapper;
import com.cloud.system.service.ICdBomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

/**
 * bom清单数据 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Service
public class CdBomServiceImpl extends BaseServiceImpl<CdBom> implements ICdBomService {
    @Autowired
    private CdBomMapper cdBomMapper;

    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R 单耗
     */
    @Override
    public R checkBomNum(String productMaterialCode, String rawMaterialCode, int applyNum) {
        if (StrUtil.isBlank(productMaterialCode)) {
            return R.error("参数：成品物料号为空！");
        }
        if (StrUtil.isBlank(rawMaterialCode)) {
            return R.error("参数：原材料物料号为空！");
        }
        if (applyNum==0) {
            return R.error("参数：申请量为空！");
        }
        Example example = new Example(CdBom.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",productMaterialCode);
        criteria.andEqualTo("rawMaterialCode",rawMaterialCode);
        criteria.andEqualTo("delFlag","0");
        CdBom cdBom = findByExampleOne(example);
        int bomNum = cdBom.getBomNum().intValue();//单耗
        if(applyNum%bomNum!=0){
            return R.error("申请量必须是单耗的整数倍！");
        }
        return R.data(bomNum);
    }
}
