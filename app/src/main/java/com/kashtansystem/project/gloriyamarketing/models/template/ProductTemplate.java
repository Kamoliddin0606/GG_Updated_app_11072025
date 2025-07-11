package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 09.07.2017.
 * ----------------------------------
 * Макет продукции.
 * Бренды
 * Серии (может быть null) -> товары по сериям
 * Товары (может быть null)
 */

public class ProductTemplate
{
    // * Бренд товара
    private String brand = "";
    // * Список серии и товаров по брендам. Может быть null
    private ArrayList<SeriesTemplate> series;

    /*public ProductTemplate() {}

    public ProductTemplate(String brand, ArrayList<SeriesTemplate> series) {
        this.brand = brand;
        this.series = series;
    }*/

    /**
     * @return Наименование бренда
     * */
    public String getBrand()
    {
        return brand;
    }

    /**
     * @param brand Наименование бренда
     * */
    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    /**
     * @return Список серий по бренду
     * */
    public ArrayList<SeriesTemplate> getSeries()
    {
        return series;
    }

    /**
     * @param series Список серий по бренду
     * */
    public void setSeries(ArrayList<SeriesTemplate> series)
    {
        this.series = series;
    }
    public ProductTemplate cloneWithEmptySeries(){
        ProductTemplate productTemplate = new ProductTemplate();
        productTemplate.brand = brand;
        productTemplate.series = new ArrayList<>();
        return productTemplate;
    }


}