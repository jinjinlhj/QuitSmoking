package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    MaterialCalendarView materialCalendarView;
    private final OnedayDecorator onedayDecorator = new OnedayDecorator();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("캘 린 더");
        setContentView(R.layout.activity_calendar);
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2018, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorator(onedayDecorator);// 오늘 날짜 표시
        materialCalendarView.setDateTextAppearance(R.style.CustomTextAppearance);
        materialCalendarView.setHeaderTextAppearance(R.style.CustomTextAppearance);
        materialCalendarView.setWeekDayTextAppearance(R.style.CustomTextAppearance);
    }


    //오버플로우 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }
}
