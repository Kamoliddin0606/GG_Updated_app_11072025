package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplateFull;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Получение списка причин отказов
 */

public class ReqGetTradePointTypeList
{
    public static LinkedList<String> load(Context context)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetTradePointTypeList";
        final String methodName = "GetTradePointTypeList";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);


        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        LinkedList<String> result = new LinkedList<>();

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject items = (SoapObject)envelope.getResponse();
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);
                result.add(refusal.getProperty("TypeName").toString().replace("anyType{}",""));
            }
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load stock <<<");
            ex.printStackTrace();
        }
        return result;
    }
}