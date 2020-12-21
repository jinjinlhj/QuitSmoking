package com.example.quitsmocking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "DateReceiver";

    //public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"날짜가 변경되었습니다.");
            SharedPreferences prefs = context.getSharedPreferences("Num",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("Today_num",0 );
        Toast.makeText(context, "변경", Toast.LENGTH_SHORT).show();


    }
}
