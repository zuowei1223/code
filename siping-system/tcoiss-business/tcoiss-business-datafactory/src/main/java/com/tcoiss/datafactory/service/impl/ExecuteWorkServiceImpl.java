package com.tcoiss.datafactory.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import com.tcoiss.datafactory.mapper.ExecuteWorkMapper;
import com.tcoiss.datafactory.domain.ExecuteWork;
import com.tcoiss.datafactory.service.IExecuteWorkService;

import java.util.List;
import java.util.Map;

/**
 * 作业Service业务层处理
 *
 * @author zw
 * @date 2021-04-26
 */
@Service
public class ExecuteWorkServiceImpl extends ServiceImpl<ExecuteWorkMapper, ExecuteWork> implements IExecuteWorkService {

    @Override
    public List<ExecuteWork> queryList(ExecuteWork executeWork) {
        LambdaQueryWrapper<ExecuteWork> lqw = Wrappers.lambdaQuery();
        if (executeWork.getSchemeId() != null){
            lqw.eq(ExecuteWork::getSchemeId ,executeWork.getSchemeId());
        }
        if (StringUtils.isNotBlank(executeWork.getWorkName())){
            lqw.like(ExecuteWork::getWorkName ,executeWork.getWorkName());
        }
        if (StringUtils.isNotBlank(executeWork.getTableName())){
            lqw.like(ExecuteWork::getTableName ,executeWork.getTableName());
        }
        if (executeWork.getWorkType() != null){
            lqw.eq(ExecuteWork::getWorkType ,executeWork.getWorkType());
        }
        if (executeWork.getStatus() != null){
            lqw.eq(ExecuteWork::getStatus ,executeWork.getStatus());
        }
        return this.list(lqw);
    }

    @Override
    public List<ExecuteWork> getWorksBySchemeId(Long schemeId) {
        LambdaQueryWrapper<ExecuteWork> lqw = Wrappers.lambdaQuery();
        if (schemeId != null){
            lqw.eq(ExecuteWork::getSchemeId ,schemeId);
        }

        return this.list(lqw);
    }


}
