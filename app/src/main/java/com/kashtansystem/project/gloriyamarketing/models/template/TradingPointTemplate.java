package com.kashtansystem.project.gloriyamarketing.models.template;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FlameKaf on 11.05.2017.
 * ----------------------------------
 * макет торговой точки
 */

public class TradingPointTemplate implements Parcelable
{
    private int rowId = 0;
    private String tpCode = "";
    private String title = "";
    private String signboard = "";
    private String referencePoint = "";
    private String inn = "";
    private String legalAddress = "";
    private String address = "";
    private double latitude = 0;
    private double longitude = 0;
    private String contactPerson = "";
    private String contactPersonPhone = "";
    private String respPersonPhone = "";
    private String tpType = "";
    private String forwarder = "";
    private int counterOfOrders = 0;
    private double creditLimit = 0;
    private double accumulatedCredit = 0;
    private String businessRegionsCode = "";
    private String director = "";
    private String mfo = "";
    private String rc = "";

    public static final Creator CREATOR = new Creator()
    {
        @Override
        public TradingPointTemplate createFromParcel(Parcel source)
        {
            return new TradingPointTemplate(source);
        }

        @Override
        public TradingPointTemplate[] newArray(int size)
        {
            return new TradingPointTemplate[0];
        }
    };

    public TradingPointTemplate(Parcel source)
    {
        rowId = source.readInt();
        tpCode = source.readString();
        title = source.readString();
        signboard = source.readString();
        referencePoint = source.readString();
        inn = source.readString();
        legalAddress = source.readString();
        address = source.readString();
        latitude = source.readDouble();
        longitude = source.readDouble();
        contactPerson = source.readString();
        contactPersonPhone = source.readString();
        respPersonPhone = source.readString();
        tpType = source.readString();
        forwarder = source.readString();
        counterOfOrders = source.readInt();
        creditLimit = source.readDouble();
        accumulatedCredit = source.readDouble();
        businessRegionsCode = source.readString();
    }

    public TradingPointTemplate()
    {}

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(rowId);
        dest.writeString(tpCode);
        dest.writeString(title);
        dest.writeString(signboard);
        dest.writeString(referencePoint);
        dest.writeString(inn);
        dest.writeString(legalAddress);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(contactPerson);
        dest.writeString(contactPersonPhone);
        dest.writeString(respPersonPhone);
        dest.writeString(tpType);
        dest.writeString(forwarder);
        dest.writeInt(counterOfOrders);
        dest.writeDouble(creditLimit);
        dest.writeDouble(accumulatedCredit);
        dest.writeString(businessRegionsCode);
    }

    /**
     * @param tpCode уникальный номер т.т.
     * */
    public void setTpCode(String tpCode)
    {
        this.tpCode = tpCode;
    }

    /**
     * @param rowId порядковый номер т.т. в списке
     * */
    public void setRowId(int rowId)
    {
        this.rowId = rowId;
    }

    /**
     * @param title название т.т.
     * */
    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setSignboard(String signboard)
    {
        this.signboard = signboard;
    }

    public void setReferencePoint(String referencePoint)
    {
        this.referencePoint = referencePoint;
    }

    /**
     * @param inn инн т.т.
     * */
    public void setInn(String inn)
    {
        this.inn = inn;
    }

    /**
     * @param address адресс т.т.
     * */
    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setDirector(String director){
        this.director = director;
    }
    /**
     * @param contactPerson Ф.И.О контактного лица
     * */
    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    /**
     * @param contactPersonPhone номер телефона контактного лица
     * */
    public void setContactPersonPhone(String contactPersonPhone)
    {
        this.contactPersonPhone = contactPersonPhone;
    }

    /**
     * @param respPersonPhone номер телефона ответственного лица за т.т.
     * */
    public void setRespPersonPhone(String respPersonPhone)
    {
        this.respPersonPhone = respPersonPhone;
    }

    /**
     * @param tpType тип т.т.
     * */
    public void setTpType(String tpType)
    {
        this.tpType = tpType;
    }

    /**
     * @param forwarder Ф.И.О экспедитра
     * */
    public void setForwarder(String forwarder)
    {
        this.forwarder = forwarder;
    }

    /**
     * @param counterOfOrders количество заказов по т.т.
     * */
    public void setCounterOfOrders(int counterOfOrders)
    {
        this.counterOfOrders = counterOfOrders;
    }

    /**
     * @param latitude долгота
     * */
    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    /**
     * @param longitude ширина
     * */
    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    /**
     * @return уникальный номер т.т.
     * */
    public String getTpCode()
    {
        return tpCode;
    }

    /**
     * @return номер в списке
     * */
    public int getRowId()
    {
        return rowId;
    }

    /**
     * @return название т.т.
     * */
    public String getTitle()
    {
        return title;
    }

    public String getSignboard()
    {
        return signboard;
    }

    public String getReferencePoint()
    {
        return referencePoint;
    }

    /**
     * @return ИНН
     * */
    public String getInn()
    {
        return inn;
    }

    /**
     * @return адрес
     * */
    public String getAddress()
    {
        return address;
    }

    /**
     * @return Ф.И.О ответственного лица
     * */
    public String getContactPerson()
    {
        return contactPerson;
    }

    /**
     * @return номер контактного лица
     * */
    public String getContactPersonPhone()
    {
        return contactPersonPhone;
    }

    /**
     * @return номер ответственного лица
     * */
    public String getRespPersonPhone()
    {
        return respPersonPhone;
    }

    /**
     * @return тип торговой точки
     * */
    public String getTpType()
    {
        return tpType;
    }

    /**
     * @return Ф.И.О экспедитора
     * */
    public String getForwarder()
    {
        return forwarder;
    }

    /**
     * @return количество заказов (активных)
     * */
    public int getCounterOfOrders()
    {
        return counterOfOrders;
    }

    /**
     * @return долгота
     * */
    public double getLatitude()
    {
        return this.latitude;
    }

    /**
     * @return ширина
     * */
    public double getLongitude()
    {
        return this.longitude;
    }

    /**
     * @return Сумма кредита клиента
     * */
    public double getCreditLimit()
    {
        return creditLimit;
    }

    /**
     * @param creditLimit Сумма кредита клиента
     * */
    public void setCreditLimit(double creditLimit)
    {
        this.creditLimit = creditLimit;
    }

    /**
     * @return Накопленная сумма кредита
     * */
    public double getAccumulatedCredit()
    {
        return accumulatedCredit;
    }

    /**
     * @param accumulatedCredit Накопленная сумма кредита
     * */
    public void setAcumulatedCredit(double accumulatedCredit)
    {
        this.accumulatedCredit = accumulatedCredit;
    }

    /**
     * @param address Юр.адрес клиента
     * */
    public void setLegalAddress(String address)
    {
        legalAddress = address;
    }

    /**
     * @return Юр.адрес клиента
     * */
    public String getLegalAddress()
    {
        return legalAddress;
    }

    public String getDirector() {
        return director;
    }

    public void setBusinessRegionsCode(String code)
    {
        this.businessRegionsCode = code;
    }

    public String getBusinessRegionsCode()
    {
        return this.businessRegionsCode;
    }

    public String getMfo() {
        return mfo;
    }

    public void setMfo(String mfo) {
        this.mfo = mfo;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }
}