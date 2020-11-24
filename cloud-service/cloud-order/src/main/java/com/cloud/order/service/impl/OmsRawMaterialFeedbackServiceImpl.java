package com.cloud.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.common.constant.*;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.impl.BaseServiceImpl;
import com.cloud.common.exception.BusinessException;
import com.cloud.common.utils.bean.BeanUtils;
import com.cloud.order.domain.entity.OmsProductionOrder;
import com.cloud.order.domain.entity.OmsProductionOrderDetail;
import com.cloud.order.domain.entity.OmsRawMaterialFeedback;
import com.cloud.order.mail.MailService;
import com.cloud.order.mapper.OmsRawMaterialFeedbackMapper;
import com.cloud.order.service.IOmsProductionOrderDetailService;
import com.cloud.order.service.IOmsProductionOrderService;
import com.cloud.order.service.IOmsRawMaterialFeedbackService;
import com.cloud.order.util.DataScopeUtil;
import com.cloud.system.domain.entity.CdBomInfo;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.domain.vo.SysUserRights;
import com.cloud.system.domain.vo.SysUserVo;
import com.cloud.system.feign.RemoteBomService;
import com.cloud.system.feign.RemoteUserService;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 原材料反馈信息 Service业务层处理
 *
 * @author ltq
 * @date 2020-06-22
 */
@Service
@Slf4j
public class OmsRawMaterialFeedbackServiceImpl extends BaseServiceImpl<OmsRawMaterialFeedback> implements IOmsRawMaterialFeedbackService {
    private static final String APPROVAL_FLAG_ADOPT = "1";
    private static final String APPROVAL_FLAG_REJECT = "0";
    //反馈信息状态
    private static final String FEEDBACK_STATUS_ZERO = "0";
    private static final String FEEDBACK_STATUS_ONE = "1";
    private static final String FEEDBACK_STATUS_TWO = "2";

    @Autowired
    private OmsRawMaterialFeedbackMapper omsRawMaterialFeedbackMapper;
    @Autowired
    private IOmsProductionOrderService omsProductionOrderService;
    @Autowired
    private IOmsProductionOrderDetailService omsProductionOrderDetailService;
    @Autowired
    private RemoteBomService remoteBomService;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private MailService mailService;

    /**
     * Description:  反馈信息处理-分页查询
     * Param: [omsRawMaterialFeedbackVo]
     * return: java.util.List<com.cloud.order.domain.entity.vo.OmsRawMaterialFeedbackVo>
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    public List<OmsRawMaterialFeedback> listPage(OmsRawMaterialFeedback omsRawMaterialFeedback, SysUser sysUser) {
        Example example = new Example(OmsRawMaterialFeedback.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getProductMaterialCode())) {
            criteria.andEqualTo("productMaterialCode", omsRawMaterialFeedback.getProductMaterialCode());
        }
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getRawMaterialCode())) {
            criteria.andEqualTo("rawMaterialCode", omsRawMaterialFeedback.getRawMaterialCode());
        }
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getProductFactoryCode())) {
            criteria.andEqualTo("productFactoryCode", omsRawMaterialFeedback.getProductFactoryCode());
        }
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getCheckDateStart())) {
            criteria.andGreaterThanOrEqualTo("productStartDate", omsRawMaterialFeedback.getCheckDateStart());
        }
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getCheckDateEnd())) {
            criteria.andLessThanOrEqualTo("productStartDate", omsRawMaterialFeedback.getCheckDateEnd());
        }
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getStatus())) {
            criteria.andEqualTo("status", omsRawMaterialFeedback.getStatus());
        }
        if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
            //排产员根据生产工厂权限查询
            if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_PCY)
                    || CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_ORDER)) {
                criteria.andIn("productFactoryCode", Arrays.asList(DataScopeUtil.getUserFactoryScopes(sysUser.getUserId()).split(",")));
                criteria.andEqualTo("productPerson", sysUser.getLoginName());
            } else if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_JIT)) {
                criteria.andEqualTo("createBy", sysUser.getLoginName());
            }
        }
        criteria.andEqualTo("delFlag", RawMaterialFeedbackConstants.DEL_FLAG_TRUE);
        return omsRawMaterialFeedbackMapper.selectByExample(example);
    }

    /**
     * Description:  反馈信息处理-通过/驳回
     * Param: [omsRawMaterialFeedback]
     * return: int
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    @GlobalTransactional
    public R approval(OmsRawMaterialFeedback omsRawMaterialFeedback, SysUser sysUser) {
        log.info("===========反馈信息处理-通过/驳回 start============");
        String approvalFlag = omsRawMaterialFeedback.getApprovalFlag();
        if (StrUtil.isBlank(approvalFlag)) {
            log.error("============传入通过/驳回标识为空=============");
            return R.error("传入通过/驳回标识为空");
        }
        Example example = new Example(OmsRawMaterialFeedback.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(omsRawMaterialFeedback.getIds())) {
            List<String> ids = Arrays.asList(omsRawMaterialFeedback.getIds().split(","));
            criteria.andIn("id", ids);
        } else {
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getProductMaterialCode())) {
                criteria.andEqualTo("productMaterialCode", omsRawMaterialFeedback.getProductMaterialCode());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getProductFactoryCode())) {
                criteria.andEqualTo("productFactoryCode", omsRawMaterialFeedback.getProductFactoryCode());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getCheckDateStart())) {
                criteria.andLessThanOrEqualTo("productStartDate", omsRawMaterialFeedback.getCheckDateStart());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getCheckDateEnd())) {
                criteria.andGreaterThanOrEqualTo("productStartDate", omsRawMaterialFeedback.getCheckDateEnd());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getStatus())) {
                criteria.andEqualTo("status", omsRawMaterialFeedback.getStatus());
            }
            criteria.andEqualTo("productPerson", sysUser.getLoginName());
        }
        //根据id查询原材料反馈信息
        List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks = omsRawMaterialFeedbackMapper.selectByExample(example);
        if (ObjectUtil.isEmpty(omsRawMaterialFeedbacks) || omsRawMaterialFeedbacks.size() <= 0) {
            log.info("根据条件为查询出JIT反馈信息记录！");
            return R.ok();
        }
        //根据专用号、生产工厂、基本开始日期、bom版本查询未审核的原材料反馈信息
        List<OmsRawMaterialFeedback> rawMaterialFeedbacks = omsRawMaterialFeedbackMapper.selectByList(omsRawMaterialFeedbacks);
        //取前台传的数据与同专用号、生产工厂、开始日期、bom版本查询出的数据差集
        List<OmsRawMaterialFeedback> checkFeedbackList = rawMaterialFeedbacks
                .stream().filter(item -> !omsRawMaterialFeedbacks.contains(item)).collect(Collectors.toList());
        //属性复制
        List<OmsProductionOrder> omsProductionOrders = omsRawMaterialFeedbacks.stream().map(o -> {
            OmsProductionOrder omsProductionOrder = new OmsProductionOrder();
            omsProductionOrder.setProductFactoryCode(o.getProductFactoryCode());
            omsProductionOrder.setProductMaterialCode(o.getProductMaterialCode());
            omsProductionOrder.setProductStartDate(o.getProductStartDate()) ;
            omsProductionOrder.setBomVersion(o.getBomVersion());
            omsProductionOrder.setStatus(ProductOrderConstants.STATUS_ONE);
            return omsProductionOrder;
        }).collect(Collectors.toList());
        //根据专用号、生产工厂、基本开始日期查询排产订单
        List<OmsProductionOrder> productionOrders = omsProductionOrderService.selectByFactoryAndMaterialAndStartDate(omsProductionOrders);
        //排产订单明细list
        List<OmsProductionOrderDetail> omsProductionOrderDetails = new ArrayList<>();
        //判断执行流程，通过or驳回
        if (APPROVAL_FLAG_ADOPT.equals(approvalFlag)) {
            //通过
            //1、更新排产订单的状态为“待调整”
            productionOrders.forEach(o -> {
                if (checkFeedbackList.size() <= 0) {
                    o.setStatus(ProductOrderConstants.STATUS_TWO);
                    o.setUpdateBy(sysUser.getLoginName());
                } else {
                    checkFeedbackList.forEach(f -> {
                        if (!o.getProductMaterialCode().equals(f.getProductMaterialCode())
                                || !o.getProductFactoryCode().equals(f.getProductFactoryCode())
                                || !o.getProductStartDate().equals(f.getProductStartDate())
                                || !o.getBomVersion().equals(f.getBomVersion())) {
                            o.setStatus(ProductOrderConstants.STATUS_TWO);
                            o.setUpdateBy(sysUser.getLoginName());
                        }
                    });
                }
                omsRawMaterialFeedbacks.forEach(f ->{
                    if (o.getProductFactoryCode().equals(f.getProductFactoryCode())
                            && o.getProductMaterialCode().equals(f.getProductMaterialCode())
                            && o.getBomVersion().equals(f.getBomVersion())
                            && o.getProductStartDate().equals(f.getProductStartDate())) {
                        OmsProductionOrderDetail omsProductionOrderDetail = new OmsProductionOrderDetail();
                        omsProductionOrderDetail.setProductOrderCode(o.getOrderCode());
                        omsProductionOrderDetail.setMaterialCode(f.getRawMaterialCode());
                        omsProductionOrderDetail.setStatus(ProductOrderConstants.DETAIL_STATUS_ZERO);
                        omsProductionOrderDetail.setUpdateBy(sysUser.getLoginName());
                        omsProductionOrderDetails.add(omsProductionOrderDetail);
                    }
                });
            });
            //2、更新原材料反馈信息表的状态为“通过”
            omsRawMaterialFeedbacks.forEach(f -> {
                f.setStatus(FEEDBACK_STATUS_ONE);
                f.setUpdateBy(sysUser.getLoginName());
                f.setUpdateTime(new Date());
            });
            //更新排产订单明细状态
            if(omsProductionOrderDetails.size() > 0){
                omsProductionOrderDetailService.updateBatchByProductOrderCode(omsProductionOrderDetails);
            }
            //更新排产订单状态
            if (productionOrders.size() > 0) {
                omsProductionOrderService.updateBatchByPrimaryKeySelective(productionOrders);
            }
        } else if (APPROVAL_FLAG_REJECT.equals(approvalFlag)) {
            //驳回
            //1、更新排产订单的状态为"待评审"
            productionOrders.forEach(o -> {
                if (checkFeedbackList.size() <= 0) {
                    o.setStatus(ProductOrderConstants.STATUS_ZERO);
                    o.setUpdateBy(sysUser.getLoginName());
                } else {
                    checkFeedbackList.forEach(f -> {
                        if (!o.getProductMaterialCode().equals(f.getProductMaterialCode())
                                || !o.getProductFactoryCode().equals(f.getProductFactoryCode())
                                || !o.getProductStartDate().equals(f.getProductStartDate())
                                || !o.getBomVersion().equals(f.getBomVersion())) {
                            o.setStatus(ProductOrderConstants.STATUS_ZERO);
                            o.setUpdateBy(sysUser.getLoginName());
                        }
                    });
                }
                omsRawMaterialFeedbacks.forEach(f ->{
                    if (o.getProductFactoryCode().equals(f.getProductFactoryCode())
                            && o.getProductMaterialCode().equals(f.getProductMaterialCode())
                            && o.getBomVersion().equals(f.getBomVersion())
                            && o.getProductStartDate().equals(f.getProductStartDate())) {
                        OmsProductionOrderDetail omsProductionOrderDetail = new OmsProductionOrderDetail();
                        omsProductionOrderDetail.setProductOrderCode(o.getOrderCode());
                        omsProductionOrderDetail.setMaterialCode(f.getRawMaterialCode());
//                        omsProductionOrderDetail.setStatus(ProductOrderConstants.DETAIL_STATUS_ZERO);
                        omsProductionOrderDetail.setUpdateBy(sysUser.getLoginName());
                        omsProductionOrderDetails.add(omsProductionOrderDetail);
                    }
                });
            });
            //2、更新原材料反馈信息表的状态为"驳回"
            omsRawMaterialFeedbacks.forEach(f -> {
                f.setStatus(FEEDBACK_STATUS_TWO);
                f.setUpdateBy(sysUser.getLoginName());
                f.setUpdateTime(new Date());
            });
            if (omsProductionOrderDetails.size() > 0) {
                R updateOrderMap = omsProductionOrderDetailService.commitProductOrderDetail(omsProductionOrderDetails, new OmsProductionOrderDetail(), sysUser);
                if (!updateOrderMap.isSuccess()) {
                    log.error("反馈信息处理-驳回操作，更新排产订单及明细的状态失败，原因：" + updateOrderMap.get("msg"));
                    throw new BusinessException("反馈信息处理-驳回操作，更新排产订单及明细的状态失败!");
                }
            }
            //邮件通知JIT、JIT处长、毛部长
            List<Map<String,String>> emailList = getEmailList(omsRawMaterialFeedbacks);
            if (CollectionUtil.isNotEmpty(emailList)){
                emailList.forEach(e -> {
                    String email = e.get("email");
                    String subject = e.get("subject");
                    String content = e.get("content");
                    try {
                        mailService.sendHtmlMail(email, subject, content);
                    } catch (Exception ex) {
                        log.error("反馈信息处理-驳回操作，发送邮件异常，原因："+ex.getMessage());
                    }
                });
            }
        }
        //更新排产订单明细状态
//        if(omsProductionOrderDetails.size() > 0){
//            omsProductionOrderDetailService.updateBatchByProductOrderCode(omsProductionOrderDetails);
//        }
        //更新排产订单状态
//        if (productionOrders.size() > 0) {
//            omsProductionOrderService.updateBatchByPrimaryKeySelective(productionOrders);
//        }
        int feedbackUpdateCount = omsRawMaterialFeedbackMapper.updateBatchByPrimaryKeySelective(omsRawMaterialFeedbacks);
        if (feedbackUpdateCount <= 0) {
            log.error("更新原材料反馈信息失败！");
            throw new BusinessException("更新原材料反馈信息失败！");
        }
        log.info("===========反馈信息处理-通过/驳回 end============");
        return R.ok();
    }
    /**
     * Description:  反馈信息处理-驳回,获取邮件内容
     * Param: [omsRawMaterialFeedbacks]
     * return: List<Map<String,String>>
     * Author: ltq
     * Date: 2020/11/23
     */
    public List<Map<String,String>> getEmailList(List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks){
        List<Map<String,String>> emailList = new ArrayList<>();
        if (CollectionUtil.isEmpty(omsRawMaterialFeedbacks)) {
            log.info("原材料反馈信息驳回，邮件处理方法，传入数据为空！");
            return emailList;
        }
        String userJitStr =
                omsRawMaterialFeedbacks.stream().map(OmsRawMaterialFeedback::getCreateBy).collect(Collectors.joining(","));
        //查询JIT信息
        log.info(">>>>>>>原材料反馈信息处理-驳回，处理JIT邮件信息>>>>>>>>>");
        R userMap = remoteUserService.selectUserByLoginName(userJitStr);
        if (!userMap.isSuccess()) {
            log.error("原材料反馈信息驳回操作,根据反馈信息创建人查询用户信息失败："+userMap.get("msg"));
        }
        List<SysUserVo> sysUserList =
                userMap.getCollectData(new TypeReference<List<SysUserVo>>() {});
        if (CollectionUtil.isNotEmpty(sysUserList)) {
            Map<String, List<SysUserVo>> map =sysUserList.stream().collect(Collectors.groupingBy(SysUserVo::getLoginName));
            Map<String, List<OmsRawMaterialFeedback>> feedbackJitMap =
                    omsRawMaterialFeedbacks.stream().collect(Collectors.groupingBy(OmsRawMaterialFeedback::getCreateBy));
            feedbackJitMap.forEach((key, val) -> {
                SysUserVo userVo = map.get(key).get(0);
                Map<String, String> emailMap = new HashMap<>();
                emailMap.put("email", userVo.getEmail());
                emailMap.put("subject", EmailConstants.TITLE_RAW_MATERIAL);
                emailMap.put("content", userVo.getUserName() + getEmailContent(val));
                emailList.add(emailMap);
            });
        }
        //查询JIT处长信息
        log.info(">>>>>>>原材料反馈信息处理-驳回，处理JIT处长邮件信息>>>>>>>>>");
        List<SysUserVo> sysUserVoList = new ArrayList<>();
        //根据工厂、角色查询国产JIT处长信息
        String productFactoryCode = omsRawMaterialFeedbacks.get(0).getProductFactoryCode();
        R sysUserJitGC = remoteUserService.selectUserByMaterialCodeAndRoleKey(productFactoryCode,RoleConstants.ROLE_KEY_JITCZGC);
        if (!sysUserJitGC.isSuccess()) {
            log.error("原材料反馈信息驳回操作，根据工厂、角色查询国产JIT处长失败："+sysUserJitGC.get("msg"));
        }
        List<SysUserVo> sysUserVoListGC = sysUserJitGC.getCollectData(new TypeReference<List<SysUserVo>>() {});
        if (CollectionUtil.isNotEmpty(sysUserVoListGC)) {
            sysUserVoList.addAll(sysUserVoListGC);
        }
        R sysUserJitJK = remoteUserService.selectUserByMaterialCodeAndRoleKey(productFactoryCode,RoleConstants.ROLE_KEY_JITCZJK);
        if (!sysUserJitJK.isSuccess()) {
            log.error("原材料反馈信息驳回操作，根据工厂、角色查询进口JIT处长失败："+sysUserJitJK.get("msg"));
        }
        List<SysUserVo> sysUserVoListJK = sysUserJitJK.getCollectData(new TypeReference<List<SysUserVo>>() {});
        if (CollectionUtil.isNotEmpty(sysUserVoListJK)) {
            sysUserVoList.addAll(sysUserVoListJK);
        }
        if (CollectionUtil.isNotEmpty(sysUserVoList)) {
            Map<String, List<SysUserVo>> userJitMap =
                    sysUserVoList.stream().collect(Collectors.groupingBy(SysUserVo::getLoginName));
            userJitMap.forEach((key, val) -> {
                SysUserVo sysUserVo = val.get(0);
                List<String> purchaseScopesList = Arrays.asList(DataScopeUtil.getUserPurchaseScopes(sysUserVo.getUserId()).split(","));
                List<OmsRawMaterialFeedback> feedbackJITCZList = omsRawMaterialFeedbacks.stream()
                        .filter(feedback -> purchaseScopesList.contains(feedback.getPurchaseGroup())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(feedbackJITCZList)) {
                    Map<String, String> emailMap = new HashMap<>();
                    emailMap.put("email", sysUserVo.getEmail());
                    emailMap.put("subject", EmailConstants.TITLE_RAW_MATERIAL);
                    emailMap.put("content", sysUserVo.getUserName() + getEmailContent(feedbackJITCZList));
                    emailList.add(emailMap);
                }
            });
        }
        //查询毛部长信息
        log.info(">>>>>>>原材料反馈信息处理-驳回，处理供应链部长信息>>>>>>>>>");
        R sysUserGYLBZMap = remoteUserService.selectUserByRoleKey(RoleConstants.ROLE_KEY_GYLBZ);
        if (!sysUserGYLBZMap.isSuccess()) {
            log.error("原材料反馈信息驳回操作,查询供应链部长用户信息失败："+sysUserGYLBZMap.get("msg"));
        }
        List<SysUserRights> userGylbzList = sysUserGYLBZMap.getCollectData(new TypeReference<List<SysUserRights>>() {});
        if (CollectionUtil.isNotEmpty(userGylbzList)) {
            userGylbzList.forEach(u -> {
                Map<String,String> emailMap = new HashMap<>();
                emailMap.put("email",u.getEmail());
                emailMap.put("subject",EmailConstants.TITLE_RAW_MATERIAL);
                emailMap.put("content",u.getUserName() + getEmailContent(omsRawMaterialFeedbacks));
                emailList.add(emailMap);
            });
        }
        return emailList;
    }
    /**
     * Description:  反馈信息处理-驳回,组织邮件内容
     * Param: [list]
     * return: String
     * Author: ltq
     * Date: 2020/11/23
     */
    public String getEmailContent(List<OmsRawMaterialFeedback> list){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(EmailConstants.RAW_MATERIAL_CONTEXT_FRONT);
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(omsRawMaterialFeedback -> {
                stringBuffer.append("<tr>");
                stringBuffer.append("<td>");
                stringBuffer.append(omsRawMaterialFeedback.getProductFactoryCode());
                stringBuffer.append("</td>");
                stringBuffer.append("<td>");
                stringBuffer.append(omsRawMaterialFeedback.getProductMaterialCode());
                stringBuffer.append("</td>");
                stringBuffer.append("<td>");
                stringBuffer.append(omsRawMaterialFeedback.getRawMaterialCode());
                stringBuffer.append("</td>");
                stringBuffer.append("<td>");
                stringBuffer.append(omsRawMaterialFeedback.getProductPerson());
                stringBuffer.append("</td>");
                stringBuffer.append("<td>");
                stringBuffer.append(omsRawMaterialFeedback.getCreateBy());
                stringBuffer.append("</td>");
                stringBuffer.append("<td>");
                stringBuffer.append(omsRawMaterialFeedback.getRemark());
                stringBuffer.append("</td>");
                stringBuffer.append("</tr>");
            });
        }
        stringBuffer.append(EmailConstants.RAW_MATERIAL_CONTEXT_AFTER);
        return stringBuffer.toString();
    }
    /**
     * Description:  快捷修改排产订单量
     * Param: [list, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    @GlobalTransactional
    public R updateProductOrder(List<OmsProductionOrder> list,Long id, SysUser sysUser) {
        log.info("============快捷修改排产订单量方法  start============");
        if (ObjectUtil.isEmpty(list) || list.size() <= 0) {
            log.error("快捷修改排产订单量，传入参数为空！");
            return R.error("快捷修改排产订单量，传入参数为空！");
        }
        for (OmsProductionOrder omsProductionOrder : list) {
            log.info("============快捷修改排产订单量方法,调用排产订单service============");
            R updateOrderMap = omsProductionOrderService.updateSave(omsProductionOrder, sysUser);
            if (!updateOrderMap.isSuccess()) {
                log.error("快捷修改排产订单量失败,原因：" + updateOrderMap.get("msg"));
                return R.error("快捷修改排产订单量失败，原因："+updateOrderMap.get("msg"));
            }
        }
        BigDecimal updateSum = list.stream().map(OmsProductionOrder::getProductNum).reduce(BigDecimal.ZERO,BigDecimal::add);
        //更新反馈信息记录
        if (StrUtil.isNotBlank(StrUtil.toString(id))) {
            OmsRawMaterialFeedback omsRawMaterialFeedback = omsRawMaterialFeedbackMapper.selectByPrimaryKey(id);
            if (BeanUtil.isNotEmpty(omsRawMaterialFeedback)
                    && updateSum.compareTo(omsRawMaterialFeedback.getProductContentNum()) <= 0) {
                omsRawMaterialFeedback.setUpdateBy(sysUser.getLoginName());
                omsRawMaterialFeedback.setStatus(FEEDBACK_STATUS_ONE);
                omsRawMaterialFeedback.setUpdateTime(new Date());
                omsRawMaterialFeedbackMapper.updateByPrimaryKeySelective(omsRawMaterialFeedback);
            }
        }
        log.info("============快捷修改排产订单量方法  end============");
        return R.ok();
    }

    /**
     * Description:  删除原材料反馈信息记录
     * Param: [ids, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/28
     */
    @Override
    @GlobalTransactional
    public R deleteByIds(String ids, OmsRawMaterialFeedback omsRawMaterialFeedback ,SysUser sysUser) {
        Example example = new Example(OmsRawMaterialFeedback.class);
        Example.Criteria criteria = example.createCriteria();
        if (StrUtil.isNotBlank(ids)) {
            criteria.andIn("id", Arrays.asList(ids.split(",")));
        } else if (!BeanUtils.checkObjAllFieldsIsNull(omsRawMaterialFeedback)) {
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getStatus())
                    && !FEEDBACK_STATUS_ZERO.equals(omsRawMaterialFeedback.getStatus())) {
                return R.error("只允许删除未审核的反馈信息记录！");
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getRawMaterialCode())) {
                criteria.andEqualTo("rawMaterialCode",omsRawMaterialFeedback.getRawMaterialCode());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getProductFactoryCode())) {
                criteria.andEqualTo("productFactoryCode", omsRawMaterialFeedback.getProductFactoryCode());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getCheckDateStart())) {
                criteria.andGreaterThanOrEqualTo("productStartDate", omsRawMaterialFeedback.getCheckDateStart());
            }
            if (StrUtil.isNotBlank(omsRawMaterialFeedback.getCheckDateEnd())) {
                criteria.andLessThanOrEqualTo("productStartDate", omsRawMaterialFeedback.getCheckDateEnd());
            }
            if (UserConstants.USER_TYPE_HR.equals(sysUser.getUserType())) {
                if (CollectionUtil.contains(sysUser.getRoleKeys(), RoleConstants.ROLE_KEY_JIT)) {
                    criteria.andEqualTo("createBy", sysUser.getLoginName());
                }
            }
            criteria.andEqualTo("status",FEEDBACK_STATUS_ZERO);
        } else {
            log.error("JIT原材料反馈信息删除操作,传入参数为空！");
            return R.error("传入参数为空！");
        }
        List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks = omsRawMaterialFeedbackMapper.selectByExample(example);
        /** 删除原材料反馈信息需要同时更新排产订单表记录的状态，然而删除的反馈信息记录不一定是一条排产订单对应的全部反馈信息，因此需要进行筛选*/
        /** 筛选开始 */
        if (ObjectUtil.isEmpty(omsRawMaterialFeedbacks) || omsRawMaterialFeedbacks.size() <= 0) {
            log.error("JIT原材料反馈信息删除操作，根据前台参数未查询出数据！");
            return R.ok();
        }
        ids = omsRawMaterialFeedbacks.stream().map(o -> o.getId().toString()).collect(Collectors.joining(","));
        //根据专用号、生产工厂、基本开始日期、bom版本查询原材料反馈信息
        List<OmsRawMaterialFeedback> rawMaterialFeedbacks = omsRawMaterialFeedbackMapper.selectByList(omsRawMaterialFeedbacks);
        //取前台传的数据与同专用号、生产工厂、开始日期、bom版本查询出的数据差集
        List<OmsRawMaterialFeedback> checkFeedbackList = rawMaterialFeedbacks
                .stream().filter(item -> !omsRawMaterialFeedbacks.contains(item)).collect(Collectors.toList());
        //属性复制
        List<OmsProductionOrder> omsProductionOrders = omsRawMaterialFeedbacks.stream().map(o -> {
            OmsProductionOrder omsProductionOrder = new OmsProductionOrder();
            omsProductionOrder.setProductFactoryCode(o.getProductFactoryCode());
            omsProductionOrder.setProductMaterialCode(o.getProductMaterialCode());
            omsProductionOrder.setProductStartDate(o.getProductStartDate());
            omsProductionOrder.setBomVersion(o.getBomVersion());
            omsProductionOrder.setStatus(ProductOrderConstants.STATUS_ONE);
            return omsProductionOrder;
        }).collect(Collectors.toList());
        //根据专用号、生产工厂、基本开始日期、bom版本查询排产订单
        List<OmsProductionOrder> productionOrders = omsProductionOrderService.selectByFactoryAndMaterialAndStartDate(omsProductionOrders);
        productionOrders.forEach(o -> {
            if (checkFeedbackList.size() <= 0) {
                o.setStatus(ProductOrderConstants.STATUS_ZERO);
                o.setUpdateBy(sysUser.getLoginName());
            } else {
                checkFeedbackList.forEach(f -> {
                    if (!o.getProductMaterialCode().equals(f.getProductMaterialCode())
                            || !o.getProductFactoryCode().equals(f.getProductFactoryCode())
                            || !o.getProductStartDate().equals(f.getProductStartDate())
                            || !o.getBomVersion().equals(f.getBomVersion())) {
                        o.setStatus(ProductOrderConstants.STATUS_ZERO);
                        o.setUpdateBy(sysUser.getLoginName());
                    }
                });
            }
        });
        List<OmsProductionOrderDetail> omsProductionOrderDetails = new ArrayList<>();
        productionOrders.forEach(o ->
                omsRawMaterialFeedbacks.forEach(f -> {
                    if (o.getProductFactoryCode().equals(f.getProductFactoryCode())
                            && o.getProductMaterialCode().equals(f.getProductMaterialCode())
                            && o.getBomVersion().equals(f.getBomVersion())
                            && o.getProductStartDate().equals(f.getProductStartDate())) {
                        OmsProductionOrderDetail omsProductionOrderDetail = new OmsProductionOrderDetail();
                        omsProductionOrderDetail.setProductOrderCode(o.getOrderCode());
                        omsProductionOrderDetail.setMaterialCode(f.getRawMaterialCode());
                        omsProductionOrderDetail.setStatus(ProductOrderConstants.DETAIL_STATUS_ZERO);
                        omsProductionOrderDetail.setUpdateBy(sysUser.getLoginName());
                        omsProductionOrderDetails.add(omsProductionOrderDetail);
                    }
                })
        );
        /** 筛选结束 */
        //更新排产订单明细状态
        if (ObjectUtil.isNotEmpty(omsProductionOrderDetails) && omsProductionOrderDetails.size() > 0) {
            omsProductionOrderDetailService.updateBatchByProductOrderCode(omsProductionOrderDetails);
        }
        //更新排产订单状态
        if (ObjectUtil.isNotEmpty(productionOrders) && productionOrders.size() > 0) {
           omsProductionOrderService.updateBatchByPrimaryKeySelective(productionOrders);
        }
        List<String> idList = Arrays.asList(ids.split(","));
        OmsRawMaterialFeedback feedback = OmsRawMaterialFeedback.builder().delFlag("1").build();
        feedback.setUpdateTime(new Date());
        feedback.setUpdateBy(sysUser.getLoginName());
        idList.forEach(id ->omsRawMaterialFeedbackMapper.updateById(feedback,Long.valueOf(id)));
        return R.ok();
    }

    /**
     * Description: 反馈信息新增
     * Param: [omsRawMaterialFeedbacks, sysUser]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/29
     */
    @Override
    @GlobalTransactional
    public R insertFeedback(List<OmsRawMaterialFeedback> omsRawMaterialFeedbacks, SysUser sysUser) {
        if (BeanUtil.isEmpty(omsRawMaterialFeedbacks) || omsRawMaterialFeedbacks.size() <= 0) {
            log.error("新增原材料反馈记录传入参数为空！");
            return R.error("新增原材料反馈记录传入参数为空!");
        }
        Example example = new Example(OmsRawMaterialFeedback.class);
        Example.Criteria criteria = example.createCriteria();
        omsRawMaterialFeedbacks.forEach(f -> {
            if (!StrUtil.isNotBlank(f.getRemark())) {
                log.error("备注不能为空！");
                throw new BusinessException("备注不能为空！");
            }
            //增加重复数据校验  2020-08-04  ltq
            criteria.andEqualTo("productFactoryCode", f.getProductFactoryCode());
            criteria.andEqualTo("rawMaterialCode", f.getRawMaterialCode());
            criteria.andEqualTo("productStartDate", f.getProductStartDate());
            criteria.andEqualTo("productMaterialCode", f.getProductMaterialCode());
            criteria.andEqualTo("bomVersion", f.getBomVersion());
            criteria.andEqualTo("status",FEEDBACK_STATUS_ZERO);
            criteria.andEqualTo("delFlag",DeleteFlagConstants.NO_DELETED);
            OmsRawMaterialFeedback omsRawMaterialFeedback = omsRawMaterialFeedbackMapper.selectOneByExample(example);
            if (BeanUtil.isNotEmpty(omsRawMaterialFeedback)) {
                log.error("数据库中存在了相同的反馈记录！");
                throw new BusinessException("数据库中存在了相同的反馈记录，请先删除原反馈记录！");
            }
            //根据成品专用、生产工厂、原材料物料号、BOM版本查询bom清单数据
            R rBom = remoteBomService.listByProductAndMaterial(f.getProductMaterialCode(), f.getRawMaterialCode(),
                    f.getBomVersion(), f.getProductFactoryCode());
            if (!rBom.isSuccess()) {
                log.error("根据成品专用号、生产工厂、原材料物料号、BOM版本查询bom清单数据失败，原因：" + rBom.get("msg"));
                throw new BusinessException("BOM信息为空！");
            }
            CdBomInfo cdBom = rBom.getData(CdBomInfo.class);
            //计算成品满足量，原材料满足量 * 单耗 / 基本数量
            BigDecimal productContentNum = f.getRawMaterialContentNum().multiply(cdBom.getBomNum())
                    .divide(cdBom.getBasicNum(), 0, BigDecimal.ROUND_HALF_DOWN);
            f.setProductContentNum(productContentNum);
            f.setCreateBy(sysUser.getLoginName());
            f.setCreateTime(new Date());
            f.setStatus(FEEDBACK_STATUS_ZERO);
            f.setDelFlag("0");
        });
        List<OmsProductionOrderDetail> omsProductionOrderDetails = omsRawMaterialFeedbacks
                .stream().map(f -> OmsProductionOrderDetail.builder()
                        .productFactoryCode(f.getProductFactoryCode())
                        .productStartDate(f.getProductStartDate())
                        .materialCode(f.getRawMaterialCode())
                        .bomVersion(f.getBomVersion())
                        .build())
                .collect(Collectors.toList());
        //查询排产订单明细,条件：生产工厂、基本开始日期、原材料物料、版本
        List<OmsProductionOrderDetail> details =
                omsProductionOrderDetailService.selectListByList(omsRawMaterialFeedbacks);
        if (ObjectUtil.isEmpty(details) || details.size() <= 0) {
            log.error("根据生产工厂、基本开始日期、原材料物料、版本未查询出排产订单明细！");
            return R.error("没有查询出排产订单明细！");
        }
        List<String> orderCodeList = details.stream()
                .map(OmsProductionOrderDetail::getProductOrderCode).distinct().collect(Collectors.toList());
        //查询排产订单记录
        List<OmsProductionOrder> omsProductionOrders =
                omsProductionOrderService.selectByOrderCode(orderCodeList);
        if (ObjectUtil.isEmpty(omsProductionOrders) || omsProductionOrders.size() <= 0) {
            log.error("根据排产订单号没有查询出排产订单！");
            return R.error("没有查询出排产订单！");
        }
        //更新排产订单明细状态
        details.forEach(d -> d.setStatus(ProductOrderConstants.DETAIL_STATUS_TWO));
        int updateOrderDetailCount = omsProductionOrderDetailService.updateBatchByPrimaryKeySelective(details);
        if (updateOrderDetailCount <= 0) {
            log.error("更新排产订单明细记录状态失败！");
            return R.error("更新排产订单明细记录状态失败!");
        }
        //更新排产订单状态
        omsProductionOrders.forEach(o -> o.setStatus(ProductOrderConstants.STATUS_ONE));
        int updateOrderCount = omsProductionOrderService.updateBatchByPrimaryKeySelective(omsProductionOrders);
        if (updateOrderCount <= 0) {
            log.error("更新排产订单记录状态失败！");
            return R.error("更新排产订单记录状态失败!");
        }
        int insertCount = omsRawMaterialFeedbackMapper.insertList(omsRawMaterialFeedbacks);
        if (insertCount <= 0) {
            log.error("原材料反馈信息新增失败！");
            return R.error("原材料反馈信息新增失败!");
        }
        //邮件通知对应排产员
        String loginNames = omsRawMaterialFeedbacks
                .stream().map(OmsRawMaterialFeedback::getProductPerson).distinct().collect(Collectors.joining(","));
        //根据排产员工号查询用户信息
        R userMap = remoteUserService.selectUserByLoginName(loginNames);
        if (!userMap.isSuccess()){
            log.error("JIT原材料反馈根据排产员工号查询用户信息失败，原因："+userMap.get("msg"));
            throw new BusinessException("JIT原材料反馈根据排产员工号查询用户信息失败，原因："+userMap.get("msg"));
        }
        List<SysUserVo> sysUserList = userMap.getCollectData(new TypeReference<List<SysUserVo>>() {});
        sysUserList.forEach(user ->{
            log.info("JIT原材料反馈邮件通知开始~");
            String email = user.getEmail();
            String context = user.getUserName() + EmailConstants.RAW_FEEDBACK_CONTEXT + EmailConstants.ORW_URL;
            mailService.sendTextMail(email, EmailConstants.TITLE_RAW_FEEDBACK, context);
        });
        return R.ok();
    }
    /**
     * Description:根据生产工厂、原材料物料、开始日期更新反馈信息状态为“未审核已确认”
     * Param: [list]
     * return: void
     * Author: ltq
     * Date: 2020/7/1
     */
    @Override
    public void updateBatchByList(List<OmsRawMaterialFeedback> list) {
        omsRawMaterialFeedbackMapper.updateBatchByList(list);
    }
}
