package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Integer.parseInt;

public class ResisterActivity extends AppCompatActivity {

    private Calendar cal = Calendar.getInstance();
    private String today;
    private EditText name, user_id, password, c_password,age,cigar_price, cigar_num;
    private Button btn_regist;
    private int today_num;
    private int year , month, day;
    private ProgressBar loading;
    //private ImageView profile_image;
    public RadioGroup mRadioGroupgender;
    private  RadioButton radioButtonM, radioButtonF;
    private String gender;
    private Spinner mSpinneryear;
    private Spinner mSpinnermonth;
    private Spinner mSpinnerday;
    private String goal_date;
    private static String URL_RESGIST = "http://nosmoke.dothome.co.kr/register.php";
    private static String URL_UPLOAD = "http://nosmoke.dothome.co.kr/upload.php";

    private Bitmap bitmap;
    CircleImageView profile_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resister);

        // 금연목표일 spinner
        mSpinneryear = (Spinner)findViewById(R.id.spinner_year);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this, R.array.date_year,
                android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinneryear.setAdapter(yearAdapter);

        mSpinnermonth = (Spinner)findViewById(R.id.spinner_month);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.date_month,
                android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnermonth.setAdapter(monthAdapter);

        mSpinnerday = (Spinner)findViewById(R.id.spinner_day);
        ArrayAdapter dayAdapter = ArrayAdapter.createFromResource(this,R.array.date_day,
                android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerday.setAdapter(dayAdapter);


        loading = findViewById(R.id.loading);
        name = findViewById(R.id.name);
        user_id=findViewById(R.id.user_id);
        age=findViewById(R.id.age);
        cigar_num=findViewById(R.id.cigar_num);
        cigar_price=findViewById(R.id.cigar_price);
        //gender=findViewById(R.id.gender_F);
        password = findViewById(R.id.password);
        mRadioGroupgender = findViewById(R.id.radioGroup);
        c_password=findViewById(R.id.c_password);
        btn_regist = findViewById(R.id.btn_regist);
        radioButtonM = findViewById(R.id.gender_M);
        radioButtonF = findViewById(R.id.gender_F);
        profile_image = findViewById(R.id.profile_image);
        year = cal.get(cal.YEAR);
        month = cal.get(cal.MONTH)+1;
        day = cal.get(cal.DATE);
        today = year+"-"+month+"-"+day;


        if(radioButtonM.isChecked()==true){
            gender = "M";
        }
        else if(radioButtonF.isChecked()==true){
            gender="F";
        }
        profile_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                chooseFile();
            }

        });
        btn_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(c_password.getText().toString()))
                {
                    Regist();
                }
                else
                {
                    c_password.setError("비밀번호가 일치하지 않습니다.");
                    //Toast.makeText(ResisterActivity.this, "비밀번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
               /* else if(parseInt(password.getText().toString())<8)
                {
                    password.setError("8자 이상 입력해 주세요.");
                }*/
                //String mUser_id = user_id.getText().toString().trim();
               // String mPass = password.getText().toString().trim();

               /* if (!mUser_id.isEmpty() || !mPass.isEmpty()){
                    Login(mUser_id, mPass);
                }else{
                    user_id.setError("아이디를 입력해 주세요");
                    password.setError("비밀번호를 입력해 주세요");
                }*/
            }

        });
    }
    private void Regist(){
        loading.setVisibility(View.VISIBLE);
        btn_regist.setVisibility(View.GONE);
        String spinneryear,spinnermonth,spinnerday;
        spinnerday = mSpinnerday.getSelectedItem().toString();
        spinnermonth = mSpinnermonth.getSelectedItem().toString();
        spinneryear = mSpinneryear.getSelectedItem().toString();
        goal_date = spinneryear+"-"+spinnermonth+"-"+spinnerday;

        final String name = this.name.getText().toString().trim();
        final String user_id = this.user_id.getText().toString().trim();
        final String password = this.password.getText().toString().trim();
        final String strage = this.age.getText().toString().trim();
        final String strcigar_num = this.cigar_num.getText().toString().trim();
        final String strcigar_price = this.cigar_price.getText().toString().trim();
        final String resist_day = this.today.trim();
        final String goal_date = this.goal_date.trim();
        final String gender = this.gender.trim();
        final String strtoday_num = this.cigar_num.getText().toString().trim();


        final int age = parseInt(strage);
        final int cigar_num = parseInt(strcigar_num);
        final int cigar_price = parseInt(strcigar_price);
        final int today_num = parseInt(strtoday_num);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_RESGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(ResisterActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResisterActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ResisterActivity.this, "Register Error!"+e.toString(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            btn_regist.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Toast.makeText(ResisterActivity.this, "Register Error!"+error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        btn_regist.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("user_id", user_id);
                params.put("password",password);
                params.put("gender", gender);
                params.put("age",String.valueOf(age));
                params.put("goal_date", goal_date);
                params.put("cigar_num",String.valueOf(cigar_num));
                params.put("cigar_price",String.valueOf(cigar_price));
                params.put("today_num",String.valueOf(today_num));
                params.put("resist_day", resist_day);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK&& data != null && data.getData() !=null){
            Uri filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profile_image.setImageBitmap(bitmap);

            }catch (IOException e){
                e.printStackTrace();
            }
           // UploadPicture(getId, getStringImage(bitmap));

        }
    }

    private void UploadPicture(final String id, final String photo) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                       // Log.i(TAG, response.toString());
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if(success.equals("1")){
                                Toast.makeText(ResisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ResisterActivity.this, "Try Again!"+e.toString(), Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ResisterActivity.this, "Try Again!"+error.toString(), Toast.LENGTH_SHORT).show();


                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("photo", photo);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public String getStringImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodeImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        return encodeImage;
    }
}
