package com.tcoiss.business.api;

import com.tcoiss.datafactory.api.factory.RemoteBusFallbackFactory;
import com.tcoiss.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 保存围栏服务
 * 
 * @author tcoiss
 */
@FeignClient(contextId = "remoteBusService", value = ServiceNameConstants.WEBAPI_SERVICE, fallbackFactory = RemoteBusFallbackFactory.class)
public interface RemoteBusService
{

}
