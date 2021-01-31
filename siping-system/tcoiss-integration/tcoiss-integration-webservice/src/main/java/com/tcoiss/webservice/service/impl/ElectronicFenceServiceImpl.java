package com.tcoiss.webservice.service.impl;

import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.mapper.ElectronicFenceMapper;
import com.tcoiss.webservice.service.IElectronicFenceService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 电子围栏Service业务层处理
 *
 * @author zw
 * @date 2021-01-31
 */
@Service
public class ElectronicFenceServiceImpl extends ServiceImpl<ElectronicFenceMapper, ElectronicFence> implements IElectronicFenceService {

    @Override
    public List<ElectronicFence> queryList(ElectronicFence electronicFence) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (electronicFence.getLocalKey() != null){
            lqw.eq(ElectronicFence::getLocalKey ,electronicFence.getLocalKey());
        }
        if (electronicFence.getFenceGid() != null){
            lqw.eq(ElectronicFence::getFenceGid ,electronicFence.getFenceGid());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceName())){
            lqw.like(ElectronicFence::getFenceName ,electronicFence.getFenceName());
        }
        if (StringUtils.isNotBlank(electronicFence.getFencePoints())){
            lqw.eq(ElectronicFence::getFencePoints ,electronicFence.getFencePoints());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceEnable())){
            lqw.eq(ElectronicFence::getFenceEnable ,electronicFence.getFenceEnable());
        }
        if (StringUtils.isNotBlank(electronicFence.getValidTime())){
            lqw.eq(ElectronicFence::getValidTime ,electronicFence.getValidTime());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceRepeat())){
            lqw.eq(ElectronicFence::getFenceRepeat ,electronicFence.getFenceRepeat());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceTime())){
            lqw.eq(ElectronicFence::getFenceTime ,electronicFence.getFenceTime());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceDesc())){
            lqw.eq(ElectronicFence::getFenceDesc ,electronicFence.getFenceDesc());
        }
        if (StringUtils.isNotBlank(electronicFence.getAlertCondition())){
            lqw.eq(ElectronicFence::getAlertCondition ,electronicFence.getAlertCondition());
        }
        if (electronicFence.getCreateorId() != null){
            lqw.eq(ElectronicFence::getCreateorId ,electronicFence.getCreateorId());
        }
        return this.list(lqw);
    }
}
