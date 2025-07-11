package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.database.tables.Contracts;
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Получение списка причин отказов
 */

public class ReqGetOldContract
{
    public static LinkedList<ContractTemplate> load(Context context, String tpCode)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetContractsPast";
        final String methodName = "GetContractsPast";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeClient", tpCode);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        LinkedList<ContractTemplate> result = new LinkedList<>();
        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject items = (SoapObject)envelope.getResponse();
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                ContractTemplate contractTemplate = new ContractTemplate(
                        simpleDF.parse(refusal.getProperty("DateOfContract").toString()),
                        refusal.getProperty("CodeContract").toString().replace("anyType{}",""),
                        Float.parseFloat(refusal.getProperty("SumOfContract").toString()),
                        simpleDF.parse(refusal.getProperty("TermOfContract").toString()),
                        refusal.getProperty("TypeContract").toString(),
                        refusal.getProperty("NameDocument").toString().replace("anyType{}","")
                );

                result.add(contractTemplate);
            }
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load stock <<<");
            ex.printStackTrace();
        }

        return result;
    }
}