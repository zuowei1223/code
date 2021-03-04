package com.tcoiss.webservice.apiServer;

import com.alibaba.fastjson.JSON;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.utils.DateUtils;
import com.tcoiss.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HttpInvoker extends Invoker{


    public Map<String,Object> Invoke(HttpAPIServer httpAPIServer,InvokeContext invokeContext){
        Map<String,Object> resultMap = new HashMap<>();
        try{
            String targerUrl = invokeContext.getEndpoint();
            String response = "";
            String reqBody = generateRequestString(invokeContext);
            invokeContext.setRequestTime(DateUtils.getTime());
            /*if(targerUrl.startsWith("https")){//需要秘钥
                targerUrl = targerUrl+"a33e36c0e15b373a2ebd7fc4aa1ec0c2";
            }*/
            //根据请求方式
            if(StringUtils.equals("post",invokeContext.getRequestType())&&StringUtils.equals("json",invokeContext.getDataType())){
                response = httpAPIServer.doPostJson(targerUrl,reqBody,invokeContext.getDataType());
            }else if (StringUtils.equals("post",invokeContext.getRequestType())){
                response = httpAPIServer.doPost(targerUrl,reqBody);

            }else if (StringUtils.equals("get",invokeContext.getRequestType())){
                //拼装请求地址
                targerUrl = targerUrl+reqBody;
                response = httpAPIServer.doGet(targerUrl);
            }
            //response 不为null时，解析响应为Map
            if(!StringUtils.isEmpty(response)){
                resultMap = JSON.parseObject(response);
            }
        }catch (Exception e){
            throw new ApiException("9003",null,"HTTP请求["+invokeContext.getOperationCode()+"]失败");
        }
        return resultMap;
    }
}
