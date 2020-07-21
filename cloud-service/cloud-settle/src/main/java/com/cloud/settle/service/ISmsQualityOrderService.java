package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.system.domain.entity.SysUser;

import java.util.List;

/**
 * 质量索赔 Service接口
 *
 * @author cs
 * @date 2020-05-27
 */
public interface ISmsQualityOrderService extends BaseService<SmsQualityOrder> {

    /**
     * 查询质量索赔详情
     * @param id 主键id
     * @return 索赔信息(包括文件信息)
     */
    R selectById(Long id);

    /**
     * 新增质量索赔信息
     * @param smsQualityOrder 质量索赔信息
     * @param ossIds 文件id
     * @return
     */
    R addSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, String ossIds);

    /**
     * 修改质量索赔信息
     * @param smsQualityOrder 质量索赔信息
     * @param ossIds 质量索赔对应的文件信息
     * @return
     */
    R updateSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, String ossIds);

    /**
     * 新增或修改时提交质量索赔信息
     * @param smsQualityOrder 质量索赔信息
     * @param ossIds 质量索赔对应的文件信息
     * @return
     */
    R insertOrupdateSubmit(SmsQualityOrder smsQualityOrder, String ossIds);


    /**
     * 删除质量索赔信息
     * @param ids 主键id
     * @return 删除结果成功或失败
     */
    R deleteSmsQualityOrderAndSysOss(String ids);

    /**
     * 根据索赔单主键批量查询
     * @param ids 主键
     * @return 索赔单集合
     */
    List<SmsQualityOrder> selectListById(String ids);

    /**
     * 提交索赔单
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    R submit(String ids);

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    R supplierConfirm(String ids, SysUser sysUser);

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param smsQualityOrder 质量索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    R supplierAppeal(SmsQualityOrder smsQualityOrder,String ossIds);

    /**
     * 超时发送邮件
     * @return 成功或失败
     */
    R overTimeSendMail();

    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    R overTimeConfim();
}
