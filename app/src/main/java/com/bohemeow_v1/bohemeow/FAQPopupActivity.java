package com.bohemeow_v1.bohemeow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class FAQPopupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_f_a_q_popup);

        Intent intent = getIntent();
        FAQDAta faqData = (FAQDAta) intent.getSerializableExtra("faqData");

        TextView faqPopupQuestion = findViewById(R.id.faqPopupQuestion);
        TextView faqPopupAnswer = findViewById(R.id.faqPopupAnswer);

        faqPopupQuestion.setText(faqData.question);
        faqPopupAnswer.setText(faqData.answer);
    }
}