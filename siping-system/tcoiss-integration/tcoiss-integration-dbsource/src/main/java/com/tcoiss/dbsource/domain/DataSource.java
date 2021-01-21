package com.tcoiss.dbsource.domain;

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
 * 数据源配置对象 data_source
 * 
 * @author zw
 * @date 2021-01-19
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("data_source")
public class DataSource implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 连接ip */
    @Excel(name = "连接ip")
    private String ipAddress;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 密码 */
    @Excel(name = "密码")
    private String passWord;

    /** 连接属性 */
    @Excel(name = "连接属性")
    private String conncetAttr;

    /** 数据库类型 */
    @Excel(name = "数据库类型")
    private String databaseType;

    /** 创建人编号 */
    @Excel(name = "创建人编号")
    private Long creatorId;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String creatorName;

    /** 创建日期 */
    @Excel(name = "创建日期" , width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /** 数据级别 */
    @Excel(name = "数据级别")
    private Integer dataLevel;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long orderNo;

    /** 数据库名称 */
    @Excel(name = "数据库名称")
    private String databaseName;

    /** 连接端口 */
    @Excel(name = "连接端口")
    private Long ipPort;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
