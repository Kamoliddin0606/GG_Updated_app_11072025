package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Получение списка РКО
 */

public class ReqGetExpenseList
{
    public static ArrayList<ExpenseTemplate> load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetExpenseList";
        final String methodName = "GetExpenseList";

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
            if (response.getPropertyCount() > 0)
            {
                ArrayList<ExpenseTemplate> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    SoapObject item = (SoapObject) response.getProperty(i);

                    ExpenseTemplate expenseTemplate = new ExpenseTemplate();
                    expenseTemplate.setCode(item.getPropertyAsString("Code"));
                    expenseTemplate.setInfo(item.getPropertyAsString("Info"));
                    result.add(expenseTemplate);
                }
                return result;
            }
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load expenses <<<");
            ex.printStackTrace();
        }
        return new ArrayList<>(1);
    }
}