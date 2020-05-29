package com.cloud.settle.service.impl;

import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.service.ISequeceService;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.feign.RemoteOssService;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsQualityOrderMapper;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.service.ISmsQualityOrderService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 质量索赔 Service业务层处理
 *
 * @author cs
 * @date 2020-05-27
 */
@Service
public class SmsQualityOrderServiceImpl extends BaseServiceImpl<SmsQualityOrder> implements ISmsQualityOrderService {
    @Autowired
    private SmsQualityOrderMapper smsQualityOrderMapper;
    @Autowired
    private RemoteOssService remoteOssService;
    @Autowired
    private ISequeceService sequeceService;

    /**
     * 索赔单所对应的索赔文件订单号后缀
     */
    private static final String ORDER_NO_QUALITY_CLAIM_END = "_01";

    /**
     * 索赔单所对应的申诉文件订单号后缀
     */
    private static final String ORDER_NO_QUALITY_APPEAL_END = "_02";

    /**
     * 索赔单序列号生成所对应的序列
     */
    private static final String QUALITY_SEQ_NAME = "quality_id";
    /**
     * 索赔单序列号生成所对应的序列长度
     */
    private static final int QUALITY_SEQ_LENGTH = 4;

    /**
     * 生成索赔单前缀
     */
    private static final String QUALITY_ORDER_PRE = "ZL";

    /**
     * 查询质量索赔详情
     * @param id 主键id
     * @return 索赔信息(包括文件信息)
     */
    @Override
    public R selectById(Long id) {
        SmsQualityOrder smsQualityOrderRes = this.selectByPrimaryKey(id);
        if(null != smsQualityOrderRes || StringUtils.isNotBlank(smsQualityOrderRes.getQualityNo())){
            //索赔文件编号
            String claimOrderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
            List<SysOss> claimListReault = remoteOssService.listByOrderNo(claimOrderNo);
            //申诉文件编号
            String appealOrderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_APPEAL_END;
            List<SysOss> appealListReault = remoteOssService.listByOrderNo(appealOrderNo);
            Map<String,Object> map = new HashMap<>();
            map.put("smsQualityOrder",smsQualityOrderRes);
            map.put("claimSysOssList",claimListReault);
            map.put("appealSysOssList",appealListReault);
            return R.ok(map);
        }

        return R.error("查询索赔单失败");
    }

    /**
     * 新增质量索赔信息
     * @param smsQualityOrder 质量索赔信息
     * @param files 质量索赔对应的文件信息
     * @return
     */
//    @GlobalTransactional
    @Override
    public R addSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, MultipartFile[] files) {
        //索赔单号生成规则 ZL+年月日+4位顺序号，循序号每日清零
        StringBuffer qualityNoBuffer = new StringBuffer(QUALITY_ORDER_PRE);
        qualityNoBuffer.append(DateUtils.getDate().replace("-",""));
        String seq = sequeceService.selectSeq(QUALITY_SEQ_NAME,QUALITY_SEQ_LENGTH);
        qualityNoBuffer.append(seq);
        smsQualityOrder.setQualityNo(qualityNoBuffer.toString());
        this.insertSelective(smsQualityOrder);
        //上传质量索赔附件上传的时候order_no 为 索赔单号_01
        for(MultipartFile file : files){
            String orderNo = smsQualityOrder.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
            R uplodeFileResult = remoteOssService.uploadFile(file,orderNo);
            Boolean flagResult = "0".equals(uplodeFileResult.get("code").toString());
            if(!flagResult){
                throw new BusinessException("新增文件失败");
            }
        }
        return R.data(smsQualityOrder.getId());
    }

    /**
     * 修改质量索赔信息
     * @param smsQualityOrder 质量索赔信息
     * @param files 质量索赔对应的文件信息
     * @return
     */
//    @GlobalTransactional
    @Override
    public R updateSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, MultipartFile[] files) {
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        SmsQualityOrder smsQualityOrderRes = this.selectByPrimaryKey(smsQualityOrder.getId());
        if(null == smsQualityOrderRes){
            return R.error("索赔单不存在");
        }
        Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrderRes.getQualityStatus());
        if(!flagResult){
            return R.error("此索赔单已提交不可再编辑");
        }
        //2.修改索赔单信息
        this.updateByPrimaryKeySelective(smsQualityOrder);
        //3.根据索赔单号所对应的索赔文件订单号查文件
        String orderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
        List<SysOss> sysOssList =  remoteOssService.listByOrderNo(orderNo);
        if(CollectionUtils.isEmpty(sysOssList)){
            throw new BusinessException("根据订单号查文件失败");
        }
        //4.批量删除文件
        StringBuffer idBuffer = new StringBuffer();
        sysOssList.forEach(sysOss -> idBuffer.append(sysOss.getId()).append(","));
        String idsSelect = idBuffer.toString();
        String ids = idsSelect.substring(0,idsSelect.length()-1);
        R deleteSysOssResult = remoteOssService.remove(ids);
        flagResult = "0".equals(deleteSysOssResult.get("code").toString());
        if(!flagResult){
            throw new BusinessException("根据ids删除文件失败");
        }
        //5.新增文件
        for(MultipartFile file : files){
            R uplodeFileResult = remoteOssService.uploadFile(file,orderNo);
            flagResult = "0".equals(uplodeFileResult.get("code").toString());
            if(!flagResult){
                throw new BusinessException("新增文件失败");
            }
        }
        return R.ok();
    }

    /**
     * 删除质量索赔信息
     * @param ids 主键id
     * @return 删除结果成功或失败
     */
    @GlobalTransactional
    @Override
    public R deleteSmsQualityOrderAndSysOss(String ids) {
        //根据订单号查文件
        List<SmsQualityOrder> selectListResult =  this.selectListById(ids);
        if(CollectionUtils.isEmpty(selectListResult)){
            return R.error("索赔单不存在");
        }
        for(SmsQualityOrder smsQualityOrder : selectListResult){
            Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus());
            if(!flagResult){
                return R.error("请确认索赔单状态是否为待提交");
            }
        }
        //根据id删除文件
        StringBuffer stringBuffer = new StringBuffer();
        for(SmsQualityOrder smsQualityOrder : selectListResult){
            String orderNo = smsQualityOrder.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
            List<SysOss> sysOssList = remoteOssService.listByOrderNo(orderNo);
            if(CollectionUtils.isEmpty(sysOssList)){
                return R.error("查询索赔单文件失败");
            }
            sysOssList.forEach(sysOss -> stringBuffer.append(sysOss.getId()).append(","));
        }
        String sysOssIdsSelect = stringBuffer.toString();
        String sysOssIds= sysOssIdsSelect.substring(0,sysOssIdsSelect.length()-1);
        R removeSysOssResult = remoteOssService.remove(sysOssIds);
        Boolean flagResult = "0".equals(removeSysOssResult.get("code").toString());
        if(!flagResult){
            return R.error("删除索赔单文件失败");
        }
        this.deleteByIds(ids);
        return R.ok();
    }

    /**
     * 根据索赔单主键批量查询
     * @param ids 主键
     * @return 索赔单集合
     */
    @Override
    public List<SmsQualityOrder> selectListById(String ids) {
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderMapper.selectByIds(ids);
        return smsQualityOrderList;
    }

    /**
     * 提交索赔单
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    @Override
    public R submit(String ids) {
        List<SmsQualityOrder> selectListResult =  this.selectListById(ids);
        for(SmsQualityOrder smsQualityOrder : selectListResult){
            Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus());
            if(!flagResult){
                return R.error("请确认索赔单状态是否为待提交");
            }
            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_1.getCode());
        }
        Integer count =  this.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @Override
    public R supplierConfirm(String ids) {
        List<SmsQualityOrder> selectListResult =  this.selectListById(ids);
        for(SmsQualityOrder smsQualityOrder : selectListResult){
            Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_1.getCode().equals(smsQualityOrder.getQualityStatus())
                    ||QualityStatusEnum.QUALITY_STATUS_7.getCode().equals(smsQualityOrder.getQualityStatus());
            if(!flagResult){
                return R.error("请确认索赔单状态是否为待供应商确认");
            }
            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
            smsQualityOrder.setSettleFee(smsQualityOrder.getClaimAmount());
            smsQualityOrder.setSupplierConfirmDate(new Date());
        }
        int count = this.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param smsQualityOrder 质量索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    //    @GlobalTransactional
    @Override
    public R supplierAppeal(SmsQualityOrder smsQualityOrder, MultipartFile[] files) {
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        SmsQualityOrder smsQualityOrderRes = this.selectByPrimaryKey(smsQualityOrder.getId());
        if(null == smsQualityOrderRes){
            return R.error("索赔单不存在");
        }
        Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_1.getCode().equals(smsQualityOrderRes.getQualityStatus())
                ||QualityStatusEnum.QUALITY_STATUS_7.getCode().equals(smsQualityOrderRes.getQualityStatus());
        if(!flagResult){
            return R.error("此索赔单不可申诉");
        }
        //2.修改索赔单信息
        smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_4.getCode());
        smsQualityOrder.setComplaintDate(new Date());
        this.updateByPrimaryKeySelective(smsQualityOrder);
        //3.根据索赔单号所对应的申诉文件订单号查文件
        String orderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_APPEAL_END;
        List<SysOss> sysOssList =  remoteOssService.listByOrderNo(orderNo);
        if(!CollectionUtils.isEmpty(sysOssList)){
            //4.批量删除文件
            StringBuffer idBuffer = new StringBuffer();
            sysOssList.forEach(sysOss -> idBuffer.append(sysOss.getId()).append(","));
            String idsSelect = idBuffer.toString();
            String ids = idsSelect.substring(0,idsSelect.length()-1);
            R deleteSysOssResult = remoteOssService.remove(ids);
            flagResult = "0".equals(deleteSysOssResult.get("code").toString());
            if(!flagResult){
                throw new BusinessException("根据ids删除文件失败");
            }
        }
        //5.新增文件
        for(MultipartFile file : files){
            R uplodeFileResult = remoteOssService.uploadFile(file,orderNo);
            flagResult = "0".equals(uplodeFileResult.get("code").toString());
            if(!flagResult){
                throw new BusinessException("新增文件失败");
            }
        }
        return R.ok();
    }
}
