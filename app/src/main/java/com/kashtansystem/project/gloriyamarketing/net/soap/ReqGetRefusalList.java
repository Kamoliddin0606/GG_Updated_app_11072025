package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Получение списка причин отказов
 */

public class ReqGetRefusalList
{
    public static void load(Context context)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetRefusalList";
        final String methodName = "GetRefusalList";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            AppDB.getInstance(context).saveRefusals(response);
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load refusals <<<");
            ex.printStackTrace();
        }
    }
}