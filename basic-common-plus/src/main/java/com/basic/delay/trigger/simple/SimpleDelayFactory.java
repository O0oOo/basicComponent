package com.basic.delay.trigger.simple;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Data
public class SimpleDelayFactory {

    static Logger log = LoggerFactory.getLogger(SimpleDelayFactory.class);

    private int queueMaxSize = 50000;
    private ThreadPoolTaskExecutor consumeTaskExecutor;
//    private DelayTriggerTem

}
