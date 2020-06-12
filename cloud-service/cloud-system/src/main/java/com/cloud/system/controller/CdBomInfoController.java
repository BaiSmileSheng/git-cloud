package com.cloud.system.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.service.ICdBomInfoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

/**
 * bom清单数据  提供者
 *
 * @author cs
 * @date 2020-06-01
 */
@RestController
@RequestMapping("bom")
@Api(tags = "BOM清单")
public class CdBomInfoController extends BaseController {

    @Autowired
    private ICdBomInfoService cdBomInfoService;

    /**
     * 查询bom清单数据
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询bom清单数据 ", response = CdBomInfo.class)
    public CdBomInfo get(Long id) {
        return cdBomInfoService.selectByPrimaryKey(id);

    }

    /**
     * 查询bom清单数据 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "bom清单数据 查询分页", response = CdBomInfo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdBomInfo cdBomInfo) {
        Example example = new Example(CdBomInfo.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdBomInfo,criteria);
        startPage();
        List<CdBomInfo> cdBomInfoList = cdBomInfoService.selectByExample(example);
        return getDataTable(cdBomInfoList);
    }

    /**
     * 查询bom清单数据 列表
     */
    @GetMapping("listByExample")
    @ApiOperation(value = "bom清单数据", response = CdBomInfo.class)
    public R listByExample(CdBomInfo cdBomInfo) {
        Example example = new Example(CdBomInfo.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdBomInfo,criteria);
        List<CdBomInfo> cdBomInfoList = cdBomInfoService.selectByExample(example);
        return R.data(cdBomInfoList);
    }

    /**
     * 分页查询条件
     * @param cdBomInfo
     * @param criteria
     */
    void listCondition(CdBomInfo cdBomInfo,Example.Criteria criteria) {
        if (StringUtils.isNotBlank(cdBomInfo.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode", cdBomInfo.getProductMaterialCode());
        }
    }

    /**
     * 根据成品物料号、原材料物料号确定一条数据
     */
    @GetMapping("listByProductAndMaterial")
    public CdBomInfo listByProductAndMaterial(String productMaterialCode,String rawMaterialCode) {
        Example example = new Example(CdBomInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isBlank(productMaterialCode)) {
            throw new BusinessException("参数：成品物料号为空");
        }
        if (StrUtil.isBlank(rawMaterialCode)) {
            throw new BusinessException("参数：原材料物料号为空");
        }
        criteria.andEqualTo("productMaterialCode",productMaterialCode);
        criteria.andEqualTo("rawMaterialCode",rawMaterialCode);
        criteria.andEqualTo("delFlag","0");
        CdBomInfo cdBomInfo = cdBomInfoService.findByExampleOne(example);
        return cdBomInfo;
    }

    /**
     * 新增保存bom清单数据
     */
    @PostMapping("save")
    @OperLog(title = "新增保存bom清单数据 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存bom清单数据 ", response = R.class)
    public R addSave(@RequestBody CdBomInfo cdBomInfo) {
        cdBomInfoService.insertSelective(cdBomInfo);
        return R.data(cdBomInfo.getId());
    }

    /**
     * 修改保存bom清单数据
     */
    @PostMapping("update")
    @OperLog(title = "修改保存bom清单数据 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存bom清单数据 ", response = R.class)
    public R editSave(@RequestBody CdBomInfo cdBomInfo) {
        return toAjax(cdBomInfoService.updateByPrimaryKeySelective(cdBomInfo));
    }

    /**
     * 删除bom清单数据
     */
    @PostMapping("remove")
    @OperLog(title = "删除bom清单数据 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除bom清单数据 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdBomInfoService.deleteByIds(ids));
    }

    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R 单耗
     */
    @PostMapping("checkBomNum")
    public R checkBomNum(String productMaterialCode,String rawMaterialCode,int applyNum){
        return cdBomInfoService.checkBomNum(productMaterialCode,rawMaterialCode,applyNum);
    }

    /**
     * 根据物料号工厂分组取bom版本
     * @return
     */
    @PostMapping("selectVersionMap")
    public Map<String,Map<String, String>> selectVersionMap(@RequestBody List<Dict> dicts){
        return cdBomInfoService.selectVersionMap(dicts);
    }
}
