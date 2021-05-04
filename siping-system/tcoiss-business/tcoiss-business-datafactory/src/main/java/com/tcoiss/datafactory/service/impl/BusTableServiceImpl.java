package com.tcoiss.datafactory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.exception.datafactory.DataException;
import com.tcoiss.common.core.utils.ListUtils;
import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.common.core.utils.StringUtils;
import com.tcoiss.common.datasource.annotation.System;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.domain.BusTableColumn;
import com.tcoiss.datafactory.domain.vo.TableVO;
import com.tcoiss.datafactory.mapper.BusTableMapper;
import com.tcoiss.datafactory.service.IBusTableColumnService;
import com.tcoiss.datafactory.service.IBusTableService;
import com.tcoiss.datafactory.util.DataBaseSql;
import com.tcoiss.webservice.api.RemoteApiService;
import com.tcoiss.webservice.api.model.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 业务表Service业务层处理
 *
 * @author zw
 * @date 2021-04-20
 */
@Service
@System
public class BusTableServiceImpl extends ServiceImpl<BusTableMapper, BusTable> implements IBusTableService {

    @Autowired
    private RemoteApiService remoteApiService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private IBusTableColumnService iBusTableColumnService;

    @Override
    public List<BusTable> queryList(BusTable busTable) {
        LambdaQueryWrapper<BusTable> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(busTable.getBusTableName())){
            lqw.like(BusTable::getBusTableName ,busTable.getBusTableName());
        }
        if (StringUtils.isNotBlank(busTable.getBusTableComment())){
            lqw.eq(BusTable::getBusTableComment ,busTable.getBusTableComment());
        }
        if (StringUtils.isNotBlank(busTable.getSyncApiCode())){
            lqw.eq(BusTable::getSyncApiCode ,busTable.getSyncApiCode());
        }
        if (StringUtils.isNotBlank(busTable.getBusGroupName())){
            lqw.like(BusTable::getBusGroupName ,busTable.getBusGroupName());
        }
        if (busTable.getTableStatus()!=null){
            lqw.like(BusTable::getTableStatus ,busTable.getTableStatus());
        }
        return this.list(lqw);
    }

    /**
     * 保存业务表后会形成业务对象后面可以根据业务对象编码来查找api
     * @param busTable
     * @return
     */
    @Override
    public boolean saveTable(BusTable busTable) {
        if(checkBusTable(busTable.getBusTableName())){
            return this.save(busTable);
        }
        return false;
    }

    @Override
    public boolean updateBusTableById(BusTable busTable) {
        if(checkBusTable(busTable.getBusTableName())){
            return this.updateById(busTable);
        }
        return false;
    }

    @Override
    public BusTable getBusTableByName(String tableName) {
        LambdaQueryWrapper<BusTable> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(tableName)){
            lqw.like(BusTable::getBusTableName ,tableName);
        }
        BusTable busTable = this.getOne(lqw);
        List<BusTableColumn> columns = iBusTableColumnService.getColumnsByName(tableName);

        return busTable.setColumns(columns);
    }

    @Override
    public Map<String,Object> getBusTableById(Long tableId) {
        Map<String,Object> map = new HashMap<>();
        //查询表详细配置以及表字段列表信息
        BusTable busTable = this.getById(tableId);
        List<BusTableColumn> columns = new ArrayList<>();
        if(busTable!=null){
            columns = iBusTableColumnService.getColumnsByName(busTable.getBusTableName());
        }
        map.put("rows",columns);
        map.put("info",busTable);
        return map;
    }

    /**
     * 提交业务表
     * @param busTable
     * @return
     */
    @Override
    public boolean updateBusTable(BusTable busTable) {
        if(this.updateById(busTable)){
            List<BusTableColumn> columns = busTable.getColumns();

            return iBusTableColumnService.updateBatchById(busTable.getColumns());
        }
        return false;
    }

    /**
     *
     * @param tableName
     * @return
     */
    private boolean createTable(String tableName,List<BusTableColumn> columns) {
        //先判断是否存在表
        if(DataBaseSql.exitTable(tableName)){
            DataBaseSql.dropTable(tableName);
        }
        //根据业务表结构生成建表sql并执行

        return DataBaseSql.createTable(tableName,columns);
    }

    /**
     *
     * @param tableName
     * @return
     */
    private boolean insetTable(String tableName,List<BusTableColumn> columns) {
        try {
            //从redis中取出数据集合
            List<List<Object>> datas = redisService.getCacheObject(tableName);
            //根据表名和分录号对数据进行分组，同步所有的表数据
            for(List<Object> data: datas){
                DataBaseSql.insert(tableName,columns,data.toArray(new String[data.size()]));
            }
            return true;
        } catch (SQLException e) {
            throw new DataException("500",null,"初始化表数据时报错如下："+e.getMessage());
        }
    }

    //同步表结构
    private List<BusTableColumn> getColumnsByApi(ApiParam apiParam,List<TableVO> vos) {
        //根据api调用获取表结构JSON串
        List<BusTableColumn> columns = new ArrayList<>();
        R<Map<String,Object>> r = remoteApiService.executeKdApi(apiParam);
        if(r.getCode()==200){
            Map<String,Object> map = r.getData();
            if(map.get("success").equals("true")){
                Map<String,Object> data =(Map<String, Object>) map.get("data");
                int count = Integer.valueOf(data.get("count").toString());
                if(count==0){
                    throw new ApiException("405",null,"未查询到可同步数据");
                }
                List<Object> header = (List)data.get("header");
                for(int i= 0;i<vos.size();i++){
                    TableVO vo = vos.get(i);
                    BusTableColumn column = new BusTableColumn();
                    Map<String,Object> headerMap = (Map) header.get(i);
                    column.setDataType(headerMap.get("type").toString());
                    column.setColumnName(vo.getColumnName());
                    column.setTableName(vo.getTableName());
                    column.setKdColumnName(vo.getKdColumnName());
                    column.setColumnType("varchar(60)");
                    column.setColumnComment(vo.getColumnComment());
                    column.setDataModel(vo.getDataModel());
                    columns.add(column);
                }
                if(columns.size()>0){
                    List<List<String>> rows = (List)data.get("rows");
                    //将数据保存到redis，key值为表名
                    redisService.setCacheObject(vos.get(0).getTableName(),rows);
                    return columns;
                }
            }else{
                throw new DataException("405",null,"生成结构时调用API接口异常");
            }
        }else {
            throw new DataException("500",null,"API调用异常");
        }
        return null;
    }


    /**
     * 初始化表数据
     * @param busTable
     * @return
     */
    public boolean initTable(BusTable busTable){
        if(busTable.getTableStatus()==1){
            throw new ApiException("201",new String[]{busTable.getBusTableName()
            },"表已初始化，请勿重复操作");
        }
        /**
         * 1 创建表
         */
        List<BusTableColumn> columns = this.iBusTableColumnService.getColumnsByName(busTable.getBusTableName());
        if(this.createTable(busTable.getBusTableName(),columns)){
            /**
             * 2. 插入数据
             */
            //根据表名和分录号对数据进行分组，同步所有的表数据
            if(this.insetTable(busTable.getBusTableName(),columns)){
                //更新表状态为已初始化
                busTable.setTableStatus(1);
                this.updateById(busTable);
                return redisService.deleteObject(busTable.getBusTableName());
            }else {
                throw new ApiException("500",null,"初始化表数据失败");
            }
        }else{
            throw new ApiException("500",null,"初始化表结构失败");
        }
    }

    public boolean syncDataByApi(BusTable busTable,String script){
        List<String> columnNames = busTable.getColumns().stream().map(column -> column.getKdColumnName()).collect(Collectors.toList());
        ApiParam apiParam = new ApiParam();
        apiParam.setApiCode(busTable.getSyncApiCode());
        String select = StringUtils.join(columnNames.toArray(),",");
        JSONObject jsonObject = JSONObject.parseObject(script);
        jsonObject.put("select",select);
        apiParam.setParam(jsonObject);
        try{
            R<Map<String,Object>> r = remoteApiService.executeKdApi(apiParam);
            if(r.getCode()==200) {
                Map<String, Object> map = r.getData();
                if (map.get("success").equals("true")) {
                    Map<String, Object> data = (Map<String, Object>) map.get("data");
                    int count = Integer.valueOf(data.get("count").toString());
                    if (count == 0) {
                        throw new ApiException("405", null, "未查询到可同步数据");
                    }
                    List<List<String>> rows = (List)data.get("rows");
                    //保存数据
                    for(List<String> row: rows){
                        DataBaseSql.insert(busTable.getBusTableName(),busTable.getColumns(),row.toArray(new String[data.size()]));
                    }
                    return true;
                }else{
                    throw new DataException("405",null,"生成结构时调用API接口异常");
                }
            }else {
                throw new DataException("500",null,"API调用异常");
            }
        }catch (SQLException e){
            throw new DataException("501",new String[] {busTable.getBusTableName()},"保存同步数据失败");

        }

    }



    private boolean checkBusTable(String tableName){
        LambdaQueryWrapper<BusTable> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(tableName)){
            lqw.eq(BusTable::getBusTableName ,tableName);
        }
        if(this.list(lqw).size()>0){
            throw new DataException("405",new String[] {tableName},"保存失败，表名已存在");
        }
        return true;

    }


    /**
     * 导入用户数据
     *
     * @param columns 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @return 结果
     */
    public String importTable(List<TableVO> columns, boolean isUpdateSupport)
    {
        if(isUpdateSupport){
            this.checkBusTable(columns.get(0).getTableName());
        }
        if (StringUtils.isNull(columns) || columns.size() == 0)
        {
            throw new DataException("500",null,"导入表数据不能为空！");
        }
        //将获取到的导入数据保存到数据库中,对数据根据表名进行分组
        Map<String, List<TableVO>> tableMap = columns.stream()
                .collect(Collectors.groupingBy(TableVO::getTableName));
        if(tableMap.size()>1){
            throw new DataException("500",null,"仅支持单表导入！");
        }
        StringBuilder successMsg = new StringBuilder();
        BusTable busTable = new BusTable();
        busTable.setBusTableName(columns.get(0).getTableName());
        busTable.setBusGroupName(columns.get(0).getBusGroupName());
        busTable.setSyncApiCode(columns.get(0).getSyncApiCode());
        busTable.setBusTableComment(columns.get(0).getTableComment());
        busTable.setCreateBy(SecurityUtils.getUsername());
        busTable.setCreateTime(new Date());
        //调用api获取数据
        List<String> columnNames = columns.stream().map(column -> column.getKdColumnName()).collect(Collectors.toList());
        ApiParam apiParam = new ApiParam();
        apiParam.setApiCode(busTable.getSyncApiCode());
        String select = StringUtils.join(columnNames.toArray(),",");
        Map<String,Object> param = new HashMap<>();
        param.put("select",select);
        apiParam.setParam(param);
        List<BusTableColumn> columns1 = getColumnsByApi(apiParam,columns);
        if(this.save(busTable)){
            if(iBusTableColumnService.saveBatch(columns1)){
                successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + columns.size() + " 条，数据如下：");
            }else{
                throw new DataException("500",null,"保存表的列数据异常");
            }
        }else{
            throw new DataException("500",null,"保存表数据异常");
        }
        return successMsg.toString();
    }

    @Override
    public boolean removeTablesByIds(List<Long> asList) {
        for(Long id:asList){
            BusTable busTable = this.getById(id);
            if(this.iBusTableColumnService.removeByTableName(busTable.getBusTableName())){
                if(DataBaseSql.exitTable(busTable.getBusTableName())){
                    DataBaseSql.dropTable(busTable.getBusTableName());
                }
            }
        }
        return this.removeByIds(asList);
    }


}
