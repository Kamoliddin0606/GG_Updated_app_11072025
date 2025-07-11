package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.CompetitorScoutingTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.CreditVisitTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SendOrderResponseTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqSendOrder {
    public static SendOrderResponseTemplate send(MadeOrderTemplate order) {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetOrder";
        final String methodName = "SetOrder";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeAgent", order.getUserCode());
        request.addProperty("CodeClient", order.getTpCode());
        request.addProperty("CodePrice", order.getPriceType());
        request.addProperty("Payment", "1");
        request.addProperty("ShippingDate", order.getUploadDateValue());
        request.addProperty("Longitude", order.getLongitude());
        request.addProperty("Latitude", order.getLatitude());
        request.addProperty("Weight", order.getWeight());
        request.addProperty("Capacity", order.getCapacity());
        request.addProperty("Credit", order.isOnCredit());
        request.addProperty("CodeProject", AppCache.USER_INFO.getProjectCode());

        request.addProperty("CodeSklad", order.getStockTemplate().getCode());
        request.addProperty("CodeOrg", order.getOrganizationTemplate().getCode());

        request.addProperty("CodeContract", order.getCodeContract());

        if (order.getCreatedDate() == 0)
            request.addProperty("CreateDate", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    .format(new Date(System.currentTimeMillis())));
        else
            request.addProperty("CreateDate", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                    .format(new Date(order.getCreatedDate())));

        String commentTo = order.getCommentTo();
        if (commentTo.equals("supervisor")||commentTo.equals("")) {
            request.addProperty("CommentSupervisor", order.getComment());
            request.addProperty("CommentForwarder", "");
        } else {
            request.addProperty("CommentSupervisor", "");
            request.addProperty("CommentForwarder", order.getComment());
        }
        request.addProperty("Comment", "");

        // * Список товаров.
        SoapObject goodsList = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        for (GoodsByBrandTemplate goods : order.getGoodsList().values()) {
            SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, methodName);
            item.addProperty("CodeSklad", goods.getWarehouseCode());
            item.addProperty("CodeProduct", goods.getProductCode());
            item.addProperty("Amount", goods.getAmount());
            item.addProperty("Price", goods.getOriginalPrice());
            item.addProperty("Total", goods.getTotal());
            item.addProperty("Weight", ((goods.getWeight() * goods.getAmount())));
            item.addProperty("Capacity", ((goods.getCapacity() * goods.getAmount())));
            item.addProperty("PaymentType", "1");
            item.addProperty("DiscountSum", (goods.getGiftAmount() == 0 ? "0" :
                    Util.getDoubleToString(((goods.getGiftAmount() * goods.getOriginalPrice()) * ((goods.getDiscountValue() == 0 ? 100 : goods.getDiscountValue()) / 100)))));
            if (goods.getGiftAmount() != 0)
                item.addProperty("DiscountRate", (goods.getDiscountValue() == 0 ? 100 : goods.getDiscountValue()));
            else
                item.addProperty("DiscountRate", 0);
            item.addProperty("GiftAmount", goods.getGiftAmount()); // Количество подарков
            goodsList.addProperty("Rows", item);
        }
        // * Список подарков. Подарки сидят в одном массиве, что и товары
        for (GoodsByBrandTemplate gifts : order.getGiftList()) {
            SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, methodName);
            item.addProperty("CodeSklad", gifts.getWarehouseCode());
            item.addProperty("CodeProduct", gifts.getProductCode());
            item.addProperty("Amount", gifts.getAmount());
            item.addProperty("Price", gifts.getPrice());
            item.addProperty("Total", 0.0);
            item.addProperty("Weight", ((gifts.getWeight() * gifts.getAmount())));
            item.addProperty("Capacity", ((gifts.getCapacity() * gifts.getAmount())));
            item.addProperty("PaymentType", "1");
            item.addProperty("DiscountSum", Util.getDoubleToString(gifts.getPrice() * gifts.getAmount()));
            item.addProperty("DiscountRate", "100");
            item.addProperty("GiftAmount", "0"); // Количество подарков
            goodsList.addProperty("Rows", item);
        }
        request.addProperty("ProductsList", goodsList);

        SoapObject competitorList = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        if (order.getCompetitorList().isEmpty()) {
            SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, methodName);
            item.addProperty("Competitor", "");
            item.addProperty("Product", "");
            item.addProperty("Price", "0");
            competitorList.addProperty("Rows", item);
        } else {
            for (CompetitorScoutingTemplate competitor : order.getCompetitorList()) {
                SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, methodName);
                item.addProperty("Competitor", competitor.getName());
                item.addProperty("Product", competitor.getGoods());
                item.addProperty("Price", competitor.getPrice());
                competitorList.addProperty("Rows", item);
            }
        }
        request.addProperty("CompetitiveIintelligenceList", competitorList);


        SoapObject creditList = new SoapObject(C.SOAP.NAME_SPACE, "CreditDetailsList");
        if (order.getCreditVisits().isEmpty()) {
            SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, "CreditDetailsRow");
            item.addProperty("DateOfPayment", "");
            item.addProperty("Total", "0");
            creditList.addProperty("Rows", item);
        } else {
            for (CreditVisitTemplate creditVisit : order.getCreditVisits()) {
                SoapObject item = new SoapObject(C.SOAP.NAME_SPACE, "CreditDetailsRow");
                item.addProperty("DateOfPayment", creditVisit.getVisitDateValue());
                item.addProperty("Total", creditVisit.getTakeSum());
                creditList.addProperty("Rows", item);
            }
        }
        request.addProperty("CreditDetails", creditList);
        request.addProperty("OrderType", order.getOrderType());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 120000);

        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        MarshalFloat marshalFloat = new MarshalFloat();
        marshalFloat.register(envelope);

        try {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            /*
             * если все ок, то возвращает 1 и номер заказа
             * если нет, то 0 и текст ошибки
             * Code
             * Message
             * CodeOrder
             * Row
             * */
            SendOrderResponseTemplate result = new SendOrderResponseTemplate();
            result.setResponseCode(response.getPropertyAsString("Code"));
            result.setMessage(response.getPropertyAsString("Message"));
            if (result.getResponseCode().equals("1"))
                result.setOrderCode(response.getPropertyAsString("CodeOrder"));

            if (result.getResponseCode().equals("2")) {
                Map<String, String> notEnoughGoods = null;
                Set<String> notEnoughByBrand = new LinkedHashSet<>();
                Set<String> notEnoughBySeries = new LinkedHashSet<>();

                SoapObject rows = (SoapObject) response.getProperty("Rows");
                if (rows.getPropertyCount() > 0) {
                    notEnoughGoods = new HashMap<>(rows.getPropertyCount());
                    for (int i = 0; i < rows.getPropertyCount(); i++) {
                        SoapObject goods = (SoapObject) rows.getProperty(i);
                        notEnoughGoods.put(
                                goods.getProperty("CodeProduct").toString(),
                                goods.getProperty("Have").toString());

                        GoodsByBrandTemplate product = order.getGoodsList().get(goods
                                .getProperty("CodeProduct").toString());

                        if (product != null) {
                            String brand = product.getBrand();
                            String series = product.getSeries();

                            if (!notEnoughByBrand.contains(brand))
                                notEnoughByBrand.add(brand);

                            if (!notEnoughBySeries.contains(series))
                                notEnoughBySeries.add(series);
                        }
                    }
                }
                result.setGoodsList(notEnoughGoods);
                result.setGoodsListByBrand(notEnoughByBrand);
                result.setGoodsListBySeries(notEnoughBySeries);
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();

            SendOrderResponseTemplate exRes = new SendOrderResponseTemplate();
            exRes.setResponseCode("-1");
            exRes.setMessage(String.format("Не удалось отправить данные заказа по причине: «%s»\nПовторите попытку снова.\n"
                            + "Или сохраните заказ на телефоне и отправьте позже",
                    (ex.getMessage() != null ? ex.getMessage() : "Network connection problem")));
            return exRes;
        }
    }
}