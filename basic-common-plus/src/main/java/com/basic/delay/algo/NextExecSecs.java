package com.basic.delay.algo;

import com.basic.delay.bean.DelayMemberConfig;

public interface NextExecSecs {
    /**
     * @param nextExecTime base on 1. * @param memberConfig * @return
     */
    int calcuNextSecs(int nextExecTime, DelayMemberConfig memberConfig);
}
