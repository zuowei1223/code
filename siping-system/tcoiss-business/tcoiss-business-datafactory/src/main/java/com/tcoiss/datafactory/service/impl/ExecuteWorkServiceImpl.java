package com.tcoiss.datafactory.service.impl;

import com.tcoiss.common.datasource.annotation.Master;
import com.tcoiss.common.datasource.annotation.System;
import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.service.IBusTableService;
import com.tcoiss.datafactory.service.IDynamicSqlService;
import org.springframework.beans.factory.annotation.Autowired;
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
@System
public class ExecuteWorkServiceImpl extends ServiceImpl<ExecuteWorkMapper, ExecuteWork> implements IExecuteWorkService {

    @Autowired
    private IBusTableService iBusTableService;

    @Autowired
    private IDynamicSqlService iDynamicSqlService;

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

    @Override
    public void executeWork(ExecuteWork work) {
        //判断作业类型 1.全量同步 2.增量同步 3.执行存储过程 4.执行ETL 5.数据转换
        BusTable table = iBusTableService.getBusTableByName(work.getTableName());
        boolean flag = false;
        String script = work.getWorkScript();
        switch (work.getWorkType()){
            case 1:
                //根据业务表以及作业脚本执行全量同步，先查询业务表数据
                flag = iBusTableService.syncDataByApi(table,script);
                break;
            case 2:
                //根据业务表以及作业脚本执行全量同步，先查询业务表数据
                flag = iBusTableService.syncDataByApi(table,script);
                break;
            case 3:
                iDynamicSqlService.callProcedure(table,script);
                break;

            default : return;
        }


    }


}
