package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Отправка информации о полученной денежной сумме от кредиторов
 */

public class ReqCollectCashierCash
{
    public static String[] send(String tpCode, LinearLayout content)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:CollectCashierCash";
        final String methodName = "CollectCashierCash";

        double total = 0;

        if (AppCache.USER_INFO == null)
            return new String[]{"-1", "data activity_agent is empty"};

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeClient", tpCode);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());

        SoapObject cashTotalList = new SoapObject(C.SOAP.NAME_SPACE, "CashList");
        for (int i = 0; i < content.getChildCount(); i++)
        {
            View child = content.getChildAt(i);
            //String type = ((AppCompatSpinner)child.findViewById(R.id.sCashType)).getSelectedView().findViewById(R.id.labelText).getTag().toString();
            String takenCashSum = ((EditText)child.findViewById(R.id.etCash)).getText().toString();
            //String rate = ((EditText)child.findViewById(R.id.etRate)).getText().toString();

            SoapObject rows =  new SoapObject(C.SOAP.NAME_SPACE, "CashRow");
            //if (type.equals("1"))
            //{
                rows.addProperty("TypeId", "1");
                rows.addProperty("TotalCash", "0");
                rows.addProperty("Rate", "0");
                rows.addProperty("TotalCashSum", takenCashSum);

                total += Double.parseDouble(takenCashSum);
            //}
            /*else
            {
                String ey = ((TextView)child.findViewById(R.id.tvExcRes)).getTag().toString().replace(" ", "").replace(",", ".");

                rows.addProperty("TypeId", "2");
                rows.addProperty("TotalCash", ey);
                rows.addProperty("Rate", rate);
                rows.addProperty("TotalCashSum", takenCashSum);
            }*/
            cashTotalList.addProperty("Rows", rows);
        }
        request.addProperty("CashTotalList", cashTotalList);
        request.addProperty("DateOfReceipt", new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            .format(new Date(System.currentTimeMillis())));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        L.info(request.toString());

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            String code = response.getPropertyAsString("Code");
            String msg = response.getPropertyAsString("Message");

            L.info("code.: " + code);
            L.info("msg..: " + msg);
            L.info("cash.: " + response.getPropertyAsString("CashTotal"));

            return new String[]{code, (code.equals("0") ? "successfully send" : msg), total + ""};
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return new String[]{"-1", (ex.getMessage() != null ? ex.getMessage() : "Exception Unknown"), "0"};
        }
    }
}