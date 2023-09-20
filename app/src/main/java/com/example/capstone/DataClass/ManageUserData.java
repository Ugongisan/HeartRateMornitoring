package com.example.capstone.DataClass;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ManageUserData {
    private static final String sharedPrefsFile = "UserData";
    private String masterKeyAlias;
    private SharedPreferences DataPref;
    private SharedPreferences.Editor DataEditor;
    public ManageUserData(Context context){
        //DataPref = context.getSharedPreferences(sharedPrefsFile, context.MODE_PRIVATE);
        EncryptSharedPref(context);     // 개인정보 저장 후 암호화용
        DataEditor = DataPref.edit();
    }

    private void EncryptSharedPref(Context context) {   // SharedPreferences 암호화
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);   // 마스터키 생성
            DataPref = EncryptedSharedPreferences.create(                        // 데이터 암호화
                    sharedPrefsFile,                                                    // 암호화된 파일 위치
                    masterKeyAlias,                                                     // 키 앨리어스
                    context,                                                           // 현재 컨텍스트
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,      // 키 암호화 방식: AES-256 SIV 알고리즘
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);   // 값 암호화 방식: AES-256 GCM 알고리즘
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() { return DataPref.getString("Name", ""); }
    public String getUserAddress() { return DataPref.getString("Address", ""); }
    public String getUserPhoneNumber() { return DataPref.getString("PhoneNumber", ""); }
    public String getUserBirth() { return DataPref.getString("Birth", ""); }

    public void setUserName(String name) { DataEditor.putString("Name", name); DataEditor.apply(); }
    public void setUserAddress(String address) { DataEditor.putString("Address", address); DataEditor.apply(); }
    public void setUserBirth(String birth) { DataEditor.putString("Birth", birth); DataEditor.apply(); }
    public void setUserPhone(String PhoneNumber) { DataEditor.putString("PhoneNumber", PhoneNumber); DataEditor.apply(); }
}
