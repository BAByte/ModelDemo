package com.example.baseadhesive.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

public class ActivityDirectional {
    private static final String TAG = "ActivityDirectional";
    private ActivityDirectional(){
    }

    public static ActivityDirectionalImp getInstance(){
        return ActivityDirectionalImp.activityDirectionalImp;
    }


     public static class ActivityDirectionalImp{
        static ActivityDirectionalImp activityDirectionalImp=new ActivityDirectionalImp();
        private HashMap<String,Class> classHashMap=new HashMap<>();
        private ActivityDirectionalImp(){
        }

        public void addMap(String p){
            try {
                Class clazz = Class.forName(p);
                IActivityConfig activityConfig = (IActivityConfig) clazz.newInstance();
                for (String s:activityConfig.getClazz().keySet()){
                    String c=activityConfig.getClazz().get(s);
                    Class clazzIn=Class.forName(c);
                    classHashMap.put(s,clazzIn);
                }
               // classHashMap.putAll(activityConfig.getClazz());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "addMap: "+classHashMap);
        }


         public void toActivity(Context context, String where){
             Intent i=new Intent(context,classHashMap.get(where));
             context.startActivity(i);
         }

         class DirectionalBuilder{

         }
    }



}
