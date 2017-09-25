package com.ruitong.yuchuan.yuchuansanqi.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.ruitong.yuchuan.yuchuansanqi.R;
import com.ruitong.yuchuan.yuchuansanqi.gps.AlxLocationManager;
import com.ruitong.yuchuan.yuchuansanqi.gps.MyLocation;
import com.ruitong.yuchuan.yuchuansanqi.manager.GlobalManager;
import com.ruitong.yuchuan.yuchuansanqi.tools.PermissionUtils;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_jingdu)
    TextView mMainJingdu;
    @BindView(R.id.main_weidu)
    TextView mMainWeidu;
    @BindView(R.id.main_hangsu)
    TextView mMainHangsu;
    @BindView(R.id.main_hangxiang)
    TextView mMainHangxiang;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        PermissionUtils.verifyStoragePermissions(this);
        //开启位置监听
        AlxLocationManager.onCreateGPS(getApplication());

        final Handler handler = new Handler();
        //每隔2s更新一下经纬度结果
        new Timer().scheduleAtFixedRate(new TimerTask() {//每秒钟检查一下当前位置
            @Override
            public void run() {
                handler.post(() -> {
                    mMainJingdu.setText(String.valueOf(MyLocation.getInstance().latitude));
                    mMainWeidu.setText(String.valueOf(MyLocation.getInstance().longitude));
                    mMainHangsu.setText(String.valueOf(MyLocation.getInstance().speed));
                    if (MyLocation.getInstance().updateTime != 0)
                        Logger.i(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(MyLocation.getInstance().updateTime)));
                });
            }
        }, 0, 5000);
        LitePal.getDatabase();
        mContext = this;
        initToolbar();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ais) {
            enterFourPageActivity(GlobalManager.ENTERTYPE_AIS);
            // Handle the camera action
        } else if (id == R.id.nav_gps) {
            enterFourPageActivity(GlobalManager.ENTERTYPE_GPS);
        } else if (id == R.id.nav_compass) {
            enterFourPageActivity(GlobalManager.ENTERTYPE_COMPASS);
        } else if (id == R.id.nav_setting) {
            enterFourPageActivity(GlobalManager.ENTERTYPE_SETTING);

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_update) {

        } else if (id == R.id.nav_backup) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void enterFourPageActivity(String entertype) {
        ViewPageActivity.actionStart(mContext, entertype);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AlxLocationManager.stopGPS();
    }
}
