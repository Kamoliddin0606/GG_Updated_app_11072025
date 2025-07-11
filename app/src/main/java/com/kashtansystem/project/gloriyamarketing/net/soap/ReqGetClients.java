package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.LinkedList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqGetClients
{
    public static LinkedList<TradingPointTemplate> load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetClients";
        final String methodName = "GetClients";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("UserCode", AppCache.USER_INFO.getUserCode());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        LinkedList<TradingPointTemplate> result = new LinkedList<>();

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            //L.info(response.toString());
            String temp;

            for (int i = 0; response.getPropertyCount() > i; i++)
            {
                SoapObject item = (SoapObject)response.getProperty(i);
                TradingPointTemplate tp = new TradingPointTemplate();
                temp = item.getProperty("Code").toString();
                tp.setTpCode((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("Name").toString();
                tp.setTitle((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("Signboard").toString();
                tp.setSignboard((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("ReferencePoint").toString();
                tp.setReferencePoint((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("INN").toString();
                tp.setInn((temp.equalsIgnoreCase("anyType{}") ? "" : temp));

                temp = item.getProperty("Latitude").toString();
                if (!temp.equalsIgnoreCase("anyType{}"))
                {
                    tp.setLatitude(Double.parseDouble(item.getProperty("Latitude").toString().replace(",", ".")));
                    tp.setLongitude(Double.parseDouble(item.getProperty("Longitude").toString().replace(",", ".")));
                }

                temp = item.getProperty("AdressDelivery").toString();
                tp.setAddress((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("ContactPerson").toString();
                tp.setContactPerson((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("ContactPersonPhone").toString();
                tp.setContactPersonPhone((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("ResponsiblePersonPhone").toString();
                tp.setRespPersonPhone((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("TradePointType").toString();
                tp.setTpType((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                temp = item.getProperty("TheNumberOfOrders").toString();
                tp.setCounterOfOrders(Integer.parseInt((temp.equalsIgnoreCase("anyType{}") ? "0" : temp)));
                temp = item.getProperty("CreditLimit").toString();
                tp.setCreditLimit(Double.parseDouble((temp.equalsIgnoreCase("anyType{}") ? "0" : temp)));
                temp = item.getProperty("AccumulatedCredit").toString();
                tp.setAcumulatedCredit(Double.parseDouble((temp.equalsIgnoreCase("anyType{}") ? "0" : temp)));

                result.add(tp);
            }

        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: loadAllTradingPointsByUserCode <<<");
            ex.printStackTrace();
        }
        return result;
    }
}