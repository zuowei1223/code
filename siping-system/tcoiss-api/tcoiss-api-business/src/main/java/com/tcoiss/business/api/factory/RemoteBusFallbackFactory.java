package com.tcoiss.business.api.factory;

import com.tcoiss.business.api.RemoteBusService;
import com.tcoiss.common.core.domain.R;
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
public class RemoteBusFallbackFactory implements FallbackFactory<RemoteBusService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteBusFallbackFactory.class);

    @Override
    public RemoteBusService create(Throwable throwable)
    {
        log.error("业务服务调用失败:{}", throwable.getMessage());
        return new RemoteBusService()
        {

        };
    }
}
