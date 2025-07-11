package com.kashtansystem.project.gloriyamarketing.utils;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.R;

/**
 * Created by FlameKaf on 05.05.2017.
 * ----------------------------------
 */

public enum UserType
{ // По текущему соответствию в 1С
    Agent(1), // Агент
    Forwarder(2), // Экспедитор
    Supervisor(3), // Супервайзер
    Boss(4), // Руководитель
    Collector(5), // Кассир
    Packer(6), // Менеджер
    WarehouseManager(7); // -

    private final int value;

    UserType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static UserType getUserTypeByValue(int value)
    {
        switch (value)
        {
            case 1:
            default:
                return Agent;
            case 2:
                return Forwarder;
            case 3:
                return Supervisor;
            case 4:
                return Boss;
            case 5:
                return Collector;
            case 6:
                return Packer;
            case 7:
                return WarehouseManager;
        }
    }

    public static String getTypeNameByValue(UserType userType, Context context)
    {
        switch (userType)
        {
            case Agent:
            default:
                return context.getString(R.string.app_title_agent);
            case Forwarder:
                return context.getString(R.string.app_title_forwarder);
            case Supervisor:
                return context.getString(R.string.app_title_supervisor);
            case Boss:
                return context.getString(R.string.app_title_boss);
            case Collector:
                return context.getString(R.string.app_title_collector);
            case Packer:
                return context.getString(R.string.app_title_packer);
            case WarehouseManager:
                return context.getString(R.string.app_title_warehouse_manager);
        }
    }
}