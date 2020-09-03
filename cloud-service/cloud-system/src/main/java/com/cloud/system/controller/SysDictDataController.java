package com.cloud.system.controller;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.common.redis.annotation.RedisCache;
import com.cloud.common.redis.util.RedisUtils;
import com.cloud.system.domain.entity.SysDictData;
import com.cloud.system.service.ISysDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据 提供者
 *
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("dict/data")
@Api(tags = "字典测试")
public class SysDictDataController extends BaseController {

    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 查询字典数据
     */
    @GetMapping("get/{dictCode}")
    public SysDictData get(@PathVariable("dictCode") Long dictCode) {
        return sysDictDataService.selectDictDataById(dictCode);

    }

    /**
     * 查询字典数据列表
     */
    @GetMapping("list")
    @HasPermissions("system:dict:list")
    public R list(SysDictData sysDictData) {
        startPage();
        return result(sysDictDataService.selectDictDataList(sysDictData));
    }

    /**
     * 根据字典类型查询字典数据信息
     * 从redis中取值，过期时间一周
     * @param dictType 字典类型
     * @return 参数键值
     */
    @GetMapping("type")
    @ApiOperation(value = "根据类型查询")
    @RedisCache(key = "dict", fieldKey = "#dictType", expired = 604800)
    public List<SysDictData> getType(String dictType) {
        return sysDictDataService.selectDictDataByType(dictType);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @GetMapping("label")
    public String getLabel(String dictType, String dictValue) {
        return sysDictDataService.selectDictLabel(dictType, dictValue);
    }


    /**
     * 根据字典类型和字典键值查询字典数据信息
     *
     * @param dictType  字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @GetMapping("listLabel")
    public List<String> listLabel(String dictType, String dictValue) {
        return sysDictDataService.selectListDictLabel(dictType, dictValue);
    }

    /**
     * 新增保存字典数据
     */
    @OperLog(title = "字典数据", businessType = BusinessType.INSERT)
    @HasPermissions("system:dict:add")
    @PostMapping("save")
    public R addSave(@RequestBody SysDictData sysDictData) {
        sysDictDataService.insertDictData(sysDictData);
        List<SysDictData> list=sysDictDataService.selectDictDataByType(sysDictData.getDictType());
        redisUtils.set(StrUtil.format("dict:{}",sysDictData.getDictType()),list);
        return R.ok();
    }

    /**
     * 修改保存字典数据
     */
    @OperLog(title = "字典数据", businessType = BusinessType.UPDATE)
    @HasPermissions("system:dict:edit")
    @PostMapping("update")
//    @RedisEvict(key = "dict",fieldKey = "#sysDictData.dictType")
    public R editSave(@RequestBody SysDictData sysDictData) {
        sysDictDataService.updateDictData(sysDictData);
        List<SysDictData> list=sysDictDataService.selectDictDataByType(sysDictData.getDictType());
        redisUtils.set(StrUtil.format("dict:{}",sysDictData.getDictType()),list);
        return R.ok();
    }

    /**
     * 删除字典数据
     */
    @OperLog(title = "字典数据", businessType = BusinessType.DELETE)
    @HasPermissions("system:dict:remove")
    @PostMapping("remove")
    public R remove(String ids) {
        return toAjax(sysDictDataService.deleteDictDataByIds(ids));
    }

}
