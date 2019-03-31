package com.basic.delay;

import com.basic.delay.bean.DelayExecContext;
import com.basic.delay.bean.DelayHandlerRet;

public interface DelayHandler {

    /**
     *
     * @param execContext 执行上下文
     * @return 返回DelayHandlerRet.success=true-消费成功；DelayHandlerRet.success=false-消费失败；抛出异常：消费异常
     */
    DelayHandlerRet handler(DelayExecContext execContext);

}
