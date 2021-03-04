package com.tcoiss.webservice.api;

import com.tcoiss.common.core.constant.ServiceNameConstants;
import com.tcoiss.webservice.api.factory.RemoteFenceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 保存围栏服务
 * 
 * @author tcoiss
 */
@FeignClient(contextId = "remoteFenceService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFenceFallbackFactory.class)
public interface RemoteFenceService
{
    /**
     * 保存围栏
     *
     * @param electronicFence 围栏信息
     * @return 结果
     */
    /*@PostMapping("/fence")
    public R saveFence(@RequestBody ElectronicFence electronicFence);*/
}
