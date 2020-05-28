package com.cloud.settle.service;

import com.cloud.common.core.domain.R;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.common.core.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

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
     * @param files 质量索赔对应的文件信息
     * @return
     */
    R addSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, MultipartFile[] files);

    /**
     * 修改质量索赔信息
     * @param smsQualityOrder 质量索赔信息
     * @param files 质量索赔对应的文件信息
     * @return
     */
    R updateSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, MultipartFile[] files);

    /**
     * 删除质量索赔信息
     * @param ids 主键id
     * @param smsQualityOrderList
     * @return 删除结果成功或失败
     */
    R deleteSmsQualityOrderAndSysOss(String ids,List<SmsQualityOrder> smsQualityOrderList);

    /**
     * 根据索赔单主键批量查询
     * @param ids
     * @return
     */
    List<SmsQualityOrder> selectListById(String ids);
}
