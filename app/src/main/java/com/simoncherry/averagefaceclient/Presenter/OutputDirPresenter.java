package com.simoncherry.averagefaceclient.Presenter;

import com.zhy.http.okhttp.callback.StringCallback;

public interface OutputDirPresenter {
    void queryFacesetDir(String url, String request, String data, String type, StringCallback callback);
    void getFacesetDir(String reqStr);
    void getImageList(String reqStr, String selectDir);
    void refreshDir(String path, StringCallback callback);

}