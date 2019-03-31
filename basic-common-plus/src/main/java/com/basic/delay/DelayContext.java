package com.basic.delay;

import com.basic.delay.bean.DelayReq;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * delay 线程上下文
 * 用途：防止delay线程触发任务后又将任务丢进delay处理(虽然持久化可以防重，但是分区任务还是有可能跨区，而且也会造成不必要的插入错误)
 */
public class DelayContext {

    private static ThreadLocal<TaskHolder> threadHolder = new ThreadLocal<TaskHolder>();

    public static final Logger log = LoggerFactory.getLogger(DelayContext.class);

    /**
     * 是否是在delay线程里，重复添加了delay任务。
     * @param delayReq
     * @return
     */
    public static boolean isRepeatAdd(DelayReq delayReq) {
        TaskHolder taskHolder = DelayContext.getTaskHolder();
        if (taskHolder != null) {
            if (taskHolder.getHandlerClass() == delayReq.getHanderClass() && taskHolder.getValue().equals(delayReq.getValue())) {
                log.info("detect repeat add delayReq in delayThread, add task canceled");
                return true;
            }
        }
        return false;
    }

    private static TaskHolder getTaskHolder() {
        return threadHolder.get();
    }

    public static void setTaskHolder(TaskHolder holder) {
        if (holder == null) {
            threadHolder.remove();
        } else {
            threadHolder.set(holder);
        }
    }

    @Getter
    @Setter
    public static class TaskHolder {
        private Class<? extends DelayHandler> handlerClass;
        private String value;

        public TaskHolder(Class<? extends DelayHandler> handlerClass, String value) {
            super();
            this.handlerClass = handlerClass;
            this.value = value;
        }
    }
}
