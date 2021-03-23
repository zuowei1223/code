package com.tcoiss.webservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.domain.AddressVo;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FencePoints;

import java.util.List;
import java.util.Map;

/**
 * 电子围栏Service接口
 *
 * @author zw
 * @date 2021-01-31
 */
public interface IElectronicFenceService extends IService<ElectronicFence> {

    /**
     * 查询列表
     */
    List<ElectronicFence> queryList(ElectronicFence electronicFence);


    ElectronicFence getByFenceCode(String fenceCode);

    boolean checkFenceName(ElectronicFence electronicFence);

    boolean syncFence(ElectronicFence electronicFence,int enable);

}
