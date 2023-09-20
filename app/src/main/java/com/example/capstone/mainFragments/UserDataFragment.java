package com.example.capstone.mainFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.capstone.DataClass.ManageUserData;
import com.example.capstone.R;

public class UserDataFragment extends Fragment {
    private EditText name, address, phone, birth;
    ManageUserData MUD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflatedView = inflater.inflate(R.layout.fragment_user_data, container,false);

        MUD = new ManageUserData(getContext());
        Button btn = (Button) myInflatedView.findViewById(R.id.button_start);
        name = (EditText) myInflatedView.findViewById(R.id.edit_name);
        address = (EditText) myInflatedView.findViewById(R.id.edit_address);
        phone = (EditText) myInflatedView.findViewById(R.id.edit_phone);
        birth = (EditText) myInflatedView.findViewById(R.id.edit_birth);
        showText();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveText();

            }
        });

        return myInflatedView;
    }

    private void saveText() {
        MUD.setUserName(name.getText().toString());
        MUD.setUserAddress(address.getText().toString());
        MUD.setUserPhone(phone.getText().toString());
        MUD.setUserBirth(birth.getText().toString());
    }

    private void showText() {
        name.setText(MUD.getUserName());
        address.setText(MUD.getUserAddress());
        phone.setText(MUD.getUserPhoneNumber());
        birth.setText(MUD.getUserBirth());
    }
}