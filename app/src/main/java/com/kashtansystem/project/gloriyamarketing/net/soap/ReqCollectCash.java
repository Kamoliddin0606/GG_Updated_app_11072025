package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

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

public class ReqCollectCash
{
    public static String[] send(String tpCode, double sum)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:CollectCash";
        final String methodName = "CollectCash";

        if (AppCache.USER_INFO == null)
            return new String[]{"-1", "data activity_agent is empty"};

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeClient", tpCode);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());

        SoapObject cashTotalList = new SoapObject(C.SOAP.NAME_SPACE, "CashList");
        SoapObject rows =  new SoapObject(C.SOAP.NAME_SPACE, "CashRow");
            rows.addProperty("TypeId", "1");
            rows.addProperty("TotalCash", "0");
            rows.addProperty("Rate", "0");
            rows.addProperty("TotalCashSum", Util.getDoubleToString(sum));
        cashTotalList.addProperty("Rows", rows);
        request.addProperty("CashTotalList", cashTotalList);
        request.addProperty("DateOfReceipt", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));

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
            L.info("cash.: " + response.getPropertyAsString("CashTotal"));

            return new String[]{code, (code.equals("0") ? "successfully send" : msg)};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[]{"-1", (ex.getMessage() != null ? ex.getMessage() : "Exception Unknown")};
        }
    }
}