package com.cloud.system.service.impl;

import com.cloud.common.constant.UserConstants;
import com.cloud.common.core.domain.Ztree;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.SysDataScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.system.mapper.SysDataScopeMapper;
import com.cloud.system.service.ISysDataScopeService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 *  数据权限Service业务层处理
 *
 * @author cs
 * @date 2020-05-02
 */
@Service
public class SysDataScopeServiceImpl extends BaseServiceImpl<SysDataScope> implements ISysDataScopeService {
    @Autowired
    private SysDataScopeMapper sysDataScopeMapper;


}
