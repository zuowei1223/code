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
 * 作业对象 execute_work
 * 
 * @author zw
 * @date 2021-04-26
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("execute_work")
public class ExecuteWork implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(value = "work_id")
    private Long workId;

    /** 所属方案 */
    @Excel(name = "所属方案")
    private Long schemeId;

    /** 作业名称 */
    @Excel(name = "作业名称")
    private String workName;

    /** 业务表 */
    @Excel(name = "业务表")
    private String tableName;

    /** 作业说明 */
    @Excel(name = "作业说明")
    private String workExplain;

    /** 作业类型 */
    @Excel(name = "作业类型")
    private Integer workType;

    private Integer workOrder;

    /** 作业脚本 */
    private String workScript;

    /** 数据状态 */
    @Excel(name = "数据状态")
    private Integer status;

    /** 创建人姓名 */
    @Excel(name = "创建人姓名")
    private String creatorName;

    /** 创建日期 */
    @Excel(name = "创建日期" , width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
