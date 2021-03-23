package com.tcoiss.webservice.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("fence_points")
public class FencePoints implements Serializable {
    private static final long serialVersionUID=1L;
    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String pointName;

    private String pointX;

    private String pointY;

    private String fenceName;

    private String fenceCode;

    public String getPoints(){
        return pointX +","+pointY;
    }
}
