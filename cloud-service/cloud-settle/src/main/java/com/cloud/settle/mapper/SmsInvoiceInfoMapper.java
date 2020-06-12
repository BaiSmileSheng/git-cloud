package com.cloud.settle.mapper;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.settle.domain.entity.SmsInvoiceInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * 发票信息 Mapper接口
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
public interface SmsInvoiceInfoMapper extends BaseMapper<SmsInvoiceInfo>{

    List<SmsInvoiceInfo> selectByMouthSettleId(@Param("mouthSettleId")String mouthSettleId);



}
