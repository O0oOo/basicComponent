package com.basic.delay;

import com.basic.delay.bean.DelayMemberConfig;

public interface DelayMemberConfigurer {

    DelayMemberConfig getByBizType(String bizType);

}
