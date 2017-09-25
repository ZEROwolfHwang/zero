package com.ruitong.yuchuan.yuchuansanqi.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.ruitong.yuchuan.yuchuansanqi.Constant;
import com.ruitong.yuchuan.yuchuansanqi.R;
import com.ruitong.yuchuan.yuchuansanqi.adapter.GpsListAdapate;
import com.ruitong.yuchuan.yuchuansanqi.bean.MyGpsSatellite;
import com.ruitong.yuchuan.yuchuansanqi.gps.MyLocation;
import com.ruitong.yuchuan.yuchuansanqi.surface.SatellitesView;
import com.ruitong.yuchuan.yuchuansanqi.tools.PermissionUtils;
import com.ruitong.yuchuan.yuchuansanqi.ui.HorizontalListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class GPSFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String ACTION_GPS_BROAD = "action_gps_broad";
    @BindView(R.id.gps_state)
    TextView mGpsState;
    @BindView(R.id.gps_jingdu)
    TextView mGpsJingdu;
    @BindView(R.id.gps_weidu)
    TextView mGpsWeidu;
    @BindView(R.id.gps_jingquedu)
    TextView mGpsJingquedu;
    Unbinder unbinder;


    public GPSFragment() {

    }

    public static GPSFragment newInstance(String param1, String param2) {
        GPSFragment fragment = new GPSFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    private int minTime = 1000;
    private int minDistance = 0;
    private LocationManager locationManager;
    private SatellitesView satellitesView;
    private Bitmap bg, spots;//背景和圆点的bitmap图
    private List<GpsSatellite> satelliteList = new ArrayList<>();
    private static List<MyGpsSatellite> gpsList2 = new ArrayList<>();
    private List<MyGpsSatellite> gpsList = new ArrayList<>();
    private HorizontalListView gpslv;
    private GpsListAdapate gpsListAdapate;
    private Runnable runnable;
    private String pdop = "";//位置精度
    private String hdop = "";//水平精度
    private Activity mActivity;
    private SharedPreferences sp;


    public static boolean isShow() {
        return isShow;
    }

    public static void setIsShow(boolean isShow) {
        GPSFragment.isShow = isShow;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this.getActivity();
        PermissionUtils.verifyStoragePermissions((AppCompatActivity) getActivity());
        sp = mActivity.getSharedPreferences("sp", Context.MODE_PRIVATE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_gps, container, false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_GPS_BROAD);
        GpsBroadcastReceiver broadcastReceiver = new GpsBroadcastReceiver();
        getActivity().registerReceiver(broadcastReceiver, filter);
      /*  AlxLocationManager.onCreateGPS(getActivity().getApplication());
        final Handler handler = new Handler();
        //每隔5s更新一下经纬度结果
        new Timer().scheduleAtFixedRate(new TimerTask() {//每秒钟检查一下当前位置
            @Override
            public void run() {
                handler.post(() -> {
                    double latitude = MyLocation.getInstance().latitude;
                    double longitude = MyLocation.getInstance().longitude;
                    float accuracy = MyLocation.getInstance().accuracy;
                    Logger.i(latitude+"");
                    mGpsJingdu.setText(String.valueOf(latitude));
                    mGpsWeidu.setText(String.valueOf(longitude));
                    mGpsJingquedu.setText(String.valueOf(accuracy));
                   *//* if (latitude != 0 && accuracy != 0) {
                        juDgeState(2);
                    } else {
                        juDgeState(1);
                    }*//*
                });
            }
        }, 0, 5000);*/

        bg = BitmapFactory.decodeResource(getResources(),
                R.drawable.compass1);
        spots = BitmapFactory.decodeResource(getResources(),
                R.drawable.satellite_mark);
//        gridView = (GridView) mView.findViewById(R.id.grid);
        gpslv = (HorizontalListView) mView.findViewById(R.id.gps_lv);
        gpslv.setHaveScrollbar(false);
        satellitesView = (SatellitesView) mView.findViewById(R.id.satellitesView);
        satellitesView.setBitmap(bg, spots);
        registerListener();
        satellitesView.setSatellites(satelliteList);
        if (gpsList.size() == 0) {
            Toast.makeText(mActivity, "正在加载", Toast.LENGTH_SHORT).show();
        }
        Collections.sort(gpsList2);
        gpsList.addAll(gpsList2);


        gpsListAdapate = new GpsListAdapate(gpsList, mActivity);

        if (gpsList != null) {
            gpslv.setAdapter(gpsListAdapate);
            startPush();
        }

        unbinder = ButterKnife.bind(this, mView);
        return mView;
    }

    public Handler handler = new Handler();
    /**
     * 定义广播接收器（内部类）
     *
     * @author lenovo
     *
     */
    private class GpsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MyLocation myLocation = (MyLocation) intent.getSerializableExtra(Constant.MYLOCATION);
            Logger.i(myLocation.toString());
        }

    }

    /**
     * 注册监听
     */
    private void registerListener() {
        if (locationManager == null) {
            locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        }
        //侦听位置信息(经纬度变化)
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime, minDistance, locationListener);
        // 侦听GPS状态，主要是捕获到的各个卫星的状态
        locationManager.addGpsStatusListener(gpsStatusListener);
        //TODO:考虑增加监听传感器中的方位数据，以使罗盘的北能自动指向真实的北向

    }

    /**
     * 移除监听
     */
    private void unregisterListener() {
        if (locationManager != null) {
            locationManager.removeGpsStatusListener(gpsStatusListener);
            locationManager.removeUpdates(locationListener);
        }
    }

    static public boolean isShow = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isShow = true;
            if (mActivity != null) {
                hidShuRuFa(mActivity);
            }
            //相当于Fragment的onResume
        } else {
            //相当于Fragment的onPause
            if (mActivity != null) {
                hidShuRuFa(mActivity);
            }
            isShow = false;
        }
    }

    /*
   隐藏输入法
    */
    public void hidShuRuFa(Context context) {


        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (isOpen && mActivity.getCurrentFocus() != null)

        {
            if (mActivity != null) {
                imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

    }

    /**
     * 坐标位置监听
     */
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            int x = 1;
            StringBuffer sb = new StringBuffer();
            if (location != null) {
                x = 2;
            }
            int fmt = Location.FORMAT_DEGREES;
            sb.append(Location.convert(location.getLongitude(), fmt));
            sb.append(" ");
            sb.append(Location.convert(location.getLatitude(), fmt));
            Logger.d("GPS-NMEA", location.getLatitude() + "," + location.getLongitude() + "GPS界面");
            juDgeState(x);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {

                case LocationProvider.OUT_OF_SERVICE:
                    Logger.d("GPS-NMEA", "OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Logger.d("GPS-NMEA", " TEMPORARILY_UNAVAILABLE");
                    break;
                case LocationProvider.AVAILABLE:
                    Logger.d("GPS-NMEA", "" + provider + "");
                    break;
            }


        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };

    /**
     * Gps状态监听
     */
    private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX: {
                    // 第一次定位时间UTC gps可用
                    break;
                }
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS: {// 周期的报告卫星状态
                    // 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                    satelliteList.clear();
                    gpsList2.clear();
                    if (satellites != null)
                        for (GpsSatellite satellite : satellites) {
                            // 包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                            if (satellite.usedInFix()) {
                                if (satellite.getPrn() <= 32) {
                                    satelliteList.add(satellite);
                                    MyGpsSatellite my = new MyGpsSatellite();
                                    my.setmPrn(satellite.getPrn());
                                    my.setmAzimuth(satellite.getAzimuth());
                                    my.setmSnr(satellite.getSnr());
                                    my.setmElevation(satellite.getElevation());
                                    gpsList2.add(my);
                                }
                            }
                        }
                    break;
                }

                case GpsStatus.GPS_EVENT_STARTED: {

                    break;
                }
                case GpsStatus.GPS_EVENT_STOPPED: {

                    break;
                }
                default:

                    break;
            }
        }
    };

    public void initList() {
        mActivity.runOnUiThread(() -> {
            if (gpsList2.size() > 0) {
                gpsList.clear();
                Collections.sort(gpsList2);
                gpsList.addAll(gpsList2);

                gpsListAdapate.notifyDataSetChanged();
            }
        });
    }

    public void startPush() {

        if (handler == null) {
            handler = new Handler();
        }
        if (runnable == null) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    satellitesView.setSatellites(satelliteList);
                    initList();
                    handler.postDelayed(this, 2000);

                }
            };
            handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerListener();
        satellitesView.setBitmap(bg, spots);
    }

    @Override
    public void onDestroy() {
        unregisterListener();
        super.onDestroy();
    }

    private void juDgeState(final int flag) {
        mActivity.runOnUiThread(() -> {
            switch (flag) {
                case 1:
                    mGpsState.setText("未定位");
                    mGpsState.setTextColor(Color.RED);
                    gpsList.clear();
                    gpsListAdapate.notifyDataSetChanged();
                    break;
                case 2:
                    mGpsState.setText("定位");
                    mGpsState.setTextColor(Color.GREEN);

                    break;
            }
        });
    }

    List<Integer> sateLiteIntList = new ArrayList<>();

/*    public void getgpsjd(Jdbean jdbean) {
        if (jdbean != null) {
            if (jdbean.getPdop() != null && jdbean.getHdop() != null) {
                pdop = jdbean.getPdop();
                hdop = jdbean.getHdop();
                sateLiteIntList = jdbean.getSateInList();
                if (!jdbean.getSfdw().equals("1")) {
                    if (!hdop.equals("") && !hdop.equals("500.0") && !hdop.equals("500") && hdop != null && hdoptv != null) {
                        hdoptv.setText(hdop);
                    }
                    if (!pdop.equals("") && !pdop.equals("500.0") && !pdop.equals("500") && pdop != null && hdoptv != null) {
                        pdoptv.setText(pdop);
                    }
                }
            } else if (jdbean.getSfdw().equals("1")) {
                pdoptv.setText("0.0");
                hdoptv.setText("0.0");
            }
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
