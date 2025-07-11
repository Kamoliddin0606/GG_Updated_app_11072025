package com.kashtansystem.project.gloriyamarketing.models.listener;

/**
 * Created by FlameKaf on 31.07.2017.
 * ----------------------------------
 */

public interface OnDateSelectedListener
{
    /**
     * События, срабатываемое когда пользователь выбирает дату доставки груза
     * @param view "dd.mm.yyyy" - читаемое значение, выводящееся пользователю
     * @param values "yyyyddmm" - значение отправляемое на сервер
     * */
    void onDateSelected(String view, String values);
}