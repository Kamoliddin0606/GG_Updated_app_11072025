package com.kashtansystem.project.gloriyamarketing.net.models;

import java.util.List;

/**
 * Created by FlameKaf on 25.05.2017.
 * ----------------------------------
 * Template of response directions from google service
 * Limitation for 2500 requests per day
 */

public class DirectionResponse
{
    public class Routes
    {
        public class Legs
        {
            /**
             * рассчитанная дистанция от точки "А" до "В"
             * */
            private class Distance
            {
                private String text = "";
                private int value = 0;
            }
            /**
             * рассчитанное время пути от "А" до "В"
             * */
            private class Duration
            {
                private String text = "";
                private int value = 0;
            }
            /**
             * Место назначения. Координаты торговой точки.
             * */
            private class EndLocation
            {
                private double lat;
                private double lng;
            }
            /**
             * Промежуточные координаты от "А" до "В"
             * */
            public class Step
            {
                private class Polyline
                {
                    private String points = "";

                    public String getPoints()
                    {
                        return points;
                    }
                }

                private Distance distance;
                private Duration duration;
                private EndLocation end_location;
                private Polyline polyline;

                public String getDistance()
                {
                    return distance.text;
                }

                public String getDuration()
                {
                    return duration.text;
                }

                public double getEndLat()
                {
                    return end_location.lat;
                }

                public double getEndLng()
                {
                    return end_location.lng;
                }

                public String getPoints()
                {
                    return polyline.getPoints();
                }
            }

            private Distance distance;
            private Duration duration;
            private EndLocation end_location;
            private List<Step> steps;

            /**
             * @return  дистанцию от точки "А" до "В"
             * */
            public String getDistance()
            {
                return distance.text;
            }
            /**
             * @return  время пути от "А" до "В"
             * */
            public String getDuration()
            {
                return duration.text;
            }
            /**
             * @return Конечну долготу т.т.
             * */
            public double getEndLat()
            {
                return end_location.lat;
            }
            /**
             * @return Конечну ширину т.т.
             * */
            public double getEndLng()
            {
                return end_location.lng;
            }
            /**
             * @return Список промежуточных точке между точкой "А" и "В"
             * */
            public List<Step> getSteps()
            {
                return steps;
            }
        }

        private List<Legs> legs;

        public List<Legs> getLegs()
        {
            return legs;
        }
    }

    private String status = "";
    private String error_message = "";
    private List<Routes> routes;

    public String getStatus()
    {
        return status;
    }

    public String getErrorMessage()
    {
        return error_message;
    }

    public List<Routes> getRoutes()
    {
        return routes;
    }
}
