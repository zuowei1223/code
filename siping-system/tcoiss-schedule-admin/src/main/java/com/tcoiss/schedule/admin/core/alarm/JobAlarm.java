package com.tcoiss.schedule.admin.core.alarm;

import com.tcoiss.schedule.admin.core.model.XxlJobInfo;
import com.tcoiss.schedule.admin.core.model.XxlJobLog;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    /**
     * test alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog);

}
