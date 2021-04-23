package com.tcoiss.common.mq.configure;

import com.tcoiss.common.mq.bean.SubscribeBean;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "subscribeBeans")
@Data
public class SubscribeBeanConfig {

    private List<SubscribeBean> list;

}
