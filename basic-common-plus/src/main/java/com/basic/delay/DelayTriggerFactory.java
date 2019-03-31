package com.basic.delay;

import java.util.HashMap;
import java.util.Map;

public class DelayTriggerFactory {

    private static final String SIMPLE = "simple";
    /**private static final String REDIS_ZSET = "redisZSet";
    private static final String DEFAULT_DELAY_TRIGGER = "DEFAULT_DELAY_TRIGGER";*/
private Map<String, DelayTrigger> triggerMap = new HashMap<String, DelayTrigger>();

    public DelayTrigger getSimpleTrigger() {
        return getTrigger(SIMPLE);
    }

    public DelayTrigger getTrigger(String triggerType) {
        if (triggerMap.isEmpty()) {
            return null;
        }
        if (triggerType == null) {
            triggerType = SIMPLE;
        }
        DelayTrigger trigger = null;
        if ((trigger = triggerMap.get(triggerType)) == null) {
            return triggerMap.get(SIMPLE);
        }
        return trigger;
    }

    public void setTriggerMap(Map<String, DelayTrigger> triggerMap) {
        this.triggerMap = triggerMap;
    }
}
