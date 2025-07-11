package com.kashtansystem.project.gloriyamarketing.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.kashtansystem.project.gloriyamarketing.service.MainService;
import com.kashtansystem.project.gloriyamarketing.utils.C;

/**
 * Created by FlameKaf on 29.06.2017.
 * ----------------------------------
 * Запускает сервис приложения, когда телефон был перезагружен
 */

public class AppRebootReceiver extends BroadcastReceiver
{
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (!TextUtils.isEmpty(PreferenceManager.getDefaultSharedPreferences(context).getString(C.KEYS.USER_CODE, null)))
            context.startService(new Intent(context, MainService.class));
    }
}