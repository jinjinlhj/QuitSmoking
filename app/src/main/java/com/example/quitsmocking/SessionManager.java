package com.example.quitsmocking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    //public RadioGroup mRadioGroupgender;
    // public  RadioButton radioButton;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAME = "NAME";
    public static final String USER_ID = "USER_ID";
    public static final String AGE = "AGE";
    public static final String GENDER = "GENDER";
    public static final String GOAL_DATE = "GOAL_DATE";
    public static final String CIGAR_NUM = "CIGAR_NUM";
    public static final String CIGAR_PRICE = "CIGAR_PRICE";
    //public static final String RESIST_DAY = "RESIST_DAY";
    public static final String ID = "ID";



    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }
    public void createsession(String name, String user_id, String age, String gender, String cigar_num, String cigar_price, String id){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name);
        editor.putString(USER_ID, user_id);
        editor.putString(AGE, age);
        editor.putString(CIGAR_NUM, cigar_num);
        editor.putString(CIGAR_PRICE, cigar_price);
        // editor.putString(RESIST_DAY, resist_day);
        editor.putString(GENDER, gender);
        //editor.putString(GOAL_DATE, goal_date);

        editor.putString(ID,id);
        editor.apply();
    }

    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN, false);
    }
    public void checkLogin(){
        if(!this.isLoggin()){
            Intent i = new Intent(context, LoginActivity.class);
            context.startActivity(i);
            ((MainActivity)context).finish();
        }
    }
    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(USER_ID, sharedPreferences.getString(USER_ID, null));
        user.put(AGE, sharedPreferences.getString(AGE, null));
        user.put(GENDER, sharedPreferences.getString(GENDER, null));
        // user.put(GOAL_DATE, sharedPreferences.getString(GOAL_DATE, null));
        user.put(CIGAR_NUM, sharedPreferences.getString(CIGAR_NUM, null));
        user.put(CIGAR_PRICE, sharedPreferences.getString(CIGAR_PRICE, null));
        user.put(ID, sharedPreferences.getString(ID, null));

        return user;
    }
    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
        ((ProfileActivity)context).finish();
    }
}
