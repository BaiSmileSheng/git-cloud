package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.SysOss;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件上传 服务层
 *
 * @author zmr
 * @date 2019-05-16
 */
public interface ISysOssService {
    /**
     * 查询文件上传信息
     *
     * @param id 文件上传ID
     * @return 文件上传信息
     */
    public SysOss selectSysOssById(Long id);

    /**
     * 查询文件上传列表
     *
     * @param sysOss 文件上传信息
     * @return 文件上传集合
     */
    public List<SysOss> selectSysOssList(SysOss sysOss);

    /**
     * 新增文件上传
     *
     * @param sysOss 文件上传信息
     * @return 结果
     */
    public int insertSysOss(SysOss sysOss);

    /**
     * 修改文件上传
     *
     * @param sysOss 文件上传信息
     * @return 结果
     */
    public int updateSysOss(SysOss sysOss);

    /**
     * 按id批量修改
     * @param sysOssList
     * @return
     */
    R batchEditSaveById(List<SysOss> sysOssList);

    /**
     * 删除文件上传信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysOssByIds(String ids);

    /**
     * 根据订单号查询文件列表
     * @param orderNo 订单编号
     * @return List<SysOss> 文件上传集合
     */
    public List<SysOss> selectSysOssListByOrderNo(String orderNo);

    /**
     * 根据订单编号修改文件上传列表(存在就先删后增)
     * @param orderNo 订单编号
     * @param files 文件数组
     * @return 成功或失败
     */
    public R updateListByOrderNo(String orderNo, MultipartFile[] files) throws IOException;

    /**
     * 根据订单编号删除文件上传列表
     * @param orderNo 订单编号
     * @return 成功或失败
     */
    public R deleteListByOrderNo(String orderNo);

}
