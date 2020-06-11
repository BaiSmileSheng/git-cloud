package com.cloud.settle.controller;

import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tk.mybatis.mapper.entity.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.controller.BaseController;
import com.cloud.settle.domain.entity.SmsQualityType;
import com.cloud.settle.service.ISmsQualityTypeService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 质量索赔扣款类型和扣款标准 提供者
 *
 * @author Lihongxia
 * @date 2020-06-08
 */
@RestController
@RequestMapping("qualityType")
@Api(tags = "质量索赔扣款类型和扣款标准 提供者")
public class SmsQualityTypeController extends BaseController {

    @Autowired
    private ISmsQualityTypeService smsQualityTypeService;

    /**
     * 查询质量索赔扣款类型和扣款标准
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询质量索赔扣款类型和扣款标准", response = SmsQualityType.class)
    public SmsQualityType get(Long id) {
        return smsQualityTypeService.selectByPrimaryKey(id);

    }

    /**
     * 查询质量索赔扣款类型和扣款标准
     */
    @GetMapping("selectByParentId")
    @ApiOperation(value = "根据父id查询质量索赔扣款类型和扣款标准", response = SmsQualityType.class)
    public List<SmsQualityType> selectByParentId(Long parentId) {
        Example example = new Example(SmsQualityType.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId",parentId);
        List<SmsQualityType> smsQualityTypeList = smsQualityTypeService.selectByExample(example);
        for(SmsQualityType smsQualityType : smsQualityTypeList){
            String claimTypeShow = smsQualityType.getClaimType();
            if(claimTypeShow.length() >= 15){
                String claimTypeShowSub = claimTypeShow.substring(0,15) + "...";
                claimTypeShow = claimTypeShowSub;
            }
            smsQualityType.setClaimTypeShow(claimTypeShow);
        }
        return smsQualityTypeList;
    }



    /**
     * 查询质量索赔扣款类型和扣款标准列表
     */
    @GetMapping("list")
    @ApiOperation(value = "质量索赔扣款类型和扣款标准查询分页", response = SmsQualityType.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(SmsQualityType smsQualityType) {
        Example example = new Example(SmsQualityType.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<SmsQualityType> smsQualityTypeList = smsQualityTypeService.selectByExample(example);
        return getDataTable(smsQualityTypeList);
    }


    /**
     * 新增保存质量索赔扣款类型和扣款标准
     */
    @PostMapping("save")
    @OperLog(title = "新增保存质量索赔扣款类型和扣款标准", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存质量索赔扣款类型和扣款标准", response = R.class)
    public R addSave(@RequestBody SmsQualityType smsQualityType) {
        smsQualityTypeService.insertSelective(smsQualityType);
        return R.data(smsQualityType.getId());
    }

    /**
     * 修改保存质量索赔扣款类型和扣款标准
     */
    @PostMapping("update")
    @OperLog(title = "修改保存质量索赔扣款类型和扣款标准", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存质量索赔扣款类型和扣款标准", response = R.class)
    public R editSave(@RequestBody SmsQualityType smsQualityType) {
        return toAjax(smsQualityTypeService.updateByPrimaryKeySelective(smsQualityType));
    }

    /**
     * 删除质量索赔扣款类型和扣款标准
     */
    @PostMapping("remove")
    @OperLog(title = "删除质量索赔扣款类型和扣款标准", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除质量索赔扣款类型和扣款标准", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(smsQualityTypeService.deleteByIds(ids));
    }

}
