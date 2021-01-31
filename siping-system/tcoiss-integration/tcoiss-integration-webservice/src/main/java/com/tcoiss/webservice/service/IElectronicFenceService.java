package com.tcoiss.webservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.webservice.domain.ElectronicFence;

import java.util.List;

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
}
