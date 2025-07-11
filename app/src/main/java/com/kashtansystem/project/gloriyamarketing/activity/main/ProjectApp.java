package com.kashtansystem.project.gloriyamarketing.activity.main;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.kashtansystem.project.gloriyamarketing.core.OfflineManager;
import com.splunk.mint.Mint;

/**
 * Created by FlameKaf on 19.09.2017.
 * ----------------------------------
 */

public class ProjectApp extends MultiDexApplication
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        OfflineManager.INSTANCE.init(this);
        //Mint.initAndStartSession(this, "6501ed68");
    }
}