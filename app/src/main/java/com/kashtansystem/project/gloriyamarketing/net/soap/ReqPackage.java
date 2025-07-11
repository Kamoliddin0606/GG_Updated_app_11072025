package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.ParcelTemplate;
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
 * Отправка данных по посылке кассира
 */

public class ReqPackage
{
    public static String[] send(ParcelTemplate data)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetPackage";
        final String methodName = "GetPackage";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("DateOfPackage", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeClient", data.getDealerCode());

        SoapObject cashTotalList = new SoapObject(C.SOAP.NAME_SPACE, "CashList");
            SoapObject rows =  new SoapObject(C.SOAP.NAME_SPACE, "CashRow");
            if (data.getCurrencyType().equals("1"))
            {
                rows.addProperty("TypeId", data.getCurrencyType());
                rows.addProperty("TotalCash", "0");
                rows.addProperty("Rate", "0");
                rows.addProperty("TotalCashSum", data.getSumma());
            }
            else
            {
                rows.addProperty("TypeId", data.getCurrencyType());
                rows.addProperty("TotalCash", data.getSumma());
                rows.addProperty("Rate", data.getRate());
                rows.addProperty("TotalCashSum", "0");
            }
        cashTotalList.addProperty("Rows", rows);
        request.addProperty("CashTotalList", cashTotalList);
        request.addProperty("Comment", data.getComment());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            String code = response.getPropertyAsString("Code");
            String msg = response.getPropertyAsString("Message");

            L.info("code.: " + code);
            L.info("msg..: " + msg);

            return new String[]{code, (code.equals("0") ? "successfully send" : msg)};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[]{"-1", (ex.getMessage() != null ? ex.getMessage() : "Exception Unknown")};
        }
    }
}