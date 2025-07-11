package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 08.07.2017.
 * ----------------------------------
 */

public interface OrdersGoods
{
    String Table = "orders_goods";
    /** ид заказа */
    String OrderId = "order_id";
    /** Код склада */
    String WarehouseCode = "warehouse_code";
    /** Код товара */
    String ProductCode = "product_code";
    /** Наименование товара */
    String ProductName = "product_name";
    /** Количество товара */
    String Amount = "amount";
    /** Доступно товара */
    String Available = "available";
    /** Цена товара */
    String Price = "price";
    /** Общая стоимость */
    String Total = "total";
    /** Общий вес товара */
    String Weight = "weight";
    /** Общий объём товара */
    String Capacity = "capacity";
    /** Наименование брэнда */
    String Brand = "brand";
    /** Серия/Группа товара */
    String Series = "series";
    /** мин.кол-во Amount, за которое выдаётся подарок */
    String MinAmount = "min_amount";
    /** Изначальная стоимость товара */
    String OriginalPrice = "original_price";
    /** Значение скидки 0,1...100 */
    String DiscountValue = "discount_value";
    // Подарок по акции - количество
    String GiftAmount = "gift_amount";
}