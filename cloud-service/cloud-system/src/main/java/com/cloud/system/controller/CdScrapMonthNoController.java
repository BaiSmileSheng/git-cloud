package com.cloud.system.controller;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.CdScrapMonthNo;
import com.cloud.system.service.ICdScrapMonthNoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
/**
 * 报废每月单号 提供者
 *
 * @author cs
 * @date 2020-09-25
 */
@RestController
@RequestMapping("cdScrapMonthNo")
@Api(tags = "报废每月一次订单号")
public class CdScrapMonthNoController extends BaseController {

    @Autowired
    private ICdScrapMonthNoService cdScrapMonthNoService;

    /**
     * 查询报废每月单号
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询报废每月单号", response = CdScrapMonthNo.class)
    public R get(Long id) {
        return R.data(cdScrapMonthNoService.selectByPrimaryKey(id));

    }

    /**
     * 查询报废每月单号列表
     */
    @GetMapping("list")
    @ApiOperation(value = "报废每月单号查询分页", response = CdScrapMonthNo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "yearMouth", value = "月份", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "factoryCode", value = "工厂编码", required = true,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdScrapMonthNo cdScrapMonthNo) {
        Example example = new Example(CdScrapMonthNo.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(cdScrapMonthNo.getYearMouth())) {
            criteria.andEqualTo("yearMouth", cdScrapMonthNo.getYearMouth());
        }
        if (StrUtil.isNotEmpty(cdScrapMonthNo.getFactoryCode())) {
            criteria.andEqualTo("factoryCode", cdScrapMonthNo.getFactoryCode());
        }
        startPage();
        List<CdScrapMonthNo> cdScrapMonthNoList = cdScrapMonthNoService.selectByExample(example);
        return getDataTable(cdScrapMonthNoList);
    }


    /**
     * 查询报废每月单号列表
     */
    @GetMapping("findOne")
    public R findOne(CdScrapMonthNo cdScrapMonthNo) {
        Example example = new Example(CdScrapMonthNo.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotEmpty(cdScrapMonthNo.getYearMouth())) {
            criteria.andEqualTo("yearMouth", cdScrapMonthNo.getYearMouth());
        }
        if (StrUtil.isNotEmpty(cdScrapMonthNo.getFactoryCode())) {
            criteria.andEqualTo("factoryCode", cdScrapMonthNo.getFactoryCode());
        }
        cdScrapMonthNo = cdScrapMonthNoService.selectOneByExample(example);
        return R.data(cdScrapMonthNo);
    }


    /**
     * 新增保存报废每月单号
     */
    @PostMapping("save")
    @OperLog(title = "新增保存报废每月单号", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存报废每月单号", response = R.class)
    public R addSave(@RequestBody CdScrapMonthNo cdScrapMonthNo) {
        cdScrapMonthNoService.insertSelective(cdScrapMonthNo);
        return R.data(cdScrapMonthNo.getId());
    }

    /**
     * 修改保存报废每月单号
     */
    @PostMapping("update")
    @OperLog(title = "修改保存报废每月单号", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存报废每月单号", response = R.class)
    public R editSave(@RequestBody CdScrapMonthNo cdScrapMonthNo) {
        return toAjax(cdScrapMonthNoService.updateByPrimaryKeySelective(cdScrapMonthNo));
    }

    /**
     * 删除报废每月单号
     */
    @PostMapping("remove")
    @OperLog(title = "删除报废每月单号", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除报废每月单号", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdScrapMonthNoService.deleteByIds(ids));
    }

}
