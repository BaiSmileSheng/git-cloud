package com.cloud.order.mapper;

import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.Oms2weeksDemandOrderEdit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * T+1-T+2周需求导入 Mapper接口
 *
 * @author cs
 * @date 2020-06-22
 */
public interface Oms2weeksDemandOrderEditMapper extends BaseMapper<Oms2weeksDemandOrderEdit>{
    /**
     * 根据创建人和客户编码删除
     * @param createBy
     * @param customerCodes
     * @return
     */
    int deleteByCreateByAndCustomerCode(@Param("createBy")String createBy, @Param("customerCodes") List<String> customerCodes,@Param("status")String status);

    /**
     * 查询不重复的物料号和工厂
     * @param oms2weeksDemandOrderEdit
     * @return
     */
    List<Oms2weeksDemandOrderEdit> selectDistinctMaterialCodeAndFactoryCode(@Param("dto")Oms2weeksDemandOrderEdit oms2weeksDemandOrderEdit);
    /**
     * 根据物料号和工厂查询
     * @param list
     * @return
     */
    List<Oms2weeksDemandOrderEdit> selectInfoInMaterialCodeAndFactoryCode(List<Oms2weeksDemandOrderEdit> list);

    /**
     * 根据需求订单号批量更新
     * @param list
     * @return
     */
    int updateBatchByDemandOrderCode(@Param("list")List<Oms2weeksDemandOrderEdit> list);

}
