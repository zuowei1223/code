package com.tcoiss.datafactory.service;

import com.tcoiss.datafactory.domain.ExecuteWork;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * 作业Service接口
 *
 * @author zw
 * @date 2021-04-26
 */
public interface IExecuteWorkService extends IService<ExecuteWork> {

    /**
     * 查询列表
     */
    List<ExecuteWork> queryList(ExecuteWork executeWork);

    /**
     * 根据方案ID查询作业列表
     */
    List<ExecuteWork> getWorksBySchemeId(Long schemeId);

    void executeWork(ExecuteWork work);
}
