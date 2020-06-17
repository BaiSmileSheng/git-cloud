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
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.order.service.IOmsRealOrderService;
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 真单 提供者
 *
 * @author ltq
 * @date 2020-06-15
 */
@RestController
@RequestMapping("realOrder")
public class OmsRealOrderController extends BaseController {

    @Autowired
    private IOmsRealOrderService omsRealOrderService;

    /**
     * 查询真单
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询真单", response = OmsRealOrder.class)
    public OmsRealOrder get(Long id) {
        return omsRealOrderService.selectByPrimaryKey(id);

    }

    /**
     * 查询真单列表
     */
    @GetMapping("list")
    @ApiOperation(value = "真单查询分页", response = OmsRealOrder.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsRealOrder omsRealOrder) {
        Example example = new Example(OmsRealOrder.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsRealOrder> omsRealOrderList = omsRealOrderService.selectByExample(example);
        return getDataTable(omsRealOrderList);
    }


    /**
     * 新增保存真单
     */
    @PostMapping("save")
    @OperLog(title = "新增保存真单", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存真单", response = R.class)
    public R addSave(@RequestBody OmsRealOrder omsRealOrder) {
        omsRealOrderService.insertSelective(omsRealOrder);
        return R.data(omsRealOrder.getId());
    }

    /**
     * 修改保存真单
     */
    @PostMapping("update")
    @OperLog(title = "修改保存真单", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存真单", response = R.class)
    public R editSave(@RequestBody OmsRealOrder omsRealOrder) {
        return toAjax(omsRealOrderService.updateByPrimaryKeySelective(omsRealOrder));
    }

    /**
     * 删除真单
     */
    @PostMapping("remove")
    @OperLog(title = "删除真单", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除真单", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsRealOrderService.deleteByIds(ids));
    }

}
