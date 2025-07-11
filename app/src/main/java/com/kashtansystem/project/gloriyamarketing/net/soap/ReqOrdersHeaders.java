package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.util.Log;

import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.OrderStatus;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.LinkedList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрашивает краткую информацию по созданным заказам за текущий месяц.
 */

public class ReqOrdersHeaders
{
    public static LinkedList<OrderTemplate>     load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetOrderList";
        final String methodName = "GetOrderList";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeAgent", AppCache.USER_INFO.getUserCode());
        Log.println(Log.DEBUG, "dightmerc", "reuqested CodeAgent " + AppCache.USER_INFO.getUserCode());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        LinkedList<OrderTemplate> result = new LinkedList<>();

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            for (int i = 0; response.getPropertyCount() > i; i++)
            {
                SoapObject item = (SoapObject)response.getProperty(i);

                OrderTemplate order = new OrderTemplate();
                order.setOrderCode(item.getPropertyAsString("NumOrder"));
                order.setCreatedDate(Util.replaceSymbols(item.getPropertyAsString("DateOrder")));
                order.setTitle(item.getPropertyAsString("CaptionOrder"));
                order.setPriceTypeName(item.getPropertyAsString("TypePrice"));
                // <-- @author MrJ
                String comment = item.getPropertyAsString("CommentForwarder").equals("anyType{}")?
                        (item.getPropertyAsString("CommentSupervisor").equals("anyType{}")?
                                "" : item.getPropertyAsString("CommentSupervisor")) : item.getPropertyAsString("CommentForwarder");
                // -->
                order.setComment(comment);
                order.setOrderStatus(OrderStatus.getOrderStatusByValue(Integer.parseInt(item.getPropertyAsString("Status"))));
                order.setTotalPrice(Double.parseDouble(item.getPropertyAsString("Total").replace(",", ".")));
                order.setTpCode(item.getPropertyAsString("ClientCode"));
                order.setTpName(item.getPropertyAsString("ClientName"));
                result.add(order);

                //L.info("----------------------------");
                //L.info(order.getOrderCode());
                //L.info(order.getCreatedDate());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }
}