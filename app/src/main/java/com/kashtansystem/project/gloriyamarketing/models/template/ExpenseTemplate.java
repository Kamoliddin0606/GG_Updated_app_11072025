package com.kashtansystem.project.gloriyamarketing.models.template;

/**
 * РКО
 */

public class ExpenseTemplate
{
    private String code = "";
    private String info = "";
    private boolean confirmed = false;

    public String getCode()
    {
        return code;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }
}