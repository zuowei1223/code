package com.tcoiss.webservice.domain;

import lombok.Data;

@Data
public class FenceVo {

    /** 名称 */
    //@Excel(name = "名称")
    private String name;

    /** 围栏坐标集 */
    //@Excel(name = "围栏坐标集")
    private String points;

    /** 过期日期 */
    //@Excel(name = "过期日期")
    private String valid_time;

    /** 监控日期 */
    //@Excel(name = "监控日期")
    private String repeat;

    /** 配置触发围栏所需动作 */
    //@Excel(name = "配置触发围栏所需动作")
    private String alert_condition;

}
