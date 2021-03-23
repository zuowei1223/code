package com.tcoiss.webservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.domain.AddressVo;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FencePoints;

import java.util.List;
import java.util.Map;

/**
 * 轨迹服务配置Service接口
 *
 * @author zw
 * @date 2021-02-21
 */
public interface IFencePointsService extends IService<FencePoints> {
    List<ElectronicFence> queryList(ElectronicFence electronicFence);

    boolean removeByCode(String fenceCode,String apiCode);

    int saveDistrictFence(ElectronicFence fence, String apiCode);

    AjaxResult getFenceByLocation(AddressVo addressVo);

    List<Map<String,Object>> getDistrictInfo(ElectronicFence fence, String apiCode);

    boolean checkFenceName(ElectronicFence electronicFence);

    ElectronicFence districtFence(ElectronicFence electronicFence, String apiCode);

    int saveOrUpdateFence(ElectronicFence fence);

    int updateByCode(ElectronicFence fence);

    /*List<ElectronicFence> queryFenceByName(ElectronicFence electronicFence);*/
}

