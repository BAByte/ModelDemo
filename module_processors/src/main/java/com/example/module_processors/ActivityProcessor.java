package com.example.module_processors;


import com.example.module_annotations.ActivityAnnotation;
import com.example.module_annotations.ServiceBelong;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

//不要写成Process.class
@AutoService(Processor.class)
//设置支持的注解类型（也可以通过重写方法实现）
@SupportedAnnotationTypes({"com.example.module_annotations.ActivityAnnotation"})
//设置支持（也可以通过重写方法实现）
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ActivityProcessor extends AbstractProcessor {
    private Map<String, String> activityMap = new HashMap<>();
    private String packageName;


    //工具类，用于获取Element信息
    private Elements mUtils;
    //生成java文件的类（生成代理工具类）
    private Filer filer;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        mUtils = processingEnvironment.getElementUtils();
        System.out.println("############_init");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("############_process");
        //收集所需信息
        try {
            collectInfo(roundEnvironment);
            //生成相应的代理类代码
            writeToFile();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void writeToFile() {


        // key 2017/4/25 13:38
        ClassName string = ClassName.get("java.lang", "String");


        // map类型
//        ClassName map = ClassName.get("java.util", "Map");

        // HashMap类型
        ClassName hashMap = ClassName.get("java.util", "HashMap");

        // 生成Map类型，类型的名称、Key、Value 2017/4/25 14:08
        TypeName listType = ParameterizedTypeName.get(hashMap, string, string);


        // 创建全局字段，并初始化 2017/4/25 09:15
        FieldSpec fieldSpec = FieldSpec.builder(listType, "APISet")
                .addModifiers(Modifier.PUBLIC)
                .build();


        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("APISet = new $T<>()",hashMap);


        for (String s : activityMap.keySet()) {
            String clazz = activityMap.get(s);
            constructor.addStatement("APISet.put($S,$S)", s, clazz);
        }

        // 创建方法 2017/4/24 14:26
        MethodSpec getDefaultAPIMethod = MethodSpec.methodBuilder("getClazz")
                .addModifiers(Modifier.PUBLIC) // 修饰符
                .returns(listType) // 返回类型
                .addStatement("return  APISet")
                .build();

        // 创建方法 2017/4/24 14:26
        MethodSpec initActivtyConfig = MethodSpec.methodBuilder("initActivtyConfig")
                .addModifiers(Modifier.PUBLIC,Modifier.STATIC) // 修饰符
                .addStatement("com.example.baseadhesive.api.ActivityDirectional.getInstance().addMap($S);",packageName+".ActivityConfig")
                .build();

        // 创建类
        TypeSpec hello = TypeSpec.classBuilder("ActivityConfig")
                .addModifiers(Modifier.PUBLIC)
                .addField(fieldSpec)
                .addMethod(constructor.build())
                .addMethod(initActivtyConfig)
                .addSuperinterface(ClassName.get("com.example.baseadhesive.api", "IActivityConfig"))
                .addMethod(getDefaultAPIMethod) // 添加方法
                .build();

        // 写入文件系统 2017/4/24 14:27
        JavaFile javaFile = JavaFile.builder(packageName, hello)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void collectInfo(RoundEnvironment roundEnvironment) throws ClassNotFoundException {
        activityMap.clear();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ActivityAnnotation.class);
        for (Element element : elements) {
            //从注解标记的地方取出主service的名字
            String name = element.getAnnotation(ActivityAnnotation.class).activityName();

            TypeElement typeElement = (TypeElement) element;
            packageName = mUtils.getPackageOf(typeElement).getQualifiedName().toString();

            System.out.println("#########packageName"+packageName);
            //获取当前services的完整包名
            String classFullName = typeElement.getQualifiedName().toString();
            //主服务作为键，默认服务作为值
            activityMap.put(name, classFullName);
        }
    }
}
