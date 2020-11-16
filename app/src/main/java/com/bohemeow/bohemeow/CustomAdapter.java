package com.bohemeow.bohemeow;

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

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

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

        ImageView iconIV;
        ImageView contentIV;
        ImageView pribate_mark;

        TextView usernameTV;
        TextView contentTV;
        TextView tagTV;
        TextView timeTV;
        TextView levelTV;

        public CustomViewHolder(View view) {
            super(view);
            this.iconIV = (ImageView) view.findViewById(R.id.user_icon);
            this.contentIV = (ImageView) view.findViewById(R.id.content_image);
            this.usernameTV = (TextView) view.findViewById(R.id.user_name);
            this.contentTV = (TextView) view.findViewById(R.id.content);
            this.tagTV = (TextView) view.findViewById(R.id.tags);
            this.timeTV = (TextView) view.findViewById(R.id.time);
            this.levelTV = (TextView) view.findViewById(R.id.user_level);
            this.pribate_mark = view.findViewById(R.id.private_mark2);

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


    public CustomAdapter(ArrayList<post> list) {
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

        String username = mList.get(position).getUsername();
        String content = mList.get(position).getContent();
        String tag = mList.get(position).getTags();
        String time = mList.get(position).getTime();
        String uri = mList.get(position).getUri();
        int level = mList.get(position).getLevel();
        int catType = mList.get(position).getCatType();
        boolean isPublic = mList.get(position).isPublic();

        int[] icons = {R.drawable.cathead_null, R.drawable.hanggangic, R.drawable.bameeic, R.drawable.chachaic,
                R.drawable.ryoniic, R.drawable.moonmoonic, R.drawable.popoic,R.drawable.taetaeic, R.drawable.sessakic};


        // set post's content image

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
        else viewholder.contentIV.setImageResource(R.drawable.commu_nullimage);


        level = calculateLevel(level);

        viewholder.usernameTV.setText(username);
        viewholder.contentTV.setText(content);
        viewholder.tagTV.setText(tag);
        viewholder.timeTV.setText(Date(time));
        viewholder.levelTV.setText("Lv. " + Integer.toString(level));
        viewholder.iconIV.setImageResource(icons[catType]);

        if(!isPublic){
            viewholder.pribate_mark.setImageResource(R.drawable.commu_show_private);
        }

        //System.out.println("\ncatType: " + catType);
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    String Date(String time){
        String t = "";

        if(time.substring(5,6).equals("0")){
            t = t + time.substring(6,7) + "월";
        }
        else t = t + time.substring(5,7) + "월";

        if(time.substring(8,9).equals("0")){
            t = t + time.substring(9,10) + "일 ";
        }
        else t = t + time.substring(8,10) + "일 ";

        t = t + time.substring(11,16);

        return t;
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
