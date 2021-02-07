package com.tcoiss.webservice.api.domain;

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
    private String localKey;

    /** 平台编号 */
    @Excel(name = "平台编号")
    private String fenceGid;

    /** 名称 */
    @Excel(name = "名称")
    private String fenceName;

    /** 围栏坐标集 */
    @Excel(name = "围栏坐标集")
    private String fencePoints;

    /** 监控状态 */
    @Excel(name = "监控状态")
    private String fenceEnable;

    /** 过期日期 */
    @Excel(name = "过期日期")
    private String validTime;

    /** 监控日期 */
    @Excel(name = "监控日期")
    private String fenceRepeat;

    /** 监控时段 */
    @Excel(name = "监控时段")
    private String fenceTime;

    /** 描述信息 */
    @Excel(name = "描述信息")
    private String fenceDesc;

    /** 配置触发围栏所需动作 */
    @Excel(name = "配置触发围栏所需动作")
    private String alertCondition;

    /** 创建时间 */
    private Date createTime;

    /** 创建人ID */
    @Excel(name = "创建人ID")
    private Long createorId;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
