package com.tcoiss.common.mq.bean;


import com.tcoiss.common.mq.enums.MdmTopicEnum;
import lombok.Data;

/**
 * @ClassName SubscribeBean
 * @Description 订阅规则实体类
 * @author zhf
 * @Date 2017年9月18日 上午9:07:43
 * @version 1.0.0
 */
@Data
public class SubscribeBean {

    private String topic; // 订阅组
    private String tags; // 订阅标签，和消息选择器二选一，优先消息选择器


}
