package com.tcoiss.webservice.domain;

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

/**
 * 轨迹服务配置对象 track_service
 * 
 * @author zw
 * @date 2021-02-21
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("track_service")
public class TrackService implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 服务名称 */
    @Excel(name = "服务名称")
    private String fwName;

    /** 平台ID */
    @Excel(name = "平台ID")
    private String gaodeKey;

    /** 轨迹服务ID */
    @Excel(name = "轨迹服务ID")
    private String serviceId;

    /** 轨迹服务ID */
    @Excel(name = "轨迹服务描述")
    private String serviceDesc;

    /** 围栏数量 */
    @Excel(name = "围栏数量")
    private Long fenceNum;

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

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
