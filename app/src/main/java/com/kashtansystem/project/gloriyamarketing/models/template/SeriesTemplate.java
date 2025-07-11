package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 17.07.2017.
 * ----------------------------------
 * Макет товаров по сериям.
 */

public class SeriesTemplate
{
    //* Наименование серии товаров
    private String name = "";
    //* Мин.кол-во товара, за которое выдаётся подарок
    private int minAmount = 0;
    //* Общее выбранных товаров
    private int amount = 0;

    // * Список товаров
    private ArrayList<GoodsByBrandTemplate> goods;


    /**
     * @return Наименование серии товаров
     * */
    public String getName()
    {
        return name;
    }
    /**
     * @param name Наименование товара
     * */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return Список товаров по серии
     * */
    public ArrayList<GoodsByBrandTemplate> getGoods()
    {
        return goods;
    }
    /**
     * @param goods Список товаров
     * */
    public void setGoods(ArrayList<GoodsByBrandTemplate> goods)
    {
        this.goods = goods;
    }

    /**
     * @return  Мин.кол-во товара, за которое выдаётся подарок
     * */
    public int getMinAmount()
    {
        return minAmount;
    }

    /**
     * @param minAmount Мин.кол-во товара, за которое выдаётся подарок
     * */
    public void setMinAmount(int minAmount)
    {
        this.minAmount = minAmount;
    }

    /**
     * @return Общее кол-во выбраннных товаров
     * */
    public int getAmount()
    {
        return amount;
    }

    /**
     * @param amount Общее кол-во выбраннных товаров
     * */
    public void setAmount(int amount)
    {
        this.amount = amount;
    }
}