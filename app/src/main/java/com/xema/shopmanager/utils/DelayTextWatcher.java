package com.xema.shopmanager.utils;

import android.support.annotation.WorkerThread;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.Timer;
import java.util.TimerTask;

//background 에서 실행되니 ui 업데이트할때 ui 스레드에서 접근할것
public abstract class DelayTextWatcher implements TextWatcher {
    private Timer timer = new Timer();
    private final long DELAY = 80; //0.08초

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        delayedChanged(editable);
                    }
                },
                DELAY
        );
    }
    
    public abstract void delayedChanged(Editable editable);
}
