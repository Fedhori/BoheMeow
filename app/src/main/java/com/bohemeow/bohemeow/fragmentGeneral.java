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
 * Use the {@link fragmentGeneral#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentGeneral extends Fragment {

    String user_name;
    boolean isWritable;
    int n;
    int m;

    private ArrayList<post> mArrayList;
    private CustomAdapter mAdapter;

    private DatabaseReference mPostReference;


    private static final String ARG_COUNT = "param1";
    private static int counter;

    public fragmentGeneral() {
        // Required empty public constructor
    }

    public static fragmentGeneral newInstance(Integer counter) {
        fragmentGeneral fragment = new fragmentGeneral();
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
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    //RecyclerView mRecyclerView;
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity();
        user_name = ((CommunityActivity)context).username;
        isWritable = ((CommunityActivity)context).isWritable;

        //final ImageView imageViewCounter = view.findViewById(R.id.imageViewFrag);
        final TextView textViewCounter = view.findViewById(R.id.textViewFrag);

        if(counter == 0){

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main_list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setReverseLayout(true);
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mArrayList = new ArrayList<>();

            mAdapter = new CustomAdapter(mArrayList);
            mRecyclerView.setAdapter(mAdapter);

            /*
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    int totalCount = recyclerView.getAdapter().getItemCount();

                    if(lastPosition == totalCount){
                        //아이템 추가 ! 입맛에 맞게 설정하시면됩니다.
                    }
                }
            });


             */
            mAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(View v, int pos)
                {

                    Intent intent = new Intent(getContext(), PostPopupActivity.class);
                    intent.putExtra("post", mArrayList.get(pos));
                    intent.putExtra("username", user_name);
                    intent.putExtra("num", pos);
                    startActivityForResult(intent, 1);
                }
            });


            // get data

            final ValueEventListener postListener = new ValueEventListener(){

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mArrayList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        postData get = postSnapshot.getValue(postData.class);
                        if(get.isPublic == true) {
                            post data = new post(get.username, get.content, get.tags, get.time, get.uri, get.level, get.catType, get.isPublic);
                            mArrayList.add(data);
                            //imageViewCounter.setVisibility(View.INVISIBLE);
                            textViewCounter.setVisibility(View.INVISIBLE);
                        }

                    }
                    if(mArrayList.size() == 0)
                        textViewCounter.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mPostReference = FirebaseDatabase.getInstance().getReference();
            mPostReference.child("post_list").limitToLast(20).addValueEventListener(postListener);

            n = 1;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if(lastPosition == 0){
                        n++;
                        mPostReference = FirebaseDatabase.getInstance().getReference();
                        mPostReference.child("post_list").limitToLast(20*n).addValueEventListener(postListener);
                        recyclerView.scrollToPosition(20);

                    }
                }
            });


        }
        else if(counter == 1){

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main_list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setReverseLayout(true);
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mArrayList = new ArrayList<>();

            mAdapter = new CustomAdapter(mArrayList);
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(View v, int pos)
                {

                    Intent intent = new Intent(getContext(), PostPopupActivity.class);
                    intent.putExtra("post", mArrayList.get(pos));
                    intent.putExtra("username", user_name);
                    intent.putExtra("num", pos);
                    startActivityForResult(intent, 1);
                }
            });


            // get data

            final ValueEventListener postListener = new ValueEventListener(){

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mArrayList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        postData get = postSnapshot.getValue(postData.class);
                        if(get.username.equals(user_name)) {
                            post data = new post(get.username, get.content, get.tags, get.time, get.uri, get.level, get.catType, get.isPublic);
                            mArrayList.add(data);
                            //imageViewCounter.setVisibility(View.INVISIBLE);
                            textViewCounter.setVisibility(View.INVISIBLE);
                        }
                    }
                    if(mArrayList.size() == 0)
                        textViewCounter.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mPostReference = FirebaseDatabase.getInstance().getReference();
            mPostReference.child("post_list").addValueEventListener(postListener);

            m = 1;
            /*
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    if(lastPosition == 0){
                        n++;
                        mPostReference = FirebaseDatabase.getInstance().getReference();
                        mPostReference.child("post_list").limitToLast(20*m).addValueEventListener(postListener);
                        recyclerView.scrollToPosition(20);

                    }
                }
            });

             */
        }
    }

}
