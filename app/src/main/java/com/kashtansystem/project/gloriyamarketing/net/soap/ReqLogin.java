package com.kashtansystem.project.gloriyamarketing.net.soap;

import android.content.Context;

import com.kashtansystem.project.gloriyamarketing.BuildConfig;
import com.kashtansystem.project.gloriyamarketing.core.OfflineManager;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.UserType;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрос авторизации
 */

public class ReqLogin
{
    public static String authorization(String login, String password)
    {
        final String soapAction = "http://www.sample-package.org#MobileAgents:GetUser";
        final String methodName = "GetUser";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("Login", login);
        request.addProperty("Password", password);
        request.addProperty("version", BuildConfig.VERSION_NAME);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 30000);
        httpTransportSE.debug = true;

        OfflineManager.INSTANCE.setGoOffline(false);

        try
        {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            //L.info(response.toString());
            if (response.getPropertyAsString("CodeError").equals("1"))
            {
                AppCache.USER_INFO.setLogined(true);
                AppCache.USER_INFO.setUserCode(response.getPropertyAsString("Code"));
                AppCache.USER_INFO.setUserName(response.getPropertyAsString("Name"));
                AppCache.USER_INFO.setProjectCode(response.getPropertyAsString("CodeProject"));
                AppCache.USER_INFO.setUserType(UserType.getUserTypeByValue(Integer.parseInt(response.getPropertyAsString("Type"))));
                AppCache.USER_INFO.setWarehouseCode(response.getPropertyAsString("CodeSklad"));

                OfflineManager.INSTANCE.setLogin(login);
                OfflineManager.INSTANCE.setPassword(password);
                OfflineManager.INSTANCE.setProjectURL(AppCache.USER_INFO.getProjectURL());

                OfflineManager.INSTANCE.setCode(AppCache.USER_INFO.getUserCode());
                OfflineManager.INSTANCE.setName(AppCache.USER_INFO.getUserName());
                OfflineManager.INSTANCE.setCodeProject(AppCache.USER_INFO.getProjectCode());
                OfflineManager.INSTANCE.setUserType(AppCache.USER_INFO.getUserType());
                OfflineManager.INSTANCE.setCodeSklad(AppCache.USER_INFO.getWarehouseCode());
                OfflineManager.INSTANCE.setGoOffline(false);
                return "";
            }
            return response.getPropertyAsString("Message");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            if(AppCache.USER_INFO.getProjectURL().equals(OfflineManager.INSTANCE.getProjectURL())
                    && login.equals(OfflineManager.INSTANCE.getLogin())
                    && password.equals(OfflineManager.INSTANCE.getPassword())
                    && OfflineManager.INSTANCE.getUserType() == UserType.Agent
            ){

                AppCache.USER_INFO.setLogined(true);
                AppCache.USER_INFO.setUserCode(OfflineManager.INSTANCE.getCode());
                AppCache.USER_INFO.setUserName(OfflineManager.INSTANCE.getName());
                AppCache.USER_INFO.setProjectCode(OfflineManager.INSTANCE.getCodeProject());
                AppCache.USER_INFO.setUserType(OfflineManager.INSTANCE.getUserType());
                AppCache.USER_INFO.setWarehouseCode(OfflineManager.INSTANCE.getCodeSklad());

                OfflineManager.INSTANCE.setGoOffline(true);
            }

            return String.format("Не удалось пройти авторизацию по причине: «%s».\nПроверьте интернет соединение и повторите попытку.",
                (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"));
        }
    }
}