package com.cloud.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.core.domain.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.CdSettleRatioMapper;
import com.cloud.system.domain.entity.CdSettleRatio;
import com.cloud.system.service.ICdSettleRatioService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import tk.mybatis.mapper.entity.Example;

/**
 * 结算索赔系数 Service业务层处理
 *
 * @author cs
 * @date 2020-06-04
 */
@Service
public class CdSettleRatioServiceImpl extends BaseServiceImpl<CdSettleRatio> implements ICdSettleRatioService {

    private static Logger logger = LoggerFactory.getLogger(CdSettleRatioServiceImpl.class);

    @Autowired
    private CdSettleRatioMapper cdSettleRatioMapper;

    /**
     * 新增结算索赔系数(先校验此索赔类型是否存在)
     * @param cdSettleRatio 结算索赔系数信息
     * @return 新增主键id
     */
    @Override
    public R addSaveVerifyClaimType(CdSettleRatio cdSettleRatio) {
        logger.info("新增结算索赔系数(先校验此索赔类型是否存在) claimType:{}",cdSettleRatio.getClaimType());
        if(StringUtils.isNotBlank(cdSettleRatio.getClaimType())){
            CdSettleRatio cdSettleRatioRes = selectByClaimType(cdSettleRatio.getClaimType());
            if(null != cdSettleRatioRes){
                logger.error("新增结算索赔系数 此索赔类型已存在 req:{},res:{}",cdSettleRatio.getClaimType(),
                        JSONObject.toJSONString(cdSettleRatioRes));
                return R.error("此索赔类型已存在禁止新增,请修改");
            }
        }
        int count = cdSettleRatioMapper.insertSelective(cdSettleRatio);
        return R.data(cdSettleRatio.getId());
    }

    /**
     * 修改保存结算索赔系数
     * @param cdSettleRatio 结算索赔系数信息
     * @return 修改结果成功或失败
     */
    @Override
    public R updateVerifyClaimType(CdSettleRatio cdSettleRatio) {
        logger.info("修改保存结算索赔系数 id:{}",cdSettleRatio.getId());
        if(StringUtils.isNotBlank(cdSettleRatio.getClaimType())){
            CdSettleRatio cdSettleRatioRes = selectByClaimType(cdSettleRatio.getClaimType());
            if(null != cdSettleRatioRes){
                Boolean flagId = cdSettleRatio.getId().toString().equals(cdSettleRatioRes.getId().toString());
                if(!flagId){
                    logger.error("修改结算索赔系数 此索赔类型已存在 req:{},res:{}",JSONObject.toJSONString(cdSettleRatio),
                            JSONObject.toJSONString(cdSettleRatioRes));
                    return R.error("此索赔类型已存在,禁止修改为此索赔类型");
                }
            }
        }
        cdSettleRatioMapper.updateByPrimaryKeySelective(cdSettleRatio);
        return R.ok();
    }

    /**
     * 根据索赔类型查结算索赔系数
     * @param claimType 索赔类型
     * @return 结算索赔系数信息
     */
    @Override
    public CdSettleRatio selectByClaimType(String claimType){
        Example example = new Example(CdSettleRatio.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("claimType",claimType);
        criteria.andEqualTo("delFlag", DeleteFlagConstants.NO_DELETED);
        return cdSettleRatioMapper.selectOneByExample(example);
    }
}
