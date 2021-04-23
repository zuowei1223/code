package com.tcoiss.business.base.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import com.tcoiss.business.base.dto.PersonDto;
import com.tcoiss.common.core.domain.R;
import com.tcoiss.common.mq.enums.OperMethodEnum;
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
import com.tcoiss.business.base.domain.KdPerson;
import com.tcoiss.business.base.service.IKdPersonService;
import com.tcoiss.common.core.web.page.TableDataInfo;

/**
 * 人员Controller
 * 
 * @author zw
 * @date 2021-04-07
 */
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/person" )
public class KdPersonController extends BaseController {

    private final IKdPersonService iKdPersonService;

    /**
     * 查询人员列表
     */
    @PreAuthorize(hasPermi = "base:person:list")
    @GetMapping("/list")
    public TableDataInfo list(KdPerson kdPerson) {
        startPage();
        List<KdPerson> list = iKdPersonService.queryList(kdPerson);
        return getDataTable(list);
    }

    /**
     * 导出人员列表
     */
    @PreAuthorize(hasPermi = "base:person:export" )
    @Log(title = "人员" , businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,KdPerson kdPerson) throws IOException
    {
        List<KdPerson> list = iKdPersonService.queryList(kdPerson);
        ExcelUtil<KdPerson> util = new ExcelUtil<KdPerson>(KdPerson.class);
        util.exportExcel(response,list, "person" );
    }

    /**
     * 获取人员详细信息
     */
    @PreAuthorize(hasPermi = "base:person:query" )
    @GetMapping(value = "/{personId}" )
    public AjaxResult getInfo(@PathVariable("personId" ) Long personId) {
        return AjaxResult.success(iKdPersonService.getById(personId));
    }

    /**
     * 新增人员
     */
    @PreAuthorize(hasPermi = "base:person:add" )
    @Log(title = "人员" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KdPerson kdPerson) {
        return toAjax(iKdPersonService.save(kdPerson) ? 1 : 0);
    }

    /**
     * 修改人员
     */
    @PreAuthorize(hasPermi = "base:person:edit" )
    @Log(title = "人员" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KdPerson kdPerson) {
        return toAjax(iKdPersonService.updateById(kdPerson) ? 1 : 0);
    }

    /**
     * 删除人员
     */
    @PreAuthorize(hasPermi = "base:person:remove" )
    @Log(title = "人员" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{personIds}" )
    public AjaxResult remove(@PathVariable Long[] personIds) {
        return toAjax(iKdPersonService.removeByIds(Arrays.asList(personIds)) ? 1 : 0);
    }

    @Log(title = "同步人员" , businessType = BusinessType.INSERT)
    @PostMapping("/syncPerson")
    public R<Boolean> syncPerson(@RequestBody PersonDto PersonDto) {

        return R.ok(iKdPersonService.syncPerson(PersonDto, OperMethodEnum.UPDATE,null,null));
    }


}
