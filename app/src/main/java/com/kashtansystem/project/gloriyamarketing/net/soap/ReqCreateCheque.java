package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.activity.forwarder.DeliveryListActivity;
import com.kashtansystem.project.gloriyamarketing.models.template.DeliveryTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Objects;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqCreateCheque
{
    public static String[] send(String orderNumb, String orderDate, String cash, String uzcard, String humo, String printInstantly, int groupId, double latitude, double longitude)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:CreateCheque";
        final String methodName = "CreateCheque";

        if (AppCache.USER_INFO == null)
            return new String[]{"-1", "data activity_agent is empty"};

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);

        request.addProperty("Cash", cash);
        request.addProperty("Humo", uzcard);
        request.addProperty("Uzcard", humo);
        request.addProperty("CodeShipman", AppCache.USER_INFO.getUserCode());
        request.addProperty("OrderNumber", orderNumb);
        request.addProperty("OrderDate", orderDate);
        request.addProperty("PrintInstantly", printInstantly);
        request.addProperty("Latitude", Double.toString(latitude) );
        request.addProperty("Longitude", Double.toString(longitude));


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            String code = response.getPropertyAsString("Code");
            String msg = response.getPropertyAsString("Message");

            if (Objects.equals(code, "200")) {
                DeliveryTemplate currentDelivery = DeliveryListActivity.filteredDeliveryList.get(groupId);
                currentDelivery.setStatus(2);

                if (printInstantly.equals("1"))
                    currentDelivery.setChequeSent(true);
            }

            L.info("code.: " + code);
            L.info("msg..: " + msg);

            return new String[]{code, (code.equals("0") ? "successfully send" : msg)};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[]{"-1", (ex.getMessage() != null ? ex.getMessage() : "Exception Unknown")};
        }
    }
}