package com.tcoiss.webservice.ApiServer;

import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.utils.bean.BeanUtils;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringWriter;
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
            Map root = new HashMap();
            BeanUtils.copyProperties(root, params);
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
}
