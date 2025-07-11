package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.CollectCashTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрашивает список кредиторов
 */

public class ReqDebitorList
{
    public static ArrayList<CollectCashTemplate> getData()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:CashierCashList";
        final String methodName = "CashierCashList";

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
                ArrayList<CollectCashTemplate> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    SoapObject item = (SoapObject) response.getProperty(i);
                    CollectCashTemplate collectCash = new CollectCashTemplate();
                    collectCash.setTpCode(item.getPropertyAsString("ClientCode"));
                    collectCash.setTpName(item.getPropertyAsString("ClientName"));
                    collectCash.setAddress(item.getPropertyAsString("Adress"));
                    collectCash.setRefPoint(item.getPropertyAsString("ReferencePoint").equals("anyType{}") ?
                        "" : item.getPropertyAsString("ReferencePoint"));
                    if (!item.getPropertyAsString("Latitude").equalsIgnoreCase("anyType{}"))
                    {
                        collectCash.setLatitude(Double.parseDouble(item.getPropertyAsString("Latitude").replace(",", ".")));
                        collectCash.setLongitude(Double.parseDouble(item.getPropertyAsString("Longitude").replace(",", ".")));
                    }
                    collectCash.setContactPerson(item.getPropertyAsString("ContactPerson"));
                    collectCash.setContactPersonPhone((item.getPropertyAsString("ContactPersonPhone")
                        .equalsIgnoreCase("anyType{}") ? "" : item.getPropertyAsString("ContactPersonPhone")));
                    collectCash.setCash(item.getPropertyAsString("CashTotal"));
                    collectCash.setDate(Util.parseStringDate2(item.getPropertyAsString("PaymentDate")));
                    result.add(collectCash);
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