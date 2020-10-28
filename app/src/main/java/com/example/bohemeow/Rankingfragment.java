package com.example.bohemeow;

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
import androidx.recyclerview.widget.DividerItemDecoration;
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
 * Use the {@link Rankingfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Rankingfragment extends Fragment {

    String user_name;

    private ArrayList<post> mArrayList;
    private RankingCustomAdapter mAdapter; // Ranking용 adapter

    private DatabaseReference mPostReference;


    private static final String ARG_COUNT = "param1";
    private static int counter;

    public Rankingfragment() {
        // Required empty public constructor
    }

    public static Rankingfragment newInstance(Integer counter) {
        Rankingfragment fragment = new Rankingfragment();
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

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity();
        user_name = ((CommunityActivity)context).username;

        //final ImageView imageViewCounter = view.findViewById(R.id.imageViewFrag);
        final TextView textViewCounter = view.findViewById(R.id.textViewFrag);

        if(counter == 0){ // 탭의 번호. 0이면 첫번째 탭, 1이면 두번째 탭. 전체적인 구조는 같으며, mArrayList에 저장하는 데이터만 다름.

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main_list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setReverseLayout(true); // 커뮤니티에서는 시간 역순으로 정렬해서 넣은 코드입니다. 랭킹에서는 다르게 정렬해야할것같네요
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mArrayList = new ArrayList<>();

            mAdapter = new RankingCustomAdapter(mArrayList);
            mRecyclerView.setAdapter(mAdapter);


            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    mLinearLayoutManager.getOrientation());
            mRecyclerView.addItemDecoration(dividerItemDecoration);


            // !! 리사이클러뷰 내부의 각 아이템을 클릭했을 때 팝업 나오게
            mAdapter.setOnItemClickListener(new RankingCustomAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(View v, int pos)
                {

                    //---------------------------<
                    Intent intent = new Intent(getContext(), PostPopupActivity.class);
                    intent.putExtra("post", mArrayList.get(pos)); // 넘겨줄 포스트의 데이터
                    intent.putExtra("username", user_name); // 보고있는 유저의 정보
                    intent.putExtra("num", pos); // 몇번째 아이템인지. 이걸 기준으로 게시글 판별
                    startActivityForResult(intent, 1);
                    //--------------------------->
                }
            });


            // get data

            final ValueEventListener postListener = new ValueEventListener(){

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mArrayList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        //보여주려는 데이터. data 클래스 참고
                        //---------------------------<
                        postData get = postSnapshot.getValue(postData.class);
                        if(get.isPublic == true) { // 첫번째 탭에서는 public만 보여줌
                            post data = new post(get.username, get.content, get.tags, get.time, get.uri, get.level, get.catType, get.isPublic);
                            mArrayList.add(data);
                            //imageViewCounter.setVisibility(View.INVISIBLE);
                            textViewCounter.setVisibility(View.INVISIBLE);
                        }
                        //--------------------------->
                    }
                    if(mArrayList.size() == 0) // 데이터가 하나도 없으면 이미지를 보여줌
                        textViewCounter.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mPostReference = FirebaseDatabase.getInstance().getReference();
            mPostReference.child("post_list").addValueEventListener(postListener);


        }
        else if(counter == 1){ // 두번째 탭

            RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_main_list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
            mLinearLayoutManager.setReverseLayout(true); // 마찬가지로 역순 정렬
            mLinearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);

            mArrayList = new ArrayList<>();

            mAdapter = new RankingCustomAdapter(mArrayList);
            mRecyclerView.setAdapter(mAdapter);


            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    mLinearLayoutManager.getOrientation());
            mRecyclerView.addItemDecoration(dividerItemDecoration);

            //아이템을 눌렀을 때 나오는 팝업
            mAdapter.setOnItemClickListener(new RankingCustomAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(View v, int pos)
                {
                    //---------------------------<
                    Intent intent = new Intent(getContext(), PostPopupActivity.class);
                    intent.putExtra("post", mArrayList.get(pos));
                    intent.putExtra("username", user_name);
                    intent.putExtra("num", pos);
                    startActivityForResult(intent, 1);
                    //--------------------------->
                }
            });


            // get data

            final ValueEventListener postListener = new ValueEventListener(){

                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mArrayList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                        //보여주려는 데이터
                        //--------------------------->
                        postData get = postSnapshot.getValue(postData.class);
                        if(get.username.equals(user_name)) {
                            post data = new post(get.username, get.content, get.tags, get.time, get.uri, get.level, get.catType, get.isPublic);
                            mArrayList.add(data);
                            //imageViewCounter.setVisibility(View.INVISIBLE);
                            textViewCounter.setVisibility(View.INVISIBLE);
                        }
                        //--------------------------->
                    }
                    if(mArrayList.size() == 0) // 아무것도 없을 경우
                        textViewCounter.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mPostReference = FirebaseDatabase.getInstance().getReference();
            mPostReference.child("post_list").addValueEventListener(postListener);

        }
    }

}
