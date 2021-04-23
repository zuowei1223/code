package com.tcoiss.webservice.api.factory;

import com.tcoiss.common.core.domain.R;
import com.tcoiss.webservice.api.RemoteApiService;
import com.tcoiss.webservice.api.model.ApiParam;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 文件服务降级处理
 * 
 * @author tcoiss
 */
@Component
public class RemoteApiFallbackFactory implements FallbackFactory<RemoteApiService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteApiFallbackFactory.class);

    @Override
    public RemoteApiService create(Throwable throwable)
    {
        log.error("Api服务调用失败:{}", throwable.getMessage());
        return new RemoteApiService()
        {
            @Override
            public R<Map<String,Object>> executeApi(ApiParam apiParam){
                return null;
            }

            @Override
            public R<Map<String, Object>> executeKdApi(ApiParam apiParam) {
                return null;
            }
        };
    }
}
