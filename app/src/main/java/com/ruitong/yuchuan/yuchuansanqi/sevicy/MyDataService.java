package com.ruitong.yuchuan.yuchuansanqi.sevicy;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.ruitong.yuchuan.yuchuansanqi.tools.TxtFileUtile;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyDataService extends Service {
    private Timer timer;
    private TimerTask task;
    private GpsStatus.NmeaListener mNmeaListener;
    private ArrayList<Integer> mLsSate;

    public MyDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("开启加载GPS信息的服务");
        mNmeaListener = (timestamp, nmea) -> {
            String path = Environment.getExternalStorageDirectory().getPath();
            String gpsPath = path + "/" + "gps.txt";
//            Logger.i(gpsPath);

            new Thread(() -> {
                try {
                    if (nmea != null && !TextUtils.isEmpty(nmea)) {
                        TxtFileUtile.fileOutputStream(nmea, gpsPath, true);
                    }
                } catch (Exception exc) {
                }
            }).start();

            if (mLsSate == null) {
                mLsSate = new ArrayList<>();
            } else {
                mLsSate.clear();
            }
            Logger.d("GPS-NMEA", nmea);

            if (nmea != null && nmea.contains("$GPGSA")) {

            }
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.addNmeaListener(mNmeaListener);

//        Intent intent = new Intent(this, ViewPageActivity.class);
//        timer = new Timer();
//        task = new TimerTask() {
//            @Override
//            public void run() {
////                intent.putExtra();
//                sendBroadcast(intent);
//            }
//        };
//        timer.schedule(task, 1000, 1000);
    }

}
