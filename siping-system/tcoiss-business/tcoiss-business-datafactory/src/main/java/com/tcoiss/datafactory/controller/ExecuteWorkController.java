package com.tcoiss.datafactory.controller;

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
import com.tcoiss.datafactory.domain.ExecuteWork;
import com.tcoiss.datafactory.service.IExecuteWorkService;
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * 作业Controller
 * 
 * @author zw
 * @date 2021-04-26
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/work" )
public class ExecuteWorkController extends BaseController {

    private final IExecuteWorkService iExecuteWorkService;

    /**
     * 查询作业列表
     */
    @PreAuthorize(hasPermi = "datafactory:work:list")
    @GetMapping("/list")
    public TableDataInfo list(ExecuteWork executeWork) {
        startPage();
        List<ExecuteWork> list = iExecuteWorkService.queryList(executeWork);
        return getDataTable(list);
    }

    /**
     * 导出作业列表
     */
    @PreAuthorize(hasPermi = "datafactory:work:export" )
    @Log(title = "作业" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,ExecuteWork executeWork) throws IOException
    {
        List<ExecuteWork> list = iExecuteWorkService.queryList(executeWork);
        ExcelUtil<ExecuteWork> util = new ExcelUtil<ExecuteWork>(ExecuteWork.class);
        util.exportExcel(response,list, "work" );
    }

    /**
     * 获取作业详细信息
     */
    @PreAuthorize(hasPermi = "datafactory:work:query" )
    @GetMapping(value = "/{workId}" )
    public AjaxResult getInfo(@PathVariable("workId" ) Long workId) {
        return AjaxResult.success(iExecuteWorkService.getById(workId));
    }

    /**
     * 新增作业
     */
    @PreAuthorize(hasPermi = "datafactory:work:add" )
    @Log(title = "作业" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExecuteWork executeWork) {
        return toAjax(iExecuteWorkService.save(executeWork) ? 1 : 0);
    }

    /**
     * 修改作业
     */
    @PreAuthorize(hasPermi = "datafactory:work:edit" )
    @Log(title = "作业" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExecuteWork executeWork) {
        return toAjax(iExecuteWorkService.updateById(executeWork) ? 1 : 0);
    }

    /**
     * 删除作业
     */
    @PreAuthorize(hasPermi = "datafactory:work:remove" )
    @Log(title = "作业" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{workIds}" )
    public AjaxResult remove(@PathVariable Long[] workIds) {
        return toAjax(iExecuteWorkService.removeByIds(Arrays.asList(workIds)) ? 1 : 0);
    }
}
