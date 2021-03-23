package com.tcoiss.webservice.controller;


import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.domain.AddressVo;
import com.tcoiss.webservice.service.IFencePointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/apiService" )
public class ApiServiceController {
    @Autowired
    private IFencePointsService iFencePointsService;
    @GetMapping("fence/getFenceByLocation")
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        return iFencePointsService.getFenceByLocation(addressVo);
    }

}
