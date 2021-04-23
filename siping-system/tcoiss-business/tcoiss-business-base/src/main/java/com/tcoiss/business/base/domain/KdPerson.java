package com.tcoiss.business.base.domain;

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
 * 人员对象 kd_person
 * 
 * @author zw
 * @date 2021-04-07
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName("kd_person")
public class KdPerson implements Serializable {

private static final long serialVersionUID=1L;


    /** 编号 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 苍穹主键ID*/
    private String personNumber;

    /** 人员名称 */
    @Excel(name = "人员名称")
    private String personName;

    /** 工号 */
    @Excel(name = "工号")
    private String personNo;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String personPhone;

    /** 性别 */
    @Excel(name = "性别")
    private String gender;

    /** 创建人 */
    @Excel(name = "创建人")
    private String creator;

    /** 创建时间 */
    @Excel(name = "创建时间" , width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 修改时间 */
    @Excel(name = "修改时间" , width = 30, dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;


    private String kdModifyTime;

    /** 使用状态 */
    @Excel(name = "使用状态")
    private String enable;

    /** 审核状态 */
    @Excel(name = "审核状态")
    private String status;

    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonNo() {
        return personNo;
    }

    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getKdModifyTime() {
        return kdModifyTime;
    }

    public void setKdModifyTime(String kdModifyTime) {
        this.kdModifyTime = kdModifyTime;
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

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
