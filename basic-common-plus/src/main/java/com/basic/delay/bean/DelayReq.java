package com.basic.delay.bean;

import com.basic.delay.DelayHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DelayReq {

    private String bizType;
    private String value;
    private String delayReason;//仅用于首次存储，用于记录delay的原因
    private Class<? extends DelayHandler> handerClass;
    private Date fistExecTime;//程序指定第一次执行时间时忽略配置指定。

    public DelayReq(String bizType, String value, Class<? extends DelayHandler> handerClass) {
        super();
        this.bizType = bizType;
        this.value = value;
        this.handerClass = handerClass;
    }

    public DelayReq(String value, Class<? extends DelayHandler> handerClass) {
        this.value = value;
        this.handerClass = handerClass;
    }

    public DelayReq(String value, Class<? extends DelayHandler> handerClass, Date fistExecTime) {
        this.value = value;
        this.handerClass = handerClass;
        this.fistExecTime = fistExecTime;
    }

    public DelayReq reason(String delayReason) {
        this.delayReason = delayReason;
        return this;
    }
}
