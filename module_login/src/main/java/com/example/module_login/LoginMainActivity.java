package com.example.module_login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.module_annotations.ActivityAnnotation;

@ActivityAnnotation(activityName = "LoginMainActivity")
public class LoginMainActivity extends AppCompatActivity {
    private static final String TAG = "LoginMainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_main);
        Log.d(TAG, "onCreate: "+UserModel.getInstance().getUser());
    }
}
