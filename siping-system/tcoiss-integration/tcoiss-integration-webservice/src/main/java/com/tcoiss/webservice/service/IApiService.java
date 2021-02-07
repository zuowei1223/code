package com.tcoiss.webservice.service;

import com.tcoiss.common.core.web.domain.AjaxResult;

import java.util.List;
import java.util.Map;

public interface IApiService {

    Map apiTest(Long id) ;

    //根据请求编码，以及请求参数对象（map）执行api请求
    Map executeByApiCode(String apiCode, Object param);
}
