package com.tcoiss.job.jobhandler.base;

import com.alibaba.fastjson.JSONObject;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.webservice.api.RemoteApiService;
import com.tcoiss.webservice.api.model.ApiParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class PersonJob {
    @Autowired
    private RemoteApiService remoteApiService;
    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("personJobHandler")
    public ReturnT<String> demoJobHandler(String jobParam) throws Exception {
        XxlJobLogger.log("开始同步【人员数据】");
        //将请求参数解析为Object
        JSONObject jsonObject = JSONObject.parseObject(jobParam);
        ApiParam apiParam = new ApiParam();
        apiParam.setApiCode("syncPerson");
        apiParam.setParam(jsonObject);
        R<Map<String,Object>> result = remoteApiService.executeKdApi(apiParam);
        //保存接口返回的结果集
        if(result==null){
            return ReturnT.FAIL;
        }
        if(result.getCode() == 200){
            Map<String,Object> map =  result.getData();
            boolean flag = (Boolean) map.get("success");
            if(flag){
                String json = map.get("data").toString();
                XxlJobLogger.log("webAPI抽取数据成功！下一步将数据包放入数据工厂");
                //将数据包传入数据工厂

            }

        }
        XxlJobLogger.log("结束同步【人员数据】");
        return ReturnT.SUCCESS;
    }



}
