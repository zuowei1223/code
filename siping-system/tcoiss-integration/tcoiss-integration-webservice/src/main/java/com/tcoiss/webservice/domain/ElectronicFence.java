package com.tcoiss.webservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tcoiss.common.core.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电子围栏对象 electronic_fence
 * 
 * @author zw
 * @date 2021-01-31
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("electronic_fence")
public class ElectronicFence implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 围栏本地编码 */
    @Excel(name = "围栏本地编码")
    private String fenceCode;

    /** 名称 */
    @Excel(name = "名称")
    private String fenceName;

    /** 监控状态 */
    @Excel(name = "监控状态")
    private String serviceId;

    /** 围栏属性 */
    @Excel(name = "围栏属性")
    private String fencePop;

    /** 所属城市 */
    @Excel(name = "所属城市")
    private String cityCode;

    /** 描述信息 */
    @Excel(name = "描述信息")
    private String fenceDesc;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createorId;

    /** 创建人 */
    @Excel(name = "创建人")
    private String createor;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

}
