package com.cloud.system.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdMaterialInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物料信息 Mapper接口
 *
 * @author ltq
 * @date 2020-06-01
 */
public interface CdMaterialInfoMapper extends BaseMapper<CdMaterialInfo> {
    void updateBatchByFactoryAndMaterial(@Param(value = "list") List<CdMaterialInfo> list);

    /**
     * 根据物料号集合查询物料信息
     *
     * @param materialCodes
     * @return
     */
    List<CdMaterialInfo> selectInfoByInMaterialCodeAndMaterialType(@Param(value = "materialCodes") List<String> materialCodes, @Param("materialType") String materialType);


    List<CdMaterialInfo> selectListByMaterialList(@Param(value = "list") List<Dict> list);

    /**
     * Description:  根据生产工厂、物料号批量删除
     * Param: List<CdMaterialInfo> list
     * return:  void
     * Author: ltq
     * Date: 2020/7/1
     */
    void deleteBatchByFactoryAndMaterial(@Param(value = "list") List<CdMaterialInfo> list);

    /**
     * Description:批量插入或更新
     * Param:
     * return:
     * Author: ltq
     * Date: 2020/7/7
     */
    int batchInsetOrUpdate(List<CdMaterialInfo> list);

    /**
     * 按物料号批量查询
     * @param list
     * @return
     */
    List<CdMaterialInfo> selectListByMaterialCodeList(@Param(value = "list") List<Dict> list);

    List<String> selectByMaterialCode(@Param("materialCode") String materialCode);
}
