package com.example.module_login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.baseadhesive.api.ActivityDirectional;
import com.example.baseadhesive.api.ServiceFactory;
import com.example.baseadhesive.api.service.IUserInfo;
import com.example.module_annotations.ActivityAnnotation;

@ActivityAnnotation(activityName = "LoginMainActivity")
public class LoginMainActivity extends AppCompatActivity {
    private static final String TAG = "LoginMainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_main);

        final EditText editText=findViewById(R.id.name);
        Button button=findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s =editText.getText().toString();

                //注意！这里我只是写例子，真实开发下在View直接操作Model是不太合理的。
                //这和你选的架构有关，这里就不多说了
                UserModel.getInstance().getUser().setName(s);
                UserModel.getInstance().getUser().setId("123213123");
                UserModel.getInstance().getUser().setLogin(true);

                try {
                    Class c= ActivityDirectional.getInstance().getClazz("UserInfoMainActivity");
                    Intent intent=new Intent(LoginMainActivity.this,c);
                    startActivity(intent);
                }catch (NoClassDefFoundError e){
                    Toast.makeText(LoginMainActivity.this, "模块未挂载！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                finish();

            }
        });

        IUserInfo userInfo= (IUserInfo) ServiceFactory.getInstance().getService("com.example.module_userinfo.UserInfoService");
        Toast.makeText(this, userInfo.get(), Toast.LENGTH_SHORT).show();
    }
}
