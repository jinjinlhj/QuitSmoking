package com.example.quitsmocking;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class GraphActivity extends AppCompatActivity implements View.OnClickListener{

    Button year_btn, month_btn, week_btn;
    FragmentManager fm;
    FragmentTransaction tran;
    private int FriCount, SatCount,SunCount,MonCount,TuseCount,WenCount,ThursCount;
    Frag_graph1 frag1;
    Frag_graph2 frag2;
    Frag_graph3 frag3;
int aaa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setTitle("통 계");
        getSupportActionBar().setElevation(0);
        year_btn = (Button)findViewById(R.id.year_btn);
        month_btn = (Button)findViewById(R.id.month_btn);
        week_btn = (Button)findViewById(R.id.week_btn);
        year_btn.setOnClickListener(this);
        month_btn.setOnClickListener(this);
        week_btn.setOnClickListener(this);

        frag1 = new Frag_graph1();
        frag2 = new Frag_graph2();
        frag3 = new Frag_graph3();

        //Intent intent = getIntent();
       // final MydayCount DayCount = (MydayCount)getApplication();
       // String SunCount1 = intent.getStringExtra("SunCount");

        SharedPreferences sf = getSharedPreferences("SunCount",MODE_PRIVATE);
        SunCount=sf.getInt("SunCount",0);
        MonCount=sf.getInt("MonCount",0);
        TuseCount=sf.getInt("TuseCount",0);
        WenCount=sf.getInt("WenCount",0);
        ThursCount=sf.getInt("ThursCount",0);
        FriCount=sf.getInt("FriCount",0);
        SatCount=sf.getInt("SatCount",0);



      //  Frag_graph1 frag_graph1=new Frag_graph1();
        Bundle bundle = new Bundle();
        bundle.putInt("SunCount",SunCount);
        bundle.putInt("MonCount",MonCount);
        bundle.putInt("TuseCount",TuseCount);
        bundle.putInt("WenCount",WenCount);
        bundle.putInt("ThursCount",ThursCount);
        bundle.putInt("FriCount",FriCount);
        bundle.putInt("SatCount",SatCount);
        frag1.setArguments(bundle);

       // Toast.makeText(this, MonCount, Toast.LENGTH_SHORT).show();
        //Bundle bundle = new Bundle();
       // bundle.putInt("SunCount",);

        //Toast.makeText(this, sf.getInt("SunCount",0)+"", Toast.LENGTH_SHORT).show();
        setFrag(0);
    }
    private void setFrag(int n) {
        //프래그먼트 매니저는 액티비티와 프래그먼트의 중간에서 서로 이어주는 역할
        fm=getSupportFragmentManager();
        //트랙잭션은 어떤 대상에 대해 추가, 제거, 변경 등의 작업을 하는 것
        //트래잭션 시작
        tran = fm.beginTransaction();
        switch (n){
            //주간그래프
            case 0:
                //frag1 = new Frag_graph1()
                tran.replace(R.id.graph_frame, frag1);
                tran.commit();
                break;
            //월간그래프
            case 1:
                //frag2 = new Frag_graph2()
                tran.replace(R.id.graph_frame, frag2);
                tran.commit();
                break;
            //연간그래프
            case 2:
                //frag2 = new Frag_graph2()
                tran.replace(R.id.graph_frame, frag3);
                tran.commit();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.week_btn:
                //버튼 색, 글자색 설정
                week_btn.setBackgroundResource(R.drawable.stroke_btn);
                week_btn.setTextColor(Color.BLACK);
                month_btn.setBackgroundResource(R.drawable.stroke);
                month_btn.setTextColor(Color.WHITE);
                year_btn.setBackgroundResource(R.drawable.stroke);
                year_btn.setTextColor(Color.WHITE);
                //주간 버튼 클릭 시 주간그래프 세팅
                setFrag(0);
                break;
            case R.id.month_btn:
                //버튼 색, 글자색 설정
                week_btn.setBackgroundResource(R.drawable.stroke);
                week_btn.setTextColor(Color.WHITE);
                month_btn.setBackgroundResource(R.drawable.stroke_btn);
                month_btn.setTextColor(Color.BLACK);
                year_btn.setBackgroundResource(R.drawable.stroke);
                year_btn.setTextColor(Color.WHITE);
                //월간 버튼 클릭 시 주간그래프 세팅
                setFrag(1);
                break;
            case R.id.year_btn:
                //버튼 색, 글자색 설정
                week_btn.setBackgroundResource(R.drawable.stroke);
                week_btn.setTextColor(Color.WHITE);
                month_btn.setBackgroundResource(R.drawable.stroke);
                month_btn.setTextColor(Color.WHITE);
                year_btn.setBackgroundResource(R.drawable.stroke_btn);
                year_btn.setTextColor(Color.BLACK);
                //연간 버튼 클릭 시 주간그래프 세팅
                setFrag(2);
                break;

        }
    }
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.overflow_menu, menu);
       return true;
   }
}
