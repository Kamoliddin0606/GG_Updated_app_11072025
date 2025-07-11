package com.kashtansystem.project.gloriyamarketing.net.models;

import java.util.List;

/**
 * Created by FlameKaf on 23.05.2017.
 * ----------------------------------
 * Шаблон ответеа на запрос получения координат по адресу из гугл-сервиса.
 * Ограничения: не большее 2500 запросов в день.
 */

public class GeocoderResponse
{
    public class GeoData
    {
        private class GeoMetry
        {
            private class LatLng
            {
                public String lat = "";
                public String lng = "";
            }

            private LatLng location;

            public String getLat()
            {
                return location.lat;
            }

            public String getLng()
            {
                return location.lng;
            }
        }

        private GeoMetry geometry;

        public double getLatitude()
        {
            return Double.parseDouble(geometry.getLat());
        }

        public double getLongitude()
        {
            return Double.parseDouble(geometry.getLng());
        }
    }

    private String status = "";
    private String error_message = "";
    private List<GeoData> results;

    public String getStatus()
    {
        return status;
    }

    public String getErroMessage()
    {
        return error_message;
    }

    public List<GeoData> getGeoData()
    {
        return  results;
    }
}