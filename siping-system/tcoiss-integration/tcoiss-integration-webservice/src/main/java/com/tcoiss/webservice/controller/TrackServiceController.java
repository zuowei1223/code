package com.tcoiss.webservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.core.utils.poi.ExcelUtil;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import javax.servlet.http.HttpServletResponse;
import com.tcoiss.webservice.domain.TrackService;
import com.tcoiss.webservice.service.ITrackServiceService;
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * 轨迹服务配置Controller
 * 
 * @author zw
 * @date 2021-02-21
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/trackservice" )
public class TrackServiceController extends BaseController {

    private final ITrackServiceService iTrackServiceService;

    /**
     * 查询轨迹服务配置列表
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:list")
    @GetMapping("/list")
    public TableDataInfo list(TrackService trackService) {
        startPage();
        List<TrackService> list = iTrackServiceService.queryList(trackService);
        return getDataTable(list);
    }

    /**
     * 查询满足条件的轨迹服务配置集合
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:list")
    @GetMapping("/getService")
    public AjaxResult getService(TrackService trackService) {
        trackService.setFenceNum(1000L);
        trackService.setDataLevel(1);
        List<TrackService> list = iTrackServiceService.queryList(trackService);
        return AjaxResult.success(list);
    }

    /**
     * 导出轨迹服务配置列表
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:export" )
    @Log(title = "轨迹服务配置" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,TrackService trackService) throws IOException
    {
        List<TrackService> list = iTrackServiceService.queryList(trackService);
        ExcelUtil<TrackService> util = new ExcelUtil<TrackService>(TrackService.class);
        util.exportExcel(response,list, "trackservice" );
    }

    /**
     * 获取轨迹服务配置详细信息
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return AjaxResult.success(iTrackServiceService.getById(id));
    }

    /**
     * 新增轨迹服务配置
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:add" )
    @Log(title = "轨迹服务配置" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TrackService trackService) {

        return toAjax(iTrackServiceService.saveTrackService(trackService,"createService") ? 1 : 0);
    }

    /**
     * 修改轨迹服务配置
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:edit" )
    @Log(title = "轨迹服务配置" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TrackService trackService) {
        return toAjax(iTrackServiceService.updateById(trackService) ? 1 : 0);
    }

    /**
     * 删除轨迹服务配置
     */
    @PreAuthorize(hasPermi = "webservice:trackservice:remove" )
    @Log(title = "轨迹服务配置" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iTrackServiceService.removeByIds(Arrays.asList(ids)) ? 1 : 0);
    }
}
