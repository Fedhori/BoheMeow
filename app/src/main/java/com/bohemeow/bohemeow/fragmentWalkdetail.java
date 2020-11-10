package com.bohemeow.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentWalkdetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentWalkdetail extends Fragment {


    TextView total_count;
    TextView total_dis;
    TextView total_time;


    private static final String ARG_COUNT = "param1";
    private static int counter;

    public fragmentWalkdetail() {
        // Required empty public constructor
    }

    public static fragmentWalkdetail newInstance(Integer counter) {
        fragmentWalkdetail fragment = new fragmentWalkdetail();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_walkdetail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity();
        //user_name = ((MainConfigActivity)context).user_name;
        final UserData userData = ((MainConfigActivity)context).userData;

        total_count = view.findViewById(R.id.total_count2);
        total_dis = view.findViewById(R.id.total_dis2);
        total_time = view.findViewById(R.id.total_time2);

        total_count.setText(Integer.toString(userData.totalWalkCount) + "번");

        int distance = (int) userData.totalWalkLength;
        total_dis.setText(Integer.toString(distance) + "m");

        long totalTime = userData.realWalkTime; // ms
        long hour;
        long minute;
        long second;

        hour = totalTime / 3600000;
        totalTime %= 3600000;
        minute = totalTime / 60000;
        totalTime %= 60000;
        second = totalTime / 1000;

        String timeText = "";
        if(hour >= 10){
            timeText += String.valueOf(hour);
        }
        else{
            timeText += "0" + hour;
        }
        timeText += ":";
        if(minute >= 10){
            timeText += String.valueOf(minute);
        }
        else{
            timeText += "0" + minute;
        }
        timeText += ":";
        if(second >= 10){
            timeText += String.valueOf(second);
        }
        else{
            timeText += "0" + second;
        }
        total_time.setText(timeText);

    }

}
