package com.example.stranger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.stranger.Reward.RewardActivity;
import com.example.stranger.call.ConnectingActivity;
import com.example.stranger.databinding.ActivityMainBinding;
import com.example.stranger.models.User;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.rupinderjeet.kprogresshud.KProgressHUD;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    long coins=0;
    String[] permissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
    private int requestCode=1;
    FirebaseUser currentUser;
    KProgressHUD progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        initialize();

        binding.chest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity( new Intent(MainActivity.this, RewardActivity.class));

            }
        });

        updateProfile();
        progress=KProgressHUD.create(this);
        progress.setDimAmount(0.5f);
        progress.show();

    }

     private void askPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,permissions,requestCode);
     }

    private Boolean isPermissionGranted(){
        for (String Permission:permissions) {
            if(ActivityCompat.checkSelfPermission(MainActivity.this,Permission)!= PackageManager.PERMISSION_GRANTED)
                  return false;
        }
        return true;
    }

    private void updateProfile() {
        FirebaseUser user=mAuth.getCurrentUser();
        database.getReference().child("profiles").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progress.dismiss();
                User user=snapshot.getValue(User.class);
                coins=user.getCoins();
                binding.userCoins.setText("You have: "+coins);
                Glide.with(MainActivity.this).load(user.getProfile()).into(binding.profileImage);

                binding.FindBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isPermissionGranted()){
                            if(coins>5){
                                coins=coins-5;
                                database.getReference().child("profiles").child(currentUser.getUid()).child("coins").setValue(coins);
//                                startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                                Intent intent=new Intent(MainActivity.this, ConnectingActivity.class);
                                intent.putExtra("profile",user.getProfile());
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Insufficient Coins",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            askPermission();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initialize() {
        database=FirebaseDatabase.getInstance();
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
    }
}