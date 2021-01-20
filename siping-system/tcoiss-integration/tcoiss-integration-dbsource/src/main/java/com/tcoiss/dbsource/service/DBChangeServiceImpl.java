package com.tcoiss.dbsource.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tcoiss.dbsource.config.DBContextHolder;
import com.tcoiss.dbsource.config.DynamicDataSource;
import com.tcoiss.dbsource.domain.DataSource;
import com.tcoiss.dbsource.mapper.DataSourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author : JCccc
 * @CreateTime : 2019/10/22
 * @Description :
 **/
@Service
public class DBChangeServiceImpl implements DBChangeService {

    @Autowired
    DataSourceMapper dataSourceMapper;
    @Autowired
    private DynamicDataSource dynamicDataSource;
    @Override
    public List<DataSource> get() {
        return dataSourceMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public boolean changeDb(String datasourceId) throws Exception {

        //默认切换到主数据源,进行整体资源的查找
        DBContextHolder.clearDataSource();

        List<DataSource> dataSourcesList = dataSourceMapper.selectList(Wrappers.emptyWrapper());

        for (DataSource dataSource : dataSourcesList) {
            if (dataSource.getDatabaseName().equals(datasourceId)) {
                System.out.println("需要使用的的数据源已经找到,datasourceId是：" + dataSource.getDatabaseName());
                //创建数据源连接&检查 若存在则不需重新创建
                dynamicDataSource.createDataSourceWithCheck(dataSource);
                //切换到该数据源
                DBContextHolder.setDataSource(dataSource.getDatabaseName());
                return true;
            }
        }
        return false;

    }

}

