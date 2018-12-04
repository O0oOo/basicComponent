package com.basic.delay.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DelayPersistedMessage {
    protected String id;
    protected String bizType;//业务类型
    protected String bizValue;//业务值
    protected Date firstExecTime;//首次执行时间
    protected Date lastExecTime;//上车执行时间
    protected Date nextExecTime;//下次触发时间，null代表job已直接结束
    protected Integer execTimes;//已执行次数
    protected String delayState;//延迟状态,I-等待处理,P-处理中,S-消费成功,F-消费失败，E-消费异常
    protected Date delayStateTime;//延迟状态更新时间
    protected Integer maxExecTimes;//最大执行次数
    protected String execLock;//执行锁
}
