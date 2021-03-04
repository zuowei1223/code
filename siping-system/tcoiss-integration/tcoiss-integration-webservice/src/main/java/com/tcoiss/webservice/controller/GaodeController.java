package com.tcoiss.webservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.text.UUID;
import com.tcoiss.common.core.utils.DateUtils;
import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.webservice.domain.AddressVo;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FencePoints;
import com.tcoiss.webservice.domain.PointsVo;
import com.tcoiss.webservice.service.IElectronicFenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/fence/gaode")
public class GaodeController extends BaseController {

    @Autowired
    private RedisService redisService;



    private final IElectronicFenceService iElectronicFenceService;
    private final static long EXPIRE_TIME = Constants.TOKEN_EXPIRE * 60;

    @GetMapping("/getFenceByLocation")
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        return iElectronicFenceService.getFenceByLocation(addressVo);
    }




    @PreAuthorize(hasPermi = "webservice:gaode:list")
    @GetMapping("/queryFence")
    public AjaxResult queryFence(ElectronicFence electronicFence) {
        electronicFence.setFenceName(electronicFence.getFenceName());
        List<ElectronicFence> list = iElectronicFenceService.queryList(electronicFence);

        return AjaxResult.success(list);
    }
    @PreAuthorize(hasPermi = "webservice:gaode:fenceCache")
    @PostMapping("/fenceCache")
    public AjaxResult fenceCache(@RequestBody ElectronicFence electronicFence) {
        //根据日期和UUID号生成缓存key
        String key = "wl_"+DateUtils.getDate() + UUID.randomUUID();
        //生成local_key
        electronicFence.setLocalKey(redisService.generateOrderId());
        electronicFence.setCreateorId(SecurityUtils.getUserId());
        electronicFence.setCreateor(SecurityUtils.getUsername());
        redisService.setCacheObject(key,electronicFence,EXPIRE_TIME, TimeUnit.SECONDS);
        Map<String,String> map = new HashMap<>();
        map.put("cacheKey",key);
        map.put("localKey",electronicFence.getLocalKey());
        map.put("fenceName",electronicFence.getFenceName());
        /*map.put("DistrictOpints",);*/
        return  AjaxResult.success("缓存成功",map);
    }

    /**
     * 根据相应的区域查询相应的区域坐标
     */
    @PreAuthorize(hasPermi = "webservice:gaode:list")
    @PostMapping("/getDistrictOpints")
    public AjaxResult getDistrictOpints( @RequestBody Map<String,Object> map) {
        List<FencePoints> fencePointsList = null;
        if(map.get("serviceId")==null){
            throw new ApiException("9999",new Object[] { map.get("serviceId") },"请先选择轨迹服务ID");
        }
        if(!map.get("dictValue").toString().isEmpty()){
            Map<String,Object> querstMap = new HashMap<>();
            querstMap.put("keywords",map.get("dictLabel"));
            querstMap.put("subdistrict",1);
            querstMap.put("filter",map.get("dictValue"));
            querstMap.put("extensions","all");
            fencePointsList = iElectronicFenceService.getDistrictOpints(map.get("serviceId").toString(),"getDistrictOpints",querstMap);
        }

        return  AjaxResult.success("查询成功",fencePointsList);
    }



    /**
     * 保存电子围栏,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:saveFence" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @PostMapping("/addFence")
    public AjaxResult saveFence(@RequestBody PointsVo pointsVo) {
        //根据key,查询缓存数据库,再保存到本地，保存成功则清除缓存
        String cacheKey = pointsVo.getCacheKey();
        ElectronicFence fence = redisService.getCacheObject(cacheKey);
        List<FencePoints> coordinate = pointsVo.getDrawPoints();
        /*if(coordinate.size()>1){
            List<String> newCoordinate = new ArrayList<>();
            for(List list: coordinate){
                String points = String.join(";", list);
                newCoordinate.add(points);
            }

            fence.setFencePoints(String.join("|", newCoordinate));
        }else{
            fence.setFencePoints(String.join(";", coordinate.get(0)));
        }*/
        if(!fence.getDistrict().isEmpty()){
            if(iElectronicFenceService.saveDistrictFence(fence,coordinate,"saveDistrictFence")==1){
                redisService.deleteObject(cacheKey);
            }
        }else{
            if(iElectronicFenceService.saveFence(fence,coordinate,"saveFence")==1){
                redisService.deleteObject(cacheKey);
            }
        }
        //调用接口数据
        return AjaxResult.success();
    }

    /**
     * 修改电子围栏,根据修改类型分别修改基本信息和围栏信息
     */
    @PreAuthorize(hasPermi = "webservice:gaode:editFence" )
    @Log(title = "电子围栏" , businessType = BusinessType.UPDATE)
    @PostMapping("/editFence")
    public AjaxResult editFence(@RequestBody PointsVo pointsVo) {
        ElectronicFence electronicFence = iElectronicFenceService.getOne(
                new QueryWrapper<ElectronicFence>().eq("local_key",pointsVo.getCacheKey()));
        List<FencePoints> coordinate = pointsVo.getDrawPoints();
        return toAjax(iElectronicFenceService.updateById(electronicFence,coordinate,"updateFence") ? 1 : 0);
    }

    /**
     * 删除电子围栏,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:removeFence" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iElectronicFenceService.removeByIds(Arrays.asList(ids),"deleteFence") ? 1 : 0);
    }



}
