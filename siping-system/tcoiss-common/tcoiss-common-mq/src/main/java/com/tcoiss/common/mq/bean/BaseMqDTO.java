package com.tcoiss.common.mq.bean;

/**
 * 
 * @ClassName BaseMqDTO
 * @Description MQDTO基类
 * @author zhf
 * @Date 2017年10月16日 上午11:38:30
 * @version 1.0.0
 */
public class BaseMqDTO extends BaseMqEntity {
    private static final long serialVersionUID = 9023402149663693341L;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
