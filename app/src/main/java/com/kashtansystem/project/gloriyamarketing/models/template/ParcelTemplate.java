package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 05.12.2017.
 * ----------------------------------
 * Макет данных по посылке
 */

public class ParcelTemplate
{
    /** ид записи в бд */
    private long rowId = -1;
    /** код дилера */
    private String dealerCode = "";
    /** наименование дилера */
    private String dealerName = "";
    /** комментарий */
    private String comment = "";
    /** денежная сумма */
    private String summa = "0";
    /** тип валюты */
    private String currencyType = "1";
    /** курс валюты */
    private String rate = "0";

    /**
     * @return код дилера
     */
    public String getDealerCode()
    {
        return dealerCode;
    }

    /**
     * @param dealerCode код код дилера
     */
    public void setDealerCode(String dealerCode)
    {
        this.dealerCode = dealerCode;
    }

    /**
     * @return наименование дилера
     */
    public String getDealerName()
    {
        return dealerName;
    }

    /**
     * @param dealerName наименование дилера
     */
    public void setDealerName(String dealerName)
    {
        this.dealerName = dealerName;
    }

    /**
     * @return комментарий
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param comment комментарий
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * @return денежная сумма
     */
    public String getSumma()
    {
        return summa;
    }

    /**
     * @param summa денежная сумма
     */
    public void setSumma(String summa)
    {
        this.summa = summa;
    }

    /**
     * @return тип валюты
     */
    public String getCurrencyType()
    {
        return currencyType;
    }

    /**
     * @param currencyType тип валюты
     */
    public void setCurrencyType(String currencyType)
    {
        this.currencyType = currencyType;
    }

    /**
     * @return ид записи бд
     */
    public long getRowId()
    {
        return rowId;
    }

    /**
     * @param rowId ид записи бд
     */
    public void setRowId(long rowId)
    {
        this.rowId = rowId;
    }

    /**
     * @return курс валюты
     */
    public String getRate()
    {
        return rate;
    }

    /**
     * @param rate курс валюты
     */
    public void setRate(String rate)
    {
        this.rate = rate;
    }
}