package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 21.08.2017.
 * ----------------------------------
 * Макет данных кассира по сбору денежных средств
 */

public class CollectCashTemplate
{
    /* Код т.т. */
    private String tpCode = "";
    /* Наименование т.т. */
    private String tpName = "";
    /* адрес */
    private String address = "";
    /* долгота */
    private double latitude = 0;
    /* ширина */
    private double longitude = 0;
    /* ориентир */
    private String refPoint = "";
    /* контактное лицо */
    private String contactPerson = "";
    /* номер телефона контактного лица */
    private String contactPersonPhone = "";
    /* сумма сбора */
    private String cash = "0";
    /* дата сбора */
    private String date = "";
    /* собранна ли сумма */
    private boolean isCollected = false;

    public String getTpCode()
    {
        return tpCode;
    }

    public void setTpCode(String tpCode)
    {
        this.tpCode = tpCode;
    }

    public String getTpName()
    {
        return tpName;
    }

    public void setTpName(String tpName)
    {
        this.tpName = tpName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
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

    public String getRefPoint()
    {
        return refPoint;
    }

    public void setRefPoint(String refPoint)
    {
        this.refPoint = refPoint;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPersonPhone()
    {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone)
    {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getCash()
    {
        return cash;
    }

    public void setCash(String cash)
    {
        this.cash = cash;
    }

    public boolean isCollected()
    {
        return isCollected;
    }

    public void setCollected(boolean collected)
    {
        isCollected = collected;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}