package com.basic.delay;

import com.basic.delay.bean.DelayPersistedMessage;
import com.basic.delay.bean.DelayReq;

public interface DelayTrigger {

    /**
     *
     * @param delayReq 添加到超时对象。注意最大重试次数：当未出现确认或者handler明确返回失败的情况，后台会重新发起。默认最大重试次数为1
     */
    public void add(DelayReq delayReq);

    /**
     * 补偿调度使用，再次加入持久化的message
     * @param message
     */
    public void addPersistedMessage(DelayPersistedMessage message);

}
