package com.example.capstone.DB;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DataForUI")
public class DataForUI {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private long Time;
    private float HeartRate;

    public DataForUI() {}

    public DataForUI(long time, float heartRate) {
        this.Time = time;
        this.HeartRate = heartRate;
    }


    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public float getHeartRate() {
        return HeartRate;
    }

    public void setHeartRate(float heartRate) {
        HeartRate = heartRate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
