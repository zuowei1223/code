package com.tcoiss.dbsource.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.tcoiss.common.core.annotation.Excel;
import com.tcoiss.common.core.web.domain.BaseEntity;

/**
 * datasource对象 datasource
 * 
 * @author zw
 * @date 2021-01-13
 */
public class Datasource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 数据源的id */
    @Excel(name = "数据源的id")
    private String datasourceId;

    /** 连接信息 */
    @Excel(name = "连接信息")
    private String url;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 密码 */
    @Excel(name = "密码")
    private String passWord;

    /** 暂留字段 */
    @Excel(name = "暂留字段")
    private String code;

    /** 数据库类型 */
    @Excel(name = "数据库类型")
    private String databasetype;

    public void setDatasourceId(String datasourceId) 
    {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceId() 
    {
        return datasourceId;
    }
    public void setUrl(String url) 
    {
        this.url = url;
    }

    public String getUrl() 
    {
        return url;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setPassWord(String passWord) 
    {
        this.passWord = passWord;
    }

    public String getPassWord() 
    {
        return passWord;
    }
    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }
    public void setDatabasetype(String databasetype) 
    {
        this.databasetype = databasetype;
    }

    public String getDatabasetype() 
    {
        return databasetype;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("datasourceId", getDatasourceId())
            .append("url", getUrl())
            .append("userName", getUserName())
            .append("passWord", getPassWord())
            .append("code", getCode())
            .append("databasetype", getDatabasetype())
            .toString();
    }
}
