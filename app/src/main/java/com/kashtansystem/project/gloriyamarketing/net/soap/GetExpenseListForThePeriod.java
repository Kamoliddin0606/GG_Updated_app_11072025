package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserDetial;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Получение списка РКО
 */

public class GetExpenseListForThePeriod {
    public static ArrayList<ExpenseTemplate> load(UserDetial userDetial, long fromDate, long toDate) {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetExpenseList";
        final String methodName = "GetExpenseListForThePeriod";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", userDetial.getCodeUser());
        request.addProperty("Date1", dateFormat.format(new Date(Math.min(fromDate,toDate))));
        request.addProperty("Date2", dateFormat.format(new Date(Math.max(fromDate,toDate))));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransportSE = new HttpTransportSE(userDetial.getSoapProject().getUrl(), 60000);

        try {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();

            if (response.getPropertyCount() > 0) {
                ArrayList<ExpenseTemplate> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++) {
                    SoapObject item = (SoapObject) response.getProperty(i);

                    ExpenseTemplate expenseTemplate = new ExpenseTemplate();
                    expenseTemplate.setCode(item.getPropertyAsString("Code"));
                    expenseTemplate.setInfo(item.getPropertyAsString("Info"));
                    String confirmed = item.getPropertyAsString("Confirmed");
                    try {
                        int conInt = Integer.parseInt(confirmed);
                        expenseTemplate.setConfirmed(conInt == 1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    result.add(expenseTemplate);
                }
                return result;
            }
        } catch (Exception ex) {
            L.exception(">>> EXCEPTION: load expenses <<<");
            ex.printStackTrace();
        }
        return new ArrayList<>(1);
    }
}