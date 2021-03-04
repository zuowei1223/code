package com.tcoiss.webservice.service;

import com.tcoiss.webservice.domain.TrackService;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 轨迹服务配置Service接口
 *
 * @author zw
 * @date 2021-02-21
 */
public interface ITrackServiceService extends IService<TrackService> {

    /**
     * 查询列表
     */
    List<TrackService> queryList(TrackService trackService);

    boolean saveTrackService(TrackService trackService,String apiCode);

    boolean addFenceNum(TrackService trackService);

    boolean removeByIds(List<Long> ids);
}
