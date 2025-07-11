package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.GPS;
import com.kashtansystem.project.gloriyamarketing.utils.GPStatus;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import java.io.IOException;
import java.util.List;

/**
 * Created by FlameKaf on 22.05.2017.
 * ----------------------------------
 * Карта google.
 * Функционал:
 * 1. Отображение маркера торговой точки, пользователя и прорисовка пути от пользователя до т.т.
 *    Путь рисуется посредством использования google direction api.
 * 2. Отображение маркета экспедитора и торговой точки, к которой он направляется.
 * 3. Вручную установка маркера торговой точки (когда создаётся новая торговая точка)
 */

public class NavigationActivity extends BaseActivity implements OnMapReadyCallback,
    GPS.OnCoordinatsReceiveListener, GoogleMap.OnMapClickListener, View.OnClickListener
{
    private GoogleMap googleMap;
    private Marker userMarker;
    private GPS gps;

    @Override
    public String getActionBarTitle()
    {
        return "";
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);

        gps = new GPS(this, this);
        gps.setInterval(1000);

        findViewById(R.id.btnRefresh).setOnClickListener(this);
        findViewById(R.id.btnAcceptAddress).setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the navigation is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
            .findFragmentById(R.id.app_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (googleMap != null)
            gps.startRequest();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        gps.stopRequest();
    }

    /**
     * Manipulates the navigation once available.
     * This callback is triggered when the navigation is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the activity_agent will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the activity_agent has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(false);
        geoLocationPermission();
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        setUserMarker(latLng, true);
        new RequestAddress().execute(latLng);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnRefresh:
                if (googleMap != null)
                {
                    if (userMarker != null)
                        new RequestAddress().execute(userMarker.getPosition());
                    else
                        gps.startRequest();
                }
            break;
            case R.id.btnAcceptAddress:
                if (userMarker != null)
                {
                    Intent intent = new Intent();
                    intent.putExtra(C.KEYS.EXTRA_DATA, findViewById(R.id.mapTvAddress).getTag().toString());
                    intent.putExtra(C.KEYS.EXTRA_LOC, userMarker.getPosition());
                    setResult(RESULT_OK, intent);
                }
                finish();
            break;
        }
    }

    @Override
    public void onPermissionGrantResult(AppPermissions permission, boolean result)
    {
        if (permission == AppPermissions.GeoLocation && result)
            gps.startRequest();
    }

    @Override
    public void onReceiveCoordinats(GPStatus status, Location location, boolean fromMock)
    {
        switch (status)
        {
            case Success:
            case LastKnownLocation:
                if ((GPStatus.LastKnownLocation == status) && (location == null))
                {
                    gps.stopRequest();
                    Toast.makeText(this, "Не удалось определить местоположение. Убедитесь что GPS на телефоне включён!",
                        Toast.LENGTH_LONG).show();
                    return;
                }

                L.info(String.format("latitude: %s; longitude: %s; location type: %s; fromMock: %s",
                    location.getLatitude(), location.getLongitude(), location.getProvider(), fromMock));

                if (!location.getProvider().equals("fused"))
                {
                    gps.stopRequest();
                    if (fromMock)
                    {
                        BaseActivity.DialogBox(this, getString(R.string.dialog_text_mock_is_using),
                            getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_ok), null, null);
                        return;
                    }
                    setUserMarker(new LatLng(location.getLatitude(), location.getLongitude()), true);
                    new RequestAddress().execute(userMarker.getPosition());
                }
            break;
            case GpsProviderDisabled:
            case NetworkProviderDisabled:
                isGPStatusOK();
            break;
        }
    }

    /**
     * Запрос адерса по маркеру, установленному на карте
     * */
    private class RequestAddress extends AsyncTask<LatLng, Void, String[]>
    {
        @Override
        public void onPreExecute()
        {
            findViewById(R.id.mapProgress).setVisibility(View.VISIBLE);
            googleMap.setOnMapClickListener(null);
        }

        @Override
        public String[] doInBackground(LatLng... params)
        {
            Geocoder geocoder = new Geocoder(NavigationActivity.this);
            List<Address> addressList;
            try
            {
                addressList = geocoder.getFromLocation(params[0].latitude, params[0].longitude, 1);
                if (addressList != null && addressList.size() > 0)
                {
                    Address address = addressList.get(0);
                    return new String[] {"success", String.format("%s, %s, %s", address.getCountryName(),
                        address.getLocality(), address.getFeatureName())};
                }

            } catch (IOException e)
            {
                L.exception(e);
            }
            return new String[] {"fail", "Не удалось определить адрес по местоположению!"};
        }

        @Override
        public void onPostExecute(String[] result)
        {
            findViewById(R.id.mapProgress).setVisibility(View.INVISIBLE);
            TextView address =  (TextView)findViewById(R.id.mapTvAddress);
            address.setTag((result[0].equals("success") ? result[1] : ""));
            address.setText(result[1]);
            googleMap.setOnMapClickListener(NavigationActivity.this);
        }
    }

    /**
     * Устанавливает маркер пользователя на карте
     * @param latLng координаты пользователя
     * @param zoomCam если "true" увеличивает зум к переданным координатам
     * */
    private void setUserMarker(LatLng latLng, boolean zoomCam)
    {
        if (userMarker == null)
        {
            userMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
            userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            userMarker.setTitle("You are here");
        }
        else
            userMarker.setPosition(latLng);

        if (zoomCam)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                .zoom(17.5f).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}