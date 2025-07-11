package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 08.07.2017.
 * ----------------------------------
 */

public interface PriceList
{
    String Table = "price_list";
    /** код типа цены */
    String Code = "code";
    /** Код товара */
    String ProductCode = "product_code";
    /** Цена */
    String Price = "price";
    /** Новая цена товара, заполняется когда появляется скидка */
    String NewPrice = "new_price";
    /** Скидка */
    String Discount = "discount";
    /** Дата обновления */
    String UpdatedDate = "updated_date";
}