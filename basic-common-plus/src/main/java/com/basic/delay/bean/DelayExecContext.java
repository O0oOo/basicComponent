package com.basic.delay.bean;

import com.basic.delay.DelayHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DelayExecContext {

    DelayMemberConfig memberConfig;//成员配置
    private int execTimes;//执行次数 base on 1
    private String bizType;//业务类型
    private String bizValue;//业务值
    private Date startExecTime;//本次开始执行时间

    private Date firstExecTime;//第一次执行时间
    private Class<? extends DelayHandler> handlerClass;//处理类

    private List<DelayLogBean> latestExecLogs;//最近执行日志，最多50条
    private Object execRecordResp;//执行响应


    public String str() {
        return String.format("ctx[%s=%s,time=%s,hash=%s]", this.bizType, this.bizValue, String.valueOf(execTimes), String.valueOf(System.identityHashCode(this)));
    }
}
