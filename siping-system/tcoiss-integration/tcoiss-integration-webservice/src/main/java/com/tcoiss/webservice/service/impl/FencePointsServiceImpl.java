package com.tcoiss.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.common.core.constant.Constants;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.utils.SecurityUtils;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.webservice.domain.*;
import com.tcoiss.webservice.mapper.FencePointsMapper;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IElectronicFenceService;
import com.tcoiss.webservice.service.IFencePointsService;
import com.tcoiss.webservice.service.ITrackServiceService;
import com.tcoiss.webservice.utils.IdGeneratorFactory;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
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




    /**
     * 查询所有的围栏
     * @param electronicFence
     * @return
     */
    @Override
    public List<ElectronicFence> queryList(ElectronicFence electronicFence) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if(StringUtils.isNotBlank(electronicFence.getFenceName())){
            lqw.eq(ElectronicFence::getFenceName, electronicFence.getFenceName());
        }
        if(StringUtils.isNotBlank(electronicFence.getAdcode())){
            lqw.eq(ElectronicFence::getAdcode, electronicFence.getAdcode());
        }
        lqw.eq(ElectronicFence::getCityCode, electronicFence.getCityCode());
        if(StringUtils.isNotBlank(electronicFence.getFencePop())){
            lqw.eq(ElectronicFence::getFencePop, electronicFence.getFencePop());
        }
        LambdaQueryWrapper<ElectronicFence> lqw2 = Wrappers.lambdaQuery();
        lqw2.eq(ElectronicFence::getFencePop,electronicFence.getFencePop());
        List<ElectronicFence> list = iElectronicFenceService.list(lqw2);
        List<ElectronicFence> list1 = iElectronicFenceService.list(lqw);
        list.forEach(fence ->{
            this.setPoints(fence);
            if(list1!=null&&list1.size()>0) {
                list1.forEach(fence2 -> {
                    if (fence2.getFenceCode().equals(fence.getFenceCode())) {
                        fence.setType("queryFence");
                    }
                });
            }

        });
        return list;
    }

    /**
     * 根据名称查询指定的围栏
     * @param fence
     * @return
     */
    private void setPoints(ElectronicFence fence) {
        LambdaQueryWrapper<FencePoints> lqw = Wrappers.lambdaQuery();
        lqw.eq(FencePoints::getFenceCode,fence.getFenceCode());
        List<FencePoints> points = this.list(lqw);
        fence.setPoints(points);

    }

    private TrackService getTrackService(String cityCode){
        LambdaQueryWrapper<TrackService> lqw = Wrappers.lambdaQuery();
        lqw.eq(TrackService::getServiceCity,cityCode);
        lqw.eq(TrackService::getDataLevel,1);
        TrackService trackService = iTrackServiceService.getOne(lqw);
        if(trackService==null){
            throw new ApiException("9999",new Object[] { cityCode },"未查询到相应的轨迹服务配置");
        }
        return trackService;
    }

    @Override
    public int saveOrUpdateFence(ElectronicFence fence) {
        if(StringUtils.isBlank(fence.getFenceCode())){
            String code = "gd_"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
            fence.setFenceCode(code);
            if("1".equals(fence.getFenceType())){
                return  this.saveDistrictFence(fence,"saveDistrictFence");
            }else{
                return  this.saveByFence(fence,"saveFence");
            }
        }else{
            //根据编码查询查询到则修改，未查询到则新增
            ElectronicFence fence1 = iElectronicFenceService.getByFenceCode(fence.getFenceCode());
            this.setPoints(fence1);
            if(fence1.equals(fence)){//未修改则直接返回，修改了则保存当前围栏对象
                return 1;
            }
            return this.updateByFence(fence,"updateFence");
        }

    }

    @Override
    public int updateByCode(ElectronicFence fence) {
        if(iElectronicFenceService.updateById(fence)){
            return iElectronicFenceService.syncFence(fence,1)?1:0;
        }
        return 0;
    }

    private int saveByFence(ElectronicFence fence, String apiCode) {
        TrackService trackService = this.getTrackService(fence.getCityCode());
        //先分配服务ID再调接口再保存到本地入库
        FenceVo vo = new FenceVo();
        vo.setName(fence.getFenceName());
        vo.setSid(trackService.getServiceId());
        vo.setKey(trackService.getGaodeKey());
        List<FencePoints> pointsList = fence.getPoints();
        pointsList.forEach(point ->{
            point.setFenceCode(fence.getFenceCode());
        });
        List<String> points= pointsList.stream().map(fencePoints->{return fencePoints.getPoints();}).collect(Collectors.toList());
        vo.setPoints(String.join(";",points));
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                String gfid = dataMap.get("gfid").toString();
                fence.setGdId(gfid);
                //将平台gfid更新到数据库中并在服务配置中加1
                iTrackServiceService.addFenceNum(trackService);
                //保存坐标数据
                this.saveBatch(pointsList);
                fence.setCreateor(SecurityUtils.getUsername());
                fence.setCreateorId(SecurityUtils.getUserId());
                return iElectronicFenceService.syncFence(fence,1)? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }
    }

    private int updateByFence(ElectronicFence electronicFence, String apiCode) {
        TrackService trackService = this.getTrackService(electronicFence.getCityCode());
        FenceVo vo = new FenceVo();
        vo.setName(electronicFence.getFenceName());
        vo.setSid(trackService.getServiceId());
        vo.setGfid(electronicFence.getGdId());
        vo.setKey(trackService.getGaodeKey());
        List<FencePoints> pointsList = electronicFence.getPoints();
        List<String> points= pointsList.stream().map(fencePoints->{return fencePoints.getPoints();}).collect(Collectors.toList());
        vo.setPoints(String.join(";",points));
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                //更新本地数据
                return this.updateBatchByFence(pointsList,electronicFence.getFenceCode())?1:0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }
    }

    private boolean updateBatchByFence(List<FencePoints> points,String fenceCode){
        Map<String,Object> objectMap = new HashMap<>();
        objectMap.put("fence_code",fenceCode);
        if(this.removeByMap(objectMap)){
            points.forEach(item ->{
                item.setFenceCode(fenceCode);
            });
            return this.saveBatch(points);
        }
        return false;
    }

    @Override
    public int saveDistrictFence(ElectronicFence fence, String apiCode) {
        TrackService trackService = this.getTrackService(fence.getCityCode());
        Map<String,Object> paramer = new HashMap<>();
        paramer.put("key",trackService.getGaodeKey());
        paramer.put("sid",trackService.getServiceId());
        paramer.put("name",fence.getFenceName());
        paramer.put("desc",fence.getFenceDesc());
        paramer.put("adcode",fence.getAdcode());
        List<FencePoints> pointsList = fence.getPoints();
        pointsList.forEach(point ->{
            point.setFenceCode(fence.getFenceCode());
        });
        Map resultMap = iApiService.executeByApiCode(apiCode,paramer);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                //将平台gfid更新到数据库中并在服务配置中加1
                iTrackServiceService.addFenceNum(trackService);
                //保存坐标数据
                this.saveBatch(pointsList);
                String gfid = dataMap.get("gfid").toString();
                fence.setGdId(gfid);
                fence.setCreateor(SecurityUtils.getUsername());
                fence.setCreateorId(SecurityUtils.getUserId());
                return iElectronicFenceService.syncFence(fence,1)? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }
    }

    @Override
    public AjaxResult getFenceByLocation(AddressVo addressVo) {
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String ,Object> map = new HashMap<>();
        try {
            String address = URLEncoder.encode(addressVo.getAddress(),"utf-8");
            map.put("address",address);
        } catch (UnsupportedEncodingException e) {
            return AjaxResult.error(9999,"系统异常");
        }
        map.put("city","");
        /*TrackService trackService = this.getTrackService(addressVo.getCityName());*/
        map.put("key","a33e36c0e15b373a2ebd7fc4aa1ec0c2");
        Map resultMap = iApiService.executeByApiCode("geocode", map);
        if (resultMap == null) {
            return AjaxResult.error(404,"获取地址坐标请求连接异常");
        } else {
            if (Integer.valueOf(resultMap.get("status").toString()) == 1) {//成功
                //获取地址坐标数据
                List<Map<String, Object>> geocodes = (List<Map<String, Object>>) resultMap.get("geocodes");
                if(geocodes==null||geocodes.size()==0){
                    return AjaxResult.error(408,"无法解析地址信息");
                }
                Map<String, Object> geocode = geocodes.get(0);
                String location = geocode.get("location").toString();
                String province = geocode.get("province").toString();
                if(geocode.get("city")==null){
                    return AjaxResult.error(407,"根据地址信息无法定位城市");
                }
                String city = geocode.get("city").toString();
                String cityCode = geocode.get("citycode").toString();
                TrackService trackService = this.getTrackService(cityCode);
                String district = geocode.get("district").toString();
                String street = geocode.get("street").toString();
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
                        results.forEach(result->{
                            if(result.get("in").toString().equals("1")){
                                //根据地址信息查询地址坐标
                                Map<String,Object> resultJoson = new HashMap<>();
                                String gfid = result.get("gfid").toString();
                                LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
                                lqw.eq(ElectronicFence::getGdId,gfid);
                                lqw.eq(ElectronicFence::getFencePop,addressVo.getAddressPop());
                                List<ElectronicFence> fences = iElectronicFenceService.list(lqw);
                                if(fences!=null&&fences.size()>0){
                                    resultJoson.put("fenceName",fences.get(0).getFenceName());
                                    resultJoson.put("fenceCode",fences.get(0).getFenceCode());
                                    resultJoson.put("province",province);
                                    resultJoson.put("district",district);
                                    resultJoson.put("city",city);
                                    resultJoson.put("street",street);
                                    list.add(resultJoson);
                                }
                            }
                        });
                        if(list.size()>1){
                            return AjaxResult.error(203,"该地址落在多个围栏中");
                        }
                        if(list.size() == 0){
                            return AjaxResult.error(201,"该地址未在围栏中");
                        }
                        return AjaxResult.success("成功",list.get(0));
                    }else{
                        return AjaxResult.error(406,"根据地址获取围栏接口异常："+resultMap2.get("errdetail"));
                    }
                }
            } else {
                return AjaxResult.error(408,"获取地址坐标接口异常："+resultMap.get("info").toString());
            }

        }
    }

    @Override
    public List<Map<String,Object>> getDistrictInfo(ElectronicFence fence, String apiCode) {
        TrackService trackService = this.getTrackService(fence.getCityCode());
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("keywords",fence.getCity());
        requestMap.put("subdistrict",1);
        //requestMap.put("filter",fence.getCityCode());
        requestMap.put("extensions","base");
        requestMap.put("key",trackService.getGaodeKey());
        Map resultMap = iApiService.executeByApiCode(apiCode,requestMap);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("infocode").toString())==10000) {//创建成功
                //更新坐标数据
                List<Object> citys = (List<Object>) resultMap.get("districts");
                Map<String,Object> city = (Map<String, Object>) citys.get(0);
                List<Map<String,Object>> districts = (List<Map<String,Object>>) city.get("districts");
                return districts;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("infocode").toString()},
                        "接口调用异常："+resultMap.get("info").toString());
            }
        }

    }

    @Override
    public boolean checkFenceName(ElectronicFence electronicFence) {

        return iElectronicFenceService.checkFenceName(electronicFence);
    }

    @Override
    public ElectronicFence districtFence(ElectronicFence electronicFence, String apiCode) {
        Map<String,Object> querstMap = new HashMap<>();
        querstMap.put("keywords",electronicFence.getAdcodeName());
        querstMap.put("subdistrict",1);
        querstMap.put("filter",electronicFence.getAdcode());
        querstMap.put("extensions","all");
        TrackService trackService = this.getTrackService(electronicFence.getCityCode());
        List<FencePoints> list = new ArrayList<>();
        querstMap.put("key",trackService.getGaodeKey());
        Map resultMap = iApiService.executeByApiCode(apiCode,querstMap);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("infocode").toString())==10000) {//创建成功
                //更新坐标数据
                List<Object> districts = (List<Object>) resultMap.get("districts");
                Map<String,Object> district = (Map<String, Object>) districts.get(0);
                String polyline = district.get("polyline").toString();
                if(polyline.contains("\\|")){
                    throw new ApiException("500",new Object[]{electronicFence.getAdcode()},
                            "该区域存在多个片区不支持创建区域围栏");
                }
                for (String s1 : polyline.split(";")){
                    String[] points = s1.split(",");
                    FencePoints fencePoints = new FencePoints();
                    fencePoints.setPointX(points[0]);
                    fencePoints.setPointY(points[1]);
                    list.add(fencePoints);
                }
                electronicFence.setPoints(list);
                return electronicFence;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("infocode").toString()},
                        "接口调用异常："+resultMap.get("info").toString());
            }
        }
    }


    @Override
    public boolean removeByCode(String fenceCode,String apiCode) {
        ElectronicFence electronicFence = iElectronicFenceService.getByFenceCode(fenceCode);
        Map<String,String> map = new HashMap<>();
        map.put("gfids",electronicFence.getGdId());
        TrackService trackService = this.getTrackService(electronicFence.getCityCode());
        map.put("sid",trackService.getServiceId());
        map.put("key",trackService.getGaodeKey());
        Map resultMap = iApiService.executeByApiCode(apiCode,map);
        if(resultMap == null){
            throw new ApiException("500",new Object[] { apiCode },"http请求连接异常");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==10000) {//创建成功
                //删除本地数据
                return iElectronicFenceService.syncFence(electronicFence,0);
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errdetail").toString());
            }
        }

    }



}
