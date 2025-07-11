package com.kashtansystem.project.gloriyamarketing.service;

import static android.app.Service.START_STICKY;
import static android.support.v4.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSendLocCurier;

import java.security.Provider;

public class LocationForegroundService extends Service {
    private Handler handler = new Handler();
    private Runnable locationSenderRunnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());

        locationSenderRunnable = new Runnable() {
            @Override
            public void run() {
                // 1. Permission tekshirish
                if (ContextCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Ruxsat so‘rash (bu joyda dialog yoki notification orqali foydalanuvchiga xabar berishingiz mumkin)
                    // Agar background location ham kerak bo‘lsa, uni ham so‘rang
                    Log.e("LocationService", "Location permission not granted!");
                    // Ruxsat so‘rash uchun ActivityCompat.requestPermissions chaqiring (faqat Activityda)
                    return;
                }

                // 2. GPS yoqilganini tekshirish
                getApplicationContext();
                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGpsEnabled) {
                    Log.e("LocationService", "GPS yoqilmagan!");
                    // Foydalanuvchiga GPS’ni yoqishni so‘rab dialog yoki notification chiqaring
                    return;
                }

                // 3. Koordinatalarni olish
                double latitude = 0.0;
                double longitude = 0.0;
                Location location = null;

                // Avval oxirgi ma’lum bo‘lgan lokatsiyani olishga harakat qilamiz
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }

                // 4. Koordinatalar null yoki 0.0 emasligini tekshirish
                if (latitude == 0.0 && longitude == 0.0) {
                    Log.e("LocationService", "Koordinatalar mavjud emas yoki signal yo‘q!");
                    // Foydalanuvchiga xabar chiqaring
                    handler.postDelayed(this, 30000);
                    return;
                }

                // 5. UserCode va type olish (masalan, SharedPreferences yoki AppCache orqali)
                String userCode = "000000423"; // Misol uchun:
               /* SharedPreferences prefs = getApplicationContext().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                userCode = prefs.getString("usercode", "");*/

                int type = 2; // Ekspeditor uchun

                // 6. Serverga yuborish
                ReqSendLocCurier.send(userCode, latitude, longitude);

                // 7. 30 sekunddan keyin yana ishga tushirish
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(locationSenderRunnable);

        return START_STICKY; // Service avtomatik qayta ishga tushadi
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(locationSenderRunnable);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification() {
        // Foreground service uchun notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "location_channel")
                .setContentTitle("Lokatsiya kuzatuvchi")
                .setContentText("Kordinatalar serverga yuborilmoqda")
                .setSmallIcon(R.drawable.ic_download)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Android 8+ uchun kanal yaratish
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "location_channel",
                    "Lokatsiya xabarnomasi",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return builder.build();
    }
}
