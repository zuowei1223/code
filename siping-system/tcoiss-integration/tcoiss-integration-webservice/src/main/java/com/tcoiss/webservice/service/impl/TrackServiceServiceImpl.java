package com.tcoiss.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.webservice.service.IApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import com.tcoiss.webservice.mapper.TrackServiceMapper;
import com.tcoiss.webservice.domain.TrackService;
import com.tcoiss.webservice.service.ITrackServiceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轨迹服务配置Service业务层处理
 *
 * @author zw
 * @date 2021-02-21
 */
@Service
public class TrackServiceServiceImpl extends ServiceImpl<TrackServiceMapper, TrackService> implements ITrackServiceService {

    @Autowired
    private IApiService iApiService;

    @Override
    public List<TrackService> queryList(TrackService trackService) {
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(trackService.getFwName())){
            lqw.like(TrackService::getFwName ,trackService.getFwName());
        }
        if (trackService.getGaodeKey() != null){
            lqw.eq(TrackService::getGaodeKey ,trackService.getGaodeKey());
        }
        if (trackService.getServiceId() != null){
            lqw.eq(TrackService::getServiceId ,trackService.getServiceId());
        }
        if (trackService.getDataLevel() != null){
            lqw.le(TrackService::getDataLevel ,trackService.getDataLevel());
        }
        if (trackService.getFenceNum() != null){
            lqw.le(TrackService::getFenceNum ,trackService.getFenceNum());
        }
        return this.list(lqw);
    }

    @Override
    public boolean saveTrackService(TrackService trackService,String apiCode) {
        Map<String,Object> param = new HashMap<>();
        param.put("key",trackService.getGaodeKey());
        param.put("name",trackService.getFwName());
        param.put("desc",trackService.getServiceDesc());
        Map<String,Object> resultMap = iApiService.executeByApiCode(apiCode,param);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }
        if(Integer.valueOf(resultMap.get("errcode").toString())==10000){//成功
            String sid = JSON.parseObject(resultMap.get("data").toString()).get("sid").toString();
            trackService.setServiceId(sid);
            trackService.setCreatorId(SecurityUtils.getUserId());
            trackService.setCreatorName(SecurityUtils.getUsername());
            return this.save(trackService) ;
        }else{//errmsg
            throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},"接口调用异常："+resultMap.get("errdetail").toString());
        }
    }

    @Override
    public boolean addFenceNum(TrackService trackService) {
        trackService.setFenceNum(trackService.getFenceNum()+1);
        return this.updateById(trackService);
    }

    @Override
    public boolean removeByIds(List<Long> ids) {

        return false;
    }

}
