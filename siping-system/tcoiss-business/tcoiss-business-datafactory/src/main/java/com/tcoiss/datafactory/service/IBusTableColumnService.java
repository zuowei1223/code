package com.tcoiss.datafactory.service;

import com.tcoiss.datafactory.domain.BusTableColumn;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 代码生成业务字段Service接口
 *
 * @author zw
 * @date 2021-04-20
 */
public interface IBusTableColumnService extends IService<BusTableColumn> {

    /**
     * 查询列表
     */
    List<BusTableColumn> queryList(BusTableColumn busTableColumn);

    /**
     * 根据表名查询字段列表
     */
    List<BusTableColumn> getColumnsByName(String tableName);

    List<BusTableColumn> getListByTableName(String tableName);

    List<BusTableColumn> getEntryNumByTableName(String tableName);

    List<BusTableColumn> getListByNum(String tableName,Integer entryNum);

    boolean removeByTableName(String busTableName);
}
