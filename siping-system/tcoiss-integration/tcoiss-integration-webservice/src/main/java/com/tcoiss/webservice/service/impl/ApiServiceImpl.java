package com.tcoiss.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.webservice.apiServer.HttpAPIServer;
import com.tcoiss.webservice.apiServer.HttpInvoker;
import com.tcoiss.webservice.apiServer.InvokeContext;
import com.tcoiss.webservice.apiServer.InvokerBuilder;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.mapper.ApiServiceConfigMapper;
import com.tcoiss.webservice.service.IApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ApiServiceImpl implements IApiService {

    @Autowired
    private ApiServiceConfigMapper apiServiceConfigMapper;

    @Autowired
    private HttpAPIServer httpAPIServer;

    @Autowired
    private RedisService redisService;

    private final static long EXPIRE_TIME = Constants.KD_TOKEN_EXPIRE * 90;

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
        context.setRequestType(serviceConfig.getRequestType());
        context.setParameters("");
        Map resultMap = httpInvoker.Invoke(httpAPIServer,context);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { serviceConfig.getApiCode() },"http请求连接异常");
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
        Map<String,Object> resultMap = httpInvoker.Invoke(httpAPIServer,context);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { serviceConfig.getApiCode() },"http请求连接异常");
            //return AjaxResult.error("连接异常,请检查地址是否正确");
        }else{
            return resultMap;
        }

    }

    @Override
    public Map<String, Object> executeKdByApiCode(String apiCode, Object param) {
        String token = getKdAccessToken();
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
        context.setAccessToken(token);
        Map<String,Object> resultMap = httpInvoker.Invoke(httpAPIServer,context);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { serviceConfig.getApiCode() },"http请求连接异常");
            //return AjaxResult.error("连接异常,请检查地址是否正确");
        }else{
            return resultMap;
        }

    }

    public String getKdAccessToken() {
        String accessToken = redisService.getCacheObject("kd_access_token");
        if(StringUtils.isEmpty(accessToken)){
            Map<String,Object> appMap = this.executeByApiCode("getAppToken",new HashMap<>());
            if(appMap!= null&&appMap.get("state").equals("success")){
                String appToken = (String)((Map)appMap.get("data")).get("app_token");
                Map<String,Object> param = new HashMap<>();
                param.put("appToken",appToken);
                Map<String,Object> accessMap = this.executeByApiCode("getUserToken",param);
                if(accessMap!= null&&accessMap.get("state").equals("success")){
                    accessToken = (String)((Map)accessMap.get("data")).get("access_token");
                    redisService.setCacheObject("kd_access_token",accessToken,EXPIRE_TIME, TimeUnit.SECONDS);
                    return accessToken;
                }else{
                    throw new ApiException("500",new Object[] { "getUserToken" },"获取用户令牌请求连接异常");
                }
            }else{
                throw new ApiException("500",new Object[] { "getAppToken" },"获取应用令牌请求连接异常");
            }
        }
        return accessToken;

    }


}
