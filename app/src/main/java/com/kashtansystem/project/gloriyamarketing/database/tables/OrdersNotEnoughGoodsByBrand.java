package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 23.07.2017.
 * ----------------------------------
 */

public interface OrdersNotEnoughGoodsByBrand
{
    String Table = "orders_not_enough_goods_by_brand";
    /** ид заказа на устройстве */
    String OrderId = "order_id";
    /** наименование брэнда товара */
    String Brand = "brand";
}