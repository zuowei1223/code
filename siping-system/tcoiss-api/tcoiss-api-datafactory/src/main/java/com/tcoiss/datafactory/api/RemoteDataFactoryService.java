package com.tcoiss.datafactory.api;

import com.tcoiss.common.core.constant.ServiceNameConstants;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.datafactory.api.factory.RemoteDataFactoryFallbackFactory;
import com.tcoiss.datafactory.api.model.SchemeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 保存围栏服务
 * 
 * @author tcoiss
 */
@FeignClient(contextId = "remoteDataFactoryService", value = ServiceNameConstants.WEBAPI_SERVICE, fallbackFactory = RemoteDataFactoryFallbackFactory.class)
public interface RemoteDataFactoryService
{
    /**
     * 执行方案
     * @param
     * @return 结果
     */
    @PostMapping("/scheme/executeScheme")
    public R<Map<String,Object>> executeScheme(@RequestBody SchemeVO vo);

}
