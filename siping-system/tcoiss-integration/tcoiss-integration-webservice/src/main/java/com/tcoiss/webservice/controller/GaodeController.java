package com.tcoiss.webservice.controller;

import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.webservice.domain.*;
import com.tcoiss.webservice.service.IFencePointsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/gaode")
public class GaodeController extends BaseController {

    @Autowired
    private RedisService redisService;

    private String fenceAddKey = "FENCE_ADD_TOKEN";

    private final IFencePointsService iFencePointsService;
    private final static long EXPIRE_TIME = Constants.TOKEN_EXPIRE * 60;

    @GetMapping("/getFenceByLocation")
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        return iFencePointsService.getFenceByLocation(addressVo);
    }


    @PreAuthorize(hasPermi = "webservice:gaode:list")
    @GetMapping("/queryFencePoints")
    public AjaxResult queryFencePoints(FencePoints fencePoints) {
        Map<String,List<PointsVo>> map = iFencePointsService.queryList(fencePoints);
        //清空缓存
        redisService.deleteObject(fenceAddKey);
        return AjaxResult.success(map);
    }

    //@PreAuthorize(hasPermi = "webservice:gaode:list")
    @GetMapping("/listCache")
    public AjaxResult listCache() {
        List<PointsVo> list = redisService.getCacheObject(fenceAddKey);
        return AjaxResult.success(list);
    }

    /**
     * 根据围栏id查询围栏坐标数据
     * @param pointsVo
     * @return
     */

    //@PreAuthorize(hasPermi = "webservice:gaode:cache")
    @PostMapping("/fencePointsCache")
    public AjaxResult fencePointsCache(@RequestBody PointsVo pointsVo) {
        //生成分组id并保存到redis
        List<PointsVo> list = redisService.getCacheObject(fenceAddKey);
        if(list!=null&&list.size()>0){
            list.add(pointsVo);
        }else{
            list = new ArrayList<>();
            list.add(pointsVo);
        }
        redisService.setCacheObject(fenceAddKey,list,EXPIRE_TIME, TimeUnit.SECONDS);
        return  AjaxResult.success("缓存成功");
    }

    /**
     * 根据相应的区域查询相应的区域坐标
     */
    @PreAuthorize(hasPermi = "webservice:gaode:list")
    @PostMapping("/districtCache")
    public AjaxResult districtCache( @RequestBody PointsVo pointsVo) {

        List<PointsVo> pointsList = null;
        Map<String,Object> querstMap = new HashMap<>();
        querstMap.put("keywords",pointsVo.getAdcodeName());
        querstMap.put("subdistrict",1);
        querstMap.put("filter",pointsVo.getAdcode());
        querstMap.put("extensions","all");
        pointsList = iFencePointsService.getDistrictOpints(pointsVo,"getDistrictOpints",querstMap);
        //生成分组id并保存到redis
        List<PointsVo> list = redisService.getCacheObject(fenceAddKey);
        if(list!=null&&list.size()>0){
            list.addAll(pointsList);
        }else{
            list = new ArrayList<>();
            list.addAll(pointsList);
        }
        redisService.setCacheObject(fenceAddKey,list,EXPIRE_TIME, TimeUnit.SECONDS);
        return  AjaxResult.success("成功");
    }



    /**
     * 保存围栏坐标,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:add" )
    @Log(title = "围栏坐标" , businessType = BusinessType.DELETE)
    @GetMapping("/addFencePoints")
    public AjaxResult addFencePoints() {
        //取出缓存坐标并保存
        List<PointsVo> list = redisService.getCacheObject(fenceAddKey);
        int num = 0;
        for(int i=0;i<list.size();i++){
            PointsVo pointsVo = list.get(i);
            if(StringUtils.isNotBlank(pointsVo.getAdcode())){
                /*if(iFencePointsService.saveDistrictFence(pointsVo,"saveDistrictFence")==1){
                    redisService.deleteObject(fenceAddKey);
                }*/
                num = iFencePointsService.saveDistrictFence(pointsVo,"saveDistrictFence");
            }else{
                /*if(iFencePointsService.saveFencePoints(pointsVo,"saveFence")==1){
                    redisService.deleteObject(fenceAddKey);
                }*/
                num = iFencePointsService.saveFencePoints(pointsVo,"saveFence");
            }

        }
        return toAjax(num);
    }

    /**
     * 修改围栏坐标
     */
    @PreAuthorize(hasPermi = "webservice:gaode:edit" )
    @Log(title = "围栏坐标" , businessType = BusinessType.UPDATE)
    @PutMapping("/editFencePoints")
    public AjaxResult editFencePoints(@RequestBody PointsVo pointsVo) {
        //取出缓存坐标并保存
        iFencePointsService.updateByVo(pointsVo,"updateFence");
        return AjaxResult.success();
    }

    /**
     * 删除电子围栏,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:remove" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{gid}" )
    public AjaxResult remove(@PathVariable Long gid) {

        return toAjax(iFencePointsService.removeByGid(gid,"deleteFence") ? 1 : 0);
    }

    /**
     * 删除缓存围栏,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:remove" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteCache" )
    public AjaxResult deleteCache() {
        if(!redisService.deleteObject(fenceAddKey)){
            return AjaxResult.success("无可清除的缓存数据");
        }
        return AjaxResult.success("清除成功");
    }


}
