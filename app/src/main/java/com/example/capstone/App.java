package com.example.capstone;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CORES);
    Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

}
