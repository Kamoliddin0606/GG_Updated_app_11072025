package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.util.Log;

import com.kashtansystem.project.gloriyamarketing.activity.forwarder.DeliveryListActivity;
import com.kashtansystem.project.gloriyamarketing.models.template.DeliveryTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрос списка доставок заказов для экспедитора
 */

public class ReqShippingList
{
    public static double load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetShippingList";
        final String methodName = "GetShippingList";

        double result = 0;

        if (AppCache.USER_INFO == null)
            return  result;

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("UserCode", AppCache.USER_INFO.getUserCode());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        Log.println(Log.DEBUG, "dightmerc", "project: " + AppCache.USER_INFO.getProjectURL());
        Log.println(Log.DEBUG, "dightmerc", "UserCode: " + AppCache.USER_INFO.getUserCode());


        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            if (response.getPropertyCount() > 0)
            {
                DeliveryListActivity.filteredDeliveryList = new ArrayList<>(response.getPropertyCount());
                DeliveryListActivity.allDeliveryList = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    DeliveryTemplate deliveryTemplate = new DeliveryTemplate();
                    String temp;

                    SoapObject item = (SoapObject) response.getProperty(i);
                    deliveryTemplate.setTpCode(item.getPropertyAsString("ClientCode"));
                    deliveryTemplate.setTpName(item.getPropertyAsString("ClientName"));
                    temp = item.getPropertyAsString("AdressDelivery");
                    deliveryTemplate.setAddress((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                    temp = item.getPropertyAsString("ReferencePoint");
                    deliveryTemplate.setRefPoint((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                    temp = item.getPropertyAsString("ContactPerson");
                    deliveryTemplate.setContactPerson((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                    temp = item.getPropertyAsString("ContactPersonPhone");
                    deliveryTemplate.setContactPersonPhone((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                    temp = item.getPropertyAsString("Agent");
                    deliveryTemplate.setAgent((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                    temp = item.getPropertyAsString("AgentPhone");
                    deliveryTemplate.setAgentPhone((temp.equalsIgnoreCase("anyType{}") ? "" : temp));
                    temp = item.getPropertyAsString("Capacity");
                    deliveryTemplate.setCapacity(Double.parseDouble((temp.equalsIgnoreCase("anyType{}") ? "" : temp)));
                    temp = item.getPropertyAsString("Weight");
                    deliveryTemplate.setWeight(Double.parseDouble((temp.equalsIgnoreCase("anyType{}") ? "" : temp)));
                    temp = item.getPropertyAsString("Longitude");
                    deliveryTemplate.setLongitude(Double.parseDouble((temp.equalsIgnoreCase("anyType{}") ? "0" : temp.replace(",", "."))));
                    temp = item.getPropertyAsString("Latitude");
                    deliveryTemplate.setLatitude(Double.parseDouble((temp.equalsIgnoreCase("anyType{}") ? "0" : temp.replace(",", "."))));

                    deliveryTemplate.setOrderCode(item.getPropertyAsString("OrderNumber"));
                    deliveryTemplate.setOrderCreatedDate(Util.replaceSymbols(item.getPropertyAsString("OrderDate")));

                    deliveryTemplate.setCashToTake(Double.parseDouble(item.getPropertyAsString("CashTotal")));

                    SoapObject goodsItems = (SoapObject) item.getProperty("ProductsList");
                    ArrayList<GoodsByBrandTemplate> goodsList = new ArrayList<>(goodsItems.getPropertyCount());
                    for (int i1 = 0; i1 < goodsItems.getPropertyCount(); i1++)
                    {
                        SoapObject goodsItem = (SoapObject)goodsItems.getProperty(i1);
                        GoodsByBrandTemplate goodsByBrandTemplate = new GoodsByBrandTemplate();
                        goodsByBrandTemplate.setProductCode(goodsItem.getPropertyAsString("CodeProduct"));
                        goodsByBrandTemplate.setProductName(goodsItem.getPropertyAsString("NameProduct"));
                        goodsByBrandTemplate.setAmount(Integer.parseInt(goodsItem.getPropertyAsString("Amount")));
                        goodsList.add(goodsByBrandTemplate);
                    }

                    deliveryTemplate.setGoodsList(goodsList);
                    deliveryTemplate.setStatus(Integer.parseInt(item.getPropertyAsString("Status")));
                    deliveryTemplate.setDeliveryDate(Util.parseStringDate2(item.getPropertyAsString("ShippingDate")));
                    deliveryTemplate.setDeliveryDateLong(dateFormat.parse(deliveryTemplate.getDeliveryDate()).getTime() + 1);

                    final String paymentStatus = item.getPropertyAsString("PaymentStatus");
                    deliveryTemplate.setIsNeedTakeCash(paymentStatus.equals("1"));
                    /* 0 - отмена; 1 - должен забрать; 2 - получил; 3 - сдал кассиру */
                    if (paymentStatus.equals("2") || paymentStatus.equals("3"))
                        result += deliveryTemplate.getCashToTake();
                    DeliveryListActivity.allDeliveryList.add(deliveryTemplate);

                    final String chequeSent = item.getPropertyAsString("ChequeSent");

                    deliveryTemplate.setChequeSent(chequeSent.equals("1"));

                    final String chequeStatus = item.getPropertyAsString("ChequeStatus");
                    deliveryTemplate.setChequeStatus(chequeStatus);
                }
            } else {
                Log.println(Log.DEBUG, "dightmerc", "body is empty");
                return result;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return result;
    }
}