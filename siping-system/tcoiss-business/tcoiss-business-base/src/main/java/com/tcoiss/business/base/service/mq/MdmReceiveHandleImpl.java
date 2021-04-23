package com.tcoiss.business.base.service.mq;

import com.alibaba.fastjson.JSON;
import com.tcoiss.business.base.domain.KdPerson;
import com.tcoiss.business.base.dto.PersonDto;
import com.tcoiss.business.base.service.IKdPersonService;
import com.tcoiss.common.mq.bean.IMdmReceiveHandle;
import com.tcoiss.common.mq.bean.MdmMessage;
import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.exception.MqException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName MdmReceiveHandleImpl
 * @Description 接收消息逻辑处理实现类
 * @author zhf
 * @Date 2017年9月20日 上午9:16:34
 * @version 1.0.0
 */
@Service
public class MdmReceiveHandleImpl implements IMdmReceiveHandle {

    private final Logger logger = LoggerFactory.getLogger(MdmReceiveHandleImpl.class);

    @Autowired
    private IKdPersonService kdPersonService;
    /*@Autowired
    private ISyncRecordService syncRecordService;*/

    /**
     * @Description 判断是否执行逻辑代码
     * @param msgId
     * @param
     * @param tags
     * @param mdmMessage
     * @return
     */
    @Override
    public boolean doFilter(String msgId, MdmTopicEnum topic, String tags, MdmMessage mdmMessage) {
        //目前只是判断去重
        //return !syncRecordService.selectIsExists(topic.getValue(), msgId);
        return true;
    }

    /**
     * @Description 接收消息后执行代码
     * @param msgId
     * @param
     * @param tags
     * @param mdmMessage
     * @throws MqException
     */
    @Override
    public void execute(String msgId, MdmTopicEnum topic, String tags, MdmMessage mdmMessage) throws MqException {
        // 执行方法
        logger.info("执行处理逻辑，消息ID:" + msgId);
        try {
            // 部门队列处理逻辑
            if (topic.compareTo(MdmTopicEnum.DEPARTMENT) == 0) {

            }
            // 人员队列处理逻辑
            else if (topic.compareTo(MdmTopicEnum.PERSON) == 0) {
                PersonDto person = JSON.toJavaObject(mdmMessage.getJsonObject() , PersonDto.class);
                kdPersonService.syncPerson(person, mdmMessage.getOperMethod(), topic, msgId);
            }
            else if (topic.compareTo(MdmTopicEnum.TEST) == 0) {
                PersonDto person = JSON.toJavaObject(mdmMessage.getJsonObject() , PersonDto.class);
                kdPersonService.syncPerson(person, mdmMessage.getOperMethod(), topic, msgId);
            }

        } catch (Exception e) {
            throw new MqException(e.getMessage(), true);
        }
    }
}
