package com.tcoiss.gateway.config;

import com.netflix.loadbalancer.IRule;
import com.tcoiss.gateway.handler.MyNacosRule;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClients(defaultConfiguration = NacosRuleConfig.class)
public class NacosRuleConfig {
    @Bean
    public IRule commonRule() {
        return new MyNacosRule();
    }
}
