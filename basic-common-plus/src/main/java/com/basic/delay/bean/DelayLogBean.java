package com.basic.delay.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DelayLogBean {

    private String bizType;
    private String bizValue;
    private Date execStartTime;
    private Date execEndTime;
    private Long execTimes;
    /**
     * 执行结果，E=异常，S=成功，F=失败
     */
    private String execResult;
    private String bizRespMemo;

}
