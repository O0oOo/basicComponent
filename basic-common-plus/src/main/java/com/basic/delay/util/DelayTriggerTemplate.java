package com.basic.delay.util;

import com.basic.delay.DelayContext;
import com.basic.delay.DelayHandler;
import com.basic.delay.DelayMemberPersister;
import com.basic.delay.bean.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Date;

@Getter
@Setter
public class DelayTriggerTemplate implements InitializingBean {

    private static Logger log = LoggerFactory.getLogger(DelayTriggerTemplate.class);

    private DelayAsst delayAsst;

    public void exec(DelayExecContext execContext, DelayTriggerCallback callback) {
        DelayHandleRet handleRet = null;
        Exception execException = null;
        try {
            handleRet = execTask(execContext, callback);
        } catch (Exception e) {
            log.error("execTask error", e);
            execException = e;
        } finally {
            trySaveExecLog(execContext, handleRet, execException);
        }
    }

    private DelayMemberPersister getMemberPersister(DelayExecContext execContext) {
        return execContext.getMemberConfig().isPersist() ? delayAsst.getMemberPersister() : null;
    }

    private DelayHandleRet execTask(DelayExecContext execContext, DelayTriggerCallback callback) {
        log.info("execTask  {} start", execContext.str());
        DelayHandler handler = delayAsst.getDelayHandler(execContext.getBizType());
        try {
            handler = delayAsst.getDelayHandler(execContext.getBizType());
        } catch (Exception e) {
            log.error("getHandler error, delete task", e.getMessage());
            callback.handleSucc();
            return DelayHandleRet.SUCC;
        }

        DelayMemberConfig memberConfig = execContext.getMemberConfig();
        //持久化
        DelayMemberPersister persister = getMemberPersister(execContext);
        DelayPersistedMessage dbRecord = null;
        if (persister != null) {
            dbRecord = persister.get(execContext);
        }

        DelayHandleRet ret = null;

        boolean needExecLock = memberConfig.isExecLock();
        if (needExecLock) {
            if (dbRecord == null) {
                log.info("查询记录为null，取消执行任务");
                callback.execLockFail();
                return ret;
            }
            if (dbRecord.getExecTimes() != execContext.getExecTimes() - 1) {
                log.info("记录执行次数是{} != 内存执行次数{},取消执行任务", dbRecord.getExecTimes(), execContext.getExecTimes() - 1);
                callback.execLockFail();
                return ret;
            }

            ExecLockResult lockRet = persister.execLock(execContext, dbRecord);
            switch (lockRet) {
                case LOCK_SUCC:
                    log.info("execLock succ");
                    ret = handle(handler, execContext);
                    handleResult(ret, execContext, dbRecord, callback);
                    break;
                case LOCKED:
                    log.info("execLock alreadyLocked,do nothing.");
                    callback.execLockFail();
                    break;
                case OVER:
                    log.info("execLock alreadyOver");
                    callback.handleSucc();
                    break;
                default:
                    break;
            }
        } else {
            log.info("execLock alreadyOver");
            callback.handleSucc();
            handleResult(ret, execContext, dbRecord, callback);
        }
        log.info("execTask {} end", execContext.str());
        return ret;
    }

    private DelayHandleRet handle(DelayHandler handler, DelayExecContext execContext) {
        DelayHandleRet ret = null;
        DelayContext.setTaskHolder(new DelayContext.TaskHolder(handler.getClass(), execContext.getBizValue()));
        try {
            ret = handler.handle(execContext);
        } catch (Exception e) {
            log.error("handle exeption", e);
        } finally {
            DelayContext.setTaskHolder(null);
        }
        return ret;
    }

    /**
     * 处理执行结果
     *
     * @param handleRet
     * @param execContext
     * @param message
     * @param callback
     */
    private void handleResult(DelayHandleRet handleRet, DelayExecContext execContext,
                              DelayPersistedMessage message, DelayTriggerCallback callback) {
        log.info("handleRet = {} ", handleRet);
        DelayMemberPersister persister = getMemberPersister(execContext);

        if (handleRet == null) {
            if (persister != null) {
                try {
                    int execMaxSecs = execContext.getMemberConfig().getExecNeedMaxSecs();//最大执行时间
                    Date next = DateUtils.addSeconds(new Date(), execMaxSecs);
                    persister.execError(execContext, message, next, null);
                } catch (Exception e) {
                    log.error("persister.execError error", e);
                }
            }
            callback.handleError();
        } else if (handleRet.isSuccess()) {
            if (persister != null) {
                try {
                    persister.execSucc(execContext, message, handleRet.getBizRespMemo());
                } catch (Exception e) {
                    log.error("persister.execSucc error", e);
                }
            }
            callback.handleSucc();
        } else {
            if (persister != null) {
                try {
                    Date next = delayAsst.getNextExecTime(execContext.getExecTimes(), execContext.getMemberConfig());
                    persister.execFail(execContext, message, next, handleRet.getBizRespMemo());
                } catch (Exception e) {
                    log.error("persister.execFail error", e);
                }
            }
            callback.handleFail();
        }
    }

    private void trySaveExecLog(DelayExecContext execContext, DelayHandleRet handleRet, Exception execException) {
        log.info("trySaveExecLog start handleRet={}, occureExeception={}", handleRet, execException != null);
        if (handleRet == null && execException == null) {
            log.info("ret is null but exception is null,cancel record log");
            return;
        }
        DelayMemberConfig memberConfig = execContext.getMemberConfig();
        if (memberConfig.isExecLog() && memberConfig.isPersist()) {
            log.info("save delayExecLog start");
            DelayLogBean logBean = new DelayLogBean();
            logBean.setBizType(memberConfig.getBizType());
            logBean.setBizValue(execContext.getBizValue());
            logBean.setExecEndTime(new Date());
            logBean.setExecStartTime(execContext.getStartExecTime());
            logBean.setExecTimes(Long.valueOf(execContext.getExecTimes()));
            if (execException != null) {
                logBean.setExecResult("E");
                logBean.setBizRespMemo(execException.getMessage());
            } else {
                logBean.setExecResult(handleRet.isSuccess() ? "S" : "F");
                logBean.setBizRespMemo(handleRet.getBizRespMemo());
            }
            try {
                delayAsst.getMemberPersister().insertExecLog(logBean, execContext);
            } catch (Exception e) {
                log.error("insertLog error", e);
            }
            log.info("save delayExecLog end");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.delayAsst, this.getClass().getSimpleName() + " delayAsst cannot be null");
    }
}
