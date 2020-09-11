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

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private StorageReference mStorageRef;

    private ArrayList<post> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView iconIV;
        ImageView contentIV;

        TextView usernameTV;
        TextView contentTV;
        TextView tagTV;


        public CustomViewHolder(View view) {
            super(view);
            this.iconIV = (ImageView) view.findViewById(R.id.user_icon);
            this.contentIV = (ImageView) view.findViewById(R.id.content_image);
            this.usernameTV = (TextView) view.findViewById(R.id.user_name);
            this.contentTV = (TextView) view.findViewById(R.id.content);
            this.tagTV = (TextView) view.findViewById(R.id.tags);
        }
    }


    public CustomAdapter(ArrayList<post> list) {
        this.mList = list;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder viewholder, int position) {

        String username = mList.get(position).getUsername();
        String content = mList.get(position).getContent();
        String tag = mList.get(position).getTags();

        // set post's icon image
        mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");
        StorageReference islandRef = mStorageRef.child(username + ".jpg");
        final long ONE_MEGABYTE = 2048 * 2048;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewholder.iconIV.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        // set post's content image
        mStorageRef = FirebaseStorage.getInstance().getReference("Post_images");
        islandRef = mStorageRef.child(content + ".jpg");
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

        viewholder.usernameTV.setText(username);
        viewholder.contentTV.setText(content);
        viewholder.tagTV.setText(tag);
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
