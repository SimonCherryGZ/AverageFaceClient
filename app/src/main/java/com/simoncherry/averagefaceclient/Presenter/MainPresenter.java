package com.simoncherry.averagefaceclient.Presenter;

import android.graphics.Bitmap;
import android.net.Uri;

public interface MainPresenter {

    void replaceFragment(String tag);
    void removeFragment(String tag);
    void addFragment(String tag);
    void showFragment(String tag);
    void hideFragment(String tag);

    void setRFABItem(int which);

    void showCreateDirDialog();

    void showMergeFaceDialog();

    void showFileChooser();

    void uploadImage(Uri uri);

    void downloadResultImage(String savepath);

    void showExitConfirmDialog();
}