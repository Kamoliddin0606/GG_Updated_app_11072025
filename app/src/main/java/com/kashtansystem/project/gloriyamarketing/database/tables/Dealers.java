package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 25.07.2017.
 * ----------------------------------
 * Таблица дилеров
 */

public interface Dealers
{
    String Table = "dealers";
    /** Код дилера*/
    String Code = "code";
    /** Наименование дилера */
    String Name = "name";
    /** Дата обновления/создания */
    String UpdatedDate = "updated_date";
}