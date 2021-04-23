package com.tcoiss.common.mq.bean;

import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.util.ByteUtil;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName MdmProducer
 * @Description 消息队列生产者
 * @author zhf
 * @Date 2017年9月13日 下午3:01:50
 * @version 1.0.0
 */
public class MdmProducer {

    private final Logger LOGGER = LoggerFactory.getLogger(MdmProducer.class);

    private DefaultMQProducer defaultMQProducer;
    private String producerGroup;                       //生产组
    private String namesrvAddr;                         //MQ地址
    private String instanceName;                        //生产者实例名称
    private MessageQueueSelector messageQueueSelector;  //队列选择器

    /**
     * Spring bean init-method
     */
    public void init() throws MQClientException {
        // 参数信息
        LOGGER.info("DefaultMQProducer initialize!");
        LOGGER.info("producerGroup is :" + producerGroup);
        LOGGER.info("namesrvAddr is :" + namesrvAddr);

        // 初始化
        defaultMQProducer = new DefaultMQProducer(producerGroup);
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        defaultMQProducer.setInstanceName(instanceName);
        defaultMQProducer.setVipChannelEnabled(false);
        defaultMQProducer.start();
        LOGGER.info("DefaultMQProudcer start success!");
    }
    
    /**
     * 
     * @Description 发送顺序短消息到消息队列
     * @param mdmMessage
     * @return
     */
    public SendResult sendOrderMsg(MdmTopicEnum topic, String tags, MdmMessage mdmMessage) throws MQClientException,RemotingException, MQBrokerException, InterruptedException{
        Message msg = new Message(topic.getValue(),// topic
                tags,// tag
                ByteUtil.objectToByte(mdmMessage));// body
        if(messageQueueSelector == null){
            messageQueueSelector = new MdmMessageQueueSelector();
        }
        SendResult sendResult = defaultMQProducer.send(msg,messageQueueSelector, mdmMessage.getObjId());
        LOGGER.info("发送MQ消息成功,MsgId：" + sendResult.getMsgId());
        return sendResult;
    }
    
    /**
     * 
     * @Description 发送短消息到消息队列
     * @param mdmMessage
     * @return
     */
    public SendResult sendOrderMsg(MdmTopicEnum topic, MdmMessage mdmMessage) throws MQClientException,RemotingException, MQBrokerException, InterruptedException{
        return sendOrderMsg(topic ,"" , mdmMessage);
    }

    /**
     * Spring bean destroy-method
     */
    public void destroy() {
        defaultMQProducer.shutdown();
    }

    // ---------------setter and getter -----------------
    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public DefaultMQProducer getDefaultMQProducer() {
        return defaultMQProducer;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    
}
