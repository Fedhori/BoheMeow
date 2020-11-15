package com.bohemeow.bohemeow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NoticeActivity extends Activity {

    ArrayList<NoticeData> noticeDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice);

        Intent intent = getIntent();

        NoticeData[] noticeData = (NoticeData[]) intent.getSerializableExtra("noticeData");

        int length = noticeData.length;

        noticeDataList = new ArrayList<NoticeData>();
        for(int i = 0;i<length;i++){
            noticeDataList.add(noticeData[i]);
        }

        ListView listView = (ListView)findViewById(R.id.notice_list_view);
        //listView.setDivider(null);
        final NoticeCustomAdapter myAdapter = new NoticeCustomAdapter(this,noticeDataList);
        listView.setAdapter(myAdapter);
    }
}