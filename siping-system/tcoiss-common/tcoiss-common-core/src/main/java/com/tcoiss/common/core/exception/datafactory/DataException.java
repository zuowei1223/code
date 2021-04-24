package com.tcoiss.common.core.exception.datafactory;

import com.tcoiss.common.core.exception.BaseException;

public class DataException extends BaseException {

    public DataException(String code, Object[] args, String message) {
        super("data", code, args, message);
    }
}
