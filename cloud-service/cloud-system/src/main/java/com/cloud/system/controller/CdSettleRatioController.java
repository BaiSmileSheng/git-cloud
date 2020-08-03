package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;
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
import com.cloud.system.domain.entity.CdSettleRatio;
import com.cloud.system.service.ICdSettleRatioService;
import com.cloud.common.core.page.TableDataInfo;

import java.util.List;

/**
 * 结算索赔系数  提供者
 *
 * @author lihongxia
 * @date 2020-06-04
 */
@RestController
@RequestMapping("settleRatio")
@Api(tags = "结算索赔系数  提供者")
public class CdSettleRatioController extends BaseController {

    @Autowired
    private ICdSettleRatioService cdSettleRatioService;

    /**
     * 查询结算索赔系数
     * @param id 主键id
     * @return 结算索赔系数
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询结算索赔系数 ", response = CdSettleRatio.class)
    public CdSettleRatio get(Long id) {
        return cdSettleRatioService.selectByPrimaryKey(id);

    }

    /**
     * 查询结算索赔系数
     * @param claimType 索赔类型
     * @return 结算索赔系数
     */
    @GetMapping("selectByClaimType")
    @ApiOperation(value = "根据索赔类型查询结算索赔系数 ", response = CdSettleRatio.class)
    public CdSettleRatio selectByClaimType(@RequestParam("claimType") String claimType) {
        return cdSettleRatioService.selectByClaimType(claimType);

    }

    /**
     * 查询结算索赔系数 列表
     * @param cdSettleRatio 结算索赔系数
     * @return 查询结算索赔系数 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "结算索赔系数 查询分页", response = CdSettleRatio.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "claimType", value = "索赔类型 1.物耗申请  2.报废申请", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(@ApiIgnore CdSettleRatio cdSettleRatio) {
        Example example = new Example(CdSettleRatio.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(cdSettleRatio.getClaimType())){
            criteria.andEqualTo("claimType",cdSettleRatio.getClaimType());
        }
        example.orderBy("createTime").desc();
        startPage();
        List<CdSettleRatio> cdSettleRatioList = cdSettleRatioService.selectByExample(example);
        return getDataTable(cdSettleRatioList);
    }


    /**
     * 新增结算索赔系数(先校验此索赔类型是否存在)
     * @param cdSettleRatio 结算索赔系数
     * @return 新增主键id
     */
    @HasPermissions("system:settleRatio:save")
    @PostMapping("save")
    @OperLog(title = "新增保存结算索赔系数 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增结算索赔系数(先校验此索赔类型是否存在) ", response = R.class)
    public R addSave(@RequestBody CdSettleRatio cdSettleRatio) {
        if(StringUtils.isBlank(cdSettleRatio.getClaimType())){
            return R.error("新增保存结算索赔系数时索赔类型为空");
        }
        SysUser sysUser = getUserInfo(SysUser.class);
        cdSettleRatio.setCreateBy(sysUser.getLoginName());
        R r = cdSettleRatioService.addSaveVerifyClaimType(cdSettleRatio);
        return r;
    }

    /**
     * 修改保存结算索赔系数
     * @param cdSettleRatio 结算索赔系数信息
     * @return 修改结果成功或失败
     */
    @HasPermissions("system:settleRatio:update")
    @PostMapping("update")
    @OperLog(title = "修改保存结算索赔系数 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存结算索赔系数 ", response = R.class)
    public R editSave(@RequestBody CdSettleRatio cdSettleRatio) {
        SysUser sysUser = getUserInfo(SysUser.class);
        cdSettleRatio.setUpdateBy(sysUser.getLoginName());
        return cdSettleRatioService.updateVerifyClaimType(cdSettleRatio);
    }

    /**
     * 删除结算索赔系数
     * @param ids 主键
     * @return 删除成功或失败
     */
    @HasPermissions("system:settleRatio:remove")
    @PostMapping("remove")
    @OperLog(title = "删除结算索赔系数 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除结算索赔系数 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdSettleRatioService.deleteByIds(ids));
    }

}
