package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * Created by Dightmerc on 25.12.2020.
 * ----------------------------------
 */

public class LastSellDateTemplate
{
    // уникальный номер записи в таблице бд приложения
    private int id = 0;

    private int LastSellDate = 5;

    public void setDate(int date)
    {
        this.LastSellDate = date;
    }

    public int getDate()
    {
        return LastSellDate;
    }

}