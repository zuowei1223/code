package com.tcoiss.dbsource.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import com.tcoiss.dbsource.mapper.DataSourceMapper;
import com.tcoiss.dbsource.domain.DataSource;
import com.tcoiss.dbsource.service.IDataSourceService;

import java.util.List;
import java.util.Map;

/**
 * 数据源配置Service业务层处理
 *
 * @author zw
 * @date 2021-01-19
 */
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements IDataSourceService {

    @Override
    public List<DataSource> queryList(DataSource dataSource) {
        LambdaQueryWrapper<DataSource> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(dataSource.getIpAddress())){
            lqw.eq(DataSource::getIpAddress ,dataSource.getIpAddress());
        }
        if (StringUtils.isNotBlank(dataSource.getDatabaseType())){
            lqw.eq(DataSource::getDatabaseType ,dataSource.getDatabaseType());
        }
        if (dataSource.getCreateDate() != null){
            lqw.eq(DataSource::getCreateDate ,dataSource.getCreateDate());
        }
        if (StringUtils.isNotBlank(dataSource.getDatabaseName())){
            lqw.like(DataSource::getDatabaseName ,dataSource.getDatabaseName());
        }
        return this.list(lqw);
    }
}
