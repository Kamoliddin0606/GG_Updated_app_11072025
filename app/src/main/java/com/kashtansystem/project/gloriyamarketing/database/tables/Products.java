package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 08.07.2017.
 * ----------------------------------
 */

public interface Products
{
    String Table = "products";
    /** Группа товаров */
    String GroupName = "product_group_name";
    /** Серия продукта */
    String Series = "series";
    /** Код склада */
    String WarehouseCode = "warehouse_code";
    /** Код продукции (товара) */
    String ProductCode = "product_code";
    /** Наименование товара */
    String ProductName = "product_name";
    /** В наличии (количество) */
    String Have = "have";
    /** В резерве (количество) */
    String Reserved = "reserved";
    /** Доступно (количество) */
    String Available = "avaible";
    /** Вес товара */
    String Weight = "weight";
    /** Объём товара */
    String Capacity = "capacity";
    /** Мин. кол-во товара, за которое выдаётся подарок */
    String MinAmount = "min_amount";
    /** Код акции, связывающий с таблице GiftList */
    String EventCode = "event_code";
    // Артикул товара
    String VendorCode = "vendor_code";
}