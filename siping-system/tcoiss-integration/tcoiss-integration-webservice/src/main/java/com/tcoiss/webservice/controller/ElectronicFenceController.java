package com.tcoiss.webservice.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.service.IElectronicFenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * 电子围栏Controller
 * 
 * @author zw
 * @date 2021-01-31
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Controller
@RequestMapping("/fence" )
public class ElectronicFenceController extends BaseController {

    private final IElectronicFenceService iElectronicFenceService;

    /**
     * 查询电子围栏列表
     */
    @PreAuthorize(hasPermi = "webservice:fence:list")
    @GetMapping("/list")
    public TableDataInfo list(ElectronicFence electronicFence) {
        startPage();
        List<ElectronicFence> list = iElectronicFenceService.queryList(electronicFence);
        return getDataTable(list);
    }

    /**
     * 导出电子围栏列表
     */
    @PreAuthorize(hasPermi = "webservice:fence:export" )
    @Log(title = "电子围栏" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,ElectronicFence electronicFence) throws IOException
    {
        List<ElectronicFence> list = iElectronicFenceService.queryList(electronicFence);
        ExcelUtil<ElectronicFence> util = new ExcelUtil<ElectronicFence>(ElectronicFence.class);
        util.exportExcel(response,list, "fence" );
    }

    /**
     * 获取电子围栏详细信息
     */
    @PreAuthorize(hasPermi = "webservice:fence:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return AjaxResult.success(iElectronicFenceService.getById(id));
    }

    /**
     * 新增电子围栏，跳转到地图的编辑页面
     */
    //@PreAuthorize(hasPermi = "webservice:fence:add" )
    @Log(title = "添加电子围栏" , businessType = BusinessType.INSERT)
    @RequestMapping("/gaode/add")
    public String toDraw(Model model,ElectronicFence electronicFence) {
        model.addAttribute("data",electronicFence);
        return "gaode/gaode.index";
    }

    /**
     * 修改电子围栏
     */
    @PreAuthorize(hasPermi = "webservice:fence:edit" )
    @Log(title = "电子围栏" , businessType = BusinessType.UPDATE)
    @RequestMapping("/gaode/edit")
    public String toEdit(Model model,String gid) {

        return "gaode.index";
    }

    /**
     * 删除电子围栏
     */
    @PreAuthorize(hasPermi = "webservice:fence:remove" )
    @Log(title = "电子围栏" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iElectronicFenceService.removeByIds(Arrays.asList(ids)) ? 1 : 0);
    }
}