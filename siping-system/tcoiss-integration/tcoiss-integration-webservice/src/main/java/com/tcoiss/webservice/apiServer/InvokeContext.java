package com.tcoiss.webservice.apiServer;

import lombok.Data;

@Data
public class InvokeContext {
    private Object parameters;
    private String resultString;
    private String requestString;
    private String accessToken;
    private String operationCode;
    private String requestTime;
    private String responseTime;
    private String endpoint;
    private String requestType;
    private String dataType;
}
