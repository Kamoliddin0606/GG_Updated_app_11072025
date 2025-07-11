package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.CompetitorScoutingTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SendOrderResponseTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqEditOrder
{
    public static SendOrderResponseTemplate send(MadeOrderTemplate order)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:EditOrder";
        final String methodName = "EditOrder";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("NumberOrder", order.getOrderCode());
        request.addProperty("OrderDate", order.getCreatedDateForEdit());
        request.addProperty("CodePrice", order.getPriceType());
        request.addProperty("ShippingDate", order.getUploadDateValue());
        request.addProperty("Weight", order.getWeight());
        request.addProperty("Capacity", order.getCapacity());
        request.addProperty("Credit", order.isOnCredit());
        // +1.2.2
//        request.addProperty("CodeWarehouse", order.getStockTemplate().getCode());
//        request.addProperty("CodeOrg", order.getOrganizationTemplate().getCode());

        String commentTo = order.getCommentTo();
        if (commentTo.equals("supervisor"))
        {
            request.addProperty("CommentSupervisor", order.getComment());
            request.addProperty("CommentForwarder", "");
        }
        else
        {
            request.addProperty("CommentSupervisor", "");
            request.addProperty("CommentForwarder", order.getComment());
        }
        request.addProperty("Comment", "");

        SoapObject goodsList = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        for (GoodsByBrandTemplate goods: order.getGoodsList().values())
        {
            SoapObject item =  new SoapObject(C.SOAP.NAME_SPACE, methodName);

            item.addProperty("CodeProduct", goods.getProductCode());
            item.addProperty("Amount", goods.getAmount());
            item.addProperty("Price", goods.getPrice());
            item.addProperty("Total", Util.getDoubleToString(goods.getTotal()));
            item.addProperty("Weight", goods.getWeight());
            item.addProperty("Capacity", goods.getCapacity());
            goodsList.addProperty("Rows", item);
        }
        request.addProperty("ProductsList", goodsList);

        SoapObject competitorList = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        if (order.getCompetitorList().isEmpty())
        {
            SoapObject item =  new SoapObject(C.SOAP.NAME_SPACE, methodName);
            item.addProperty("Competitor", "");
            item.addProperty("Product", "");
            item.addProperty("Price", "0");
            competitorList.addProperty("Rows", item);
        }
        else
        {
            for (CompetitorScoutingTemplate competitor : order.getCompetitorList())
            {
                SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, methodName);
                item.addProperty("Competitor", competitor.getName());
                item.addProperty("Product", competitor.getGoods());
                item.addProperty("Price", competitor.getPrice());
                competitorList.addProperty("Rows", item);
            }
        }
        request.addProperty("CompetitiveIintelligenceList", competitorList);

        L.info(request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL());

        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            L.info(response.toString());

            //если все ок, то возвращает 1 и номер заказа
            //если нет, то 0 и текст ошибки
            //Code
            //Message
            //CodeOrder
            //Row

            SendOrderResponseTemplate result = new SendOrderResponseTemplate();
            result.setResponseCode(response.getProperty("Code").toString());
            result.setMessage(response.getProperty("Message").toString());
            result.setOrderCode(response.getProperty("CodeOrder").toString());

            if (result.getResponseCode().equals("2"))
            {
                Map<String, String> notEnoughGoods = null;
                Set<String> notEnoughByBrand = new LinkedHashSet<>();
                Set<String> notEnoughBySeries = new LinkedHashSet<>();

                SoapObject rows = (SoapObject) response.getProperty("Rows");
                if (rows.getPropertyCount() > 0)
                {
                    notEnoughGoods = new HashMap<>(rows.getPropertyCount());
                    for (int i = 0; i < rows.getPropertyCount(); i++)
                    {
                        SoapObject goods = (SoapObject) rows.getProperty(i);
                        notEnoughGoods.put(
                            goods.getProperty("CodeProduct").toString(),
                            goods.getProperty("Have").toString());

                        GoodsByBrandTemplate product = order.getGoodsList().get(goods
                            .getProperty("CodeProduct").toString());

                        String brand = product.getBrand();
                        String series = product.getSeries();

                        if (!notEnoughByBrand.contains(brand))
                            notEnoughByBrand.add(brand);

                        if (!notEnoughBySeries.contains(series))
                            notEnoughBySeries.add(series);
                    }
                }
                result.setGoodsList(notEnoughGoods);
                result.setGoodsListByBrand(notEnoughByBrand);
                result.setGoodsListBySeries(notEnoughBySeries);
            }
            return result;
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: sending order <<<");
            ex.printStackTrace();
        }

        SendOrderResponseTemplate orderResponseTemplate = new SendOrderResponseTemplate();
        orderResponseTemplate.setResponseCode("-1");
        orderResponseTemplate.setMessage("Some unknown exception appears");
        return orderResponseTemplate;
    }
}