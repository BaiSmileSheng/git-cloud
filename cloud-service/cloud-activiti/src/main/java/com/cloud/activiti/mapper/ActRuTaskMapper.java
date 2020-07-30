package com.cloud.activiti.mapper;

import java.util.List;

import com.cloud.activiti.domain.ActRuTask;
import org.apache.ibatis.annotations.Param;

public interface ActRuTaskMapper {
    int deleteByPrimaryKey(String id);

    int insert(ActRuTask record);

    int insertSelective(ActRuTask record);

    ActRuTask selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ActRuTask record);

    int updateByPrimaryKey(ActRuTask record);

    List<ActRuTask> selectAll();

    List<ActRuTask> selectByProcInstId(List<String> list);
}