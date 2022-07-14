package com.example.stranger.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.stranger.MainActivity;
import com.example.stranger.R;
import com.example.stranger.databinding.ActivityLoginBinding;
import com.example.stranger.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    GoogleSignInClient signInClient;
    int rc_sign=11;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        //intialize all variables
        initialize();

        if(mAuth.getCurrentUser()!=null){
            gotoNextActivity();
        }
        //btnclick
        BtnClick();

        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient= GoogleSignIn.getClient(this,signInOptions);

    }

    private void gotoNextActivity() {
        startActivity(new Intent(loginActivity.this,MainActivity.class));
        finish();
    }

    void initialize(){
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
    }

    void BtnClick(){
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=signInClient.getSignInIntent();
               startActivityForResult(intent,rc_sign);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == rc_sign) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                authWithGoogle(account.getIdToken());
            } else {
                Log.e("errorGoogle", task.getException().getMessage());
            }
        }
    }

    void authWithGoogle(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=mAuth.getCurrentUser();
                    User myUser=new User(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(),"-", user.getEmail(), 500);
                    database.getReference().child("profiles").child(user.getUid()).setValue(myUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(loginActivity.this, MainActivity.class));
                                finishAffinity();
                            }
                            else{
                                Log.e("errorDatabase",task.getException().getLocalizedMessage() );
                            }
                        }
                    });
                    Log.d("profile", "onComplete: "+user.getPhotoUrl().toString());
                }
                else{
                    Log.e("errorG ",task.getException().getLocalizedMessage() );
                }
            }
        });
    }
}