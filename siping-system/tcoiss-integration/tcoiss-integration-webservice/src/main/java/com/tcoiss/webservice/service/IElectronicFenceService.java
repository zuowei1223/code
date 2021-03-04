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

    ElectronicFence getFenceById(Long id);

    /**
     * 新增围栏
     */
    int saveFence(ElectronicFence fence, List<FencePoints> coordinate, String apiCode);

    boolean removeByIds(List<Long> ids,String apiCode);

    boolean updateById(ElectronicFence electronicFence, List<FencePoints> coordinate, String apiCode);

    List<FencePoints> getDistrictOpints(String serviceId,String apiCode, Map<String, Object> querstMap);

    int saveDistrictFence(ElectronicFence fence, List<FencePoints> coordinate, String saveDistrictFence);

    AjaxResult getFenceByLocation(AddressVo addressVo);
}
