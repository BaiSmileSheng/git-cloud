package com.cloud.system.service;

import com.cloud.common.core.domain.R;
import com.cloud.system.domain.entity.CdSettleProductMaterial;
import com.cloud.common.core.service.BaseService;
import com.cloud.system.domain.entity.SysUser;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 物料号和加工费号对应关系 Service接口
 *
 * @author cs
 * @date 2020-06-05
 */
public interface ICdSettleProductMaterialService extends BaseService<CdSettleProductMaterial> {

    /**
     * 导入
     * @return 成功或失败
     */
    R importMul(SysUser sysUser,MultipartFile file) throws Exception;

    /**
     * 新增
     * @param cdSettleProductMaterial
     * @return
     */
    R insertProductMaterial(CdSettleProductMaterial cdSettleProductMaterial);

    /**
     * 修改
     * @param cdSettleProductMaterial
     * @return
     */
    R updateProductMaterial(CdSettleProductMaterial cdSettleProductMaterial);

}
