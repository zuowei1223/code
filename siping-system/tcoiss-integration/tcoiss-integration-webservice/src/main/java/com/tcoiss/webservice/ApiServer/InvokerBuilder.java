package com.tcoiss.webservice.ApiServer;

import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.utils.StringUtils;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.service.IApiServiceConfigService;
import com.tcoiss.webservice.utils.TemplateUtils;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvokerBuilder {
    //private static Log logger=Logger.get(InvokerBuilder.class);
    @Autowired
    private  IApiServiceConfigService iApiServiceConfigService;

    public Invoker buildInvoker(String operationCode) {
        ApiServiceConfig operation= null;
        try {
            //查询api配置信息
            operation = iApiServiceConfigService.getOperationByCode(operationCode);
        } catch (Exception e) {
            // e.printStackTrace();
            //logger.error("根据service[" + operationCode + "]创建失败");
            throw e;
        }
        if (operation == null) {
            throw new ApiException("9001", null,"该操作调用[" + operationCode + "]未进行配置,请检查!");
        }
        return buildInvoker(operation);
    }

    private Invoker buildInvoker(ApiServiceConfig operation) {
        /*if(operation.isClosed()){
            return null;
        }*/
        String operationCode = operation.getApiCode();
        String endpoint = operation.getApiUrl();
        String requestTemplate = operation.getParamTemplate();
        //String responseId = operation.getResponseId();

        if(StringUtils.isEmpty(endpoint)) {
            throw new ApiException("",null, "该操作["+operationCode+"]调用请求地址[" + endpoint +
                    "]未进行配置,请检查!");
        }
        //ClientRequest request = commBO.getRequest(requestId);
        if (StringUtils.isEmpty(requestTemplate)) {
            throw new ApiException("", null,"该操作["+operationCode+"]调用请求报文[" + requestTemplate +
                    "]未进行配置,请检查!");
        }
        /*ClientResponse response = commBO.getResponse(responseId);
        if (response == null) {
            throw new CodedException("", "该操作["+operationCode+"]调用反馈报文[" + responseId +
                    "]未进行配置,请检查!");
        }*/
        //ClientGlobalVar gvars = commBO.getGlobalVars(request.getGlobalVarsId());
        Invoker invoker = null;
        String epType = operation.getRequestType();
        if ("SOCKET".equalsIgnoreCase(epType)) {
            //invoker = new SocketInvoker();
        } else if ("http".equalsIgnoreCase(epType)) {
            invoker = new HttpAPIServer();
        } else if ("RMI".equalsIgnoreCase(epType)) {
            //invoker = new RMIInvoker();
        } else if ("FTP".equalsIgnoreCase(epType)) {
            //invoker = new FtpInvoker();
        } else if("SFTP".equalsIgnoreCase(epType)) {
            //invoker = new SftpInvoker();
        } else {
            //invoker = new WsInvoker();
        }
        invoker.endpoint = operation.getApiUrl();
        //invoker.timeout = operation.getTimeout();
        //invoker.transformFault = response.isTransformFault();
        //invoker.logColMap=commBO.getLogColsByOpId(operation.getOperationId());
        invoker.requestTemplate = buildRequestTemplate(requestTemplate);
        //invoker.transformTemplate = buildTransformTemplate(response);

        return invoker;
    }

    /*private Template buildTransformTemplate(ClientResponse response) {
        if (!StringUtils.hasLength(response.getTransform())) {
            return null;
        }
        try {
            Template template = TemplateUtils.createTemplate(response.getTransform());
            return template;
        } catch (Exception e) {
            StringUtils.printInfo("buildTransformTemplate=="+response.getTransform());
            throw new CodedException("9999", "创建响应报文转换模板["+response.getResponseId()+"]出错.", e);
        }
    }*/
    private Template buildRequestTemplate(String requestTemplate) {
        try {
            Template template = TemplateUtils.createTemplate(requestTemplate);
            return template;
        } catch (Exception e) {
            //StringUtils.printInfo("buildRequestTemplate="+requestTemplate);
            throw new ApiException("9999",null, "创建请求报文模板["+requestTemplate+"]出错");
        }
    }
}
