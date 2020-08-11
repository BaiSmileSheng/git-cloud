package com.cloud.order.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.OmsDemandOrderGatherEdit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 滚动计划需求操作 Mapper接口
 *
 * @author cs
 * @date 2020-06-16
 */
public interface OmsDemandOrderGatherEditMapper extends BaseMapper<OmsDemandOrderGatherEdit>{

    /**
     * 根据创建人和客户编码删除
     * @param createBy
     * @param customerCodes
     * @return
     */
    int deleteByCreateByAndCustomerCode(@Param("createBy")String createBy,@Param("customerCodes") List<String> customerCodes,@Param("status")String status);

    /**
     * 查询不重复的物料号和工厂
     * @param omsDemandOrderGatherEdit
     * @return
     */
    List<OmsDemandOrderGatherEdit> selectDistinctMaterialCodeAndFactoryCode(@Param("dto")OmsDemandOrderGatherEdit omsDemandOrderGatherEdit);

    /**
     * 根据物料号和工厂查询
     * @param list
     * @return
     */
    List<OmsDemandOrderGatherEdit> selectInfoInMaterialCodeAndFactoryCode(List<OmsDemandOrderGatherEdit> list);

    /**
     * 根据需求订单号批量更新
     * @param list
     * @return
     */
    int updateBatchByDemandOrderCode(@Param("list")List<OmsDemandOrderGatherEdit> list);

}
