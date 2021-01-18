package com.tcoiss.dbsource.mapper;


import com.tcoiss.dbsource.domain.Datasource;

import java.util.List;

/**
 * datasourceMapper接口
 * 
 * @author zw
 * @date 2021-01-13
 */
public interface DatasourceMapper 
{
    /**
     * 查询datasource
     * 
     * @param datasourceId datasourceID
     * @return datasource
     */
    public Datasource selectDatasourceById(String datasourceId);

    /**
     * 查询datasource列表
     * 
     * @param datasource datasource
     * @return datasource集合
     */
    public List<Datasource> selectDatasourceList(Datasource datasource);

    /**
     * 新增datasource
     * 
     * @param datasource datasource
     * @return 结果
     */
    public int insertDatasource(Datasource datasource);

    /**
     * 修改datasource
     * 
     * @param datasource datasource
     * @return 结果
     */
    public int updateDatasource(Datasource datasource);

    /**
     * 删除datasource
     * 
     * @param datasourceId datasourceID
     * @return 结果
     */
    public int deleteDatasourceById(String datasourceId);

    /**
     * 批量删除datasource
     * 
     * @param datasourceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteDatasourceByIds(String[] datasourceIds);
}
