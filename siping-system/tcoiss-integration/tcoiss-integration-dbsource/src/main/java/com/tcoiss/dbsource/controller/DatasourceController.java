package com.tcoiss.dbsource.controller;

import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import com.tcoiss.dbsource.service.IDatasourceService;
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
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.dbsource.domain.Datasource;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.core.utils.poi.ExcelUtil;
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * datasourceController
 * 
 * @author zw
 * @date 2021-01-13
 */
@RestController
@RequestMapping("/datasource")
public class DatasourceController extends BaseController
{
    @Autowired
    private IDatasourceService datasourceService;

    /**
     * 查询datasource列表
     */
    @PreAuthorize(hasPermi = "dbsource:datasource:list")
    @GetMapping("/list")
    public TableDataInfo list(Datasource datasource)
    {
        startPage();
        List<Datasource> list = datasourceService.selectDatasourceList(datasource);
        return getDataTable(list);
    }

    /**
     * 导出datasource列表
     */
    @PreAuthorize(hasPermi = "dbsource:datasource:export")
    @Log(title = "datasource", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Datasource datasource) throws IOException
    {
        List<Datasource> list = datasourceService.selectDatasourceList(datasource);
        ExcelUtil<Datasource> util = new ExcelUtil<Datasource>(Datasource.class);
        util.exportExcel(response, list, "datasource");
    }

    /**
     * 获取datasource详细信息
     */
    @PreAuthorize(hasPermi = "dbsource:datasource:query")
    @GetMapping(value = "/{datasourceId}")
    public AjaxResult getInfo(@PathVariable("datasourceId") String datasourceId)
    {
        return AjaxResult.success(datasourceService.selectDatasourceById(datasourceId));
    }

    /**
     * 新增datasource
     */
    @PreAuthorize(hasPermi = "dbsource:datasource:add")
    @Log(title = "datasource", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Datasource datasource)
    {
        return toAjax(datasourceService.insertDatasource(datasource));
    }

    /**
     * 修改datasource
     */
    @PreAuthorize(hasPermi = "dbsource:datasource:edit")
    @Log(title = "datasource", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Datasource datasource)
    {
        return toAjax(datasourceService.updateDatasource(datasource));
    }

    /**
     * 删除datasource
     */
    @PreAuthorize(hasPermi = "dbsource:datasource:remove")
    @Log(title = "datasource", businessType = BusinessType.DELETE)
	@DeleteMapping("/{datasourceIds}")
    public AjaxResult remove(@PathVariable String[] datasourceIds)
    {
        return toAjax(datasourceService.deleteDatasourceByIds(datasourceIds));
    }
}
