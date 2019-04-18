package com.basic.delay.trigger.simple;

import com.basic.delay.DelayHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class SimpleDelayBean implements Delayed {

    private String bizType;
    private Date triggerTime;
    private String bizValue;
    private int execTimes;
    private Class<? extends DelayHandler> handlerClass;
    private Date firstExecTime;

    @Override
    public long getDelay(TimeUnit unit) {
        return triggerTime.getTime() - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed comparedDelay) {
        SimpleDelayBean that = (SimpleDelayBean) comparedDelay;
        return (triggerTime.getTime() + this.bizKey()).compareTo(that.getTriggerTime().getTime() + that.bizKey());
    }

    public String bizKey() {
        return this.getBizType() + "_" + this.getBizValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleDelayBean other = (SimpleDelayBean) obj;
        if (bizType == null) {
            if (other.bizType != null) {
                return false;
            }
        } else if (!bizType.equals(other.bizType)) {
            return false;
        }
        if (bizValue == null) {
            if (other.bizValue != null) {
                return false;
            }
        } else if (!bizValue.equals(other.bizValue)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final  int prime = 31;
        int result = 1;
        result = prime * result + ((bizType == null) ? 0 : bizType.hashCode());
        result = prime * result + ((bizValue == null) ? 0 : bizValue.hashCode());
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleDelayBean{");
        sb.append("bizType='").append(bizType).append('\'');
        sb.append(", triggerTime=").append(triggerTime);
        sb.append(", bizValue='").append(bizValue).append('\'');
        sb.append(", execTimes=").append(execTimes);
        sb.append('}');
        return sb.toString();
    }
}
