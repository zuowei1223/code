package com.tcoiss.business.base.service;

import com.tcoiss.business.base.domain.KdPerson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.business.base.dto.PersonDto;
import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.enums.OperMethodEnum;

import java.util.List;

/**
 * 人员Service接口
 *
 * @author zw
 * @date 2021-04-07
 */
public interface IKdPersonService extends IService<KdPerson> {

    /**
     * 查询列表
     */
    List<KdPerson> queryList(KdPerson kdPerson);

    boolean syncPerson(PersonDto person, OperMethodEnum operMethod, MdmTopicEnum topic, String msgId);
}
