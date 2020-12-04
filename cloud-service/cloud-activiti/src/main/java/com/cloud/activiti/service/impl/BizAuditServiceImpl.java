/*
 * @(#)BizAuditServiceImpl.java 2020年1月6日 下午3:38:49
 * Copyright 2020 zmr, Inc. All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.activiti.service.impl;

import com.cloud.activiti.domain.BizAudit;
import com.cloud.activiti.mapper.BizAuditMapper;
import com.cloud.activiti.service.IBizAuditService;
import com.cloud.activiti.vo.HiTaskVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>File：BizAuditServiceImpl.java</p>
 * <p>Title: </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2020 2020年1月6日 下午3:38:49</p>
 * <p>Company: zmrit.com </p>
 *
 * @author zmr
 * @version 1.0
 */
@Service
public class BizAuditServiceImpl implements IBizAuditService {
    @Autowired
    private BizAuditMapper auditMapper;

    /**
     * 查询审核记录
     *
     * @param id 审核记录ID
     * @return 审核记录
     */
    @Override
    public BizAudit selectBizAuditById(String id) {
        return auditMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询审核记录列表
     *
     * @param bizAudit 审核记录
     * @return 审核记录
     */
    @Override
    public List<BizAudit> selectBizAuditList(BizAudit bizAudit) {
        return auditMapper.select(bizAudit);
    }

    /**
     * 新增审核记录
     *
     * @param bizAudit 审核记录
     * @return 结果
     */
    @Override
    @GlobalTransactional
    public int insertBizAudit(BizAudit bizAudit) {
        return auditMapper.insertSelective(bizAudit);
    }

    /**
     * 修改审核记录
     *
     * @param bizAudit 审核记录
     * @return 结果
     */
    @Override
    @GlobalTransactional
    public int updateBizAudit(BizAudit bizAudit) {
        return auditMapper.updateByPrimaryKeySelective(bizAudit);
    }

    /**
     * 删除审核记录对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizAuditByIds(String ids) {
        return auditMapper.deleteByIds(ids);
    }

    /**
     * 删除审核记录对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteBizAuditLogic(String ids) {
        String[] idArr = ids.split(",");
        return auditMapper.deleteLogic(idArr);
    }

    /**
     * 删除审核记录信息
     *
     * @param id 审核记录ID
     * @return 结果
     */
    public int deleteBizAuditById(Long id) {
        return auditMapper.deleteByPrimaryKey(id);
    }

    /* (non-Javadoc)
     * @see com.cloud.activiti.service.IBizAuditService#getHistoryTaskList(com.cloud.activiti.vo.HiTaskVo)
     */
    @Override
    public List<HiTaskVo> getHistoryTaskList(HiTaskVo hiTaskVo) {
        return auditMapper.getHistoryTaskList(hiTaskVo);
    }

    @Override
    public List<HiTaskVo> getHistoryTaskList(String tableId, String procDefKey) {
        return auditMapper.getHistoryTaskListForMore(tableId,procDefKey);
    }
}
