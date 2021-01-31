package com.tcoiss.webservice.controller;

import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.text.UUID;
import com.tcoiss.common.core.utils.DateUtils;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/gaode")
public class GaodeController {

    @Autowired
    private RedisService redisService;
    private final static long EXPIRE_TIME = Constants.TOKEN_EXPIRE * 60;

    @RequestMapping("/index")
    public String index(Model model) {

        return "gaode/gaode.index";
    }
    @RequestMapping("/redisSave")
    @ResponseBody
    public AjaxResult redisSave(Model model, List<String> coordinate) {
        //根据日期和UUID号生成围栏编码
        String key = "wl_"+DateUtils.getDate() + UUID.randomUUID();
        redisService.setCacheObject(key,coordinate,EXPIRE_TIME, TimeUnit.SECONDS);
        return  AjaxResult.success(key);
    }
    @RequestMapping("/dbSave")
    @ResponseBody
    public AjaxResult dbSave(Model model,String key) {
        //根据key查询缓存数据库
        List<String> list = redisService.getCacheObject(key);
        //调用接口数据

        return AjaxResult.success();
    }
}
