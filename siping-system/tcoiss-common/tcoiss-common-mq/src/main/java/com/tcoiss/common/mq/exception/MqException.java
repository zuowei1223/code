package com.tcoiss.common.mq.exception;

/**
 * 
 * @ClassName MqException
 * @Description 消息处理异常类
 * @author zhf
 * @Date 2017年9月18日 上午10:10:30
 * @version 1.0.0
 */
public class MqException extends RuntimeException {

    private static final long serialVersionUID = -3372727668159591290L;
    
    //是否重试
    private boolean isRetry;
    
    public MqException(String message, boolean isRetty) {
        super(message);
        this.isRetry = isRetty;
    }

    public boolean getIsRetry() {
        return isRetry;
    }

    public void setIsRetry(boolean isRetry) {
        this.isRetry = isRetry;
    }
    
}
