package com.tcoiss.webservice.controller;

import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.common.core.utils.poi.ExcelUtil;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.core.web.page.TableDataInfo;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.service.IElectronicFenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 电子围栏Controller
 *
 * @author zw
 * @date 2021-01-31
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/fence" )
public class ElectronicFenceController extends BaseController {

    private final IElectronicFenceService iElectronicFenceService;

    /**
     * 查询电子围栏列表
     */
    //@PreAuthorize(hasPermi="${integration:fence}:list")
    @GetMapping("/list")
    public TableDataInfo list(ElectronicFence electronicFence) {
        startPage();
        List<ElectronicFence> list = iElectronicFenceService.queryList(electronicFence);
        return getDataTable(list);
    }

    /**
     * 导出电子围栏列表
     */
    //@PreAuthorize(hasPermi = "${integration:fence}:export" )
    @Log(title = "电子围栏" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ElectronicFence electronicFence) throws IOException
    {
        List<ElectronicFence> list = iElectronicFenceService.queryList(electronicFence);
        ExcelUtil<ElectronicFence> util = new ExcelUtil<ElectronicFence>(ElectronicFence.class);
        util.exportExcel(response,list, "fence" );
    }

    /**
     * 获取电子围栏详细信息
     */
    //@PreAuthorize(hasPermi = "${integration:fence}:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return AjaxResult.success(iElectronicFenceService.getById(id));
    }


    /**
     * 新增围栏
     */
    //@PreAuthorize(hasPermi="${integration:fence}:add" )
    @Log(title = "地理围栏" , businessType = BusinessType.INSERT)
    @PostMapping("/addFence")
    public AjaxResult addFence(@RequestBody ElectronicFence electronicFence) {
        //生成围栏编码
        String code = "gd_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        //redisService.setIncr(code,0);
        //校验是否存在相同的围栏名称
        if(iElectronicFenceService.checkFenceName(electronicFence)){
            return AjaxResult.error("围栏名称不能重复");
        }
        electronicFence.setFenceCode(code);
        electronicFence.setCreateor(SecurityUtils.getUsername());
        electronicFence.setCreateorId(SecurityUtils.getUserId());
        return toAjax(iElectronicFenceService.syncFence(electronicFence,1) ? 1 : 0);
    }



    /**
     * 修改电子围栏,根据修改类型分别修改基本信息和围栏信息
     */
    //@PreAuthorize(hasPermi = "${integration:fence}:edit" )
    @Log(title = "电子围栏" , businessType = BusinessType.UPDATE)
    @PutMapping("/editFence")
    public AjaxResult edit(@RequestBody ElectronicFence electronicFence) {
        //校验是否存在相同的围栏名称
        if(iElectronicFenceService.checkFenceName(electronicFence)){
            return AjaxResult.error("围栏名称不能重复");
        }
        return toAjax(iElectronicFenceService.syncFence(electronicFence,1) ? 1 : 0);
    }

    /**
     * 删除电子围栏,
     */
    //@PreAuthorize(hasPermi = "${integration:fence}:remove" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        int num = 0;
        for(Long id:ids){
            ElectronicFence electronicFence = iElectronicFenceService.getById(id);
            num = iElectronicFenceService.syncFence(electronicFence,0)? 1 : 0;
        }
        return toAjax(num);
    }
}
