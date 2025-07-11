package com.kashtansystem.project.gloriyamarketing.database.tables;

/**
 * Created by FlameKaf on 25.07.2017.
 * ----------------------------------
 * Таблица контрагентов
 */

public interface Clients
{
    String Table = "clients";
    /** Код клиента. При создании нового клиента, присылается с сервера */
    String Code = "code";
    /** Юр.название клиента */
    String Name = "name";
    /** Вывеска */
    String Signboard = "signboard";
    /** */
    String Inn = "inn";
    /** Тип клиента */
    String TradePointType = "trade_point_type";
    /** Контактное лицо */
    String ContactPerson = "contact_person";
    /** Номер телефона контактного лица */
    String ContactPersonPhone = "contact_person_phone";
    /** Юр.адрес */
    String LegalAddress = "legal_address";
    /** Адрес доставки */
    String DeliveryAddress = "delivery_address";
    /** Ориентир */
    String ReferencePoint = "reference_point";
    /** Номер телефона ответственного лица */
    String ResponsiblePersonPhone = "responsible_person_phone";
    /** Долгота */
    String Latitude = "latitude";
    /** Ширина */
    String Longitude = "longitude";
    /** Лимит кредита */
    String CreditLimit = "credit_limit";
    /** Накопленный кредит */
    String AccumulatedCredit = "accumulated_credit";
    /** Код пользователя. Несёт в себе только информативную часть, о том, кто создал инфу по клиенту */
    String UserCode = "user_code";
    /** Дата обновления/создания */
    String UpdatedDate = "updated_date";
}