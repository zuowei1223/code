package com.tcoiss.schedule.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.text.UUID;
import com.tcoiss.common.core.utils.DateUtils;
import com.tcoiss.common.core.utils.StringUtils;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.schedule.admin.core.ApiServer.HttpAPIServer;
import com.tcoiss.schedule.admin.core.ApiServer.HttpResult;
import com.tcoiss.schedule.admin.core.model.ElectronicFence;
/*import com.tcoiss.webservice.api.RemoteFenceService;
import com.tcoiss.webservice.api.domain.ElectronicFence;*/
import com.tcoiss.schedule.admin.core.model.FenceVo;
import com.tcoiss.schedule.admin.dao.ElectronicFenceDao;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/gaode")
public class GaodeController {

    @Resource
    private ElectronicFenceDao electronicFenceDao;
    @Autowired
    private RedisService redisService;
    /*@Autowired
    private RemoteFenceService remoteFenceService;*/

    @Autowired
    private HttpAPIServer httpAPIServer;

    private final static long EXPIRE_TIME = Constants.TOKEN_EXPIRE * 60;

    private static Logger logger = LoggerFactory.getLogger(GaodeController.class);
    @RequestMapping("/index")
    public String index(Model model,String localKey,String name) {
        model.addAttribute("localKey",localKey);
        model.addAttribute("name",name);
        return "gaode/gaode.index";
    }

    @RequestMapping("/queryFence")
    @ResponseBody
    public AjaxResult queryFence(Model model,String name) {
        //根据名称查询数据库中有效的围栏
        List<ElectronicFence> electronicFences = electronicFenceDao.queryFence(name);
        return  AjaxResult.success("查询成功",electronicFences);
    }

    @RequestMapping("/redisSave")
    @ResponseBody
    public AjaxResult redisSave(Model model,@RequestBody List<String> coordinate) {
        //根据日期和UUID号生成围栏编码
        String key = "wl_"+DateUtils.getDate() + UUID.randomUUID();
        redisService.setCacheObject(key,coordinate,EXPIRE_TIME, TimeUnit.SECONDS);
        Map<String,List<String>> result = new HashMap<>();
        result.put(key,coordinate);
        return  AjaxResult.success("缓存成功",key);
    }
    @RequestMapping("/dbSave")
    @ResponseBody
    public AjaxResult dbSave(Model model,@RequestBody List<Map<String,Object>> fenceRedis) {
        //根据key查询缓存数据库
        if(fenceRedis.size()==0){
            return AjaxResult.error(-1,"为检查到新编辑的围栏信息");
        }
        for(Map<String,Object> fence:fenceRedis){
            String name = fence.get("name").toString();
            String localKey = fence.get("localKey").toString();
            List<String> list = redisService.getCacheObject(fence.get("key").toString());
            String points = String.join(";", list);
            //组装围栏数据
            FenceVo vo = new FenceVo();
            vo.setName(name);
            vo.setPoints(points);
            vo.setRepeat("Mon,Tues,Wed,Thur,Fri,Sat,Sun");
            vo.setValid_time("2055-02-01");
            vo.setAlert_condition("enter;leave");
            //Map<String,Object> requestMap = JSON.parseObject(JSON.toJSONString(electronicFence), Map.class);
            //调用平台接口创建围栏
            try {
                HttpResult result = httpAPIServer.doPostJson("https://restapi.amap.com/v4/geofence/meta?key=a33e36c0e15b373a2ebd7fc4aa1ec0c2",JSON.toJSONString(vo));
                if(result.getCode()!=200){
                    return AjaxResult.error(-1,"调用平台接口异常");
                }
                Map<String,Object> resultMap = JSON.parseObject(result.getBody());
                if(Integer.valueOf(resultMap.get("errcode").toString())==0){//创建成功
                    Map<String,Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                    ElectronicFence electronicFence = new ElectronicFence();
                    electronicFence.setFenceName(name);
                    electronicFence.setFencePoints(points);
                    electronicFence.setValidTime("2055-02-01");
                    electronicFence.setFenceRepeat("Mon,Tues,Wed,Thur,Fri,Sat,Sun");
                    electronicFence.setAlertCondition("enter;leave");
                    electronicFence.setFenceGid(dataMap.get("gid").toString());
                    electronicFence.setLocalKey(localKey);
                    electronicFence.setFenceDesc("");
                    electronicFence.setFenceEnable("true");
                    electronicFence.setFenceTime("00:00,23:59;");
                    electronicFenceDao.save(electronicFence);
                    //调用本地接口数据保存数据
                    //remoteFenceService.saveFence(electronicFence);
                }else{
                    return AjaxResult.error("调用平台接口异常："+resultMap.get("errmsg"));
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
                return AjaxResult.error("调用平台接口异常");
            }
        }
        return AjaxResult.success();
    }
    @RequestMapping("/queryInputTips")
    @ResponseBody
    public AjaxResult queryInputTips(Model model,String keywords,String city) {
        try {
            String url = "https://restapi.amap.com/v3/assistant/inputtips?key=a33e36c0e15b373a2ebd7fc4aa1ec0c2";
            url = url +"&keywords="+keywords+"&city="+city+"&datatype=poi";
            String result = httpAPIServer.doGet(url);
            Map<String,Object> resultMap = JSON.parseObject(result);
            if(Integer.valueOf(resultMap.get("status").toString())!=1){
                return AjaxResult.error(-1,"调用平台接口异常");
            }
            String tips = resultMap.get("tips").toString();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return AjaxResult.error("调用平台接口异常");
        }

        return AjaxResult.success();
    }

    @RequestMapping("/queryAddressByLocation")
    @ResponseBody
    public AjaxResult queryAddressByLocation(Model model,String location) {
        try {
            if(StringUtils.isEmpty(location)){
                return AjaxResult.error(-2,"坐标不能为空");
            }
            String url = "https://restapi.amap.com/v3/geocode/regeo?key=a33e36c0e15b373a2ebd7fc4aa1ec0c2";
            url = url +"&location="+location+"&extensions=base&roadlevel=0";
            String result = httpAPIServer.doGet(url);
            Map<String,Object> resultMap = JSON.parseObject(result);
            if(Integer.valueOf(resultMap.get("status").toString())!=1){
                return AjaxResult.error(-1,"调用平台接口异常");
            }

            Map<String,Object> regeocode = (Map<String, Object>) resultMap.get("regeocode");
            String address = regeocode.get("formatted_address").toString();
            return AjaxResult.success("查询成功",address);
        }catch (Exception e){
            logger.info(e.getMessage());
            return AjaxResult.error("调用平台接口异常");
        }
    }
    @RequestMapping("/checkAddress")
    @ResponseBody
    public AjaxResult queryAddress(Model model,String address,String city) {
        try {
            //查询地址的坐标
            String url = "https://restapi.amap.com/v3/geocode/geo?key=a33e36c0e15b373a2ebd7fc4aa1ec0c2";
            url = url +"&address="+address+"&city="+city;
            String result = httpAPIServer.doGet(url);
            Map<String,Object> resultMap = JSON.parseObject(result);
            if(Integer.valueOf(resultMap.get("status").toString())!=1){
                return AjaxResult.error(-1,"调用平台接口异常");
            }

            List<Map<String,Object>> geocodes = (List<Map<String, Object>>) resultMap.get("geocodes");
            String location = geocodes.get(0).get("location").toString();
            //根据返回的坐标查询围栏，系统生成一个设备码  有位数限制大于13位小于16位（13<diu<16）统一生成15位的设备码
            String diu = "g"+DateUtils.dateTimeNow();
            long time =(int) (System.currentTimeMillis()/1000); // 当前时间戳
            url = "https://restapi.amap.com/v4/geofence/status?key=a33e36c0e15b373a2ebd7fc4aa1ec0c2";
            url = url +"&diu="+diu+"&locations="+location+","+time;
            String result2 = httpAPIServer.doGet(url);
            Map<String,Object> resultMap2 = JSON.parseObject(result2);
            if(Integer.valueOf(resultMap2.get("errcode").toString())!=0){
                return AjaxResult.error(-1,"调用平台监控接口异常");
            }
            Map<String,Object> data = (Map<String, Object>) resultMap2.get("data");
            if(Integer.valueOf(data.get("status").toString())!=0){
                return AjaxResult.error(-1,"调用平台监控接口异常"+data.get("message").toString());
            }
            List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("fencing_event_list");
            Map<String,Object> fenceInfo = new HashMap<>();
            if(list.size()>0){//区域内返回围栏信息
                fenceInfo = (Map<String, Object>) list.get(0).get("fence_info");
                fenceInfo.put("is_in",1);
                return AjaxResult.success("在围栏中",fenceInfo);
            }
            fenceInfo.put("is_in",0);
            fenceInfo.put("fence_gid",data.get("nearest_fence_gid"));
            fenceInfo.put("fence_distance",data.get("nearest_fence_distance"));
            return AjaxResult.success("在围栏外",fenceInfo);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return AjaxResult.error("调用平台接口异常");
        }

    }
}
