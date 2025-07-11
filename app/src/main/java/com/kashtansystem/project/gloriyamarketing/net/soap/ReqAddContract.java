package com.kashtansystem.project.gloriyamarketing.net.soap;

import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.MarshalDouble;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 07.07.2017.
 * ----------------------------------
 */

public class ReqAddContract {
    public static String[] send(
            Date DateOfContract,
            String CodeClient,
            Date TermReference,
            Date TermCertificate,
            String NumbReference,
            String NumbCertificate,
            String ContractType,
            String PassportSerial,
            Date PassortDate,
            Boolean isUnlim,
            String CodeDistrict,
            String CodeProject,
            String NameDistrict
    ) {
        final String soapAction = "http://www.sample-package.org#MobileAgents:SetContract";

        SimpleDateFormat simpleDF = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        DecimalFormat formater = new DecimalFormat("#.##");

        SoapObject request = new SoapObject(C.SOAP.NAME_SPACE, "SetContract");
        request.addProperty("DateOfContract", simpleDF.format(DateOfContract));
        request.addProperty("CodeUser", AppCache.USER_INFO.getUserCode());
        request.addProperty("CodeClient", CodeClient);
        request.addProperty("SumOfContract", formater.format(0f));
        request.addProperty("TermReference", simpleDF.format(TermReference));
        request.addProperty("TermCertificate", simpleDF.format(TermCertificate));
        request.addProperty("NumbReference", NumbReference);
        request.addProperty("NumbCertificate", NumbCertificate);
        request.addProperty("TypeOfContract", ContractType);

        if (PassportSerial != null && !PassportSerial.isEmpty())
            request.addProperty("NumbPassport", PassportSerial);
        else
            request.addProperty("NumbPassport", "");

        if (PassortDate != null)
            request.addProperty("TermPassport", simpleDF.format(PassortDate));
        else
            request.addProperty("TermPassport", "");

        request.addProperty("CertificateUnlimited", (isUnlim) ? "1" : "0");

        request.addProperty("PS_CodeProject", CodeProject);
        request.addProperty("PS_CodeDistrict", CodeDistrict);
        request.addProperty("PS_NameDistrict", NameDistrict);



        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE httpTransportSE = new HttpTransportSE(AppCache.USER_INFO.getProjectURL(), 60000);

        MarshalDouble marshalDouble = new MarshalDouble();
        marshalDouble.register(envelope);

        try {
            httpTransportSE.call(soapAction, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();

            String code = response.getPropertyAsString("Code");
            String msg = response.getPropertyAsString("Message");
            String CodeContract = response.getPropertyAsString("CodeContract");

            return new String[]{code, (code.equals("0") ? "successfully send" : msg), CodeContract};
        } catch (Exception ex) {
            ex.printStackTrace();
            return new String[]{"-1", String.format("Не удалось отправить данные по причине: «%s»\nПовторите попытку.",
                    (ex.getMessage() != null ? ex.getMessage() : "Network connection problem"))};
        }
    }
}