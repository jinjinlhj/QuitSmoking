package com.example.quitsmocking;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    AlarmService alarmService;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView limit_num, today_progress, save_money, save_time, today_num;
    private String stoday_num;
    private ProgressBar cpb, cpb2;
    private Button button;
    private int FriCount, SatCount,SunCount,MonCount,TuseCount,WenCount,ThursCount;
    private int iToday_num;
    private Button mBtnSendData;

    private long time=0;

    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //스마트폰-아두이노 간 데이터 전송


    SessionManager sessionManager;
    BroadcastReceiver myReceiver;

    AlarmManager resetAlarmMager;
   Intent  resetIntent;
    PendingIntent resetSender;

    String getId;

    private static String URL_MAIN = "http://nosmoke.dothome.co.kr/main.php";
    private static String URL_MAINUPDATE = "http://nosmoke.dothome.co.kr/main_update.php";
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;

    ServiceConnection conn= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AlarmService.MyBinder myBinder = (AlarmService.MyBinder) service;
            alarmService = myBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MydayCount mydayCount = (MydayCount)getApplication();
        myReceiver = new AlarmReceiver();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.DATE_CHANGED");
        registerReceiver(myReceiver,intentFilter);
        setTitle(" ");

        getSupportActionBar().setElevation(0);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        limit_num = findViewById(R.id.limit_num);
        cpb = findViewById(R.id.cpb);
        cpb2 = findViewById(R.id.cpb2);
        button = findViewById(R.id.button);
        today_progress = findViewById(R.id.today_progress);
        save_money = findViewById(R.id.save_money);
        save_time = findViewById(R.id.save_Time);
        today_num = findViewById(R.id.today_num);
        mBtnSendData = findViewById(R.id.btnSendData);


        final HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        Intent parseIntent = getIntent();
        processIntent(parseIntent);


        if (mBluetoothAdapter == null) {
            //블루투스 권한사용 x
        } else {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, BT_REQUEST_ENABLE);
            }

        }

        resetAlarmMager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        resetIntent = new Intent(MainActivity.this,AlarmReceiver.class);
        resetSender = PendingIntent.getBroadcast(MainActivity.this,0,resetIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        resetIntent.putExtra("value",iToday_num);
        Calendar resetCal = Calendar.getInstance();
        resetCal.setTimeInMillis(System.currentTimeMillis());
        resetCal.set(Calendar.HOUR_OF_DAY,12);
        resetCal.set(Calendar.MINUTE,18);
        resetCal.set(Calendar.SECOND,0);
        resetAlarmMager.setRepeating(AlarmManager.RTC_WAKEUP,resetCal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,resetSender);


        SimpleDateFormat format = new SimpleDateFormat("MM/dd kk:mm:ss");

        arcMenu();

        /*핸들러로 블투 연결 뒤 수신된 데이터 읽기 기능*/
       mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                String readMessage = null;
                if(msg.what == BT_MESSAGE_READ){

                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    stoday_num=readMessage;
                    today_num.setText(readMessage);
                    MainUpdate();
                }

            }
        };

      // iToday_num =aaa;
        /*button.setOnClickListener(new View.OnClickListener(){
            Calendar cal = Calendar.getInstance();
            //int iToday_num=0;
            int nWeek = cal.get(Calendar.DAY_OF_WEEK);

            @Override
            public void onClick(View view){
                stoday_num = iToday_num+"";
                SharedPreferences sf = getSharedPreferences("SunCount", MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                switch (nWeek){
                    case 1:
                       // Calendar calendar = Calendar.getInstance();

                        SunCount=sf.getInt("SunCount",0);
                        SunCount++;
                        editor.putInt("SunCount", SunCount);
                        editor.commit();
                        iToday_num=SunCount;
                        stoday_num=iToday_num+"";
                       // Toast.makeText(MainActivity.this, sf.getInt("SunCount",0)+"", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        MonCount=sf.getInt("MonCount",0);
                        MonCount++;
                        editor.putInt("MonCount", MonCount);
                        editor.commit();
                        iToday_num=MonCount;
                        stoday_num=iToday_num+"";
                        Toast.makeText(MainActivity.this, sf.getInt("MonCount",0)+"",Toast.LENGTH_SHORT).show();

                        break;
                    case 3:
                        TuseCount=sf.getInt("TuseCount",0);
                        TuseCount++;
                        editor.putInt("TuseCount", TuseCount);
                        editor.commit();
                        iToday_num=TuseCount;
                        stoday_num=iToday_num+"";
                        break;
                    case 4:
                        WenCount=sf.getInt("WenCount",0);
                        WenCount++;
                        editor.putInt("WenCount", WenCount);
                        editor.commit();
                        iToday_num=WenCount;
                        stoday_num=iToday_num+"";
                        break;
                    case 5:
                        ThursCount=sf.getInt("ThursCount",0);
                        ThursCount++;
                        editor.putInt("ThursCount", ThursCount);
                        editor.commit();
                        iToday_num=ThursCount;
                        stoday_num=iToday_num+"";
                        break;
                    case 6:
                        FriCount=sf.getInt("FriCount",0);
                        FriCount++;
                        editor.putInt("FriCount", FriCount);
                        editor.commit();
                        iToday_num=FriCount;
                        stoday_num=iToday_num+"";
                        break;
                    case  7:
                        SatCount=sf.getInt("SatCount",0);
                        SatCount++;
                        editor.putInt("SatCount", SatCount);
                        editor.commit();
                        iToday_num=SatCount;
                        stoday_num=iToday_num+"";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + nWeek);

                }
                MainUpdate();
            }

        });*/

        Calendar cal = Calendar.getInstance();

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        int nYear = cal.get(Calendar.MONTH)+1;
        int nMonth = cal.get(Calendar.WEEK_OF_MONTH);

        stoday_num = iToday_num+"";
        SharedPreferences sf = getSharedPreferences("SunCount", MODE_PRIVATE);
        SharedPreferences sff = getSharedPreferences("Num", MODE_PRIVATE);
        SharedPreferences spf=getSharedPreferences("Date",MODE_PRIVATE);
        SharedPreferences.Editor edit = spf.edit();
        SharedPreferences.Editor editor = sf.edit();
        //Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
        switch  (nMonth){
            case 1:
                break;
            case 2:
                Toast.makeText(MainActivity.this, "2주차 입니다.", Toast.LENGTH_SHORT).show();

                break;
            case 3:
                editor.putInt("ThreeCount",sf.getInt("SunCount",0)+sf.getInt("MonCount",0)+sf.getInt("TuseCount",0)+sf.getInt("WenCount",0)
                +sf.getInt("ThursCount",0)+sf.getInt("FriCount",0)+sf.getInt("SatCount",0));
                editor.commit();
               // Toast.makeText(MainActivity.this, sf.getInt("ThreeCount",0)+"", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(MainActivity.this, "4주차 입니다.", Toast.LENGTH_SHORT).show();

                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
        }
        switch (nYear){
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
               // Toast.makeText(MainActivity.this, "11월 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case 12:
                //editor.putInt("DecCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
               // MonCount = sf.getInt("MonCount",0);
               // editor.commit();
//                edit.putString("Cur_Sunday1",simpleDateFormat.fo
               // Toast.makeText(MainActivity.this, "12월 입니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        switch (nWeek){
            case 1:
                editor.putInt("SunCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0));
                SunCount = sf.getInt("SunCount",0);
             //   editor.commit();
               // edit.putString("Cur_Sunday",simpleDateFormat.format(calendar));
                edit.commit();
                resetAlarm(this);
               // Toast.makeText(MainActivity.this, sf.getInt("SunCount",0)+"", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                resetAlarm(this);
                editor.putInt("MonCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
                MonCount = sf.getInt("MonCount",0);
                editor.commit();

                break;
            case 3:
                editor.putInt("TuseCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
                TuseCount = sf.getInt("TuseCount",0);
                editor.commit();
                break;
            case 4:
                editor.putInt("WenCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
                WenCount = sf.getInt("WenCount",0);
                editor.commit();
                break;
            case 5:
                editor.putInt("ThursCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
                ThursCount = sf.getInt("ThursCount",0);
                editor.commit();
                break;
            case 6:
                editor.putInt("FriCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
                FriCount = sf.getInt("FriCount",0);
                editor.commit();
                break;
            case  7:
                editor.putInt("SatCount",sff.getInt("Limit_num",0)-sff.getInt("Today_num",0) );
                SatCount = sf.getInt("SatCount",0);
                editor.commit();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nWeek);

        }

    }

@Override
    protected void onDestroy(){
            super.onDestroy();;
            unregisterReceiver(myReceiver);
    }

    private Date today(){
        return Calendar.getInstance().getTime();
    }
    private Date resetTime (Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    private int getWeeksBetween(Date a, Date b){
        if(b.before(a)){
            return -getWeeksBetween(b, a);
        }
        a = resetTime(a);
        b = resetTime(b);

        Calendar cal = new GregorianCalendar();
        cal.setTime(a);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        int weeks = 0;
        while(cal.getTime().before(b)) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);

            weeks++;
        }
        return weeks;
    }
    private int BetweenWeeks(String strStartDate, String strEndDate){

        String strFormat = "yyyy-MM-dd";    //strStartDate 와 strEndDate 의 format
        SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
        int aaa=0;
        try {
            Date startDate = sdf.parse(strStartDate);
            Date endDate = sdf.parse(strEndDate);

            aaa = getWeeksBetween(startDate,endDate);

        }catch(ParseException e) {
            e.printStackTrace();
        }
        return aaa;
    }

    private void getLimitNum(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG,response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    //가입 시 입력한 하루 평균 흡연량
                                    String strCigar_num = object.getString("cigar_num").trim();
                                    // 가입 날짜
                                    String strResist = object.getString("resist_day").trim();
                                    // 목표 날짜
                                    String strGoal_Date = object.getString("goal_date").trim();
                                    // 담배케이스 개폐시 열리는 횟수
                                    String strToday_num = object.getString("today_num").trim();
                                    //가입시 입력한 담배값 가격(20개피 기준)
                                    String strCigar_price = object.getString("cigar_price").trim();
                                    
                                    //흡연량 계산을 위해 string으로 입력된 하루 평균 흡연량을 int로 변환한다.
                                    int iCigar_num = Integer.parseInt(strCigar_num);
                                    iToday_num = Integer.parseInt(strToday_num);

                           

                                    //int s =resetIntent.getIntExtra("value",0)
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //날짜형식으로 변환하기 위해 사용된 거
                                    //cc: 오늘 날짜
                                    Calendar today = Calendar.getInstance();
                                    //오늘날짜를 문자열 형태로, sdf: SimpleDateFormat 'yyyy-MM-dd'
                                    String str_today = sdf.format(today.getTime());

                                    //가입날짜의 주차를 가져온다.
                                    Calendar c2 = Calendar.getInstance();
                                    c2.setTime(sdf.parse(strResist));
                                    String week = String.valueOf(c2.get(Calendar.WEEK_OF_MONTH));

                                    // mysql date 형식 yyyy-MM-dd라서 -를 기준으로 분리
                                    String stryyyy = strResist.split("-")[0];
                                    String strMM = strResist.split("-")[1];
                                    String strdd = strResist.split("-")[2];

                                    int y = Integer.parseInt(stryyyy);
                                    int m = Integer.parseInt(strMM)-1;
                                    int w = Integer.parseInt(week);

                                    c2.set(Calendar.YEAR,y);
                                    c2.set(Calendar.MONTH,m);
                                    c2.set(Calendar.WEEK_OF_MONTH,w);
                                    c2.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
                                   // c2.add(c2.DATE,7);->월요일 첫쨋날 기준 없으면 일요일이 일주일의 첫쨋날

                                    String c_resisterday = sdf.format(c2.getTime());

                                    Calendar c3 = Calendar.getInstance();
                                    c3.setTime(sdf.parse(strGoal_Date));
                                    String week2 = String.valueOf(c3.get(Calendar.WEEK_OF_MONTH));

                                    //
                                    String stryear = strGoal_Date.split("-")[0];
                                    String strmonth = strGoal_Date.split("-")[1];
                                    String strday = strGoal_Date.split("-")[2];

                                    //목표 날짜의 연도를 int형으로 변환한다.
                                    int yy = Integer.parseInt(stryear);
                                    //목표 날짜의 달을 int형으로 변환한다.
                                    int mm = Integer.parseInt(strmonth)-1;
                                    //목표 날짜의 주차를 int형으로 변환한다.
                                    int ww = Integer.parseInt(week2);

                                    c3.set(Calendar.YEAR,yy);
                                    c3.set(Calendar.MONTH,mm);
                                    c3.set(Calendar.WEEK_OF_MONTH,ww);
                                    c3.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);

                                    String c_goal_date = sdf.format(c3.getTime());
                                    int i_Cigarnum = Integer.parseInt(strCigar_num);

                                    // 하루 제한 횟수를 계산하기위해 가입날짜와 목표날짜 사이의 주차를 구해 평균흡연갯수로 나눈 후 주차를 일차로 바꾸기 위해 7을 곱함,일주일->7일
                                    final int strLimit_num=(BetweenWeeks(c_resisterday,c_goal_date)/i_Cigarnum)*7; //주*7일->일자를 구함


                                    Calendar calendar = Calendar.getInstance();
                                    //calendar의 날짜를 가입날짜로 setting
                                    calendar.setTime(sdf.parse(strResist));
                                    calendar.add(Calendar.DATE, strLimit_num);  // number of days to add
                                    String calendar1 = sdf.format(calendar.getTime());

                                    //시작날짜를 가입한날짜를 가져 온다.
                                    Date startDate = sdf.parse(strResist);
                                    // 끝나는 날짜를 목표날짜를 가져온다.
                                    Date endDate = sdf.parse(strGoal_Date);
                                    //오늘 날짜를 가져온다.
                                    Date _today = sdf.parse(str_today);

                                    double diffDay_GR = (endDate.getTime() - startDate.getTime()) / (24*60*60*1000); //시작날짜(가입날짜)와 종료날짜(목표날짜) 사이의 기간
                                    double diffDay_TR = (_today.getTime()-startDate.getTime())/(24*60*60*1000); // 오늘날짜와 시작날짜 사이의 기간
                                    double diffDay = (diffDay_TR/diffDay_GR);
                                    long diffDay_percent =(long)(diffDay*100);
                                    //int iLimit_num;
                                    // 두개의 문자열을 비교하고 int형 값을 반환하는 매소드 calender와 today가 같으면 0반환 calendar가 크면 양수 반환 작으면 음수 반환
                                    /*************이부분은 하루 제한횟수를 구하는 건데 수정해야함.*************/
                                    int result = calendar.compareTo(today);
                                    if(result<=0){
                                        iCigar_num--;
                                        iToday_num--;
                                        _today = sdf.parse(str_today);
                                        calendar.add(Calendar.DATE, strLimit_num);
                                    }
                                    /************************************************************************/
                                    final int finalICigar_num = iCigar_num;
                                    mBtnSendData.setOnClickListener(new Button.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if(mThreadConnectedBluetooth != null) {
                                                mThreadConnectedBluetooth.write(finalICigar_num +"");
                                                //mLimit_num.setText("");
                                            }
                                        }
                                    });
                                    //Toast.makeText(MainActivity.this, finalICigar_num+"", Toast.LENGTH_SHORT).show();
                                    //퍼센트를 먼저 더블 형으로 가져온다.
                                    double num_percent1 = Double.valueOf(iCigar_num - Integer.valueOf(strToday_num))/iCigar_num;
                                    //더블 형으로 가져온 퍼센트를 백분율로 변환한다.
                                    int num_percent2 = (int)(num_percent1 * 100);


                                    int oneCigar_money = Integer.parseInt(strCigar_price)/20; //한갑 20개피 기준 담배 한개피의 가격
                                    //절약한 돈을 계산한다.
                                    int saveMoney = (Integer.parseInt(strCigar_num)*oneCigar_money)-((Integer.parseInt(strCigar_num) - (Integer.parseInt(strCigar_num)- Integer.parseInt(strToday_num))*oneCigar_money));
                                    //절약한 시간을 계산한다.
                                    int saveTime = (300*(Integer.parseInt(strCigar_num)))-((Integer.parseInt(strCigar_num) - Integer.parseInt(strToday_num))*300);
                                    //
                                    //
                                    // Toast.makeText(MainActivity.this, strToday_num, Toast.LENGTH_SHORT).show();

                                    int saveDay = saveTime / (60*60*24);    //절약한 일
                                    int saveHour = (saveTime-saveDay*60*60*24)/(60*60); //절약한 시간
                                    int saveMinute = (saveTime-saveDay*60*60*24-saveHour*3600)/60; //절약한 분
                                    int saveSecond = saveTime % 60;//절약한 초

                                    // calendar를 다 다르게 사용한 이유: 캘린더를 다 같은 걸 사용하면 값이 계속 마지막에 들어가는 걸로 바뀜
                                    //iToday_num=Integer.parseInt(stoday_num);
                                    //하루 제한 담배개피 갯수
                                    limit_num.setText(iCigar_num+"");
                                    //Toast.makeText(MainActivity.this, ""+str_today, Toast.LENGTH_SHORT).show();
                                    //오늘 제한횟수 퍼센트 수
                                    today_progress.setText(diffDay_percent+"%");
                                    //담배케이스 개폐시 열리는 횟수
                                    /***********************************************/
                                   // today_num.setText(iToday_num+"");
                                    SharedPreferences sf = getSharedPreferences("Num", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sf.edit();
                                    editor.putInt("Today_num", iToday_num);
                                    editor.putInt("Limit_num",iCigar_num);
                                  //  Toast.makeText(MainActivity.this, iToday_num+"", Toast.LENGTH_SHORT).show();
                                    editor.commit();
                                    today_num.setText(sf.getInt("Today_num",0)+"");

                                    /***********************************************/
                                    //현재날짜와 목표날짜 사이의 퍼센트 바
                                    cpb.setProgress((int)diffDay_percent);
                                    // 하루 개피갯수와 제한갯수 사이의 퍼핸트 바
                                    cpb2.setProgress(num_percent2);
                                    //절약한 돈
                                    save_money.setText(saveMoney+"원");
                                    //절역한 시간
                                    save_time.setText(saveDay+"일"+saveHour+"시간"+saveMinute+"분"+saveSecond+"초");

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error Reading Detail "+e.toString(), Toast.LENGTH_SHORT).show();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error Reading Detail "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params = new HashMap<>();
                params.put("id", getId);
                params.put("today_num",iToday_num+"");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onNewIntent(Intent intent){
        processIntent(intent);

        super.onNewIntent(intent);
    }
    private void processIntent(Intent intent){
        iToday_num=intent.getIntExtra("iToday_num",iToday_num);
    }
    private void MainUpdate() {

        final String today_num = stoday_num.trim();
        final String id = getId;
        
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAINUPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                //Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                //sessionManager.createsession(name, user_id,String.valueOf(age),gender,String.valueOf(cigar_num),String.valueOf(cigar_price),id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                           // progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error"+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("today_num", today_num);
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onResume(){
        super.onResume();

        getLimitNum();
      //  registerReceiver(listener,filter);

    }
    @Override
    protected void onPause(){
        super.onPause();
       // unregisterReceiver(listener);
    }


    private void arcMenu() {
        //arc menu
        final AllAngleExpandableButton button = (AllAngleExpandableButton) findViewById(R.id.arc_button);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.color.Transparent, R.drawable.profile2, R.drawable.chart, R.drawable.calender,R.drawable.trophy};
        int[] color = {R.color.Transparent, R.color.colorAccent, R.color.colorAccent, R.color.colorAccent, R.color.colorAccent};
        for (int i = 0; i < 5; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 7);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }


    //메뉴 버튼 클릭 시 이벤트
    private void setListener(AllAngleExpandableButton button) {
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                if(index==3) {
                    Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent);

                }
                if(index==2) {
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    startActivity(intent);

                }
                if(index==1) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);

                }
                if(index==4) {
                    Intent intent = new Intent(MainActivity.this,TrophyActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onExpand() {
                //showToast("onExpand");
            }
            @Override
            public void onCollapse() {
                //showToast("onCollapse");
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_bluetooth:
                listPairedDevices();
                return true;
            case R.id.action_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    void listPairedDevices() {
        if (mBluetoothAdapter.isEnabled()) { //블루투스 활성화 상태여부 확인
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) { //페어링된 장치 존재시
                AlertDialog.Builder builder = new AlertDialog.Builder(this); //알림창 객체 생성
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>(); //페어링된 장치명 추가
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]); //페어링된 장치 수 표시

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) { //장치명 클릭시 connectSelectedDevice 메소드로 장치명 전달
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create(); //리스트로 추가된 알림창을 실제로 띄워주는 함수
                alert.show();
            } else {//페어링된 장치가 없을 경우
                Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
        else {//비활성화 상태일 경우
            Toast.makeText(getApplicationContext(), "블루투스가 비활성화 되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    /*블루투스 연결하는 메소드*/
    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {//장치의 이름과 매개변수(listPairedDevices함수)가 같을 경우
                mBluetoothDevice = tempDevice;//장치의 주소값 가져온다
                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID); //uuid 호출하여 소켓가져오기 > 장치에 연결된 소켓 초기화
            mBluetoothSocket.connect(); //연결 시작
            /*데이터 수신시, 언제 받을지 모르므로 그를 위한 쓰레드를 따로 만들어 처리, 쓰레드 생성*/
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    public static void resetAlarm(Context context){
        AlarmManager resetAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent resetIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent resetSender = PendingIntent.getBroadcast(context,0,resetIntent,0);
        Calendar resetCal = Calendar.getInstance();
        resetCal.setTimeInMillis(System.currentTimeMillis());
        resetCal.set(Calendar.HOUR_OF_DAY,0);
        resetCal.set(Calendar.MINUTE,0);
        resetCal.set(Calendar.SECOND,0);
        resetAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, resetCal.getTimeInMillis() +AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, resetSender);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd kk:mm:ss");
        String setResetTime = format.format(new Date(resetCal.getTimeInMillis()+AlarmManager.INTERVAL_DAY));

        Log.d("resetAlarm", "ResetHour : " + setResetTime);

    }
    //상단 오버플로우 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            super.onBackPressed();
        }
    }


    private class ConnectedBluetoothThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        /*쓰레드 초기화 과정*/
        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {//소켓을 통한 전송 처리, 데이터 전송 및 수신하는 길 구축 작업
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /*데이터 읽어오는 과정*/
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) { //계속 반복, 수신 데이터 확인
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) { //데이터 존재시,
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes); //데이터 읽기
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /*데이터 전송을 위한 메소드*/
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }

        /*블루투스 소켓을 닫는 메소드*/
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
