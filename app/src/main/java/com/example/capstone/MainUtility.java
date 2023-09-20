package com.example.capstone;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainUtility {

    public static int avg_bpm(ArrayList<Float> bpm_list){
        int sum = 0;
        for(double d: bpm_list) sum+=d;
        int avg = sum/bpm_list.size();
        return avg;
    }
    public static int min_bpm(ArrayList<Float> bpm_list){
        ArrayList<Float> sorted_list = deep_copy(bpm_list);
        sorted_list.sort(null);
        int min = sorted_list.get(0).intValue();
        return min;
    }
    public static int max_bpm(ArrayList<Float> bpm_list){
        ArrayList<Float> sorted_list = deep_copy(bpm_list);
        sorted_list.sort(null);
        int max = sorted_list.get(sorted_list.size() - 1).intValue();
        return max;
    }

    public static ArrayList<Float> deep_copy(ArrayList<Float> oldlist) {
        ArrayList<Float> newlist = new ArrayList<>(0);
        for (float n: oldlist) {
            newlist.add(n);
        }
        return newlist;
    }
}
