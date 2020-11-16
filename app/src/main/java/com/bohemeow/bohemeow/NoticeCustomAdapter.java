package com.bohemeow.bohemeow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NoticeCustomAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<NoticeData> noticeData;

    public NoticeCustomAdapter(Context context, ArrayList<NoticeData> data) {
        mContext = context;
        noticeData = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public NoticeData getData(NoticeData noticeData){
        return noticeData;
    }

    @Override
    public int getCount() {
        return noticeData.size();
    }

    @Override
    public NoticeData getItem(int position) {
        return noticeData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.notice_list_view, null);

        TextView timeText = view.findViewById(R.id.timeText);
        TextView titleText = view.findViewById(R.id.titleText);
        TextView contentText = view.findViewById(R.id.contentText);
        TextView authorText = view.findViewById(R.id.authorText);
        ImageView catType = view.findViewById(R.id.imageView45);

        int[] icons = {R.drawable.cathead_null, R.drawable.hanggangic, R.drawable.bameeic, R.drawable.chachaic,
                R.drawable.ryoniic, R.drawable.moonmoonic, R.drawable.popoic,R.drawable.taetaeic, R.drawable.sessakic};

        timeText.setText(noticeData.get(position).time);
        titleText.setText(noticeData.get(position).title);
        contentText.setText(noticeData.get(position).content);
        authorText.setText(noticeData.get(position).author);
        catType.setImageResource(icons[noticeData.get(position).catType]);

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}