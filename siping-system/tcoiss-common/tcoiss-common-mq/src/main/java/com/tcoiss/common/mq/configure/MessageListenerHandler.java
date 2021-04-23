package com.tcoiss.common.mq.configure;

import com.tcoiss.common.mq.bean.IMdmReceiveHandle;
import com.tcoiss.common.mq.bean.MdmMessage;
import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.exception.MqException;
import com.tcoiss.common.mq.util.ByteUtil;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class MessageListenerHandler implements MessageListenerConcurrently {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListenerHandler.class);
    private static String TOPIC = "DemoTopic";
    private IMdmReceiveHandle mdmReceiveHandle; // 处理逻辑类

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                    ConsumeConcurrentlyContext context) {
        MessageExt msg = msgs.get(0);
        LOGGER.info(Thread.currentThread().getName() + " 获取到新的消息，消息ID: " + msg.getMsgId());
        try {
            String msgId = msg.getMsgId();
            String topicName = msg.getTopic();
            String tags = msg.getTags();
            MdmMessage mdmMessage = (MdmMessage) ByteUtil.byteToObject(msg.getBody());
            // 过滤是否需要执行处理代码
            MdmTopicEnum topic = MdmTopicEnum.getByValue(topicName);
            if (mdmReceiveHandle.doFilter(msgId, topic, tags, mdmMessage)) {
                mdmReceiveHandle.execute(msgId, topic, tags, mdmMessage);
                LOGGER.info("消息ID：" + msg.getMsgId() + "执行同步操作成功");
            } else {
                LOGGER.info("消息ID：" + msg.getMsgId() + "不符合同步条件，不用执行同步操作");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            // 执行错误逻辑
            e.printStackTrace();
            if (isRetry(msg, e)) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            } else {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        }
    }

    /**
     * @Description 判断是否重试
     * @return
     */
    private boolean isRetry(MessageExt message, Exception ex) {
        if (message.getReconsumeTimes() > 10) {
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
}
