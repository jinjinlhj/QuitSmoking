package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TrophyActivity extends AppCompatActivity {

    ListView listView;
    private static String URL_TROPHY = "http://nosmoke.dothome.co.kr/trophy.php";
    String getId;
    SessionManager sessionManager;
    private String str_resist;
    long ToDate;
    long ToDateNum;
    long ToColorNum;
   // MydayCount myday;
    //long aaaa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("챌 린 지");
        setContentView(R.layout.activity_trophy);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);
        /*리스트에 아이템 추가*/
        listView = (ListView)this.findViewById(R.id.listView);
        ArrayList<String> items = new ArrayList<>();
        items.add("금연 1일 째");
        items.add("금연 3일 째");
        items.add("금연 5일 째");
        items.add("금연 10일 째");
        items.add("금연 25일 째");
        items.add("금연 50일 째");
        items.add("금연 100일 째");
        items.add("금연 300일 째");

        CustomAdapter adapter = new CustomAdapter(this, 0, items);
        listView.setAdapter(adapter);
     //   myday = (MydayCount)getApplication();



    }
    private void getDate(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TROPHY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1")){
                                for (int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    str_resist = object.getString("resist_day").trim();
                                    SharedPreferences sf = getSharedPreferences("Date", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sf.edit();
                                    editor.putLong("resistTtoday",CalendarDate(str_resist));
                                    editor.commit();
                                   // Toast.makeText(TrophyActivity.this, sf.getLong("resistTtoday",0)+"", Toast.LENGTH_SHORT).show();

                                    // 가입 날짜

                                   // Toast.makeText(TrophyActivity.this, strResist, Toast.LENGTH_SHORT).show();

                                   // myday.setmDate( CalendarDate(str_resist));

                                    //Toast.makeText(TrophyActivity.this,  myday.getmDate()+"", Toast.LENGTH_SHORT).show();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progressDialog.dismiss();
                            Toast.makeText(TrophyActivity.this, "Error Reading Detail "+e.toString(), Toast.LENGTH_SHORT).show();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                       // progressDialog.dismiss();
                        Toast.makeText(TrophyActivity.this, "Error Reading Detail "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params = new HashMap<>();
                params.put("id", getId);
                // params.put("today_num",iToday_num+"");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    private long CalendarDate(String first) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar today = Calendar.getInstance();
        String strToday = format.format(today.getTime());

        Date FirstDate = format.parse(strToday);
        Date SecondDate = format.parse(first);

        ToDate = FirstDate.getTime() - SecondDate.getTime();
        ToDateNum = ToDate / (24*60*60*1000);
        ToDateNum = Math.abs(ToDateNum);
        return ToDateNum;
    }
    @Override
    protected void onResume(){

        super.onResume();
        getDate();

    }
    @Override
    protected void onPause(){
        super.onPause();
        // unregisterReceiver(listener);
    }
    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;
        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects){
            super(context, textViewResourceId, objects);
            this.items = objects;
        }
        @SuppressLint("ResourceAsColor")
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;
            if(v==null){
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v=vi.inflate(R.layout.trophy_info, null);

                SharedPreferences sf = getSharedPreferences("Date", MODE_PRIVATE);
                ToColorNum= sf.getLong("resistTtoday",0);
                Toast.makeText(TrophyActivity.this, ToColorNum+"", Toast.LENGTH_SHORT).show();

                if (position==0&&ToColorNum>=1)v.setBackgroundColor(Color.GRAY);
                if (position==1&&ToColorNum>=3)v.setBackgroundColor(Color.GRAY);
                if (position==2&&ToColorNum>=5)v.setBackgroundColor(Color.GRAY);
                if (position==3&&ToColorNum>=10)v.setBackgroundColor(Color.GRAY);
                if (position==4&&ToColorNum>=25)v.setBackgroundColor(Color.GRAY);
                if (position==5&&ToColorNum>=50)v.setBackgroundColor(Color.GRAY);
                if (position==6&&ToColorNum>=100)v.setBackgroundColor(Color.GRAY);
                if (position==7&&ToColorNum>=300)v.setBackgroundColor(Color.GRAY);
               //if (position == 0) v.setBackgroundColor(Color.GRAY);

               //Toast.makeText(TrophyActivity.this, myday.getmDate()+"", Toast.LENGTH_SHORT).show();
               //
            }
            TextView textView = (TextView)v.findViewById(R.id.trophy);
            textView.setText(items.get(position));

            final String text = items.get(position);
            return v;
        }

    }
}
