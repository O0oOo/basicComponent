package com.basic.delay.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DelayHandlerRet {
    public static final DelayHandlerRet SUCC = new DelayHandlerRet(true);
    public static final DelayHandlerRet FAIL = new DelayHandlerRet(false);

    private boolean success;
    private String bizRespMemo;

    public DelayHandlerRet() {
        super();
    }

    public DelayHandlerRet(boolean success, String bizRespMemo) {
        this.success = success;
        this.bizRespMemo = bizRespMemo;
    }

    public DelayHandlerRet(boolean success) {
        super();
        this.success = success;
    }
}
