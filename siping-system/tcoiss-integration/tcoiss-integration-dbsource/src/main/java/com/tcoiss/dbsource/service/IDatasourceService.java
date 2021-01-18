package com.tcoiss.dbsource.service;

import java.util.List;
import com.tcoiss.dbsource.domain.Datasource;

/**
 * datasourceService接口
 * 
 * @author zw
 * @date 2021-01-13
 */
public interface IDatasourceService 
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
     * 批量删除datasource
     * 
     * @param datasourceIds 需要删除的datasourceID
     * @return 结果
     */
    public int deleteDatasourceByIds(String[] datasourceIds);

    /**
     * 删除datasource信息
     * 
     * @param datasourceId datasourceID
     * @return 结果
     */
    public int deleteDatasourceById(String datasourceId);
}
