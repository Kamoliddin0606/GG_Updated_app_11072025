package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 11.10.2017.
 * ----------------------------------
 * Макет данных по упоковкам сборщика склада
 */

public class PackageTemplate
{
    /** код заказа */
    private String orderCode = "";
    /** дата доставки */
    private String deliveryDate = "";
    /** клиент */
    private String customer = "";
    /** общий обьём */
    private double capacity = 0;
    /** общий вес */
    private double weight = 0;
    /** ф.и.о сборщика */
    private String packerName = "";
    /** статус заказа */
    private byte statusCode = 0;
    /** Доп.информация по упаковке */
    private List<PackageDetailTemplate> details;

    /**
     * @return дата доставки
     */
    public String getDeliveryDate()
    {
        return deliveryDate;
    }

    /**
     * @param deliveryDate дата доставки
     */
    public void setDeliveryDate(String deliveryDate)
    {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return клиент (торговая точка)
     */
    public String getCustomer()
    {
        return customer;
    }

    /**
     * @param customer клиент (торговая точка)
     */
    public void setCustomer(String customer)
    {
        this.customer = customer;
    }

    /**
     * @return обьём
     */
    public double getCapacity()
    {
        return capacity;
    }

    /**
     * @param capacity обьём
     */
    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    /**
     * @return вес
     */
    public double getWeight()
    {
        return weight;
    }

    /**
     * @param weight вес
     */
    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    /**
     * @return доп.информация по упаковке
     */
    public List<PackageDetailTemplate> getDetails()
    {
        return details;
    }

    /**
     * @param details доп.информация по упаковки
     */
    public void setDetails(List<PackageDetailTemplate> details)
    {
        this.details = new ArrayList<>(details.size());
        this.details.addAll(details);
    }

    /**
     * @return ф.и.о. сборщика
     */
    public String getPackerName()
    {
        return packerName;
    }

    /**
     * @param packerName ф.и.о. сборщика
     */
    public void setPackerName(String packerName)
    {
        this.packerName = packerName;
    }

    /**
     * @return код заказа
     */
    public String getOrderCode()
    {
        return orderCode;
    }

    /**
     * @param orderCode код заказа
     */
    public void setOrderCode(String orderCode)
    {
        this.orderCode = orderCode;
    }

    /**
     * @return статус заказа
     */
    public byte getStatusCode()
    {
        return statusCode;
    }

    /**
     * @param statusCode статус заказа
     */
    public void setStatusCode(byte statusCode)
    {
        this.statusCode = statusCode;
    }
}