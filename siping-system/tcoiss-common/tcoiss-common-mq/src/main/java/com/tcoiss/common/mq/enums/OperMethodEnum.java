package com.tcoiss.common.mq.enums;

/**
 * 
 * @ClassName OperMethodEnum
 * @Description 主数据操作方式枚举
 * @author zhf
 * @Date 2017年9月19日 上午10:36:02
 * @version 1.0.0
 */
public enum OperMethodEnum {
    INSERT((short)1),              //新增
    UPDATE((short)2),              //全量更新
    UPDATESELECTIVE((short)3),     //更新，为空字段不更新
    DELETE((short)4);              //删除
    
    private short value;
    
    private OperMethodEnum(short value) {
        this.value = value;
    }
 
    public short getValue() {
        return value;
    }
    
    public static OperMethodEnum getByValue(short value) {  
        for(OperMethodEnum myEnum : OperMethodEnum.values()) {  
            if(myEnum.value == value) {  
                return myEnum;  
            }
        }
        throw new IllegalArgumentException("No element matches " + value);  
    }
}
