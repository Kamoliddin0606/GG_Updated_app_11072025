package com.kashtansystem.project.gloriyamarketing.models.template;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 11.10.2017.
 * ----------------------------------
 * Общая информация о собранных денежных средств экспедитора
 */

public class ForwarderHeaderTemplate
{
    /* код экспедитора */
    private String userCode = "";
    /* ф.и.о. экспедитора */
    private String userName = "";
    /* общая собранная сумма по т.т-ам */
    private double totalCash = 0;
    /* детальная информация */
    private ArrayList<ForwarderBodyTemplate> details;

    /**
     * @return код экспедитора
     */
    public String getUserCode()
    {
        return userCode;
    }

    /**
     * @param userCode код экспедитора
     */
    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    /**
     * @return ф.и.о. экспедитора
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @param userName ф.и.о. экспедитора
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return общая собранная денежная сумма
     */
    public double getTotalCash()
    {
        return totalCash;
    }

    /**
     * @param totalCash общая собранная денежная сумма
     */
    public void setTotalCash(double totalCash)
    {
        this.totalCash = totalCash;
    }

    /**
     * @return детали
     */
    public ArrayList<ForwarderBodyTemplate> getDetails()
    {
        return details;
    }

    /**
     * @param details детали
     */
    public void setDetails(ArrayList<ForwarderBodyTemplate> details)
    {
        this.details = new ArrayList<>(details.size());
        this.details.addAll(details);
    }
}