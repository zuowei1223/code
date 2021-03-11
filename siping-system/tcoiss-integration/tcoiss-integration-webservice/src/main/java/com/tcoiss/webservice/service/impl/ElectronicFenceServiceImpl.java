package com.tcoiss.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.apiServer.HttpAPIServer;
import com.tcoiss.webservice.domain.*;
import com.tcoiss.webservice.mapper.ElectronicFenceMapper;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IElectronicFenceService;
import com.tcoiss.webservice.service.IFencePointsService;
import com.tcoiss.webservice.service.ITrackServiceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 电子围栏Service业务层处理
 *
 * @author zw
 * @date 2021-01-31
 */
@Service
public class ElectronicFenceServiceImpl extends ServiceImpl<ElectronicFenceMapper, ElectronicFence> implements IElectronicFenceService {


    @Override
    public List<ElectronicFence> queryList(ElectronicFence electronicFence) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (electronicFence.getFenceCode() != null){
            lqw.eq(ElectronicFence::getFenceCode ,electronicFence.getFenceCode());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceName())){
            lqw.like(ElectronicFence::getFenceName ,electronicFence.getFenceName());
        }
        if (electronicFence.getCreateorId() != null){
            lqw.eq(ElectronicFence::getCreateorId ,electronicFence.getCreateorId());
        }
        List<ElectronicFence> list = this.list(lqw);

        return list;
    }

    @Override
    public ElectronicFence getByFenceName(String fenceName) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(fenceName)){
            lqw.eq(ElectronicFence::getFenceName ,fenceName);
        }
        List<ElectronicFence> list = this.list(lqw);
        if(list==null||list.size()==0){
            throw new ApiException("404",new Object[]{fenceName},
                    "未查询到围栏信息：");
        }
        return list.get(0);
    }

    @Override
    public boolean checkFenceName(ElectronicFence electronicFence) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(electronicFence.getFenceName())){
            lqw.like(ElectronicFence::getFenceName ,electronicFence.getFenceName());
        }
        List<ElectronicFence> list = this.list(lqw);
        if(list!=null&&list.size()>0){
            return true;
        }
        return false;
    }

    /*@Override
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        List<Map> list = null;
        //根据地址信息查询地址坐标
        TrackService trackService = new TrackService();
        trackService.setDataLevel(1);
        trackService.setFenceNum(1000L);
        List<TrackService> trackServices = iTrackServiceService.queryList(trackService);
        for(TrackService trackService1:trackServices) {
            addressVo.setKey(trackService1.getGaodeKey());
            Map resultMap = iApiService.executeByApiCode("geocode", addressVo);
            if (resultMap == null) {
                return AjaxResult.error(404,"获取地址坐标请求连接异常");
            } else {
                if (Integer.valueOf(resultMap.get("status").toString()) == 1) {//成功
                    //获取坐标数据
                    List<Map<String, Object>> geocodes = (List<Map<String, Object>>) resultMap.get("geocodes");
                    Map<String, Object> geocode = geocodes.get(0);
                    String location = geocode.get("location").toString();
                    Map<String, Object> param = new HashMap<>();
                    param.put("key", trackService1.getGaodeKey());
                    param.put("sid", trackService1.getServiceId());
                    param.put("location", location);
                    Map resultMap2 = iApiService.executeByApiCode("queryLocationStatus", param);
                    if (resultMap2 == null) {
                        return AjaxResult.error(405,"根据地址获取围栏请求连接异常");
                    } else {
                        if (Integer.valueOf(resultMap2.get("errcode").toString()) == 10000) {//成功
                            Map<String,Object> dataMap = (Map<String, Object>) resultMap2.get("data");
                            List<Map<String,Object>> results = (List<Map<String, Object>>) dataMap.get("results");
                            for(Map<String,Object> result:results){
                                if(result.get("in").toString().equals("1")){
                                    LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
                                    lqw.eq(ElectronicFence::getFenceGid,result.get("gfid").toString());
                                    ElectronicFence electronicFence = this.getOne(lqw);
                                    Map<String,Object> rMap = new HashMap<>();
                                    rMap.put("fenceName",electronicFence.getFenceName());
                                    rMap.put("fenceId",electronicFence.getLocalKey());
                                    rMap.put("fencePop",electronicFence.getFencePop());
                                    list.add(rMap);
                                }

                            }
                            if(list.size()>0){
                                return AjaxResult.success("成功",list);
                            }
                            return AjaxResult.error(201,"该地址未在任何围栏中");
                        }else{
                            return AjaxResult.error(406,"根据地址获取围栏接口异常："+resultMap2.get("errmsg"));
                        }
                    }
                } else {
                    return AjaxResult.error(408,"获取地址坐标接口异常："+resultMap.get("info").toString());
                }

            }
        }

        return null;
    }



    @Override
    public List<FencePoints> getDistrictOpints(String serviceId,String apiCode, Map<String, Object> querstMap) {

        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,serviceId);
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { serviceId },"未查询到相应的轨迹服务配置");
        }
        List<FencePoints> list = new ArrayList<>();
        querstMap.put("key",trackService.getGaodeKey());
        Map resultMap = iApiService.executeByApiCode(apiCode,querstMap);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("infocode").toString())==10000) {//创建成功
                //更新坐标数据
                List<Object> districts = (List<Object>) resultMap.get("districts");
                Map<String,Object> district = (Map<String, Object>) districts.get(0);
                String points = district.get("polyline").toString();
                for (String s : points.split(";")){
                    String[] point = s.split(",");
                    FencePoints fencePoints = new FencePoints();
                    fencePoints.setPointX(point[0]);
                    fencePoints.setPointY(point[1]);
                    //fencePoints.setPointName(querstMap.get("keywords").toString());
                    list.add(fencePoints);
                }
                return list;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("infocode").toString()},
                        "接口调用异常："+resultMap.get("info").toString());
            }
        }

    }



    @Override
    public boolean removeByIds(List<Long> ids,String apiCode) {
        for (Long id :ids){
            ElectronicFence electronicFence = this.getById(id);
            Map<String,String> map = new HashMap<>();
            map.put("sid",electronicFence.getServiceId());
            map.put("gfids",electronicFence.getFenceGid());
            LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
            lqw.eq(TrackService::getServiceId,electronicFence.getServiceId());
            lqw.eq(TrackService::getDataLevel,1);
            TrackService trackService = iTrackServiceService.getOne(lqw);
            if(trackService==null){
                throw new ApiException("9999",new Object[] { electronicFence.getServiceId() },"未查询到相应的轨迹服务配置");
            }
            map.put("key",trackService.getGaodeKey());
            Map resultMap = iApiService.executeByApiCode(apiCode,map);
            if(resultMap == null){
                throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
            }else{
                if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                    //删除坐标数据
                    iFencePointsService.deleteByFenceId(electronicFence.getLocalKey());
                    return this.removeById(electronicFence);
                }else{
                    throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                            "接口调用异常："+resultMap.get("errmsg").toString());
                }
            }
        }

        return false;
    }*/

}
