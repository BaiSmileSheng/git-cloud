package com.cloud.system.controller;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.utils.StringUtils;
import com.cloud.system.domain.entity.CdBom;
import com.cloud.system.service.ICdBomService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * bom清单数据  提供者
 *
 * @author cs
 * @date 2020-06-01
 */
@RestController
@RequestMapping("bom")
public class CdBomController extends BaseController {

    @Autowired
    private ICdBomService cdBomService;

    /**
     * 查询bom清单数据
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询bom清单数据 ", response = CdBom.class)
    public CdBom get(Long id) {
        return cdBomService.selectByPrimaryKey(id);

    }

    /**
     * 查询bom清单数据 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "bom清单数据 查询分页", response = CdBom.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false, paramType = "query", dataType = "String")
    })
    public TableDataInfo list(CdBom cdBom) {
        Example example = new Example(CdBom.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdBom,criteria);
        startPage();
        List<CdBom> cdBomList = cdBomService.selectByExample(example);
        return getDataTable(cdBomList);
    }

    /**
     * 查询bom清单数据 列表
     */
    @GetMapping("listByExample")
    @ApiOperation(value = "bom清单数据", response = CdBom.class)
    public R listByExample(CdBom cdBom) {
        Example example = new Example(CdBom.class);
        Example.Criteria criteria = example.createCriteria();
        listCondition(cdBom,criteria);
        List<CdBom> cdBomList = cdBomService.selectByExample(example);
        return R.data(cdBomList);
    }

    /**
     * 分页查询条件
     * @param cdBom
     * @param criteria
     */
    void listCondition(CdBom cdBom,Example.Criteria criteria) {
        if (StringUtils.isNotBlank(cdBom.getProductMaterialCode())) {
            criteria.andLike("productMaterialCode", cdBom.getProductMaterialCode());
        }
    }

    /**
     * 根据成品物料号、原材料物料号确定一条数据
     */
    @GetMapping("listByProductAndMaterial")
    public CdBom listByProductAndMaterial(String productMaterialCode,String rawMaterialCode) {
        Example example = new Example(CdBom.class);
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
        CdBom cdBom = cdBomService.findByExampleOne(example);
        return cdBom;
    }

    /**
     * 新增保存bom清单数据
     */
    @PostMapping("save")
    @OperLog(title = "新增保存bom清单数据 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存bom清单数据 ", response = R.class)
    public R addSave(@RequestBody CdBom cdBom) {
        cdBomService.insertSelective(cdBom);
        return R.data(cdBom.getId());
    }

    /**
     * 修改保存bom清单数据
     */
    @PostMapping("update")
    @OperLog(title = "修改保存bom清单数据 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存bom清单数据 ", response = R.class)
    public R editSave(@RequestBody CdBom cdBom) {
        return toAjax(cdBomService.updateByPrimaryKeySelective(cdBom));
    }

    /**
     * 删除bom清单数据
     */
    @PostMapping("remove")
    @OperLog(title = "删除bom清单数据 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除bom清单数据 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(cdBomService.deleteByIds(ids));
    }

    /**
     * 校验申请数量是否是单耗的整数倍
     * @param productMaterialCode
     * @param rawMaterialCode
     * @param applyNum
     * @return R
     */
    @PostMapping("checkBomNum")
    public R checkBomNum(String productMaterialCode,String rawMaterialCode,int applyNum){
        if (StrUtil.isBlank(productMaterialCode)) {
            return R.error("参数：成品物料号为空！");
        }
        if (StrUtil.isBlank(rawMaterialCode)) {
            return R.error("参数：原材料物料号为空！");
        }
        if (applyNum==0) {
            return R.error("参数：申请量为空！");
        }
        Example example = new Example(CdBom.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productMaterialCode",productMaterialCode);
        criteria.andEqualTo("rawMaterialCode",rawMaterialCode);
        criteria.andEqualTo("delFlag","0");
        CdBom cdBom = cdBomService.findByExampleOne(example);
        int bomNum = cdBom.getBomNum().intValue();//单耗
        if(applyNum%bomNum!=0){
            return R.error("申请量必须是单耗的整数倍！");
        }
        return R.data(bomNum);
    }
}
