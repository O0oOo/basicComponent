package com.basic.delay.algo.geome;

import com.alibaba.fastjson.JSON;
import com.basic.delay.algo.NextExecSecs;
import com.basic.delay.bean.DelayMemberConfig;

public class GeometricNextExecSecs implements NextExecSecs {

    @Override
    public int calcuNextSecs(int nextExecTime, DelayMemberConfig memberConfig) {
        GeomeAlgoConfig config = JSON.parseObject(memberConfig.getNextExecTimeCfg(), GeomeAlgoConfig.class);
        //起始值
        int init = Math.max(1, config.getInitSecs());
        //max 最小>=init。
        int max = Math.max(config.getMaxSecs(), init);
        // 比值
        int ratio = config.getRatio();
        int nextSecs = (int) (init * Math.pow(ratio, (nextExecTime - 1)));
        return nextSecs;
    }
}
