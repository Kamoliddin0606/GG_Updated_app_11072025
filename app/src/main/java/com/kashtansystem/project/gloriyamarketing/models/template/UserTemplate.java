package com.kashtansystem.project.gloriyamarketing.models.template;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.UserType;

import org.json.JSONArray;

import java.util.Arrays;

/**
 * Created by FlameKaf on 05.05.2017.
 * ----------------------------------
 * Макет данных о пользователе
 */

public class UserTemplate
{
    private boolean logined = false;
    // * Код пользователя
    private String userCode = "";
    // * Тип пользователя
    private UserType userType = UserType.Agent;
    // * Ф.И.О пользователя
    private String userName = "";
    // * Код проекта
    private String projectCode = "";
    // * код склада, которому прикреплён агент
    private String warehouseCode = "";

    private String plan = "0";
    private String fact = "0";
    private int okb = 0;
    private String kpi_updated_date = "";
    private int akbPlan = 0;
    private int akbFact = 0;
    private String akbPercent = "0";

    // Текущий проект
    private String projectURL = "";
    // Логины на разных проектах
    private String logins[];

    public void setProjectURL(String projectURL) { this.projectURL = projectURL; }
    public String getProjectURL() { return projectURL; }


    public void setLogin(String login, int id, Context context)
    {
        if (logins == null) {
            //add +1 for type "all project"
            logins = new String[C.SOAP.URLs.length + 1];
        }
        this.logins[id] = login;
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString("logins_cache", (new JSONArray(Arrays.asList(logins))).toString())
                .apply();
    }
    public String getLogin(int id, Context context)
    {
        if (logins == null)
        {
            //add +1 for type "all project"
            logins = new String[C.SOAP.URLs.length+1];
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            try
            {
                JSONArray loginsJSON = new JSONArray(prefs.getString("logins_cache", "[]"));
                for(int i = 0; i < loginsJSON.length(); i++)
                {
                    if (!loginsJSON.isNull(i))
                        logins[i] = loginsJSON.getString(i);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return logins[id];
    }

    // Проект по-умолчанию
    public void setProjectId(int id, Context context)
    {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putInt("projectId_cache", id)
                .apply();
    }

    public int getProjectId(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt("projectId_cache", 0);
    }

    /**
     * @param userId код пользователя
     * */
    public void setUserCode(String userId) { this.userCode = userId; }

    /**
     * @param userType тип пользователя
     * */
    public void setUserType(UserType userType)
    {
        this.userType = userType;
    }

    /**
     * @param name ф.и.о пользователя
     * */
    public void setUserName(String name)
    {
        userName = name;
    }

    /**
     * @return код пользователя
     * */
    public String getUserCode()
    {
        return userCode;
    }

    /**
     * @return тип пользователя
     * */
    public UserType getUserType()
    {
        return userType;
    }

    /**
     * @return тип пользователя
     * */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @return Код проекта
     * */
    public String getProjectCode()
    {
        return projectCode;
    }

    /**
     * @param projectCode Код проекта
     * */
    public void setProjectCode(String projectCode)
    {
        this.projectCode = projectCode;
    }

    public String getWarehouseCode()
    {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode)
    {
        this.warehouseCode = warehouseCode;
    }

    public boolean isLogined()
    {
        return logined;
    }

    public void setLogined(boolean logined)
    {
        this.logined = logined;
    }

    public String getPlan()
    {
        return plan;
    }

    public void setPlan(String plan)
    {
        this.plan = plan;
    }

    public String getFact()
    {
        return fact;
    }

    public void setFact(String fact)
    {
        this.fact = fact;
    }

    public int getOkb()
    {
        return okb;
    }

    public void setOkb(int okb)
    {
        this.okb = okb;
    }

    public String getKpiUpdatedDate()
    {
        return kpi_updated_date;
    }

    public void setKpiUpdatedDate(String kpi_updated_date)
    {
        this.kpi_updated_date = kpi_updated_date;
    }

    public int getAkbPlan()
    {
        return akbPlan;
    }

    public void setAkbPlan(int akbPlan)
    {
        this.akbPlan = akbPlan;
    }

    public int getAkbFact()
    {
        return akbFact;
    }

    public void setAkbFact(int akbFact)
    {
        this.akbFact = akbFact;
    }

    public String getAkbPercent()
    {
        return akbPercent;
    }

    public void setAkbPercent(String akbPercent)
    {
        this.akbPercent = akbPercent;
    }
}