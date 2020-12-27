package com.cloud.order.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.constant.RoleConstants;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsProductDifference;
import com.cloud.order.domain.entity.vo.OmsProductDifferenceExportVo;
import com.cloud.order.service.IOmsProductDifferenceService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.order.util.EasyExcelUtilOSS;
import com.cloud.system.domain.entity.SysUser;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
/**
 * 外单排产差异报表  提供者
 *
 * @author ltq
 * @date 2020-09-30
 */
@RestController
@RequestMapping("difference")
public class OmsProductDifferenceController extends BaseController {

    @Autowired
    private IOmsProductDifferenceService omsProductDifferenceService;

    /**
     * 查询外单排产差异报表
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询外单排产差异报表 ", response = OmsProductDifference.class)
    public R get(Long id) {
        return R.data(omsProductDifferenceService.selectByPrimaryKey(id));

    }

    /**
     * 查询外单排产差异报表 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "外单排产差异报表 查询分页", response = OmsProductDifference.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productType", value = "产品类别", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "weeks", value = "周数", required = false,paramType = "query", dataType = "String"),
    })
    @HasPermissions("order:difference:list")
    public TableDataInfo list(OmsProductDifference omsProductDifference) {
        SysUser sysUser = getUserInfo(SysUser.class);
        Example example = new Example(OmsProductDifference.class);
        checkParams(example,omsProductDifference,sysUser);
        startPage();
        List<OmsProductDifference> omsProductDifferenceList = omsProductDifferenceService.selectByExample(example);
        return getDataTable(omsProductDifferenceList);
    }


    /**
     * 新增保存外单排产差异报表
     */
    @PostMapping("save")
    @OperLog(title = "新增保存外单排产差异报表 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存外单排产差异报表 ", response = R.class)
    public R addSave(@RequestBody OmsProductDifference omsProductDifference) {
        omsProductDifferenceService.insertSelective(omsProductDifference);
        return R.data(omsProductDifference.getId());
    }

    /**
     * 修改保存外单排产差异报表
     */
    @PostMapping("update")
    @OperLog(title = "修改保存外单排产差异报表 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存外单排产差异报表 ", response = R.class)
    public R editSave(@RequestBody OmsProductDifference omsProductDifference) {
        return toAjax(omsProductDifferenceService.updateByPrimaryKeySelective(omsProductDifference));
    }

    /**
     * 删除外单排产差异报表
     */
    @PostMapping("remove")
    @OperLog(title = "删除外单排产差异报表 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除外单排产差异报表 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductDifferenceService.deleteByIds(ids));
    }

    /**
     * 外单排产差异报表导出
     */
    @GetMapping("export")
    @OperLog(title = "外单排产差异报表导出 ", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "外单排产差异报表导出 ", response = R.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productFactoryCode", value = "工厂", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productType", value = "产品类别", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "productMaterialCode", value = "专用号", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "weeks", value = "周数", required = false,paramType = "query", dataType = "String"),
    })
    @HasPermissions("order:difference:export")
    public R export(@ApiIgnore OmsProductDifference omsProductDifference) {
        SysUser sysUser = getUserInfo(SysUser.class);
        Example example = new Example(OmsProductDifference.class);
        checkParams(example,omsProductDifference,sysUser);
        List<OmsProductDifference> omsProductDifferenceList = omsProductDifferenceService.selectByExample(example);
        String fileName = "外单排产差异报表.xlsx";
        return EasyExcelUtilOSS.writeExcel(omsProductDifferenceList, fileName, "sheet", new OmsProductDifferenceExportVo());
    }
    /**
    * Description:  组织参数
    * Param:
    * return:
    * Author: ltq
    * Date: 2020/9/30
    */
    public void checkParams(Example example,OmsProductDifference omsProductDifference,SysUser sysUser){
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsProductDifference.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode",omsProductDifference.getProductFactoryCode());
        }
        if (StrUtil.isNotBlank(omsProductDifference.getProductType())) {
            criteria.andEqualTo("productType",omsProductDifference.getProductType());
        }
        if (StrUtil.isNotBlank(omsProductDifference.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode",omsProductDifference.getProductMaterialCode());
        }
        if (StrUtil.isNotBlank(omsProductDifference.getWeeks())) {
            criteria.andEqualTo("weeks",omsProductDifference.getWeeks());
        }
        if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)
                || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_ORDER)
                || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_DDPT)) {
            example.and().andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(getCurrentUserId()).split(",")));
        }
        if (StrUtil.isNotBlank(omsProductDifference.getCreateBy())) {
            criteria.andEqualTo("createBy",omsProductDifference.getCreateBy());
        }
        example.orderBy("differenceNum").asc();
    }
    /**
     * 生成外单排产差异报表
     */
    @PostMapping("timeProductDiffTask")
    @OperLog(title = "生成外单排产差异报表 ", businessType = BusinessType.OTHER)
    @ApiOperation(value = "生成外单排产差异报表 ", response = R.class)
    public R timeProductDiffTask() {
        return omsProductDifferenceService.timeProductDiffTask();
    }

}
