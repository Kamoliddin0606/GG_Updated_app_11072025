package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Получение списка причин отказов
 */

public class ReqGetMFO
{
    public static String load(Context context, String mfo)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetBankName";
        final String methodName = "GetBankName";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("MFO", mfo);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);


        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());



        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject item = (SoapObject)envelope.getResponse();
            return item.getPropertyAsString("Name");
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load stock <<<");
            ex.printStackTrace();
        }
        return "";
    }
}