package com.tcoiss.dbsource.service;

import com.tcoiss.dbsource.domain.Datasource;
import com.tcoiss.dbsource.mapper.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * datasourceService业务层处理
 * 
 * @author zw
 * @date 2021-01-13
 */
@Service
public class DatasourceServiceImpl implements IDatasourceService 
{
    @Autowired
    private DatasourceMapper datasourceMapper;

    /**
     * 查询datasource
     * 
     * @param datasourceId datasourceID
     * @return datasource
     */
    @Override
    public Datasource selectDatasourceById(String datasourceId)
    {
        return datasourceMapper.selectDatasourceById(datasourceId);
    }

    /**
     * 查询datasource列表
     * 
     * @param datasource datasource
     * @return datasource
     */
    @Override
    public List<Datasource> selectDatasourceList(Datasource datasource)
    {
        return datasourceMapper.selectDatasourceList(datasource);
    }

    /**
     * 新增datasource
     * 
     * @param datasource datasource
     * @return 结果
     */
    @Override
    public int insertDatasource(Datasource datasource)
    {
        return datasourceMapper.insertDatasource(datasource);
    }

    /**
     * 修改datasource
     * 
     * @param datasource datasource
     * @return 结果
     */
    @Override
    public int updateDatasource(Datasource datasource)
    {
        return datasourceMapper.updateDatasource(datasource);
    }

    /**
     * 批量删除datasource
     * 
     * @param datasourceIds 需要删除的datasourceID
     * @return 结果
     */
    @Override
    public int deleteDatasourceByIds(String[] datasourceIds)
    {
        return datasourceMapper.deleteDatasourceByIds(datasourceIds);
    }

    /**
     * 删除datasource信息
     * 
     * @param datasourceId datasourceID
     * @return 结果
     */
    @Override
    public int deleteDatasourceById(String datasourceId)
    {
        return datasourceMapper.deleteDatasourceById(datasourceId);
    }
}
