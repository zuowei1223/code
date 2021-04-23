package com.tcoiss.common.mq.enums;

/**
 * 
 * @ClassName MdmSexEnum
 * @Description MQ性别枚举
 * @author zhf
 * @Date 2017年9月11日 下午3:09:12
 * @version 1.0.0
 */
public enum MdmSexEnum {

	SEX_MAN("1", "男"),
    SEX_WOMAN("2", "女");
    
    public String code;
    public String msg;

    private MdmSexEnum(String code, String msg) {
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
