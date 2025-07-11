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
 * Created by untec on 13.01.19.
 * ----------------------------------
 */

public class ReqSetCashOtherIncomes
{
    public static String[] send(String typeOfIncome, String currencyCode, String totalCash, String comment)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetCashOtherIncomes";
        final String methodName = "SetCashOtherIncomes";

        if (AppCache.USER_INFO == null)
            return new String[]{"-1", "data activity_agent is empty"};

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("DateOfIncomes", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("TypeOfIncome", typeOfIncome);
        request.addProperty("Currencies", currencyCode);
        request.addProperty("TotalCash", totalCash);
        request.addProperty("Comments", comment);

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