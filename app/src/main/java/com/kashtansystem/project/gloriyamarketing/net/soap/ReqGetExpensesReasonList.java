package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.ExpensesReasonTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрос списка лиц, которым кассир сдаёт деньги
 */

public class ReqGetExpensesReasonList
{
    public static ArrayList<ExpensesReasonTemplate> load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetExpensesList";
        final String methodName = "GetExpensesList";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            if (response.getPropertyCount() > 0)
            {
                ArrayList<ExpensesReasonTemplate> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    SoapObject item = (SoapObject) response.getProperty(i);

                    ExpensesReasonTemplate expensesReasonTemplate = new ExpensesReasonTemplate();
                    expensesReasonTemplate.setCode(item.getPropertyAsString("Code"));
                    expensesReasonTemplate.setName(item.getPropertyAsString("Name"));
                    result.add(expensesReasonTemplate);
                }
                return result;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return new ArrayList<>(1);
    }
}