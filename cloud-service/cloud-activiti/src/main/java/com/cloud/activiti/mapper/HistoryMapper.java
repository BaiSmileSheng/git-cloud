package com.cloud.activiti.mapper;

import java.util.List;

import com.cloud.activiti.vo.HiProcInsVo;

public interface HistoryMapper {
    List<HiProcInsVo> getHiProcInsListDone(HiProcInsVo hiProcInsVo);
}