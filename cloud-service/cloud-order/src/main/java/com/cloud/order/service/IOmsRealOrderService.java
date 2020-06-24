package com.cloud.order.service;

import com.cloud.common.core.domain.R;
import com.cloud.order.domain.entity.OmsRealOrder;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 真单Service接口
 *
 * @author ltq
 * @date 2020-06-15
 */
public interface IOmsRealOrderService extends BaseService<OmsRealOrder> {

    /**
     * 修改保存真单
     * @param omsRealOrder 真单对象
     * @return
     */
    R editSaveOmsRealOrder(OmsRealOrder omsRealOrder, SysUser sysUser,long userId);

    /**
     * 导入真单
     * @param file 文件
     * @param orderFrom 来源
     * @return
     * @throws IOException
     */
    R importRealOrderFile(MultipartFile file, String orderFrom,SysUser sysUser,long loginId) throws IOException;
    /**
     * 导入真单
     * @param successResult  需要导入的数据
     * @param auditResult  需要审核的数据
     * @param sysUser  用户信息
     * @param orderFrom  内单或外单
     * @return
     */
    R importOmsRealOrder(List<OmsRealOrder> successResult,List<OmsRealOrder> auditResult,SysUser sysUser,String orderFrom,long loginId);

    /**
     * 定时任务每天在获取到PO信息后 进行需求汇总
     * @return
     */
    R timeCollectToOmsRealOrder();
}
