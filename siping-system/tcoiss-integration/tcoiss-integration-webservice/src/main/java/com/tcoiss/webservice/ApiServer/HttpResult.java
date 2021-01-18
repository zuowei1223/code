package com.tcoiss.webservice.ApiServer;

import lombok.Data;
import lombok.Generated;
import lombok.ToString;
import lombok.extern.java.Log;

@Data
@ToString
@Log
@Generated
public class HttpResult {
    // 响应的状态码
    private int code;
    // 响应的响应体

    private String body;

    public HttpResult(int code, String body) {
        this.code = code;
        this.body = body;
    }
}
