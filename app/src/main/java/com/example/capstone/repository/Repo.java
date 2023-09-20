package com.example.capstone.repository;

import android.os.Handler;

import com.example.capstone.DataClass.UIData;

import java.util.concurrent.Executor;

public class Repo {
    private final Executor executor;
    private final Handler handler;

    public Repo(Executor executor, Handler handler) {
        this.executor = executor;
        this.handler = handler;
    }

    public void LongTask(RepoCallback<UIData> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    UIData uiData = new UIData();
                    for(int i = 0; i < 10; i++) {
                        uiData.addBpm_list((float)i);
                        uiData.addTime_List((long)i);
                        uiData.setMaxBPM(i<(10-i)?(10-i):i);
                        uiData.setMinBPM(i>(10-i)?(10-i):i);
                        uiData.setAvgBPM((uiData.getMaxBPM()*uiData.getMaxBPM() + uiData.getMinBPM()*uiData.getMinBPM())/2);
                        Result<UIData> result = new Result.Success<>(uiData);
                        notifyResult(result, callback);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    Result<UIData> result = new Result.Error<>(e);
                    notifyResult(result, callback);
                    e.printStackTrace();
                }
            }
        });
    }

    private void notifyResult(
            final Result<UIData> result,
            final RepoCallback<UIData> callback
            ) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(result);
            }
        });
    }
}
