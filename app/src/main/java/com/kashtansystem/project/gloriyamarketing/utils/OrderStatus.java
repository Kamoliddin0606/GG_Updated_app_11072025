package com.kashtansystem.project.gloriyamarketing.utils;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.R;

/**
 * Created by FlameKaf on 22.05.2017.
 * ----------------------------------
 */

public enum OrderStatus
{
    /** * Новый заказ * */
    NewOrder(299),
    /**
     *  Заказ временно сохранён в бд приложения.
     *  Данный заказ можно отредактировать и отправить на сервер либо повторно сохранить в бд приложения.
     */
    SavedLocal(300),
    /**
     * Заказ не был отослан на сервер, по причине отсутствия интернет соединения на устройстве,
     * следовательно он был сохранён в бд приложения и будет отослан автоматом, когда связь восстановится.
     * */
    NeedToSend(301),
    /** * Заказ отправлен на сервер * */
    Sent(302),
    /** * На складе не хватает товаров * */
    NotEnoughGoods(303),
    UnknownStatus(666),
    /** * к отгрузке * */
	ForShipment(1),
	/** * доставлен * */
	Delivered(2),
	/** * оплачен * */
	Paid(3),
	/** * закрыт * */
	Closed(4),
	/** * отменён * */
	Canceled(5),
	/** * в пути * */
	OnTheWay(6);
    //Complete(4),
    /* На соглосовании */
    //OnAgreement(1),
    /* К обеспечению/в резерве */
    //ToProvide(2),
    /* К отгрузке */
    //ForShipment(3),
    /* необходимо завершить отгрузку */
    //NeedToDelivery(0);

    private final int value;

    OrderStatus(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static OrderStatus getOrderStatusByValue(int value)
    {
        switch (value)
        {
            case 1:
                return ForShipment;
            case 2:
                return Delivered;
            case 3:
                return Paid;
            case 4:
                return Closed;
            case 5:
                return Canceled;
            case 6:
                return OnTheWay;
            case 300:
                return SavedLocal;
            case 302:
                return Sent;
            case 301:
            default:
                return UnknownStatus;
        }
    }

    public static String getOrderStatusNameByValue(Context ctx, OrderStatus value)
    {
        switch (value)
        {
            case ForShipment:
                return ctx.getString(R.string.order_status_for_shipment);
            case Delivered:
                return ctx.getString(R.string.order_status_delivered);
            case Paid:
                return ctx.getString(R.string.order_status_paid);
            case Closed:
                return ctx.getString(R.string.order_status_complete);
            case Canceled:
                return ctx.getString(R.string.order_status_cancel_delivery);
            case OnTheWay:
                return ctx.getString(R.string.order_status_on_the_way);
            case SavedLocal:
                return ctx.getString(R.string.order_status_saved_locale);
            case Sent:
                return ctx.getString(R.string.order_status_sent);
            case NeedToSend:
                return ctx.getString(R.string.order_status_wait_to_send);
            default:
                return ctx.getString(R.string.order_status_unknown);
        }
    }
}