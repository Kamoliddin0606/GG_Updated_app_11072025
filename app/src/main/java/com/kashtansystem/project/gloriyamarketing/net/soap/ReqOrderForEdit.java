package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.CompetitorScoutingTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.CreditVisitTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.WarehouseTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqOrderForEdit
{
    public static MadeOrderTemplate load(Context context, String orderCode, String orderDate)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetOrderDetails";
        final String methodName = "GetOrderDetails";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("NumberOrder", orderCode);
        request.addProperty("OrderDate1", orderDate);
        request.addProperty("OrderDate2", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));
        //L.info(request.toString());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            L.info(response.toString());

            MadeOrderTemplate madeOrder = new MadeOrderTemplate();
            madeOrder.setOrderCode(orderCode);
            madeOrder.setOrderType(Integer.parseInt(response.getPropertyAsString("OrderType")));
            madeOrder.setOnCredit(Boolean.parseBoolean(response.getPropertyAsString("Credit")));
            madeOrder.setPriceType(response.getPropertyAsString("CodePrice"));

            ArrayList<WarehouseTemplate> stockTemplates = AppDB.getInstance(context).getWarehouse();
            for (WarehouseTemplate stockTemplate: stockTemplates) {
                if(stockTemplate.getCode().equals(response.getPropertyAsString("CodeSklad"))){
                    madeOrder.setStockTemplate(stockTemplate);
                    madeOrder.setOrganizationTemplate(stockTemplate.getOrg());
                    if(!response.getPropertyAsString("CodeOrg").equals(stockTemplate.getOrg().getCode())) {
                        throw new Throwable("This is stock not this organization");
                    }
                }
            }

            //
            madeOrder.setUploadDateView(Util.parseStringDate2(response.getPropertyAsString("ShippingDate")));
            madeOrder.setUploadDateValue(Util.replaceSymbols(response.getPropertyAsString("ShippingDate")));
            madeOrder.setCreatedDateForEdit(orderDate);

            final String warehouseCode = response.getPropertyAsString("CodeSklad");
            madeOrder.setCommentTo((response.getPropertyAsString("CommentForwarder").equals("anyType{}") ?
             "supervisor" : "forwarder"));
            final String comment = response.getPropertyAsString("CommentAgent");
            madeOrder.setComment((comment.equals("anyType{}") ? "" : comment));
            // * Список товаров
            SoapObject productRows = (SoapObject)response.getProperty("ProductRows");
            Map<String, GoodsByBrandTemplate> goodsList = new HashMap<>();
            LinkedList<GoodsByBrandTemplate> gifts = new LinkedList<>();

            double orderTotalPrice = 0;

            for (int i = 0; i < productRows.getPropertyCount(); i++)
            {
                SoapObject productObject = (SoapObject)productRows.getProperty(i);

                float discountValue = Float.parseFloat(productObject.getPropertyAsString("DiscountRate")) / 100;

                GoodsByBrandTemplate goodsItem = new GoodsByBrandTemplate();
                goodsItem.setWarehouseCode(warehouseCode);
                goodsItem.setProductCode(productObject.getPropertyAsString("CodeProduct"));
                goodsItem.setProductName(productObject.getPropertyAsString("NameProduct"));
                goodsItem.setAmount(Integer.parseInt(productObject.getPropertyAsString("Amount")));
                goodsItem.setOriginalPrice(Double.parseDouble(productObject.getPropertyAsString("Price").replace(",", ".")));
                goodsItem.setPrice((goodsItem.getOriginalPrice() - (goodsItem.getOriginalPrice() * discountValue)));
                goodsItem.setTotal(Double.parseDouble(productObject.getPropertyAsString("Total").replace(",", ".")));
                goodsItem.setDiscountValue(Integer.parseInt(productObject.getPropertyAsString("DiscountRate")));

                if (discountValue != 1f)
                {
                    orderTotalPrice += goodsItem.getTotal();
                    goodsList.put(goodsItem.getProductCode(), goodsItem);
                }
                else
                    gifts.add(goodsItem);
            }
            madeOrder.setGoodsList(goodsList);
            madeOrder.setGiftList(gifts);
            madeOrder.setTotalPrice(orderTotalPrice);
            // * Список конкурентов
            SoapObject competitorRows = (SoapObject)response.getProperty("CompetitorRows");
            LinkedList<CompetitorScoutingTemplate> competitorsList = new LinkedList<>();
            for (int i = 0; i < competitorRows.getPropertyCount(); i++)
            {
                SoapObject competitorObject = (SoapObject)competitorRows.getProperty(i);

                CompetitorScoutingTemplate compItem = new CompetitorScoutingTemplate();
                compItem.setName(competitorObject.getPropertyAsString("Competitor"));
                compItem.setGoods(competitorObject.getPropertyAsString("Product"));
                compItem.setPrice(competitorObject.getPropertyAsString("Price"));
                competitorsList.add(compItem);
            }
            madeOrder.setCompetitorList(competitorsList);
            // * Детали по кредиту
            SoapObject creditDetails = (SoapObject)response.getProperty("CreditDetailsList");
            LinkedList<CreditVisitTemplate> creditVisitList = new LinkedList<>();
            for (int i = 0; i < creditDetails.getPropertyCount(); i++)
            {
                SoapObject item = (SoapObject)creditDetails.getProperty(i);
                CreditVisitTemplate creditVisit = new CreditVisitTemplate();
                creditVisit.setVisitDate(Util.parseStringDate1(item.getPropertyAsString("DateOfPayment")));
                creditVisit.setTakeSum(item.getPropertyAsString("Total"));
                creditVisitList.add(creditVisit);
            }
            madeOrder.setCreditVisits(creditVisitList);
            return madeOrder;
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: loadAllTradingPointsByUserCode <<<");
            ex.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;
    }
}