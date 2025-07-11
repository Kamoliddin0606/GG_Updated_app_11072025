package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderBodyTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderHeaderTemplate;
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
 * Запрос списка экспедиторов, которые собрали денежные средства в т.т-ах
 */

public class ReqShippingCashList
{
    public static ArrayList<ForwarderHeaderTemplate> load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:ShippingCashList";
        final String methodName = "ShippingCashList";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();

            if (response.getPropertyCount() > 0)
            {
                ArrayList<ForwarderHeaderTemplate> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    SoapObject soHeader = (SoapObject) response.getProperty(i);

                    ForwarderHeaderTemplate header = new ForwarderHeaderTemplate();
                    header.setUserCode(soHeader.getPropertyAsString("CodeUser"));
                    header.setUserName(soHeader.getPropertyAsString("NameUser"));
                    header.setTotalCash(Double.parseDouble(soHeader.getPropertyAsString("CashTotal")));

                    SoapObject soDetails = (SoapObject) soHeader.getProperty("ShippingCahList");
                    if (soDetails.getPropertyCount() > 0)
                    {
                        ArrayList<ForwarderBodyTemplate> details = new ArrayList<>(soDetails.getPropertyCount());
                        for (int i1 = 0; i1 < soDetails.getPropertyCount(); i1++)
                        {
                            SoapObject soDetail = (SoapObject) soDetails.getProperty(i1);

                            ForwarderBodyTemplate detail = new ForwarderBodyTemplate();
                            detail.setTpCode(soDetail.getPropertyAsString("CodeClient"));
                            detail.setTpName(soDetail.getPropertyAsString("NameClient"));

                            SoapObject soCashList = (SoapObject) soDetail.getProperty("CashTotalList");
                            if (soCashList.getPropertyCount() > 0)
                                detail.setCash(Double.parseDouble(((SoapObject)soCashList.getProperty(0)).getPropertyAsString("TotalCashSum")));

                            details.add(detail);
                        }
                        header.setDetails(details);
                    }
                    result.add(header);
                }
                return result;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }
}