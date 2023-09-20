package com.example.capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

import java.util.Set;

import static android.content.ContentValues.TAG;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Log.i(TAG, "Start!");
        Intent intent_caution=new Intent(IntroActivity.this, CautionActivity.class);
        startActivity(intent_caution);

        if (!permissionGrantred()) {
            Toast.makeText(getApplicationContext(),"앱을 실행하기 위해서는 앱의 알림 접근 허용이 필요합니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent( "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (!permissionGrantred()) {
                    Toast.makeText(getApplicationContext(),"앱을 실행하기 위해서는 앱의 알림 접근 허용이 필요합니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent( "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    startActivity(intent);
                }*/
                googleSignin();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), CautionActivity.class);
                startActivity(intent); // CautionActivity로 이동
            }
        });

    }

    private boolean permissionGrantred() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS ,Manifest.permission.FOREGROUND_SERVICE
                }, MODE_PRIVATE);
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (sets != null && sets.contains(getPackageName())) {
            return true;
        } else {
            return false;
        }
    }

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 2;   // 구글핏 퍼미션 승인 시 얻는 값


    public void googleSignin() {    // 해당 구글 계정으로 피트니스의 심박수 데이터를 읽을 권한을 얻음
        Log.i(TAG, "googleSignin!");

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)  // 심박수 데이터를 읽을 권한
                .build();

        // 해당 구글 계정이 위의 권한을 얻지 않은 경우 권한을 요청
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)
                || GoogleSignIn.getLastSignedInAccount(this).getDisplayName() == null ){

            Log.i(TAG, "Not has GoogleSignin Permissions");
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        }
        else {
            Log.i(TAG,"googleSignin_else!");
            Intent intent = new Intent (getApplicationContext(), DataActivity.class);
            startActivity(intent); //인트로 실행 후 바로 DataActivity로 넘어감
            finish();   // IntroActivity 종료
        }
    }

    @Override   // 퍼미션 및 구글계정에 대한 접근 권한 요청에 대한 결과 처리
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult!"
                + "\n resultCode: " + resultCode
                + "\n requestCode: " + requestCode
                + "\n Activity.RESULT_OK : " + Activity.RESULT_OK);
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "if (resultCode == Activity.RESULT_OK)!");
            if (GOOGLE_FIT_PERMISSIONS_REQUEST_CODE == requestCode) {
                Log.i(TAG, "if (GOOGLE_FIT_PERMISSIONS_REQUEST_CODE == requestCode)!");

                Intent intent = new Intent (getApplicationContext(), DataActivity.class);
                startActivity(intent); //인트로 실행 후 바로 MainActivity로 넘어감.
                finish();
            }
        }
        else {
            Log.i(TAG, "onActivityResult_else!");
            Toast.makeText(this, "앱 실행을 위해서는 구글 계정 로그인을 해야 합니다", Toast.LENGTH_LONG).show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        }
    }
}