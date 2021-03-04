package com.tcoiss.webservice.domain;

import lombok.Data;

@Data
public class FenceVo {

    /** 名称 */
    private String name;

    private String gfid;

    /** 围栏坐标集 */
    private String points;

    /** 服务ID */
    private String sid;

    /** 平台key */
    private String key;

    /** 围栏描述 */
    private String desc;

}
