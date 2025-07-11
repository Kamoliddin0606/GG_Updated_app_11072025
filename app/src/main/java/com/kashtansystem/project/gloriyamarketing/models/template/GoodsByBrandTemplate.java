package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 09.07.2017.
 * ----------------------------------
 * Параметры товара.
 */

public class GoodsByBrandTemplate
{
    /** Наименование брэнда */
    private String brand = "";
    /** Серия/Группа товара */
    private String series = "";
    /** Код склада */
    private String warehouseCode = "";
    /** Код товара */
    private String productCode = "";
    /** Наименование товара */
    private String productName = "";
    /** Количество доступного товара на складе */
    private int available = 0;
    /**
     * Текущая цена (цена со скилкой или же оригинальная), по которой расчитывается стоимость за
     * указанное кол-во товаров
     */
    private double price = 0;
    /** Оригинальная цена */
    private double originalPrice = 0;
    /** Значение скидки. 0,1...100 */
    private int discountValue = 0;
    /** Вес товара */
    private double weight = 0;
    /** Объём товара */
    private double capacity = 0;
    /** Количество товара (заполняется во время выбора товара, при формировании заказа) */
    private int amount = 0;
    /** Общая сумма (заполняется во время указания кол-ва товара, при формировании заказа) */
    private double total = 0;
    /** Мин.кол-во товара, за которое выдаётся подарок. */
    private int minAmount = 0;
    /** Кол-во товара, выдаваемое за превышение minAmount */
    private int count = 0;
    /** Код товара, за который выдаётся подарок */
    private String forProduct = "";
    /** Кол-во выбранного товара в разрезе серии */
    private int amountBySeries = 0;
    /* Кол-во возвращаемого товара */
    private int amountToReturn = 0;

    // Артикул
    private String vendorCode = "";
    public String getVendorCode()
    {
        return vendorCode;
    }
    public void setVendorCode(String vendorCode)
    {
        this.vendorCode = vendorCode;
    }
    // Количество подарков по акции
    private int giftAmount = 0;
    public int getGiftAmount() { return giftAmount; }
    public void setGiftAmount(int giftAmount)
    {
        this.giftAmount = giftAmount;
    }


    /**
     * @return Код склада
     * */
    public String getWarehouseCode()
    {
        return warehouseCode;
    }

    /**
     * @param warehouseCode Код склада
     * */
    public void setWarehouseCode(String warehouseCode)
    {
        this.warehouseCode = warehouseCode;
    }

    /**
     * @return Код продукции
     * */
    public String getProductCode()
    {
        return productCode;
    }

    /**
     * @param productCode Код склада
     * */
    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    /**
     * @return Наименование продукции (товара)
     * */
    public String getProductName()
    {
        return productName;
    }

    /**
     * @param productName Наименование продукции (товара)
     * */
    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    /**
     * @return Количество доступного товара
     * */
    public int getAvailable()
    {
        return available;
    }

    /**
     * @param available Количество доступного товара
     * */
    public void setAvailable(int available)
    {
        this.available = available;
    }

    /**
     * @return Цена товара
     * */
    public double getPrice()
    {
        return price;
    }

    /**
     * @param price Цена товара
     * */
    public void setPrice(double price)
    {
        this.price = price;
    }

    /**
     * @return Вес товара
     * */
    public double getWeight()
    {
        return weight;
    }

    /**
     * @param weight Вес товара
     * */
    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    /**
     * @return Объём товара
     * */
    public double getCapacity()
    {
        return capacity;
    }

    /**
     * @param capacity Объём товара
     * */
    public void setCapacity(double capacity)
    {
        this.capacity = capacity;
    }

    /**
     * @return Количество выбранного товара
     * */
    public int getAmount()
    {
        return amount;
    }

    /**
     * @param amount Количество выбранного товара
     * */
    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    /**
     * @return Общая сумма товаров
     * */
    public double getTotal()
    {
        return total;
    }

    /**
     * @param total Общая сумма товаров
     * */
    public void setTotal(double total)
    {
        this.total = total;
    }

    /**
     * @return наименование бренда
     * */
    public String getBrand()
    {
        return brand;
    }

    /**
     * @param brand Наименование бренда товара
     * */
    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    /**
     * @return наименование серии
     * */
    public String getSeries()
    {
        return series;
    }

    /**
     * @param series наименование серии/группы товара
     * */
    public void setSeries(String series)
    {
        this.series = series;
    }

    /**
     * @return мин.кол-во товара, за которое выдаётся подарок. Если 0, то акция на товар не распространяется.
     * */
    public int getMinAmount()
    {
        return minAmount;
    }

    /**
     * @param minAmount мин.кол-во товара, за которое выдаётся подарок. Если 0, то акция на товар не распространяется.
     * */
    public void setMinAmount(int minAmount)
    {
        this.minAmount = minAmount;
    }

    /**
     * @return Оригинальная цена товара
     * */
    public double getOriginalPrice()
    {
        return originalPrice;
    }

    /**
     * @param originalPrice оригинальная цена
     * */
    public void setOriginalPrice(double originalPrice)
    {
        this.originalPrice = originalPrice;
    }

    /**
     * @return Значение скидки. 0,1...100
     * */
    public int getDiscountValue()
    {
        return discountValue;
    }

    /**
     * @param discountValue значение скидки. 0,1...100
     * */
    public void setDiscountValue(int discountValue)
    {
        this.discountValue = discountValue;
    }

    /**
     * @return кол-во товара, выдаваемое за превышение minAmount
     * */
    public int getCount()
    {
        return count;
    }

    /**
     * @param count кол-во товара, выдаваемое за превышение minAmount
     * */
    public void setCount(int count)
    {
        this.count = count;
    }

    /**
     * @return код товара, за который выдаётся подарок
     * */
    public String getForProduct()
    {
        return forProduct;
    }

    /**
     * @param forProduct код товара, за который выдаётся подарок
     * */
    public void setForProduct(String forProduct)
    {
        this.forProduct = forProduct;
    }

    /**
     * @return Кол-во выбранного товара в разрезе серии
     * */
    public int getAmountBySeries()
    {
        return amountBySeries;
    }

    /**
     * @param amountBySeries Кол-во выбранного товара в разрезе серии
     * */
    public void setAmountBySeries(int amountBySeries)
    {
        this.amountBySeries = amountBySeries;
    }

    public int getAmountToReturn()
    {
        return amountToReturn;
    }

    public void setAmountToReturn(int amountToReturn)
    {
        this.amountToReturn = amountToReturn;
    }
}