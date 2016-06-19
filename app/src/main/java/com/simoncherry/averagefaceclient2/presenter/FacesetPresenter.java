package com.simoncherry.averagefaceclient2.presenter;

import com.simoncherry.averagefaceclient2.bean.DirectoryBean;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

/**
 * Created by Simon on 2016/6/19.
 */
public interface FacesetPresenter {

    void getFacesetDirectory();

    void getFacesetPhoto(String path);

    List<DirectoryBean> getDirectoryBean();

    String[] getImagePath();
}
