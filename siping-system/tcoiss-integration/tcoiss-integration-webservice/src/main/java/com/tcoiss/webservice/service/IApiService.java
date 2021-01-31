package com.tcoiss.webservice.service;

import com.tcoiss.common.core.web.domain.AjaxResult;

import java.util.List;
import java.util.Map;

public interface IApiService {

    AjaxResult apiTest(List<Long> asList) ;

    //根据请求编码，以及请求参数对象（map）执行api请求
    AjaxResult executeByApiCode(String apiCode, Map<String,Object> map);
}
