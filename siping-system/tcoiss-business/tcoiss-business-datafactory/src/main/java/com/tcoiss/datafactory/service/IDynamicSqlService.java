package com.tcoiss.datafactory.service;

import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.domain.BusTableColumn;

import java.util.List;
import java.util.Map;

public interface IDynamicSqlService {

    boolean createTable(String tabName, List<BusTableColumn> columns);

    void insert(String tabName,List<BusTableColumn> columns,String[] data);

    void batchInsert(String tabName,List<BusTableColumn> columns,List<Object[]> list);

    List<Map<String,Object>> query(String tabName,String[] fields,String[] data,String[] tab_fields);

    void delete(String tabName);

    boolean exitTable(String tabName);

    boolean dropTable(String tabName);

    Map<String,Object> callProcedure(BusTable table, String procedure);
}
