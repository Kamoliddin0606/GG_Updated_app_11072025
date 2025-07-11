package com.kashtansystem.project.gloriyamarketing.models.template;

import android.os.Parcel;
import android.os.Parcelable;

import com.kashtansystem.project.gloriyamarketing.utils.OrderStatus;

/**
 * Created by FlameKaf on 30.05.2017.
 * ----------------------------------
 */

public class OrderTemplate implements Parcelable
{
    // уникальный номер записи в таблице бд приложения
    private int id = 0;
    // порядковый номер в списке
    private int listItemId = 0;
    // ид заказа на сервере
    private String orderCode = "";
    // код торговой точки
    private String tpCode = "";
    // наименование торговой точки
    private String tpName = "";
    // Наименование заказа
    private String title = "";
    // Тип цены (наименование)
    private String priceTypeName = "";
    // Комментарий агенту.
    private String comment = "";
    // долгота
    private double latitude = 0;
    // ширина
    private double longitude = 0;
    // Статус заказа
    private OrderStatus orderStatus = OrderStatus.SavedLocal;
    // * Дата создания заказа
    private String createdDate = "";
    // * Общая сумма по заказу
    private double totalPrice = 0;
    // * предупреждение о возникшей ошибке.
    private String attention = "";

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public Object createFromParcel(Parcel source)
        {
            return new OrderTemplate(source);
        }

        @Override
        public OrderTemplate[] newArray(int size)
        {
            return new OrderTemplate[0];
        }
    };

    public OrderTemplate()
    {}

    public OrderTemplate(Parcel parcel)
    {
        id = parcel.readInt();
        listItemId = parcel.readInt();
        orderCode = parcel.readString();
        tpCode = parcel.readString();
        tpName = parcel.readString();
        title = parcel.readString();
        priceTypeName = parcel.readString();
        comment = parcel.readString();
        latitude = parcel.readDouble();
        longitude = parcel.readDouble();
        orderStatus = OrderStatus.getOrderStatusByValue(parcel.readInt());
        createdDate = parcel.readString();
        totalPrice = parcel.readDouble();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(listItemId);
        dest.writeString(orderCode);
        dest.writeString(tpCode);
        dest.writeString(tpName);
        dest.writeString(title);
        dest.writeString(priceTypeName);
        dest.writeString(comment);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(orderStatus.getValue());
        dest.writeString(createdDate);
        dest.writeDouble(totalPrice);
    }

    /**
     * @param id номер записи в таблице бд приложения
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @param id порядковый номер в списке
     */
    public void setRowId(int id)
    {
        this.listItemId = id;
    }

    /**
     * @param title наименование заказа
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @param priceTypeName тип цены (наименование)
     * */
    public void setPriceTypeName(String priceTypeName)
    {
        this.priceTypeName = priceTypeName;
    }

    /**
     * @param orderStatus статус заказа
     */
    public void setOrderStatus(OrderStatus orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    /**
     * @param comment комментарий агенту
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * @return индекс записи в таблице бд приложения
     */
    public int getId()
    {
        return id;
    }

    /**
     * @return порядковый номер в списке
     */
    public int getRowId()
    {
        return listItemId;
    }

    /**
     * @return наименование заказа
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @return статус заказа
     */
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }

    /**
     * @return комментарий агенту
     */
    public String getComment()
    {
        return comment;
    }

    public String getPriceTypeName()
    {
        return priceTypeName;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public String getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(String createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getOrderCode()
    {
        return orderCode;
    }

    public void setOrderCode(String orderCode)
    {
        this.orderCode = orderCode;
    }

    public double getTotalPrice()
    {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public String getAttention()
    {
        return attention;
    }

    public void setAttention(String attention)
    {
        this.attention = attention;
    }

    /**
     * @return код т.т.
     */
    public String getTpCode()
    {
        return tpCode;
    }

    /**
     * @param tpCode код т.т.
     */
    public void setTpCode(String tpCode)
    {
        this.tpCode = tpCode;
    }

    /**
     * @return наименование т.т.
     */
    public String getTpName()
    {
        return tpName;
    }

    /**
     * @param tpName наименование т.т.
     */
    public void setTpName(String tpName)
    {
        this.tpName = tpName;
    }
}