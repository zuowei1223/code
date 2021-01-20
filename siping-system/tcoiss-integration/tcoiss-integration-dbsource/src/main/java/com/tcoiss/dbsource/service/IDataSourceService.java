package com.tcoiss.dbsource.service;

import com.tcoiss.dbsource.domain.DataSource;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 数据源配置Service接口
 *
 * @author zw
 * @date 2021-01-19
 */
public interface IDataSourceService extends IService<DataSource> {

    /**
     * 查询列表
     */
    List<DataSource> queryList(DataSource dataSource);
}
