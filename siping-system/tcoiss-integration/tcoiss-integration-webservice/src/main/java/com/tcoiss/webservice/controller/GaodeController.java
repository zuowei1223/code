package com.tcoiss.webservice.controller;

import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.webservice.domain.*;
import com.tcoiss.webservice.service.IFencePointsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/gaode")
public class GaodeController extends BaseController {

    private final IFencePointsService iFencePointsService;

    //@PreAuthorize(hasPermi = "${integration:gaode}:list")
    @GetMapping("/queryFence")
    public AjaxResult queryPsFence(ElectronicFence electronicFence) {
        List<ElectronicFence> list1 = iFencePointsService.queryList(electronicFence);
        return AjaxResult.success(list1);
    }

    /**
     * 根据相应的区域查询相应的区域信息
     */
    //@PreAuthorize(hasPermi = "${integration:gaode}:list")
    @GetMapping("/getDistrictByCity")
    public AjaxResult getDistrictByCity(ElectronicFence fence) {

        return  AjaxResult.success(iFencePointsService.getDistrictInfo(fence,"getDistrictInfo"));
    }

    /**
     * 校验围栏名称是否重复
     */
    //@PreAuthorize(hasPermi = "${integration:gaode}:add" )
    @Log(title = "围栏坐标" , businessType = BusinessType.OTHER)
    @GetMapping("/checkFence")
    public AjaxResult checkFence(ElectronicFence electronicFence) {
        if(iFencePointsService.checkFenceName(electronicFence)){
            return AjaxResult.error("围栏名称不能重复");
        }
        //校验围栏类型，区域围栏时需要获取区域坐标
        if("1".equals(electronicFence.getFenceType())){
            if(StringUtils.isBlank(electronicFence.getAdcode())){
                return AjaxResult.error("请选择围栏区域");
            }
            if(StringUtils.isBlank(electronicFence.getFenceDesc())){
                return AjaxResult.error("创建区域围栏时描述信息不能空");
            }
            ElectronicFence fence = iFencePointsService.districtFence(electronicFence,"getDistrictInfo");
            return AjaxResult.success(fence);
        }
        return AjaxResult.success(electronicFence);
    }

    /**
     * 保存围栏
     */
    //@PreAuthorize(hasPermi = "${integration:gaode}:add" )
    @Log(title = "围栏坐标" , businessType = BusinessType.INSERT)
    @PostMapping("/saveCache")
    public AjaxResult addFence(@RequestBody ElectronicFence fence) {
        //取出缓存坐标并保存
        return toAjax(iFencePointsService.saveOrUpdateFence(fence));

    }

    /**
     * 修改本地围栏信息
     */
    //@PreAuthorize(hasPermi = "${integration:gaode}:edit" )
    @Log(title = "围栏坐标" , businessType = BusinessType.UPDATE)
    @PutMapping()
    public AjaxResult editFence(@RequestBody ElectronicFence fence) {
        return toAjax(iFencePointsService.updateByCode(fence));
    }

    /**
     * 删除电子围栏,
     */
    //@PreAuthorize(hasPermi = "${integration:gaode}:remove" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @PostMapping("/delFence" )
    public AjaxResult remove(@RequestBody Map<String,Object> requestMap) {
        return toAjax(iFencePointsService.removeByCode(requestMap.get("code").toString(),"deleteFence") ? 1 : 0);
    }


}
