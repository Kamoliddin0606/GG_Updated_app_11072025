package com.kashtansystem.project.gloriyamarketing.activity.forwarder;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.models.template.DeliveryTemplate;
import com.kashtansystem.project.gloriyamarketing.net.RestService;
import com.kashtansystem.project.gloriyamarketing.net.models.DirectionResponse;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.GPS;
import com.kashtansystem.project.gloriyamarketing.utils.GPStatus;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by FlameKaf on 20.06.2017.
 * ----------------------------------
 */

public class DirectionActivity extends BaseActivity implements OnMapReadyCallback,
    GPS.OnCoordinatsReceiveListener, View.OnClickListener
{
    private DeliveryTemplate delivery;

    private GoogleMap googleMap;
    private GPS gps;
    private Marker userMarker;
    private Circle circle;
    private LatLng endPoint;
    private boolean zoomCam = true;

    @Override
    public String getActionBarTitle()
    {
        return delivery.getTpName();
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direction);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        delivery = DeliveryListActivity.filteredDeliveryList.get(getIntent().getIntExtra(C.KEYS.EXTRA_DATA_ID, 0));

        gps = new GPS(this, this);
        gps.setInterval(1000);

        // Obtain the SupportMapFragment and get notified when the navigation is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
            .findFragmentById(R.id.app_map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.fabDirection).setOnClickListener(this);
        findViewById(R.id.fabDMyLocation).setOnClickListener(this);

        ((TextView)findViewById(R.id.tvDirectionInfo)).setText(String.format("Адрес: %s\nОриентир: %s",
            delivery.getAddress(), delivery.getRefPoint()));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            goBack();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fabDMyLocation:
                if (googleMap != null)
                {
                    if (gps.isStartedRequest())
                        gps.stopRequest();
                    gps.startRequest();
                }
            break;
            case R.id.fabDirection:
                zoomCam = false;
                findViewById(R.id.appProgressBar).setVisibility(View.VISIBLE);
                requestDirection((delivery.getLatitude() != 0 ? String.format("%s,%s",
                    delivery.getLatitude(), delivery.getLongitude()) : delivery.getAddress()));
            break;
        }
    }

    @Override
    public void onBackPressed()
    {
        goBack();
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

                setUserMarker(new LatLng(location.getLatitude(), location.getLongitude()),
                    location.getAccuracy());

                if (endPoint != null)
                {
                    Location tempLoc = new Location(LocationManager.GPS_PROVIDER);
                    tempLoc.setLatitude(endPoint.latitude);
                    tempLoc.setLongitude(endPoint.longitude);

                    float distance = tempLoc.distanceTo(location);
                    if (distance <= 2)
                        gps.stopRequest();
                }
            break;
            case GpsProviderDisabled:
            case NetworkProviderDisabled:
                isGPStatusOK();
            break;
        }
    }

    private void goBack()
    {
        finish();
    }

    /**
     * Метод выполняет http(s) запрос в сервис гугла (direction api) на получение
     * данных по маршруту от пользователя до торговой точки (основной и альтернативные пути).
     * @param origin параметр, состоящий из координат торговой точки либо её адреса.
     * */
    private void requestDirection(String origin)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.url_to_google_api))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofit.create(RestService.class)
                .getDirection(String.format("%s,%s", userMarker.getPosition().latitude, userMarker.getPosition().longitude),
                    origin, "metric", getString(R.string.direction_key), "ru", "false")
                .enqueue(new retrofit2.Callback<DirectionResponse>()
                {
                    @Override
                    public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response)
                    {
                        if (response.isSuccessful())
                        {
                            DirectionResponse directResp = response.body();
                            if (directResp != null)
                            {
                                if (directResp.getStatus().equals("OK"))
                                {
                                    DirectionResponse.Routes.Legs legs = directResp.getRoutes().get(0).getLegs().get(0);
                                    setTradingPointMarker(new LatLng(legs.getEndLat(), legs.getEndLng()),
                                            true, delivery.getTpName(), delivery.getAddress());
                                    drawDirection(directResp.getRoutes());
                                }
                                else
                                {
                                    Toast.makeText(DirectionActivity.this, directResp.getStatus(), Toast.LENGTH_LONG).show();
                                    L.exception(String.format("status: %s;\nerror.: %s;", directResp.getStatus(), directResp.getErrorMessage()));
                                }
                            }
                        }
                        findViewById(R.id.appProgressBar).setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<DirectionResponse> call, Throwable t)
                    {
                        L.exception(t.getMessage());
                        Toast.makeText(DirectionActivity.this, (t.getMessage() != null ? t.getMessage() : "Exception unknown"), Toast.LENGTH_LONG).show();
                        findViewById(R.id.appProgressBar).setVisibility(View.INVISIBLE);
                    }
                });
    }

    /**
     * Устанавливает маркер пользователя на карте
     * @param latLng координаты пользователя
     * */
    private void setUserMarker(LatLng latLng, float accuracy)
    {
        if (userMarker == null)
        {
            userMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
            userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            userMarker.setTitle("You are here");
        }
        else
            userMarker.setPosition(latLng);

        if (circle == null)
        {
            circle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(accuracy)
                .strokeWidth(2)
                .strokeColor(getResources().getColor(R.color.colorPrimaryDark))
                .fillColor(Color.parseColor("#50387ea4"))
                .clickable(false));
        }
        else
        {
            circle.setCenter(latLng);
            circle.setRadius(accuracy);
        }

        if (zoomCam)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                .zoom(16.2f).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * Устанавливает маркер торговой точки на карте
     * @param latLng координаты торговой точки
     * @param zoomCam если "true" увеличивает зум к переданным координатам
     * @param title заголовок маркера
     * @param address доп.информация, высвечивающаяся при нажатии на маркер.
     * */
    private void setTradingPointMarker(LatLng latLng, boolean zoomCam, String title, String address)
    {
        Marker tpMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
        tpMarker.setTitle(title);
        tpMarker.setSnippet(address);

        if (zoomCam)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(13).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /**
     * Отрисовка пути от пользователя до пункта назначения
     * @param routes полученные координаты маршрутов
     * */
    private void drawDirection(List<DirectionResponse.Routes> routes)
    {
        googleMap.clear();

        userMarker = googleMap.addMarker(new MarkerOptions().position(userMarker.getPosition()));
        userMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        userMarker.setTitle("You are here");

        circle = googleMap.addCircle(new CircleOptions()
            .center(userMarker.getPosition())
            .strokeWidth(2)
            .strokeColor(getResources().getColor(R.color.colorPrimaryDark))
            .fillColor(Color.parseColor("#50387ea4"))
            .clickable(false));

        PolylineOptions mainPolylineOpt = null;

        for (DirectionResponse.Routes rts: routes)
        {
            if (mainPolylineOpt == null)
            {
                mainPolylineOpt = new PolylineOptions()
                    .width(7).geodesic(true).color(Color.BLUE)
                    .startCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.mipmap.ic_cap_start)))
                    .endCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.mipmap.ic_cap_end)));

                //setDirectionInfo(true, rts.getLegs().get(0).getDistance(), rts.getLegs().get(0).getDuration());

                for (DirectionResponse.Routes.Legs.Step step: rts.getLegs().get(0).getSteps())
                {
                    //mainPolylineOpt.add(new LatLng(step.getEndLat(), step.getEndLng()));
                    List<LatLng> smoothPathDrw = PolyUtil.decode(step.getPoints());
                    if (!smoothPathDrw.isEmpty())
                    {
                        mainPolylineOpt.addAll(smoothPathDrw);
                        endPoint = smoothPathDrw.get(smoothPathDrw.size() - 1);
                    }
                }
            }
            else
            {
                //setDirectionInfo(false, rts.getLegs().get(0).getDistance(), rts.getLegs().get(0).getDuration());

                PolylineOptions polylineOptions = new PolylineOptions().width(7).color(Color.GRAY);
                for (DirectionResponse.Routes.Legs.Step step : rts.getLegs().get(0).getSteps())
                {
                    //polylineOptions.add(new LatLng(step.getEndLat(), step.getEndLng()));
                    List<LatLng> smoothPathDrw = PolyUtil.decode(step.getPoints());
                    if (!smoothPathDrw.isEmpty())
                        polylineOptions.addAll(smoothPathDrw);
                }
                googleMap.addPolyline(polylineOptions);
            }
        }
        googleMap.addPolyline(mainPolylineOpt);
    }

    /*
     * Выводит доп.информация по маршрутам
     * @param main если <b>true</b> выводит информацию по главному маршруту, в противном случае для
     *             альтернативных маршрутов.
     * @param distance расстояние
     * @param duration время пути
     */
    /*private void setDirectionInfo(boolean main, String distance, String duration)
    {
        if (main)
            ((TextView)findViewById(R.id.mapMainDirection)).setText(String.format("%s; %s",
                    distance, duration));
        else
        {
            TextView textView = (TextView)findViewById(R.id.mapAlternativeDirection);
            textView.setText(String.format("%s%s; %s\n", textView.getText(), distance, duration));
        }
    }*/
}