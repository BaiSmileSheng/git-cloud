package com.cloud.order.controller;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import io.swagger.annotations.*;
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
import com.cloud.order.domain.entity.OmsProductionOrderDel;
import com.cloud.order.service.IOmsProductionOrderDelService;
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 排产订单删除  提供者
 *
 * @author ltq
 * @date 2020-06-22
 */
@RestController
@RequestMapping("productDel")
public class OmsProductionOrderDelController extends BaseController {

    @Autowired
    private IOmsProductionOrderDelService omsProductionOrderDelService;

    /**
     * 查询排产订单删除 
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询排产订单删除 ", response = OmsProductionOrderDel.class)
    public OmsProductionOrderDel get(Long id) {
        return omsProductionOrderDelService.selectByPrimaryKey(id);

    }

    /**
     * 查询排产订单删除 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "排产订单删除 查询分页", response = OmsProductionOrderDel.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsProductionOrderDel omsProductionOrderDel) {
        Example example = new Example(OmsProductionOrderDel.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsProductionOrderDel> omsProductionOrderDelList = omsProductionOrderDelService.selectByExample(example);
        return getDataTable(omsProductionOrderDelList);
    }


    /**
     * 新增保存排产订单删除 
     */
    @PostMapping("save")
    @OperLog(title = "新增保存排产订单删除 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存排产订单删除 ", response = R.class)
    public R addSave(@RequestBody OmsProductionOrderDel omsProductionOrderDel) {
        omsProductionOrderDelService.insertSelective(omsProductionOrderDel);
        return R.data(omsProductionOrderDel.getId());
    }

    /**
     * 修改保存排产订单删除 
     */
    @PostMapping("update")
    @OperLog(title = "修改保存排产订单删除 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存排产订单删除 ", response = R.class)
    public R editSave(@RequestBody OmsProductionOrderDel omsProductionOrderDel) {
        return toAjax(omsProductionOrderDelService.updateByPrimaryKeySelective(omsProductionOrderDel));
    }

    /**
     * 删除排产订单删除 
     */
    @PostMapping("remove")
    @OperLog(title = "删除排产订单删除 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除排产订单删除 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsProductionOrderDelService.deleteByIds(ids));
    }

}
