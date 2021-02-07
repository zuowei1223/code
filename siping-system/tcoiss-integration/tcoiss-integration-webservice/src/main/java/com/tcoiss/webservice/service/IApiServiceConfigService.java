package com.tcoiss.webservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.webservice.domain.ApiServiceConfig;

import java.util.List;

/**
 * API服务配置Service接口
 *
 * @author zw
 * @date 2021-01-19
 */
public interface IApiServiceConfigService extends IService<ApiServiceConfig> {

    /**
     * 查询列表
     */
    List<ApiServiceConfig> queryList(ApiServiceConfig apiServiceConfig);

    /**
     * 根据apiCode 查询接口配置
     */
    ApiServiceConfig getOperationByCode(String apiConfig);

}
