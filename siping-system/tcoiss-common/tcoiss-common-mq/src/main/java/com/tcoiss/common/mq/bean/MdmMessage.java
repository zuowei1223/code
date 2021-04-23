package com.tcoiss.common.mq.bean;

import com.alibaba.fastjson.JSONObject;
import com.tcoiss.common.mq.enums.OperMethodEnum;

import java.io.Serializable;

/**
 * 
 * @ClassName MdmMessage
 * @Description 消息队列承载消息类
 * @author zhf
 * @Date 2017年9月13日 下午4:48:45
 * @version 1.0.0
 * @param <T>
 */
public class MdmMessage extends BaseMqEntity implements Serializable {
    private static final long serialVersionUID = -6902161825878208224L;
    
    private String objId;                  //更新对象主键ID，为保证同一个对象更新顺序一致，需确保同一个对象更新的时候该ID一致
    private OperMethodEnum operMethod;     //操作方法
    private JSONObject jsonObject;              //操作对象Json
    
    public String getObjId() {
        return objId;
    }
    
    public void setObjId(String objId) {
        this.objId = objId;
    }
    
    public OperMethodEnum getOperMethod() {
        return operMethod;
    }
    
    public void setOperMethod(OperMethodEnum operMethod) {
        this.operMethod = operMethod;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
