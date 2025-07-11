package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.BossTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Получение списка руководителей
 */

public class ReqGetBossList
{
    public static ArrayList<BossTemplate> load()
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetBossList";
        final String methodName = "GetBossList";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());

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
                ArrayList<BossTemplate> result = new ArrayList<>(response.getPropertyCount());
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    SoapObject item = (SoapObject) response.getProperty(i);

                    BossTemplate bossTemplate = new BossTemplate();
                    bossTemplate.setCode(item.getPropertyAsString("Code"));
                    bossTemplate.setName(item.getPropertyAsString("Name"));
                    result.add(bossTemplate);
                }
                return result;
            }
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load bosses <<<");
            ex.printStackTrace();
        }
        return new ArrayList<>(1);
    }
}