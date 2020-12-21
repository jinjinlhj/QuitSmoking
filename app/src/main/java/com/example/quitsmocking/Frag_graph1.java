package com.example.quitsmocking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Frag_graph1 extends Fragment {
    private BarChart mChart;
    private View _selectedItemView;
    private Spinner sItems;
    private Button button;
    private int SunCount, MonCount, TuseCount, WendsCount, ThurseCount, FriCount, SatCount;
   // final static MainActivity mainActivity= new MainActivity();

   // private static int SunCount = mainActivity.SunCount;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_graph1, container, false);
        List<String> spinnerArray = new ArrayList<String>();


        Calendar calendar = new GregorianCalendar(Locale.KOREA);
        int last_week = getWeek() - 1;      //저번 주
        int last2_week = getWeek() - 2;     //저저번 주
        int cur_month = getMonth();       //현재 달
        int cur_year = calendar.get(Calendar.YEAR);   //현재 년도

        Bundle extra = getArguments();
        //SunCount = extra.getInt("SunCount");
        if (extra != null){
            extra = getArguments();
            SunCount = extra.getInt("SunCount");
            MonCount = extra.getInt("MonCount");
        }
        //Toast.makeText(getActivity(), M, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(),SunCount+"",Toast.LENGTH_SHORT).show();
      //  Toast.makeText(this.getActivity(), SunCount+"", Toast.LENGTH_SHORT).show();
        /********************************************spinner*************************************************************/
        //현재 주의 일요일과 현재 주의 토요일을 구해 일주일 표현
        String Curweek = getCurSunday() + "~" + getCurSaturday();
        //저번 주의 일요일과 현재 주의 토요일을 구해 일주일 표현
        String Lastweek = getSunday(cur_year, cur_month, last_week) + "~" + getSaturday(cur_year, cur_month, last_week);
        //저저번 주의 일요일과 현재 주의 토요일을 구해 일주일 표현
        String Lastweek1 = getSunday(cur_year, cur_month, last2_week) + "~" + getSaturday(cur_year, cur_month, last2_week);
        spinnerArray.add(Curweek);
        spinnerArray.add(Lastweek);
        spinnerArray.add(Lastweek1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.sItems = (Spinner) view.findViewById(R.id.graph_week);
        sItems.setAdapter(adapter);
        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (selectedItemView != null) {
                    _selectedItemView = selectedItemView;
                    ((TextView) selectedItemView).setTextColor(Color.WHITE);
                } else {

                    ((TextView) _selectedItemView).setTextColor(Color.WHITE);
                }
                if(position==0){
                    SpinnerZero();
                }
                else if(position==1){
                    SpinnerOne();
                }
                //((TextView)parentView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
/************************************************************************************************************************/


        /*this.button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            Calendar cal = Calendar.getInstance();
            int nWeek = cal.get(Calendar.DAY_OF_WEEK);
            @Override
            public void onClick(View view){
                switch (nWeek){
                    case 1:
                        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(getContext(), "5", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(getContext(), "6", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        Toast.makeText(getContext(), "7", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });*/
        mChart = (BarChart) view.findViewById(R.id.chart);
        mChart.setDrawBarShadow(false);
        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        /*xLable*/
        final ArrayList<String> xLable = new ArrayList<>();
        xLable.add("일");
        xLable.add("월");
        xLable.add("화");
        xLable.add("수");
        xLable.add("목");
        xLable.add("금");
        xLable.add("토");

        mChart.getLegend().setEnabled(false);
        mChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        mChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
        mChart.getLegend().setDrawInside(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setCenterAxisLabels(false);
        //x축 위치 아래쪽
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //x축 선 그릴 건지
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1f);
        //x축 그리드 선 그릴 건지
        xAxis.setDrawGridLines(false);
        //x축 텍스트 컬러
        xAxis.setTextColor(Color.WHITE);
        //x측 텍스트 사이즈
        xAxis.setTextSize(15);
        //x축 선 색
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLable));


        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setTextSize(12);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setAxisMinimum(0);
        yAxis.setDrawGridLines(false);
        yAxis.setGranularity(2);
        yAxis.setLabelCount(4, true);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);



        mChart.getAxisRight().setEnabled(false);
        return view;
    } // 현재 주의 일요일 구하기

    private  void SpinnerZero(){

        /*그래프 값*/
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0,SunCount));//일
        entries.add(new BarEntry(1,MonCount));//월
        entries.add(new BarEntry(2,0));//화
        entries.add(new BarEntry(3,0));//수
        entries.add(new BarEntry(4,0));//목
        entries.add(new BarEntry(5,0));//금
        entries.add(new BarEntry(6,0));//토

        BarDataSet set = new BarDataSet(entries, "entries");
        set.setColor(Color.WHITE);

        set.setHighlightEnabled(false);
        set.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);
        BarData data = new BarData(dataSets);

        float barWidth = 0.3f;

        data.setBarWidth(barWidth);
        //xAxis.setAxisMaximum(8);
        mChart.setData(data);
        mChart.setScaleEnabled(false);
        //mChart.setVisibleXRangeMaximum(8f);
        mChart.invalidate();

    }
    private void SpinnerOne(){
        /*그래프 값*/
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 5));//일
        entries.add(new BarEntry(1,0));//월
        entries.add(new BarEntry(2,0));//화
        entries.add(new BarEntry(3,0));//수
        entries.add(new BarEntry(4,0));//목
        entries.add(new BarEntry(5,0));//금
        entries.add(new BarEntry(6,0));//토

        BarDataSet set = new BarDataSet(entries, "entries");
        set.setColor(Color.WHITE);

        set.setHighlightEnabled(false);
        set.setDrawValues(false);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);
        BarData data = new BarData(dataSets);

        float barWidth = 0.3f;

        data.setBarWidth(barWidth);
        //xAxis.setAxisMaximum(8);
        mChart.setData(data);
        mChart.setScaleEnabled(false);
        //mChart.setVisibleXRangeMaximum(8f);
        mChart.invalidate();

    }
    public static String getCurSunday(){
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd");
        Calendar c1=Calendar.getInstance();
        c1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String formatDate = sdfNow.format(c1.getTime());
        return formatDate;
    }
    //현재 주의 토요일 구하기
    public static String getCurSaturday(){
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd");
        Calendar c1=Calendar.getInstance();
        c1.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        String formatDate = sdfNow.format(c1.getTime());
        return formatDate;
    }
    //특정 날짜가 속해있는 주의 일요일 구하기
    public static String getSunday(int yyyy,int mm, int wk){
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd");
        Calendar c1 = Calendar.getInstance();

        int y = yyyy;
        int m = mm;
        int w = wk;

        c1.set(Calendar.YEAR,y);
        c1.set(Calendar.MONTH, m);
        c1.set(Calendar.WEEK_OF_MONTH,w);
        c1.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);

        String formatDate = sdfNow.format(c1.getTime());
        return formatDate;
    }
    //특정 날짜가 속해있는 주의 토요일 구하기
    public static String getSaturday(int yyyy,int mm, int wk){
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd");
        Calendar c1 = Calendar.getInstance();

        int y = yyyy;
        int m = mm;
        int w = wk;

        c1.set(Calendar.YEAR,y);
        c1.set(Calendar.MONTH, m);
        c1.set(Calendar.WEEK_OF_MONTH,w);
        c1.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);

        String formatDate = sdfNow.format(c1.getTime());
        return formatDate;
    }

    //현재 주 구하기(몇째 주인지)
    public static int getWeek(){
        Calendar c1 = Calendar.getInstance();
        String week = String.valueOf(c1.get(Calendar.WEEK_OF_MONTH));
        int week1=Integer.parseInt(week);
        return week1;
    }
    //현재 달 구하기
    public static int getMonth(){
        Calendar c1 = Calendar.getInstance();
        String month = String.valueOf(c1.get(Calendar.MONTH));
        int month1=Integer.parseInt(month);
        return month1;
    }
}
