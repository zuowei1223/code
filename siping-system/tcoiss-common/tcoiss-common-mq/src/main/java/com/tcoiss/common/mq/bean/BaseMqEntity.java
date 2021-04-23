package com.tcoiss.common.mq.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class BaseMqEntity implements Serializable {

    private static final long serialVersionUID = -5528628517384851669L;

    public String toString() {
        return JSON.toJSONString(this);
    }
}
