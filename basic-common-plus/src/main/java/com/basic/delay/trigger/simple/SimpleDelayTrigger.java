package com.basic.delay.trigger.simple;

import com.basic.delay.DelayTrigger;
import com.basic.delay.bean.DelayPersistedMessage;
import com.basic.delay.bean.DelayReq;
import org.springframework.beans.factory.InitializingBean;

public class SimpleDelayTrigger implements DelayTrigger, InitializingBean {

    
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
