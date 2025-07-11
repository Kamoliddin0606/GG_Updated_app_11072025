package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 29.09.2017.
 * ----------------------------------
 * Цена за товар
 */

public class PriceTemplate
{
    // * текущая цена товара
    private double price = 0;
    // * цена по скидке
    private double newPrice = 0;
    // * скидка
    private int discount = 0;

    /**
     * @return текущая цена товара
     * */
    public double getPrice()
    {
        return price;
    }

    /**
     * @param price текущая цена товара
     * */
    public void setPrice(double price)
    {
        this.price = price;
    }

    /**
     * @return новая цена товара по скидке
     * */
    public double getNewPrice()
    {
        return newPrice;
    }

    /**
     * @param newPrice новая цена товара по скидке
     * */
    public void setNewPrice(double newPrice)
    {
        this.newPrice = newPrice;
    }

    /**
     * @return значение скидки - 0,1...100
     * */
    public int getDiscount()
    {
        return discount;
    }

    /**
     * @param discount значение скидки - 0,1...100
     * */
    public void setDiscount(int discount)
    {
        this.discount = discount;
    }
}