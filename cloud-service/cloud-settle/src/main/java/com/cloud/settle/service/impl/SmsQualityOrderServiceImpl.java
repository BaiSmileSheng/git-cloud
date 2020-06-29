package com.cloud.settle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.domain.entity.SmsQualityOrder;
import com.cloud.settle.enums.QualityStatusEnum;
import com.cloud.settle.mail.MailService;
import com.cloud.settle.mapper.SmsQualityOrderMapper;
import com.cloud.settle.service.ISmsQualityOrderService;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteOssService;
import com.cloud.system.feign.RemoteSequeceService;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
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

    private static Logger logger = LoggerFactory.getLogger(SmsQualityOrderServiceImpl.class);

    @Autowired
    private SmsQualityOrderMapper smsQualityOrderMapper;
    @Autowired
    private RemoteOssService remoteOssService;
    @Autowired
    private RemoteSequeceService remoteSequeceService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private MailService mailService;

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
     *
     * @param id 主键id
     * @return 索赔信息(包括文件信息)
     */
    @Override
    public R selectById(Long id) {
        logger.info("根据id查询质量索赔单详情 id:{}", id);
        Map<String, Object> map = new HashMap<>();
        SmsQualityOrder smsQualityOrderRes = smsQualityOrderMapper.selectByPrimaryKey(id);
        if (null != smsQualityOrderRes || StringUtils.isNotBlank(smsQualityOrderRes.getQualityNo())) {
            //索赔文件编号
            String claimOrderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
            R claimListR = remoteOssService.listByOrderNo(claimOrderNo);
            if(!claimListR.isSuccess()){
                logger.error("据id查询质量索赔单详情获取索赔图片信息失败claimOrderNo:{},res:{}",
                        claimOrderNo, JSONObject.toJSON(claimListR));
                throw new BusinessException("据id查询质量索赔单详情获取图片信息失败");
            }
            List<SysOss> claimListReault = claimListR.getCollectData(new TypeReference<List<SysOss>>() {});
            //如果申诉过再查申诉文件
            if(StringUtils.isNotBlank(smsQualityOrderRes.getComplaintDescription())){
                //申诉文件编号
                String appealOrderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_APPEAL_END;
                R appealListR  = remoteOssService.listByOrderNo(appealOrderNo);
                if(!appealListR.isSuccess()){
                    logger.error("据id查询质量索赔单详情获取申诉图片信息失败claimOrderNo:{},res:{}",
                            claimOrderNo, JSONObject.toJSON(appealListR));
                    throw new BusinessException("据id查询质量索赔单详情获取图片信息失败");
                }
                List<SysOss> appealListReault = appealListR.getCollectData(new TypeReference<List<SysOss>>() {});
                map.put("appealSysOssList", appealListReault);

            }
            map.put("smsQualityOrder", smsQualityOrderRes);
            map.put("claimSysOssList", claimListReault);
            return R.ok(map);
        }

        return R.error("查询质量索赔单失败");
    }

    /**
     * 新增质量索赔信息
     *
     * @param smsQualityOrder 质量索赔信息
     * @param ossIds           质量索赔对应的文件信息
     * @return
     */

    @GlobalTransactional
    @Override
    public R addSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder,String ossIds) {

        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        //1.索赔单号生成规则 ZL+年月日+4位顺序号，循序号每日清零
        StringBuffer qualityNoBuffer = new StringBuffer(QUALITY_ORDER_PRE);
        qualityNoBuffer.append(DateUtils.getDate().replace("-", ""));
        R seqR = remoteSequeceService.selectSeq(QUALITY_SEQ_NAME, QUALITY_SEQ_LENGTH);
        if (!seqR.isSuccess()) {
            throw new BusinessException("新增质量索赔信息时获取序列号异常");
        }
        String seq = seqR.getStr("data");
        logger.info("新增质量索赔信息时获取序列号seq:{}", seq);
        qualityNoBuffer.append(seq);
        smsQualityOrder.setQualityNo(qualityNoBuffer.toString());
        smsQualityOrderMapper.insertSelective(smsQualityOrder);
        logger.info("新增质量索赔信息时成功后 主键id:{},索赔单号:{}", smsQualityOrder.getId(), smsQualityOrder.getQualityNo());

        //2.上传质量索赔附件上传的时候order_no 为 索赔单号_01
        String orderNo = smsQualityOrder.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;

        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        if (!uplodeFileResult.isSuccess()) {
            logger.error("新增质量索赔时会写文件订单号失败 orderNo:{},ossIds:{},res:{}", orderNo, ossIds,JSONObject.toJSON(uplodeFileResult));
            throw new BusinessException("新增质量索赔时会写文件订单号失败");
        }
        return R.data(smsQualityOrder.getId());
    }

    /**
     * 修改质量索赔信息
     *
     * @param smsQualityOrder 质量索赔信息
     * @param ossIds           质量索赔对应的文件信息
     * @return
     */
    @GlobalTransactional
    @Override
    public R updateSmsQualityOrderAndSysOss(SmsQualityOrder smsQualityOrder, String ossIds) {
        logger.info("修改质量索赔单信息 id:{},qualityNo:{}", smsQualityOrder.getId(), smsQualityOrder.getQualityNo());

        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        SmsQualityOrder smsQualityOrderRes = smsQualityOrderMapper.selectByPrimaryKey(smsQualityOrder.getId());
        if (null == smsQualityOrderRes) {
            logger.error("根据id查询质量索赔单不存在 id:{}", smsQualityOrder.getId());
            throw new BusinessException("索赔单不存在");
        }
        Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrderRes.getQualityStatus());
        if (!flagResult) {
            logger.error("此索赔单已提交不可再编辑 id:{},qualityStatus:{}", smsQualityOrderRes.getId(), smsQualityOrderRes.getQualityStatus());
            throw new BusinessException("此索赔单已提交不可再编辑");
        }
        //2.修改索赔单信息
        smsQualityOrderMapper.updateByPrimaryKeySelective(smsQualityOrder);
        //3.根据索赔单号所对应的索赔文件订单号查文件
        if(StringUtils.isNotBlank(ossIds)){
            String[] ossIdsString = ossIds.split(",");
            if(ossIdsString.length >0 ){
                String orderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
                List<SysOss> sysOssList = new ArrayList<>();
                for(String ossId : ossIdsString){
                    SysOss sysOss = new SysOss();
                    sysOss.setId(Long.valueOf(ossId));
                    sysOss.setOrderNo(orderNo);
                    sysOssList.add(sysOss);
                }
                R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
                if (!uplodeFileResult.isSuccess()) {
                    logger.error("修改质量索赔时会写文件订单号失败 orderNo:{},ossIds:{},res:{}", orderNo, ossIds,JSONObject.toJSON(uplodeFileResult));
                    throw new BusinessException("修改质量索赔时会写文件订单号失败");
                }
            }
        }
        return R.ok();
    }

    /**
     * 新增或修改时提交质量索赔信息
     *
     * @param smsQualityOrder 质量索赔信息
     * @param ossIds           质量索赔对应的文件信息
     * @return
     */
    @Override
    public R insertOrupdateSubmit(SmsQualityOrder smsQualityOrder, String ossIds) {

        Long id = smsQualityOrder.getId();
        R result;
        if(null == id){
            result = addSmsQualityOrderAndSysOss(smsQualityOrder,ossIds);
            if(!result.isSuccess()){
                logger.error("提交时新增质量索赔信息异常 res{}",JSONObject.toJSONString(result));
                throw new BusinessException(result.get("msg").toString());
            }
            id = result.getLong("data");
        }else{
            result = updateSmsQualityOrderAndSysOss(smsQualityOrder,ossIds);
            if(!result.isSuccess()){
                logger.error("提交时修改质量索赔信息异常 res{}",JSONObject.toJSONString(result));
                throw new BusinessException(result.get("msg").toString());
            }
        }
        result = submit(id.toString());
        if(!result.isSuccess()){
            logger.error("提交时质量索赔信息异常 res{}",JSONObject.toJSONString(result));
            throw new BusinessException(result.get("msg").toString());
        }
        return R.ok();
    }

    /**
     * 删除质量索赔信息
     *
     * @param ids 主键id
     * @return 删除结果成功或失败
     */
    @GlobalTransactional
    @Override
    public R deleteSmsQualityOrderAndSysOss(String ids) {
        logger.info("批量删除质量索赔单 ids:{}", ids);
        //1.根据ids查质量索赔信息校验状态
        List<SmsQualityOrder> selectListResult = smsQualityOrderMapper.selectByIds(ids);
        if (CollectionUtils.isEmpty(selectListResult)) {
            logger.error("删除质量索赔单时失败,质量索赔单不存在 ids:{}", ids);
            throw  new BusinessException("索赔单不存在");
        }
        Boolean flagResult;
        for (SmsQualityOrder smsQualityOrder : selectListResult) {
            flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus());
            if (!flagResult) {
                logger.error("删除质量索赔单失败 id:{},qualityStatus:{}", smsQualityOrder.getId(), smsQualityOrder.getQualityStatus());
                throw  new BusinessException("请确认索赔单状态是否为待提交");
            }
        }
        //2.根据订单号删除文件
        for (SmsQualityOrder smsQualityOrder : selectListResult) {
            String orderNo = smsQualityOrder.getQualityNo() + ORDER_NO_QUALITY_CLAIM_END;
            R resultOss = remoteOssService.deleteListByOrderNo(orderNo);
            if (!resultOss.isSuccess()) {
                logger.error("修改质量索赔单时修改文件信息失败 orderNo:{},res:{}", orderNo, JSONObject.toJSON(resultOss));
                throw  new BusinessException("修改质量索赔单时修改文件信息失败");
            }
        }
        smsQualityOrderMapper.deleteByIds(ids);
        return R.ok();
    }

    /**
     * 根据索赔单主键批量查询
     *
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
     *
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    @GlobalTransactional
    @Override
    public R submit(String ids) {
        logger.info("提交质量索赔单 ids:{}", ids);
        List<SmsQualityOrder> selectListResult = smsQualityOrderMapper.selectByIds(ids);
        if (CollectionUtils.isEmpty(selectListResult)) {
            logger.error("提交质量索赔单失败,质量索赔单不存在 ids:{}", ids);
            throw  new BusinessException("质量索赔单不存在");
        }
        //1.校验状态 发送邮件
        for (SmsQualityOrder smsQualityOrder : selectListResult) {
            Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_0.getCode().equals(smsQualityOrder.getQualityStatus());
            if (!flagResult) {
                logger.error("提交其他索赔单失败,状态异常 id:{},qualityStatus:{}",
                        smsQualityOrder.getId(), smsQualityOrder.getQualityStatus());
                throw  new BusinessException("请确认索赔单状态是否为待提交");
            }

            //发送邮件
            String supplierCode = smsQualityOrder.getSupplierCode();
            //根据供应商编号查询供应商信息
            R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
            if (!sysUserR.isSuccess()) {
                logger.error("提交质量索赔时查询供应商信息失败供应商编号 supplierCode:{}", supplierCode);
                throw new BusinessException("提交质量索赔时查询供应商信息失败");
            }
            SysUserVo sysUser = sysUserR.getData(SysUserVo.class);
            String mailSubject = "质量索赔邮件";
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条质量索赔订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(smsQualityOrder.getSupplierName()).append("+").append(supplierCode).append("+")
                    .append(sysUser.getCorporation()).append(" ").append("您有一条质量索赔订单，订单号")
                    .append(smsQualityOrder.getQualityNo()).append(",请及时处理，如不处理，3天后系统自动确认，无法申诉");
            String toSupplier = sysUser.getEmail();
            mailService.sendTextMail(toSupplier, mailTextBuffer.toString(), mailSubject);

            //设置提交状态
            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_1.getCode());
            smsQualityOrder.setSubmitDate(new Date());
        }

        //2.批量修改为提交
        Integer count = smsQualityOrderMapper.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }

    /**
     * 供应商确认索赔单
     *
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @Override
    public R supplierConfirm(String ids) {
        logger.info("供应商确认索赔单 ids:{}", ids);
        List<SmsQualityOrder> selectListResult = smsQualityOrderMapper.selectByIds(ids);
        if (CollectionUtils.isEmpty(selectListResult)) {
            logger.error("供应商确认质量索赔单失败,质量索赔单不存在 ids:{}", ids);
            throw new BusinessException("质量索赔单不存在");
        }

        for (SmsQualityOrder smsQualityOrder : selectListResult) {
            Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_1.getCode().equals(smsQualityOrder.getQualityStatus())
                    || QualityStatusEnum.QUALITY_STATUS_7.getCode().equals(smsQualityOrder.getQualityStatus());
            if (!flagResult) {
                logger.error("供应商确认质量索赔单失败,状态异常 id:{},qualityStatus:{}",
                        smsQualityOrder.getId(), smsQualityOrder.getQualityStatus());
                throw new BusinessException("请确认索赔单状态是否为待供应商确认");
            }
            smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
            smsQualityOrder.setSettleFee(smsQualityOrder.getClaimAmount());
            smsQualityOrder.setSupplierConfirmDate(new Date());
        }
        int count = smsQualityOrderMapper.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     *
     * @param smsQualityOrder 质量索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    @GlobalTransactional
    @Override
    public R supplierAppeal(SmsQualityOrder smsQualityOrder,String ossIds) {
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        SmsQualityOrder smsQualityOrderRes = smsQualityOrderMapper.selectByPrimaryKey(smsQualityOrder.getId());
        if (null == smsQualityOrderRes) {
            throw new BusinessException("索赔单不存在");
        }
        Boolean flagResult = QualityStatusEnum.QUALITY_STATUS_1.getCode().equals(smsQualityOrderRes.getQualityStatus())
                || QualityStatusEnum.QUALITY_STATUS_7.getCode().equals(smsQualityOrderRes.getQualityStatus());
        if (!flagResult) {
            throw new BusinessException("此索赔单不可申诉");
        }
        //2.修改索赔单信息
        smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_4.getCode());
        smsQualityOrder.setComplaintDate(new Date());
        smsQualityOrderMapper.updateByPrimaryKeySelective(smsQualityOrder);
        //3.根据订单号新增文件
        String orderNo = smsQualityOrderRes.getQualityNo() + ORDER_NO_QUALITY_APPEAL_END;
        List<SysOss> sysOssList = new ArrayList<>();
        for(String ossId : ossIdsString){
            SysOss sysOss = new SysOss();
            sysOss.setId(Long.valueOf(ossId));
            sysOss.setOrderNo(orderNo);
            sysOssList.add(sysOss);
        }
        R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
        return uplodeFileResult;
    }

    /**
     * 48未确认超时发送邮件
     *
     * @return 成功或失败
     */
    @Override
    public R overTimeSendMail() {
        //1.查询状态是待供应商确认的 提交时间<=2天前的 >3天前的
        String twoDate = DateUtils.getDaysTimeString(-2);
        String threeDate = DateUtils.getDaysTimeString(-3);
        List<SmsQualityOrder> smsQualityOrderList = overTimeSelect(twoDate, threeDate);
        if (CollectionUtils.isEmpty(smsQualityOrderList)) {
            return R.ok();
        }
        //2.发送邮件
        for (SmsQualityOrder smsQualityOrder : smsQualityOrderList) {
            String supplierCode = smsQualityOrder.getSupplierCode();
            //根据供应商编号查询供应商信息
            R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
            if (!sysUserR.isSuccess()) {
                logger.error("定时发送邮件时查询供应商信息失败供应商编号 supplierCode:{}", supplierCode);
                throw new BusinessException("定时发送邮件时查询供应商信息失败");
            }
            SysUserVo sysUser = sysUserR.getData(SysUserVo.class);
            String mailSubject = "质量索赔邮件";
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条质量索赔订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(smsQualityOrder.getSupplierName()).append("+").append(supplierCode).append("+")
                    .append(sysUser.getCorporation()).append(" ").append("您有一条质量索赔订单，订单号")
                    .append(smsQualityOrder.getQualityNo()).append(",请及时处理，如不处理，1天后系统自动确认，无法申诉");
            String toSupplier = sysUser.getEmail();
            mailService.sendTextMail(toSupplier, mailTextBuffer.toString(), mailSubject);
        }
        return R.ok();
    }

    /**
     * 获取超时未确认的列表
     *
     * @param submitDateStart 提交时间起始值
     * @param submitDateEnd   提交时间结束值
     * @return
     */
    private List<SmsQualityOrder> overTimeSelect(String submitDateStart, String submitDateEnd) {
        Example example = new Example(SmsQualityOrder.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("qualityStatus", QualityStatusEnum.QUALITY_STATUS_1);
        criteria.andGreaterThanOrEqualTo("submitDate", submitDateStart);
        criteria.andLessThan("submitDate", submitDateEnd);
        List<SmsQualityOrder> smsQualityOrderList = smsQualityOrderMapper.selectByExample(example);
        return smsQualityOrderList;
    }

    /**
     * 72H超时供应商自动确认
     *
     * @return 成功或失败
     */
    @Override
    public R overTimeConfim() {
        //1.查询状态是待供应商确认的 提交时间<=3天前的 >4天前的
        String threeDate = DateUtils.getDaysTimeString(-3);
        String fourDate = DateUtils.getDaysTimeString(-4);
        List<SmsQualityOrder> smsQualityOrderList = overTimeSelect(threeDate, fourDate);
        int count = 0;
        if (!CollectionUtils.isEmpty(smsQualityOrderList)) {
            for (SmsQualityOrder smsQualityOrder : smsQualityOrderList) {
                smsQualityOrder.setQualityStatus(QualityStatusEnum.QUALITY_STATUS_11.getCode());
                smsQualityOrder.setSettleFee(smsQualityOrder.getClaimAmount());
                smsQualityOrder.setSupplierConfirmDate(new Date());
            }
            count = smsQualityOrderMapper.updateBatchByPrimaryKeySelective(smsQualityOrderList);
        }
        return R.data(count);
    }
}
