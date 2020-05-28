/*
 * @(#)ActBusinessMapper.java 2020年1月6日 下午3:38:12
 * Copyright 2020 zmr, Inc. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.activiti.mapper;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.vo.HiTaskVo;
import com.cloud.common.core.dao.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>File：BizAuditMapper.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:12</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
public interface BizAuditMapper extends BaseMapper<BizAudit> {
    List<HiTaskVo> getHistoryTaskList(HiTaskVo hiTaskVo);

    /**
     * task 流转历史  一个订单有多次审核
     * @param tableId
     * @param procDefKey
     * @return
     */
    List<HiTaskVo> getHistoryTaskListForMore(@Param("tableId") String tableId, @Param("procDefKey") String procDefKey);

    /**
     * logic删除
     *
     * @param ids
     * @return
     * @author zmr
     */
    int deleteLogic(String[] ids);
}
