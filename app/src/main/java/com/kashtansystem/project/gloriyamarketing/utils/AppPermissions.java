package com.kashtansystem.project.gloriyamarketing.utils;

/**
 * Created by FlameKaf on 07.02.2017.
 * ----------------------------------
 */

public enum AppPermissions
{
    Storage(101),
    GeoLocation(102),
    NetworkState(103),
    Camera(104),
    CallPhone(105);

    private final int value;

    AppPermissions(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public static AppPermissions getValueByInt(int value)
    {
        switch (value)
        {
            case 101:
                return Storage;
            case 102:
                return GeoLocation;
            case 103:
                return NetworkState;
            case 104:
                return Camera;
            case 105:
                return CallPhone;
            default:
                return Storage;
        }
    }
}