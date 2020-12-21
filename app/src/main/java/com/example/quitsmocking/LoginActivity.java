package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText user_id, password;
    private Button btn_login;
    private TextView link_regist;
    private ProgressBar loading;
    private static String URL_LOGIN = "http://nosmoke.dothome.co.kr/login.php";
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager =new SessionManager(this);
        loading = findViewById(R.id.loading);
        user_id = findViewById(R.id.user_id);
        password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login);
        link_regist = findViewById(R.id.link_regist);

        btn_login.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String mUser_id = user_id.getText().toString().trim();
                String mPass = password.getText().toString().trim();

                if (!mUser_id.isEmpty() || !mPass.isEmpty()){
                    Login(mUser_id, mPass);
                }else{
                    user_id.setError("아이디를 입력해 주세요");
                    password.setError("비밀번호를 입력해 주세요");
                }
            }
        });
        link_regist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResisterActivity.class));
            }
        });

    }
    private void Login(final String user_id, final String password){
        loading.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")){
                                for (int i=0; i< jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String name = object.getString("name").trim();
                                    String user_id = object.getString("user_id").trim();
                                    String age = object.getString("age").trim();
                                    String gender = object.getString("gender").trim();
                                    String cigar_num = object.getString("cigar_num").trim();
                                    String cigar_price = object.getString("cigar_price").trim();
                                    String goal_date = object.getString("goal_date").trim();
                                    String id = object.getString("id").trim();
                                    String resist_day = object.getString("resist_day").trim();

                                    sessionManager.createsession(name, user_id,age,gender,cigar_num,cigar_price, id);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("user_id",user_id);
                                    intent.putExtra("age",age);
                                    intent.putExtra("gender",gender);
                                    intent.putExtra("goal_date",goal_date);
                                    intent.putExtra("cigar_num",cigar_num);
                                    intent.putExtra("cigar_price",cigar_price);
                                    intent.putExtra("resist_day", resist_day);
                                    startActivity(intent);
                                    Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                    //Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                                    //intent1.putExtra("name", name);
                                    //intent1.putExtra("user_id",user_id);
                                    //intent1.putExtra("age",age);
                                    ////intent.putExtra("gender",gender);
                                    //intent1.putExtra("cigar_num",cigar_num);
                                    //intent1.putExtra("cigar_price",cigar_price);
                                    //startActivity(intent1);

                                    loading.setVisibility(View.GONE);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading.setVisibility(View.GONE);
                            btn_login.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this,"Error "+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        loading.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this,"Error "+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",user_id);
                params.put("password",password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
