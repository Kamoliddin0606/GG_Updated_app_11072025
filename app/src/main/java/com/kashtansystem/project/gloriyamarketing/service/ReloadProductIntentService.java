package com.kashtansystem.project.gloriyamarketing.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.kashtansystem.project.gloriyamarketing.net.soap.ReqProductBalance;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSpecEvents;
import com.kashtansystem.project.gloriyamarketing.utils.L;

/**
 * Created by FlameKaf on 09.07.2017.
 * ----------------------------------
 */

public class ReloadProductIntentService extends IntentService
{
    public ReloadProductIntentService()
    {
        super(ReloadProductIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        L.info("RELOAD PRODUCTS AND BALANCE...");
        ReqProductBalance.load(this);
        L.info("SPEC EVENTS...");
        ReqSpecEvents.load(this);
    }
}