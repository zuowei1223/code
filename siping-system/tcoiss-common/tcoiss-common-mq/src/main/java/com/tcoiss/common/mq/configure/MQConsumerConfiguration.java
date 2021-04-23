package com.tcoiss.common.mq.configure;

import com.tcoiss.common.mq.bean.IMdmReceiveHandle;
import com.tcoiss.common.mq.bean.MdmMessage;
import com.tcoiss.common.mq.bean.SubscribeBean;
import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.exception.MqException;
import com.tcoiss.common.mq.util.ByteUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息MQ配置
 * 
 * @author tcoiss
 */
@Component
public class MQConsumerConfiguration
{
    public static final Logger LOGGER = LoggerFactory.getLogger(MQConsumerConfiguration.class);
    @Value("${rocketmq.consumer.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.consumer.groupName}")
    private String groupName;
    @Value("${rocketmq.consumer.instanceName}")
    private String instanceName; // 当前消费者实例名称
    @Value("${rocketmq.consumer.consumeThreadMax}")
    private int maxThread;
    @Value("${rocketmq.consumer.consumeThreadMin}")
    private int minThread;
    // 订阅指定的 topic
    @Value("#{'${rocketmq.consumer.subscribes}'.split(',')}")
    private List<String> subscribes;

    @Value("${rocketmq.consumer.allowRetryTime}")
    private int allowRetryTime; // 允许重试次数

    @Autowired
    private IMdmReceiveHandle mdmReceiveHandle; // 处理逻辑类

    /*@Autowired
    private MessageListenerHandler mqMessageListenerProcessor;*/

    @Bean
    @ConditionalOnMissingBean
    public DefaultMQPushConsumer defaultMQPushConsumer() throws RuntimeException {
        // 初始化消费者
        DefaultMQPushConsumer defaultMQConsumer = new DefaultMQPushConsumer(groupName);
        defaultMQConsumer.setNamesrvAddr(namesrvAddr);
        defaultMQConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);// 默认初次初始化从第一条获取消息
        defaultMQConsumer.setConsumeMessageBatchMaxSize(1);// 一次接收一条记录
        defaultMQConsumer.setMessageModel(MessageModel.BROADCASTING);// 使用非集群消费模式
        defaultMQConsumer.setInstanceName(instanceName);
        defaultMQConsumer.setVipChannelEnabled(false);
        if (maxThread != 0 && minThread != 0) {
            defaultMQConsumer.setConsumeThreadMax(maxThread);
            defaultMQConsumer.setConsumeThreadMin(minThread);
        }
        try {
            // 设置该消费者订阅的主题和tag，如果是订阅该主题下的所有tag，使用*；
            for (String subscribe : subscribes) {
                LOGGER.info("subscribe Topic,Topic:" + subscribe + ",tags: *");
                defaultMQConsumer.subscribe(subscribe, "*");// 设置订阅条件
            }
            defaultMQConsumer.setPullBatchSize(1);// 设置一次获取消息记录
            defaultMQConsumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                MessageExt messageExt = list.get(0);
                try {
                    LOGGER.info(Thread.currentThread().getName() + " 获取到新的消息，消息ID: " + messageExt.getMsgId());
                    String msgId = messageExt.getMsgId();
                    String topicName = messageExt.getTopic();
                    String tags = messageExt.getTags();
                    LOGGER.info("messageExt: " + messageExt);//输出消息内容
                    // 过滤是否需要执行处理代码
                    String messageBody = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    MdmTopicEnum topic = MdmTopicEnum.getByValue(topicName);
                    MdmMessage mdmMessage =  ByteUtil.byteToMsg(messageExt.getBody());
                    if (mdmReceiveHandle.doFilter(msgId, topic, tags, mdmMessage)) {
                        mdmReceiveHandle.execute(msgId, topic, tags, mdmMessage);
                        LOGGER.info("消息ID：" + messageExt.getMsgId() + "执行同步操作成功");
                    } else {
                        LOGGER.info("消息ID：" + messageExt.getMsgId() + "不符合同步条件，不用执行同步操作");
                    }
                    LOGGER.info("消费响应：msgId : " + messageExt.getMsgId() + ", msgBody : " + messageBody);//输出消息内容
                } catch (Exception e) {
                    if (isRetry(messageExt, e)) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER; //稍后再试
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; //消费成功
            });
            // 启动消费
            defaultMQConsumer.start();
            LOGGER.info("consumer is started. groupName:{}, topics:{}, namesrvAddr:{}",groupName,subscribes.toString(),namesrvAddr);

        } catch (Exception e) {
            LOGGER.error("failed to start consumer . groupName:{}, topics:{}, namesrvAddr:{}",groupName,subscribes.toString(),namesrvAddr,e);
            throw new RuntimeException(e);
        }
        return defaultMQConsumer;
    }

    /**
     * Spring bean destroy-method
     */
    /*public void destroy() {
        defaultMQConsumer.shutdown();
    }*/

    /**
     * @Description 判断是否重试
     * @return
     */
    private boolean isRetry(MessageExt message, Exception ex) {
        if (message.getReconsumeTimes() > allowRetryTime) {
            LOGGER.error("处理消息失败，消息队列："+message.getTopic()+",消息ID：" + message.getMsgId() + "，错误原因：" + ex.getMessage());
            LOGGER.error("消息ID：" + message.getMsgId() + "重试次数已经超过上限，不再重新尝试");
            return false;
        } else if (ex instanceof MqException) {
            MqException e = (MqException) ex;
            if (e.getIsRetry()) {
                LOGGER.info("处理消息失败，消息队列："+message.getTopic()+",消息ID：" + message.getMsgId() + "，错误原因：" + ex.getMessage());
                LOGGER.info("消息ID：" + message.getMsgId() + "将在下次重新尝试消费");
                return true;
            } else {
                LOGGER.error("处理消息失败，消息队列："+message.getTopic()+",消息ID：" + message.getMsgId() + "，错误原因：" + ex.getMessage());
                LOGGER.error("消息ID：" + message.getMsgId() + "将不再重新尝试消费");
                return false;
            }
        } else {
            LOGGER.error("处理消息失败，消息ID：消息队列："+message.getTopic()+"," + message.getMsgId() + "，错误原因：" + ex.getMessage());
            LOGGER.error("消息ID：" + message.getMsgId() + "由于系统异常,将在下次重新消费");
            return true;
        }
    }

    // -------getter and setter----------
    /*public DefaultMQPushConsumer getDefaultMQConsumer() {
        return defaultMQConsumer;
    }

    public void setDefaultMQConsumer(DefaultMQPushConsumer defaultMQConsumer) {
        this.defaultMQConsumer = defaultMQConsumer;
    }*/




}
