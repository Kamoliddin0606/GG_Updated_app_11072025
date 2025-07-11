package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 26.09.2017.
 * ----------------------------------
 * Таблица товаров, которые выдаются в подарок
 */

public interface GiftList
{
    String Table = "gift_list";
    /** код события SpecEvent */
    String EventCode = "event_code";
    /** код товара, который выдаётся в подарок */
    String ProductCode = "product_code";
    /** наименование товара */
    String ProductName = "product_name";
    /** количество выдаваемого товара */
    String Amount = "amount";
    /** Мин.кол-во товара, за которое выдаётся подарок */
    String MinAmount = "min_amount";
}