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
    @Autowired
    private IApiService iApiService;
    @Autowired
    private ITrackServiceService iTrackServiceService;
    @Autowired
    private IFencePointsService iFencePointsService;


    @Override
    public List<ElectronicFence> queryList(ElectronicFence electronicFence) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (electronicFence.getLocalKey() != null){
            lqw.eq(ElectronicFence::getLocalKey ,electronicFence.getLocalKey());
        }
        if (electronicFence.getFenceGid() != null){
            lqw.eq(ElectronicFence::getFenceGid ,electronicFence.getFenceGid());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceName())){
            lqw.like(ElectronicFence::getFenceName ,electronicFence.getFenceName());
        }
        if (electronicFence.getCreateorId() != null){
            lqw.eq(ElectronicFence::getCreateorId ,electronicFence.getCreateorId());
        }
        List<ElectronicFence> list = this.list(lqw);
        for(ElectronicFence fence:list){
            List<FencePoints> fencePointsList = iFencePointsService.queryListByFenceId(fence.getLocalKey());
            fence.setPoints(fencePointsList);
        }
        return list;
    }

    @Override
    public ElectronicFence getFenceById(Long id) {
        ElectronicFence electronicFence = this.getById(id);
        List<FencePoints> fencePointsList = iFencePointsService.queryListByFenceId(electronicFence.getLocalKey());
        electronicFence.setPoints(fencePointsList);
        return electronicFence;
    }

    @Override
    public int saveFence(ElectronicFence fence, List<FencePoints> coordinate, String apiCode) {

        //先分配服务ID再调接口再保存到本地入库
        FenceVo vo = new FenceVo();
        vo.setName(fence.getFenceName());
        vo.setSid(fence.getServiceId());
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,fence.getServiceId());
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { fence.getServiceId() },"未查询到相应的轨迹服务配置");
        }
        vo.setKey(trackService.getGaodeKey());
        List<String> points= coordinate.stream().map(fencePoints->{return fencePoints.getPoints();}).collect(Collectors.toList());
        vo.setPoints(String.join(";",points));
        /*List<String> points = new ArrayList<>();
        List<String> gids = new ArrayList<>();
        if(fence.getFencePoints().contains("|")){
            points = Arrays.asList(fence.getFencePoints().split("\\|"));
        }else{
            points.add(fence.getFencePoints());
        }*/
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                fence.setFenceGid(dataMap.get("gfid").toString());
                //将平台gfid更新到数据库中并在服务配置中加1
                iTrackServiceService.addFenceNum(trackService);
                //将坐标数据保存
                coordinate.forEach(item-> {
                    item.setFenceName(fence.getFenceName());
                    item.setFenceId(fence.getLocalKey());
                });
                iFencePointsService.saveBatch(coordinate);
                return this.save(fence) ? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errmsg").toString());
            }
        }
    }

    @Override
    public int saveDistrictFence(ElectronicFence fence, List<FencePoints> coordinate, String apiCode) {
        Map<String,Object> paramer = new HashMap<>();
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,fence.getServiceId());
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { fence.getServiceId() },"未查询到相应的轨迹服务配置");
        }
        paramer.put("key",trackService.getGaodeKey());
        paramer.put("sid",fence.getServiceId());
        paramer.put("name",fence.getFenceName());
        paramer.put("desc",fence.getFenceDesc()==null?fence.getFenceName():fence.getFenceDesc());
        paramer.put("adcode",fence.getDistrict());
        Map resultMap = iApiService.executeByApiCode(apiCode,paramer);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                fence.setFenceGid(dataMap.get("gfid").toString());
                //将平台gfid更新到数据库中并在服务配置中加1
                iTrackServiceService.addFenceNum(trackService);
                //将坐标数据保存
                coordinate.forEach(item-> {
                    item.setFenceName(fence.getFenceName());
                    item.setFenceId(fence.getLocalKey());
                        });
                iFencePointsService.saveBatch(coordinate);
                return this.save(fence) ? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errmsg").toString());
            }
        }
    }

    @Override
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        List<ElectronicFence> list = null;
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
                                    ElectronicFence electronicFence = new ElectronicFence();
                                    electronicFence.setFenceGid(result.get("gfid").toString());
                                    electronicFence.setFenceName(result.get("gfname").toString());
                                    return AjaxResult.success("成功",this.queryList(electronicFence).get(0));
                                }

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
    public boolean updateById(ElectronicFence electronicFence, List<FencePoints> coordinate, String apiCode) {
        FenceVo vo = new FenceVo();
        vo.setName(electronicFence.getFenceName());
        vo.setSid(electronicFence.getServiceId());
        vo.setGfid(electronicFence.getFenceGid());
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,electronicFence.getServiceId());
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { electronicFence.getServiceId() },"未查询到相应的轨迹服务配置");
        }
        vo.setKey(trackService.getGaodeKey());
        List<String> points= coordinate.stream().map(fencePoints->{return fencePoints.getPoints();}).collect(Collectors.toList());
        vo.setPoints(String.join(";",points));
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                //更新坐标数据
                coordinate.forEach(item-> {
                    item.setFenceName(electronicFence.getFenceName());
                    item.setFenceId(electronicFence.getLocalKey());
                });
                iFencePointsService.updateBatch(electronicFence,coordinate);
                return this.updateById(electronicFence);
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errmsg").toString());
            }
        }
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
    }

}
