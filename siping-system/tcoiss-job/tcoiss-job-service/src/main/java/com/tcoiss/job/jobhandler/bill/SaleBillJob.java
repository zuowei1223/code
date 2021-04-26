package com.tcoiss.job.jobhandler.bill;

import com.alibaba.fastjson.JSONObject;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.webservice.api.model.ApiParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;

import java.util.Map;

public class SaleBillJob {
    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("saleBillJob")
    public ReturnT<String> demoJobHandler(String jobCode) throws Exception {
        XxlJobLogger.log("开始同步【人员数据】");
        //更据任务编码调用数据工厂接口执行方案，执行同步逻辑

        XxlJobLogger.log("结束同步【人员数据】");
        return ReturnT.SUCCESS;
    }
}
