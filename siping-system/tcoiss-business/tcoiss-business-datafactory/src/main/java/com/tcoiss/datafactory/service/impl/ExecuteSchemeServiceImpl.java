package com.tcoiss.datafactory.service.impl;

import com.tcoiss.common.core.domain.R;
import com.tcoiss.common.core.exception.datafactory.DataException;
import com.tcoiss.datafactory.api.model.SchemeVO;
import com.tcoiss.datafactory.domain.ExecuteWork;
import com.tcoiss.datafactory.service.IExecuteWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import com.tcoiss.datafactory.mapper.ExecuteSchemeMapper;
import com.tcoiss.datafactory.domain.ExecuteScheme;
import com.tcoiss.datafactory.service.IExecuteSchemeService;

import java.util.List;
import java.util.Map;

/**
 * 执行方案Service业务层处理
 *
 * @author zw
 * @date 2021-04-26
 */
@Service
public class ExecuteSchemeServiceImpl extends ServiceImpl<ExecuteSchemeMapper, ExecuteScheme> implements IExecuteSchemeService {

    @Autowired
    private IExecuteWorkService iExecuteWorkService;

    @Override
    public List<ExecuteScheme> queryList(ExecuteScheme executeScheme) {
        LambdaQueryWrapper<ExecuteScheme> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(executeScheme.getSchemeName())){
            lqw.like(ExecuteScheme::getSchemeName ,executeScheme.getSchemeName());
        }
        if (executeScheme.getExecuteType() != null){
            lqw.eq(ExecuteScheme::getExecuteType ,executeScheme.getExecuteType());
        }
        return this.list(lqw);
    }

    @Override
    public R execute(SchemeVO vo) {
        //查询执行方案
        LambdaQueryWrapper<ExecuteScheme> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(vo.getExecuteNumber())){
            lqw.like(ExecuteScheme::getExecuteNumber ,vo.getExecuteNumber());
        }
        if (vo.getExecuteType() != null){
            lqw.eq(ExecuteScheme::getExecuteType ,vo.getExecuteType());
        }
        List<ExecuteScheme> schemes = this.list(lqw);
        if(schemes==null||schemes.size()==0){
            return R.fail(401,"未查询到相应的执行方案");
        }
        if(schemes.size()>1){
            return R.fail(402,"存在多个执行方案");
        }
        ExecuteScheme scheme = schemes.get(0);
        //根据执行方案查询作业
        List<ExecuteWork> works = iExecuteWorkService.getWorksBySchemeId(scheme.getSchemeId());
        if(works==null||works.size()==0){
            return R.fail(403,"方案: "+scheme.getSchemeName()+"下未查询到可执行的作业");
        }
        // todo 根据执行策略，执行作业 1.串行 2.并行 3.异步
        switch (scheme.getExecuteStrategy()){
            case 1:

        }
        return R.ok();
    }

    private void executeWork(ExecuteWork executeWork){

    }

}
