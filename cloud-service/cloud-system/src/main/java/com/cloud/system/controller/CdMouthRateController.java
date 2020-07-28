package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdMouthRate;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.service.ICdMouthRateService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 汇率 提供者
 *
 * @author cs
 * @date 2020-05-27
 */
@RestController
@RequestMapping("mouthRate")
public class CdMouthRateController extends BaseController {

    @Autowired
    private ICdMouthRateService cdMouthRateService;

    /**
     * 查询 汇率
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询 汇率", response = CdMouthRate.class)
    public CdMouthRate get(Long id) {
        return cdMouthRateService.selectByPrimaryKey(id);

    }

    /**
     * 查询 汇率列表
     */
    @GetMapping("list")
    @ApiOperation(value = " 汇率查询分页", response = CdMouthRate.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "yearMouth", value = "年月", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "currency", value = "币种", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdMouthRate cdMouthRate) {
        Example example = new Example(CdMouthRate.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(cdMouthRate.getYearMouth())) {
            criteria.andEqualTo("yearMouth", cdMouthRate.getYearMouth());
        }
        if (StrUtil.isNotEmpty(cdMouthRate.getCurrency())) {
            criteria.andEqualTo("currency", cdMouthRate.getCurrency());
        }
        startPage();
        List<CdMouthRate> cdMouthRateList = cdMouthRateService.selectByExample(example);
        return getDataTable(cdMouthRateList);
    }


    /**
     * 新增保存 汇率
     */
    @HasPermissions("system:mouthRate:save")
    @PostMapping("save")
    @OperLog(title = "新增保存 汇率", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存 汇率", response = R.class)
    public R addSave(@RequestBody CdMouthRate cdMouthRate) {
        SysUser sysUser = getUserInfo(SysUser.class);
        cdMouthRate.setCreateBy(sysUser.getLoginName());
        cdMouthRate.setCreateTime(new Date());
        R result = cdMouthRateService.insertMouthRate(cdMouthRate);
        return result;
    }

    /**
     * 修改保存 汇率
     */
    @HasPermissions("system:mouthRate:update")
    @PostMapping("update")
    @OperLog(title = "修改保存 汇率", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存 汇率", response = R.class)
    public R editSave(@RequestBody CdMouthRate cdMouthRate) {
        SysUser sysUser = getUserInfo(SysUser.class);
        cdMouthRate.setUpdateBy(sysUser.getLoginName());
        cdMouthRate.setUpdateTime(new Date());
        R r = cdMouthRateService.updateMouthRate(cdMouthRate);
        return r;
    }

    /**
     * 删除 汇率
     */
    @HasPermissions("system:mouthRate:remove")
    @PostMapping("remove")
    @OperLog(title = "删除 汇率", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除 汇率", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdMouthRateService.deleteByIds(ids));
    }

    /**
     * 根据月份查询汇率
     * @param yearMouth
     * @param currency
     * @return rate
     */
    @GetMapping(value = "findRate")
    public R findRateByYearMouth(String yearMouth,String currency){
        Example example = new Example(CdMouthRate.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("yearMouth", yearMouth);
        criteria.andEqualTo("currency", currency);
        CdMouthRate cdMouthRate = cdMouthRateService.findByExampleOne(example);
        return R.data(cdMouthRate);
    }
}
