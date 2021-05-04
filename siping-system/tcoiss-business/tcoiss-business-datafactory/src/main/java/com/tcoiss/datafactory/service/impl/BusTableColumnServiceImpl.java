package com.tcoiss.datafactory.service.impl;

import com.tcoiss.common.datasource.annotation.Master;
import com.tcoiss.common.datasource.annotation.System;
import com.tcoiss.datafactory.domain.BusTableColumn;
import com.tcoiss.datafactory.mapper.BusTableColumnMapper;
import com.tcoiss.datafactory.service.IBusTableColumnService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代码生成业务字段Service业务层处理
 *
 * @author zw
 * @date 2021-04-20
 */
@Service
@System
public class BusTableColumnServiceImpl extends ServiceImpl<BusTableColumnMapper, BusTableColumn> implements IBusTableColumnService {

    @Override
    public List<BusTableColumn> queryList(BusTableColumn busTableColumn) {
        LambdaQueryWrapper<BusTableColumn> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(busTableColumn.getTableName())){
            lqw.eq(BusTableColumn::getTableName ,busTableColumn.getTableName());
        }
        if (StringUtils.isNotBlank(busTableColumn.getColumnName())){
            lqw.like(BusTableColumn::getColumnName ,busTableColumn.getColumnName());
        }
        if (StringUtils.isNotBlank(busTableColumn.getColumnComment())){
            lqw.eq(BusTableColumn::getColumnComment ,busTableColumn.getColumnComment());
        }
        if (StringUtils.isNotBlank(busTableColumn.getColumnType())){
            lqw.eq(BusTableColumn::getColumnType ,busTableColumn.getColumnType());
        }
        if (StringUtils.isNotBlank(busTableColumn.getIsPk())){
            lqw.eq(BusTableColumn::getIsPk ,busTableColumn.getIsPk());
        }
        if (StringUtils.isNotBlank(busTableColumn.getIsRequired())){
            lqw.eq(BusTableColumn::getIsRequired ,busTableColumn.getIsRequired());
        }
        if (StringUtils.isNotBlank(busTableColumn.getIsList())){
            lqw.eq(BusTableColumn::getIsList ,busTableColumn.getIsList());
        }
        if (StringUtils.isNotBlank(busTableColumn.getIsFilter())){
            lqw.eq(BusTableColumn::getIsFilter ,busTableColumn.getIsFilter());
        }
        if (StringUtils.isNotBlank(busTableColumn.getFilterType())){
            lqw.eq(BusTableColumn::getFilterType ,busTableColumn.getFilterType());
        }
        if (StringUtils.isNotBlank(busTableColumn.getDataType())){
            lqw.eq(BusTableColumn::getDataType ,busTableColumn.getDataType());
        }
        if (StringUtils.isNotBlank(busTableColumn.getDataModel())){
            lqw.eq(BusTableColumn::getDataModel ,busTableColumn.getDataModel());
        }
        if (busTableColumn.getEntryNum()!=null){
            lqw.eq(BusTableColumn::getEntryNum ,busTableColumn.getEntryNum());
        }
        if (busTableColumn.getSort() != null){
            lqw.eq(BusTableColumn::getSort ,busTableColumn.getSort());
        }
        return this.list(lqw);
    }

    @Override
    public List<BusTableColumn> getColumnsByName(String tableName) {
        BusTableColumn column = new BusTableColumn();
        column.setTableName(tableName);
        return this.queryList(column);
    }

    @Override
    public List<BusTableColumn> getListByTableName(String tableName) {
        LambdaQueryWrapper<BusTableColumn> lqw = Wrappers.lambdaQuery();
        lqw.eq(BusTableColumn::getTableName ,tableName);
        lqw.eq(BusTableColumn::getIsPk,"1");
        lqw.orderByDesc(BusTableColumn::getEntryNum );
        return  this.list(lqw);
    }

    @Override
    public List<BusTableColumn> getEntryNumByTableName(String tableName) {
        LambdaQueryWrapper<BusTableColumn> lqw = Wrappers.lambdaQuery();
        lqw.select(BusTableColumn::getEntryNum);
        lqw.eq(BusTableColumn::getTableName ,tableName);
        lqw.groupBy(BusTableColumn::getEntryNum,BusTableColumn::getTableName);
        lqw.orderByDesc(BusTableColumn::getEntryNum );
        return this.list(lqw);
    }

    @Override
    public List<BusTableColumn> getListByNum(String tableName, Integer entryNum) {
        LambdaQueryWrapper<BusTableColumn> lqw = Wrappers.lambdaQuery();
        lqw.eq(BusTableColumn::getTableName ,tableName);
        lqw.eq(BusTableColumn::getEntryNum,entryNum);
        lqw.orderByDesc(BusTableColumn::getEntryNum );
        return this.list(lqw);
    }

    @Override
    public boolean removeByTableName(String busTableName) {
        LambdaQueryWrapper<BusTableColumn> lqw = Wrappers.lambdaQuery();
        lqw.eq(BusTableColumn::getTableName ,busTableName);
        List<BusTableColumn> columns = this.list(lqw);
        List<Long> idList = columns.stream().map(BusTableColumn::getColumnId).collect(Collectors.toList());
        return this.removeByIds(idList);
    }


}
