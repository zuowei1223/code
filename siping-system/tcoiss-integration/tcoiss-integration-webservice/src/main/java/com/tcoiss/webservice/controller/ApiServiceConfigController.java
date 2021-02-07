package com.tcoiss.webservice.controller;

import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.common.core.utils.poi.ExcelUtil;
import com.tcoiss.common.core.web.controller.BaseController;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.core.web.page.TableDataInfo;
import com.tcoiss.common.log.annotation.Log;
import com.tcoiss.common.log.enums.BusinessType;
import com.tcoiss.common.security.annotation.PreAuthorize;
import com.tcoiss.webservice.domain.ApiServiceConfig;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IApiServiceConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * API服务配置Controller
 * 
 * @author zw
 * @date 2021-01-19
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/apiServiceConfig" )
public class ApiServiceConfigController extends BaseController {

    private final IApiServiceConfigService iApiServiceConfigService;
    private final IApiService iApiService;

    /**
     * 查询API服务配置列表
     */
    @PreAuthorize(hasPermi="${integration:apiServiceConfig}:list")
    @GetMapping("/list")
    public TableDataInfo list(ApiServiceConfig apiServiceConfig) {
        startPage();
        List<ApiServiceConfig> list = iApiServiceConfigService.queryList(apiServiceConfig);
        return getDataTable(list);
    }

    /**
     * 导出API服务配置列表
     */
    @PreAuthorize(hasPermi="${integration:apiServiceConfig}:export" )
    @Log(title = "API服务配置" , businessType = BusinessType.EXPORT)
    @PostMapping("/export" )
    public void export(HttpServletResponse response, ApiServiceConfig apiServiceConfig)throws IOException {
        List<ApiServiceConfig> list = iApiServiceConfigService.queryList(apiServiceConfig);
        ExcelUtil<ApiServiceConfig> util = new ExcelUtil<ApiServiceConfig>(ApiServiceConfig.class);
        util.exportExcel(response,list, "apiServiceConfig" );
    }

    /**
     * 获取API服务配置详细信息
     */
    @PreAuthorize(hasPermi="${integration:apiServiceConfig}:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return AjaxResult.success(iApiServiceConfigService.getById(id));
    }

    /**
     * 新增API服务配置
     */
    @PreAuthorize(hasPermi="${integration:apiServiceConfig}:add" )
    @Log(title = "API服务配置" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ApiServiceConfig apiServiceConfig) {
        apiServiceConfig.setCreateName(SecurityUtils.getUsername());
        apiServiceConfig.setCreateId(SecurityUtils.getUserId());
        return toAjax(iApiServiceConfigService.save(apiServiceConfig) ? 1 : 0);
    }

    /**
     * 修改API服务配置
     */
    @PreAuthorize(hasPermi="${integration:apiServiceConfig}:edit" )
    @Log(title = "API服务配置" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ApiServiceConfig apiServiceConfig) {
        return toAjax(iApiServiceConfigService.updateById(apiServiceConfig) ? 1 : 0);
    }

    /**
     * 删除API服务配置
     */
    @PreAuthorize(hasPermi="${integration:apiServiceConfig}:remove" )
    @Log(title = "API服务配置" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(iApiServiceConfigService.removeByIds(Arrays.asList(ids)) ? 1 : 0);
    }

    /**
     * 测试API服务
     */
    @PreAuthorize(hasPermi="${integration:apiService}:test" )
    @Log(title = "API服务测试" , businessType = BusinessType.DELETE)
    @GetMapping("apiTest/{id}" )
    public AjaxResult apiTest(@PathVariable("id" ) Long id) {
        return AjaxResult.success(iApiService.apiTest(id));
    }
}
