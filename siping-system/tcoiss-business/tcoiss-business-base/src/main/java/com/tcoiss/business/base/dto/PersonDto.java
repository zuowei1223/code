package com.tcoiss.business.base.dto;

import com.tcoiss.common.core.annotation.CopyField;

import java.io.Serializable;


public class PersonDto  implements Serializable {
    private static final long serialVersionUID=1L;

    @CopyField(targetName = "personNumber")
    private String id;

    @CopyField(targetName = "personName")
    private String name;

    /** 工号 */
    @CopyField(targetName = "personNo")
    private String number;

    /** 联系电话 */
    @CopyField(targetName = "personPhone")
    private String phone;

    /** 性别 */
    private String gender;

    private String birthday;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    private String createtime;

    /** 修改时间 */
    @CopyField(targetName = "kdModifyTime")
    private String modifytime;

    /** 使用状态 */
    private String enable;

    /** 审核状态 */
    private String status;

    private String modifier;

    private String idcard;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }
}
