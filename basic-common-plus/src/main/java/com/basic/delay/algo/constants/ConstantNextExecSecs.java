package com.basic.delay.algo.constants;

import com.alibaba.fastjson.JSON;
import com.basic.delay.algo.NextExecSecs;
import com.basic.delay.bean.DelayMemberConfig;

public class ConstantNextExecSecs implements NextExecSecs {

    @Override
    public int calcuNextSecs(int nextExecTime, DelayMemberConfig memberConfig) {
        ConstantAlgoConfig config = JSON.parseObject(memberConfig.getNextExecTimeCfg(), ConstantAlgoConfig.class);
        //步长最小为1s
        int nextSecs = Math.max(1, config.getStepSecs());
        return nextSecs;
    }
}
