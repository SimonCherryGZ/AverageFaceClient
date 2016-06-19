package com.simoncherry.averagefaceclient2.view;

import android.widget.ListAdapter;

import com.simoncherry.averagefaceclient2.adapter.DirectoryAdapter;

/**
 * Created by Simon on 2016/6/19.
 */
public interface FacesetView {

    void showLoadingScene();

    void loadFacesetDirectory();

    void loadFacesetPhoto();

    void showFailedScene();

    void showFacesetDirectory(DirectoryAdapter adapter);

    void showFacesetPhoto(String[] imagePath);
}
