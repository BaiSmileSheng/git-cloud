package com.cloud.system.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.mapper.CdBomInfoMapper;
import com.cloud.system.service.ICdBomInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * bom清单数据 Service业务层处理
 *
 * @author cs
 * @date 2020-06-01
 */
@Service
public class CdBomInfoInfoServiceImpl extends BaseServiceImpl<CdBomInfo> implements ICdBomInfoService {
    @Autowired
    private CdBomInfoMapper cdBomInfoMapper;

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
        Example example = new Example(CdBomInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",productMaterialCode);
        criteria.andEqualTo("rawMaterialCode",rawMaterialCode);
        CdBomInfo cdBomInfo = findByExampleOne(example);
        if (cdBomInfo ==null|| cdBomInfo.getBomNum() == null) {
            return R.error("未维护BOM！");
        }
        int bomNum = cdBomInfo.getBomNum().intValue();//单耗
        if(applyNum%bomNum!=0){
            return R.error("申请量必须是单耗的整数倍！");
        }
        return R.data(bomNum);
    }

    /**
     * 1、获取调用SAP系统获取BOM清单接口的入参
     * 2、组织接口入参数据
     * 3、调用获取BOM清单数据接口
     * 4、执行保存BOM清单数据sql
     *
     * @Description: 定时任务获取BOM清单-保存
     * @Param: []
     * @return: com.cloud.common.core.domain.R
     * @Author: ltq
     * @Date: 2020/6/8
     */
    @Override
    public R saveBomInfoBySap() {
        //TODO:
        //1、获取调用SAP系统获取BOM清单接口的入参
        //2、组织接口入参数据
        //3、调用获取BOM清单数据接口
        //4、执行保存BOM清单数据sql
        return null;
    }
    /**
     * 根据物料号工厂分组取bom版本
     * @return
     */
    @Override
    public Map<String,Map<String, String>> selectVersionMap(List<Dict> dicts) {
        return cdBomInfoMapper.selectVersionMap(dicts);
    }
}
