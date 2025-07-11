package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 05.12.2017.
 * ----------------------------------
 * Доп.информация по упаковкам сборщика склада
 */

public class PackageDetailTemplate
{
    /** Артикул */
    private String vendorCode = "";
    /** Наименование */
    private String name = "";
    /** Единица измерения */
    private String measure = "";
    /** Количество */
    private int count = 0;
    /** Вес */
    private double weight = 0;
    /** Обьём */
    private double capacity = 0;

    /**
     * @return артикул
     */
    public String getVendorCode()
    {
        return vendorCode;
    }

    /**
     * @param vendorCode артикул
     */
    public void setVendorCode(String vendorCode)
    {
        this.vendorCode = vendorCode;
    }

    /**
     * @return наименование товара
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name наименование товара
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return единица измерения
     */
    public String getMeasure()
    {
        return measure;
    }

    /**
     * @param measure ед.измерения
     */
    public void setMeasure(String measure)
    {
        this.measure = measure;
    }

    /**
     * @return количество
     */
    public int getCount()
    {
        return count;
    }

    /**
     * @param count количество
     */
    public void setCount(int count)
    {
        this.count = count;
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
}