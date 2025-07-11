package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.util.Log;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqReasonsToReturn
{
    public static String[] load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetReasonsOfReturn";
        final String methodName = "GetReasonsOfReturn";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.println(Log.DEBUG, "dightmerc", "project: " + AppCache.USER_INFO.getProjectURL());
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            if (response.getPropertyCount() > 0)
            {
                ArrayList<String> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                    result.add(((SoapObject)response.getProperty(i)).getPropertyAsString("Name"));
                return result.toArray(new String[]{});
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new String[]{""};
    }
}