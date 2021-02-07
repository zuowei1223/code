package com.tcoiss.webservice.api;

import com.tcoiss.common.core.constant.ServiceNameConstants;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.api.domain.ElectronicFence;
import com.tcoiss.webservice.api.factory.RemoteFenceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/fence")
    public R saveFence(@RequestBody ElectronicFence electronicFence);
}
