package com.tcoiss.datafactory.controller;

import java.util.List;
import java.util.Arrays;

import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.service.IBusTableService;
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
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * 生成表结构业务Controller
 * 
 * @author zw
 * @date 2021-04-20
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/table" )
public class BusTableController extends BaseController {

    private final IBusTableService iBusTableService;

    /**
     * 查询业务表
     */
    @PreAuthorize(hasPermi = "bus:table:list")
    @GetMapping("/list")
    public TableDataInfo list(BusTable busTable) {
        startPage();
        List<BusTable> list = iBusTableService.queryList(busTable);
        return getDataTable(list);
    }

    /**
     * 导入业务表
     */
    @PreAuthorize(hasPermi = "bus:table:export" )
    @Log(title = "业务表" , businessType = BusinessType.EXPORT)
    @PostMapping("/importTable")
    public void importTable(@RequestBody BusTable busTable)
    {
        List<BusTable> list = iBusTableService.queryList(busTable);

    }

    /**
     * 获取业务表配置详细信息,以及表的字段信息
     */
    @PreAuthorize(hasPermi = "bus:table:query" )
    @GetMapping(value = "/{tableId}" )
    public AjaxResult getInfo(@PathVariable("tableId" ) Long tableId) {
        return AjaxResult.success(iBusTableService.getBusTableById(tableId));
    }

    /**
     * 新增业务表配置
     */
    @PreAuthorize(hasPermi = "bus:table:add" )
    @Log(title = "业务表配置" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.saveTable(busTable) ? 1 : 0);
    }

    /**
     * 修改业务表
     */
    @PreAuthorize(hasPermi = "bus:table:edit" )
    @Log(title = "修改业务表配置" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.updateBusTableById(busTable) ? 1 : 0);
    }

    @PreAuthorize(hasPermi = "bus:table:edit" )
    @Log(title = "修改业务表配置" , businessType = BusinessType.UPDATE)
    @PutMapping("/updateBusTable")
    public AjaxResult editTable(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.updateBusTable(busTable) ? 1 : 0);
    }

    /**
     * 删除业务表配置
     */
    @PreAuthorize(hasPermi = "bus:table:remove" )
    @Log(title = "业务表配置" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}" )
    public AjaxResult remove(@PathVariable Long[] tableIds) {
        return toAjax(iBusTableService.removeByIds(Arrays.asList(tableIds)) ? 1 : 0);
    }
    /**
     * 同步表结构
     */
    @PreAuthorize(hasPermi = "bus:table:add" )
    @Log(title = "业务表配置" , businessType = BusinessType.INSERT)
    @PostMapping("/syncTableJg")
    public AjaxResult createTable(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.syncTableAllJg(busTable) ? 1 : 0);
    }

    /**
     * 依据配置同步业务表数据
     */
    @PreAuthorize(hasPermi = "bus:table:add" )
    @Log(title = "生成业务表" , businessType = BusinessType.INSERT)
    @PostMapping("/initTableData")
    public AjaxResult initTableData(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.initTableAllData(busTable) ? 1 : 0);
    }

}
