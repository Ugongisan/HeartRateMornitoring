package com.example.capstone.mainFragments;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.capstone.DB.AppDB;
import com.example.capstone.DB.DataForUI;
import com.example.capstone.DataClass.UIData;
import com.example.capstone.MainActivity;
import com.example.capstone.MainUtility;
import com.example.capstone.MainViewModel;
import com.example.capstone.R;
import com.example.capstone.databinding.FragmentMainBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class MainFragment extends Fragment {
    private static final String TAG = "googleFitData: MainFragment";
    FragmentMainBinding binding;
    AppDB db;
    UIData uiData = new UIData();
    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewmodel(mainViewModel);
        mainViewModel.getAll().observe(getViewLifecycleOwner(), dataForUIS -> {
            uiData.resetBpm_list();
            uiData.resetTime_List();
            if(!dataForUIS.isEmpty()) {
                StringBuilder dfus = new StringBuilder("dataforuis\n:");
                for (DataForUI dfu:dataForUIS) {
                    dfus.append(" [ ").append(dfu.getId()).append(", ").append(dfu.getTime()).append(", ").append(dfu.getHeartRate()).append(" ]");
                    uiData.addBpm_list(dfu.getHeartRate());
                    uiData.addTime_List(dfu.getTime());
                }
                Log.i(TAG, dfus.toString());
                Log.i(TAG, "dataforuis: bpm_list before: " + uiData.getBpm_list().toString());
                StringBuilder tmp = new StringBuilder("dataforuis: time_list before:");
                for(long time: uiData.getTime_list()) {
                    tmp.append(" ").append(format.format(time));
                }
                Log.i(TAG, tmp.toString());
                uiData.setMaxBPM(MainUtility.max_bpm(uiData.getBpm_list()));
                uiData.setMinBPM(MainUtility.min_bpm(uiData.getBpm_list()));
                uiData.setAvgBPM(MainUtility.avg_bpm(uiData.getBpm_list()));
                Log.i(TAG, "dataforuis: bpm_list after: " + uiData.getBpm_list().toString());
                tmp = new StringBuilder("dataforuis: time_list: after");
                for(long time: uiData.getTime_list()) {
                    tmp.append(" ").append(format.format(time));
                }
                Log.i(TAG, tmp.toString());
                Log.i(TAG, "dataforuis Max: " + uiData.getMaxBPM());
                Log.i(TAG, "dataforuis Min: " + uiData.getMinBPM());
                Log.i(TAG, "dataforuis Avg: " + uiData.getAvgBPM());
                binding.bpmAvg.setText(uiData.getAvgBPMtoString());
                binding.bpmMax.setText(uiData.getMaxBPMtoString());
                binding.bpmMin.setText(uiData.getMinBPMtoString());
                LineChart(uiData.getBpm_list(), uiData.getTime_list());
            }
        });
        return binding.getRoot();

    }

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        binding = FragmentMainBinding.bind(view);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        db = Room.databaseBuilder(requireContext(), AppDB.class, "data_for_ui")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        return view;
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated()");

        binding.RefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.getAll().observe(getViewLifecycleOwner(), dataForUIS -> {
                    uiData.resetBpm_list();
                    uiData.resetTime_List();
                    if(!dataForUIS.isEmpty()) {
                        StringBuilder dfus = new StringBuilder("dataforuis\n:");
                        for (DataForUI dfu:dataForUIS) {
                            dfus.append(" [ ").append(dfu.getId()).append(", ").append(dfu.getTime()).append(", ").append(dfu.getHeartRate()).append(" ]");
                            uiData.addBpm_list(dfu.getHeartRate());
                            uiData.addTime_List(dfu.getTime());
                        }
                        Log.i(TAG, dfus.toString());
                        Log.i(TAG, "dataforuis: bpm_list before: " + uiData.getBpm_list().toString());
                        StringBuilder tmp = new StringBuilder("dataforuis: time_list before:");
                        for(long time: uiData.getTime_list()) {
                            tmp.append(" ").append(format.format(time));
                        }
                        Log.i(TAG, tmp.toString());
                        uiData.setMaxBPM(MainUtility.max_bpm(uiData.getBpm_list()));
                        uiData.setMinBPM(MainUtility.min_bpm(uiData.getBpm_list()));
                        uiData.setAvgBPM(MainUtility.avg_bpm(uiData.getBpm_list()));
                        Log.i(TAG, "dataforuis: bpm_list after: " + uiData.getBpm_list().toString());
                        tmp = new StringBuilder("dataforuis: time_list: after");
                        for(long time: uiData.getTime_list()) {
                            tmp.append(" ").append(format.format(time));
                        }
                        Log.i(TAG, tmp.toString());
                        Log.i(TAG, "dataforuis Max: " + uiData.getMaxBPM());
                        Log.i(TAG, "dataforuis Min: " + uiData.getMinBPM());
                        Log.i(TAG, "dataforuis Avg: " + uiData.getAvgBPM());
                        binding.bpmAvg.setText(uiData.getAvgBPMtoString());
                        binding.bpmMax.setText(uiData.getMaxBPMtoString());
                        binding.bpmMin.setText(uiData.getMinBPMtoString());
                        LineChart(uiData.getBpm_list(), uiData.getTime_list());
                    }
                });
            }
        });

        /*mainViewModel.getAll().observe(getViewLifecycleOwner(), dataForUIS -> {
            uiData.resetBpm_list();
            uiData.resetTime_List();
            if(!dataForUIS.isEmpty()) {
                StringBuilder dfus = new StringBuilder("dataforuis\n:");
                for (DataForUI dfu:dataForUIS) {
                    dfus.append(" [ ").append(dfu.getId()).append(", ").append(dfu.getTime()).append(", ").append(dfu.getHeartRate()).append(" ]");
                    uiData.addBpm_list(dfu.getHeartRate());
                    uiData.addTime_List(dfu.getTime());
                }
                Log.i(TAG, dfus.toString());
                Log.i(TAG, "dataforuis: bpm_list before: " + uiData.getBpm_list().toString());
                StringBuilder tmp = new StringBuilder("dataforuis: time_list before:");
                for(long time: uiData.getTime_list()) {
                    tmp.append(" ").append(format.format(time));
                }
                Log.i(TAG, tmp.toString());
                uiData.setMaxBPM(MainUtility.max_bpm(uiData.getBpm_list()));
                uiData.setMinBPM(MainUtility.min_bpm(uiData.getBpm_list()));
                uiData.setAvgBPM(MainUtility.avg_bpm(uiData.getBpm_list()));
                Log.i(TAG, "dataforuis: bpm_list after: " + uiData.getBpm_list().toString());
                tmp = new StringBuilder("dataforuis: time_list: after");
                for(long time: uiData.getTime_list()) {
                    tmp.append(" ").append(format.format(time));
                }
                Log.i(TAG, tmp.toString());
                Log.i(TAG, "dataforuis Max: " + uiData.getMaxBPM());
                Log.i(TAG, "dataforuis Min: " + uiData.getMinBPM());
                Log.i(TAG, "dataforuis Avg: " + uiData.getAvgBPM());
                binding.bpmAvg.setText(uiData.getAvgBPMtoString());
                binding.bpmMax.setText(uiData.getMaxBPMtoString());
                binding.bpmMin.setText(uiData.getMinBPMtoString());
                LineChart(uiData.getBpm_list(), uiData.getTime_list());
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    /*MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.setViewmodel(mainViewModel);
        mainViewModel.getMyUIData().observe(getViewLifecycleOwner(), uiData -> {
            binding.bpmAvg.setText(uiData.getAvgBPMtoString());
            binding.bpmMax.setText(uiData.getMaxBPMtoString());
            binding.bpmMin.setText(uiData.getMinBPMtoString());
        });
        return binding.getRoot();

    }*/

    public void LineChart(ArrayList<Float> split_bpm_list, ArrayList<Long> time_list) {
        LineChart lineChart = (LineChart) binding.chart;
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < split_bpm_list.size(); i++){
            entries.add(new Entry(i, (float)split_bpm_list.get(i)));
        }

        LineDataSet dataset = new LineDataSet(entries, "심박수");
        //dataset.setColors(Color.BLACK);
                ArrayList<String> labels = new ArrayList<>();
        for(int i = 0; i < time_list.size(); i++) {
            labels.add(format.format(time_list.get(i)));
        }
        //Log.i(TAG, "bpm: " + entries + "\n time: " + labels);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setTextColor(Color.GRAY);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(Color.GRAY);

        //xAxis.setLabelCount(10);
        LineData lineData = new LineData(dataset);
        lineData.setValueTextColor(Color.GRAY);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.GRAY);
        lineChart.setData(lineData);
        lineChart.invalidate();

        return;
    }
}