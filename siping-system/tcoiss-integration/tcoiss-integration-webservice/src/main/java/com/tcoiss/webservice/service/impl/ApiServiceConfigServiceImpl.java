package com.tcoiss.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.mapper.ApiServiceConfigMapper;
import com.tcoiss.webservice.service.IApiServiceConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * API服务配置Service业务层处理
 *
 * @author zw
 * @date 2021-01-19
 */
@Service
public class ApiServiceConfigServiceImpl extends ServiceImpl<ApiServiceConfigMapper, ApiServiceConfig> implements IApiServiceConfigService {

    @Override
    public List<ApiServiceConfig> queryList(ApiServiceConfig apiServiceConfig) {
        LambdaQueryWrapper<ApiServiceConfig> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(apiServiceConfig.getApiName())){
            lqw.like(ApiServiceConfig::getApiName ,apiServiceConfig.getApiName());
        }
        if (StringUtils.isNotBlank(apiServiceConfig.getRequestType())){
            lqw.eq(ApiServiceConfig::getRequestType ,apiServiceConfig.getRequestType());
        }
        if (StringUtils.isNotBlank(apiServiceConfig.getAppName())){
            lqw.like(ApiServiceConfig::getAppName ,apiServiceConfig.getAppName());
        }
        if (StringUtils.isNotBlank(apiServiceConfig.getCreateName())){
            lqw.like(ApiServiceConfig::getCreateName ,apiServiceConfig.getCreateName());
        }
        if (apiServiceConfig.getDataLevel() != null){
            lqw.eq(ApiServiceConfig::getDataLevel ,apiServiceConfig.getDataLevel());
        }
        return this.list(lqw);
    }

    @Override
    public ApiServiceConfig getOperationByCode(String apiConfig) {

        return null;
    }


}
