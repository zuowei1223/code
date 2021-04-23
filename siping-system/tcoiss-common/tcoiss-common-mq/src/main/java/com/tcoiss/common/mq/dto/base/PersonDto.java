package com.tcoiss.common.mq.dto.base;

import com.tcoiss.common.core.annotation.CopyField;
import com.tcoiss.common.mq.bean.BaseMqDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class PersonDto extends BaseMqDTO implements Serializable {

    @CopyField(targetName = "personId")
    private String id;

    @CopyField(targetName = "personName")
    private String name;

    /** 工号 */
    @CopyField(targetName = "personNo")
    private String number;

    /** 联系电话 */
    @CopyField(targetName = "personId")
    private String phone;

    /** 性别 */
    private String gender;

    private String birthday;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    @CopyField(targetName = "createTime")
    private String createtime;

    /** 修改时间 */
    @CopyField(targetName = "modifyTime")
    private String modifytime;

    /** 使用状态 */
    private String enable;

    /** 审核状态 */
    private String status;

    private String modifier;

    private String idcard;
}
