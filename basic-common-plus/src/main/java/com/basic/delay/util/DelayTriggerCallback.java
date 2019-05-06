package com.basic.delay.util;

public interface DelayTriggerCallback {

    void execLockFail();

    void handleSucc();

    void handleFail();

    void handleError();

}
