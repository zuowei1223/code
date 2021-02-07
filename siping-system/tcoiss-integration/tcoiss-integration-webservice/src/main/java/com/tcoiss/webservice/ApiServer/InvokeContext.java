package com.tcoiss.webservice.ApiServer;

import lombok.Data;

import java.security.Timestamp;

@Data
public class InvokeContext {
    private Object parameters;
    private String resultString;
    private String requestString;
    //private String responeString;
    private String operationCode;
    private String requestTime;
    private String responseTime;
    private String endpoint;
    private String requestType;
}
