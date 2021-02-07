package com.tcoiss.common.core.exception.api;

import com.tcoiss.common.core.exception.BaseException;

public class ApiException extends BaseException {

    public ApiException(String code, Object[] args,String message) {
        super("webApi", code, args, message);
    }
}
