package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Проверка накопленного кредита
 */

public class ReqCheckCredit
{
    public static Double getBalance(String tpCode)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetLoanBalance";
        final String methodName = "GetLoanBalance";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeClient", tpCode);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        httpTransportSE.debug = true;

        L.info(request.toString());

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
            L.info(response.toString());
            return Double.parseDouble(response.toString());
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: authorization <<<");
            ex.printStackTrace();
            return 0.0;
        }
    }
}