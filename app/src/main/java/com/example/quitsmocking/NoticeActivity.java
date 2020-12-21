package com.example.quitsmocking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.Arrays;
import java.util.List;

public class NoticeActivity extends AppCompatActivity {

    private RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        setTitle("공지사항");

        init();

        getData();


    }
    private void init(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
    }
    private void getData(){
        List<String> listTitle = Arrays.asList("첫번째 공지사항입니다.");
        List<String>listContent = Arrays.asList(
                "2019-12-15"
        );
        List<String>listContent2 = Arrays.asList(
          "안녕하세요. 첫 번째 공지사항입니다."
        );
        for (int i =0;i<listTitle.size();i++){
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));;
            data.setContent2(listContent2.get(i));
            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        adapter.notifyDataSetChanged();
    }
}






