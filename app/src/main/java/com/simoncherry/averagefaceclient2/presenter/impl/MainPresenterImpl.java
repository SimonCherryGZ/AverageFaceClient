package com.simoncherry.averagefaceclient2.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.application.MyApplication;
import com.simoncherry.averagefaceclient2.fragment.FacesetFragment;
import com.simoncherry.averagefaceclient2.fragment.MergeFragment;
import com.simoncherry.averagefaceclient2.presenter.MainPresenter;
import com.simoncherry.averagefaceclient2.view.MainView;

/**
 * Created by Simon on 2016/6/19.
 */
public class MainPresenterImpl implements MainPresenter{

    private MainView mainView;

    private String fragmentTag;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
    }

    public String getFragmentTag() {
        return fragmentTag;
    }

    public void setFragmentTag(String fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    public void showFragmentByTag(String tag){

    }

    @Override
    public void showFragment(int index) {

        switch (index){
            case R.id.nav_faceset:
                mainView.replaceFragment(new FacesetFragment(), MyApplication.TAG_FACESET);
                break;
            case R.id.nav_cloud:
                //mainView.replaceFragment(new MergeFragment(), MyApplication.TAG_OUTPUT);
                break;
            case R.id.nav_output:
                mainView.replaceFragment(new MergeFragment(), MyApplication.TAG_MERGE);
            default:
                break;
        }
    }

    @Override
    public void loadFacesetDirectory(AppCompatActivity activity) {
        FacesetFragment facesetFragment = (FacesetFragment) activity.getSupportFragmentManager().findFragmentByTag(MyApplication.TAG_FACESET);
        facesetFragment.getPresenter().getFacesetDirectory();
    }

    @Override
    public boolean handleHomeAsUp(AppCompatActivity activity) {
        FacesetFragment facesetFragment = (FacesetFragment) activity.getSupportFragmentManager().findFragmentByTag(MyApplication.TAG_FACESET);
        if(View.VISIBLE == facesetFragment.getPhotoDraweeView().getVisibility()){
            facesetFragment.resumeFaceSet();
            return false;
        }else {
            facesetFragment.setInDirectory(false);
            facesetFragment.getPresenter().getFacesetDirectory();
            return true;
        }
    }
}
