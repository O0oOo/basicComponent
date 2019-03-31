package com.basic.delay;

import com.basic.delay.bean.*;

import java.util.Date;
import java.util.List;

public interface DelayMemberPersister {

    DelayPersistedMessage persist(DelayReq delayReq, Date firstRunTime, DelayMemberConfig memberConfig);

    DelayPersistedMessage get(DelayExecContext execContext);

    ExecLockResult execLock(DelayExecContext execContext, DelayPersistedMessage message);

    boolean execError(DelayExecContext execContext, DelayPersistedMessage message, Date nextRunTime, String bizRespMemo);

    boolean execSucc(DelayExecContext execContext, DelayPersistedMessage message, String bizRespMemo);

    boolean execFail(DelayExecContext execContext, DelayPersistedMessage message, Date nextRunTime, String bizRespMemo);

    /**
     * @param logBean * @param execContext * @return
     */
    boolean insertExecLog(DelayLogBean logBean, DelayExecContext execContext);

    List<DelayLogBean> queryLatestExceLog(DelayExecContext execContext);

}
