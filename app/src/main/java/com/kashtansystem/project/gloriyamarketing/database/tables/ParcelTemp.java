package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 25.07.2017.
 * ----------------------------------
 * Таблица временно хранения посылки
 */

public interface ParcelTemp
{
    String Table = "parcel_temp";
    /** Порядковый номер записи в таблице */
    String RowId = "rowid";
    /** Код бизнес региона */
    String BrCode = "br_code";
    /** Наименование бизнес региона */
    String BrName = "br_name";
    /** Комментарий */
    String Comment = "comment";
    /** Тип валюты */
    String CurrencyType = "currency_type";
    /** Денежная сумма */
    String Summa = "summa";
    /** Курс валюты */
    String Rate = "rate";
}