package com.kashtansystem.project.gloriyamarketing.net.soap;


import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Получение списка причин отказов
 */

public class ReqChangeContract
{
    public static String[] load(String codeContract, Date TermOfContract, String NameDocument)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:ChangeContract";
        final String methodName = "ChangeContract";

        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeContract", codeContract);
        request.addProperty("TermOfContract", simpleDF.format(TermOfContract));
        request.addProperty("NameDocument", NameDocument);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        LinkedList<ContractTemplate> result = new LinkedList<>();

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            String res = response.getProperty("Code").toString();
            String mess = response.getProperty("Message").toString();

            return new String[] {res, mess};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[] {"0", String.format("Не удалось отправить данные по причине: «%s»\nПовторите попытку.",
                    (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"))};
        }
    }
}