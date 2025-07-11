package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 06.06.2017.
 * ----------------------------------
 * Макет товара по брендам
 */

public class GoodsTemplate
{
    private String brand = "";
    private ArrayList<String> goodsList;

    /**
     * @param brand производитель
     * */
    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    /**
     * @param goodsList список товара производителя
     * */
    public void setGoodsList(ArrayList<String> goodsList)
    {
        this.goodsList = goodsList;
    }

    /**
     * @return наименование производителя
     * */
    public String getBrand()
    {
        return brand;
    }

    /**
     * @return список товаров производителя
     * */
    public ArrayList<String> getGoodsList()
    {
        return goodsList;
    }
}