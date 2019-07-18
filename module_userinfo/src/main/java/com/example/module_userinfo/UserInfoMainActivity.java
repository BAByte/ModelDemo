package com.example.module_userinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baseadhesive.api.ActivityDirectional;
import com.example.baseadhesive.api.ServiceFactory;
import com.example.baseadhesive.api.service.ILoginService;
import com.example.module_annotations.ActivityAnnotation;



@ActivityAnnotation(activityName = "UserInfoMainActivity")
public class UserInfoMainActivity extends AppCompatActivity {
    private static final String TAG = "UserInfoMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_main);

        //获取登录服务
        final ILoginService loginService = (ILoginService) ServiceFactory.getInstance()
                .getService("LoginService");

        TextView name=findViewById(R.id.name);
        TextView id=findViewById(R.id.idString);
        TextView isLogin=findViewById(R.id.isLogin);
        Button out=findViewById(R.id.out);

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginService.release();
                    Class c= ActivityDirectional.getInstance().getClazz("LoginMainActivity");
                    Intent intent=new Intent(UserInfoMainActivity.this,c);
                    startActivity(intent);
                    finish();
                }catch (NoClassDefFoundError e){
                    Toast.makeText(UserInfoMainActivity.this, "模块未挂载！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        name.setText("用户名："+loginService.getName());
        id.setText("用户ID："+loginService.getId());
        isLogin.setText("用户是否登录"+loginService.isLogin());
    }
}
