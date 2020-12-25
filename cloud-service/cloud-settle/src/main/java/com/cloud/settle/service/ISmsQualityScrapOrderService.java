package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityScrapOrder;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsQualityScrapOrderLog;
import com.cloud.system.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 质量报废Service接口
 *
 * @author ltq
 * @date 2020-12-10
 */
public interface ISmsQualityScrapOrderService extends BaseService<SmsQualityScrapOrder> {
    /**
     * 质量部报废新增
     * @author ltq
     * @date 2020-12-07
     */
    R insertQualityScrap(SmsQualityScrapOrder smsQualityScrapOrder, SysUser sysUser);

    /**
     * 质量部报废新增-多条
     * @author ltq
     * @date 2020-12-07
     */
    R insertQualityScrapList(List<SmsQualityScrapOrder> smsQualityScrapOrders, SysUser sysUser);

    /**
     * 质量部报废更新
     * @author ltq
     * @date 2020-12-07
     */
    R updateQualityScrap(SmsQualityScrapOrder smsQualityScrapOrder, SysUser sysUser);
    /**
     * 质量部报废删除
     * @param ids
     * @return R
     */
    R remove(String ids);

    /**
     * 质量部报废提交
     * @param id
     * @return R
     */
    R commitQualityScrap(String id,SysUser sysUser);
    /**
     * 定时更新质量部报废订单价格
     */
    R updatePriceJob();

    List<SmsQualityScrapOrder> selectByMonthAndStatus(String month,List<String> scrapStatus);

    R confirm(String ids,SysUser sysUser);

    List<Map<String,String>> selectMaterialAndCompanyCodeGroupBy(String month,List<String> scrapStatus);

    R appealSupplier(Long id,String complaintDescription, String ossIds,SysUser sysUser);
    /**
     * 审批流更新业务数据
     */
    R updateAct(SmsQualityScrapOrder smsQualityScrapOrder,Integer result,String comment,String auditor);

    R selectDeatils(Long id);
}
