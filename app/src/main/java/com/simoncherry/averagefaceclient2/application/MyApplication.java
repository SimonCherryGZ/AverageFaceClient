package com.simoncherry.averagefaceclient2.application;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Simon on 2016/6/19.
 */
public class MyApplication extends Application {
    private static Context context;
    public final static int INDEX_FACESET = 0;
    public final static int INDEX_OUTPUT = 1;
    public final static int INDEX_MERGE = 2;

    public final static String TAG_FACESET = "faceset";
    public final static String TAG_OUTPUT = "output";
    public final static String TAG_MERGE = "merge";

    public final static String URL_DIRECTORY = "http://192.168.1.103:8128/AverageFaceServer/DirectoryServlet";
    // TODO
    //public final static String URL_FILE = "http://192.168.1.103:8128/AverageFaceServer/faceset/";
    public final static String URL_FILE = "http://192.168.1.103:8128/AverageFaceServer/";
    //
    public final static String URL_DOWNLOAD = "http://192.168.1.103:8128/AverageFaceServer/";
    public final static String URL_UPLOAD = "http://192.168.1.103:8128/AverageFaceServer/UploadFileServlet";
    public final static String URL_MERGE = "http://192.168.1.103:8128/AverageFaceServer/MergeFaceServlet";

    public final static int COMMAND_FACESET = 0x123;
    public final static int COMMAND_PHOTO = 0x456;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        JPushInterface.init(this);
        Fresco.initialize(this);
        Logger.init();
        Logger.t("onCreate").e("init Logger");
    }

    public static Context getContextObject(){
        return context;
    }
}
