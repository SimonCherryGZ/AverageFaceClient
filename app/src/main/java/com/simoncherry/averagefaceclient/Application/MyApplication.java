package com.simoncherry.averagefaceclient.Application;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.Logger;

public class MyApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Logger.init();
        Logger.t("onCreate").e("init Logger");
    }

    public static Context getContextObject(){
        return context;
    }
}