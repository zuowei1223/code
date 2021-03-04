package com.tcoiss.webservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcoiss.webservice.domain.ElectronicFence;
import com.tcoiss.webservice.domain.FencePoints;
import com.tcoiss.webservice.mapper.FencePointsMapper;
import com.tcoiss.webservice.service.IFencePointsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 轨迹服务配置Service业务层处理
 *
 * @author zw
 * @date 2021-02-21
 */
@Service
public class FencePointsServiceImpl extends ServiceImpl<FencePointsMapper, FencePoints> implements IFencePointsService {


    @Override
    public List<FencePoints> queryListByFenceId(String fenceId) {
        LambdaQueryWrapper<FencePoints> lqw = Wrappers.lambdaQuery();
        lqw.eq(FencePoints::getFenceId,fenceId);
        return this.list(lqw);
    }

    @Override
    public boolean deleteByFenceId(String fenceId) {
        List<FencePoints> fencePointsList = this.queryListByFenceId(fenceId);
        List<Long> ids = fencePointsList.stream().map(fencePoints->{return fencePoints.getId();}).collect(Collectors.toList());
        return this.removeByIds(ids);
    }

    @Override
    public boolean updateBatch(ElectronicFence electronicFence, List<FencePoints> coordinate) {
        //先删除坐标集，再重新保存
        this.deleteByFenceId(electronicFence.getLocalKey());
        return this.saveBatch(coordinate);
    }


}
