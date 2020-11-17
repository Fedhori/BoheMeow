package com.bohemeow.bohemeow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FAQCustomAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<FAQDAta> faqData;

    public FAQCustomAdapter(Context context, ArrayList<FAQDAta> data) {
        mContext = context;
        faqData = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public FAQDAta getData(FAQDAta faqData){
        return faqData;
    }

    @Override
    public int getCount() {
        return faqData.size();
    }

    @Override
    public FAQDAta getItem(int position) {
        return faqData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.faq_list_view, null);

        TextView question = view.findViewById(R.id.question);

        question.setText(faqData.get(position).question);

        return view;
    }
}
