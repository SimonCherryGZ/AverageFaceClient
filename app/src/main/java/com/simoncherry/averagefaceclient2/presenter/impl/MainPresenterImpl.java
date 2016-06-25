package com.simoncherry.averagefaceclient2.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.application.MyApplication;
import com.simoncherry.averagefaceclient2.base.LayerBean;
import com.simoncherry.averagefaceclient2.event.onRefreshEvent;
import com.simoncherry.averagefaceclient2.fragment.FacesetFragment;
import com.simoncherry.averagefaceclient2.fragment.MergeFragment;
import com.simoncherry.averagefaceclient2.presenter.MainPresenter;
import com.simoncherry.averagefaceclient2.view.MainView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

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
    public void handleHomeAsUp(AppCompatActivity activity) {
//        FacesetFragment facesetFragment = (FacesetFragment) activity.getSupportFragmentManager().findFragmentByTag(MyApplication.TAG_FACESET);
//        if(View.VISIBLE == facesetFragment.getPhotoDraweeView().getVisibility()){
//            facesetFragment.resumeFaceSet();
//            return false;
//        }else {
//            // TODO
//            facesetFragment.setInDirectory(false);
//            facesetFragment.getPresenter().getFacesetDirectory();
//            return true;
//            //
//        }
        if(LayerBean.getLayer() == 1){
            LayerBean.setLayer(0);
            EventBus.getDefault().post(new onRefreshEvent());
            mainView.setRFABItem(0);
            mainView.setHomeAsUpBtnEnable(false);
        }else if(LayerBean.getLayer() == 2){
            LayerBean.setLayer(1);
            EventBus.getDefault().post(new onRefreshEvent());
            mainView.setRFABItem(1);
        }else {
            // TODO
            mainView.removeFragment(MyApplication.TAG_MERGE);
            LayerBean.setLayer(1);
            mainView.showFragment(MyApplication.TAG_FACESET);
            mainView.setRFABItem(1);
        }
    }

    @Override
    public void handleRFAC(AppCompatActivity activity, int index){
        switch(LayerBean.getLayer()){
            case 0:
                switch ((index)){
                    case 0:
                        showCreateDirDialog(activity);
                        break;
                    default:
                        break;
                }
                break;
            case 1:
                switch ((index)){
                    case 0:
                        showFileChooser(activity);
                        break;
                    case 1:
                        showMergeFaceDialog(activity, LayerBean.getDirectory()); // TODO
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                break;
            case 3:
                switch ((index)){
                    case 0:
                        downloadResultImage(activity, "AverageFaceClient"); // TODO
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showCreateDirDialog(Context context) {
        final EditText editText = new EditText(context);
        new AlertDialog.Builder(context).setTitle("新建人脸目录").setMessage("输入目录名称")
                .setIcon(R.drawable.ic_error_grey600_48dp).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String dirName = editText.getText().toString();
                        new Thread(){
                            @Override
                            public void run(){
                                OkHttpUtils.get().url(MyApplication.URL_DIRECTORY)
                                        .addParams("request", "newdir")
                                        .addParams("data", dirName)
                                        .addParams("type", "faceset")
                                        .build().execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {
                                        //Toast.makeText(mActivity, response, Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onResponse(String response) {
                                        //UserDirFragment currentFragment = (UserDirFragment)mActivity.getSupportFragmentManager().findFragmentByTag("user");
                                        //currentFragment.refreshDir("root");
                                        Logger.t("new dir").e("onResponse");
                                        EventBus.getDefault().post(new onRefreshEvent());
                                    }
                                });
                            }
                        }.start();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void showFileChooser(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 0);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(activity, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void uploadImage(Uri uri, String diretory) {
        String url = uri.getPath();
        File file = new File(url);
        Map<String, String> params = new HashMap<>();
        params.put("folder", diretory);
        OkHttpUtils.post()
                .addFile("mFile", url, file)
                .url(MyApplication.URL_UPLOAD)
                .params(params)
                .build().execute(new Callback<String>() {
            @Override
            public void inProgress(float progress){
                Log.v("upload", "progress: " + String.valueOf(progress));
                if(progress == 1.0f){
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            EventBus.getDefault().post(new onRefreshEvent());
                        }
                    }, 1000);
                }
            }
            @Override
            public String parseNetworkResponse(Response response) throws Exception {
                return null;
            }
            @Override
            public void onError(Call call, Exception e) {
            }
            @Override
            public void onResponse(String response) {
            }
        });
    }

    private String getSDPath() {
        File sdDir = Environment.getExternalStorageDirectory();// 获取根目录
        return sdDir.toString();
    }

    private static Bitmap convertViewToBitmap(View view){
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache().copy(Bitmap.Config.RGB_565, false);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    public void downloadResultImage(AppCompatActivity activity, String savepath) {
        if(getSDPath() == null) {
            Toast.makeText(activity, "没有内存卡", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(getSDPath() + "/" +savepath);
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if(!res){
                Toast.makeText(activity, "创建目录失败", Toast.LENGTH_SHORT).show();
            }
        }

        File imageFile = new File(file, System.currentTimeMillis() + ".jpg");
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            // TODO
            FragmentManager fm = activity.getSupportFragmentManager();
            Bitmap bitmap = convertViewToBitmap(fm.findFragmentByTag(MyApplication.TAG_MERGE).getView().findViewById(R.id.img_result));
            //
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(activity, "图片已保存到 " + getSDPath()+"/"+savepath, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, "图片保存失败 FileNotFoundException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(activity, "图片保存失败 IOException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void showMergeFaceDialog(Activity activity, final String diretory) {
        new AlertDialog.Builder(activity)
                .setTitle("提示")
                .setMessage("确定合成 " + diretory + " 目录内图片的平均脸吗？")
                .setIcon(R.drawable.ic_error_grey600_48dp)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        new Thread(){
                            @Override
                            public void run(){
                                try {
                                    OkHttpUtils.post().url(MyApplication.URL_MERGE).addParams("path", diretory).build().execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

                        // TODO
//                        addFragment("result");
//                        hideFragment("user");
//                        setFragmentTag("result");
//                        setRFABItem(RFAB_RESULT);
                        mainView.addFragment(new MergeFragment(), MyApplication.TAG_MERGE);
                        mainView.hideFragment(MyApplication.TAG_FACESET);
                        mainView.setRFABItem(3);
                        LayerBean.setLayer(3);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void showExitConfirmDialog(final Activity activity) {
        new AlertDialog.Builder(activity).setTitle("提示").setMessage("确定要退出本应用吗？")
                .setIcon(R.drawable.ic_error_grey600_48dp)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
