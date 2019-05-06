package com.basic.delay.util;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

@Getter
@Setter
public class DelayTriggerTemplate implements InitializingBean {

    static Logger log = LoggerFactory.getLogger(DelayTriggerTemplate.class);

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
