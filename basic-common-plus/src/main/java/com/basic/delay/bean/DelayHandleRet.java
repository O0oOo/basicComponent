package com.basic.delay.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DelayHandleRet {
    public static final DelayHandleRet SUCC = new DelayHandleRet(true);
    public static final DelayHandleRet FAIL = new DelayHandleRet(false);

    private boolean success;
    private String bizRespMemo;

    public DelayHandleRet() {
        super();
    }

    public DelayHandleRet(boolean success, String bizRespMemo) {
        this.success = success;
        this.bizRespMemo = bizRespMemo;
    }

    public DelayHandleRet(boolean success) {
        super();
        this.success = success;
    }
}
