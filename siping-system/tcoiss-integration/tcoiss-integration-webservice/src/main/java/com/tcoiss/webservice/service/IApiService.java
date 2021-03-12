package com.tcoiss.webservice.service;


import java.util.Map;

public interface IApiService {

    Map apiTest(Long id) ;

    //根据请求编码，以及请求参数对象（map）执行api请求
    Map executeByApiCode(String apiCode, Object param);

    Map<String, Object> executeKdByApiCode(String apiCode, Object param,String token);

    String getKdAccessToken();
}
