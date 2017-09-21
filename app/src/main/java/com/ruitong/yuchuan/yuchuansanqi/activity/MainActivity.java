package com.ruitong.yuchuan.yuchuansanqi.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ruitong.yuchuan.yuchuansanqi.R;
import com.ruitong.yuchuan.yuchuansanqi.manager.GlobalManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        ViewPageActivity.actionStart(mContext,entertype);
    }
}
