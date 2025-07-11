package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Таблица причин отказов
 */

public interface Refusals
{
    String Table = "refusals";
    /** Код причины отказа */
    String Code = "code";
    /** Текст причины отказа */
    String Name = "name";
    /** Дата обновления/создания */
    //String UpdatedDate = "updated_date";
}