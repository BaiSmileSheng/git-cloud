package com.cloud.settle.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsClaimOther;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 其他索赔Mapper接口
 *
 * @author cs
 * @date 2020-06-02
 */
public interface SmsClaimOtherMapper extends BaseMapper<SmsClaimOther> {
    List<SmsClaimOther> selectByMonthAndStatus(@Param("month") String month, @Param("claimOtherStatus") List<String> claimOtherStatus);
}
