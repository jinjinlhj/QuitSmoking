package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> items_sub = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("설 정");
        setContentView(R.layout.activity_setting);

        /*리스트에 아이템 추가*/
        listView = (ListView) this.findViewById(R.id.listView);

        items.add("공지사항");
        items_sub.add("공지사항을 확인할 수 있습니다.");
        items.add("버전정보");
        items_sub.add("현재버전이 최신버전인지 확인할 수 있습니다.");


        SettingActivity.CustomAdapter adapter = new SettingActivity.CustomAdapter(this, 0, items, items_sub);
        listView.setAdapter(adapter);
       // listView.setOnItemClickListener(listener);

        listView.setOnItemClickListener(mItemClickListener);


    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l_position){
            if(position==1)
            {
                Intent intent = new Intent(SettingActivity.this, VersionActivity.class);
                startActivity(intent);
            }
            if(position==0)
            {
                Intent intent = new Intent(SettingActivity.this,NoticeActivity.class);
                startActivity(intent);
            }

        }
    };
    /*AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(items.get(position).equals("버전정보")){
                Intent intent = new Intent(SettingActivity.this,VersionActivity.class);
                startActivity(intent);
            }
           // Toast.makeText(SettingActivity.this, items.get(position), Toast.LENGTH_SHORT).show();

        }
    };*/

    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;
        private ArrayList<String> items_sub;
        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects, ArrayList<String> objects_sub){
            super(context, textViewResourceId, objects);
            this.items = objects;
            this.items_sub = objects_sub;
        }
        @SuppressLint("ResourceAsColor")
        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView;
            if(v==null){
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v=vi.inflate(R.layout.setting_info, null);
                // if (position == 1) v.setBackgroundColor(Color.GRAY);
            }
            TextView textView = (TextView)v.findViewById(R.id.setting);
            TextView textView1 = (TextView)v.findViewById(R.id.setting_sub);
            textView.setText(items.get(position));
            textView1.setText(items_sub.get(position));

            final String text = items.get(position);
            final String text1 = items_sub.get(position);
            return v;
        }

    }
}
