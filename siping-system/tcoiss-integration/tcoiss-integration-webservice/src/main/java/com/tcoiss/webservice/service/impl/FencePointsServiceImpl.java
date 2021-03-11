package com.tcoiss.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.webservice.domain.*;
import com.tcoiss.webservice.mapper.FencePointsMapper;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IElectronicFenceService;
import com.tcoiss.webservice.service.IFencePointsService;
import com.tcoiss.webservice.service.ITrackServiceService;
import com.tcoiss.webservice.utils.IdGeneratorFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 轨迹服务配置Service业务层处理
 *
 * @author zw
 * @date 2021-02-21
 */
@Service
public class FencePointsServiceImpl extends ServiceImpl<FencePointsMapper, FencePoints> implements IFencePointsService {

    @Autowired
    private IElectronicFenceService iElectronicFenceService;

    @Autowired
    private ITrackServiceService iTrackServiceService;

    @Autowired
    private IApiService iApiService;

    @Autowired
    private IdGeneratorFactory idGeneratorFactory;

    /**
     * 根据查询指定的围栏
     * @param fencePoints
     * @return
     */
    @Override
    public Map<String,List<PointsVo>> queryList(FencePoints fencePoints) {
        List<PointsVo> list = new ArrayList<>();
        List<PointsVo> list2 = new ArrayList<>();
        Map<String,List<PointsVo>> map = new HashMap<>();
        LambdaQueryWrapper<FencePoints> lqw = Wrappers.lambdaQuery();
        lqw.select(FencePoints::getPointsGroupId);
        lqw.groupBy(FencePoints::getPointsGroupId);
        List<FencePoints> gids = this.list(lqw);
        for(FencePoints fencePoints1:gids) {
            //查询所有的围栏坐标
            LambdaQueryWrapper<FencePoints> lqw0 = Wrappers.lambdaQuery();
            if (fencePoints1!= null) {
                lqw0.eq(FencePoints::getPointsGroupId, fencePoints1.getPointsGroupId());
                PointsVo pointsVo = new PointsVo();
                pointsVo.setDrawPoints(this.list(lqw0));
                pointsVo.setFenceName(this.list(lqw0).get(0).getFenceName());
                pointsVo.setPointsGroupId(fencePoints1.getPointsGroupId());
                list.add(pointsVo);
            }

        }
        for(FencePoints fencePoints1:gids) {
            if(StringUtils.isNotBlank(fencePoints.getFenceName())||StringUtils.isNotBlank(fencePoints.getPointsGroupId())){
                LambdaQueryWrapper<FencePoints> lqw2 = Wrappers.lambdaQuery();
                if(StringUtils.isNotBlank(fencePoints.getFenceName())){
                    lqw2.eq(FencePoints::getFenceName, fencePoints.getFenceName());
                }
                if (fencePoints1 != null && StringUtils.isNotBlank(fencePoints.getPointsGroupId())) {
                    lqw2.eq(FencePoints::getPointsGroupId, fencePoints.getPointsGroupId());

                }else if(fencePoints1 != null
                        &&(StringUtils.isNotBlank(fencePoints.getFenceName()))) {
                    lqw2.eq(FencePoints::getPointsGroupId, fencePoints1.getPointsGroupId());
                }
                List<FencePoints> pointsList = this.list(lqw2);
                if(pointsList!=null&&pointsList.size()>0){
                    PointsVo pointsVo2 = new PointsVo();
                    pointsVo2.setDrawPoints(pointsList);
                    pointsVo2.setFenceName(fencePoints.getFenceName());
                    pointsVo2.setPointsGroupId(pointsList.get(0).getPointsGroupId());
                    list2.add(pointsVo2);
                    if(StringUtils.isNotBlank(fencePoints.getPointsGroupId())){
                        break;
                    }
                }
            }

        }
        if(list2!=null){
            list.removeAll(list2);
        }
        map.put("fence",list2);
        map.put("all",list);
        return map;
    }

    @Override
    public int saveFencePoints(PointsVo pointsVo, String apiCode) {
        ElectronicFence fence = iElectronicFenceService.getByFenceName(pointsVo.getFenceName());
        String pointsName = pointsVo.getFenceName()+"-"+idGeneratorFactory.generate(fence.getFenceCode());
        //先分配服务ID再调接口再保存到本地入库
        FenceVo vo = new FenceVo();
        vo.setName(pointsName);
        vo.setSid(fence.getServiceId());
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,fence.getServiceId());
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { fence.getServiceId() },"未查询到相应的轨迹服务配置");
        }
        vo.setKey(trackService.getGaodeKey());
        List<FencePoints> pointsList = pointsVo.getDrawPoints();
        List<String> points= pointsList.stream().map(fencePoints->{return fencePoints.getPoints();}).collect(Collectors.toList());
        vo.setPoints(String.join(";",points));
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                //将平台gfid更新到数据库中并在服务配置中加1
                iTrackServiceService.addFenceNum(trackService);
                //将坐标数据保存
                pointsList.forEach(item-> {
                    item.setPointName(pointsName);
                    item.setFenceName(pointsVo.getFenceName());
                    item.setPointsGroupId(dataMap.get("gfid").toString());
                });
                return this.saveBatch(pointsList)? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errmsg").toString());
            }
        }
    }

    @Override
    public boolean updateByVo(PointsVo pointsVo, String apiCode) {
        LambdaQueryWrapper<FencePoints> lqw0 = Wrappers.lambdaQuery();
        lqw0.eq(FencePoints::getPointsGroupId,pointsVo.getPointsGroupId());
        List<FencePoints> fencePointsList = this.list(lqw0);
        ElectronicFence fence = iElectronicFenceService.getByFenceName(fencePointsList.get(0).getFenceName());
        FenceVo vo = new FenceVo();
        vo.setName(fencePointsList.get(0).getPointName());
        vo.setSid(fence.getServiceId());
        vo.setGfid(pointsVo.getPointsGroupId());
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,fence.getServiceId());
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { fence.getServiceId() },"未查询到相应的轨迹服务配置");
        }
        vo.setKey(trackService.getGaodeKey());
        List<FencePoints> pointsList = pointsVo.getDrawPoints();
        List<String> points= pointsList.stream().map(fencePoints->{return fencePoints.getPoints();}).collect(Collectors.toList());
        vo.setPoints(String.join(";",points));
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                //更新坐标数据
                pointsList.forEach(item-> {
                    item.setFenceName(fence.getFenceName());
                    item.setFenceCode(fence.getFenceCode());
                    item.setPointName(fencePointsList.get(0).getPointName());
                    item.setPointsGroupId(pointsVo.getPointsGroupId());
                });
                this.deleteByGid(pointsVo.getPointsGroupId());
                return this.saveBatch(pointsList);
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }
    }

    @Override
    public List<PointsVo> getDistrictOpints(PointsVo pointsVo, String apiCode, Map<String, Object> querstMap) {
        ElectronicFence fence = iElectronicFenceService.getByFenceName(pointsVo.getFenceName());
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceId,fence.getServiceId());
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { fence.getServiceId() },"未查询到相应的轨迹服务配置");
        }
        List<PointsVo> pointsVos = new ArrayList<>();
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
                String[] ss = {};
                if (StringUtils.contains(points, "|")) {
                    ss = points.split("\\|");
                }else{
                    ss = points.split("\\|");
                }
                for(String s : ss){
                    for (String s1 : s.split(";")){
                        String[] point = s1.split(",");
                        FencePoints fencePoints = new FencePoints();
                        fencePoints.setPointX(point[0]);
                        fencePoints.setPointY(point[1]);
                        list.add(fencePoints);
                    }
                    pointsVo.setDrawPoints(list);
                    pointsVos.add(pointsVo);
                }
                return pointsVos;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("infocode").toString()},
                        "接口调用异常："+resultMap.get("info").toString());
            }
        }

    }

    @Override
    public int saveDistrictFence(PointsVo pointsVo, String apiCode) {
        ElectronicFence fence = iElectronicFenceService.getByFenceName(pointsVo.getFenceName());
        String pointsName = pointsVo.getFenceName()+"-"+ idGeneratorFactory.generate(fence.getFenceCode());
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
        paramer.put("name",pointsName);
        paramer.put("desc",fence.getFenceDesc()==null?pointsVo.getAdcodeName():fence.getFenceDesc());
        paramer.put("adcode",pointsVo.getAdcode());
        Map resultMap = iApiService.executeByApiCode(apiCode,paramer);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                //将平台gfid更新到数据库中并在服务配置中加1
                iTrackServiceService.addFenceNum(trackService);
                //将坐标数据保存
                List<FencePoints> pointsList = pointsVo.getDrawPoints();
                pointsList.forEach(item-> {
                    item.setPointName(pointsName);
                    item.setFenceCode(fence.getFenceCode());
                    item.setFenceName(pointsVo.getFenceName());
                    item.setPointsGroupId(dataMap.get("gfid").toString());
                });
                return this.saveOrUpdateBatch(pointsList)? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }
    }

    @Override
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        List<Map<String,Object>> list = new ArrayList<>();
        //根据地址信息查询地址坐标
        TrackService trackService = iTrackServiceService.getOneByAddrss(addressVo);
        addressVo.setKey(trackService.getGaodeKey());
        Map resultMap = iApiService.executeByApiCode("geocode", addressVo);
        if (resultMap == null) {
            return AjaxResult.error(404,"获取地址坐标请求连接异常");
        } else {
            if (Integer.valueOf(resultMap.get("status").toString()) == 1) {//成功
                //获取地址坐标数据
                List<Map<String, Object>> geocodes = (List<Map<String, Object>>) resultMap.get("geocodes");
                Map<String, Object> geocode = geocodes.get(0);
                String location = geocode.get("location").toString();
                Map<String, Object> param = new HashMap<>();
                param.put("key", trackService.getGaodeKey());
                param.put("sid", trackService.getServiceId());
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
                                String gfid = result.get("gfid").toString();
                                LambdaQueryWrapper<FencePoints> lqw = Wrappers.lambdaQuery();
                                lqw.eq(FencePoints::getPointsGroupId,gfid);
                                List<FencePoints> fencePointsList = this.list(lqw);
                                Map<String,Object> map = new HashMap<>();
                                map.put("fenceName",fencePointsList.get(0).getFenceName());
                                map.put("fenceCode",fencePointsList.get(0).getFenceCode());
                                //String gfName = result.get("gfname").toString();
                                list.add(map);
                            }
                        }
                        if(list!=null&&list.size()>0){
                            return AjaxResult.success("成功",list);
                        }
                        return AjaxResult.error(201,"该地址未在围栏中");
                    }else{
                        return AjaxResult.error(406,"根据地址获取围栏接口异常："+resultMap2.get("errdetail"));
                    }
                }
            } else {
                return AjaxResult.error(408,"获取地址坐标接口异常："+resultMap.get("info").toString());
            }

        }
    }

    private boolean deleteByGid(String gid) {
        LambdaQueryWrapper<FencePoints> lqw = Wrappers.lambdaQuery();
        lqw.eq(FencePoints::getPointsGroupId,gid);
        return this.remove(lqw);
    }

    @Override
    public boolean removeByGid(Long gid,String apiCode) {
        LambdaQueryWrapper<FencePoints> lqw0 = Wrappers.lambdaQuery();
        lqw0.eq(FencePoints::getPointsGroupId,gid);
        List<FencePoints> fencePointsList = this.list(lqw0);
        ElectronicFence electronicFence = iElectronicFenceService.getByFenceName(fencePointsList.get(0).getFenceName());
        Map<String,String> map = new HashMap<>();
        map.put("sid",electronicFence.getServiceId());
        map.put("gfids",gid.toString());
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
                return this.deleteByGid(gid.toString());
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }

    }

}
