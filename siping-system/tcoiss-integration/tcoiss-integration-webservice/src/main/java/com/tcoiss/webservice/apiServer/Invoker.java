package com.tcoiss.webservice.apiServer;

import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.utils.StringUtils;
import com.tcoiss.common.core.utils.bean.BeanUtils;
import freemarker.template.Template;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class Invoker {
    Template requestTemplate;
    String endpoint;
    Integer timeout;
    String response;

    public String getEndpoint() {
        return endpoint;
    }
    protected String generateRequestString(InvokeContext context) {
        Object params = context.getParameters();
        StringWriter out = new StringWriter();
        try {
            Map root ;
            if(params instanceof Map){
                root =(Map) params;
            }else {
                root = objectToMap(params);
            }
            //BeanUtils.copyProperties(params, root);
            //TemplateUtils.addUtils(root);
            requestTemplate.process(root, out);
            String reqString = out.toString();
            context.setRequestString(reqString);
        } catch (Exception e) {
            throw new ApiException("9001", null,"根据模板["
                    + context.getOperationCode() + "]组装请求报文出错.");
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return out.toString();
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = StringUtils.nvl(field.get(obj),"");
            map.put(fieldName, value);
        }
        return map;
    }
}
