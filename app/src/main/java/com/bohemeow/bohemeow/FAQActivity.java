package com.bohemeow.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FAQActivity extends Activity {

    ArrayList<FAQDAta> faqDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_f_a_q);

        Intent intent = getIntent();

        FAQDAta[] faqData = (FAQDAta[]) intent.getSerializableExtra("faqData");

        int length = faqData.length;

        // add FAQ data
        faqDataList = new ArrayList<FAQDAta>();
        for(int i = 0;i<length;i++){
            faqDataList.add(faqData[i]);
        }

        ListView listView = (ListView)findViewById(R.id.FAQlistView);
        //listView.setDivider(null);
        final FAQCustomAdapter myAdapter = new FAQCustomAdapter(this,faqDataList);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                FAQDAta faqData = myAdapter.getItem(position);
                Intent intent = new Intent(FAQActivity.this, FAQPopupActivity.class);
                intent.putExtra("faqData", faqData);
                startActivity(intent);
            }
        });
    }
}