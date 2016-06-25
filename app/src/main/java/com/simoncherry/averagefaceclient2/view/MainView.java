package com.simoncherry.averagefaceclient2.view;

import android.support.v4.app.Fragment;

/**
 * Created by Simon on 2016/6/19.
 */
public interface MainView {

    void replaceFragment(Fragment fragment, String tag);

    void addFragment(Fragment fragment, String tag);

    void removeFragment(String tag);

    void showFragment(String tag);

    void hideFragment(String tag);

    void setRFABItem(int which);

    void setHomeAsUpBtnEnable(boolean enable);

    void setToolbarTitle(String title);
}
