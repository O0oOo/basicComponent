package com.basic.delay.util;

import com.basic.delay.DelayHandler;
import com.basic.delay.DelayMemberConfigurer;
import com.basic.delay.DelayMemberPersister;
import com.basic.delay.algo.NextExecTimeAlgo;
import com.basic.delay.bean.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
public class DelayAsst implements ApplicationContextAware {

    static Logger log = LoggerFactory.getLogger(DelayAsst.class);

    private DelayMemberConfigurer memberConfigurer;
    private DelayMemberPersister memberPersister;

    private ApplicationContext applicationContext;

    public boolean isNeedPersist(DelayReq delayReq) {
        DelayMemberConfig memberConfig = getDelayMemberConfig(delayReq.getBizType());
        if (memberConfig == null) {
            String message = "missing memberConfig for" + delayReq.getBizType();
        }
        return memberConfig.isPersist();
    }

    public DelayPersistedMessage persist(DelayReq delayReq) {
        DelayMemberConfig memberConfig = getDelayMemberConfig(delayReq.getBizType()); /* *最大执行次数校验，如果配置小于等于0，则取消执行delay操作! */
        if (memberConfig.getMaxExecTimes() <= 0) {
            log.info("memberConfig maxExecTimes<=0,cancal job.");
            return null;
        }
        if (memberPersister == null) {
            String message = "missing delayPersister for " + delayReq.getBizType();
//            logCat(message);
            throw new RuntimeException(message);
        }
        DelayPersistedMessage ret = null;
        Date firstExecTime = getFirstExecTime(delayReq);
        try {
            ret = memberPersister.persist(delayReq, firstExecTime, memberConfig);
        } catch (Exception e) {
            log.error("persist error", e);
        }
        log.info("persist result {}", ret);
        if (ret == null) {
//            logCat("persister result false" + delayReq.getValue());
        }
        return ret;
    }

    private Date getFirstExecTime(DelayReq delayReq) {
        Date firstExecTime = delayReq.getFistExecTime();
        if (firstExecTime == null) {
            DelayMemberConfig memberConfig = getDelayMemberConfig(delayReq.getBizType());
            firstExecTime = NextExecTimeAlgo.nextTime(0, memberConfig);
            delayReq.setFistExecTime(firstExecTime);
        }
        return delayReq.getFistExecTime();
    }

    public Date getNextExecTime(int execTimes, String bizType) {
        return NextExecTimeAlgo.nextTime(execTimes, getDelayMemberConfig(bizType));
    }

    public Date getNextExecTime(int execTimes, DelayMemberConfig memberConfig) {
        return NextExecTimeAlgo.nextTime(execTimes, memberConfig);
    }

    public DelayMemberConfig getDelayMemberConfig(String bizType) {
        DelayMemberConfig memberConfig = memberConfigurer.getByBizType(bizType);
        if (null == memberConfig) {
            return null;
        }
        if (memberConfig.isExecLock() && !memberConfig.isPersist()) {
            memberConfig.setPersist(true);
        }
        return memberConfig;
    }

    public DelayHandler getDelayHandler(String bizType) {
        DelayMemberConfig memberConfig = getDelayMemberConfig(bizType);
        return getDelayHandler(memberConfig);
    }

    private DelayHandler getDelayHandler(DelayMemberConfig memberConfig) {
        String bean = memberConfig.getDelayHandlerBean();
        try {
            return this.applicationContext.getBean(bean, DelayHandler.class);
        } catch (Exception e) {
            log.warn("未找到DelayHandler的beanId={}", bean);
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public DelayMemberPersister getMemberPersist() {
        return memberPersister;
    }

    public List<DelayLogBean> getLatestExecLog(DelayExecContext execContext) {
        List<DelayLogBean> list = new ArrayList<>();
        if (this.getMemberPersister() == null) {
            return list;
        }
        return this.getMemberPersister().queryLatestExceLog(execContext);
    }

}
