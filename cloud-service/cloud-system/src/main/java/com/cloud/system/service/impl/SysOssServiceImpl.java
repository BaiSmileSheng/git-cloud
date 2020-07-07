package com.cloud.system.service.impl;

import com.cloud.common.constant.Constants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.mapper.SysOssMapper;
import com.cloud.system.oss.CloudStorageService;
import com.cloud.system.oss.OSSFactory;
import com.cloud.system.service.ISysOssService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cloud.common.utils.ServletUtils.getRequest;

/**
 * 文件上传 服务层实现
 *
 * @author zmr
 * @date 2019-05-16
 */
@Service
public class SysOssServiceImpl implements ISysOssService {
    @Autowired
    private SysOssMapper sysOssMapper;

    /**
     * 查询文件上传信息
     *
     * @param id 文件上传ID
     * @return 文件上传信息
     */
    @Override
    public SysOss selectSysOssById(Long id) {
        return sysOssMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询文件上传列表
     *
     * @param sysOss 文件上传信息
     * @return 文件上传集合
     */
    @Override
    public List<SysOss> selectSysOssList(SysOss sysOss) {
        Example example = new Example(SysOss.class);
        Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(sysOss.getFileName())) {
            criteria.andLike("fileName", "%" + sysOss.getFileName() + "%");
        }
        if (StringUtils.isNotBlank(sysOss.getFileSuffix())) {
            criteria.andEqualTo("fileSuffix", sysOss.getFileSuffix());
        }
        if (StringUtils.isNotBlank(sysOss.getCreateBy())) {
            criteria.andEqualTo("createBy", sysOss.getCreateBy());
        }
        return sysOssMapper.selectByExample(example);
    }

    /**
     * 新增文件上传
     *
     * @param sysOss 文件上传信息
     * @return 结果
     */
    @Override
    public int insertSysOss(SysOss sysOss) {
        return sysOssMapper.insertSelective(sysOss);
    }

    /**
     * 修改文件上传
     *
     * @param sysOss 文件上传信息
     * @return 结果
     */
    @Override
    public int updateSysOss(SysOss sysOss) {
        return sysOssMapper.updateByPrimaryKeySelective(sysOss);
    }

    @Override
    public R batchEditSaveById(List<SysOss> sysOssList) {
        sysOssMapper.updateBatchByPrimaryKeySelective(sysOssList);
        return R.ok();
    }

    /**
     * 删除文件上传对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteSysOssByIds(String ids) {
        return sysOssMapper.deleteByIds(ids);
    }

    /**
     * 根据订单号查询文件列表
     * @param orderNo 订单编号
     * @return List<SysOss> 文件上传集合
     */
    @Override
    public List<SysOss> selectSysOssListByOrderNo(String orderNo) {
        Example example = new Example(SysOss.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo", orderNo);
        return sysOssMapper.selectByExample(example);
    }

    /**
     * 根据订单编号修改文件上传列表
     * @param orderNo 订单编号
     * @param files 文件数组
     * @return 成功或失败
     */
    @Transactional
    @Override
    public R updateListByOrderNo(String orderNo, MultipartFile[] files) throws IOException {
        if(files.length == 0){
            return R.error("上传文件集合不存在");
        }
        //根据订单号删除文件
        deleteFilesByOrderNo(orderNo);
        //根据订单号新增文件列表
        List<SysOss> sysOssListReq = new ArrayList<>();
        for(MultipartFile file : files){
            if(file.isEmpty()){
                throw new BusinessException("上传文件为空");
            }
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            CloudStorageService storageAdd = OSSFactory.build();
            String url = storageAdd.uploadSuffix(file.getBytes(), suffix);
            SysOss ossReq = new SysOss();
            ossReq.setUrl(url);
            ossReq.setFileSuffix(suffix);
            ossReq.setCreateBy(getLoginName());
            ossReq.setFileName(fileName);
            ossReq.setCreateTime(new Date());
            ossReq.setService(storageAdd.getService());
            ossReq.setOrderNo(orderNo);
            sysOssListReq.add(ossReq);
        }
        // 保存文件信息
        int count = sysOssMapper.insertList(sysOssListReq);
        return R.ok();
    }

    /**
     * 根据订单号删除文件
     * @param orderNo 订单号
     */
    private void deleteFilesByOrderNo(String orderNo){
        //根据订单号删除文件列表
        Example example = new Example(SysOss.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderNo", orderNo);
        List<SysOss> sysOssList = sysOssMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(sysOssList)){
            CloudStorageService storage = OSSFactory.build();
            StringBuffer idsBuffer = new StringBuffer();
            for(SysOss sysOss:sysOssList){
                storage.deleteFile(sysOss.getUrl());
                idsBuffer.append(sysOss.getId()).append(",");
            }
            String idsAll = idsBuffer.toString();
            String ids = idsAll.substring(0,idsAll.length()-1);
            sysOssMapper.deleteByIds(ids);
        }
    }

    /**
     * 根据订单编号删除文件上传列表
     * @param orderNo 订单编号
     * @return 成功或失败
     */
    @Transactional
    @Override
    public R deleteListByOrderNo(String orderNo) {
        deleteFilesByOrderNo(orderNo);
        return R.ok();
    }

    /**
     * 获取登录人名称
     * @return 登录人名称
     */
    public String getLoginName() {
        String userName = getRequest().getHeader(Constants.CURRENT_USERNAME);
        return userName == null ? " " : userName;
    }

}
