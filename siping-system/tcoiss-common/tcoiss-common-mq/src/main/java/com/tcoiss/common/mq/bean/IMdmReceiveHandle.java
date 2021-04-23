package com.tcoiss.common.mq.bean;

import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.exception.MqException;

/**
 * 
 * @ClassName IMdmReceiveHandle
 * @Description 消息接收处理接口
 * @author zhf
 * @Date 2017年9月18日 上午9:39:54
 * @version 1.0.0
 */
public interface IMdmReceiveHandle {
    
    /**
     * 
     * @Description 判断是否执行逻辑代码
     * @param msgId
     * @param
     * @param tags
     * @param mdmMessage
     * @return
     */
    public boolean doFilter(String msgId, MdmTopicEnum topic, String tags, MdmMessage mdmMessage);

    /**
     *
     * @Description 接收消息后执行代码
     * @param msgId
     * @param
     * @param tags
     * @param mdmMessage
     * @throws MqException
     */
    public void execute(String msgId, MdmTopicEnum topic, String tags, MdmMessage mdmMessage) throws MqException;

}
