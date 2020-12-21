package com.example.quitsmocking;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class AlarmService extends Service {
    /*public AlarmService(){
        super("someone");
    }
    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
        return super.onStartCommand(intent, flags,startId);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent.hasExtra("iToday_num")){
            int iToday_num = intent.getIntExtra("iToday_num",0);
            Toast.makeText(this.getApplicationContext(), iToday_num, Toast.LENGTH_SHORT).show();
        }
    }*/
    IBinder mBinder = new MyBinder();


    class MyBinder extends Binder {
        AlarmService getService(){
            return AlarmService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    int TodayNum(){
        return 0;
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }
    public int onStarCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
    /*public class LocalBinder extends Binder {
        public AlarmService getService(){
            return AlarmService.this;
        }
    }
    private IBinder mBinder = new LocalBinder();*/
   // @Nullable
  /*  @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public int showTheNumber(){
        return new Random().nextInt(99);
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
*/
}