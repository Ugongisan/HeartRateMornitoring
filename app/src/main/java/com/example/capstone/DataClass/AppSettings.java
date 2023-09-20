package com.example.capstone.DataClass;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {
    private static final String sharedPrefsFile = "AppSettings";
    private SharedPreferences SettingsPref;
    private SharedPreferences.Editor SettingsEditor;

    public AppSettings(Context context) {
        this.SettingsPref = context.getSharedPreferences(sharedPrefsFile , context.MODE_PRIVATE); // 앱 설정용
        this.SettingsEditor = SettingsPref.edit();   // 앱 설정 변경
    }

    // 키 값 반환. 값이 없으면 기본값 반환
    public int getMAX_BPM() {return  SettingsPref.getInt("MAX_BPM", 150);}  // MAX_BPM 키 값 가져옴. 없으면 100
    public int getMIN_BPM() {return SettingsPref.getInt("MIN_BPM", 45);}    // MIN_BPM 키 값 가져옴. 없으면 60
    public Boolean getCallCheck() { return SettingsPref.getBoolean("CallCheck", false); }   // CallCheck 키 값 가져옴. 없으면 false
    public Boolean getMessageCheck() { return SettingsPref.getBoolean("MessageCheck", false); } // MessageCheck 키 값 가져옴. 없으면 false
    public String getMode() { return SettingsPref.getString("Mode", "FIRST"); } // Mode 키 값 가져옴. 없으면 "FIRST"
    public Boolean getExercisingState() { return SettingsPref.getBoolean("Exercise", false); }
    public Integer getCautionCount() {return SettingsPref.getInt("CautionCount", 0); }


    // 키 값 입력
    public void setMAX_BPM(int max_bpm) { SettingsEditor.putInt("MAX_BPM", max_bpm); SettingsEditor.apply(); };
    public void setMIN_BPM(int min_bpm) { SettingsEditor.putInt("MIN_BPM", min_bpm); SettingsEditor.apply(); };
    public void setCallCheck(Boolean CallCheck) { SettingsEditor.putBoolean("CallCheck", CallCheck); SettingsEditor.apply(); }
    public void setMessageCheck(Boolean MessageCheck) { SettingsEditor.putBoolean("MessageCheck", MessageCheck); SettingsEditor.apply(); }
    public void setMode(String mode) { SettingsEditor.putString("Mode", mode); SettingsEditor.apply(); }
    public void setExercisingState(Boolean isExercising) { SettingsEditor.putBoolean("Exercise", isExercising); SettingsEditor.apply(); }
    public void addCautionCount() {SettingsEditor.putInt("CautionCount", getCautionCount() + 1); SettingsEditor.apply(); }
    public void resetCautionCount() {SettingsEditor.putInt("CautionCount", 0); SettingsEditor.apply();}

}

