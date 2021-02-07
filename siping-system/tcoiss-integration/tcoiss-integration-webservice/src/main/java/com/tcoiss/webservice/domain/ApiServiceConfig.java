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
import java.util.Map;

/**
 * API服务配置对象 api_service_config
 * 
 * @author zw
 * @date 2021-01-19
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("api_service_config")
public class ApiServiceConfig implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** API名称 */
    @Excel(name = "API名称")
    private String apiName;

    /** API编码 */
    @Excel(name = "API编码")
    private String apiCode;

    /** 请求方式 */
    @Excel(name = "请求方式")
    private String requestType;

    /** 集成对象 */
    @Excel(name = "集成对象")
    private String apiObj;

    /** 所属应用 */
    @Excel(name = "所属应用")
    private String appName;

    /** API地址 */
    @Excel(name = "API地址")
    private String apiUrl;

    /** 内容格式 */
    @Excel(name = "内容格式")
    private String dataType;

    /** 参数模板 */
    @Excel(name = "参数模板")
    private String paramTemplate;

    /** 创建人id */
    @Excel(name = "创建人id")
    private Long createId;

    /** 创建人名称 */
    @Excel(name = "创建人名称")
    private String createName;

    /** 创建日期 */
    private Date createTime;

    /** 数据级别 */
    @Excel(name = "数据级别")
    private Integer dataLevel;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long orderNo;

    /** 测试数据*/
    private String test_param;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();
}
