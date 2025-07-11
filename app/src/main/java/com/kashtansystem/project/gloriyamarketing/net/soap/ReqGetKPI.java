package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
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

public class ReqGetKPI
{
    public static void load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetKPI";
        final String methodName = "GetKPI";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            //L.info(response.toString());
            AppCache.USER_INFO.setPlan(Util.getParsedPrice(Double.parseDouble(response.getPropertyAsString("TotalPlan"))));
            AppCache.USER_INFO.setFact(Util.getParsedPrice(Double.parseDouble(response.getPropertyAsString("TotalFact"))));
            AppCache.USER_INFO.setOkb(Integer.parseInt(response.getPropertyAsString("OKB")));
            AppCache.USER_INFO.setAkbPlan(Integer.parseInt(response.getPropertyAsString("AKBPlan")));
            AppCache.USER_INFO.setAkbFact(Integer.parseInt(response.getPropertyAsString("AKBFact")));
            AppCache.USER_INFO.setAkbPercent(response.getPropertyAsString("AKBPercent"));
            AppCache.USER_INFO.setKpiUpdatedDate(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                .format(new Date(System.currentTimeMillis())));
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: loadAllTradingPointsByUserCode <<<");
            ex.printStackTrace();
        }
    }
}