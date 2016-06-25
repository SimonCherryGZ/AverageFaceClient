package com.simoncherry.averagefaceclient2.presenter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Simon on 2016/6/19.
 */
public interface MainPresenter {

    public void showFragment(int index);

    public void loadFacesetDirectory(AppCompatActivity activity);

    public void handleHomeAsUp(AppCompatActivity activity);

    public void handleRFAC(AppCompatActivity activity, int index);

    public void showCreateDirDialog(Context context);

    public void showFileChooser(Activity mActivity);

    public void uploadImage(Uri uri, String diretory);

    public void downloadResultImage(AppCompatActivity activity, String savepath);

    public void showExitConfirmDialog(final Activity activity);
}
