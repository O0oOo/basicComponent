package com.basic.delay.trigger.simple;

import com.basic.delay.DelayTrigger;
import com.basic.delay.bean.DelayPersistedMessage;
import com.basic.delay.bean.DelayReq;
import com.basic.delay.util.DelayTriggerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class SimpleDelayTrigger implements DelayTrigger, InitializingBean {

    static Logger log = LoggerFactory.getLogger(SimpleDelayTrigger.class);

    private int queueMaxSize;
    private DelayTriggerTemplate triggerTemplate;
    private ThreadPoolTaskExecutor consumeTaskExecutor;

    private SimpleDelayFactory simpleDelayFactory;
    
    @Override
    public void add(DelayReq delayReq) {

    }

    @Override
    public void addPersistedMessage(DelayPersistedMessage message) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
