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

//    /**
//     * 查询 数据权限树列表
//     *
//     * @return 所有 数据权限信息
//     */
//    @Override
//    public List<Ztree> selectSysDataScopeTree(SysDataScope sysDataScope) {
//        Example example = new Example(SysDataScope.class);
//        example.and().andEqualTo(sysDataScope).andLike("materialDesc", sysDataScope.getMaterialDesc())
//                .andEqualTo("delFlag", 0).andEqualTo("status",0);
//        List<SysDataScope> sysDataScopeList = sysDataScopeMapper.selectByExample(example);
//        List<Ztree> ztrees = initZtree(sysDataScopeList);
//        return ztrees;
//    }
//
//    /**
//     * 对象转部门树
//     *
//     * @param sysDataScopeList 部门列表
//     * @return 树结构列表
//     */
//    public List<Ztree> initZtree(List<SysDataScope> sysDataScopeList) {
//        return initZtree(sysDataScopeList, null);
//    }
//
//    /**
//     * 对象转部门树
//     *
//     * @param sysDataScopeList      数据权限列表
//     * @param userScopeList 用户所拥有的的权限
//     * @return 树结构列表
//     */
//    public List<Ztree> initZtree(List<SysDataScope> sysDataScopeList, List<String> userScopeList) {
//
//        List<Ztree> ztrees = new ArrayList<Ztree>();
//        boolean isCheck = StringUtils.isNotNull(userScopeList);
//        for (SysDataScope sysDataScope : sysDataScopeList) {
//            if (UserConstants.DEPT_NORMAL.equals(sysDataScope.getStatus())) {
//                Ztree ztree = new Ztree();
//                ztree.setId(sysDataScope.getId());
//                ztree.setpId(sysDataScope.getParentId());
//                ztree.setName(sysDataScope.getMaterialDesc());
//                ztree.setTitle(sysDataScope.getMaterialDesc());
//                if (isCheck) {
//                    ztree.setChecked(userScopeList.contains(sysDataScope.getMaterialCode() + sysDataScope.getMaterialDesc()));
//                }
//                ztrees.add(ztree);
//            }
//        }
//        return ztrees;
//    }
}
