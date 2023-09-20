package com.example.capstone.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.capstone.DataClass.AppSettings;
import com.example.capstone.R;

public class OptionFragment extends Fragment {
    private EditText maxHR, minHR;
    private CheckBox checkCall, checkMessage;
    AppSettings appSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_option, container, false);
        appSettings = new AppSettings(getActivity().getApplicationContext());
        maxHR = myInflatedView.findViewById(R.id.maxHR);
        minHR = myInflatedView.findViewById(R.id.minHR);
        checkCall = myInflatedView.findViewById(R.id.check_call);
        checkMessage = myInflatedView.findViewById(R.id.check_message);
        showView();

        Button btn = myInflatedView.findViewById(R.id.save_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveView();
            }
        });


        return myInflatedView;
    }

    private void showView() {
        maxHR.setText("" + appSettings.getMAX_BPM());
        minHR.setText("" + appSettings.getMIN_BPM());
        checkMessage.setChecked(appSettings.getMessageCheck());
        checkCall.setChecked(appSettings.getCallCheck());
    }

    private void saveView() {
        appSettings.setMAX_BPM(Integer.parseInt(maxHR.getText().toString()));
        appSettings.setMIN_BPM(Integer.parseInt(minHR.getText().toString()));
        appSettings.setCallCheck(checkCall.isChecked());
        appSettings.setMessageCheck(checkMessage.isChecked());
    }
}