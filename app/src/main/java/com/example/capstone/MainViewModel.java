package com.example.capstone;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.capstone.DB.AppDB;
import com.example.capstone.DB.DataForUI;
import com.example.capstone.DataClass.UIData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private AppDB db;
    private static String TAG = "googleFitData: MainViewModel";

    public MainViewModel(@NonNull Application application) {
        super(application);
        Log.i(TAG, "MainViewModel");

        db = Room.databaseBuilder(application, AppDB.class, "data_for_ui").build();

    }

    public LiveData<List<DataForUI>> getAll() {
        return db.dataForUIDao().getAll();
    }

    public void reStartService() {
        MyService.WorkThread.currentThread().interrupt();
        MyService.WorkThread.currentThread().start();
    }
}
