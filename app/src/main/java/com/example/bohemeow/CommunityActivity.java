package com.example.bohemeow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class CommunityActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int PICK_IMAGE = 777;
    private StorageReference mStorageRef;
    Uri currentImageUri;

    String username;
    String name;

    DrawerLayout drawerLayout;
    //TabLayout tabLayout;
    ViewPager2 viewPager;

    ImageView user_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // someday.. 언제나 bonjour! 유저의 데이터만 받아올수는 없잖아?
        SharedPreferences registerInfo = getSharedPreferences("registerUserName", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = registerInfo.edit();
        editor.putString("registerUserName", "Bonjour!");
        editor.commit();

        // get user preference values
        username = registerInfo.getString("registerUserName", "NULL");

        // get user icon
        mStorageRef = FirebaseStorage.getInstance().getReference("User_icons");

        // get intent from add_post
        /*
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        */

        // view pager
        viewPager = findViewById(R.id.view_pager);
        //tabLayout = findViewById(R.id.tabs);
        viewPager.setAdapter(createCardAdapter());
        /*
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0){
                            tab.setText("PERSONAL");
                        }
                        else if(position == 1){
                            tab.setText("PUBLIC");
                        }
                    }
                }).attach();
         */
        // button
        Button add_post_btn = (Button) findViewById(R.id.add_post_btn);

        // add toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        // add post button
        add_post_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommunityActivity.this, add_post.class);
                intent.putExtra("username", username);
                //intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private static final int CARD_ITEM_SIZE = 1;
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }
        @NonNull @Override
        public Fragment createFragment(int position) {
            return fragment.newInstance(position);
        }
        @Override
        public int getItemCount() {
            return CARD_ITEM_SIZE;
        }
    }


    // change user icon's image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            currentImageUri = data.getData();
            boolean imageUriChecker = true;
            user_icon.setImageURI(currentImageUri);


            StorageReference riversRef = mStorageRef.child(username+".jpg");
            UploadTask uploadTask = riversRef.putFile(currentImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });
        }
    }
}
