package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 23.07.2017.
 * ----------------------------------
 */

public interface OrdersNotEnoughGoods
{
    String Table = "orders_not_enough_goods";
    /** ид заказа */
    String OrderId = "order_id";
    /** код товара */
    String ProductCode = "product_code";
    /** */
    String Available = "available";
}