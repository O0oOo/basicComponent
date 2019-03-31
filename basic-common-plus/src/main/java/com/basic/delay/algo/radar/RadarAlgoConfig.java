package com.basic.delay.algo.radar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RadarAlgoConfig {
    // 最大秒
    private int maxSecs;
    // 步长
    private int stepSecs;
    // 初始
    private int initSecs = 0;
    // 执行次数
    private int stepExeTimes = 0;
}
