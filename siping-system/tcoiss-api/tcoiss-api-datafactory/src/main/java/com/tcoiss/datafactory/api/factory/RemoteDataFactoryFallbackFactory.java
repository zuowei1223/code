package com.tcoiss.datafactory.api.factory;

import com.tcoiss.common.core.domain.R;
import com.tcoiss.datafactory.api.RemoteDataFactoryService;
import com.tcoiss.datafactory.api.model.SchemeVO;
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
public class RemoteDataFactoryFallbackFactory implements FallbackFactory<RemoteDataFactoryService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteDataFactoryFallbackFactory.class);

    @Override
    public RemoteDataFactoryService create(Throwable throwable)
    {
        log.error("业务服务调用失败:{}", throwable.getMessage());
        return new RemoteDataFactoryService()
        {

            @Override
            public R<Map<String, Object>> executeScheme(SchemeVO vo) {

                return null;
            }
        };
    }
}
