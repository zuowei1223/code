package com.tcoiss.common.mq.bean;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;
import java.util.Random;

/**
 * 
 * @ClassName MqMessageQueueSelector
 * @Description MQ队列选择类，用于解决消息队列顺序性问题
 * @author zhf
 * @Date 2017年9月13日 下午4:27:44
 * @version 1.0.0
 */
public class MdmMessageQueueSelector implements MessageQueueSelector {
    
    /**
     * 根据KEY 选择队列
     */
    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {  
        int index = 0; 
        if(arg==null){
            //没有传入key 则随机选择队列
            Random rand = new Random();
            index = rand.nextInt(mqs.size());
        }
        else{
            //根据KEY值取ASCII之和，再根据和求余数确认队列，确保同一个KEY在相同队列
            String key = arg.toString();  
            int temp = 0;
            for (int i = 0; i < key.length(); i++) {
                temp += Integer.valueOf(key.charAt(i));
            }
            index = temp % mqs.size();  
        }
        return mqs.get(index);  
    }
}
