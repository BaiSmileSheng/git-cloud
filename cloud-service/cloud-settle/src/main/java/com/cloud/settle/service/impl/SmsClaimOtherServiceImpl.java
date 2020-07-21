package com.cloud.settle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cloud.activiti.feign.RemoteBizBusinessService;
import com.cloud.common.constant.DeleteFlagConstants;
import com.cloud.common.constant.EmailConstants;
import com.cloud.common.core.domain.R;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.DateUtils;
import com.cloud.common.utils.StringUtils;
import com.cloud.settle.enums.ClaimOtherStatusEnum;
import com.cloud.settle.mail.MailService;
import com.cloud.system.domain.entity.CdFactoryInfo;
import com.cloud.system.domain.entity.CdSupplierInfo;
import com.cloud.system.domain.entity.SysOss;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteFactoryInfoService;
import com.cloud.system.feign.RemoteOssService;
import com.cloud.system.feign.RemoteSequeceService;
import com.cloud.system.feign.RemoteSupplierInfoService;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloud.settle.mapper.SmsClaimOtherMapper;
import com.cloud.settle.domain.entity.SmsClaimOther;
import com.cloud.settle.service.ISmsClaimOtherService;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 其他索赔Service业务层处理
 *
 * @author cs
 * @date 2020-06-02
 */
@Service
public class SmsClaimOtherServiceImpl extends BaseServiceImpl<SmsClaimOther> implements ISmsClaimOtherService {

    private static Logger logger = LoggerFactory.getLogger(SmsClaimOtherServiceImpl.class);

    @Autowired
    private SmsClaimOtherMapper smsClaimOtherMapper;

    @Autowired
    private RemoteSequeceService remoteSequeceService;

    @Autowired
    private RemoteOssService remoteOssService;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RemoteBizBusinessService remoteBizBusinessService;

    @Autowired
    private RemoteFactoryInfoService remoteFactoryInfoService;

    @Autowired
    private RemoteSupplierInfoService remoteSupplierInfoService;

    /**
     * 索赔单所对应的索赔文件订单号后缀
     */
    private static final String ORDER_NO_OTHER_CLAIM_END = "_01";

    /**
     * 索赔单所对应的申诉文件订单号后缀
     */
    private static final String ORDER_NO_OTHER_APPEAL_END = "_02";

    /**
     * 索赔单序列号生成所对应的序列
     */
    private static final String OTHER_SEQ_NAME = "other_id";
    /**
     * 索赔单序列号生成所对应的序列长度
     */
    private static final int OTHER_SEQ_LENGTH = 4;

    /**
     * 生成索赔单前缀
     */
    private static final String OTHER_ORDER_PRE = "QT";

    /**
     * 其他索赔管理服务相关流程key
     */
    public static final String ACTIVITI_PRO_DEF_KEY_CHAIM_OTHER_TEST = "claimOther";

    @Override
    public R selectById(Long id) {
        logger.info("根据id查询其他索赔单详情 id:{}",id);
        SmsClaimOther smsClaimOtherRes = smsClaimOtherMapper.selectByPrimaryKey(id);
        if(null != smsClaimOtherRes || StringUtils.isNotBlank(smsClaimOtherRes.getClaimCode())){
            Map<String,Object> map = new HashMap<>();
            //索赔文件编号
            String claimOrderNo =smsClaimOtherRes.getClaimCode() + ORDER_NO_OTHER_CLAIM_END;
            R claimListR = remoteOssService.listByOrderNo(claimOrderNo);
            if(!claimListR.isSuccess()){
                logger.error("根据id查询其他索赔单详情时获取索赔图片信息失败claimOrderNo:{},res:{}",
                        claimOrderNo,JSONObject.toJSON(claimListR));
                throw new BusinessException("根据id查询其他索赔单详情时获取索赔图片信息失败");
            }
            List<SysOss> claimListReault = claimListR.getCollectData(new TypeReference<List<SysOss>>() {});
            //如果申诉过则查申诉文件
            if(StringUtils.isNotBlank(smsClaimOtherRes.getComplaintDescription())){
                //申诉文件编号
                String appealOrderNo = smsClaimOtherRes.getClaimCode() + ORDER_NO_OTHER_APPEAL_END;
                R appealListR = remoteOssService.listByOrderNo(appealOrderNo);
                if(!appealListR.isSuccess()){
                    logger.error("根据id查询其他索赔单详情时获取申诉图片信息失败appealOrderNo:{},res:{}",
                            claimOrderNo,JSONObject.toJSON(appealListR));
                    throw new BusinessException("根据id查询其他索赔单详情时获取审诉图片信息失败");
                }
                List<SysOss> appealListReault = appealListR.getCollectData(new TypeReference<List<SysOss>>() {});
                map.put("appealSysOssList",appealListReault);

                R businessR = remoteBizBusinessService.selectByKeyAndTable(ACTIVITI_PRO_DEF_KEY_CHAIM_OTHER_TEST,id.toString());
                if(!businessR.isSuccess()){
                    logger.error("获取流程图失败 res:{}",JSONObject.toJSONString(businessR));
                    throw new BusinessException(businessR.get("msg").toString());
                }
                String procInstId = businessR.getStr("data");
                map.put("procInstId", procInstId);
            }

            map.put("smsClaimOther",smsClaimOtherRes);
            map.put("claimSysOssList",claimListReault);
            return R.ok(map);
        }
        return R.error("查询索赔单失败");
    }

    /**
     * 新增其他索赔信息(包含文件信息)
     * @param smsClaimOther 其他索赔信息
     * @param ossIds 文件信息
     * @return 新增结果
     */
    @GlobalTransactional
    @Override
    public R insertClaimOtherAndOss(SmsClaimOther smsClaimOther,String ossIds) {

        //校验供应商,工厂存在合理性
        verifyParameter(smsClaimOther);
        //1.生成单号 索赔单号生成规则 QT+年月日+4位顺序号，循序号每日清零
        StringBuffer qualityNoBuffer = new StringBuffer(OTHER_ORDER_PRE);
        qualityNoBuffer.append(DateUtils.getDate().replace("-",""));
        R seqResult = remoteSequeceService.selectSeq(OTHER_SEQ_NAME,OTHER_SEQ_LENGTH);
        if(!seqResult.isSuccess()){
            return R.error("新增其他索赔信息时获取序列号异常");
        }
        String seq = seqResult.getStr("data");
        logger.info("新增其他索赔信息时获取序列号seq:{}",seq);
        qualityNoBuffer.append(seq);
        smsClaimOther.setClaimCode(qualityNoBuffer.toString());
        smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_0.getCode());
        //填写付款公司
        setCompanyCode(smsClaimOther);
        //2.插入其他索赔信息
        smsClaimOtherMapper.insertSelective(smsClaimOther);
        logger.info("新增其他索赔信息时成功后 主键id:{},索赔单号:{}",smsClaimOther.getId(),smsClaimOther.getClaimCode());

        //上传其他索赔附件上传的时候order_no 为 索赔单号_01
        //3.根据订单号新增文件
        if(StringUtils.isNotBlank(ossIds)){
            String[] ossIdsString = ossIds.split(",");
            String orderNo = smsClaimOther.getClaimCode() + ORDER_NO_OTHER_CLAIM_END;
            List<SysOss> sysOssList = new ArrayList<>();
            for(String ossId : ossIdsString){
                SysOss sysOss = new SysOss();
                sysOss.setId(Long.valueOf(ossId));
                sysOss.setOrderNo(orderNo);
                sysOssList.add(sysOss);
            }
            R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
            if(!uplodeFileResult.isSuccess()){
                logger.error("新增其他索赔时新增文件失败订单号 orderNo:{},res:{}",orderNo, JSONObject.toJSON(uplodeFileResult));
                throw new BusinessException("新增其他索赔时新增质新增文件失败");
            }
        }

        //4.若直接提交调用提交接口
        if(smsClaimOther.getFlagCommit()){
            R resultResult = submit(smsClaimOther.getId().toString());
            if(!resultResult.isSuccess()){
                logger.error("新增其他索赔时提交失败 id:{},res:{}",smsClaimOther.getId(), JSONObject.toJSON(resultResult));
                throw new BusinessException("新增其他索赔时提交失败");
            }
        }
        return R.data(smsClaimOther.getId());
    }

    /**
     * 赋值付款公司
     * @param smsClaimOther
     */
    private void setCompanyCode(SmsClaimOther smsClaimOther) {
        //查付款公司
        R factoryInfoResult = remoteFactoryInfoService.selectOneByFactory(smsClaimOther.getFactoryCode());
        if(!factoryInfoResult.isSuccess()){
            logger.error("获取付款公司异常 工厂:{},res:{}",smsClaimOther.getFactoryCode(), JSONObject.toJSON(factoryInfoResult));
            throw new BusinessException("请维护工厂"+ smsClaimOther.getFactoryCode() +"对应的付款公司");
        }
        CdFactoryInfo cdFactoryInfo = factoryInfoResult.getData(CdFactoryInfo.class);
        if(StringUtils.isBlank(cdFactoryInfo.getCompanyCode())){
            logger.error("获取付款公司异常 工厂:{}",smsClaimOther.getFactoryCode());
            throw new BusinessException("请维护工厂"+ smsClaimOther.getFactoryCode() +"对应的付款公司");
        }
        smsClaimOther.setCompanyCode(cdFactoryInfo.getCompanyCode());
    }

    /**
     * 校验供应商,工厂存在合理性
     * @param smsClaimOther
     */
    private void verifyParameter(SmsClaimOther smsClaimOther){
        R factoryResult = remoteFactoryInfoService.selectOneByFactory(smsClaimOther.getFactoryCode());
        if(!factoryResult.isSuccess()){
            logger.error("查工厂信息失败工厂:{},res:{}",smsClaimOther.getFactoryCode(),JSONObject.toJSON(factoryResult));
            throw new BusinessException("校验工厂异常" + factoryResult.get("msg").toString());
        }
        R supplierResult = remoteSupplierInfoService.selectOneBySupplierCode(smsClaimOther.getSupplierCode());
        if(!supplierResult.isSuccess()){
            logger.error("查供应商信息失败供应商:{},res:{}",smsClaimOther.getSupplierCode(),JSONObject.toJSON(supplierResult));
            throw new BusinessException("校验供应商异常" + supplierResult.get("msg").toString());
        }
    }
    /**
     * 修改保存其他索赔(包含图片信息)
     * @param smsClaimOther  其他索赔信息
     * @param ossIds 文件信息
     * @return 修改成功或失败
     */
    @GlobalTransactional
    @Override
    public R updateClaimOtherAndOss(SmsClaimOther smsClaimOther,String ossIds) {
        logger.info("修改其他索赔单信息 id:{},claimCode:{}",smsClaimOther.getId(),smsClaimOther.getClaimCode());

        //校验供应商,工厂存在合理性
        verifyParameter(smsClaimOther);
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        SmsClaimOther smsClaimOtherRes = smsClaimOtherMapper.selectByPrimaryKey(smsClaimOther.getId());
        if(null == smsClaimOtherRes){
            logger.error("根据id查询其他索赔单不存在 id:{}",smsClaimOther.getId());
            throw new BusinessException("其他索赔单不存在");
        }
        Boolean flagResult = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_0.getCode().equals(smsClaimOtherRes.getClaimOtherStatus());
        if(!flagResult){
            logger.error("此索赔单已提交不可再编辑 id:{},claimOtherStatus:{}",smsClaimOtherRes.getId(),smsClaimOtherRes.getClaimOtherStatus());
            throw new BusinessException("此索赔单已提交不可再编辑");
        }
        //2.修改索赔单信息
        //填写付款公司
        setCompanyCode(smsClaimOther);
        smsClaimOtherMapper.updateByPrimaryKeySelective(smsClaimOther);
        //3.根据索赔单号对所对应的文件修改(先删后增)
        if(StringUtils.isNotBlank(ossIds)){
            String[] ossIdsString = ossIds.split(",");
            if(ossIdsString.length >0 ){
                String orderNo = smsClaimOtherRes.getClaimCode() + ORDER_NO_OTHER_CLAIM_END;
                List<SysOss> sysOssList = new ArrayList<>();
                for(String ossId : ossIdsString){
                    SysOss sysOss = new SysOss();
                    sysOss.setId(Long.valueOf(ossId));
                    sysOss.setOrderNo(orderNo);
                    sysOssList.add(sysOss);
                }
                R uplodeFileResult = remoteOssService.batchEditSaveById(sysOssList);
                if (!uplodeFileResult.isSuccess()) {
                    logger.error("修改其他索赔时会写文件订单号失败 orderNo:{},ossIds:{},res:{}", orderNo, ossIds,JSONObject.toJSON(uplodeFileResult));
                    throw new BusinessException("修改其他索赔时会写文件订单号失败");
                }
            }
        }

        //4.若直接提交调用提交接口
        if(smsClaimOther.getFlagCommit()){
            R resultResult = submit(smsClaimOther.getId().toString());
            if(!resultResult.isSuccess()){
                logger.error("修改其他索赔时提交失败 id:{},res:{}",smsClaimOther.getId(), JSONObject.toJSON(resultResult));
                throw new BusinessException("修改其他索赔时提交失败");
            }
        }
        return R.ok();
    }

    /**
     * 删除其他索赔
     * @param ids 主键
     * @return 成功或失败
     */
    @GlobalTransactional
    @Override
    public R deleteClaimOtherAndOss(String ids) {
        logger.info("批量删除其他索赔单 ids:{}",ids);
        //1.根据ids查其他索赔单信息校验状态
        List<SmsClaimOther> selectListResult =  smsClaimOtherMapper.selectByIds(ids);
        if(CollectionUtils.isEmpty(selectListResult)){
            logger.error("删除其他索赔单时失败,其他索赔单不存在 ids:{}",ids);
            throw new BusinessException("其他索赔单不存在");
        }

        Boolean flagResult;
        for(SmsClaimOther smsClaimOther : selectListResult){
            flagResult = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_0.getCode().equals(smsClaimOther.getClaimOtherStatus());
            if(!flagResult){
                logger.error("删除其他索赔单失败 id:{},claimOtherStatus:{}",smsClaimOther.getId(),smsClaimOther.getClaimOtherStatus());
                throw new BusinessException("请确认其他索赔单状态是否为待提交");
            }
        }
        //2.根据订单号删除文件
        List<SmsClaimOther> smsClaimOtherListDelete = new ArrayList<>();//批量逻辑删除集合
        for(SmsClaimOther smsClaimOther : selectListResult){
            String orderNo = smsClaimOther.getClaimCode() + ORDER_NO_OTHER_CLAIM_END;
            R resultOss = remoteOssService.deleteListByOrderNo(orderNo);
            if(!resultOss.isSuccess()){
                logger.error("修改其他索赔单时修改文件信息失败 orderNo:{},res:{}",orderNo,JSONObject.toJSON(resultOss));
                throw new BusinessException("修改其他索赔单时修改文件信息失败");
            }
            SmsClaimOther smsClaimOtherDelete = new SmsClaimOther();
            smsClaimOtherDelete.setId(smsClaimOther.getId());
            smsClaimOtherDelete.setDelFlag(DeleteFlagConstants.HAVE_DELETED);
            smsClaimOtherListDelete.add(smsClaimOtherDelete);
        }
        //3.删除其他质量索赔信息
        smsClaimOtherMapper.updateBatchByPrimaryKeySelective(smsClaimOtherListDelete);
        return R.ok();
    }

    /**
     * 提交其他索赔单
     * @param ids 主键id
     * @return 提交结果成功或失败
     */
    @Override
    public R submit(String ids) {
        logger.info("提交其他索赔单 ids:{}",ids);
        List<SmsClaimOther> selectListResult =  smsClaimOtherMapper.selectByIds(ids);
        if(CollectionUtils.isEmpty(selectListResult)){
            logger.error("提交其他索赔单失败,其他索赔单不存在 ids:{}",ids);
            throw new BusinessException("其他索赔单不存在");
        }
        //1.校验状态,是否有图片
        Map<String,SysUserVo> mapEmail = new HashMap<>(); //key是单号,value是供应商信息;
        for(SmsClaimOther smsClaimOther : selectListResult){
            Boolean flagResult = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_0.getCode().equals(smsClaimOther.getClaimOtherStatus());
            String claimCode = smsClaimOther.getClaimCode();
            if(!flagResult){
                logger.error("提交其他索赔单失败,状态异常 id:{},claimOtherStatus:{}",
                        smsClaimOther.getId(),smsClaimOther.getClaimOtherStatus());
                throw new BusinessException("请确认其他索赔单"+ claimCode +"状态是否为待提交");
            }
            //查图片
            String orderNo = claimCode + ORDER_NO_OTHER_CLAIM_END;
            R resultOss = remoteOssService.listByOrderNo(orderNo);
            if(!resultOss.isSuccess()){
                logger.error("提交其他索赔单失败,没有图片 claimCode:{}",claimCode);
                throw new BusinessException("请对其他索赔单"+ claimCode +"上传图片再提交");
            }
            String supplierCode = smsClaimOther.getSupplierCode();
            //根据供应商编号查询供应商信息
            R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
            if(!sysUserR.isSuccess()){
                logger.error("提交其他索赔时查询供应商信息失败供应商编号 supplierCode:{}",supplierCode);
                throw new BusinessException("提交其他索赔时查询供应商信息失败");
            }
            SysUserVo sysUser = sysUserR.getData(SysUserVo.class);
            String toSupplier = sysUser.getEmail();
            String userName = sysUser.getUserName();
            if(StringUtils.isBlank(toSupplier)){
                logger.error("提交其他索赔时查询供应商信息邮箱不存在 供应商编号 supplierCode:{}", supplierCode);
                throw new BusinessException("提交其他索赔时查询供应商"+userName+"信息邮箱不存在,请维护");
            }
            mapEmail.put(claimCode,sysUser);
            //设置提交状态和时间
            smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1.getCode());
            smsClaimOther.setSubmitDate(new Date());
        }
        //2.批量修改状态
        Integer count =  smsClaimOtherMapper.updateBatchByPrimaryKeySelective(selectListResult);
        //3.发送邮件(避免异常时邮件已发送)
        for(SmsClaimOther smsClaimOther : selectListResult){
            String orderNo = smsClaimOther.getClaimCode();
            SysUserVo sysUserVo = mapEmail.get(orderNo);
            String mailSubject = "其他索赔邮件";
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条其他索赔订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(sysUserVo.getCorporation()).append(smsClaimOther.getSupplierCode())
                    .append("：").append("您有一条其他索赔单，单号")
                    .append(smsClaimOther.getClaimCode()).append("，请及时处理，72小时不处理，系统将自动确认，无法申诉!")
                    .append("\n系统登录地址：\n")
                    .append(EmailConstants.ORW_URL);
            mailService.sendTextMail(sysUserVo.getEmail(),mailSubject,mailTextBuffer.toString());
        }
        return R.data(count);
    }

    /**
     * 供应商确认索赔单
     * @param ids 主键id
     * @return 供应商确认成功或失败
     */
    @Override
    public R supplierConfirm(String ids,SysUser sysUser) {
        logger.info("供应商确认索赔单 ids:{}",ids);
        String supplierCodeLogin = sysUser.getSupplierCode();
        if(StringUtils.isBlank(supplierCodeLogin)){
            return R.error("非供应商用户,请勿操作");
        }
        List<SmsClaimOther> selectListResult =  smsClaimOtherMapper.selectByIds(ids);
        if(CollectionUtils.isEmpty(selectListResult)){
            logger.error("供应商确认其他索赔单失败,其他索赔单不存在 ids:{}",ids);
            throw new BusinessException("其他索赔单不存在");
        }
        //校验状态
        for(SmsClaimOther smsClaimOther : selectListResult){
            Boolean flagResult = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1.getCode().equals(smsClaimOther.getClaimOtherStatus())
                    ||ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_7.getCode().equals(smsClaimOther.getClaimOtherStatus());
            if(!flagResult){
                logger.error("供应商确认其他索赔单失败,状态异常 id:{},claimOtherStatus:{}",
                        smsClaimOther.getId(),smsClaimOther.getClaimOtherStatus());
                throw new BusinessException("请确认其他索赔单状态是否为待供应商确认");
            }
            if(!smsClaimOther.getSupplierCode().equals(supplierCodeLogin)){
                logger.error("供应商确认其他索赔单失败,供应商信息异常 supplierCode:{},supplierCodeLogin:{}",
                        smsClaimOther.getSupplierCode(), supplierCodeLogin);
                throw new BusinessException("请勿操作其他供应商的数据");
            }
            smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_11.getCode());
            smsClaimOther.setSettleFee(smsClaimOther.getClaimPrice());
            smsClaimOther.setSupplierConfirmDate(new Date());
        }
        int count = smsClaimOtherMapper.updateBatchByPrimaryKeySelective(selectListResult);
        return R.data(count);
    }

    /**
     * 索赔单供应商申诉(包含文件信息)
     * @param smsClaimOther 其他索赔信息
     * @return 索赔单供应商申诉结果成功或失败
     */
    @GlobalTransactional
    @Override
    public R supplierAppeal(SmsClaimOther smsClaimOther, String ossIds) {
        String[] ossIdsString = ossIds.split(",");
        if(ossIdsString.length == 0){
            throw new BusinessException("上传图片id不能为空");
        }
        //1.查询索赔单数据,判断状态是否是待提交,待提交可修改
        SmsClaimOther smsClaimOtherRes = smsClaimOtherMapper.selectByPrimaryKey(smsClaimOther.getId());
        if(null == smsClaimOtherRes){
            logger.error("供应商申诉的其他索赔单不存在 id:{}",smsClaimOther.getId());
            throw new BusinessException("索赔单不存在");
        }
        Boolean flagResult = ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1.getCode().equals(smsClaimOtherRes.getClaimOtherStatus())
                ||ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_7.equals(smsClaimOtherRes.getClaimOtherStatus());
        if(!flagResult){
            logger.error("供应商申诉的其他索赔单 状态异常 id:{},claimOtherStatus:{}",
                    smsClaimOther.getId(),smsClaimOtherRes.getClaimOtherStatus());
            throw new BusinessException("此索赔单不可申诉");
        }
        //2.修改索赔单信息
        smsClaimOtherRes.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_3.getCode());
        smsClaimOtherRes.setComplaintDate(new Date());
        smsClaimOtherMapper.updateByPrimaryKeySelective(smsClaimOtherRes);
        String orderNo = smsClaimOtherRes.getClaimCode() + ORDER_NO_OTHER_APPEAL_END;
        //3.根据订单号新增文件
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
     * @return 成功或失败
     */
    @Override
    public R overTimeSendMail() {
        //1.查询状态是待供应商确认的 提交时间<=2天前的 >3天前的
        String twoDate = DateUtils.getDaysTimeString(-2);
        String threeDate = DateUtils.getDaysTimeString(-3);
        List<SmsClaimOther> smsClaimOtherList = overTimeSelect(twoDate,threeDate);
        if(CollectionUtils.isEmpty(smsClaimOtherList)){
            return R.ok();
        }
        //2.发送邮件
        for(SmsClaimOther smsClaimOther : smsClaimOtherList){
            String supplierCode = smsClaimOther.getSupplierCode();
            //根据供应商编号查询供应商信息
            R sysUserR = remoteUserService.findUserBySupplierCode(supplierCode);
            if(!sysUserR.isSuccess()){
                logger.error("定时发送邮件时查询供应商信息失败供应商编号 supplierCode:{}",supplierCode);
                throw new BusinessException("定时发送邮件时查询供应商信息失败");
            }
            SysUserVo sysUser = sysUserR.getData(SysUserVo.class);
            String mailSubject = "其他索赔邮件";
            StringBuffer mailTextBuffer = new StringBuffer();
            // 供应商名称 +V码+公司  您有一条其他索赔订单，订单号XXXXX，请及时处理，如不处理，3天后系统自动确认，无法申诉
            mailTextBuffer.append(sysUser.getCorporation()).append(supplierCode)
                    .append("：").append("您有一条其他索赔单，单号")
                    .append(smsClaimOther.getClaimCode()).append("，请及时处理，24小时不处理，系统将自动确认，无法申诉!")
                    .append("\n系统登录地址：\n")
                    .append(EmailConstants.ORW_URL);
            String toSupplier = sysUser.getEmail();
            mailService.sendTextMail(toSupplier,mailSubject,mailTextBuffer.toString());
        }
        return R.ok();
    }

    /**
     * 获取超时未确认的列表
     * @param submitDateStart 提交时间起始值
     * @param submitDateEnd 提交时间结束值
     * @return
     */
    private List<SmsClaimOther> overTimeSelect(String submitDateStart,String submitDateEnd){
        Example example = new Example(SmsClaimOther.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("claimOtherStatus",ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_1);
        criteria.andGreaterThanOrEqualTo("submitDate",submitDateStart);
        criteria.andLessThan("submitDate",submitDateEnd);
        List<SmsClaimOther> smsQualityOrderList = smsClaimOtherMapper.selectByExample(example);
        return smsQualityOrderList;
    }
    /**
     * 72H超时供应商自动确认
     * @return 成功或失败
     */
    @Override
    public R overTimeConfim() {
        //1.查询状态是待供应商确认的 提交时间<=3天前的 >4天前的
        String threeDate = DateUtils.getDaysTimeString(-3);
        String fourDate = DateUtils.getDaysTimeString(-4);
        List<SmsClaimOther> smsClaimOtherList = overTimeSelect(threeDate,fourDate);
        int count = 0;
        if(!CollectionUtils.isEmpty(smsClaimOtherList)){
            for(SmsClaimOther smsClaimOther : smsClaimOtherList){
                smsClaimOther.setClaimOtherStatus(ClaimOtherStatusEnum.CLAIM_OTHER_STATUS_11.getCode());
                smsClaimOther.setSettleFee(smsClaimOther.getClaimPrice());
                smsClaimOther.setSupplierConfirmDate(new Date());
            }
            count = smsClaimOtherMapper.updateBatchByPrimaryKeySelective(smsClaimOtherList);
        }
        return R.data(count);
    }
}
