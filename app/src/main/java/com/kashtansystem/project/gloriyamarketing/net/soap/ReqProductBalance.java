package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.WarehouseTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqProductBalance
{
    public static void load(Context context)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetProductBalance";
        final String methodName = "GetProductBalance";

        if (AppCache.USER_INFO == null)
            return;
        ArrayList<WarehouseTemplate> warehouses = AppDB.getInstance(context).getWarehouse();
        for (WarehouseTemplate warehouseTemplate:warehouses) {

            SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
            request.addProperty("CodeProject", AppCache.USER_INFO.getProjectCode());
            request.addProperty("CodeSklad", warehouseTemplate.getCode());

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

            try
            {
                httpTransportSE.call(soapAction, envelope);
                SoapObject response = (SoapObject)envelope.getResponse();
                AppDB.getInstance(context).addProduct(context, response, warehouses.indexOf(warehouseTemplate) == 0);
            }
            catch (Exception ex)
            {
                System.out.println(">>> EXCEPTION: loadProductsAndBalance <<<");
                ex.printStackTrace();
            }

        }

    }
}