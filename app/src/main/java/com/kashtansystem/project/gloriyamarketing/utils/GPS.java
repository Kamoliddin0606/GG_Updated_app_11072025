package com.kashtansystem.project.gloriyamarketing.utils;

import android.annotation.SuppressLint;
import android.location.Criteria;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

public class GPS implements LocationListener
{
    public interface OnCoordinatsReceiveListener
    {
        void onReceiveCoordinats(GPStatus status, Location location, boolean fromMock);
    }

    private boolean start = false;
    private long interval = 1000;

    private OnCoordinatsReceiveListener listener;
    private LocationManager locationMananger;
    private Criteria criteria;
    private Context context;

    public GPS(Context context)
    {
        locationMananger = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
        Init();
    }

    public GPS(Context context, OnCoordinatsReceiveListener listener)
    {
        locationMananger = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        this.listener = listener;
        this.context = context;
        Init();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        listener.onReceiveCoordinats(GPStatus.Success, location, fromMock(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onProviderDisabled(String provider)
    {
        Location location = null;
        try
        {
            location = locationMananger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location == null)
                location = locationMananger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        catch(IllegalArgumentException ex)
        {
            L.exception(ex);
        }

        listener.onReceiveCoordinats(GPStatus.LastKnownLocation, location, fromMock(location));
    }

    /**
     * @param interval интервал во времени между запросами координат в миллесекундах
     * */
    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    public boolean isProviderEnabled()
    {
        return (locationMananger.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            locationMananger.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    @SuppressLint("MissingPermission")
    public void startRequest()
    {
        if (!locationMananger.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            listener.onReceiveCoordinats(GPStatus.GpsProviderDisabled, null, false);
            return;
        }
        else if (!locationMananger.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            listener.onReceiveCoordinats(GPStatus.NetworkProviderDisabled, null, false);
            return;
        }

        if (!start)
        {
            locationMananger.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 1, this);
            locationMananger.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 1, this);
            locationMananger.requestLocationUpdates(interval, 1, criteria, this, null);
            start = true;
        }
    }

    public void stopRequest()
    {
        if (start)
        {
            locationMananger.removeUpdates(this);
            start = false;
        }
    }

    public boolean isStartedRequest()
    {
        return start;
    }

    private void Init()
    {
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
    }

    private boolean fromMock(Location location)
    {
        try
        {
            //noinspection deprecation
            return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ?
                location.isFromMockProvider() : Util.IntToBool(Settings.Secure.getInt(this.context
                    .getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION)));
        }
        catch (Exception ex)
        {
            L.exception(ex);
        }
        return false;
    }
}