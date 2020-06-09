package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsClaimCashDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 索赔兑现明细 Mapper接口
 *
 * @author cs
 * @date 2020-06-05
 */
public interface SmsClaimCashDetailMapper extends BaseMapper<SmsClaimCashDetail> {
    /**
     * 本月兑现扣款
     * @param settleNo
     * @return
     */
    List<Map<String, BigDecimal>> selectSumCashGroupByClaimTypeActual(@Param("settleNo") String settleNo);
    /**
     * 历史兑现扣款
     * @param settleNo
     * @return
     */
    List<Map<String, BigDecimal>> selectSumCashGroupByClaimTypeHistory(@Param("settleNo") String settleNo);
}
