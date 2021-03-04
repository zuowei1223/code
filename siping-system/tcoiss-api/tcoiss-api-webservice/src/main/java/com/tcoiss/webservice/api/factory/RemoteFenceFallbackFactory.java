package com.tcoiss.webservice.api.factory;

import com.tcoiss.webservice.api.RemoteFenceService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 文件服务降级处理
 * 
 * @author tcoiss
 */
@Component
public class RemoteFenceFallbackFactory implements FallbackFactory<RemoteFenceService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteFenceFallbackFactory.class);

    @Override
    public RemoteFenceService create(Throwable throwable)
    {
        log.error("围栏保存调用失败:{}", throwable.getMessage());
        return new RemoteFenceService()
        {
            /*@Override
            public R<Boolean> saveFence(ElectronicFence electronicFence)
            {
                return R.fail("保存围栏失败:" + throwable.getMessage());
            }*/
        };
    }
}
