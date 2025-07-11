package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqSetCashExpenses
{
    public static String[] send(String typeOfExp, String currency, String totalCash, String comment, String codeBoss)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetCashExpenses";
        final String methodName = "SetCashExpenses";

        if (AppCache.USER_INFO == null)
            return new String[]{"-1", "data activity_agent is empty"};

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("DateOfExpenses", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("TypeOfExpenses", typeOfExp);
        request.addProperty("Currency", currency);
        request.addProperty("TotalCash", totalCash);
        request.addProperty("Comment", comment);
        request.addProperty("CodeBoss", codeBoss);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        L.info(request.toString());

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            String code = response.getPropertyAsString("Code");
            String msg = response.getPropertyAsString("Message");

            L.info("code.: " + code);
            L.info("msg..: " + msg);

            return new String[]{code, (code.equals("0") ? "Успешно" : msg)};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[]{"-1", (ex.getMessage() != null ? ex.getMessage() : "Exception Unknown")};
        }
    }
}