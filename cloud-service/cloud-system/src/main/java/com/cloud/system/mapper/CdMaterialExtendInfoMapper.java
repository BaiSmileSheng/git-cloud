package com.cloud.system.mapper;

import cn.hutool.core.lang.Dict;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物料扩展信息 Mapper接口
 *
 * @author lihongia
 * @date 2020-06-15
 */
public interface CdMaterialExtendInfoMapper extends BaseMapper<CdMaterialExtendInfo>{

    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
    List<String> selectMaterialCodeByLifeCycle(@Param("lifeCycle")String lifeCycle);


    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    @MapKey("materialCode")
    Map<String, CdMaterialExtendInfo> selectInfoInMaterialCodes(@Param(value = "materialCodes") List<String> materialCodes);
    /**
     * Description:  根据物料List查询
     * Param:
     * return:
     * Author: ltq
     * Date: 2020/6/23
     */
    List<CdMaterialExtendInfo> selectByMaterialCodeList(@Param(value = "list") List<Dict> list);
    /**
    * Description:  根据物料查询一条数据
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/6/23
    */
    CdMaterialExtendInfo selectOneByMaterialCode(@Param("materialCode")String materialCode);

    /**
     * 增量更新
     * @param list
     * @return
     */
    int batchInsertOrUpdate(List<CdMaterialExtendInfo> list);

    /**
     * 增量更新仅更新时间和更新人
     * @param list
     * @return
     */
    int batchMaterialInsertOrUpdate(List<CdMaterialExtendInfo> list);
}
