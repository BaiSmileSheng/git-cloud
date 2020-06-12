package com.cloud.order.controller;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.order.domain.entity.OmsInternalOrderRes;
import com.cloud.order.service.IOmsInternalOrderResService;
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
import com.cloud.common.core.page.TableDataInfo;
import java.util.List;
/**
 * 内单PR/PO原  提供者
 *
 * @author ltq
 * @date 2020-06-05
 */
@RestController
@RequestMapping("demand")
@Api(tags = "内单PR/PO原表信息")
public class OmsInternalOrderResController extends BaseController {

    @Autowired
    private IOmsInternalOrderResService omsInternalOrderResService;

    /**
     * 查询内单PR/PO原 
     */
    @GetMapping("get")
    @ApiOperation(value = "根据id查询内单PR/PO原 ", response = OmsInternalOrderRes.class)
    public OmsInternalOrderRes get(Long id) {
        return omsInternalOrderResService.selectByPrimaryKey(id);

    }

    /**
     * 查询内单PR/PO原 列表
     */
    @GetMapping("list")
    @ApiOperation(value = "内单PR/PO原 查询分页", response = OmsInternalOrderRes.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前记录起始索引", required =true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示记录数", required = true,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortField", value = "排序列", required = false,paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "sortOrder", value = "排序的方向", required = false,paramType = "query", dataType = "String")
    })
    public TableDataInfo list(OmsInternalOrderRes omsInternalOrderRes) {
        Example example = new Example(OmsInternalOrderRes.class);
        Example.Criteria criteria = example.createCriteria();
        startPage();
        List<OmsInternalOrderRes> omsInternalOrderResList = omsInternalOrderResService.selectByExample(example);
        return getDataTable(omsInternalOrderResList);
    }


    /**
     * 新增保存内单PR/PO原 
     */
    @PostMapping("save")
    @OperLog(title = "新增保存内单PR/PO原 ", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增保存内单PR/PO原 ", response = R.class)
    public R addSave(@RequestBody OmsInternalOrderRes omsInternalOrderRes) {
        omsInternalOrderResService.insertSelective(omsInternalOrderRes);
        return R.data(omsInternalOrderRes.getId());
    }

    /**
     * 修改保存内单PR/PO原 
     */
    @PostMapping("update")
    @OperLog(title = "修改保存内单PR/PO原 ", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改保存内单PR/PO原 ", response = R.class)
    public R editSave(@RequestBody OmsInternalOrderRes omsInternalOrderRes) {
        return toAjax(omsInternalOrderResService.updateByPrimaryKeySelective(omsInternalOrderRes));
    }

    /**
     * 删除内单PR/PO原 
     */
    @PostMapping("remove")
    @OperLog(title = "删除内单PR/PO原 ", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除内单PR/PO原 ", response = R.class)
    @ApiParam(name = "ids", value = "需删除数据的id")
    public R remove(@RequestBody String ids) {
        return toAjax(omsInternalOrderResService.deleteByIds(ids));
    }

}
