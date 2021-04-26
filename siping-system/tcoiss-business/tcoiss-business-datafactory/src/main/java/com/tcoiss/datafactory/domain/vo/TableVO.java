package com.tcoiss.datafactory.domain.vo;

import com.tcoiss.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class TableVO implements Serializable {
    private static final long serialVersionUID=1L;

    /** 业务表名称 */
    @Excel(name = "表名称")
    private String tableName;

    /** 业务表说明 */
    @Excel(name = "业务表说明")
    private String tableComment;

    /** 同步api编码 */
    @Excel(name = "同步api编码")
    private String syncApiCode;

    @Excel(name = "分组名称")
    private String busGroupName;

    @Excel(name = "列名称" , type = Excel.Type.IMPORT)
    private String columnName;

    /** 列描述 */
    @Excel(name = "列描述")
    private String columnComment;


    @Excel(name = "金碟列名称" , type = Excel.Type.IMPORT)
    private String kdColumnName;



}
