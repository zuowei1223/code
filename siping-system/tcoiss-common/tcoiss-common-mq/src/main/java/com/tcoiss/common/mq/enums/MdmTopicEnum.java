package com.tcoiss.common.mq.enums;

/**
 * 
 * @ClassName MdmTopicEnum
 * @Description 主数据队列枚举
 * @author zhf
 * @Date 2017年9月11日 下午3:09:12
 * @version 1.0.0
 */
public enum MdmTopicEnum {

    DEPARTMENT("DEPARTMENT"),           //部门
    PERSON("PERSON"),                  //人员
    TEST("test");
    
    private String value;
    
    private MdmTopicEnum(String value) {
        this.value = value;
    }
 
    public String getValue() {
        return value;
    }
    
    public static MdmTopicEnum getByValue(String value) {  
        for(MdmTopicEnum myEnum : MdmTopicEnum.values()) {  
            if(myEnum.value.equals(value)) {  
                return myEnum; 
            }
        }
        throw new IllegalArgumentException("No element matches " + value);  
    }
}
