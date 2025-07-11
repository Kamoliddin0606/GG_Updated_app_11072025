package com.kashtansystem.project.gloriyamarketing.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

/**
 * Created by FlameKaf on 09.07.2017.
 * ----------------------------------
 */

public class SendOrdersByAlarm
{
    public static void alarmResendOrders(Context context, int repeat, boolean create)
    {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
            new Intent(C.ACTIONS.ACTION_RESEND_ORDERS), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Service.ALARM_SERVICE);

        if (create)
        {
            L.info("Alarm for resend orders created");
            final long updateTime = repeat * 60 * 1000;
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + updateTime,
                updateTime, pendingIntent);
        }
        else
        {
            L.info("Alarm for resend orders canceled");
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}