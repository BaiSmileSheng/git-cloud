package com.cloud.system.service;

import cn.hutool.core.lang.Dict;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.CdMaterialExtendInfo;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 物料扩展信息 Service接口
 *
 * @author lihongia
 * @date 2020-06-15
 */
public interface ICdMaterialExtendInfoService extends BaseService<CdMaterialExtendInfo> {

    /**
     * 定时任务传输成品物料接口
     *
     * @return
     */
    R timeSycMaterialCode();


    /**
     * 根据生命周期查询物料号集合
     * @param lifeCycle
     * @return
     */
	R selectMaterialCodeByLifeCycle(String lifeCycle);

    /**
     * 根据物料号集合查询
     * @param materialCodes
     * @return
     */
    R selectInfoInMaterialCodes(List<String> materialCodes);
    /**
     * Description:  根据多个成品专用号查询
     * Param: [list]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/18
     */
    R selectByMaterialCodeList(List<Dict> list);
    /**
     * Description:  根据物料查询一条数据
     * Param: [materialCode]
     * return: com.cloud.common.core.domain.R
     * Author: ltq
     * Date: 2020/6/23
     */
    R selectOneByMaterialCode(String materialCode);

    /**
     * 导入
     */
    R importMaterialExtendInfo(MultipartFile file, SysUser sysUser)throws IOException;

}
