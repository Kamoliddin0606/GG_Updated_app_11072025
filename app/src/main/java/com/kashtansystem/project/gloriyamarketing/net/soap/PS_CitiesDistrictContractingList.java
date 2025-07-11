package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.util.Log;

import com.kashtansystem.project.gloriyamarketing.models.template.BossTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PS_CitiesDistrictContract;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.LinkedList;

public class PS_CitiesDistrictContractingList {
    public static LinkedList<PS_CitiesDistrictContract> load()
    {
        Log.println(Log.DEBUG, "dightmerc", "send for district");
        final String soapAction = "http://www.sample-package.org#MobileAgents:PS_GetCitiesDistrictContracting";
        final String methodName = "PS_GetCitiesDistrictContracting";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeProject", AppCache.USER_INFO.getProjectCode());

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
                LinkedList<PS_CitiesDistrictContract> result = new LinkedList<>();
                for (int i = 0; i < response.getPropertyCount(); i++)
                {
                    SoapObject item = (SoapObject) response.getProperty(i);

                    PS_CitiesDistrictContract dictrictTepmlate = new PS_CitiesDistrictContract();
                    dictrictTepmlate.setCodeDistrict(item.getPropertyAsString("CodeDistrict"));
                    dictrictTepmlate.setNameDistrict(item.getPropertyAsString("NameDistrict"));
                    dictrictTepmlate.setCodeProject(item.getPropertyAsString("CodeProject"));
                    result.add(dictrictTepmlate);
                }
                return result;
            }
        }
        catch (Exception ex)
        {
            L.exception(">>> EXCEPTION: load cities and districts <<<");
            ex.printStackTrace();
        }
        return new LinkedList<>();
    }
}
