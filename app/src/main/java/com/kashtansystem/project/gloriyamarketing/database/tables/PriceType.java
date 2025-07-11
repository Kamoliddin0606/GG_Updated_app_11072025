package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 08.07.2017.
 * ----------------------------------
 */

public interface PriceType
{
    String Table = "price_type";
    /** Код цены */
    String Code = "code";
    /** Наименование цены */
    String Name = "name";
    /** Дата создания записи */
    String CreatedDate = "created_date";
}