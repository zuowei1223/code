package com.tcoiss.common.mq.configure;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * groupName: 发送同一类消息的设置为同一个 group，保证唯一， 默认不需要设置，rocketmq 会使用 ip@pid(pid代表jvm名字) 作为唯一标示。
 * namesrvAddr：Name Server 地址
 * maxMessageSize：消息最大限制，默认 4M
 * sendMsgTimeout：消息发送超时时间，默认 3 秒
 * retryTimesWhenSendFailed：消息发送失败重试次数，默认 2 次
 */
@Configuration
public class MQProducerConfiguration {

    public static final Logger LOGGER = LoggerFactory.getLogger(MQProducerConfiguration.class);

    @Value("${rocketmq.producer.groupName}")
    private String groupName;

    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.producer.maxMessageSize}")
    private Integer maxMessageSize;

    @Value("${rocketmq.producer.sendMsgTimeout}")
    private Integer sendMsgTimeout;

    @Value("${rocketmq.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailed;

    @Bean
    @ConditionalOnMissingBean
    public DefaultMQProducer defaultMQProducer() throws RuntimeException {
        DefaultMQProducer producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);
        producer.setCreateTopicKey("AUTO_CREATE_TOPIC_KEY");
        //如果需要同一个 jvm 中不同的 producer 往不同的 job 集群发送消息，需要设置不同的 instanceName
        //producer.setInstanceName(instanceName);
        //如果发送消息的最大限制
        producer.setMaxMessageSize(this.maxMessageSize);
        //如果发送消息超时时间
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        //如果发送消息失败，设置重试次数，默认为 2 次
        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        try {
            producer.start();
            LOGGER.info("生产者启动. groupName:{}, namesrvAddr: {}", groupName, namesrvAddr);
        } catch (MQClientException e) {
            LOGGER.error("生产者启动失败.", e);
            throw new RuntimeException(e);
        }
        return producer;
    }
}
