package com.tcoiss.business.api;

import com.tcoiss.business.api.factory.RemoteBusFallbackFactory;
import com.tcoiss.common.core.constant.ServiceNameConstants;
import com.tcoiss.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 保存围栏服务
 * 
 * @author tcoiss
 */
@FeignClient(contextId = "remoteBusService", value = ServiceNameConstants.WEBAPI_SERVICE, fallbackFactory = RemoteBusFallbackFactory.class)
public interface RemoteBusService
{

}
