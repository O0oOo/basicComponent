package com.basic.delay.trigger.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSimpleDelayQueue extends DelayQueue<SimpleDelayBean> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LinkedHashMap<String, Set<SimpleDelayBean>> pair = new LinkedHashMap<>();

    private final transient ReentrantLock lock = new ReentrantLock();

    private int maxSize;

    public abstract void consume(SimpleDelayBean delayBean);

    public AbstractSimpleDelayQueue(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    private boolean pairContains(SimpleDelayBean e) {
        String key = String.valueOf(e.getTriggerTime().getTime());
        if (pair.containsKey(key)) {
            return pair.get(key).contains(e);
        }
        return false;
    }

    private void pairAdd(SimpleDelayBean e) {
        String key = String.valueOf(e.getTriggerTime().getTime());
        Set<SimpleDelayBean> set = pair.get(key);
        if (set == null) {
            set = new HashSet<>();
            pair.put(key, set);
        }
        set.add(e);
    }

    @SuppressWarnings("unchecked")
    private Entry<String, Set<SimpleDelayBean>> pairLast() {
        try {
            Field tailField = null;
            tailField = pair.getClass().getDeclaredField("tail");
            tailField.setAccessible(true);
            return (Entry<String, Set<SimpleDelayBean>>) tailField.get(pair);
        } catch (Exception e) {
            Iterator<Entry<String, Set<SimpleDelayBean>>> iterator = pair.entrySet().iterator();
            Entry<String, Set<SimpleDelayBean>> tail = null;
            while (iterator.hasNext()) {
                tail = iterator.next();
            }
            return tail;
        }
    }

    @Override
    public void put(SimpleDelayBean e) {
        try {
            lock.lock();
            if (pairContains(e)) {
                logger.warn("repeat add {}", e);
                return;
            }
            boolean succ = false;
            if (this.size() < maxSize) {
                products(e);
                succ = true;
            } else {
                Entry<String, Set<SimpleDelayBean>> lastEntry = pairLast();
                if (lastEntry != null && lastEntry.getValue() != null) {
                    Set<SimpleDelayBean> lastValueSet = lastEntry.getValue();
                    if (!lastValueSet.isEmpty()) {
                        Iterator<SimpleDelayBean> iter = lastValueSet.iterator();
                        //随便移除一个
                        if (iter.hasNext()) {
                            SimpleDelayBean sdb = iter.next();
                            iter.remove();
                            logger.warn("get rid of{}", sdb);
                            super.remove(sdb);
                            products(e);
                            succ = true;
                        }
                    }
                }
            }
            if (!succ) {
                logger.warn("queue is full = {}, ignored {} ", maxSize, e);
            } else {
                logger.info("添加元素成功 {} ", e);
            }
        } catch (Exception excep) {
            logger.error("异常", excep);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public SimpleDelayBean take() throws InterruptedException {
        SimpleDelayBean bean = super.take();
        try {
            lock.lock();
            //pair.get(Str(et(String.valueOf(bea(Of(bean.getTriggerTimrTime().getTime())).remove(bean);
            String key = String.valueOf(bean.getTriggerTime().getTime());
            Set<SimpleDelayBean> set = pair.get(key);
            boolean deleteKeyItemRet = set.remove(bean);
            logger.info("rm valueInkey={},deleteValueInKeyRet={},valueInkeyRemainSize={}", key, deleteKeyItemRet, set.size());
            if (set.isEmpty()) {
                pair.remove(key);
                logger.info("deleteKeyRet=true");
            }
            logger.info("pair keySize={}", pair.size());
        } catch (Exception e) {
            logger.error("pair remove error", e);
        } finally {
            lock.unlock();
        }
        return bean;
    }

    /**
     * 同时添加
     *
     * @parame
     */
    private void products(SimpleDelayBean e) {
        super.put(e);
        pairAdd(e);
    }

    /**
     * 启动消费线程池
     */
    public void start() {
        SimpleDelayRunnable run = new SimpleDelayRunnable();
        Thread transferTaskThread = new Thread(run);
        transferTaskThread.setDaemon(true);
        transferTaskThread.setName("SimpleDelayRunnable");
        transferTaskThread.start();
    }

    class SimpleDelayRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                SimpleDelayBean delayBean = null;
                try {
                    logger.info("take...");
                    delayBean = take();
                    if (delayBean != null) {
                        logger.info("分发任务{}", delayBean);
                        consume(delayBean);
                    }
                } catch (Exception e) {
                    logger.error("error", e);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e2) {
                    }
                }
            }
        }
    }

    @Override
    public boolean add(SimpleDelayBean e) {
        throw new RuntimeException("not supported");
    }

    @Override
    public SimpleDelayBean poll() {
        throw new RuntimeException("not supported");
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("not supported");
    }

    public int pairSize() {
        Iterator<Set<SimpleDelayBean>> iter = this.pair.values().iterator();
        int size = 0;
        while (iter.hasNext()) {
            size += iter.next().size();
        }
        return size;
    }

    @Override
    public void clear() {
        this.manualClear();
    }

    public void manualClear() {
        try {
            lock.lock();
            logger.info("manualClear开始");
            pair.clear();
            super.clear();
            logger.info("manualClear结束");
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            lock.unlock();
        }
    }

    public void printAll() {
        try {
            lock.lock();
            logger.info("打印pair开始,size={}", pairSize());
            Set<String> pairKeys = pair.keySet();
            Iterator<String> keyIter = pairKeys.iterator();
            while (keyIter.hasNext()) {
                String time = keyIter.next();
                Set<SimpleDelayBean> values = pair.get(time);
                Iterator<SimpleDelayBean> valueIter = values.iterator();
                while (valueIter.hasNext()) {
                    SimpleDelayBean simpleDelayBean = (SimpleDelayBean) valueIter.next();
                    logger.info("pair元素：{}", simpleDelayBean);
                }
            }
            logger.info("打印pair结束");

            logger.info("打印Queue开始,size={}", size());
            Iterator<SimpleDelayBean> iter = super.iterator();
            while (iter.hasNext()) {
                SimpleDelayBean bean = iter.next();
                logger.info("queue元素 {}", bean);
            }
            logger.info("打印Queue结束");
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            lock.unlock();
        }
    }

}
