package com.tcoiss.datafactory.service;

import com.tcoiss.datafactory.domain.BusTable;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcoiss.datafactory.domain.BusTableColumn;
import com.tcoiss.datafactory.domain.vo.TableVO;

import java.util.List;
import java.util.Map;

/**
 * 代码生成业务Service接口
 *
 * @author zw
 * @date 2021-04-20
 */
public interface IBusTableService extends IService<BusTable> {

    /**
     * 查询列表
     */
    List<BusTable> queryList(BusTable busTable);

    boolean saveTable(BusTable busTable);

    boolean updateBusTableById(BusTable busTable);

    BusTable getBusTableByName(String tableName);

    Map<String,Object> getBusTableById(Long tableId);

    boolean updateBusTable(BusTable busTable);

    boolean initTable(BusTable busTable);

    String importTable(List<TableVO> columns, boolean updateSupport);

    boolean removeTablesByIds(List<Long> asList);

    boolean syncDataByApi(BusTable table, String script);
}
