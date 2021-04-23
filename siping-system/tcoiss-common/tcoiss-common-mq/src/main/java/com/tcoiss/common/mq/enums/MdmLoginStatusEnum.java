package com.tcoiss.common.mq.enums;

public enum MdmLoginStatusEnum {

    ALLOW_LOGIN("0", "允许登录"),
    FORBID_LOGIN("1", "禁止登录");

    public String code;
    public String msg;

    private MdmLoginStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
