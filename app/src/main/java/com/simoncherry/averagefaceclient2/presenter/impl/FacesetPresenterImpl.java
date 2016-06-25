package com.simoncherry.averagefaceclient2.presenter.impl;

import android.os.Handler;
import android.os.Looper;
import android.widget.ListAdapter;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient2.application.MyApplication;
import com.simoncherry.averagefaceclient2.bean.DirectoryBean;
import com.simoncherry.averagefaceclient2.bean.ImagePathBean;
import com.simoncherry.averagefaceclient2.presenter.FacesetPresenter;
import com.simoncherry.averagefaceclient2.view.FacesetView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Simon on 2016/6/19.
 */
public class FacesetPresenterImpl implements FacesetPresenter{

    Handler handler;
    private FacesetView facesetView;
    private DirectoryAdapter mListViewAdapter;
    private List<DirectoryBean> mBean;
    private ListAdapter mGridViewAdapter;
    private String imagePath[];
    // TODO
    private String type = MyApplication.TAG_FACESET;
    //

    // TODO
    //public FacesetPresenterImpl(FacesetView facesetView){
    public FacesetPresenterImpl(FacesetView facesetView, String type){
    //
        this.facesetView = facesetView;
        this.handler = new Handler(Looper.getMainLooper());
        // TODO
        this.type = type;
        //
    }

    @Override
    public String[] getImagePath() {
        return imagePath;
    }

    @Override
    public void getFacesetDirectory() {
        OkHttpUtils.get().url(MyApplication.URL_DIRECTORY)
                .addParams("request", "getdir")
                .addParams("data", "null")
                // TODO
                //.addParams("type", "faceset")
                .addParams("type", type)
                //
                .build().execute(new MyStringCallBack(MyApplication.COMMAND_FACESET, "null"));
    }

    @Override
    public void getFacesetPhoto(String path) {
        OkHttpUtils.get().url(MyApplication.URL_DIRECTORY)
                .addParams("request", "imglist")
                .addParams("data", path)
                // TODO
                //.addParams("type", "faceset")
                .addParams("type", type)
                //
                .build().execute(new MyStringCallBack(MyApplication.COMMAND_PHOTO, path));
    }

    @Override
    public List<DirectoryBean> getDirectoryBean() {
        return this.mBean;
    }

    private void setDirectoryAdapter(List<DirectoryBean> list){
        mListViewAdapter = new DirectoryAdapter(MyApplication.getContextObject(), list);
    }

    private void addDirectoryList(int imgID, String name, Long date, int count){
        DirectoryBean bean = new DirectoryBean();
        bean.setImgID(imgID);
        bean.setFileName(name);
        bean.setFileDate(date);
        bean.setFileCount(count);
        mBean.add(bean);
        mListViewAdapter.notifyDataSetChanged();
    }

    public void getFacesetDir(String reqStr){
        Gson gson = new Gson();
        DirectoryBean[] arrayBean = gson.fromJson(reqStr, DirectoryBean[].class);

        mBean = new ArrayList<>();
        setDirectoryAdapter(mBean);
        for(int i=0; i<arrayBean.length; i++){
            addDirectoryList(R.drawable.ic_folder_shared_grey600_48dp,
                    arrayBean[i].getFileName(), arrayBean[i].getFileDate(), arrayBean[i].getFileCount());
        }
    }

    public String[] getImageList(String reqStr, String selectDir) {
        Gson gson = new Gson();
        ImagePathBean[] arrayBean = gson.fromJson(reqStr, ImagePathBean[].class);
        //String[] imagePath = new String[arrayBean.length];
        imagePath = new String[arrayBean.length];
        for(int i=0; i<arrayBean.length; i++){
            //imagePath[i] = MyApplication.URL_FILE + selectDir + "/" + arrayBean[i].getImgPath();
            // TODO
            imagePath[i] = MyApplication.URL_FILE + type +"/" + selectDir + "/" + arrayBean[i].getImgPath();
            //
            //Logger.t("getImagePath").e(imagePath[i]);
        }
        return imagePath;
    }

    private class MyStringCallBack extends StringCallback {
        int mCommand;
        String mData;

        private MyStringCallBack(int command, String data){
            mCommand = command;
            mData = data;
        }

        @Override
        public void onError(Call call, Exception e) {
            Logger.t("onError").e(e.getMessage());
            facesetView.showFailedScene();
        }

        @Override
        public void onResponse(final String response) {
            Logger.t("onResponse").e(response);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(mCommand == MyApplication.COMMAND_FACESET){

                        facesetView.loadFacesetDirectory();
                        getFacesetDir(response);
                        facesetView.showFacesetDirectory(mListViewAdapter);

                    }else if(mCommand == MyApplication.COMMAND_PHOTO){

                        facesetView.loadFacesetPhoto();
                        String[] imgPath = getImageList(response, mData);
                        facesetView.showFacesetPhoto(imgPath);
                    }
                }
            });

        }
    }

}
