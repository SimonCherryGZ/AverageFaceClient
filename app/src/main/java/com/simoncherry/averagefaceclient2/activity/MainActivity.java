package com.simoncherry.averagefaceclient2.activity;

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

import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.application.MyApplication;
import com.simoncherry.averagefaceclient2.fragment.FacesetFragment;
import com.simoncherry.averagefaceclient2.presenter.MainPresenter;
import com.simoncherry.averagefaceclient2.presenter.impl.MainPresenterImpl;
import com.simoncherry.averagefaceclient2.view.MainView;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        intToolBar();
        setHomeAsUpBtnEnable(false);
        presenter = new MainPresenterImpl(this);
        replaceFragment(new FacesetFragment(), MyApplication.TAG_FACESET);
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        setHomeAsUpBtnEnable(false);

        int id = item.getItemId();
        presenter.showFragment(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRFACItemLabelClick(int i, RFACLabelItem rfacLabelItem) {

    }

    @Override
    public void onRFACItemIconClick(int i, RFACLabelItem rfacLabelItem) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO
            //presenter.loadFacesetDirectory(MainActivity.this);
            if(presenter.handleHomeAsUp(MainActivity.this)){
                setHomeAsUpBtnEnable(false);
            }
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

    @Override
    public void setInDiretory(Boolean isInDir) {
        if(isInDir){
            setHomeAsUpBtnEnable(true);
        }
    }
}
