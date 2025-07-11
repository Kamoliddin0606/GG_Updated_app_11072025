package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqNewClient
{
    public static String[] send(TradingPointTemplate in)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetClient";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, "SetClient");
        request.addProperty("Name", in.getTitle());
        request.addProperty("Signboard", in.getSignboard());
        request.addProperty("INN", in.getInn());
        request.addProperty("TradePointType", in.getTpType());
        request.addProperty("ContactPerson", in.getContactPerson());
        request.addProperty("ContactPersonPhone", in.getContactPersonPhone());
        request.addProperty("Adress", in.getLegalAddress());
        request.addProperty("AdressDelivery", in.getAddress());
        request.addProperty("ReferencePoint", in.getReferencePoint());
        request.addProperty("ResponsiblePersonPhone", in.getRespPersonPhone());
        request.addProperty("Latitude", in.getLatitude());
        request.addProperty("Longitude", in.getLongitude());
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeRegion", in.getBusinessRegionsCode());
        request.addProperty("Director", in.getDirector());
        request.addProperty("MFO", in.getMfo());
        request.addProperty("BankAccount", in.getRc());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);
        //L.info(request.toString());
        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject soapObject = (SoapObject)envelope.getResponse();
            //L.info(soapObject.toString());
            return new String[]{
                soapObject.getPropertyAsString("Code"),
                soapObject.getPropertyAsString("Message"),
                (soapObject.getPropertyAsString("Code").equals("1") ? soapObject.getPropertyAsString("CodeClient") : "")};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[] {"0", String.format("Не удалось отправить данные по причине: «%s»\nПовторите попытку.",
                (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"))};
        }
    }
}