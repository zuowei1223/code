package com.tcoiss.webservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.domain.AddressVo;
import com.tcoiss.webservice.domain.FencePoints;
import com.tcoiss.webservice.domain.PointsVo;

import java.util.List;
import java.util.Map;

/**
 * 轨迹服务配置Service接口
 *
 * @author zw
 * @date 2021-02-21
 */
public interface IFencePointsService extends IService<FencePoints> {
    Map<String,List<PointsVo>> queryList(FencePoints fencePoints);

    boolean removeByGid(Long gid,String apiCode);

    int saveFencePoints(PointsVo pointsVo, String saveFence);

    boolean updateByVo(PointsVo pointsVo, String apiCode);

    List<PointsVo> getDistrictOpints(PointsVo pointsVo, String apiCode, Map<String, Object> querstMap);

    int saveDistrictFence(PointsVo pointsVo, String apiCode);

    AjaxResult getFenceByLocation(AddressVo addressVo);
}

