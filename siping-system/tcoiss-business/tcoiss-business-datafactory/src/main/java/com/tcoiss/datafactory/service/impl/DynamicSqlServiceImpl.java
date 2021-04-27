package com.tcoiss.datafactory.service.impl;

import com.tcoiss.common.datasource.annotation.Master;
import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.domain.BusTableColumn;
import com.tcoiss.datafactory.service.IDynamicSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Master
public class DynamicSqlServiceImpl implements IDynamicSqlService {

    @Autowired
    private JdbcTemplate jdbcTemplate;  //这个是系统自带的

    /**
     * 创建表
     * @param tabName 表名称
     * @param columns 表字段
     */
    public boolean createTable(String tabName, List<BusTableColumn> columns) {
        String id = columns.get(0).getColumnName();
        try {
            String sql = "create table "+tabName+"("+id+" bigint primary key not null";
            if(columns!=null&&columns.size()>0){
                sql+=",";

                for(int i=1;i<columns.size();i++){
                    BusTableColumn column = columns.get(i);
                    //添加字段
                    sql+=column.getColumnName().trim()+" "+column.getColumnType();
                    //防止最后一个,
                    if(i<columns.size()-1){
                        sql+=",";
                    }
                }
            }
            //拼凑完 建表语句 设置默认字符集
            sql+=")DEFAULT CHARSET=utf8;";
            System.out.println("建表语句是："+sql);
            jdbcTemplate.execute(sql);
            return true;
        } catch (Exception e) {
            System.out.println("建表失败" + e.getMessage());
            return false;
        }
    }

    /**
     * 添加数据
     * @param tabName 表名
     * @param tabName 参数字段
     * @param columns 参数字段数据
     */
    public void insert(String tabName,List<BusTableColumn> columns,String[] data) {
        try {
            String sql = "insert into "+tabName+"(";
            for(int i=0;i<columns.size();i++){
                BusTableColumn column = columns.get(i);
                sql+=column.getColumnName();
                //防止最后一个,
                if(i<columns.size()-1){
                    sql+=",";
                }
            }
            sql+=") values(";
            for(int i=0;i<columns.size();i++){
                sql+="?";
                //防止最后一个,
                if(i<columns.size()-1){
                    sql+=",";
                }
            }
            sql+=");";
            System.out.println("添加数据的sql:"+sql);
            //执行
            jdbcTemplate.update(sql,data);

            /*//关闭流
            ps.close();
            conn.close();  //关闭数据库连接*/
        } catch (Exception e) {
            System.out.println("添加数据失败" + e.getMessage());
        }
    }

    /**
     * 批量添加数据
     * @param tabName 表名
     * @param tabName 参数字段
     * @param columns 参数字段数据
     */
    public void batchInsert(String tabName,List<BusTableColumn> columns,List<Object[]> list)  {
        try {
            String sql = "insert into "+tabName+"(";
            for(int i=0;i<columns.size();i++){
                BusTableColumn column = columns.get(i);
                sql+=column.getColumnName();
                //防止最后一个,
                if(i<columns.size()-1){
                    sql+=",";
                }
            }
            sql+=") values(";
            for(int i=0;i<columns.size();i++){
                sql+="?";
                //防止最后一个,
                if(i<columns.size()-1){
                    sql+=",";
                }
            }
            sql+=");";
            System.out.println("添加数据的sql:"+sql);
            //执行
            jdbcTemplate.batchUpdate(sql,list);
        } catch (Exception e) {
            System.out.println("添加数据失败" + e.getMessage());
        }
    }


    /**
     * 查询表 【查询结果的顺序要和数据库字段的顺序一致】
     * @param tabName 表名
     * @param fields 参数字段
     * @param data 参数字段数据
     * @param tab_fields 数据库的字段
     */
    public List<Map<String,Object>> query(String tabName,String[] fields,String[] data,String[] tab_fields){
        //String[] result = null;
        try {
            String sql = "select * from "+tabName+" where ";
            int length = fields.length;
            for(int i=0;i<length;i++){
                sql+=fields[i]+" = ? ";
                //防止最后一个,
                if(i<length-1){
                    sql+=" and ";
                }
            }
            sql+=";";
            System.out.println("查询sql:"+sql);
            //查询结果集
            List<Map<String,Object>> results = jdbcTemplate.queryForList(sql,data);
            //存放结果集
            return results;
        } catch (Exception e) {
            System.out.println("查询失败" + e.getMessage());
        }
        return null;
    }
    /**
     * 获取某张表总数
     * @param tabName
     * @return
     */
    public Integer getCount(String tabName){
        int count = 0;
        try {
            String sql = "select * from "+tabName+" ;";
            return jdbcTemplate.queryForRowSet(sql).getRow();
        } catch (Exception e) {
            System.out.println("获取总数失败" + e.getMessage());
        }
        return count;
    }
    /**
     * 后台分页显示
     * @param tabName
     * @param pageNo
     * @param pageSize
     * @param tab_fields
     * @return
     */
    /*public List<String[]> queryForPage(String tabName,int pageNo,int pageSize ,String[] tab_fields){
        List<String[]> list = new ArrayList<String[]>();
        try {
            String sql = "select * from "+tabName+" LIMIT ?,? ; ";
            System.out.println("查询sql:"+sql);
            //预处理SQL 防止注入
            ps = conn.prepareStatement(sql);
            //注入参数
            ps.setInt(1,pageNo);
            ps.setInt(2,pageSize);
            //查询结果集
            ResultSet rs = ps.executeQuery();
            //存放结果集
            while(rs.next()){
                String[] result = new String[tab_fields.length];
                for (int i = 0; i < tab_fields.length; i++) {
                    result[i] = rs.getString(tab_fields[i]);
                }
                list.add(result);
            }
            //关闭流
            rs.close();
            ps.close();
            conn.close();  //关闭数据库连接
        } catch (SQLException e) {
            System.out.println("查询失败" + e.getMessage());
        }
        return list;
    }*/
    /**
     * 清空表数据
     * @param tabName 表名称
     */
    public void delete(String tabName){
        try {
            String sql = "delete from "+tabName+";";
            System.out.println("删除数据的sql:"+sql);
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.out.println("删除数据失败" + e.getMessage());
        }
    }


    /**
     * 判断表是否存在
     * @param tabName
     * @return
     */
    public boolean exitTable(String tabName){
        boolean flag = false;
        try {
            String sql = "select id from "+tabName+";";
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.out.println("删除数据失败" + e.getMessage());
        }
        return flag;
    }
    /**
     * 删除数据表
     * 如果执行成功则返回false
     * @param tabName
     * @return
     */
    public boolean dropTable(String tabName){
        boolean flag = true;
        try {
            String sql = "drop table "+tabName+";";
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            System.out.println("删除数据失败" + e.getMessage());
        }
        return flag;
    }

    /**
     * 执行存储过程
     * 如果执行成功则返回false
     * @param procedure
     * @return
     */
    @Override
    public Map<String,Object> callProcedure(BusTable table,String procedure) {
        Map<String,Object> result = (Map<String,Object>) jdbcTemplate.execute(
                new CallableStatementCreator() {
                    public CallableStatement createCallableStatement(java.sql.Connection con) throws SQLException {
                        //String storedProc = "{call procedure(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                        CallableStatement cs = con.prepareCall(procedure);
                        /*cs.setInt(1,Integer.valueOf(type));// 设置输入参数的值
                        cs.setString(2, mobile);// 设置输入参数的值*/
                        cs.registerOutParameter(3, Types.INTEGER);// 注册输出参数的类型
                        cs.registerOutParameter(4, Types.VARCHAR);
                        cs.registerOutParameter(5, Types.VARCHAR);
                        cs.registerOutParameter(6, Types.VARCHAR);
                        cs.registerOutParameter(7, Types.BIT);
                        cs.registerOutParameter(8, Types.VARCHAR);
                        cs.registerOutParameter(9, Types.VARCHAR);
                        return cs;
                    }
                }, new CallableStatementCallback() {
                    public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                        cs.execute();
                        String csString = cs.getString(3);
                        if (csString.equals("0")) {
                            return null;
                        }else{
                            /*member.setCode(cs.getString(5));
                            member.setUsername(cs.getString(6));
                            String s = cs.getString(7);
                            if (s.equals("0")) {
                                member.setGender(Member.Gender.male);
                            }else{
                                member.setGender(Member.Gender.female);
                            }
                            member.setMobile(cs.getString(8));
                            member.setEmail(cs.getString(9));*/
                            return null;
                        }
                    }
                });
        return result;
    }

}
