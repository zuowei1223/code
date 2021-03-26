package com.tcoiss.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.redis.service.RedisService;
import com.tcoiss.webservice.domain.*;
import com.tcoiss.webservice.mapper.ElectronicFenceMapper;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IElectronicFenceService;
import com.tcoiss.webservice.service.IFencePointsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 电子围栏Service业务层处理
 *
 * @author zw
 * @date 2021-01-31
 */
@Service
public class ElectronicFenceServiceImpl extends ServiceImpl<ElectronicFenceMapper, ElectronicFence> implements IElectronicFenceService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private IApiService iApiService;

    @Override
    public List<ElectronicFence> queryList(ElectronicFence electronicFence) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (electronicFence.getFenceCode() != null){
            lqw.eq(ElectronicFence::getFenceCode ,electronicFence.getFenceCode());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceName())){
            lqw.eq(ElectronicFence::getFenceName ,electronicFence.getFenceName());
        }
        if (StringUtils.isNotBlank(electronicFence.getCityCode())){
            lqw.eq(ElectronicFence::getCityCode ,electronicFence.getCityCode());
        }
        if (electronicFence.getCreateorId() != null){
            lqw.eq(ElectronicFence::getCreateorId ,electronicFence.getCreateorId());
        }
        lqw.eq(ElectronicFence::getFencePop ,electronicFence.getFencePop());
        List<ElectronicFence> list = this.list(lqw);

        return list;
    }

    @Override
    public ElectronicFence getByFenceCode(String fenceCode) {
        LambdaQueryWrapper<ElectronicFence> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(fenceCode)){
            lqw.eq(ElectronicFence::getFenceCode ,fenceCode);
        }
        List<ElectronicFence> list = this.list(lqw);
        if(list==null||list.size()==0){
            return null;
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

    /**
     * 上传到苍穹后在保存的本地
     * @param electronicFence
     * @return
     */
    @Override
    public boolean syncFence(ElectronicFence electronicFence,int enable) {
        //查看缓存是否存在token,存在则取，不存在则接口获取
        String accessToken = redisService.getCacheObject("kd_access_token");
        if(StringUtils.isEmpty(accessToken)){
            accessToken = iApiService.getKdAccessToken();
        }
        Map<String,Object> map = new HashMap<>();
        map.put("fenceCode",electronicFence.getFenceCode());
        map.put("fenceName",electronicFence.getFenceName());
        map.put("typeflag",","+electronicFence.getFencePop()+",");
        map.put("enable",enable);
        Map<String,Object> resultMap = iApiService.executeKdByApiCode("syncKdFence",map,accessToken);
        if(resultMap.get("errorCode").equals("success")){
            if(enable==1) {
                return this.saveOrUpdate(electronicFence);
            }else{
                //删除围栏地图数据
                return this.removeById(electronicFence.getId());
            }
        }else{
            throw new ApiException("500",new Object[]{"syncKdFence"},
                    "同步苍穹围栏失败");
        }
    }



}
