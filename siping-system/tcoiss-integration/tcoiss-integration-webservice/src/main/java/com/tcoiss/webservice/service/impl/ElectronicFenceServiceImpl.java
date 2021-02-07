package com.tcoiss.webservice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.common.core.exception.api.ApiException;
import com.tcoiss.common.core.web.domain.AjaxResult;
import com.tcoiss.webservice.ApiServer.HttpAPIServer;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FenceVo;
import com.tcoiss.webservice.mapper.ElectronicFenceMapper;
import com.tcoiss.webservice.service.IApiService;
import com.tcoiss.webservice.service.IElectronicFenceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        if (StringUtils.isNotBlank(electronicFence.getFencePoints())){
            lqw.eq(ElectronicFence::getFencePoints ,electronicFence.getFencePoints());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceEnable())){
            lqw.eq(ElectronicFence::getFenceEnable ,electronicFence.getFenceEnable());
        }
        if (StringUtils.isNotBlank(electronicFence.getValidTime())){
            lqw.eq(ElectronicFence::getValidTime ,electronicFence.getValidTime());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceRepeat())){
            lqw.eq(ElectronicFence::getFenceRepeat ,electronicFence.getFenceRepeat());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceTime())){
            lqw.eq(ElectronicFence::getFenceTime ,electronicFence.getFenceTime());
        }
        if (StringUtils.isNotBlank(electronicFence.getFenceDesc())){
            lqw.eq(ElectronicFence::getFenceDesc ,electronicFence.getFenceDesc());
        }
        if (StringUtils.isNotBlank(electronicFence.getAlertCondition())){
            lqw.eq(ElectronicFence::getAlertCondition ,electronicFence.getAlertCondition());
        }
        if (electronicFence.getCreateorId() != null){
            lqw.eq(ElectronicFence::getCreateorId ,electronicFence.getCreateorId());
        }
        return this.list(lqw);
    }

    @Override
    public int saveFence(ElectronicFence fence, String apiCode) {
        //先调接口再保存到本地入库
        FenceVo vo = new FenceVo();
        vo.setName(fence.getFenceName());
        vo.setPoints(fence.getFencePoints());
        vo.setRepeat(fence.getFenceRepeat());
        vo.setValid_time(fence.getValidTime());
        vo.setAlert_condition(fence.getAlertCondition());
        Map resultMap = iApiService.executeByApiCode(apiCode,vo);
        if(resultMap == null){
            throw new ApiException("code404",new Object[] { apiCode },"http请求连接异常");
            //return AjaxResult.error("连接异常,请检查地址是否正确");
        }else{
            if(Integer.valueOf(resultMap.get("errcode").toString())==0) {//创建成功
                Map<String, Object> dataMap = JSON.parseObject(resultMap.get("data").toString());
                String status = dataMap.get("status").toString();
                if(Integer.valueOf(status) != 0){
                    throw new ApiException("9999",new Object[]{apiCode,status},"接口调用异常："+dataMap.get("message").toString());
                }
                fence.setFenceGid(dataMap.get("gid").toString());
                //将平台gid更新到数据库中
                return this.save(fence) ? 1 : 0;
            }else{
                throw new ApiException("9999",new Object[]{apiCode,resultMap.get("errcode").toString()},
                        "接口调用异常："+resultMap.get("errmsg").toString());
            }
        }

    }


}
