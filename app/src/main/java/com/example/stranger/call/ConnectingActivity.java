package com.example.stranger.call;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.stranger.databinding.ActivityConnectingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ConnectingActivity extends AppCompatActivity {
    ActivityConnectingBinding binding;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    boolean isOkay=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String profilePic= getIntent().getStringExtra("profile");
        Glide.with(this).load(profilePic).into(binding.profileImage);
        init();
        connect();
    }
   private void init(){
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
   }
    private void connect() {

        String username=mAuth.getUid();

        database.getReference().child("users")
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.getChildrenCount()>0) {
                   isOkay=true;
                   //room available
                   for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                       database.getReference().child("users")
                               .child(snapshot1.getKey())
                               .child("incoming")
                               .setValue(username);
                       database.getReference().child("users")
                               .child(snapshot1.getKey())
                               .child("status")
                               .setValue(1);

                       Intent intent = new Intent(ConnectingActivity.this, callActivity.class);
                       String incoming = snapshot1.child("incoming").getValue(String.class);
                       String createdBy = snapshot1.child("createdBy").getValue(String.class);
                       boolean isAvailable = snapshot1.child("isAvailable").getValue(Boolean.class);
                       intent.putExtra("username", username);
                       intent.putExtra("incoming", incoming);
                       intent.putExtra("createdBy", createdBy);
                       intent.putExtra("isAvailable", isAvailable);
                       startActivity(intent);
                       finish();
                   }
                   }
               else {
                   //no room//create a new room
                   HashMap<String, Object> room = new HashMap<>();
                   room.put("incoming", username);
                   room.put("createdBy", username);
                   room.put("isAvailable", true);
                   room.put("status", 0);
                   database.getReference().child("users")
                           .child(username)
                           .setValue(room)
                           .addOnSuccessListener(unused -> database.getReference().child("users")
                                   .child(username)
                                   .addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot12) {
                           if (snapshot12.child("status").exists()) {
                               if (snapshot12.child("status").getValue(Integer.class) == 1) {
                                   if(isOkay)
                                       return;
                                   isOkay=true;
                                   Intent intent = new Intent(ConnectingActivity.this, callActivity.class);
                                   String incoming = snapshot12.child("incoming").getValue(String.class);
                                   String createdBy = snapshot12.child("createdBy").getValue(String.class);
                                   boolean isAvailable = snapshot12.child("isAvailable").getValue(boolean.class);
                                   intent.putExtra("username", username);
                                   intent.putExtra("incoming", incoming);
                                   intent.putExtra("createdBy", createdBy);
                                   intent.putExtra("isAvailable", isAvailable);
                                   startActivity(intent);
                                   finish();
                               }
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   }));
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}