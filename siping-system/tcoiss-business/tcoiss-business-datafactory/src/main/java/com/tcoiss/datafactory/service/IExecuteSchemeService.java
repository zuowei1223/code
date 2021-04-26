package com.tcoiss.datafactory.service;

import com.tcoiss.common.core.domain.R;
import com.tcoiss.datafactory.api.model.SchemeVO;
import com.tcoiss.datafactory.domain.ExecuteScheme;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 执行方案Service接口
 *
 * @author zw
 * @date 2021-04-26
 */
public interface IExecuteSchemeService extends IService<ExecuteScheme> {

    /**
     * 查询列表
     */
    List<ExecuteScheme> queryList(ExecuteScheme executeScheme);


    R execute(SchemeVO vo);
}
