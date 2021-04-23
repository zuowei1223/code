package com.tcoiss.webservice.api;

import com.tcoiss.common.core.constant.ServiceNameConstants;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.webservice.api.factory.RemoteApiFallbackFactory;
import com.tcoiss.webservice.api.model.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 保存围栏服务
 * 
 * @author tcoiss
 */
@FeignClient(contextId = "remoteApiService", value = ServiceNameConstants.WEBAPI_SERVICE, fallbackFactory = RemoteApiFallbackFactory.class)
public interface RemoteApiService
{
    /**
     *执行Api调用
     * @param
     * @return 结果
     */
    @PostMapping("/apiService/executeApi")
    public R<Map<String,Object>> executeApi(@RequestBody ApiParam apiParam);

    /**
     *执行Api调用
     * @param
     * @return 结果
     */
    @PostMapping("/apiService/executeKdApi")
    public R<Map<String,Object>> executeKdApi(@RequestBody ApiParam apiParam);
}
