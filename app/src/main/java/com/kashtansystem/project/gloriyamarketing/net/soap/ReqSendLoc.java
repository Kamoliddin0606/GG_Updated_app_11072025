package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqSendLoc
{
    public static String[] send(String userCode, String tpCode, double latitude, double longitude)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetGPS";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, "SetGPS");
        request.addProperty("CodeUser", userCode);
        request.addProperty("CodeClient", tpCode);
        request.addProperty("Longitude", longitude);
        request.addProperty("Latitude", latitude);
        request.addProperty("CreateDate", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        L.info(request.toString());
        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            String res = response.toString();
            L.info(res);
            return new String[] {res, (res.equals("1") ? "успешно отослан" : "Во время отправки произошла ошибка. Повторите попытку снова.")};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[] {"0", String.format("Не удалось отправить данные по причине: «%s»\nПовторите попытку.",
                (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"))};
        }
    }
}