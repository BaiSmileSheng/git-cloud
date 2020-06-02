package com.cloud.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.system.domain.entity.CdMaterialPriceInfo;
import com.cloud.system.mapper.CdMaterialPriceInfoMapper;
import com.cloud.system.service.ICdMaterialPriceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;

/**
 * SAP成本价格 Service业务层处理
 *
 * @author cs
 * @date 2020-05-26
 */
@Service
public class CdMaterialPriceInfoServiceImpl extends BaseServiceImpl<CdMaterialPriceInfo> implements ICdMaterialPriceInfoService {
    @Autowired
    private CdMaterialPriceInfoMapper cdMaterialPriceInfoMapper;

    /**
     * 根据物料号校验价格是否已同步SAP,如果是返回价格信息
     * @param materialCode
     * @return CdMaterialPriceInfo
     */
    @Override
    public R checkSynchroSAP(String materialCode) {
        if (StrUtil.isBlank(materialCode)) {
            throw new BusinessException("参数：物料号为空！");
        }
        String dateStr = DateUtils.getTime();
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        //根据物料号  有效期查询SAP价格
        criteria.andEqualTo("materialCode", materialCode)
                .andLessThanOrEqualTo("beginDate", dateStr)
                .andGreaterThanOrEqualTo("endDate", dateStr);
        List<CdMaterialPriceInfo> materialPrices = selectByExample(example);
        if(materialPrices==null||materialPrices.size()==0){
            throw new BusinessException("物料号未同步SAP价格！");
        }
        CdMaterialPriceInfo materialPrice = materialPrices.get(0);
        if(materialPrice==null||materialPrice.getProcessPrice()==null||materialPrice.getProcessPrice().compareTo(BigDecimal.ZERO)<0){
            throw new BusinessException("物料号未同步SAP价格！");
        }
        return R.data(materialPrice);
    }

    /**
     * 校验申请数量是否是最小包装量的整数倍
     * @param materialCode
     * @param applyNum
     * @return 是否正确
     */
    @Override
    public R checkIsMinUnit(String materialCode, int applyNum) {
        if (StrUtil.isBlank(materialCode)) {
            return R.error("参数：物料号为空！");
        }
        if (applyNum==0) {
            return R.error("参数：申请量为空！");
        }
        String dateStr = DateUtils.getTime();
        Example example = new Example(CdMaterialPriceInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("materialCode",materialCode)
                .andLessThanOrEqualTo("beginDate", dateStr)
                .andGreaterThanOrEqualTo("endDate", dateStr)
                .andEqualTo("delFlag","0");
        CdMaterialPriceInfo cdMaterialPriceInfo = findByExampleOne(example);
        //最小包装量
        int minUnit= Integer.parseInt(cdMaterialPriceInfo.getPriceUnit()==null?"0":cdMaterialPriceInfo.getPriceUnit());
        if(minUnit==0){
            return R.error("最小包装量不正确！");
        }
        if(applyNum%minUnit!=0){
            return R.error("申请量必须是最小包装量的整数倍！");
        }
        return R.ok();
    }
}
