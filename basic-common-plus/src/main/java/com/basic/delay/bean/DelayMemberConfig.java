package com.basic.delay.bean;

import lombok.Data;

@Data
public class DelayMemberConfig {

    private String bizType;
    private Integer firstDelaySecs;
    private Integer maxExecTimes;
    private String nextExecTimeAlgo;
    private String nextExecTimeCfg;
    private Integer execNeedMaxSecs;
    private boolean persist;
    private boolean execLock;
    private boolean execLog;
    private String delayHandlerBean;
    private String extCfg;
    private boolean unackUseExecTimes;

}
