package com.example.capstone.DataClass;

import java.util.ArrayList;

public class UIData {
    private int maxBPM, minBPM, avgBPM;
    private ArrayList<Float> bpm_list;
    private ArrayList<Long> time_list;

    public UIData() {
        this.avgBPM = 0;
        this.maxBPM = 0;
        this.minBPM = 0;
        this.bpm_list = new ArrayList<>(0);
        this.time_list = new ArrayList<>(0);
    }

    public void setMaxBPM(int maxBPM) { this.maxBPM = maxBPM; }
    public void setMinBPM(int minBPM) { this.minBPM = minBPM; }
    public void setAvgBPM(int avgBPM) { this.avgBPM = avgBPM; }
    public void setBpm_list(ArrayList<Float> list) {
        this.bpm_list = list;
    }
    public void setTime_list(ArrayList<Long> list) {
        this.time_list = list;
    }
    public void addTime_List(long value) {this.time_list.add(value); }
    public void addBpm_list(Float value) {this.bpm_list.add(value);}
    public void resetTime_List() {this.time_list.clear(); }
    public void resetBpm_list() {this.bpm_list.clear();}

    public int getAvgBPM() {
        return avgBPM;
    }
    public int getMaxBPM() {
        return maxBPM;
    }
    public int getMinBPM() { return minBPM; }
    public ArrayList<Float> getBpm_list() { return bpm_list; }
    public ArrayList<Long> getTime_list() {return time_list;}

    public String getAvgBPMtoString() {
        return Integer.toString(avgBPM);
    }
    public String getMaxBPMtoString() {
        return Integer.toString(maxBPM);
    }
    public String getMinBPMtoString() {
        return Integer.toString(minBPM);
    }
}
