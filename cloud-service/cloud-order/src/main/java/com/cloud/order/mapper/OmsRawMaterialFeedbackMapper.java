package com.cloud.order.mapper;

import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.common.core.dao.BaseMapper;
import com.cloud.order.domain.entity.vo.OmsRawMaterialFeedbackVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 原材料反馈信息 Mapper接口
 *
 * @author ltq
 * @date 2020-06-22
 */
public interface OmsRawMaterialFeedbackMapper extends BaseMapper<OmsRawMaterialFeedback>{
    /**
    * Description:  根据成品专用号、生产工厂、开始日期、bom版本查询未审核记录
    * Param:  list
    * return:  list
    * Author: ltq
    * Date: 2020/7/1
    */
    List<OmsRawMaterialFeedback> selectByList(@Param(value = "list") List<OmsRawMaterialFeedback> list);
    /**
     * Description:根据生产工厂、原材料物料、开始日期更新反馈信息状态为“未审核已确认”
     * Param: [list]
     * return: void
     * Author: ltq
     * Date: 2020/7/1
     */
    void updateBatchByList(@Param(value = "list") List<OmsRawMaterialFeedback> list);
    /**
     * Description:根据ID更新反馈信息状态
     * Param: updatedStatus,id
     * return: int
     * Author: ltq
     * Date: 2020/8/6
     */
    int updateStatusById(@Param("updatedStatus")String updatedStatus,@Param("id")Long id);



}
