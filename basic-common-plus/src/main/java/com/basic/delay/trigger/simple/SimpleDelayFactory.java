package com.basic.delay.trigger.simple;

import com.basic.delay.bean.DelayExecContext;
import com.basic.delay.bean.DelayMemberConfig;
import com.basic.delay.util.DelayAsst;
import com.basic.delay.util.DelayTriggerCallback;
import com.basic.delay.util.DelayTriggerTemplate;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Date;

@Data
public class SimpleDelayFactory {

    static Logger log = LoggerFactory.getLogger(SimpleDelayFactory.class);

    private int queueMaxSize = 50000;
    private ThreadPoolTaskExecutor consumeTaskExecutor;
    private DelayTriggerTemplate triggerTemplate;
    private AbstractSimpleDelayQueue queue = null;

    public SimpleDelayFactory(int queueMaxSize, final ThreadPoolTaskExecutor consumeTaskExecutor,
                              DelayTriggerTemplate triggerTemplate) {
        super();
        this.queueMaxSize = queueMaxSize;
        this.consumeTaskExecutor = consumeTaskExecutor;
        this.triggerTemplate = triggerTemplate;

        queue = new AbstractSimpleDelayQueue(queueMaxSize) {
            @Override
            public void consume(final SimpleDelayBean delayBean) {
                asynConsume(delayBean);
            }
        };

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                consumeTaskExecutor.shutdown();
            }
        }));
        queue.start();
    }

    private void asynConsume(final SimpleDelayBean delayBean) {
        consumeTaskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                consumeTask(delayBean);
            }
        });
    }

    private void consumeTask(SimpleDelayBean delayBean) {
        final int execTimes = delayBean.getExecTimes() + 1;
        DelayAsst delayAsst = triggerTemplate.getDelayAsst();
        DelayMemberConfig config = delayAsst.getDelayMemberConfig(delayBean.getBizType());

        final DelayExecContext execContext = new DelayExecContext();
        execContext.setBizType(delayBean.getBizType());
        execContext.setBizValue(delayBean.getBizValue());
        execContext.setExecTimes(execTimes);
        execContext.setMemberConfig(config);
        execContext.setStartExecTime(new Date());

        execContext.setFirstExecTime(delayBean.getFirstExecTime());
        execContext.setHandlerClass(delayBean.getHandlerClass());
        execContext.setLatestExecLogs(triggerTemplate.getDelayAsst().getLatestExecLog(execContext));

        triggerTemplate.exec(execContext, new DelayTriggerCallback() {
            @Override
            public void execLockFail() {

            }

            @Override
            public void handleSucc() {
                log.info("delayHandler success");
            }

            @Override
            public void handleFail() {
                log.info("delayHanlder fail");
                if (execTimes < config.getMaxExecTimes()) {
                    retryAdd(execContext, null, false);
                } else {
                    log.info("over times maxExecTimes={},execTimes={}", config.getMaxExecTimes(), execTimes);
                }
            }

            @Override
            public void handleError() {
                retryAdd(execContext, null, true);
            }
        });

    }

    /**
     * 重试加入
     *
     * @param execContext
     * @param nextExecTime 下次执行时间
     * @param lastError    上次是否错误
     */
    public void retryAdd(DelayExecContext execContext, Date nextExecTime, boolean lastError) {
        int execTimes = execContext.getExecTimes();
        DelayMemberConfig memberConfig = execContext.getMemberConfig();
        if (nextExecTime == null && lastError) {
//            if (memberConfig.isUnackUseExecTimes()) {
            if (execTimes >= memberConfig.getMaxExecTimes()) {
                log.info("over times maxExecTimes={},execTimes={}", memberConfig.getMaxExecTimes(), execTimes);
                return;
            }
//            } else {//未确认不占用次数
//                execTimes--;
//            }
        }

        log.info("retryAdd {}", execContext.str());
        DelayAsst delayAsst = triggerTemplate.getDelayAsst();
        log.info("delayMemberConfig {}", memberConfig);

        if (nextExecTime == null) {
            if (lastError) {
                Integer execMaxSecs = memberConfig.getExecNeedMaxSecs();
                log.info("error occurs. retry after 'execNeedMaxSecs' {}s", execMaxSecs);
                nextExecTime = DateUtils.addSeconds(new Date(), execMaxSecs);
            } else {
                nextExecTime = delayAsst.getNextExecTime(execTimes, memberConfig);
            }
        }

//        String humanNextTime = DateUtil.formatCompactDateTime(nextExecTime);
//        log.info("nextExecTime {}", humanNextTime);
        SimpleDelayBean delayBean = new SimpleDelayBean();
        delayBean.setTriggerTime(nextExecTime);
        delayBean.setBizValue(execContext.getBizValue());
        delayBean.setExecTimes(execTimes);
        delayBean.setBizType(execContext.getBizType());
        delayBean.setHandlerClass(execContext.getHandlerClass());
        delayBean.setFirstExecTime(execContext.getFirstExecTime());

        if (nextExecTime.compareTo(new Date()) <= 0) {
            log.info("重新添加任务执行时间在当前时间之前，直接提交线程池处理");
            asynConsume(delayBean);
        } else {
            queue.put(delayBean);
        }

    }

    public void add(SimpleDelayBean delayBean) {
        queue.put(delayBean);
    }

    public AbstractSimpleDelayQueue getQueue() {
        return this.queue;
    }
}
