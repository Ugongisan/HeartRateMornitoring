package com.example.capstone;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.capstone.DataClass.AppSettings;
import com.example.capstone.DataClass.ManageUserData;

public class MyNotificationListener extends NotificationListenerService {
    private static String TAG = "googleFitData: MyNotificationListener";
    private static final int NOTI_ALARM_ID = 1234;
    private AppSettings as;
    private ManageUserData MUD;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        as = new AppSettings(base);
        MUD = new ManageUserData(base);

    }
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn);
        if(sbn.getId() == NOTI_ALARM_ID && reason == REASON_TIMEOUT) {
        Log.d(TAG, "onNotificationRemoved ~ " +
                " packageName: " + sbn.getPackageName() +
                " id: " + sbn.getId());
            as.addCautionCount();
            call_to_parents();
            Log.i(TAG, "caution_count: " + as.getCautionCount());
        }
    }

    private void call_to_parents() {
        if(as.getCautionCount()>=2) {
            Log.i(TAG, "phone: " + MUD.getUserPhoneNumber());
            Log.i(TAG, "messagecheck: " + as.getMessageCheck());
            Log.i(TAG, "callcheck: " + as.getCallCheck());
            if(as.getMessageCheck()) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(MUD.getUserPhoneNumber(),
                        null, MUD.getUserName() + "님에게 이상 심박수가 감지되어 문자를 보냅니다.",
                        null, null);
            }
            if(as.getCallCheck()) {
                Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + MUD.getUserPhoneNumber()));
                call.setFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(call);
            }
            Log.i(TAG, "보호자에게 연락되었습니다.");
            Log.i(TAG, "before) caution_count= " + as.getCautionCount());
            as.resetCautionCount();
            Log.i(TAG, "after) caution_count= " + as.getCautionCount());
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if(sbn.getId() == NOTI_ALARM_ID) {
            Notification notification = sbn.getNotification();
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
            Icon smallIcon = notification.getSmallIcon();
            Icon largeIcon = notification.getLargeIcon();

            Log.d(TAG, "onNotificationPosted ~ " +
                    " packageName: " + sbn.getPackageName() +
                    " id: " + sbn.getId() +
                    " postTime: " + sbn.getPostTime() +
                    " title: " + title +
                    " text : " + text +
                    " subText: " + subText);
        }
    }
}

