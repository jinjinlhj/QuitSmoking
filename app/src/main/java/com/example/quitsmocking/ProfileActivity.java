package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName(); //getting the info
    private TextView name, user_id, age, cigar_num, cigar_price, textView;
    private ImageView camera;
    private Button btn_logout;
    // private String gender;
    public RadioGroup mRadioGroupgender;
    public RadioButton radioButton;
    private RadioButton radioButtonM, radioButtonF;
    private String gender;
    SessionManager sessionManager;
    String getId;
    private static String URL_READ = "http://nosmoke.dothome.co.kr/read_detail.php";
    private static String URL_EDIT = "http://nosmoke.dothome.co.kr/edit_detail.php";
    private static String URL_UPLOAD = "http://nosmoke.dothome.co.kr/upload.php";

    private Menu action;
    private Bitmap bitmap;
    CircleImageView profile_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("프 로 필");
        sessionManager = new SessionManager(this);
        // sessionManager.checkLogin();

        name = findViewById(R.id.name);
        user_id = findViewById(R.id.user_id);
        age = findViewById(R.id.age);
        cigar_num = findViewById(R.id.cigar_num);
        cigar_price = findViewById(R.id.cigar_price);
        btn_logout = findViewById(R.id.btn_logout);
        profile_image = findViewById(R.id.profile_image);
        textView = findViewById(R.id.textView);
        camera = findViewById(R.id.camera);
        mRadioGroupgender = findViewById(R.id.rg_gender);
        radioButtonM = findViewById(R.id.gender_M);
        radioButtonF = findViewById(R.id.gender_F);

        textView.setVisibility(View.GONE);
        SharedPreferences sf = getSharedPreferences("Gender", MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        if(radioButtonM.isChecked()==true){

            editor.putString("Gender", "M");
           // gender = "M";
        }
        else if(radioButtonF.isChecked()==true){
            editor.putString("Gender", "F");
            //gender= "F";
        }
        camera.setVisibility(View.GONE);
        HashMap<String, String> user = sessionManager.getUserDetail();
        getId = user.get(sessionManager.ID);

        btn_logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sessionManager.logout();
            }
        });
        profile_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                chooseFile();
            }
        });
    }

    //getUserDetail
    private void getUserDetail(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
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

                                    String strName = object.getString("name").trim();
                                    String struser_id = object.getString("user_id").trim();
                                    String strAge = object.getString("age").trim();
                                    String strGender = object.getString("gender").trim();
                                    String strCigar_num = object.getString("cigar_num").trim();
                                    String strCigar_price = object.getString("cigar_price").trim();

                                    name.setText(strName);
                                    user_id.setText(struser_id);

                                    switch (strGender){
                                        case "M":
                                            radioButtonM.setChecked(true);
                                            break;
                                        case "F":
                                            radioButtonF.setChecked(true);
                                            break;
                                    }

                                    age.setText(String.valueOf(strAge));
                                    cigar_num.setText(String.valueOf(strCigar_num));
                                    cigar_price.setText(String.valueOf(strCigar_price));

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Error Reading Detail "+e.toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error Reading Detail "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>params = new HashMap<>();
                params.put("id", getId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //name.setFocusableInTouchMode(false);
        //user_id.setFocusableInTouchMode(false);
        //age.setFocusableInTouchMode(false);


        /*radioButtonF.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        radioButtonM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/

      /*  cigar_num.setFocusableInTouchMode(false);
        cigar_price.setFocusableInTouchMode(false);

        name.setFocusable(false);
        user_id.setFocusable(false);
        age.setFocusable(false);
        cigar_num.setFocusable(false);
        cigar_price.setFocusable(false);
*/
    }
    @Override
    protected void onResume(){
        super.onResume();
        getUserDetail();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_action, menu);

        action = menu;
        action.findItem(R.id.menu_save).setVisible(false);

        return true;
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_edit:
                name.setFocusableInTouchMode(true);
                user_id.setFocusableInTouchMode(true);
                age.setFocusableInTouchMode(true);

                cigar_num.setFocusableInTouchMode(true);
                cigar_price.setFocusableInTouchMode(true);


                radioButtonF.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                radioButtonM.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                action.findItem(R.id.menu_edit).setVisible(false);
                action.findItem(R.id.menu_save).setVisible(true);
                textView.setVisibility(View.VISIBLE);
                camera.setVisibility(View.VISIBLE);

                return true;
            case R.id.menu_save:
                SaveEditDetail();
                action.findItem(R.id.menu_edit).setVisible(true);
                action.findItem(R.id.menu_save).setVisible(false);

                name.setFocusableInTouchMode(false);
                user_id.setFocusableInTouchMode(false);
                age.setFocusableInTouchMode(false);
                radioButtonF.setFocusableInTouchMode(false);
                radioButtonM.setFocusableInTouchMode(false);
                cigar_num.setFocusableInTouchMode(false);
                cigar_price.setFocusableInTouchMode(false);
                radioButtonF.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                radioButtonM.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
                name.setFocusable(false);
                user_id.setFocusable(false);
                age.setFocusable(false);
                cigar_num.setFocusable(false);
                cigar_price.setFocusable(false);
                radioButtonM.setFocusable(false);
                radioButtonF.setFocusable(false);

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    //Save
    private void SaveEditDetail() {
        SharedPreferences sf = getSharedPreferences("Gender", MODE_PRIVATE);
        this.gender=sf.getString("Gender","M");
        final String name = this.name.getText().toString().trim();
        final String user_id = this.user_id.getText().toString().trim();
        final String strage = this.age.getText().toString().trim();
        final String gender = this.gender.trim();
        final String strcigar_num = this.cigar_num.getText().toString().trim();
        final String strcigar_price = this.cigar_price.getText().toString().trim();
        final int age = Integer.parseInt(strage);
        final int cigar_num = Integer.parseInt(strcigar_num);
        final int cigar_price = Integer.parseInt(strcigar_price);
        // String strCigar_num = object.getString("cigar_num").trim();

        final String id = getId;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                Toast.makeText(ProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                sessionManager.createsession(name, user_id,String.valueOf(age),gender,String.valueOf(cigar_num),String.valueOf(cigar_price),id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Error"+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("user_id",user_id);
                params.put("age",String.valueOf(age));
                params.put("gender",gender);
                params.put("cigar_num",String.valueOf(cigar_num));
                params.put("cigar_price",String.valueOf(cigar_price));
                params.put("id", id);
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
            UploadPicture(getId, getStringImage(bitmap));

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
                                Toast.makeText(ProfileActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Try Again!"+e.toString(), Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Try Again!"+error.toString(), Toast.LENGTH_SHORT).show();


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
