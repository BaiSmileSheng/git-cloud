package com.cloud.system.controller;
import cn.hutool.core.collection.CollUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.RandomUtil;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.SysDataScope;
import com.cloud.system.service.ISysDataScopeService;
import com.cloud.system.util.DataScopeUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 *  数据权限 提供者
 *
 * @author cs
 * @date 2020-05-02
 */
@RestController
@RequestMapping("scope")
public class SysDataScopeController extends BaseController {

    @Autowired
    private ISysDataScopeService sysDataScopeService;

    /**
     * 查询数据权限
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询数据权限", response = SysDataScope.class)
    public SysDataScope get(String id) {
        return sysDataScopeService.selectByPrimaryKey(id);

    }

    /**
     * 查询 数据权限列表
     */
    @GetMapping("list")
    @ApiOperation(value = " 数据权限查询分页", response = SysDataScope.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SysDataScope sysDataScope) {
        Example example = new Example(SysDataScope.class);
        example.and().andEqualTo(sysDataScope);
        if(sysDataScope.getMaterialDesc()!=null){
            example.and().andLike("materialDesc",sysDataScope.getMaterialDesc());
        }
        example.orderBy("orderNum").asc();
        startPage();
        List<SysDataScope> sysDataScopeList = sysDataScopeService.selectByExample(example);
        return getDataTable(sysDataScopeList);
    }


    /**
     * 新增保存 数据权限
     */
    @PostMapping("save")
    @OperLog(title = "新增保存 数据权限", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存 数据权限", response = SysDataScope.class)
    public R addSave(@RequestBody SysDataScope sysDataScope) {
        if(sysDataScopeService.selectByPrimaryKey(sysDataScope.getMaterialCode())!=null){
            return R.error("物料号不能重复！");
        }
        sysDataScope.setId(RandomUtil.randomStr(15));
        sysDataScope.setDelFlag("0");
        sysDataScope.setStatus("0");
        sysDataScope.setCreateBy(getLoginName());
        sysDataScope.setCreateTime(new Date());
        return toAjax(sysDataScopeService.insertSelective(sysDataScope));
    }

    /**
     * 修改保存数据权限
     */
    @PostMapping("update")
    @OperLog(title = "修改保存 数据权限", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存 数据权限", response = SysDataScope.class)
    public R editSave(@RequestBody SysDataScope sysDataScope) {
        return toAjax(sysDataScopeService.updateByPrimaryKeySelective(sysDataScope));
    }

    /**
     * 物理删除 数据权限
     */
    @PostMapping("remove")
    @OperLog(title = "删除数据权限", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除数据权限", response = SysDataScope.class)
    public R remove(String ids) {
        return toAjax(sysDataScopeService.deleteByIdsWL(ids));
    }

    /**
     * 查询用户所有有效权限
     * @return
     */
    @GetMapping("getUserScopeIds")
    public Set<String> getUserScopeIds(Long userId) {
        //工厂权限
        String scopeFactory = DataScopeUtil.getUserFactoryScopes(userId);
        //采购组权限
        String scopePurchase = DataScopeUtil.getUserPurchaseScopes(userId);
        if(StringUtils.isBlank(scopeFactory)&&StringUtils.isBlank(scopePurchase)){
            return new HashSet<>();
        }
        Set<String> scopeSet=new HashSet<> ();
        if(StringUtils.isNotBlank(scopeFactory)){
            CollUtil.addAll(scopeSet, Arrays.asList(scopeFactory.split(",")));
        }
        if(StringUtils.isNotBlank(scopePurchase)){
            CollUtil.addAll(scopeSet, Arrays.asList(scopePurchase.split(",")));
        }
        return scopeSet;
    }

    /**
     * 获取所有物料权限  树结构用
     * @return
     */
    @GetMapping("allList")
    public R allList() {
        Example example = new Example(SysDataScope.class);
        example.orderBy("orderNum").asc();
        return result(sysDataScopeService.selectByExample(example));
    }
}
