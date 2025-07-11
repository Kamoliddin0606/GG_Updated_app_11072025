package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 09.07.2017.
 * ----------------------------------
 * Выбранный товар
 */

public class GoodsItemTemplate
{
    private String warehouseCode = "";
    private String goodsCode = "";
    private String goodsName = "";
    private int amount = 0;
    private int available = 0;
    private double price = 0;
    private double total = 0;
    private double weight = 0;
    private double capacity = 0;

    public void setWarehouseCode(String warehouseCode)
    {
        this.warehouseCode = warehouseCode;
    }

    public void setGoodsCode(String goodsCode)
    {
        this.goodsCode = goodsCode;
    }

    public void setGoodsName(String goodsName)
    {
        this.goodsName = goodsName;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public void setAvailable(int available)
    {
        this.available = available;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    public String getWarehouseCode()
    {
        return warehouseCode;
    }

    public String getGoodsCode()
    {
        return goodsCode;
    }

    public String getGoodsName()
    {
        return goodsName;
    }

    public int getAmount()
    {
        return amount;
    }

    public int getAvailable()
    {
        return available;
    }

    public double getPrice()
    {
        return price;
    }

    public double getTotal()
    {
        return total;
    }

    public double getWeight()
    {
        return weight;
    }

    public double getCapacity()
    {
        return capacity;
    }
}