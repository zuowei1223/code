package com.tcoiss.datafactory.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import com.tcoiss.datafactory.domain.BusTableColumn;
import com.tcoiss.datafactory.service.IBusTableColumnService;
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
import com.tcoiss.common.core.web.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 代码生成业务字段Controller
 * 
 * @author zw
 * @date 2021-04-20
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/column" )
public class BusTableColumnController extends BaseController {

    private final IBusTableColumnService iBusTableColumnService;

    /**
     * 查询代码生成业务字段列表
     */
    @PreAuthorize(hasPermi = "gen:column:list")
    @GetMapping("/list")
    public TableDataInfo list(BusTableColumn busTableColumn) {
        startPage();
        List<BusTableColumn> list = iBusTableColumnService.queryList(busTableColumn);
        return getDataTable(list);
    }

    /**
     * 导出代码生成业务字段列表
     */
    @PreAuthorize(hasPermi = "gen:column:export" )
    @Log(title = "代码生成业务字段" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,BusTableColumn busTableColumn) throws IOException
    {
        List<BusTableColumn> list = iBusTableColumnService.queryList(busTableColumn);
        ExcelUtil<BusTableColumn> util = new ExcelUtil<BusTableColumn>(BusTableColumn.class);
        util.exportExcel(response,list, "column" );
    }


    /**
     * 获取代码生成业务字段详细信息
     */
    @PreAuthorize(hasPermi = "gen:column:query" )
    @GetMapping(value = "/{columnId}" )
    public AjaxResult getInfo(@PathVariable("columnId" ) Long columnId) {
        return AjaxResult.success(iBusTableColumnService.getById(columnId));
    }

    /**
     * 新增代码生成业务字段
     */
    @PreAuthorize(hasPermi = "gen:column:add" )
    @Log(title = "代码生成业务字段" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusTableColumn busTableColumn) {
        return toAjax(iBusTableColumnService.save(busTableColumn) ? 1 : 0);
    }

    /**
     * 修改代码生成业务字段
     */
    @PreAuthorize(hasPermi = "gen:column:edit" )
    @Log(title = "代码生成业务字段" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusTableColumn busTableColumn) {
        return toAjax(iBusTableColumnService.updateById(busTableColumn) ? 1 : 0);
    }

    /**
     * 删除代码生成业务字段
     */
    @PreAuthorize(hasPermi = "gen:column:remove" )
    @Log(title = "代码生成业务字段" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{columnIds}" )
    public AjaxResult remove(@PathVariable Long[] columnIds) {
        return toAjax(iBusTableColumnService.removeByIds(Arrays.asList(columnIds)) ? 1 : 0);
    }
}
