package com.basic.delay.algo.radar;

import com.alibaba.fastjson.JSON;
import com.basic.delay.algo.NextExecSecs;
import com.basic.delay.bean.DelayMemberConfig;

public class RadarNextExecSecs implements NextExecSecs {

    public int calcuNextSecs(int nextExecTime, DelayMemberConfig memberConfig) {
        RadarAlgoConfig config = JSON.parseObject(memberConfig.getNextExecTimeCfg(), RadarAlgoConfig.class);
        //步长最小为1s
        int stepSecs = Math.max(1, config.getStepSecs());
        //max 最小等于步长。
        int max = Math.max(config.getMaxSecs(), stepSecs);
        // 执行次数*步长和max 取最小
        int nextSecs = Math.max(config.getInitSecs(), 0) + Math.min(nextExecTime  * stepSecs, max);
        return nextSecs;
    }
}
