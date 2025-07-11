package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by FlameKaf on 11.10.2017.
 * ----------------------------------
 * Детальная информация о собранных денежных средств экспедитора
 */

public class ForwarderBodyTemplate
{
    /* код клиента */
    private String tpCode = "";
    /* наименование клиента */
    private String tpName = "";
    /* собранная денежная сумма */
    private double cash = 0;
    /* подтверждение о получении денежных средств */
    private boolean isConfirmed = false;

    /**
     * @return код клиента
     */
    public String getTpCode()
    {
        return tpCode;
    }

    /**
     * @param tpCode код клиента
     */
    public void setTpCode(String tpCode)
    {
        this.tpCode = tpCode;
    }

    /**
     * @return наименование клиента
     */
    public String getTpName()
    {
        return tpName;
    }

    /**
     * @param tpName наименование клиента
     */
    public void setTpName(String tpName)
    {
        this.tpName = tpName;
    }

    /**
     * @return собранная денежная сумма
     */
    public double getCash()
    {
        return cash;
    }

    /**
     * @param cash собранная денежная сумма
     */
    public void setCash(double cash)
    {
        this.cash = cash;
    }

    /**
     * @return подтверждение о получении денежных средств
     */
    public boolean isConfirmed()
    {
        return isConfirmed;
    }

    /**
     * @param confirmed подтверждение о получении денежных средств
     */
    public void setConfirmed(boolean confirmed)
    {
        this.isConfirmed = confirmed;
    }
}