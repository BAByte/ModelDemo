# ModelDemo
个人实现组件化开发的Demo

# 前言

本来是不想搞组件化开发的，奈何AndroidStudio越来越吃性能，我从Win10换到Linux开发后，AndroidStudio的编译速度已经有大的提升，但是有时候还是要等个几分钟，让我很不舒服。听师兄说了组件化开发可以单独编译和调试！心里哪个激动！！

# 为什么要重复造轮子

而我看了下网上大都是使用阿里的路由，但是学习成本较高，而且假设我的项目是重构的，那么很多地方就不得不改成阿里路由的调用。Activity 的路由方案使用的过程真的把我搞蒙了，我就启动个Activity，你给我搞那么多花里胡哨的，嗯，学习成本太高了（好吧，是我懒得学），而且路由的功能我又不是全部用到，我也不想在自己的项目中引入很多第三方的库，所以我还是希望能够尽量沿用项目已有的调用方式。并且轻量。

##　最重要的原因

在网上的一些组件化开发他们是怎么动态的调试单独组件的呢？他们在各个Module的build.gradle文件都是用在gradle.properties文件里面的一个值来区分运行时是library还是application

~~~xml
//如果是单独调试就为Application
if (isRunAlone.toBoolean()) {
    apply plugin: 'com.android.application'
} else {
    apply plugin: 'com.android.library'
}

        //单独调试需要设置Application的ID
        if (isRunAlone.toBoolean()) {
            applicationId "com.example.login"
        }

~~~

我觉得这样还好，不会太麻烦，但是下面这样的操作让我震惊了，我们知道library和application的AndroidManifest文件是不一样的，而且当我们是主模块进行运行的时候，那么其他组件是不能有defaultActivity的！不然编译器会报错，所以他们也是使用一个值，在Module的build.gradle文件中做了如下处理

~~~xml
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
~~~

是的，这意味着我们需要自己新建一个文件夹！！！我现在为了实现项目的组件化，导致需要改动build.gradle和AndroidManifest。有些人说：总是要做出牺牲的嘛！但是这种牺牲我是认为不值得的！大多数开发者对gradle玩的本来就不是很溜，所以这样做实属没有必要。而且这样做有个很大的问题：假设另一个同事负责开发moduleA，他开发时是基于一个library进行实现的，而当你想往你的项目中导入这个module的时候，为了能让这个library适应你当前的组件化开发架构，你居然要去改动moduleA的内容！！！作为一个程序员，大家都知道设计原则里面有一条：开闭原则，只对扩展开放，对修改关闭，所以这种实现组件化的方式我并不认同。

# 我所期望的组件化开发

+ module单独编译，单独运行，而不是耗费大量时间全量编译整个 App
+ 并且在module需要单独编译的时候能不修改module的内容！
+ 能指定启动module的任意一个Activity进行调试
+ module之间无法访问到对方的类文件！！

+ 跨模块的调用，应该都可以很简单的互相调用

+ 不要有太多的学习成本，尽量沿用目前已有的开发方式，避免代码和具体的组件化框架绑定

+ 轻量级

# library初始化过程

这是当module和主module一起运行的时候需要考虑的一个问题，主module的application启动会初始化一些所有module都要使用到的服务（比如腾讯的日志框架，他就是程序运行的时候就需要运行的），而其他module也有自己运行时一些需要初始化的服务。这些module用到的第三方库服务的生命周期我们一般会和谁相关？？是的！和应用的application的生命周期相关。

一些module在整个项目运行时，是library类型的，意味着他们就算定义了一个Application类，library类型的module在运行是就算你在AndroidManifest声明了该application类，运行是系统也是不会去启动的这个application的！而是会去启动主module的application

这些library的application也没有办法和主module的application的生命周期进行绑定（我们强调过两个模块之间不能直接使用对方的类，这样才能解耦！）。那我们就需要定义一个大家都用到的module:   base_app，base_app专门负责把主模块的application的生命周期和其他module的application的生命周期进行绑定：

~~~java
package com.example.baseapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

iBaseApplication定义好的init方法BaseApplication定义好的init方法BaseApplication定义好的init方法BaseApplication定义好的init方法mport java.util.ArrayList;
import java.util.List;

public  abstract class BaseApplication extends Application {
    //该集合用来存所有module的application，当走到对应的回调时就遍历调用
    private List<BaseApplication> applications=new ArrayList<>();

    public abstract void onCreate(Context context);

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (BaseApplication a:applications){
            a.onTerminate();
        }
        applications.clear();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        for (BaseApplication a:applications){
            a.onTrimMemory(level);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        for (BaseApplication a:applications){
            a.onLowMemory();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        for (BaseApplication a:applications){
            a.onConfigurationChanged(newConfig);
        }
    }
}

~~~

所以我们所有module都在自己的build.gradle文件进行如下操作：

~~~xml

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'

	//引入base_app这个module
    api project(path: ':base_app')
}
~~~

ps：api和implementation的区别可以去看这篇文章 https://blog.csdn.net/u010296640/article/details/79114028

然后我们的library的application类这样写

~~~java

//继承自BaseApplication
public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";
    
  @Override
    public void onCreate(Context context) {
    	//利用这个context进行初始第三方库的服务

}
~~~

那你就会开时问:你不是说library的application没用吗?为什么还去定义？系统是不会去启动这个LoginApplication，但是我们可以在主模块手动去启动呀！

BaseApplication定义好的init方法如下！该方法给主module对所有被挂载的Module的application进行初始化

```java
public void init(Context application,String... s){
    for (String module: s){
        try {
            Class clazz = Class.forName(module);
            BaseApplication baseApp = (BaseApplication) clazz.newInstance();
            baseApp.onCreate(application);
            applications.add(baseApp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
```
是的！！很简单！就是反射拿到所有的application，调用踏马的onCreate方法，然后保存到容器中，等下在其他生命周期的回调进行使用，下面我们看主模块的application怎么写：

~~~java
public class MainApplication extends BaseApplication {
    //声明已有模块
    private static final  String MODEL_LOGIN ="com.example.module_login.LoginApplication";
    private static final  String MODEL_USERINFO ="com.example.module_userinfo.UserApplication";

    private static final String TAG = "MainApplication";

    @Override
    public void onCreate(Context context) {
        //调用BaseApplication定义好的init方法去启动下面两个模块的application
        init(context,MODEL_LOGIN,MODEL_USERINFO);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        
        //这个方法为主模块调用，其他模块不需要调用，主要是为了选择要挂载哪些模块，以及传入Application
        init(this,这里等下传入要加载的模块Application类的完整包名);
    }
  }
~~~

但是！！我们的主module运行时想要使用到这些module，当然需要引入啦!

~~~java

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
        
        //引入base_app
    implementation project(path: ':base_app')
    //只有使用时才能调用到类
    runtimeOnly project(path: ':module_login')
    runtimeOnly project(path: ':module_userinfo')
}
~~~

runtimeOnly是什么意思呢？就是只有运行时我们的主module才可以使用到这两个module的类，我们编写代码时是无法使用这个两个module的类的，编译器会报错的！这其实就是为了强制我们解耦！！

既然可以全量运行了！那library的单独调试运行怎么办？？不急！！下面开始进行,


# 拆分

我们新建module的时候会发现可以选library或者phone&Tablet。第一种就是无法进行单独调试的，只能依赖于application的module，但是第二种可呀！第二种就是application类型的！！我们可以单独装入手机进行调试！

```xml
//我们的主模块和phone&Tablet在build.gradle是属于这个
apply plugin: 'com.android.application'
```



那我们可以在我们的项目中为添加进来的library，增加一个phone&Tablet类型的module,专门去启动library进行调试！！！！这样的话根本不需要更改library的代码！！！

我们在项目目录新建一个base_alone文件夹，然后在里面新建两个phone&Tablet类型的module！(因为我的demo是有三个module，两个是library)

![eyg9e0.png](https://s2.ax1x.com/2019/08/04/eyg9e0.png)

可以看到我在base_alone定义两个module，是的，我就是利用这两个phone&Tablet类型的module来启动两个library类型的module！！！！新建这两个Module后需要在项目目录下的gradle.setting导入



~~~xml
include ':app',':lib'
include':base_adhesive',':base_app'

//就是下面这两行
include ':module_login',  ':module_userinfo'

include ':base_alone:alone_module_login',':base_alone:alone_module_userinfo'
~~~

这有什么用呢？我看看下图!!

![eygW7V.png](https://s2.ax1x.com/2019/08/04/eygW7V.png)

是不是在android studio上就出现了这两个模块的运行选项了！！所以下面我们要做的就是把这个两个moudle和两个library类型的moudle关联起来！！我就讲一个就好了！



首先我们需要在alone_module里面引入libraly

~~~java
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])

    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
        
        //这里！！是不是很简单？
    api project(path: ':module_login')

}
~~~



然后在alone_module新建一个application

~~~java
//注意哦！这里是继承module_login的LoginApplication
public class AloneLoginApplication extends LoginApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //在单独编译时，可以先初始化全局编译时需要的服务

        //初始化登录模块的全局服务
        onCreate(getApplicationContext());

    }

    @Override
    public void onCreate(Context context) {
        //就是因为这个super！！我们就不用再去写module_login第三方库的初始化代码啦！！
        super.onCreate(context);
    }
}
~~~

然后把AloneLoginApplication设置为alone_module的默认application

~~~java
<application
        android:name=".AloneLoginApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/login_AppTheme">

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
~~~

我们可以在alone_module的activity里面启动library的任何一个Activity进行调试啦！！

~~~java
//用来跳转登录模块的界面
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //直接跳转到指定界面进行调试
        Intent intent=new Intent(this,LoginMainActivity.class);
        startActivity(intent);
    }
}

~~~

ok!!运行alone_module！！！！是不是我们的module_login就可以启动了！！！现在就做到了不需要修改module_login的任何代码，只需要增加一个alone_module就好了！！这不就遵从了开闭原则吗？？？？



你可能会产生的疑问：不对啊，我们的library的module不还是改了代码吗？我们引入了base_app这个Module！还写了一个application啊！！不是改了代码吗？

这是必不可少的一步了！！base_app这个Module是所有模块共有的，本来就是需要他耦合，而且我们写了一个application，但是改动很小啊！！！而且就算本来library就定义了application，没关系，你发现没有！只需要改个继承关系就好了！！！！而且只有这样做，才能让我们的library的application生命周期得到管理啊！！！



然后你还会有一些疑问:假设我们有很多library，那我们单独调试不是要写很多类似于alone_module这样的Module？那打包apk的时候会不会导致apk体积变大？？？

不会！！我们在主Module只引入了谁？只引入了library！！没有引入alone_module这些Module！！没有引用的化就不会添加到编译路径！！！！



# 跨Module的交互

那我们在主module上引入其他Module的时候是这样写的

~~~java
    //只有使用时才能调用到类
    runtimeOnly project(path: ':module_login')
    runtimeOnly project(path: ':module_userinfo')
~~~

那意味着我们的各个Module之间的直接调用显然是不行的！说明各个module之间没有半点交互！！那怎么办？下面我们进行实战，让你知道怎么使用我写好的库来进跨Module的调用。（跨Module的具体实现我就不讲了！你可以看我的源码！）

# 需求

我们要做一个简单的demo，看下面的gif图

[![ZO7BcD.gif](https://s2.ax1x.com/2019/07/18/ZO7BcD.gif)](https://imgchr.com/i/ZO7BcD)



+ 一个欢迎界面，我作为主模块，什么都不干，就负责加载模块
+ 一个登录模块，负责管理用户的登录
+ 一个用户信息展示模块，用于展示用户信息

# 文件结构

![eyfp3F.png](https://s2.ax1x.com/2019/08/04/eyfp3F.png)

+ 主项目名称为ModuleDemo
+ app为主项目的文件目录我们稍后再看
+ base_alon里面有两个用来单独调试module_login和module_userinfo的Module
+ base_adhesive库为全局服务库，所有的服务接口和默认服务以及Activity跳转的相关内容都在里面
+ base_app负责定义Application类和所有被挂载组件的Application的初始化
+ module_annotations负责注解的声明，一些注册操作都是用注解声明的
+ module_login是登录的
+ module_processors是注解解析器，负责编译时自动生成代码
+ module_userinfo是展示用户信息的



+ 准备工作(我们从一个新项目开始)

+ 新建一个项目

+ 导入base_adhesive，module_annotations，module_processors，这三个基本模块（我没有打包成jar包，毕竟是垃圾代码，直接导入库，你们觉得不好的地方可以自己改

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

    //只有使用时才能调用到类
    runtimeOnly project(path: ':module_login')
    runtimeOnly project(path: ':module_userinfo')
    
}
~~~



+ 接下来为app目录自定义一个Application，记得把这个Application声明到AM文件哦！

~~~java
public class MainApplication extends BaseApplication {
    //声明已有模块
    private static final  String MODEL_LOGIN ="com.example.module_login.LoginApplication";
    private static final  String MODEL_USERINFO ="com.example.module_userinfo.UserApplication";

    private static final String TAG = "MainApplication";

    @Override
    public void onCreate(Context context) {
                //这个方法为主模块调用，其他模块不需要调用，主要是为了选择要挂载哪些模块，以及传入Application
        init(context,MODEL_LOGIN,MODEL_USERINFO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onCreate(getApplicationContext());
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
    api project(path: ':base_app')

//这两个库就是我实现跳转的秘密
    annotationProcessor project(path: ':module_processors')
    implementation project(path: ':module_annotations')

}
~~~

module的Application

~~~java
public class LoginApplication extends BaseApplication {
    private static final String TAG = "LoginApplication";
    
       //要复写该方法，进行初始化，现在我们里面还没东西，等下加！！！除此之外你可以复写Application的其他生命周期
    @Override
    public void onCreate(Context context) {
    }
    
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

然后在登录模块的application进行注册,注意

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



那主模块怎么跳转的？在主模块的Activity里面进行跳转

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
                //记得我们在前面声明的LoginMainActivity吗？这里就可以通过ActivityDirectional直接获取到他的Class，然后通过Intent进行启动了！！！！这个ActivityDirectional是我写的Activity导向器，可以帮你拿到你想去的activity的class
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


目前我是没有做重名处理的！！！有兴趣的大佬可以去弄！！！具体实现其实非常的简单，详情可以看看源码！！！


