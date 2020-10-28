package com.example.bohemeow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class RankingCustomAdapter extends RecyclerView.Adapter<RankingCustomAdapter.CustomViewHolder> {

    private StorageReference mStorageRef;

    private ArrayList<post> mList;

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //---------------------------<
        ImageView iconIV;
        ImageView contentIV;

        TextView usernameTV;
        TextView contentTV;
        TextView tagTV;
        TextView timeTV;
        TextView levelTV;
        //--------------------------->

        public CustomViewHolder(View view) {
            super(view);

            // 바꿀 내용 전부 선언
            //---------------------------<
            this.iconIV = (ImageView) view.findViewById(R.id.user_icon);
            this.contentIV = (ImageView) view.findViewById(R.id.content_image);
            this.usernameTV = (TextView) view.findViewById(R.id.user_name);
            this.contentTV = (TextView) view.findViewById(R.id.content);
            this.tagTV = (TextView) view.findViewById(R.id.tags);
            this.timeTV = (TextView) view.findViewById(R.id.time);
            this.levelTV = (TextView) view.findViewById(R.id.user_level);
            //--------------------------->




            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(v, pos);
                    }

                }
            });

        }
    }


    public RankingCustomAdapter(ArrayList<post> list) {
        this.mList = list;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.post, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewholder, int position) {

        // position은 이전 화면(탭)에서 몇번째 item 인지를 나타냄.

        //---------------------------<
        String username = mList.get(position).getUsername();
        String content = mList.get(position).getContent();
        String tag = mList.get(position).getTags();
        String time = mList.get(position).getTime();
        String uri = mList.get(position).getUri();
        int level = mList.get(position).getLevel();
        int catType = mList.get(position).getCatType();
        //--------------------------->

        //얘는 캐릭터별 얼굴 이미지라 그대로 쓰셔도 됩니다
        int[] icons = {R.drawable.beth_0000, R.drawable.heads_0001, R.drawable.heads_0002, R.drawable.heads_0003,
                R.drawable.heads_0004, R.drawable.heads_0005, R.drawable.heads_0006,R.drawable.heads_0007, R.drawable.heads_0008};


        // set post's content image
        /* 이미지 로드. 랭킹에서는 필요 없을듯
        StorageReference islandRef;
        final long ONE_MEGABYTE = 2048 * 2048;

        if (!uri.equals("")){
        mStorageRef = FirebaseStorage.getInstance().getReference("Post_images");
        islandRef = mStorageRef.child(time + ".jpg");

            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewholder.contentIV.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

         */


        //---------------------------<
        level = calculateLevel(level);

        viewholder.usernameTV.setText(username);
        viewholder.contentTV.setText(content);
        viewholder.tagTV.setText(tag);
        viewholder.levelTV.setText("Lv." + Integer.toString(level));
        viewholder.iconIV.setImageResource(icons[catType]);
        //--------------------------->


    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }



    int calculateLevel(int score){
        int level;
        if(score >= 10000){
            score -= 10000;
            level = (score / 1500) + 11;
        }
        else{
            level = score/1000 + 1;
        }
        return level;
    }
}
