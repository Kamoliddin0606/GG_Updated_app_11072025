package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 11.07.2017.
 * ----------------------------------
 */

public interface OrdersCompetitorScouting
{
    String Table = "orders_competitor_scouting";
    /** ид заказа */
    String OrderId = "order_id";
    /** Наименование конкурента */
    String Name = "name";
    /** Товар конкурента */
    String Goods = "goods";
    /** Цена товара конкурента */
    String Price = "price";
}