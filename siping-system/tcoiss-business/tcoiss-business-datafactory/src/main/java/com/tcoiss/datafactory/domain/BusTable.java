package com.tcoiss.datafactory.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.tcoiss.common.core.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.*;
import java.math.BigDecimal;

/**
 * 代码生成业务对象 bus_table
 * 
 * @author zw
 * @date 2021-04-20
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("bus_table")
public class BusTable implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long tableId;

    /** 业务表名称 */
    @Excel(name = "业务表名称")
    private String busTableName;

    /** 业务表说明 */
    @Excel(name = "业务表说明")
    private String busTableComment;

    /** 同步api编码 */
    @Excel(name = "同步api编码")
    private String syncApiCode;

    /** 业务分组名称 */
    @Excel(name = "业务分组名称")
    private String busGroupName;

    private String splitTables;

    private String splitApiCode;

    private String splitGlsqlScript;

    private String subTables;

    private String subGlsqlScript;

    private String resultType;

    private String resultTableName;

    private String tableType;

    private String syncTableParam;


    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;
    @TableField(exist = false)
    private List<BusTableColumn> columns = new ArrayList<>();

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
