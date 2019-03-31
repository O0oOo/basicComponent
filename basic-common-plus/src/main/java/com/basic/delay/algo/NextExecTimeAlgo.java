package com.basic.delay.algo;

import com.basic.delay.algo.arith.ArithmeticNextExecSecs;
import com.basic.delay.algo.constants.ConstantNextExecSecs;
import com.basic.delay.algo.geome.GeometricNextExecSecs;
import com.basic.delay.algo.radar.RadarNextExecSecs;
import com.basic.delay.bean.DelayMemberConfig;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NextExecTimeAlgo {

    private static final String CONSTANT = "constant";

    private static Map<String, NextExecSecs> mapping = new HashMap<String, NextExecSecs>();

    static {
        mapping.put("radar", new RadarNextExecSecs());
        mapping.put(CONSTANT, new ConstantNextExecSecs());
        //等比
        mapping.put("geometric", new GeometricNextExecSecs());
        //等差
        mapping.put("arithmetic", new ArithmeticNextExecSecs());
    }

    private NextExecTimeAlgo() {

    }

    /**
     *  基于当前执行次数计算下一次执行时间。
     *  @param currExecTime 当前执行次数。计算下次执行时间内部会自动+1进行计算
     *  @param memberConfig 
     *  @return
     */
    private static Date nextTime(int currExecTime, DelayMemberConfig memberConfig) {
        if (currExecTime <= 0 && memberConfig.getFirstDelaySecs() != null) {
            // 最小2s
            return DateUtils.addSeconds(new Date(), Math.max(memberConfig.getFirstDelaySecs(), 2));
        }
        int nextExecTime = currExecTime + 1;
        NextExecSecs algo = null;
        if (!mapping.containsKey(memberConfig.getNextExecTimeAlgo())) {
            algo = mapping.get(CONSTANT);
        } else {
            algo = mapping.get(memberConfig.getNextExecTimeAlgo());
        }
        //传入nextExecTime最小为2
        int nextSecs = algo.calcuNextSecs(nextExecTime, memberConfig);
        return DateUtils.addSeconds(new Date(), Math.max(nextSecs, 2));
    }

}
