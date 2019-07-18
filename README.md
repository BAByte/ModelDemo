# ModelDemo
个人实现组件化开发的Demo

# 前言

本来是不想搞组件化开发的，奈何AndroidStudio越来越吃性能，我从Win10换到Linux开发后，AndroidStudio的编译速度已经有大的提升，但是有时候还是要等个几分钟，让我很不舒服。于是我决定尝试组件化开发。而我看了下网上大都是使用阿里的路由，有空我再去看。

当前这个代码是没有经过项目验证的，毕竟没有必要重复造轮子，但是本着学习的心态，我自己简单的实现了组件化开发库。如果大佬们不嫌麻烦的话，一起来完善这个库！！！这个库在会让app在启动时通过反射获取一些资源，以备运行时使用，如果对反射有很大怨念的人请不要喷我。反射据我所知并没有那么的耗资源呢。

# 版本说明

+ 这一版单纯实现了功能，并没有考虑很多复杂的场景，将来会在实践中进行优化

# 实现组件化开发需要考虑什么问题？

+ 我们首先要考虑的就是各个组件之间是无法访问对方的Service类，但是又需要交互，参考Android中Binder的C/S架构，提供一个全局的ServiceFactory来管理所有模块的开放接口，所以各个模块需要向我们的ServiceFactory注册接口。又由于组件可能开发的人员不同，我还提供了默认服务自动绑定功能，这样就能在没有另一个组件的情况下编译通过
+ 接下来就是页面之间的跳转，各个组件之间是无法访问对方的Activity类，所以想直接通过Intent跳转是不可以的，我简单的实现Activity的全局注册。我在启动时提前取得对应的Class，因为这样我们就能通过Intent启动了。
+ 那接下来就是处理gradle，让他可以自动选择单独编译时是lib还是application了，这个参照网上的方法，还是很简单了
+ 一个应用就一个Application，我们怎么保证其他组件能统一使用同一个Application进行初始化？
+ 并且Application的生命周期能及时回调？说实话我目前觉得这个是没有必要的，因为所有模块最终依赖的都是主模块的Application实例，所以假设我们不去操作Application的生命周期的话（如果是应用级别的，建议都保留在主模块中初始化，这样一般就不用管Application的生命周期），我们是不需要去管模块之间的，但是考虑到可能在某些场景需要将模块的一些东西进行释放，我还是写了回调方法，下面会讲怎么用。

# 使用

我们要做一个简单的demo，看下面的gif图

[![ZO7BcD.gif](https://s2.ax1x.com/2019/07/18/ZO7BcD.gif)](https://imgchr.com/i/ZO7BcD)



+ 一个欢迎界面，我作为主模块，什么都不干，就负责加载模块
+ 一个登录模块，负责管理用户的登录
+ 一个用户信息展示模块，用于展示用户信息

# 文件结构

![ZOHAC6.png](https://s2.ax1x.com/2019/07/18/ZOHAC6.png)

+ 主项目名称为ModuleDemo
+ app为主项目的文件目录我们稍后再看
+ base_adhesive库为全局服务库，所有的服务接口和默认服务以及Activity跳转的相关内容都在里面
+ base_app负责定义Application类和所有被挂载组件的Application的初始化
+ module_annotations负责注解的声明，一些注册操作都是用注解声明的
+ module_login是登录的
+ module_processors是注解解析器，负责编译时自动生成代码
+ module_userinfo是展示用户信息的
+ gradle.properties里面声明了一个变量，控制各个组件是否能单独编译运行

# 准备工作(我们从一个新项目开始)

+ 新建一个项目

+ 导入base_adhesive，module_annotations，module_processors，这三个基本模块（我没有打包成jar包，毕竟是垃圾代码，直接导入库，你们觉得不好的地方可以自己修改）
+ 接下来编写我们项目级别的gradle.properties

~~~properties
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
org.gradle.jvmargs=-Xmx1536m
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true
# 加上这一句，等于true时代表我们要单独编译某个模块，这个是网上的一些大神的处理方式，因为我并不熟悉gradle
isRunAlone=false

~~~

+ 接下来给我们的主项目的app，先导入在build.gradle导入对应的模块

~~~gradle
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

//我们要导入这个库，不然无法继承BaseApplication
    implementation project(path: ':base_app')
    }
}
~~~



+ 接下来为app目录自定义一个Application，记得把这个Application声明到AM文件哦！

~~~java
import com.example.baseapp.BaseApplication;

public class MainApplication extends BaseApplication {


    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        //这个方法为主模块调用，其他模块不需要调用，主要是为了选择要挂载哪些模块，以及传入Application
        init(this,这里等下传入要加载的模块的Application类包名);
    }
}

~~~

# 导入或者新建Module后需要处理什么

那么我们的主项目就写好了，这里我们有两个模块登录和用户信息，我们看看添加一个登录模块后需要怎么处理(怎么导入或者新建Module自己查哦！！)

+ 先导入在build.gradle声明全局模块

~~~xml
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'


//就是下面这三个！！
    implementation project(path: ':base_app')
    annotationProcessor project(path: ':module_processors')
    implementation project(path: ':module_annotations')

}
~~~



+ 给moudle自定义Application，原因很简单，因为在应用运行，一般只有一个Application（谷歌不推荐应用多进程）。而我们的模块又需要application的Context进行其他东西的初始化，所以我们要写一个Application来让主模块进行反射调用init方法进而完成初始化

~~~java
public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";
    
       //要复写该方法，进行初始化，现在我们里面还没东西，等下加！！！除此之外你可以复写Application的其他生命周期
    @Override
    public void onCreate(Application application) {
    }

        //我们怎么保证模块的Application的生命周期能及时回调？说实话我目前觉得这个是没有必要的，因为所有模块最终依赖的都是主模块的Application实例，所以假设我们不去操作Application的生命周期的话（如果是应用级别的，建议都保留在主模块中初始化，这样一般就不用管Application的生命周期），我们是不需要去管模块之间的，但是考虑到可能在某些场景需要将模块的一些东西进行释放，我还是写了回调方法。比如下面这些，你可以直接用。
    
        @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

~~~

+ 但是我们在单独运行时调试Moudle和运行一整个项目时需要区别AM文件，因为单独调试时要指定哪个是启动的Activity，而整个项目跑的时候不需要指定，我们看看LoginMoudle的目录

[![ZOOqpt.png](https://s2.ax1x.com/2019/07/18/ZOOqpt.png)](https://imgchr.com/i/ZOOqpt)

可以看到在src/main/manifest目录下和src/main目录下各有一个AM文件。

src/main/manifest目录下的AM是我们单独调试时用的，看看里面的代码

~~~xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.module_login">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/login_AppTheme">
        <activity android:name=".LoginMainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
~~~

很熟悉了吧！我们再看看src/main目录下的AM文件

~~~xml

<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.module_login">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name="com.example.module_login.LoginMainActivity">
        </activity>
    </application>

</manifest>
~~~

可以看到并没有指定Activity为启动的，而且你注意，我们没有指定Application（前面说过在整个项目运行时moudle的Application由主模块生成），当然你单独调试时如果有需要可以自己指定，相信你知道该怎么做。还有就是主题的问题，在多模块运行时，其实Application是主模块的，所以其他模块的主题也是主模块的，你可以在AM文件里面自己指定Activity的主题。

那怎么在编译的时候区分出是用哪个目录下的AM文件呢？编写我们的moudle下的build.gradle

~~~xml
//如果是单独调试就为Application
if (isRunAlone.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}


android {
    compileSdkVersion 28

//设置资源名字必须以 ' login_ ' 开头否则报红
    resourcePrefix "login_"

    defaultConfig {



        //单独调试需要设置Application的ID
        if (isRunAlone.toBoolean()) {
            applicationId "com.example.login"
        }

        minSdkVersion 16
        targetSdkVersion 28
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }


    sourceSets {
        main {
            // 单独调试与集成调试时使用不同的 AndroidManifest.xml 文件
            if (isRunAlone.toBoolean()) {
                manifest.srcFile 'src/main/manifest/AndroidManifest.xml'
            } else {
                manifest.srcFile 'src/main/AndroidManifest.xml'
            }
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

//注意要导入库
    implementation project(path: ':base_app')
    annotationProcessor project(path: ':module_processors')
    implementation project(path: ':module_annotations')

}

~~~

+ 我们从gif图可以看到主模块跳入了我们登录模块的Activity，但是在主模块本来就访问不到我们登录模块的Activity，我们看看我们登录模块的Activity怎么写

~~~java
import com.example.module_annotations.ActivityAnnotation;

//看这里！只需要声明这个注解，然后设置一个名字就好了！
@ActivityAnnotation(activityName = "LoginMainActivity")
public class LoginMainActivity extends AppCompatActivity {
    private static final String TAG = "LoginMainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_main);

    }
}

~~~

然后在登录模块的application进行注册,注意：模块化的AM文件中不需要指定这个Application！因为我们通过主模块的Application进行了对所有模块的Application进行了管理（我是采用了继承的方式，因为是多个模块）

~~~java
public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";
    
    @Override
    public void onCreate(Application application) {

        //注册我们该模块的Activity
        ActivityConfig.initActivtyConfig();
    }
    
    

}

~~~



那主模块怎么跳转的？首先我们需要在主模块挂载我们的登录模块,先导入在build.gradle声明对应的模块

~~~xml
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

//我们要导入这个库，不然无法继承BaseApplication
    implementation project(path: ':base_app')
    
    //这里判断是否单独运行模块，如果不是单独运行就使用runtimeOnly导入进来，runtimeOnly的用法你可以百度查，这里是为了编写代码时不能直接访问其他组件的代码，实现隔离
    if (!isRunAlone.toBoolean()) {
        runtimeOnly project(path: ':module_login')
    }
}
~~~

然后在主模块的Application对动态挂载的模块进行声明和初始化

~~~java
import com.example.baseapp.BaseApplication;

public class MainApplication extends BaseApplication {
    //声明已有模块的Application，因为要用到反射的机制去获取，所以要写完整包名
    private static final  String MODEL_LOGIN ="com.example.module_login.LoginApplication";



    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        //这个方法为主模块调用，其他模块不需要调用，主要是为了选择要挂载哪些模块，以及传入Application，第二个参数无限长
        init(this,MODEL_LOGIN);
    }
}
~~~

然后在主模块的Activity里面进行跳转

~~~java
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //假装这个是欢迎界面，等待两秒后跳到登录界面
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //记得我们在前面声明的LoginMainActivity吗？这里就可以通过ActivityDirectional直接获取到他的Class，然后通过Intent进行启动了！！！！
                Class c = ActivityDirectional.getInstance().getClazz("LoginMainActivity");
                Intent intent = new Intent(MainActivity.this, c);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}

~~~

从gif图中可以看到用户信息展示模块需要从登录模块拿到用户的信息，所以我们的登录模块需要向外提供服务

+ 所以我们要在base_adhesive声明我们的服务接口，以及定义我们的默认服务

~~~java
//Login的服务
public interface ILoginService {

    public void release();

    public String getUserJson();
    public String getId();

    public String getName() ;

    public boolean isLogin() ;
}
~~~



~~~java
import com.example.module_annotations.ServiceBelong;
//我们需要声明当前默认服务属于哪个服务
@ServiceBelong(serviceName = "LoginService")
public class LoginServiceDefault implements ILoginService {
    @Override
    public void release() {

    }

    @Override
    public String getUserJson() {
        return "无用户";
    }

    @Override
    public String getId() {
        return "无id";
    }

    @Override
    public String getName() {
        return "无名字";
    }

    @Override
    public boolean isLogin() {
        return false;
    }
}
~~~

ok了，剩下的就是注册我们的服务

~~~java

public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";
    
    @Override
    public void onCreate(Application application) {
        //注册我们的服务
        try {
            ServiceFactory.getInstance().addService("LoginService",new LoginService());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //注册Activity
        ActivityConfig.initActivtyConfig();
    }

}
~~~

# 再添加一个用户信息展示模块

步骤和添加LoginMoudle一样！！！

我们直接看在用户展示模块怎么获取登录模块的接口

~~~java

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

        //退出登录的按钮
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
~~~

# 具体实现

具体实现其实非常的简单，详情可以看看源码，组件化开发的前期工作 确实很复杂，目前我觉得挂载组件的操作有点繁琐，不仅要在主模块的build.gradle文件声明，还有在Application声明，这个和Activity的注册不一样，因为Activity是在Application注册的，而模块未挂载的话，根本不用谈Application了，因为使用编译时注解生成代码时无法跨Moudle，所以我没有办法做到跨组件获取Application的信息（也可能是我查不到，如果所以有请告诉我，谢谢！！！不要太复杂哦！！！）本来我是想用MVVM写组件化开发的Demo，但是一想一用MVVM又需要讲MVVM，所以直接一点写的简单一点。