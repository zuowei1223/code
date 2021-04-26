package com.tcoiss.datafactory.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import com.tcoiss.common.core.domain.R;
import com.tcoiss.datafactory.api.model.SchemeVO;
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
import com.tcoiss.datafactory.domain.ExecuteScheme;
import com.tcoiss.datafactory.service.IExecuteSchemeService;
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * 执行方案Controller
 * 
 * @author zw
 * @date 2021-04-26
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/scheme" )
public class ExecuteSchemeController extends BaseController {

    private final IExecuteSchemeService iExecuteSchemeService;

    /**
     * 查询执行方案列表
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:list")
    @GetMapping("/list")
    public TableDataInfo list(ExecuteScheme executeScheme) {
        startPage();
        List<ExecuteScheme> list = iExecuteSchemeService.queryList(executeScheme);
        return getDataTable(list);
    }

    /**
     * 根据参数查询方案列表
     * @param executeScheme
     * @return
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:list")
    @GetMapping("/getSchemes")
    public List<ExecuteScheme>  getSchemes(ExecuteScheme executeScheme) {
        List<ExecuteScheme> list = iExecuteSchemeService.queryList(executeScheme);
        return list;
    }

    /**
     * 根据执行编码和执行方式，执行策略，运行作业
     */
    //@PreAuthorize(hasPermi = "datafactory:scheme:execute" )
    @Log(title = "执行方案" , businessType = BusinessType.EXPORT)
    @PostMapping("/executeScheme")
    public R<Boolean> executeScheme(SchemeVO vo)
    {
        return iExecuteSchemeService.execute(vo);
    }



    /**
     * 导出执行方案列表
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:export" )
    @Log(title = "执行方案" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,ExecuteScheme executeScheme) throws IOException
    {
        List<ExecuteScheme> list = iExecuteSchemeService.queryList(executeScheme);
        ExcelUtil<ExecuteScheme> util = new ExcelUtil<ExecuteScheme>(ExecuteScheme.class);
        util.exportExcel(response,list, "scheme" );
    }

    /**
     * 获取执行方案详细信息
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:query" )
    @GetMapping(value = "/{schemeId}" )
    public AjaxResult getInfo(@PathVariable("schemeId" ) Long schemeId) {
        return AjaxResult.success(iExecuteSchemeService.getById(schemeId));
    }

    /**
     * 新增执行方案
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:add" )
    @Log(title = "执行方案" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExecuteScheme executeScheme) {
        return toAjax(iExecuteSchemeService.save(executeScheme) ? 1 : 0);
    }

    /**
     * 修改执行方案
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:edit" )
    @Log(title = "执行方案" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExecuteScheme executeScheme) {
        return toAjax(iExecuteSchemeService.updateById(executeScheme) ? 1 : 0);
    }

    /**
     * 删除执行方案
     */
    @PreAuthorize(hasPermi = "datafactory:scheme:remove" )
    @Log(title = "执行方案" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{schemeIds}" )
    public AjaxResult remove(@PathVariable Long[] schemeIds) {
        return toAjax(iExecuteSchemeService.removeByIds(Arrays.asList(schemeIds)) ? 1 : 0);
    }
}
