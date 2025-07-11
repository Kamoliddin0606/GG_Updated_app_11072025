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
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрос получения скидов
 */

public class ReqDocumentTypes

{
    public static void load(Context ctx)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetTypeOfContract";
        final String methodName = "GetTypeOfContract";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        //L.info(request.toString());
        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            //L.info(response.toString());
            AppDB.getInstance(ctx).saveContractTypes(response);
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: Load Discounts <<<");
            ex.printStackTrace();
        }
    }
}