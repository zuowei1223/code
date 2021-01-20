package com.tcoiss.dbsource.controller;

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
import com.tcoiss.dbsource.domain.DataSource;
import com.tcoiss.dbsource.service.IDataSourceService;
import com.tcoiss.common.core.web.page.TableDataInfo;

import javax.servlet.http.HttpServletResponse;

/**
 * 数据源配置Controller
 * 
 * @author zw
 * @date 2021-01-19
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/datasource" )
public class DataSourceController extends BaseController {

    private final IDataSourceService iDataSourceService;

    /**
     * 查询数据源配置列表
     */
    @PreAuthorize(hasPermi = "integration:datasource:list")
    @GetMapping("/list")
    public TableDataInfo list(DataSource dataSource) {
        startPage();
        List<DataSource> list = iDataSourceService.queryList(dataSource);
        return getDataTable(list);
    }

    /**
     * 导出数据源配置列表
     */
    @PreAuthorize(hasPermi = "integration:datasource:export" )
    @Log(title = "数据源配置" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataSource dataSource) throws IOException
    {
        List<DataSource> list = iDataSourceService.queryList(dataSource);
        ExcelUtil<DataSource> util = new ExcelUtil<DataSource>(DataSource.class);
        util.exportExcel(response,list, "datasource" );
    }

    /**
     * 获取数据源配置详细信息
     */
    @PreAuthorize(hasPermi = "integration:datasource:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return AjaxResult.success(iDataSourceService.getById(id));
    }

    /**
     * 新增数据源配置
     */
    @PreAuthorize(hasPermi = "integration:datasource:add" )
    @Log(title = "数据源配置" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataSource dataSource) {
        return toAjax(iDataSourceService.save(dataSource) ? 1 : 0);
    }

    /**
     * 修改数据源配置
     */
    @PreAuthorize(hasPermi = "integration:datasource:edit" )
    @Log(title = "数据源配置" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataSource dataSource) {
        return toAjax(iDataSourceService.updateById(dataSource) ? 1 : 0);
    }

    /**
     * 删除数据源配置
     */
    @PreAuthorize(hasPermi = "integration:datasource:remove" )
    @Log(title = "数据源配置" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iDataSourceService.removeByIds(Arrays.asList(ids)) ? 1 : 0);
    }
}
