package com.tcoiss.webservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FencePoints;

import java.util.List;

/**
 * 轨迹服务配置Service接口
 *
 * @author zw
 * @date 2021-02-21
 */
public interface IFencePointsService extends IService<FencePoints> {

    /**
     * 查询列表
     */
    List<FencePoints> queryListByFenceId(String fenceId);


    boolean deleteByFenceId(String fenceId);

    boolean updateBatch(ElectronicFence electronicFence, List<FencePoints> coordinate);
}
