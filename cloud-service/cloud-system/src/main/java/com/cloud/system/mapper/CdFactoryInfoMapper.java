package com.cloud.system.mapper;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.system.domain.entity.CdFactoryInfo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工厂信息 Mapper接口
 *
 * @author cs
 * @date 2020-06-03
 */
public interface CdFactoryInfoMapper extends BaseMapper<CdFactoryInfo>{

    /**
     * 根据公司V码查询
     * @param companyCodeV
     * @return
     */
    @MapKey("companyCodeV")
    Map<String, CdFactoryInfo> selectAllByCompanyCodeV(@Param("companyCodeV")String companyCodeV);


    /**
     *  获取所有工厂编码
     * @return
     */
    List<String> selectAllFactoryCode();



}
