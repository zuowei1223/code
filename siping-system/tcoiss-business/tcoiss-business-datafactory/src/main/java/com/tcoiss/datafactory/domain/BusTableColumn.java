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
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

/**
 * 代码生成业务字段对象 bus_table_column
 * 
 * @author zw
 * @date 2021-04-20
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("bus_table_column")
public class BusTableColumn implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long columnId;

    /** 归属表编号 */
    @Excel(name = "归属表编号")
    private String tableName;
    /** 分录号*/
    private Integer entryNum;

    /** 列名称 */
    @Excel(name = "列名称")
    private String columnName;

    private String columnValue;

    /** 列描述 */
    @Excel(name = "列描述")
    private String columnComment;

    /** 列类型 */
    @Excel(name = "列类型")
    private String columnType;

    /** 是否主键（1是） */
    @Excel(name = "是否主键" , readConverterExp = "1=是")
    private String isPk;

    /** 是否必填（1是） */
    @Excel(name = "是否必填" , readConverterExp = "1=是")
    private String isRequired;

    /** 是否列表字段（1是） */
    @Excel(name = "是否列表字段" , readConverterExp = "1=是")
    private String isList;

    /** 是否过滤字段（1是） */
    @Excel(name = "是否过滤字段" , readConverterExp = "1=是")
    private String isFilter;

    /** 过滤方式（等于、不等于、大于、小于、范围） */
    @Excel(name = "过滤方式" , readConverterExp = "等=于、不等于、大于、小于、范围")
    private String filterType;

    /** 数据类型（REF,ENUM,String,decimal） */
    @Excel(name = "数据类型" , readConverterExp = "R=EF,ENUM,String,decimal")
    private String dataType;

    /** 数据模型 */
    @Excel(name = "数据模型")
    private String dataModel;

    /** 排序 */
    @Excel(name = "排序")
    private Long sort;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
