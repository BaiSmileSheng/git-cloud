package com.cloud.system.service.impl;

import com.cloud.common.exception.BusinessException;
import com.cloud.system.mapper.SequenceMapper;
import com.cloud.system.service.ISequeceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 ** 获取序列号 Service接口
 * @Author Lihongxia
 * @Date 2020-05-28
 */
@Service
public class SequeceServiceImpl implements ISequeceService {

    @Autowired
    private SequenceMapper sequenceMapper;

    /**
     * 获取序列号
     * @param seqName  序列名称
     * @param length 序列号长度
     * @return 序列号
     */
    @Override
    public String selectSeq(String seqName,int length) {

        int seq = sequenceMapper.selectSeq(seqName);
        String seqStr =  getFixedLengthString(String.valueOf(seq), length);
        return seqStr;
    }

    /**
     * 获取固定长度 序列号
     * @param raw
     * @param length
     * @return
     */
    private String getFixedLengthString(String raw, int length) {
        if (raw == null){
            throw new BusinessException("查询序列异常");
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i=0; i<length; i++){
            stringBuffer.append("0");
        }
        stringBuffer.append(raw);
        String seqAll = stringBuffer.toString();
        String seq = seqAll.substring(seqAll.length()-length);
        return seq;
    }
}


