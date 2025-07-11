package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by FlameKaf on 21.07.2017.
 * ----------------------------------
 * Макет ответа на отправку заказа на сервер.
 */

public class SendOrderResponseTemplate
{
    // * Порядковый номер заказа
    private int orderId = 0;
    // * Код заказа
    private String orderCode = "";
    // * Название заказа в приложении (наименование страницы)
    private String orderTitle = "";
    // * Сведения в какой момент выполнялось действие (создание/редактирование)
    //private String info = "";
    // * Код ответа
    private String responseCode = "1";
    // * Детальное сообщение
    private String message = "";
    // * Список товаров, которых не хватает на складе (key - код товара; value - кол.товара на складе)
    private Map<String, String> goodsList = null;
    // *
    private Set<String> goodsListByBrand = null;
    private Set<String> goodsListBySeries = null;

    /**
     * @return Код сообщения
     * */
    public String getResponseCode()
    {
        return responseCode;
    }

    /**
     * @param responseCode Код сообщения
     * */
    public void setResponseCode(String responseCode)
    {
        this.responseCode = responseCode;
    }

    /**
     * @return Детальное сообщение
     * */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message Детальное сообщение
     * */
    public void setMessage(String message)
    {
        this.message = message;
    }

    /**
     * @return Список товаров, которых не хватает на складе
     * */
    public Map<String, String> getGoodsList()
    {
        if (goodsList == null)
            return new HashMap<>(1);
        return goodsList;
    }

    /**
     * @param goodsList Список товаров, которых не хватает на складе
     * */
    public void setGoodsList(Map<String, String> goodsList)
    {
        if (goodsList != null)
        {
            this.goodsList = new HashMap<>(goodsList.size());
            this.goodsList.putAll(goodsList);
        }
        else
            this.goodsList = new HashMap<>(1);
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    /*public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }*/

    public String getOrderTitle()
    {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle)
    {
        this.orderTitle = orderTitle;
    }

    public Set<String> getGoodsListByBrand()
    {
        if (goodsListByBrand == null)
            return new LinkedHashSet<>(1);
        return goodsListByBrand;
    }

    public void setGoodsListByBrand(Set<String> goodsListByGrand)
    {
        if (goodsListByGrand != null)
        {
            this.goodsListByBrand = new LinkedHashSet<>(goodsListByGrand.size());
            this.goodsListByBrand.addAll(goodsListByGrand);
        }
        else
            this.goodsListByBrand = new LinkedHashSet<>(1);
    }

    public Set<String> getGoodsListBySeries()
    {
        if (goodsListBySeries == null)
            return new LinkedHashSet<>(1);
        return goodsListBySeries;
    }

    public void setGoodsListBySeries(Set<String> goodsListBySeries)
    {
        if (goodsListBySeries != null)
        {
            this.goodsListBySeries = new LinkedHashSet<>(goodsListBySeries.size());
            this.goodsListBySeries.addAll(goodsListBySeries);
        }
        else
            this.goodsListBySeries = new LinkedHashSet<>(1);
    }

    public String getOrderCode()
    {
        return orderCode;
    }

    public void setOrderCode(String orderCode)
    {
        this.orderCode = (orderCode.equals("anyType{}") ? "" : orderCode);
    }
}