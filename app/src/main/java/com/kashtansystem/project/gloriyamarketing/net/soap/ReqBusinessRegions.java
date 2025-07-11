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
 */

public class ReqBusinessRegions
{
    public static void load(Context context)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetBusinessRegions";
        final String methodName = "GetBusinessRegions";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("UserCode", AppCache.USER_INFO.getUserCode());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            //L.info(response.toString());
            AppDB.getInstance(context).saveBusinessRegions(response);
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: loadPriceTypes <<<");
            ex.printStackTrace();
        }
    }
}