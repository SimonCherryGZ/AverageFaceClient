package com.simoncherry.averagefaceclient.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.simoncherry.averagefaceclient.Fragment.CloudDirFragment;
import com.simoncherry.averagefaceclient.Fragment.OutputDirFragment;
import com.simoncherry.averagefaceclient.Fragment.LocalDirFragment;
import com.simoncherry.averagefaceclient.Fragment.ResultFragment;
import com.simoncherry.averagefaceclient.R;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CloudDirFragment.OnFragmentInteractionListener,
        LocalDirFragment.OnFragmentInteractionListener,
        OutputDirFragment.OnFragmentInteractionListener,
        ResultFragment.OnFragmentInteractionListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener{

    private ViewGroup fragment_container;
    private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String mergeUrl = "http://192.168.1.102:8128/AverageFaceServer/MergeFaceServlet";
    private String uploadUrl = "http://192.168.1.102:8128/AverageFaceServer/UploadFileServlet";
    private String whichFragment = "null";
    private Boolean isInFolder = false;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    FloatingActionButton fab;
    private final static int FILE_SELECT_CODE = 0x123;
    private String whichFolder = "root";
    //=============================================================================================
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_48dp);
//        toolbar.setNavigationOnClickListener(navigationClickListener);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                if(!isInFolder){
//                    final EditText editText = new EditText(UserActivity.this);
//                    new AlertDialog.Builder(UserActivity.this).setTitle("新建人脸目录").setMessage("输入目录名称")
//                            .setIcon(android.R.drawable.ic_dialog_info).setView(editText)
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener(){
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    final String dirName = editText.getText().toString();
//                                    //Toast.makeText(UserActivity.this, dirName, Toast.LENGTH_SHORT).show();
//                                    new Thread(){
//                                        @Override
//                                        public void run(){
//                                            OkHttpUtils.get().url(dirUrl)
//                                                    .addParams("request", "newdir")
//                                                    .addParams("data", dirName)
//                                                    .build().execute(new StringCallback() {
//                                                @Override
//                                                public void onError(Call call, Exception e) {
//                                                }
//                                                @Override
//                                                public void onResponse(String response) {
//                                                    Toast.makeText(UserActivity.this, response, Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        }
//                                    }.start();
//                                }
//                            })
//                            .setNegativeButton("取消", null)
//                            .show();
//                }else{ // isInFolder == true
//                    showFileChooser();
//                }
//            }
//        });
        rfaLayout = (RapidFloatingActionLayout ) findViewById(R.id.activity_main_rfal);
        rfaBtn = (RapidFloatingActionButton ) findViewById(R.id.activity_main_rfab);
        setRfabItem(0);

        fragment_container = (ViewGroup) findViewById(R.id.fragment_container);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        whichFragment = "cloud";
        ft.replace(R.id.fragment_container, new CloudDirFragment(), "cloud");
        ft.commit();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {
        Toast.makeText(this, "clicked label: " + i, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {
        //Toast.makeText(this, "clicked icon: " + i, Toast.LENGTH_SHORT).show();
        if(!isInFolder){
            showCreateDirDialog();
        }else{ // isInFolder == true
            if(i == 0) {
                showFileChooser();
            }else if(i == 1){
                showMerageDirDialog();
            }
        }

        rfabHelper.toggleContent();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO
            final EditText editText = new EditText(this);
            new AlertDialog.Builder(this).setTitle("请输入").setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            final String path = editText.getText().toString();
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

                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            whichFragment = "result";
                            ft.replace(R.id.fragment_container, new ResultFragment(), "result");
                            ft.commit();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

//            FragmentManager fm = getSupportFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            whichFragment = "result";
//            ft.replace(R.id.fragment_container, new ResultFragment(), "result");
//            ft.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        toggle = new ActionBarDrawerToggle(
                UserActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //fab.setImageResource(R.drawable.ic_folder_shared_white_48dp);
        setRfabItem(0);
        isInFolder = false;
        whichFolder = "root";

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_faceset) {
            // TODO
            whichFragment = "local";
            ft.replace(R.id.fragment_container, new LocalDirFragment(), "local");
            ft.commit();
        } else if (id == R.id.nav_cloud) {
            whichFragment = "cloud";
            ft.replace(R.id.fragment_container, new CloudDirFragment(), "cloud");
            ft.commit();
        } else if (id == R.id.nav_output) {
            whichFragment = "output";
            ft.replace(R.id.fragment_container, new OutputDirFragment(), "output");
            ft.commit();
        } else if (id == R.id.nav_manage) {
            whichFragment = "null";
        } else if (id == R.id.nav_share) {
            whichFragment = "null";
        } else if (id == R.id.nav_send) {
            whichFragment = "null";
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setWhetherInFolder(Boolean isInFolder) {
        this.isInFolder = isInFolder;
        if(this.isInFolder){
            toggle.setDrawerIndicatorEnabled(false);
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_48dp);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(navigationClickListener);

            //fab.setImageResource(R.drawable.ic_add_white_48dp);
            setRfabItem(1);
        }else{
            //fab.setImageResource(R.drawable.ic_folder_shared_white_48dp);
            setRfabItem(0);
        }
    }

    @Override
    public void setWhichFolder(String folder) {
        this.whichFolder = folder;
    }

    View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(UserActivity.this, "click navigation", Toast.LENGTH_SHORT).show();
            if(isInFolder == true){

                if(whichFragment.equals("cloud")){
                    //Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("cloud");
                    CloudDirFragment currentFragment = (CloudDirFragment)getSupportFragmentManager().findFragmentByTag("cloud");
                    currentFragment.backUpperLevel();
                }else if(whichFragment.equals("output")){
                    OutputDirFragment currentFragment = (OutputDirFragment)getSupportFragmentManager().findFragmentByTag("output");
                    currentFragment.backUpperLevel();
                }

                isInFolder = false;
                whichFolder = "root";
                if(whichFragment.equals("cloud")){
                    CloudDirFragment currentFragment = (CloudDirFragment)getSupportFragmentManager().findFragmentByTag("cloud");
                    currentFragment.refreshFolder(whichFolder);
                }

                toggle = new ActionBarDrawerToggle(
                        UserActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                //fab.setImageResource(R.drawable.ic_folder_shared_white_48dp);
                setRfabItem(0);
            }else{

            }
        }
    };

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(UserActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String url = uri.getPath();
            //Toast.makeText(UserActivity.this, "url= " + url, Toast.LENGTH_LONG).show();
            File file = new File(url);
            //OkHttpUtils.postFile().url(uploadUrl).file(file).build()
            Map<String, String> params = new HashMap<>();
            params.put("folder", whichFolder);
            OkHttpUtils.post().addFile("mFile", url, file).url(uploadUrl).params(params).build()
                    .execute(new Callback<String>(){
                        @Override
                        public void inProgress(float progress){
                            //use progress: 0 ~ 1
                            Log.v("upload", "progress: " + String.valueOf(progress));
                            if(progress == 1.0f){
                                if(whichFragment.equals("cloud")){
                                    CloudDirFragment currentFragment = (CloudDirFragment)getSupportFragmentManager().findFragmentByTag("cloud");
                                    currentFragment.refreshFolder(whichFolder);
                                }
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setRfabItem(int which){
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(this);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
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
                .setIconShadowRadius(ABTextUtil.dip2px(this, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(this, 5))
        ;
        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
    }

    private void showCreateDirDialog(){
        final EditText editText = new EditText(UserActivity.this);
        new AlertDialog.Builder(UserActivity.this).setTitle("新建人脸目录").setMessage("输入目录名称")
                .setIcon(android.R.drawable.ic_dialog_info).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String dirName = editText.getText().toString();
                        //Toast.makeText(UserActivity.this, dirName, Toast.LENGTH_SHORT).show();
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
                                        if(whichFragment.equals("cloud")){
                                            CloudDirFragment currentFragment = (CloudDirFragment)getSupportFragmentManager().findFragmentByTag("cloud");
                                            currentFragment.refreshFolder(whichFolder);
                                        }
                                        Toast.makeText(UserActivity.this, response, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }.start();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showMerageDirDialog(){
        final EditText editText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("确定合成 " + whichFolder + " 目录内图片的平均脸吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                //.setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        //final String path = editText.getText().toString();
                        final String path = whichFolder;
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

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        whichFragment = "result";
                        ft.replace(R.id.fragment_container, new ResultFragment(), "result");
                        ft.commit();
                        setRfabItem(2);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

}
