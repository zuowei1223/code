package com.tcoiss.webservice.controller;


import com.tcoiss.common.core.domain.R;
import com.tcoiss.common.core.utils.StringUtils;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.api.model.ApiParam;
import com.tcoiss.webservice.domain.AddressVo;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IApiServiceConfigService;
import com.tcoiss.webservice.service.IFencePointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/apiService" )
public class ApiServiceController {
    @Autowired
    private IFencePointsService iFencePointsService;
    @Autowired
    private IApiService iApiService;
    @Autowired
    private IApiServiceConfigService iApiServiceConfigService;
    @GetMapping("fence/getFenceByLocation")
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        return iFencePointsService.getFenceByLocation(addressVo);
    }
    @PostMapping("/executeApi")
    public Map<String, Object> executeApi(@RequestBody ApiParam apiParam){

        return iApiService.executeByApiCode(apiParam.getApiCode(),apiParam.getParam());
    }

    @PostMapping("/executeKdApi")
    public R<Map<String, Object>> executeKdApi(@RequestBody ApiParam apiParam){
        /*if(!StringUtils.isNotEmpty(apiParam.getApiCode())){
            return R.fail("webApi编码不能为空");
        }*/
        ApiServiceConfig config = new ApiServiceConfig();
        config.setApiObj(apiParam.getApiObj());
        ApiServiceConfig config1 = iApiServiceConfigService.queryList(config).get(0);
        if(config1==null){
            return R.fail("未查询到对应的API配置");
        }
        Map<String,Object> result = iApiService.executeKdByApiCode(config1.getApiCode(),apiParam.getParam());
        if(result==null){
            return R.fail("webApi接口请求失败");
        }
        return R.ok(result);
    }

}
