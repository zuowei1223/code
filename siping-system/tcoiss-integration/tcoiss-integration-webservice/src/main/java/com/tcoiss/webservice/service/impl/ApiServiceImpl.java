package com.tcoiss.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.ApiServer.HttpAPIServer;
import com.tcoiss.webservice.ApiServer.HttpResult;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.mapper.ApiServiceConfigMapper;
import com.tcoiss.webservice.service.IApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ApiServiceImpl implements IApiService {

    @Autowired
    private ApiServiceConfigMapper apiServiceConfigMapper;

    @Autowired
    private HttpAPIServer httpAPIServer;

    @Override
    public AjaxResult apiTest(List<Long> asList)  {
        //todo 根据选择的id对创建0的挨个进行接口测试，测试成功的状态改为启用1
        for (Long id: asList) {
            //查询接口配置信息
            ApiServiceConfig serviceConfig = apiServiceConfigMapper.selectById(id);
            if(serviceConfig.getDataLevel()!=0){
                continue;
            }
            //获取请求地址
            String apiUrl = serviceConfig.getApiUrl();
            String paramer = serviceConfig.getParamTemplate();
            try {
                if(serviceConfig.getDataType().equals("post")&&serviceConfig.getDataType().equals("json")){
                    //解析参数
                    Map paramMap = new Gson().fromJson(paramer, Map.class);
                    HttpResult httpResult  = httpAPIServer.doPost(apiUrl,paramMap);
                    return AjaxResult.success(httpResult);
                }else if(serviceConfig.getDataType().equals("get")){
                    Map<String,Object> paramMap = new HashMap<>();//paramer
                    String result = httpAPIServer.doGet(apiUrl,paramMap);
                    return AjaxResult.success(result);
                }else{
                    return AjaxResult.error(-1,"请求方式异常");
                }
            } catch (Exception e) {
                return AjaxResult.error(99,"系统异常请检查代码");
            }
        }

        return AjaxResult.success();
    }
}
