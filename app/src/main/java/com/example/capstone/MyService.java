package com.example.capstone;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.capstone.DB.AppDB;
import com.example.capstone.DB.DataForUI;
import com.example.capstone.DataClass.AppSettings;
import com.example.capstone.DataClass.ManageUserData;
import com.example.capstone.DataClass.UIData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    private static final int NOTI_ALARM_ID = 1234;
    private static int NOTI_ID  = 2;
    private static String TAG = "googleFitData: MyService";
    private UIData uiData = new UIData();
    private long endTime, startTime, last_update_time = 0;
    private int sleeptime_min = 1000*60*10;  // 슬립 시간(분) = 10분
    private int onSuccess=-1;
    private AppSettings as;
    private ManageUserData MUD;
    AppDB db;

    public WorkThread mThread = new WorkThread();
    public MyService() {
        Log.i(TAG, "MyService()");
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            Log.i(TAG, "getService()");
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        as = new AppSettings(getApplicationContext());
        MUD = new ManageUserData(getApplicationContext());

        db = Room.databaseBuilder(this, AppDB.class, "data_for_ui")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        createNotification();
        Toast.makeText(getApplication().getApplicationContext(), "서비스 시작", Toast.LENGTH_SHORT).show();
        mThread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind()");
        return binder;
    }

    class WorkThread extends Thread{
        private long synctime = 1000*60;
        private Boolean firsttime = true;
        @Override
        public void run() {
            super.run();
            Log.i(TAG, "Thread Start");
            while(true){
                try {
                    if(firsttime){
                        firsttime=false;
                        readData();  // 구글핏으로 부터 심박수 데이터를 가져온다.
                        while(true){
                            if(onSuccess==1){
                                onSuccess=-1;
                                break;
                            }
                            else{
                                sleep(50);
                                continue;
                            }
                        }
                        /*Thread.sleep(200);
                        Log.i(TAG, "IN IF");
                        Log.i(TAG, "first cnt: " + cnt);
                        Log.i(TAG, "firsttime: " + firsttime);*/
                    }
                    else{
                        Thread.sleep(synctime);   // 정해진 시간 만큼 멈추게 한다.
                        readData();
                        while(true){
                            if(onSuccess==1){
                                onSuccess=-1;
                                break;
                            }
                            else{
                                sleep(50);
                                continue;
                            }
                        }
                        /*
                        Thread.sleep(200);
                        Log.i(TAG, "after read");*/
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();    // 에러 메세지의 발생 근원지 부터 차례로 에러 출력
                }
            }
        }

        public long getSynctime() {
            return synctime;
        }

        public void setSynctime(long synctime) {
            this.synctime = synctime;
        }
    };

    private void readData() {
        Log.e(TAG, "readData");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date()); // 현재 시각
        endTime = cal.getTimeInMillis();   // 측정 종료 시각
        cal.add(Calendar.MINUTE, -10);      // 측정 간격 (10분)
        startTime = cal.getTimeInMillis(); // 측정 시작 시각

        DataReadRequest readRequest = new DataReadRequest.Builder() // 데이터 요청
                .read(DataType.TYPE_HEART_RATE_BPM)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)    // 데이터 시간 범위
                .bucketByTime((int)(endTime - startTime), TimeUnit.MINUTES) // 종료시간에서 부터 i 시간 만큼 TimeUnit의 단위로 읽어옴
                .build();
        // Google Fit HistoryClient의 저장소에서 심박수 데이터를 가져온다.
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {  // 성공
                        Log.d(TAG, "onSuccess()");

                        for (Bucket bucket : dataReadResponse.getBuckets()) {   // 버켓 마다 데이터 셋을 출력
                            List<DataSet> dataSets = bucket.getDataSets();
                            for (DataSet dataSet : dataSets) {
                                showDataSet(dataSet);
                            }
                        }
                        onSuccess=1;
                    }
                })
                .addOnFailureListener(new OnFailureListener() { // 실패시 오류를 출력
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure()", e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {  // 작업 완료
                        Log.d(TAG, "onComplete()");
                    }
                });

    }

    private void showDataSet(DataSet dataSet) { // 읽어온 데이터를 보기 좋게 정리
        DateFormat dateFormat = DateFormat.getDateInstance();   // 날짜 형식
        DateFormat timeFormat = DateFormat.getTimeInstance();   // 시각 형식
        uiData.resetBpm_list();
        uiData.resetTime_List();
        db.dataForUIDao().deleteAll();
        for (DataPoint dp : dataSet.getDataPoints()) {
            //Log.e(TAG, "Data point:");
            //Log.e(TAG, "\tType: " + dp.getDataType().getName());
            String startdate = dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS));;
            String enddate = dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS));
            Log.e(TAG, "\tStart: " + startdate);
            Log.e(TAG, "\tEnd: " + enddate);
            last_update_time = dp.getEndTime(TimeUnit.MILLISECONDS);
            for (Field field : dp.getDataType().getFields()) {
                Log.e(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                uiData.addTime_List(dp.getStartTime(TimeUnit.MILLISECONDS));
                uiData.addBpm_list(dp.getValue(field).asFloat());

                db.dataForUIDao().insert(new DataForUI(
                        dp.getStartTime(TimeUnit.MILLISECONDS),
                        dp.getValue(field).asFloat()));
            }
        }
            Log.i(TAG, "last_updated_time: " + timeFormat.format(last_update_time) + ", long: " + last_update_time);
            Log.i(TAG, "bpm_list: " + uiData.getBpm_list());
            Log.i(TAG, "time_list:");
            for(long time: uiData.getTime_list()) {
                Log.i(TAG, " " + timeFormat.format(time));
            }
        //set_to_scrollview(recent_bpm); // 스크롤뷰에 심박수 데이터 입력
        /*
           평균 심박수, 최저 심박수, 최고 심박수를 구하는 부분
           1. String으로 전달 받은 최근 10분 간의 심박수 데이터를 공백을 기준으로 쪼개어 String 배열에 저장후 오름차순 정렬
           2. String 배열을 활용하여 최소, 최대 심박수 추출
        */
        Log.i(TAG, "before synctime: " + mThread.getSynctime());
        mThread.setSynctime(sleeptime_min-(endTime-last_update_time)+30000);
        Log.e(TAG, "after set syntime= " + mThread.getSynctime());
        if (uiData.getBpm_list().isEmpty() || mThread.getSynctime() <= 0) {
            mThread.setSynctime(30*1000);
            Log.e(TAG, "set syntime= "+mThread.getSynctime());
        }
        else {   // 데이터 못 읽었을 때 강제 종료 방지
            uiData.setAvgBPM(MainUtility.avg_bpm(uiData.getBpm_list())); // 평균 bpm 구해서 텍스트뷰에 넣는 함수
            uiData.setMinBPM(MainUtility.min_bpm(uiData.getBpm_list())); // 최소 bpm 구해서 텍스트뷰에 넣는 함수
            uiData.setMaxBPM(MainUtility.max_bpm(uiData.getBpm_list())); // 최소 bpm 구해서 텍스트뷰에 넣는 함수
            //((MainActivity)MainActivity.main_context).avg = uiData.getAvgBPM();
            Log.i(TAG, "MIN: " + uiData.getMinBPM());
            Log.i(TAG, "AVG: " + uiData.getAvgBPM());
            Log.i(TAG, "MAX: " + uiData.getMaxBPM());

            caution(); // 심박수 판단(150이상, 45미만 갯수 카운트)
        }
    }

    private void caution() {
        if(bpm_more_than_150(uiData.getBpm_list())>=3)
            process_of_150();
        if(bpm_less_than_45(uiData.getBpm_list())>=4)
            process_of_45();
        if(bpm_less_than_45(uiData.getBpm_list())<4
                && bpm_more_than_150(uiData.getBpm_list())<3) {
            if(as.getExercisingState())
                as.setExercisingState(false);
            as.resetCautionCount();
        }
    }
    // caution함수에서 사용됨.
    // 10분의 데이터 중 150이상인 bpm의 수를 카운트
    private int bpm_more_than_150(ArrayList<Float> bpm_list) {
        int count=0;
        for(float bpm : bpm_list) {
            if(bpm>=as.getMAX_BPM()) count+=1;
        }
        return count;
    }

    // caution함수에서 사용됨.
    // 10분의 데이터 중 50미만인 bpm의 수를 카운트
    private int bpm_less_than_45(ArrayList<Float> bpm_list) {
        int count=0;
        for(double bpm : bpm_list) {
            if(bpm<as.getMIN_BPM()) count+=1;
        }
        return count;
    }

    private void process_of_150() {

        if(as.getExercisingState()) // 운동중이라면 종료
            return;

        else  // switch가 OFF라면 알람호출
        {
            IsExercising(); // 알람 호출
            Log.i(TAG, "caution_count in process of 150: " + as.getCautionCount());

        }
    }
    public void IsExercising() {
        //알림(Notification)을 관리하는 관리자 객체를 운영체제(Context)로부터 소환하기
        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Notification 객체를 생성해주는 건축가객체 생성(AlertDialog 와 비슷)
        NotificationCompat.Builder builder= null;
        //Oreo 버전(API26 버전)이상에서는 알림시에 NotificationChannel 이라는 개념이 필수 구성요소가 됨.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelID="Exercise"; //알림채널 식별자
            String channelName="exercise"; //알림채널의 이름(별명)

            //알림채널 객체 만들기
            NotificationChannel channel= new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_DEFAULT);

            //알림매니저에게 채널 객체의 생성을 요청
            notificationManager.createNotificationChannel(channel);

            //알림건축가 객체 생성
            builder=new NotificationCompat.Builder(this, channelID);


        }else{
            //알림 건축가 객체 생성
            builder= new NotificationCompat.Builder(this, (Notification) null);
        }

        //건축가에게 원하는 알림의 설정작업
        builder.setSmallIcon(R.drawable.heartbpm);
        //상태바를 드래그하여 아래로 내리면 보이는
        //알림창(확장 상태바)의 설정
        builder.setContentTitle("현재 운동중이신가요?");//알림창 제목
        builder.setContentText("운동 중일 경우 반응해주세요.");//알림창 내용

        //알림 진동 설정[진동은 반드시 퍼미션 추가 필요]
        builder.setVibrate(new long[]{0, 2000, 1000, 3000});// 0초 대기, 2초 진동, 1초 대기, 3초 진동
        builder.setTimeoutAfter(30000);
        Intent ansintent=new Intent(this, MainActivity.class);

        //ansintent.putExtra("ans", 1); 기존 인텐트 입력

        Bundle bundle = new Bundle();
        bundle.putInt("ans", 1);
        ansintent.putExtras(bundle);

        PendingIntent anspendingIntent=PendingIntent.getActivity(this, 1, ansintent, PendingIntent.FLAG_MUTABLE);
        /*
        Intent yesintent=new Intent(this, MainActivity.class);
        yesintent.putExtra("yes",1);
        PendingIntent yespendingIntent=PendingIntent.getActivity(this, 0,yesintent, PendingIntent.FLAG_MUTABLE);

        Intent nointent=new Intent(this, MainActivity.class);
        nointent.putExtra("no",0);
        PendingIntent nopendingIntent=PendingIntent.getActivity(this, 0,nointent, PendingIntent.FLAG_IMMUTABLE);
*/
        builder.setContentIntent(anspendingIntent);
        builder.setAutoCancel(true);

        //건축가에게 알림 객체 생성하도록
        Notification notification=builder.build();
        //알림매니저에게 알림(Notify) 요청
        notificationManager.notify(NOTI_ALARM_ID, notification);

    }

    private void process_of_45() {
        Log.i(TAG, "최근 10분 동안 45bpm 미만 수치가 4회 이상 감지되었습니다."); // + 진동 기능
        as.addCautionCount();
        Log.i(TAG, "caution_count= " + as.getCautionCount());
        call_to_parents();
    }

    private void call_to_parents() {
        if(as.getCautionCount()>=2) {
            Log.i(TAG, "phone: " + MUD.getUserPhoneNumber());
            Log.i(TAG, "messagecheck: " + as.getMessageCheck());
            Log.i(TAG, "callcheck: " + as.getCallCheck());
            if(as.getMessageCheck()) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(MUD.getUserPhoneNumber(),
                        null, "심박수 이상이 2회 이상 감지되어 문자를 보냅니다.",
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

    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.heartbpm);
        builder.setContentTitle("HeartRateBPM");
        builder.setContentText("서비스가 실행중입니다.");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE);

        builder.setContentIntent(pendingIntent); // 알림 클릭 시 이동

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        notificationManager.notify(NOTI_ID, builder.build()); // id : 정의해야하는 각 알림의 고유한 int값
        Notification notification = builder.build();
        startForeground(NOTI_ID, notification);
    }
}