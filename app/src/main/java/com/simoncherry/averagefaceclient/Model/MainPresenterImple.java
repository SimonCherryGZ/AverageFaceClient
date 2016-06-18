package com.simoncherry.averagefaceclient.Model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.simoncherry.averagefaceclient.Presenter.MainPresenter;
import com.simoncherry.averagefaceclient.R;
import com.simoncherry.averagefaceclient.Application.MyApplication;
import com.simoncherry.averagefaceclient.View.MergeResultFragment;
import com.simoncherry.averagefaceclient.View.UserDirFragment;
import com.simoncherry.averagefaceclient.View.OutputDirFragment;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class MainPresenterImple implements MainPresenter {

    //private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String dirUrl = "http://192.168.1.103:8128/AverageFaceServer/DirectoryServlet";
    //private String uploadUrl = "http://192.168.1.102:8128/AverageFaceServer/UploadFileServlet";
    private String uploadUrl = "http://192.168.1.103:8128/AverageFaceServer/UploadFileServlet";
    //private String mergeUrl = "http://192.168.1.102:8128/AverageFaceServer/MergeFaceServlet";
    private String mergeUrl = "http://192.168.1.103:8128/AverageFaceServer/MergeFaceServlet";


    private String fragmentTag = "user";
    private boolean isInDir = false;
    private String whichDir = "root";
    // TODO
    private boolean isViewDetail = false;
    final static private int RFAB_OUTDIR = 0;
    final static private int RFAB_INDIR = 1;
    final static private int RFAB_RESULT = 2;

    private AppCompatActivity mActivity;
    private RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener mListener;
    private RapidFloatingActionHelper mRfabHelper;
    private RapidFloatingActionLayout mRfabLayout;
    private RapidFloatingActionButton mRfaBtn;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;


    public MainPresenterImple(AppCompatActivity activity){
        setmActivity(activity);
        Log.v("indir", "MainPresenterImple: " + getIsInDir());
    }

    // TODO
    public boolean isViewDetail() {
        return isViewDetail;
    }

    public void setViewDetail(boolean viewDetail) {
        isViewDetail = viewDetail;
    }

    public FragmentActivity getmActivity() {
        return mActivity;
    }

    public void setmActivity(AppCompatActivity mActivity) {
        this.mActivity = mActivity;
    }

    public RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener getmRfabListener() {
        return mListener;
    }

    public void setmRfabListener(RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener listener){
        this.mListener = listener;
    }

    public RapidFloatingActionHelper getmRfabHelper(){
        return mRfabHelper;
    }

    public void setmRfabHelper(RapidFloatingActionHelper mRfabHelper) {
        this.mRfabHelper = mRfabHelper;
    }

    public void setmRfabLayout(RapidFloatingActionLayout mRfabLayout) {
        this.mRfabLayout = mRfabLayout;
    }

    public RapidFloatingActionLayout getmRfabLayout() {
        return mRfabLayout;
    }

    public RapidFloatingActionButton getmRfaBtn() {
        return mRfaBtn;
    }

    public void setmRfaBtn(RapidFloatingActionButton mRfaBtn) {
        this.mRfaBtn = mRfaBtn;
    }

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    public void setmToolbar(Toolbar mToolbar) {
        this.mToolbar = mToolbar;
    }

    public DrawerLayout getmDrawer() {
        return mDrawer;
    }

    public void setmDrawer(DrawerLayout mDrawer) {
        this.mDrawer = mDrawer;
    }

    public ActionBarDrawerToggle getmToggle() {
        return mToggle;
    }

    public void setmToggle(ActionBarDrawerToggle mToggle) {
        this.mToggle = mToggle;
    }

    public String getFragmentTag(){
        return fragmentTag;
    }

    public void setFragmentTag(String tag){
        fragmentTag = tag;
    }

    public boolean getIsInDir(){
        return this.isInDir;
    }

    public void setIsInDir(boolean isInDir){
        this.isInDir = isInDir;
        Log.v("indir", "setInDir: " + this.isInDir);
    }

    public String getWhichDir() {
        return whichDir;
    }

    public void setWhichDir(String whichDir) {
        this.whichDir = whichDir;
    }


    @Override
    public void replaceFragment(String tag) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        if(tag.equals("output")){
            fragment = new OutputDirFragment();
        }else if(tag.equals("result")){
            fragment = new MergeResultFragment();
        }else if(tag.equals("user")){
            fragment = new UserDirFragment();
        }

        if(fragment != null) {
            ft.replace(R.id.fragment_container, fragment, tag);
            ft.commit();
        }
    }

    @Override
    public void removeFragment(String tag) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);

        if(fragment != null) {
            ft.remove(fm.findFragmentByTag(tag));
            ft.commit();
        }
    }

    @Override
    public void addFragment(String tag) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;

        if(tag.equals("output")){
            fragment = new OutputDirFragment();
        }else if(tag.equals("result")){
            fragment = new MergeResultFragment();
        }else if(tag.equals("user")){
            fragment = new UserDirFragment();
        }

        if(fragment != null) {
            ft.add(R.id.fragment_container, fragment, tag);
            ft.commit();
        }
    }

    @Override
    public void showFragment(String tag) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);

        if(fragment != null) {
            ft.show(fm.findFragmentByTag(tag));
            ft.commit();
        }
    }

    @Override
    public void hideFragment(String tag) {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);

        if(fragment != null) {
            ft.hide(fm.findFragmentByTag(tag));
            ft.commit();
        }
    }

    @Override
    public void setRFABItem(int which) {
        Context context = MyApplication.getContextObject();
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(mListener);
        List<RFACLabelItem> items = new ArrayList<>();
        switch(which){
            case 0 :
                items.add(new RFACLabelItem<Integer>()
                        .setLabel("新建")
                        .setResId(R.drawable.ic_folder_shared_white_48dp)
                        .setIconNormalColor(R.color.colorAccent)
                        .setIconPressedColor(R.color.colorAccentDark)
                        .setWrapper(0)
                );
                break;
            case 1 :
                items.add(new RFACLabelItem<Integer>()
                        .setLabel("上传")
                        .setResId(R.drawable.ic_file_upload_white_48dp)
                        .setIconNormalColor(R.color.colorAccent)
                        .setIconPressedColor(R.color.colorAccentDark)
                        .setWrapper(0)
                );
                items.add(new RFACLabelItem<Integer>()
                        .setLabel("合成")
                        .setResId(R.drawable.ic_group_add_white_48dp)
                        .setIconNormalColor(R.color.colorAccent)
                        .setIconPressedColor(R.color.colorAccentDark)
                        .setWrapper(1)
                );
                break;
            case 2 :
                items.add(new RFACLabelItem<Integer>()
                        .setLabel("保存")
                        .setResId(R.drawable.ic_file_download_white_48dp)
                        .setIconNormalColor(R.color.colorAccent)
                        .setIconPressedColor(R.color.colorAccentDark)
                        .setWrapper(0)
                );
                items.add(new RFACLabelItem<Integer>()
                        .setLabel("搜索")
                        .setResId(R.drawable.ic_pageview_white_48dp)
                        .setIconNormalColor(R.color.colorAccent)
                        .setIconPressedColor(R.color.colorAccentDark)
                        .setWrapper(1)
                );
                break;
            default:
                break;
        }
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(context, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(context, 5))
        ;
        mRfabHelper = new RapidFloatingActionHelper(
                context,
                mRfabLayout,
                mRfaBtn,
                rfaContent
        ).build();
    }

    @Override
    public void showCreateDirDialog() {
        final EditText editText = new EditText(mActivity);
        new AlertDialog.Builder(mActivity).setTitle("新建人脸目录").setMessage("输入目录名称")
                .setIcon(R.drawable.ic_error_grey600_48dp).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String dirName = editText.getText().toString();
                        new Thread(){
                            @Override
                            public void run(){
                                OkHttpUtils.get().url(dirUrl)
                                        .addParams("request", "newdir")
                                        .addParams("data", dirName)
                                        .addParams("type", "faceset")
                                        .build().execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e) {
                                    }
                                    @Override
                                    public void onResponse(String response) {
                                            UserDirFragment currentFragment = (UserDirFragment)mActivity.getSupportFragmentManager().findFragmentByTag("user");
                                            currentFragment.refreshDir("root");
                                        //Toast.makeText(mActivity, response, Toast.LENGTH_SHORT).show();
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
    public void showMergeFaceDialog() {
        new AlertDialog.Builder(mActivity)
                .setTitle("提示")
                .setMessage("确定合成 " + getWhichDir() + " 目录内图片的平均脸吗？")
                .setIcon(R.drawable.ic_error_grey600_48dp)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        final String path = getWhichDir();
                        new Thread(){
                            @Override
                            public void run(){
                                try {
                                    OkHttpUtils.post().url(mergeUrl).addParams("path", path).build().execute();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();

//                        FragmentManager fm = getSupportFragmentManager();
//                        FragmentTransaction ft = fm.beginTransaction();
//                        whichFragment = "result";
//                        //ft.replace(R.id.fragment_container, new ResultFragment(), "result");
//                        ft.add(R.id.fragment_container, new ResultFragment(), "result");
//                        ft.hide(fm.findFragmentByTag("cloud"));
//                        ft.commit();
//                        setRfabItem(2);

                        addFragment("result");
                        hideFragment("user");
                        setFragmentTag("result");
                        setRFABItem(RFAB_RESULT);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            mActivity.startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), 0);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(mActivity, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void uploadImage(Uri uri) {
        String url = uri.getPath();
        File file = new File(url);
        Map<String, String> params = new HashMap<>();
        params.put("folder", getWhichDir());
        OkHttpUtils.post()
                .addFile("mFile", url, file)
                .url(uploadUrl)
                .params(params)
                .build().execute(new Callback<String>() {
                    @Override
                    public void inProgress(float progress){
                        Log.v("upload", "progress: " + String.valueOf(progress));
                        if(progress == 1.0f){
//                            UserDirFragment fragment = (UserDirFragment) mActivity.getSupportFragmentManager().findFragmentByTag("user");
//                            fragment.refreshDir(getWhichDir());
                            new Handler().postDelayed(new Runnable(){
                                public void run() {
                                    UserDirFragment fragment = (UserDirFragment) mActivity.getSupportFragmentManager().findFragmentByTag("user");
                                    fragment.refreshDir(getWhichDir());
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
    public void downloadResultImage(String savepath) {
        if(getSDPath() == null) {
            Toast.makeText(mActivity, "没有内存卡", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(getSDPath() + "/" +savepath);
        if(!file.exists()) {
            boolean res = file.mkdirs();
            if(!res){
                Toast.makeText(mActivity, "创建目录失败", Toast.LENGTH_SHORT).show();
            }
        }

        File imageFile = new File(file, System.currentTimeMillis() + ".jpg");
        try {
            imageFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imageFile);
            FragmentManager fm = mActivity.getSupportFragmentManager();
            Bitmap bitmap = convertViewToBitmap(fm.findFragmentByTag("result").getView().findViewById(R.id.img_result));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(mActivity, "图片已保存到 " + getSDPath()+"/"+savepath, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(mActivity, "图片保存失败 FileNotFoundException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(mActivity, "图片保存失败 IOException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void showExitConfirmDialog() {
        new AlertDialog.Builder(mActivity).setTitle("提示").setMessage("确定要退出本应用吗？")
                .setIcon(R.drawable.ic_error_grey600_48dp)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActivity.finish();
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