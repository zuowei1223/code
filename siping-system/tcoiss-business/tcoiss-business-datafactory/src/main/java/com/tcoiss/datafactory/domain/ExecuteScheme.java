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
 * 执行方案对象 execute_scheme
 * 
 * @author zw
 * @date 2021-04-26
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("execute_scheme")
public class ExecuteScheme implements Serializable {

private static final long serialVersionUID=1L;


    /** 方案ID */
    @TableId(value = "scheme_id")
    private Long schemeId;

    /** 方案名称 */
    @Excel(name = "方案名称")
    private String schemeName;

    /** 执行编码 */
    @Excel(name = "执行编码")
    private String executeNumber;

    /** 执行方式 */
    @Excel(name = "执行方式")
    private Integer executeType;

    /** 执行策略 */
    @Excel(name = "执行策略")
    private Integer executeStrategy;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String creatorName;

    /** 创建日期 */
    @Excel(name = "创建日期" , width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 数据状态 */
    @Excel(name = "数据状态")
    private Integer status;

    /** 方案描述 */
    @Excel(name = "方案描述")
    private String schemeExplain;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
