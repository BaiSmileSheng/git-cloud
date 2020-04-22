package com.cloud.activiti.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.activiti.mapper.HistoryMapper;
import com.cloud.activiti.service.IHistoryInfoService;
import com.cloud.activiti.vo.HiProcInsVo;

/**
 * @Auther: Ace Lee
 * @Date: 2019/3/7 16:55
 */
@Service
public class HistoryInfoServiceImpl implements IHistoryInfoService {
    @Autowired
    private HistoryMapper historyMapper;

    /* (non-Javadoc)
     * @see com.cloud.activiti.service.IHistoryInfoService#getHiProcInsListDone(com.cloud.activiti.vo.HiProcInsVo)
     */
    @Override
    public List<HiProcInsVo> getHiProcInsListDone(HiProcInsVo hiProcInsVo) {
        return historyMapper.getHiProcInsListDone(hiProcInsVo);
    }
}
