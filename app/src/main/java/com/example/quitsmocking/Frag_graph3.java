package com.example.quitsmocking;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Frag_graph3 extends Fragment{
  private BarChart mChart;
  /*텍스트 색 흰색으로 바꾸기*/
  View view;
  private View _selectedItemView;
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_graph1, container, false);
    List<String> spinnerArray = new ArrayList<String>();
    Calendar calendar = new GregorianCalendar(Locale.KOREA);
    int cur_year = calendar.get(Calendar.YEAR);
    String CurYear = cur_year+"";
    String LastYear1= (cur_year-1)+"";
    String LastYear2=(cur_year-2)+"";

    spinnerArray.add(CurYear);
    spinnerArray.add(LastYear1);
    spinnerArray.add(LastYear2);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    Spinner sItems = (Spinner) view.findViewById(R.id.graph_week);
    sItems.setAdapter(adapter);

    sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
        if (selectedItemView != null){
          _selectedItemView = selectedItemView;
          ((TextView) selectedItemView).setTextColor(Color.WHITE);
        } else {

          ((TextView) _selectedItemView).setTextColor(Color.WHITE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    mChart = (BarChart) view.findViewById(R.id.chart);
    mChart.setDrawBarShadow(false);
    mChart.getDescription().setEnabled(false);
    mChart.setPinchZoom(false);
    mChart.setDrawGridBackground(false);

    /*xLable*/
    final ArrayList<String> xLable = new ArrayList<>();
    xLable.add("1월");
    xLable.add("2월");
    xLable.add("3월");
    xLable.add("4월");
    xLable.add("5월");
    xLable.add("6월");
    xLable.add("7월");
    xLable.add("8월");
    xLable.add("9월");
    xLable.add("10월");
    xLable.add("11월");
    xLable.add("12월");


    mChart.getLegend().setEnabled(false);
    mChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
    mChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
    mChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
    mChart.getLegend().setDrawInside(false);

    XAxis xAxis = mChart.getXAxis();
    xAxis.setCenterAxisLabels(false);
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis.setDrawAxisLine(true);
    xAxis.setGranularity(1);
    xAxis.setDrawGridLines(false);
    xAxis.setTextColor(Color.WHITE);
    xAxis.setTextSize(13);
    xAxis.setAxisLineColor(Color.WHITE);
    xAxis.setValueFormatter(new IndexAxisValueFormatter(xLable));
    xAxis.setLabelCount(12,false);
    // xAxis.setSpaceMax(5);

    YAxis yAxis = mChart.getAxisLeft();
    yAxis.setTextColor(Color.WHITE);
    yAxis.setTextSize(12);
    yAxis.setAxisLineColor(Color.WHITE);
    yAxis.setDrawGridLines(false);
    yAxis.setGranularity(2);
    yAxis.setLabelCount(4, true);
    yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);


    mChart.getAxisRight().setEnabled(false);
    /*그래프 값*/
    ArrayList<BarEntry> entries = new ArrayList<>();
    entries.add(new BarEntry(0, 10));
    entries.add(new BarEntry(1, 5));
    entries.add(new BarEntry(2, 15));
    entries.add(new BarEntry(3, 20));
    entries.add(new BarEntry(4, 2));
    entries.add(new BarEntry(5, 4));
    entries.add(new BarEntry(6, 10));
    entries.add(new BarEntry(7, 2));
    entries.add(new BarEntry(8, 2));
    entries.add(new BarEntry(9, 2));
    entries.add(new BarEntry(10, 2));
    entries.add(new BarEntry(11, 2));



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
    mChart.invalidate();
    return view;
  }
}