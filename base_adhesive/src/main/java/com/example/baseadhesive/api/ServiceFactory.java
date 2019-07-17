package com.example.baseadhesive.api;

import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServiceFactory {
    private static final String TAG = "ServiceFactory";

    private ServiceFactory() {
    }

    public static IServiceFactory getInstance() {
        return ServiceFactoryImp.serviceFactoryImp;
    }

    static class ServiceFactoryImp implements IServiceFactory {
        static ServiceFactoryImp serviceFactoryImp = new ServiceFactoryImp();
        private IServiceConfig serviceConfig;
        private HashMap<String, Object> hashMap = new HashMap<>();

        private ServiceFactoryImp() {
            //获取表
            try {
                Class clazz = Class.forName("com.example.baseadhesive.api.service.ServiceConfig");
                serviceConfig = (IServiceConfig) clazz.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void addService(String id, Object o) {
            hashMap.put(id, o);
        }

        @Override
        public Object getService(String id) {
            Log.d(TAG, "getService: ");
            Object o = hashMap.get(id);
            if (o == null) {
                o = getDefaultAPI(id);
            }
            return o;
        }


        private Object getDefaultAPI(String id) {
            Log.d(TAG, "getDefaultAPI: " + serviceConfig);
            Log.d(TAG, "getDefaultAPI: " + serviceConfig.getDefaultAPI(id));
            Object o = null;
            try {
                Class clazz = Class.forName(serviceConfig.getDefaultAPI(id));
                o = clazz.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return o;
        }
    }

}