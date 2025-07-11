package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 02.06.2017.
 * ----------------------------------
 */

public class OrdersHisTemplate
{
    private String tradingPoint = "";
    private ArrayList<OrderTemplate> ordersByTp;

    public void setTradingPoint(String tradingPoint)
    {
        this.tradingPoint = tradingPoint;
    }

    public void setOrdersByTp(ArrayList<OrderTemplate> ordersByTp)
    {
        this.ordersByTp = ordersByTp;
    }

    public String getTradingPoint()
    {
        return tradingPoint;
    }

    public ArrayList<OrderTemplate> getOrdersByTp()
    {
        return ordersByTp;
    }
}