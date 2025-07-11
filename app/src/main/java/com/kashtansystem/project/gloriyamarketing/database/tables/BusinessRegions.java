package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 25.07.2017.
 * ----------------------------------
 * Таблица бизнес регионов
 */

public interface BusinessRegions
{
    String Table = "clients_business_regions";
    /** Код клиента. При создании нового клиента, присылается с сервера */
    String Code = "code";
    /** Юр.название клиента */
    String Name = "name";
    /** Дата обновления/создания */
    String UpdatedDate = "updated_date";
}