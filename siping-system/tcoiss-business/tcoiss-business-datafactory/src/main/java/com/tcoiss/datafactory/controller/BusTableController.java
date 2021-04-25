package com.tcoiss.datafactory.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.tcoiss.common.core.utils.poi.ExcelUtil;
import com.tcoiss.datafactory.domain.BusTable;
import com.tcoiss.datafactory.domain.BusTableColumn;
import com.tcoiss.datafactory.domain.vo.TableVO;
import com.tcoiss.datafactory.service.IBusTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.common.core.web.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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
    @PreAuthorize(hasPermi = "bus:table:import" )
    @Log(title = "业务表", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<TableVO> util = new ExcelUtil<TableVO>(TableVO.class);
        List<TableVO> columns = util.importExcel(file.getInputStream());

        String message = iBusTableService.importTable(columns,updateSupport);
        return AjaxResult.success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<TableVO> util = new ExcelUtil<TableVO>(TableVO.class);
        util.importTemplateExcel(response,"表结构");
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

    /**
     * 提交业务表配置，同步业务表结构，创建表并插入表数据
     * @param busTable
     * @return
     */
    @PreAuthorize(hasPermi = "bus:table:edit" )
    @Log(title = "提交业务表配置" , businessType = BusinessType.UPDATE)
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
        return toAjax(iBusTableService.removeTablesByIds(Arrays.asList(tableIds)) ? 1 : 0);
    }
    /**
     * 同步表结构
     */
    /*@PreAuthorize(hasPermi = "bus:table:add" )
    @Log(title = "业务表配置" , businessType = BusinessType.INSERT)
    @PostMapping("/syncTableJg")
    public AjaxResult syncTableJg(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.syncTableJg(busTable) ? 1 : 0);
    }*/

    /**
     * 初始化业务表
     */
    @PreAuthorize(hasPermi = "bus:table:add" )
    @Log(title = "初始化业务表" , businessType = BusinessType.INSERT)
    @PostMapping("/initTable")
    public AjaxResult initTable(@RequestBody BusTable busTable) {
        return toAjax(iBusTableService.initTable(busTable) ? 1 : 0);
    }

}
