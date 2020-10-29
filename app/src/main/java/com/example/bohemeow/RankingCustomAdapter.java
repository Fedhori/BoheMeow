package com.example.bohemeow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class RankingCustomAdapter extends BaseAdapter {

    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<RankData> rankData;

    int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
            R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};

    public RankingCustomAdapter(Context context, ArrayList<RankData> data) {
        mContext = context;
        rankData = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public RankData getRankData(RankData rankData){
        return rankData;
    }

    @Override
    public int getCount() {
        return rankData.size();
    }

    @Override
    public RankData getItem(int position) {
        return rankData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.rank_list_view, null);

        ConstraintLayout background = view.findViewById(R.id.background);
        ImageView catImage = (ImageView)view.findViewById(R.id.image);
        TextView rank = (TextView)view.findViewById(R.id.rank);
        TextView username = (TextView)view.findViewById(R.id.username);
        TextView point = (TextView)view.findViewById(R.id.point);

        if(rankData.get(position).rank == 1){
            background.setBackgroundResource(R.drawable.rank1);
        }
        else if(rankData.get(position).rank == 2){
            background.setBackgroundResource(R.drawable.rank2);
        }
        else if(rankData.get(position).rank == 3){
            background.setBackgroundResource(R.drawable.rank3);
        }
        else{
            background.setBackgroundResource(R.drawable.rank4);
        }
        rank.setText(rankData.get(position).rank + "");
        catImage.setImageResource(icons[rankData.get(position).catType]);
        username.setText(rankData.get(position).nickname);
        point.setText(rankData.get(position).level + "");

        return view;
    }
}
