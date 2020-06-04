package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.common.core.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 其他索赔Service接口
 *
 * @author cs
 * @date 2020-06-02
 */
public interface ISmsClaimOtherService extends BaseService<SmsClaimOther> {

    /**
     * 查询其他索赔(包含文件信息)
     * @param id 主键
     * @return 成功或失败
     */
    R selectById(Long id);

    /**
     * 新增其他索赔信息(包含文件信息)
     * @param smsClaimOther 其他索赔信息
     * @param files 文件信息
     * @return 新增结果
     */
    R insertClaimOtherAndOss(SmsClaimOther smsClaimOther, MultipartFile[] files);

    /**
     * 修改保存其他索赔(包含图片信息)
     * @param smsClaimOther  其他索赔信息
     * @param files 文件信息
     * @return 修改成功或失败
     */
    R updateClaimOtherAndOss(SmsClaimOther smsClaimOther, MultipartFile[] files);

    /**
     * 删除其他索赔
     * @param ids 主键
     * @return 成功或失败
     */
    R deleteClaimOtherAndOss(String ids);

    /**
     * 提交其他索赔单
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    R submit(String ids);

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    R supplierConfirm(String ids);

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param smsClaimOther 其他索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    R supplierAppeal(SmsClaimOther smsClaimOther,MultipartFile[] files);
}
