package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.core.SoapProject;
import com.kashtansystem.project.gloriyamarketing.models.template.UserDetial;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.UserType;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 * Запрос авторизации
 */

public class ReqLoginMultiUser
{
    public static String authorization(String login, String password)
    {
        ArrayList<UserDetial> userDetials = new ArrayList<>();

        final String soapAction = "http://www.sample-package.org#MobileAgents:GetUser";
        final String methodName = "GetUser";

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, methodName);
        request.addProperty("Login", login);
        request.addProperty("Password", password);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        AppCache.USERS_DETAIL.clear();
        for (SoapProject soapProject: SoapProject.values()) {
            HttpTransportSE httpTransportSE = new HttpTransportSE(soapProject.getUrl(), 60000);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(soapAction, envelope);
                SoapObject response = (SoapObject) envelope.getResponse();
                //L.info(response.toString());
                if (response.getPropertyAsString("CodeError").equals("1")) {
                    UserDetial userDetial = new UserDetial(
                            response.getPropertyAsString("Code"),
                            response.getPropertyAsString("Name"),
                            response.getPropertyAsString("CodeProject"),
                            UserType.getUserTypeByValue(Integer.parseInt(response.getPropertyAsString("Type"))),
                            response.getPropertyAsString("CodeSklad"),
                            soapProject
                    );
                    userDetials.add(userDetial);
                    if(AppCache.userType!=null && AppCache.userType != userDetial.getUserType()) return "Не удалось пройти авторизацию";
                    AppCache.userType = UserType.getUserTypeByValue(Integer.parseInt(response.getPropertyAsString("Type")));
                }else
                return response.getPropertyAsString("Message");
            } catch (Exception ex) {
                ex.printStackTrace();
                return String.format("Не удалось пройти авторизацию по причине: «%s».\nПроверьте интернет соединение и повторите попытку.",
                        (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"));
            }
        }
        AppCache.USERS_DETAIL = userDetials;
        return "";
    }
}