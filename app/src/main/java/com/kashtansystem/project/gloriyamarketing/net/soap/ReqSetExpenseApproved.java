package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Отправка подтверждения РКО
 */

public class ReqSetExpenseApproved
{
    public static String[] send(String codeExpense)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetExpenseApproved";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, "SetExpenseApproved");
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeExpense", codeExpense);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapPrimitive response = (SoapPrimitive)envelope.getResponse();
            String res = response.toString();
            return new String[] {res, (res.equals("1") ? "Подтверждение успешно отправлено." : "Во время отправки произошла ошибка. Повторите попытку снова.")};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[] {"0", String.format("Не удалось отправить данные по причине: «%s»\nПовторите попытку.",
                (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"))};
        }
    }
}