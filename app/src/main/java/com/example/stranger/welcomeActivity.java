package com.example.stranger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.stranger.authentication.loginActivity;
import com.example.stranger.databinding.ActivityWelcomeBinding;
import com.google.firebase.auth.FirebaseAuth;

public class welcomeActivity extends AppCompatActivity {
ActivityWelcomeBinding binding;
FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding=ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            gotoNextActivity();
        }

        onClick();
    }

    private void gotoNextActivity() {
        startActivity(new Intent(welcomeActivity.this,MainActivity.class));
        finish();
    }

    private void onClick() {
        binding.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(welcomeActivity.this, loginActivity.class));
                finish();
            }
        });
    }
}