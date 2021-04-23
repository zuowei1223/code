package com.tcoiss.business.base.service.impl;

import com.tcoiss.business.base.dto.PersonDto;
import com.tcoiss.common.core.utils.DateUtils;
import com.tcoiss.common.core.utils.bean.BeanUtils;
import com.tcoiss.common.mq.enums.MdmTopicEnum;
import com.tcoiss.common.mq.enums.OperMethodEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import com.tcoiss.business.base.mapper.KdPersonMapper;
import com.tcoiss.business.base.domain.KdPerson;
import com.tcoiss.business.base.service.IKdPersonService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 人员Service业务层处理
 *
 * @author zw
 * @date 2021-04-07
 */
@Service
public class KdPersonServiceImpl extends ServiceImpl<KdPersonMapper, KdPerson> implements IKdPersonService {

    /*@Autowired
    private ISyncRecordService syncRecordService;*/

    @Override
    public List<KdPerson> queryList(KdPerson kdPerson) {
        LambdaQueryWrapper<KdPerson> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(kdPerson.getPersonName())){
            lqw.like(KdPerson::getPersonName ,kdPerson.getPersonName());
        }
        if (StringUtils.isNotBlank(kdPerson.getPersonNo())){
            lqw.eq(KdPerson::getPersonNo ,kdPerson.getPersonNo());
        }
        if (kdPerson.getModifyTime() != null){
            lqw.eq(KdPerson::getModifyTime ,kdPerson.getModifyTime());
        }
        if (StringUtils.isNotBlank(kdPerson.getEnable())){
            lqw.eq(KdPerson::getEnable ,kdPerson.getEnable());
        }
        if (StringUtils.isNotBlank(kdPerson.getStatus())){
            lqw.eq(KdPerson::getStatus ,kdPerson.getStatus());
        }
        return this.list(lqw);
    }

    @Override
    public boolean syncPerson(PersonDto personDto, OperMethodEnum operMethod, MdmTopicEnum topic, String msgId) {
        boolean flag = false;
        KdPerson person = new KdPerson();
        BeanUtils.copyBean(personDto,person);
        KdPerson oldPerson = this.getByKdId(personDto.getId());
        if(operMethod.compareTo(OperMethodEnum.INSERT)==0||operMethod.compareTo(OperMethodEnum.UPDATE)==0){
            if(oldPerson == null){//新增
                person.setCreateTime(DateUtils.getNowDate());
                flag = this.save(person);
            }else{//修改
                person.setId(oldPerson.getId());
                flag = this.updateById(person);
            }
        }
        if(topic!=null){
            /*// 保存同步记录
            syncRecordService.insertSyncRecord(topic, msgId, operMethod, userMqDTO);*/
        }
        return flag;
    }



    private KdPerson getByKdId(String kdId){
        LambdaQueryWrapper<KdPerson> lqw = Wrappers.lambdaQuery();
        lqw.eq(KdPerson::getPersonNumber ,kdId);
        return this.getOne(lqw);
    }

}
