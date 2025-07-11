package com.kashtansystem.project.gloriyamarketing.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SendOrderResponseTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSendLoc;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSendOrder;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.GPS;
import com.kashtansystem.project.gloriyamarketing.utils.GPStatus;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.LoadProductsAlarm;
import com.kashtansystem.project.gloriyamarketing.utils.SendOrdersByAlarm;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by FlameKaf on 10.05.2017.
 * ----------------------------------
 * Сервис приложения.
 * Создаёт BroadcastReceiver, получающий события изменения состояния сети телефона и отправляющий
 * заказы на сервер.
 */

public class MainService extends Service implements GPS.OnCoordinatsReceiveListener
{
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private GPS geolocation;
    private int lastUpdTime = 15;
    private int currentDay;
    private String tpCode = "";
    private SendLoc sendLoc;
    private SendingOrders sendOrds;

    private BroadcastReceiver networkOberser = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();
            if (TextUtils.isEmpty(action))
                return;

            switch (action)
            {
                case Intent.ACTION_DATE_CHANGED:
                    currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    L.info("current day of week: " + currentDay);
                break;
                case Intent.ACTION_TIME_TICK:
                    if (currentDay == Calendar.SUNDAY)
                        return;

                    final int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    if (hourOfDay >= 9 && hourOfDay <= 16)
                    {
                        lastUpdTime++;
                        if (lastUpdTime >= 15)
                        {
                            lastUpdTime = 0;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                    return;
                            }
                            geolocation.startRequest();
                        }
                    }
                break;
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    if (getNetworkState((ConnectivityManager)context.getSystemService(Service.CONNECTIVITY_SERVICE)) == NetworkInfo.State.CONNECTED)
                        sendingIsWaitingOrders(false);
                break;
                case C.ACTIONS.ACTION_RESEND_ORDERS:
                    sendingIsWaitingOrders(true);
                break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        L.info("start day: " + currentDay);
        geolocation = new GPS(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_DATE_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(C.ACTIONS.ACTION_RESEND_ORDERS);
        registerReceiver(networkOberser, filter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(networkOberser);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int actionId)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(C.ACTIONS.NOTIF_ABOUT_VISIT))
            {
                tpCode = intent.getStringExtra(C.KEYS.EXTRA_DATA_TP);
                if (geolocation != null)
                    geolocation.startRequest();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onReceiveCoordinats(GPStatus status, Location location, boolean fromMock)
    {
        switch (status)
        {
            case Success:
            case LastKnownLocation:
                if (GPStatus.LastKnownLocation == status)
                {
                    geolocation.stopRequest();
                    if (location == null)
                        sendResult(new String[]{"0", "Не удалось определить местоположение. Убедитесь что GPS на телефоне включён!"});
                    else
                    {
                        if (sendLoc == null || sendLoc.getStatus() == AsyncTask.Status.FINISHED)
                        {
                            sendLoc = new SendLoc(location.getLatitude(), location.getLongitude());
                            sendLoc.execute();
                        }
                    }
                    return;
                }

                L.info(String.format("latitude: %s; longitude: %s; location type: %s; fromMock: %s",
                    location.getLatitude(), location.getLongitude(), location.getProvider(), fromMock));

                if (!location.getProvider().equals("fused"))
                {
                    geolocation.stopRequest();
                    if (fromMock)
                        return;

                    if (sendLoc == null || sendLoc.getStatus() == AsyncTask.Status.FINISHED)
                    {
                        sendLoc = new SendLoc(location.getLatitude(), location.getLongitude());
                        sendLoc.execute();
                    }
                }
                break;
            case GpsProviderDisabled:
            case NetworkProviderDisabled:
                geolocation.stopRequest();
                sendResult(new String[]{"0", "На телефоне отключена геолокация"});
            break;
        }
    }

    public static NetworkInfo.State getNetworkState(ConnectivityManager connectivityManager)
    {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null ? networkInfo.getState() : NetworkInfo.State.UNKNOWN);
    }

    private void sendingIsWaitingOrders(boolean byAlarm)
    {
        if (sendOrds == null || sendOrds.getStatus() == AsyncTask.Status.FINISHED)
        {
            ArrayList<MadeOrderTemplate> sendingItems = AppDB.getInstance(this).getOrdersToSend();
            if (sendingItems != null)
                createSendingNotification(sendingItems, byAlarm);
        }
    }

    private void createSendingNotification(ArrayList<MadeOrderTemplate> sendingItems, boolean byAlarm)
    {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this)
            .setOngoing(false)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.app_ico)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notif_txt_transfer));

        sendOrds = new SendingOrders(sendingItems, byAlarm);
        sendOrds.execute();
    }

    private void sendResult(String[] result)
    {
        if (!TextUtils.isEmpty(tpCode))
        {
            Intent iResult = new Intent(C.ACTIONS.NOTIF_ABOUT_VISIT);
            iResult.putExtra(C.KEYS.EXTRA_DATA, result);
            sendBroadcast(iResult);
        }
        tpCode = "";
    }

    @SuppressLint("StaticFieldLeak")
    private class SendingOrders extends AsyncTask<Void, Integer, Void>
    {
        private ArrayList<MadeOrderTemplate> items;
        private boolean byAlarm = false;
        private int successSend = 0;
        private int notSend = 0;
        private SendOrderResponseTemplate response;

        SendingOrders(ArrayList<MadeOrderTemplate> items, boolean byAlarm)
        {
            this.items = items;
            this.byAlarm = byAlarm;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            builder.setProgress(this.items.size(), values[0], false);
            notificationManager.notify(1, builder.build());

            MadeOrderTemplate order = items.get(values[0] - 1);

            switch (response.getResponseCode())
            {
                // * successfully send
                case "1":
                    successSend++;
                    AppDB.getInstance(MainService.this).deleteOrder(order.getRowId());
                break;
                // * unknown exception
                case "-1":
                // * read msg response to known a reason
                case "0":
                // * not enough goods on warehouse
                case "2":
                    notSend++;
                    order.setNotEnoughGoods(response.getGoodsList());
                    order.setNotEnoughByBrand(response.getGoodsListByBrand());
                    order.setNotEnoughBySeries(response.getGoodsListBySeries());
                    order.setDescription(response.getMessage());
                    AppDB.getInstance(MainService.this).editOrder(order);
                break;
            }
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            for (int i = 0; items.size() > i; i++)
            {
                MadeOrderTemplate order = items.get(i);
                response = ReqSendOrder.send(order);
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            final boolean isHave  = AppDB.getInstance(MainService.this).isHaveToResendOrders();

            if (byAlarm && !isHave)
                SendOrdersByAlarm.alarmResendOrders(MainService.this, 5, false);

            if (!isHave)
            {
                builder.setContentText(getString(R.string.notif_txt_transfer_complete));
                LoadProductsAlarm.alarmReloadProducts(MainService.this, 2, true);
            }
            else
                builder.setContentText(getString(R.string.notif_txt_transfer_part, successSend + "",
                    items.size() + "", notSend + "", items.size() + ""));
            notificationManager.notify(1, builder.build());

            items.clear();
            response = null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SendLoc extends AsyncTask<Void, Void, String[]>
    {
        private String userCode;
        private double latitude;
        private double longitude;

        SendLoc(double latitude, double longitude)
        {
            userCode = PreferenceManager.getDefaultSharedPreferences(MainService.this)
                .getString(C.KEYS.USER_CODE, "0");
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String[] doInBackground(Void... params)
        {
            return ReqSendLoc.send(userCode, tpCode, latitude, longitude);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            sendResult(result);
        }
    }
}