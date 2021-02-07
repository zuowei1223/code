package com.tcoiss.webservice.controller;

import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.text.UUID;
import com.tcoiss.common.core.utils.DateUtils;
import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FenceVo;
import com.tcoiss.webservice.service.IElectronicFenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/fence/gaode")
public class GaodeController extends BaseController {

    @Autowired
    private RedisService redisService;
    private final IElectronicFenceService iElectronicFenceService;
    private final static long EXPIRE_TIME = Constants.TOKEN_EXPIRE * 60;


    @PreAuthorize(hasPermi = "webservice:gaode:list")
    @GetMapping("/queryFence")
    public AjaxResult queryFence(ElectronicFence electronicFence) {
        electronicFence.setFenceName(electronicFence.getFenceName());
        List<ElectronicFence> list = iElectronicFenceService.queryList(electronicFence);
        return AjaxResult.success(list);
    }
    @PreAuthorize(hasPermi = "webservice:gaode:fenceCache")
    @PostMapping("/fenceCache")
    public AjaxResult fenceCache(ElectronicFence electronicFence) {
        //根据日期和UUID号生成缓存key
        String key = "wl_"+DateUtils.getDate() + UUID.randomUUID();
        electronicFence.setCreateorId(SecurityUtils.getUserId());
        electronicFence.setCreateor(SecurityUtils.getUsername());
        electronicFence.setFenceRepeat("Mon,Tues,Wed,Thur,Fri,Sat,Sun");
        electronicFence.setValidTime("2055-02-01");
        electronicFence.setAlertCondition("enter;leave");
        electronicFence.setFenceEnable("true");
        electronicFence.setFenceTime("00:00,23:59;");
        redisService.setCacheObject(key,electronicFence,EXPIRE_TIME, TimeUnit.SECONDS);
        return  AjaxResult.success(key);
    }
    /**
     * 保存电子围栏,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:saveFence" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @PostMapping
    public AjaxResult saveFence(@RequestBody List<String> cacheKeys) {
        //根据key,查询缓存数据库,再保存到本地，保存成功则清除缓存
        for(String key: cacheKeys){
            ElectronicFence fence = redisService.getCacheObject(key);
            if(iElectronicFenceService.saveFence(fence,"saveFence")==1){
                redisService.deleteObject(key);
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
    @PutMapping
    public AjaxResult editFence(@RequestBody ElectronicFence electronicFence) {
        return toAjax(iElectronicFenceService.updateById(electronicFence) ? 1 : 0);
    }

    /**
     * 删除电子围栏,
     */
    @PreAuthorize(hasPermi = "webservice:gaode:removeFence" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iElectronicFenceService.removeByIds(Arrays.asList(ids)) ? 1 : 0);
    }



}
