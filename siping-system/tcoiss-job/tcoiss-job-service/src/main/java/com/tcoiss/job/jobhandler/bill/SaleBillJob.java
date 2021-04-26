package com.tcoiss.job.jobhandler.bill;

import com.alibaba.fastjson.JSONObject;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.datafactory.api.RemoteDataFactoryService;
import com.tcoiss.datafactory.api.model.SchemeVO;
import com.tcoiss.webservice.api.model.ApiParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class SaleBillJob {

    @Autowired
    private RemoteDataFactoryService remoteDataFactoryService;
    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("saleBillJob")
    public ReturnT<String> demoJobHandler(String jobCode) throws Exception {
        XxlJobLogger.log("开始执行方案");
        //更据任务编码调用数据工厂接口执行方案，执行同步逻辑
        SchemeVO vo = new SchemeVO();
        vo.setExecuteNumber(jobCode);
        vo.setExecuteType(1);
        R<Map<String,Object>> result = remoteDataFactoryService.executeScheme(vo);
        if(result==null){
            return ReturnT.FAIL;
        }
        if(result.getCode() == 200){
            Map<String,Object> map =  result.getData();

        }
        XxlJobLogger.log("结束执行方案");
        return ReturnT.SUCCESS;
    }
}
