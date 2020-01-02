package com.basic.delay.trigger.simple;

import com.basic.delay.DelayContext;
import com.basic.delay.DelayHandler;
import com.basic.delay.DelayTrigger;
import com.basic.delay.bean.DelayExecContext;
import com.basic.delay.bean.DelayMemberConfig;
import com.basic.delay.bean.DelayPersistedMessage;
import com.basic.delay.bean.DelayReq;
import com.basic.delay.util.DelayAsst;
import com.basic.delay.util.DelayTriggerTemplate;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;

public class SimpleDelayTrigger implements DelayTrigger, InitializingBean {

    static Logger log = LoggerFactory.getLogger(SimpleDelayTrigger.class);

    private int queueMaxSize;
    private DelayTriggerTemplate triggerTemplate;
    private ThreadPoolTaskExecutor consumeTaskExecutor;

    private SimpleDelayFactory simpleDelayFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.triggerTemplate, this.getClass().getSimpleName() + " triggerTemplate cannot be null");
        if (queueMaxSize <= 0) {
            queueMaxSize = 1000;
        }
        simpleDelayFactory = new SimpleDelayFactory(queueMaxSize, consumeTaskExecutor, triggerTemplate);
    }

    @Override
    public void add(DelayReq delayReq) {
        log.info("add {}", delayReq);
        if (DelayContext.isRepeatAdd(delayReq)) {
            log.info("detect RepeatAdd,return:");
            return;
        }
        DelayAsst asst = triggerTemplate.getDelayAsst();
        if (asst.isNeedPersist(delayReq)) {
            DelayPersistedMessage dmp = asst.persist(delayReq);
            if (dmp == null) {
                log.info("persist ret is null.");
                return;
            }
        }
        int execTimes = 0;
        DelayAsst delayAsst = triggerTemplate.getDelayAsst();
        DelayMemberConfig memberConfig = delayAsst.getDelayMemberConfig(delayReq.getBizType());
        log.info("memberConfig {}", memberConfig);
        Date nextExecTime = null;

        if (delayReq.getFistExecTime() != null) {
            nextExecTime = delayReq.getFistExecTime();
        } else {
            nextExecTime = delayAsst.getNextExecTime(execTimes, memberConfig);
        }
        DateUtils.truncate(nextExecTime, Calendar.SECOND);
        //        String humanNextTime = DateUtil.formatCompactDateTime(nextExecTime);
//        log.info("nextExecTime {}", humanNextTime);

        SimpleDelayBean delayBean = new SimpleDelayBean();
        delayBean.setTriggerTime(nextExecTime);
        delayBean.setBizType(delayReq.getValue());
        delayBean.setExecTimes(execTimes);
        delayBean.setBizType(delayReq.getBizType());
        delayBean.setHandlerClass(delayReq.getHanderClass());
        delayBean.setFirstExecTime(nextExecTime);

        simpleDelayFactory.add(delayBean);
    }

    @Override
    public void addPersistedMessage(DelayPersistedMessage dpm) {
        DelayHandler delayHandler = triggerTemplate.getDelayAsst().getDelayHandler(dpm.getBizType());

        DelayExecContext context = new DelayExecContext();
        context.setBizType(dpm.getBizType());
        context.setBizValue(dpm.getBizValue());
        context.setExecTimes(dpm.getExecTimes());
        context.setFirstExecTime(dpm.getFirstExecTime());
        context.setHandlerClass(delayHandler.getClass());

        DelayMemberConfig memberConfig = triggerTemplate.getDelayAsst().getDelayMemberConfig(dpm.getBizType());
        context.setMemberConfig(memberConfig);
        context.setStartExecTime(new Date());

        Date nextExecTime = DateUtils.truncate(dpm.getNextExecTime(), Calendar.SECOND);
        simpleDelayFactory.retryAdd(context, nextExecTime, false);
    }

    public void setQueueMaxSize(int queueMaxSize) {
        this.queueMaxSize = queueMaxSize;
    }

    public void setTriggerTemplate(DelayTriggerTemplate triggerTemplate) {
        this.triggerTemplate = triggerTemplate;
    }

    public void setConsumeTaskExecutor(ThreadPoolTaskExecutor consumeTaskExecutor) {
        this.consumeTaskExecutor = consumeTaskExecutor;
    }

    public void cleanQueue() {
        this.simpleDelayFactory.getQueue().manualClear();
    }

    public void printQueue() {
        this.simpleDelayFactory.getQueue().printAll();
    }

}
