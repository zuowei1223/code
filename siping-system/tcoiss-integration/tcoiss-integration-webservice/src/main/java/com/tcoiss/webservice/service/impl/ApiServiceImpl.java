package com.tcoiss.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.webservice.apiServer.HttpAPIServer;
import com.tcoiss.webservice.apiServer.HttpInvoker;
import com.tcoiss.webservice.apiServer.InvokeContext;
import com.tcoiss.webservice.apiServer.InvokerBuilder;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.mapper.ApiServiceConfigMapper;
import com.tcoiss.webservice.service.IApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApiServiceImpl implements IApiService {

    @Autowired
    private ApiServiceConfigMapper apiServiceConfigMapper;

    @Autowired
    private HttpAPIServer httpAPIServer;

    @Override
    public Map<String, Object> apiTest(Long id)  {
        //todo 根据选择的id对创建0的挨个进行接口测试，测试成功的状态改为启用1
        //查询接口配置信息
        ApiServiceConfig serviceConfig = apiServiceConfigMapper.selectById(id);
        if(serviceConfig.getDataLevel()!=0){
            throw new ApiException("-1",null,"接口配置未生效");
        }
        //初始化接口服务
        HttpInvoker httpInvoker = (HttpInvoker) new InvokerBuilder().buildInvoker(serviceConfig);
        InvokeContext context = new InvokeContext();
        context.setEndpoint(serviceConfig.getApiUrl());
        context.setOperationCode(serviceConfig.getApiCode());
        context.setParameters("");
        Map resultMap = httpInvoker.Invoke(httpAPIServer,context);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { serviceConfig.getApiCode() },"http请求连接异常");
            //return AjaxResult.error("连接异常,请检查地址是否正确");
        }else{
            return resultMap;
        }

    }

    @Override
    public Map<String, Object> executeByApiCode(String apiCode, Object param) {
        QueryWrapper<ApiServiceConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("api_code",apiCode);
        queryWrapper.eq("data_level",1);
        //查询接口配置信息
        ApiServiceConfig serviceConfig = apiServiceConfigMapper.selectOne(queryWrapper);
        if(serviceConfig==null||serviceConfig.getDataLevel()!=1){
            throw new ApiException("-1",new Object[]{apiCode},"接口未配置或未启用");
        }
        HttpInvoker httpInvoker = (HttpInvoker) new InvokerBuilder().buildInvoker(serviceConfig);
        InvokeContext context = new InvokeContext();
        context.setEndpoint(serviceConfig.getApiUrl());
        context.setOperationCode(serviceConfig.getApiCode());
        context.setRequestType(serviceConfig.getRequestType());
        context.setParameters(param);
        context.setDataType(serviceConfig.getDataType());
        return httpInvoker.Invoke(httpAPIServer,context);

    }



}
