package com.tcoiss.datafactory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.domain.BusTableColumn;
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
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码生成业务Service业务层处理
 *
 * @author zw
 * @date 2021-04-20
 */
@Service
public class BusTableServiceImpl extends ServiceImpl<BusTableMapper, BusTable> implements IBusTableService {

    @Autowired
    private RemoteApiService remoteApiService;

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
        return this.list(lqw);
    }

    /**
     * 保存业务表后会形成业务对象后面可以根据业务对象编码来查找api
     * @param busTable
     * @return
     */
    @Override
    public boolean saveTable(BusTable busTable) {
        if(checkBusTable(busTable)){

        }

        return false;
    }

    @Override
    public boolean updateBusTableById(BusTable busTable) {
        if(checkBusTable(busTable)){
            return this.updateById(busTable);
        }
        return false;
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
     * 修改业务表配置
     * @param busTable
     * @return
     */
    @Override
    public boolean updateBusTable(BusTable busTable) {
         if(this.updateById(busTable)){
             return iBusTableColumnService.updateBatchById(busTable.getColumns());
         }
         return false;
    }

    /**
     *
     * @param busTable
     * @return
     */
    @Override
    public boolean createTable(BusTable busTable) {
        //根据业务表结构生成建表sql并执行
        Map<String,Object> busTableMap = this.getBusTableById(busTable.getTableId());
        List<BusTableColumn> columns = (List<BusTableColumn>) busTableMap.get("rows");
        String id = "";
        for(BusTableColumn column :columns){
            if(column.getIsPk()=="1"){
                id = column.getColumnName();
            }
            columns.remove(column);
        }
        DataBaseSql.createTable(busTable.getBusTableName(),id,columns);
        //创建拆分表，获取拆分表名称
        String[] splitTables = busTable.getSplitTables().split(",");
        for(int i=0;i<splitTables.length;i++){
            List<BusTableColumn> splitColumns = iBusTableColumnService.getColumnsByName(splitTables[i]);
            DataBaseSql.createTable(splitTables[i],id,splitColumns);
        }

        return true;
    }

    /**
     *
     * @param busTable
     * @return
     */
    private boolean insetTable(List<BusTableColumn> columns) {


        List<String> data = columns.stream().map(column -> column.getColumnValue()).collect(Collectors.toList());
        //根据业务表结构生成建表sql并执行
        Map<String,Object> busTableMap = this.getBusTableById(busTable.getTableId());
        List<BusTableColumn> columns = (List<BusTableColumn>) busTableMap.get("rows");
        String id = "";
        for(BusTableColumn column :columns){
            if(column.getIsPk()=="1"){
                id = column.getColumnName();
            }
            columns.remove(column);
        }
        DataBaseSql.createTable(busTable.getBusTableName(),id,columns);
        //创建拆分表，获取拆分表名称
        String[] splitTables = busTable.getSplitTables().split(",");
        for(int i=0;i<splitTables.length;i++){
            List<BusTableColumn> splitColumns = iBusTableColumnService.getColumnsByName(splitTables[i]);
            DataBaseSql.createTable(splitTables[i],id,splitColumns);
        }

        return true;
    }

    //同步表结构
    private boolean syncTableJg(String tableName,String syncTableParam) {
        //根据api调用获取表结构JSON串
        ApiParam apiParam = new ApiParam();
        apiParam.setApiObj(tableName);
        Object param = JSONObject.parse(syncTableParam);
        apiParam.setParam(param);
        R<Map<String,Object>> r = remoteApiService.executeKdApi(apiParam);
        if(r.getCode()==200){
            Map<String,Object> map = r.getData();
            if((boolean)map.get("success")){
                List<Map<String,Object>> list =(List) map.get("data");
                for(int i=0;i<list.size();i++){
                    Map<String,Object> result = list.get(i);
                    Set<Map.Entry<String, Object>> entrys = result.entrySet();  //此行可省略，直接将map.entrySet()写在for-each循环的条件中
                    List<BusTableColumn> columns = new ArrayList<>();
                    for(Map.Entry<String, Object> entry:entrys){
                        System.out.println("key值："+entry.getKey()+" value值："+entry.getValue());
                        //将key值保存到竖表中,默认字段物理类型varchar(60),默认数据类型为String,默认
                        BusTableColumn column = new BusTableColumn();
                        if(entry.getKey().equals("fid")){
                            column.setColumnName("fid");
                            column.setColumnType("bigint(20)");
                            column.setDataType("Long");
                            column.setIsPk("1");
                            column.setIsRequired("1");
                        }else{
                            column.setColumnName(entry.getKey());
                            column.setColumnType("varchar(60)");
                            column.setDataType("String");
                            column.setIsPk("0");
                        }
                        column.setTableName(tableName);
                        column.setColumnValue(entry.getValue().toString());
                        column.setEntryNum(i);
                        columns.add(column);
                    }
                    iBusTableColumnService.saveBatch(columns);
                }
                return true;

            }else{
                throw new ApiException("405",null,"生成结构时调用API接口异常");
            }
        }
        return false;
    }

    public boolean syncTableAll(BusTable busTable){
        //同步主业务表结构及数据
        String tableName = busTable.getBusTableName();
        if(this.syncTableJg(tableName,busTable.getSyncTableParam())){
            //获取保存数据中id最大的一个id值
            Integer maxId = iBusTableColumnService.getMaxIdByTableName(tableName);
            JSONObject object = new JSONObject();
            object.put("fid",maxId);
            String syncParam = object.toJSONString();
            String[] splitTableNames = busTable.getSplitTables().split(",");
            for(String splitName: splitTableNames){
                syncTableJg(splitName,syncParam);
            }
            //同步完成数据结构后创建表并插入数据
            if(this.createTable(busTable)){

            }

        }
        return false;

    }


    private boolean checkBusTable(BusTable busTable){
        LambdaQueryWrapper<BusTable> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(busTable.getBusTableName())){
            lqw.eq(BusTable::getBusTableName ,busTable.getBusTableName());
        }
        if(this.list(lqw).size()>0){
            throw new ApiException("405",new String[] {busTable.getBusTableName()},"保存失败，表名已存在");
        }
        return true;

    }



}
