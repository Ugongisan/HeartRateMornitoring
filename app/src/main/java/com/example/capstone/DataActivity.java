package com.example.capstone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.DataClass.AppSettings;
import com.example.capstone.DataClass.ManageUserData;

public class DataActivity extends AppCompatActivity {
    private static final String TAG = "DataTAG";
    private static final String MODE_PASS = "PASS", MODE_EDIT = "EDIT", MODE_FIRST = "FIRST";
    Button btn_start;
    EditText UserName, UserAddress, UserPhone, UserBirth;
    CheckBox CheckMessage, CheckCall;
    ManageUserData MUD;
    AppSettings AS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Log.i(TAG, "Start!");
        Initialize();
        Log.i(TAG, AS.getMode());

        if(AS.getMode().equals(MODE_PASS)) {    // PASS: 모든 데이터가 입력되어 있는 경우
            Log.i(TAG, "PASS!!");
            goMainActivity(this);
        }

        else {                                  // 데이터가 없거나 수정해야 할 경우
            Log.i(TAG, "FIRST OR EDIT!!");
            ShowDataToView();                   // 기존 데이터 표시 (존재하는 경우만)

            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isAllFilled()) {                 // 모든 양식이 기입되어 있는 경우
                        ApplyAll();                     // 모든 변동사항 저장
                        AS.setMode(MODE_PASS);          // PASS 모드로 변경 (앞으로 앱 실행시 이 액티비티를 건너 뜀)
                        goMainActivity(getApplicationContext());

                    }
                    else {                              // 모두 입력되지 않은 경우 넘어갈 수 없음
                        Toast.makeText(getApplicationContext(), "모든 정보가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
    // 객체 초기화
    public void Initialize() {
        btn_start = (Button)findViewById(R.id.button_start);
        UserName = (EditText) findViewById(R.id.edit_name);
        UserAddress = (EditText) findViewById(R.id.edit_address);
        UserPhone = (EditText) findViewById(R.id.edit_phone);
        UserBirth = (EditText) findViewById(R.id.edit_birth);
        CheckCall = (CheckBox) findViewById(R.id.check_call);
        CheckMessage = (CheckBox) findViewById(R.id.check_message);
        MUD = new ManageUserData(getApplicationContext());
        AS = new AppSettings(getApplicationContext());
    }

    public void goMainActivity(Context context) {
        Intent intent = new Intent (context, MainActivity.class);
        startActivity(intent);          // 인트로 실행 후 바로 MainActivity로 넘어감.
        finish();
    }

    // 모든 양식이 기입되어 있는지
    public Boolean isAllFilled() {
        return isFilled(UserName)
                && isFilled(UserAddress)
                && isFilled(UserPhone)
                && isFilled(UserBirth)
                &&(CheckCall.isChecked() || CheckMessage.isChecked());
    }

    // editText 기입 여부
    public Boolean isFilled(EditText text) {
        return !(text.getText().toString().isEmpty());
    }


    // 변동사항 저장 (기존 내용과 다른 경우에만 저장)
    public void ApplyAll() {
        String Name = UserName.getText().toString();
        String Address = UserAddress.getText().toString();
        String Birth = UserBirth.getText().toString();
        String PhoneNumber = UserPhone.getText().toString();


        if( !Name.equals(MUD.getUserName().toString())) {
            MUD.setUserName(Name);
        }
        if( !Address.equals(MUD.getUserAddress().toString())) {
            MUD.setUserAddress(Address);
        }
        if( !Birth.equals(MUD.getUserBirth().toString())) {
            MUD.setUserBirth(Birth);
        }
        if( !PhoneNumber.equals(MUD.getUserPhoneNumber().toString())) {
            MUD.setUserPhone(PhoneNumber);
        }
        AS.setMessageCheck(CheckMessage.isChecked());
        AS.setCallCheck(CheckCall.isChecked());
    }

    // 데이터 불러오기(존재하는 경우만)
    public void ShowDataToView() {
        Log.i(TAG, "저장된 데이터\n"+
                MUD.getUserName() +"\n"+
                MUD.getUserAddress() +"\n"+
                MUD.getUserPhoneNumber() +"\n"+
                MUD.getUserBirth() +"\n"+
                AS.getCallCheck() +"\n"+
                AS.getMessageCheck()
        );
        if(!MUD.getUserName().isEmpty()) { UserName.setText(MUD.getUserName()); }
        if(!MUD.getUserAddress().isEmpty()) { UserAddress.setText(MUD.getUserAddress()); }
        if(!MUD.getUserPhoneNumber().isEmpty()) { UserPhone.setText(MUD.getUserPhoneNumber()); }
        if(!MUD.getUserBirth().isEmpty()) { UserBirth.setText(MUD.getUserBirth()); }
        CheckCall.setChecked(AS.getCallCheck());
        CheckMessage.setChecked(AS.getMessageCheck());
    }
}