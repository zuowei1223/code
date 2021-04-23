package com.tcoiss.webservice.api.model;

import lombok.Data;

@Data
public class ApiParam {
    private String apiCode;

    private String apiObj;

    private Object param;
}
