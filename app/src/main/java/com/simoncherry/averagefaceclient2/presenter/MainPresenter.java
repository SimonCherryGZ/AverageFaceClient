package com.simoncherry.averagefaceclient2.presenter;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Simon on 2016/6/19.
 */
public interface MainPresenter {

    public void showFragment(int index);

    public void loadFacesetDirectory(AppCompatActivity activity);

    public boolean handleHomeAsUp(AppCompatActivity activity);
}
