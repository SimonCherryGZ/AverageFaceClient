package com.simoncherry.averagefaceclient.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.simoncherry.averagefaceclient.Model.MainPresenterImple;
import com.simoncherry.averagefaceclient.R;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.orhanobut.logger.Logger;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,
        UserDirFragment.OnFragmentInteractionListener,
        OutputDirFragment.OnFragmentInteractionListener,
        MergeResultFragment.OnFragmentInteractionListener{

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaBtn;
    private RapidFloatingActionHelper rfabHelper;

    private MainPresenterImple mainPresenterImple;

    final static private int RFAB_OUTDIR = 0;
    final static private int RFAB_INDIR = 1;
    final static private int RFAB_RESULT = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mainPresenterImple = new MainPresenterImple(this);
        initViews();
        initRfab(RFAB_OUTDIR);
        mainPresenterImple.setWhichDir("root");
        intToolBar();
        setHomeAsUpBtnEnable(false);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        mainPresenterImple.replaceFragment(mainPresenterImple.getFragmentTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            mainPresenterImple.showExitConfirmDialog();
        }
    }

    private void initViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        rfaLayout = (RapidFloatingActionLayout ) findViewById(R.id.activity_main_rfal);
        rfaBtn = (RapidFloatingActionButton ) findViewById(R.id.activity_main_rfab);
    }

    private void initRfab(int index){
        //mainPresenterImple = new MainPresenterImple(this);
        mainPresenterImple.setmRfabListener(this);
        mainPresenterImple.setmRfabHelper(rfabHelper);
        mainPresenterImple.setmRfabLayout(rfaLayout);
        mainPresenterImple.setmRfaBtn(rfaBtn);
        mainPresenterImple.setRFABItem(index);
        rfabHelper = mainPresenterImple.getmRfabHelper();
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

    private void setHomeAsUpBtnEnable(boolean enable){
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

    View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!mainPresenterImple.getFragmentTag().equals("result")) {
                if(mainPresenterImple.getFragmentTag().equals("user")) {
                    UserDirFragment fragment = (UserDirFragment) getSupportFragmentManager().findFragmentByTag("user");
                    // TODO
                    if(mainPresenterImple.isViewDetail()){
                        fragment.resumeFaceSet();
                    }else {
                        fragment.backUpperLevel();
                        fragment.refreshDir("root");
                        setHomeAsUpBtnEnable(false);
                        initRfab(RFAB_OUTDIR);
                        mainPresenterImple.setWhichDir("root");
                    }
                }else if(mainPresenterImple.getFragmentTag().equals("output")){
                    OutputDirFragment fragment = (OutputDirFragment) getSupportFragmentManager().findFragmentByTag("output");
                    // TODO
                    if(mainPresenterImple.isViewDetail()){
                        fragment.resumeFaceSet();
                    }else {
                        fragment.backUpperLevel();
                        fragment.refreshDir("root");
                        setHomeAsUpBtnEnable(false);
                        initRfab(RFAB_OUTDIR);
                        mainPresenterImple.setWhichDir("root");
                    }
                }
            }else {
                mainPresenterImple.removeFragment("result");
                mainPresenterImple.showFragment("user");
                mainPresenterImple.setFragmentTag("user");
                initRfab(RFAB_INDIR);
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setHomeAsUpBtnEnable(false);
        initRfab(RFAB_OUTDIR);
        mainPresenterImple.setWhichDir("root");

        int id = item.getItemId();
        switch (id){
            case R.id.nav_faceset :
                mainPresenterImple.setFragmentTag("user");
                mainPresenterImple.replaceFragment(mainPresenterImple.getFragmentTag());
                break;
            case R.id.nav_cloud :
                Toast.makeText(this, "click cloud", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_output :
                mainPresenterImple.setFragmentTag("output");
                mainPresenterImple.replaceFragment(mainPresenterImple.getFragmentTag());
                break;
            case R.id.nav_manage :
                break;
            case R.id.nav_share :
                break;
            case R.id.nav_send :
                break;
        }
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
        //Toast.makeText(this, "clicked icon: " + i, Toast.LENGTH_SHORT).show();

        if(!mainPresenterImple.getIsInDir()){
            switch (i) {
                case 0:
                    mainPresenterImple.showCreateDirDialog();
                    break;
            }

        }else {
            if(mainPresenterImple.getFragmentTag().equals("user")) {
                switch (i) {
                    case 0:
                        mainPresenterImple.showFileChooser();
                        break;
                    case 1:
                        mainPresenterImple.showMergeFaceDialog();
                        rfabHelper = mainPresenterImple.getmRfabHelper();
                        break;
                }
            }else if(mainPresenterImple.getFragmentTag().equals("result")) {
                mainPresenterImple.downloadResultImage("AverageFaceClient");
            }
        }

        rfabHelper.toggleContent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mainPresenterImple.uploadImage(data.getData());
        }
    }

    @Override
    public void setInDir(Boolean isInDir) {
        mainPresenterImple.setIsInDir(isInDir);
        if(isInDir){
            setHomeAsUpBtnEnable(true);
            initRfab(RFAB_INDIR);
        }
    }

    @Override
    public void setWhichDir(String dir) {
        mainPresenterImple.setWhichDir(dir);
    }

    @Override
    public void setViewDetail(Boolean viewDetail) {
        mainPresenterImple.setViewDetail(viewDetail);
    }

}