package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 22.05.2017.
 * ----------------------------------
 */

public interface Orders
{
    String Table = "orders";
    /** Порядковый номер записи в таблице */
    String RowId = "rowid";
    /** тип заказа */
    String OrderType = "order_type";
    /** В кредит */
    String OnCredit = "on_credit";
    /** ид пользователя */
    String UserCode = "user_code";
    /** Колонка "Наименование торговой точки" */
    String TradingPoint = "trading_point";
    /** Колонка "Код торговой точки" */
    String TradingPointCode = "tp_code";
    /** Колонка "тип цены" */
    String PriceType = "price_type";
    /** Колонка "тип цены" */
    String PriceTypeName = "price_type_name";
    /** Колонка "Заказ" */
    String Title = "title";
    /** Колонка "1-ое фото" */
    String PhotoLink1 = "photo_link_1";
    /** Колонка "2-ое фото" */
    String PhotoLink2 = "photo_link_2";
    /** Колонка "3-ье фото" */
    String PhotoLink3 = "photo_link_3";
    /** Долгота */
    String Latitude = "latitude";
    /** Широта */
    String Longitude = "longitude";
    /** Дата выгрузки товара для отображения пользователю */
    String UploadDateView = "upload_date_view";
    /** Дата выгрузки товара для передачи на сервер */
    String UploadDateValue = "upload_date_value";
    /** Кому комментарий */
    String CommentTo = "comment_to";
    /** Комментарий */
    String Comment = "comment";
    /** Общий вес заказаных товаров */
    String Weight = "weight";
    /** Общий объем заказанных товаров */
    String Capacity = "capacity";
    /** Общая стоимость по заказу */
    String TotalPrice = "total_price";
    /** Колонка "Статус заказа" */
    String Status = "status";
    /** Колонка "Дата записи" */
    String CreatedDate = "created_date";
    /** Описание какой-либо ошибки при отправке заказа*/
    String Description = "desc";

    // +1.2.2
    String CodeWarehouse = "code_warehouse";
    String CodeOrg = "code_org";

    //
    String CodeContract = "code_contract";

}