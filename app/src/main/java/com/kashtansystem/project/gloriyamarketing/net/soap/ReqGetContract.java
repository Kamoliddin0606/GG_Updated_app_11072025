package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplateFull;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Получение списка причин отказов
 */

public class ReqGetContract
{
    public static LinkedList<ContractTemplateFull> load(Context context, String tpCode)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetContracts";
        final String methodName = "GetContracts";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeClient", tpCode);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        LinkedList<ContractTemplateFull> result = new LinkedList<>();

        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject items = (SoapObject)envelope.getResponse();
            for (int i = 0; items.getPropertyCount() > i; i++) {
                SoapObject refusal = (SoapObject) items.getProperty(i);

                String termCertificate = refusal.getProperty("TermCertificate").toString().replace("anyType{}", "");


                ContractTemplateFull contractTemplate = new ContractTemplateFull(
                        simpleDF.parse(refusal.getProperty("DateOfContract").toString()),
                        refusal.getProperty("CodeContract").toString().replace("anyType{}",""),
                        Float.parseFloat(refusal.getProperty("SumOfContract").toString()),
                        simpleDF.parse(refusal.getProperty("TermOfContract").toString()),
                        refusal.getProperty("TypeContract").toString(),
                        refusal.getProperty("NumbReference").toString().replace("anyType{}",""),
                        refusal.getProperty("NumbCertificate").toString().replace("anyType{}",""),
                        termCertificate.isEmpty() ? new Date() : simpleDF.parse(refusal.getProperty("TermReference").toString()),
                        simpleDF.parse(refusal.getProperty("TermCertificate").toString()),
                        refusal.getProperty("NumbPassport").toString().replace("anyType{}",""),
                        simpleDF.parse(refusal.getProperty("TermCertificate").toString()),
                        refusal.getProperty("CertificateUnlimited").equals("1")
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