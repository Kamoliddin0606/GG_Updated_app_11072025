package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 01.10.2017.
 * ----------------------------------
 * Таблица с товарами, которые выдаются в подарок
 */

public interface OrdersGiftList
{
    String Table = "orders_gifts";
    /** Ид заказа */
    String OrderId = "order_id";
    /** Код продукта, за который выдаётся подарок */
    String ForProduct = "for_product";
    /** Код товара */
    String ProductCode = "product_code";
    /** Наименование товара */
    String ProductName = "product_name";
    /** Цена товара, всегда содержит 0 */
    String Price = "price";
    /** Скидка, всегда содержит 100 */
    String Discount = "discount";
    /** Кол-во товара, которое выдаётся в подарок, при превышения мин.лимита */
    String Count = "count";
    /** Кол-во товара, которое выдадут в подарок */
    String Amount = "amount";
    /** Кол-во товара, которое выдадут в подарок */
    String Weight = "weight";
    /** Кол-во товара, которое выдадут в подарок */
    String Capacity = "capacity";
    /** Кол-во товара, которое выдадут в подарок */
    String MinAmount = "min_amount";
    /** Кол-во товара, которое выдадут в подарок */
    String AmountBySeries = "amount_by_series";
}