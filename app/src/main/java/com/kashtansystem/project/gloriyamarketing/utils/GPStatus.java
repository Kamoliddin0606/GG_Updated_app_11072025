package com.kashtansystem.project.gloriyamarketing.utils;

/**
 * Created by FlameKaf on 18.05.2017.
 */

public enum GPStatus
{
    Failure(200),
    Success(201),
    GpsProviderDisabled(202),
    NetworkProviderDisabled(203),
    LastKnownLocation(204);

    private final int value;

    GPStatus(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}