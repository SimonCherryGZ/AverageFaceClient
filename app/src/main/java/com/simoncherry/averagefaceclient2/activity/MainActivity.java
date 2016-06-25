package com.simoncherry.averagefaceclient2.activity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.application.MyApplication;
import com.simoncherry.averagefaceclient2.base.LayerBean;
import com.simoncherry.averagefaceclient2.event.onChangeDirectoryEvent;
import com.simoncherry.averagefaceclient2.event.onRefreshEvent;
import com.simoncherry.averagefaceclient2.fragment.FacesetFragment;
import com.simoncherry.averagefaceclient2.presenter.MainPresenter;
import com.simoncherry.averagefaceclient2.presenter.impl.MainPresenterImpl;
import com.simoncherry.averagefaceclient2.view.MainView;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView,
        NavigationView.OnNavigationItemSelectedListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,
        FacesetFragment.OnFragmentInteractionListener{

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;
    private MainPresenter presenter;

    final static private int RFAB_OUTDIR = 0;
    final static private int RFAB_INDIR = 1;
    final static private int RFAB_RESULT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        intToolBar();
        setHomeAsUpBtnEnable(false);
        setRFABItem(RFAB_OUTDIR);
        presenter = new MainPresenterImpl(this);
        // TODO
        //replaceFragment(new FacesetFragment(), MyApplication.TAG_FACESET);
        replaceFragment(new FacesetFragment().newInstance(MyApplication.TAG_FACESET), MyApplication.TAG_FACESET);
        //
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            presenter.showExitConfirmDialog(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setHomeAsUpBtnEnable(false);
        setRFABItem(RFAB_OUTDIR);

        int id = item.getItemId();
        presenter.showFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {
        Toast.makeText(this, "clicked label: " + i, Toast.LENGTH_SHORT).show();
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {
//        switch(i){
//            case 0 :
//                presenter.showCreateDirDialog(this);
//                break;
//        }
        presenter.handleRFAC(this, i);
        rfabHelper.toggleContent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            presenter.uploadImage(data.getData(), LayerBean.getDirectory()); // TODO
        }
    }

    View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO
//            if(presenter.handleHomeAsUp(MainActivity.this)){
//                setHomeAsUpBtnEnable(false);
//                setRFABItem(RFAB_OUTDIR);
//            }
            presenter.handleHomeAsUp(MainActivity.this);
        }
    };

    @Override
    public void replaceFragment(Fragment fragment, String tag) {
        Logger.t("replaceFragment").e(tag);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment, tag);
        ft.commit();
    }

    @Override
    public void addFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, fragment, tag);
        ft.commit();
    }

    @Override
    public void removeFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fm.findFragmentByTag(tag));
        ft.commit();
    }

    @Override
    public void showFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fm.findFragmentByTag(tag));
        ft.commit();
    }

    @Override
    public void hideFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(fm.findFragmentByTag(tag));
        ft.commit();
    }

    private void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        rfaLayout = (RapidFloatingActionLayout ) findViewById(R.id.activity_main_rfal);
        rfaBtn = (RapidFloatingActionButton ) findViewById(R.id.activity_main_rfab);
    }

    private void intToolBar(){
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setHomeAsUpBtnEnable(boolean enable){
        if(!enable){
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
        }else{
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(navigationClickListener);
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void setRFABItem(int which) {
        Context context = MyApplication.getContextObject();
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
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
            case 3 :
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
        rfabHelper = new RapidFloatingActionHelper(
                context,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
    }

    @Override
    public void setInDiretory(Boolean isInDir) {
        if(isInDir){
            setHomeAsUpBtnEnable(true);
            setRFABItem(RFAB_INDIR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(onChangeDirectoryEvent event){
        Logger.e("onChangeDirectory");
        switch (LayerBean.getLayer()){
            case 0:
                setToolbarTitle("Root/");
                break;
            case 1:
                setToolbarTitle("Root/" + LayerBean.getDirectory());
                setHomeAsUpBtnEnable(true);
                setRFABItem(RFAB_INDIR);
                break;
            case 2:
                break;
            case 3:
                break;
            default:
                break;
        }
    }
}
