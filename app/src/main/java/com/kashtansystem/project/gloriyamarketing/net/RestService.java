package com.kashtansystem.project.gloriyamarketing.net;

import com.kashtansystem.project.gloriyamarketing.net.models.DirectionResponse;
import com.kashtansystem.project.gloriyamarketing.net.models.GeocoderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by FlameKaf on 23.05.2017.
 * ----------------------------------
 */

public interface RestService
{
    /**
     * @param address адрес
     * @param apiKey сгенерированый ключ
     * */
    @GET("/maps/api/geocode/json")
    Call<GeocoderResponse> getGeocoding(@Query("address") String address, @Query("key") String apiKey);

    /**
     * @param origin начальная позиция пользователя. может быть адресом, координатами(долгота/ширина)
     *               либо индетификатором места.
     * @param destination точка назанчения. может быть адресом, координатами(долгото/ширина) либо
     *                    индетификатором места.
     * @param  units систем единиц. указание "metric" возвращает рассчитанное расстояние в киллометрах/метрах
     *               указание "imperial" возвращает рассчитанное расстояние в милях/футах
     * @param lang язык, в котором вернётся ответ за запрос
     * @param altDirections необходимо ли получить альтернативные пути
     * @param apiKey сгенерированый ключ
     * */
    @GET("/maps/api/directions/json")
    Call<DirectionResponse> getDirection(@Query("origin") String origin, @Query("destination") String destination,
        @Query("units") String units, @Query("key") String apiKey, @Query("language") String lang,
        @Query("alternatives") String altDirections);
}